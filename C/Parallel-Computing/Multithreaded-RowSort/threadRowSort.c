/*
 * threadRowSort.c — Parallel RowSort
 * Author: Eric Romero
 *
 * Framing:
 * - Treat s as #rows and r as #cols.
 * - Driver passes length=r, width=s.
 *
 * Rules of Engagement:
 * - Create threads once.
 * - Use dissemination barrier from class.
 * - Dom't parallelize or time init/finalization.
 * - Each of the 8 phases execute in parallel.
 * - Timing starts just before thread creation, stops before finalization.
 */

#include "rowSortHelper.h"
#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>

typedef struct {
    int numThreads;
    int rows; /* s */
    int cols; /* r */
    
    /* Phase buffers */
    int **cur; /* s x r (current) */
    int **tmp; /* s x r (scratch) */
    
    /* Shift buffer: (s+1) x r */
    int **shift; /* (s+1) x r */
    DisseminationBarrier barrier;
} RowSortShared;

typedef struct {
    int agentId;
    RowSortShared *ops;
} Operative;

/* Phase 4 inverse mapping (range)*/
static void inversePermuteTransposeReshapeRange(int **dst, int **src, int rows, int cols, int startRR, int endRR) {
    int t = cols / rows;
    for (int rr = startRR; rr < endRR; rr++) {
        for (int i = 0; i < rows; i++) {
            for (int k = 0; k < t; k++) {
                dst[rr][i + k * rows] = src[i][rr * t + k];
            }
        }
    }
}

/* Phase 2 transpose+reshape (range) */
static void permuteTransposeReshapeRange(int **dst, int **src, int rows, int cols, int startI, int endI) {
    int t = cols / rows;
    for (int i = startI; i < endI; i++) {
        for (int rr = 0; rr < rows; rr++) {
            for (int k = 0; k < t; k++) {
                dst[i][rr * t + k] = src[rr][i + k * rows];
            }
        }
    }
}

/* Phase 6 helpers */
static void fillHighRange(int **grid, int startRow, int endRow, int cols) {
    for (int r = startRow; r < endRow; r++) {
        for (int c = 0; c < cols; c++) {
            grid[r][c] = SHIFT_PAD_HIGH;
        }
    }
}

/* Move real entries from s x r into (s+1) x r . */
static void shiftDownMoveRange(int **dst, int **src, int rowsPlusOne, int cols, int startRow, int endRow) {
    (void)rowsPlusOne; /* documentation symmetry */
    int half = cols / 2;
    
    for (int r = startRow; r < endRow; r++) {
        for (int c = 0; c < cols; c++) {
            int linear  = r * cols + c;
            int shifted = linear + half;
            int dr = shifted / cols;
            int dc = shifted % cols;
            dst[dr][dc] = src[r][c];
        }
    }
}

/* Phase 8 unshift */
/*
 * Inverse of Phase 6's linear +half mapping. Overwrites every dst cell exactly once -> race-free.
 *
 * dst: s x r
 * srcShift: (s+1) x r
 */
static void shiftUpDstOwnedRange(int **dst, int **srcShift, int rows, int cols, int startRow, int endRow) {
    int half = cols / 2;
    for (int dr = startRow; dr < endRow; dr++) {
        for (int dc = 0; dc < cols; dc++) {
            int L = dr * cols + dc;
            int srcL = L + half;
            int sr = srcL / cols;
            int sc = srcL % cols;
            dst[dr][dc] = srcShift[sr][sc];
        }
    }
}

static void *worker(void *arg) {
    Operative *op = (Operative *)arg;
    RowSortShared *ops = op->ops;
    
    int agentId = op->agentId;
    int p = ops->numThreads;
    int s = ops->rows;
    int r = ops->cols;
    int rowStart, rowEnd;
    
    /* PHASE 1 */
    assignRowSlice(s, p, agentId, &rowStart, &rowEnd);
    sortRowBand(ops->cur, rowStart, rowEnd, r);
    barrierWait(&ops->barrier, agentId);
    
    /* PHASE 2 */
    assignRowSlice(s, p, agentId, &rowStart, &rowEnd);
    permuteTransposeReshapeRange(ops->tmp, ops->cur, s, r, rowStart, rowEnd);
    barrierWait(&ops->barrier, agentId);
    
    if (agentId == 0) {
        int **t = ops->cur; ops->cur = ops->tmp; ops->tmp = t;
    }
    barrierWait(&ops->barrier, agentId);
    
    /* PHASE 3 */
    assignRowSlice(s, p, agentId, &rowStart, &rowEnd);
    sortRowBand(ops->cur, rowStart, rowEnd, r);
    barrierWait(&ops->barrier, agentId);
    
    /* PHASE 4 */
    assignRowSlice(s, p, agentId, &rowStart, &rowEnd);
    inversePermuteTransposeReshapeRange(ops->tmp, ops->cur, s, r, rowStart, rowEnd);
    barrierWait(&ops->barrier, agentId);
    
    if (agentId == 0) {
        int **t = ops->cur; ops->cur = ops->tmp; ops->tmp = t;
    }
    barrierWait(&ops->barrier, agentId);
    
    /* PHASE 5 */
    assignRowSlice(s, p, agentId, &rowStart, &rowEnd);
    sortRowBand(ops->cur, rowStart, rowEnd, r);
    barrierWait(&ops->barrier, agentId);
    
    
    /* PHASE 6 */
    /* Beat: fill shift grid with infinity */
    {
        int sp1 = s + 1;
        int fillStart, fillEnd;
        assignRowSlice(sp1, p, agentId, &fillStart, &fillEnd);
        fillHighRange(ops->shift, fillStart, fillEnd, r);
    }
    barrierWait(&ops->barrier, agentId);
    
    /* Beat: agent 0 lays neg-infinity pads on top row, first r/2 */
    if (agentId == 0) {
        int half = r / 2;
        for (int c = 0; c < half; c++) {
            ops->shift[0][c] = SHIFT_PAD_LOW;
        }
    }
    barrierWait(&ops->barrier, agentId);
    
    /* Beat: move real entries by +half in linear index */
    assignRowSlice(s, p, agentId, &rowStart, &rowEnd);
    shiftDownMoveRange(ops->shift, ops->cur, s + 1, r, rowStart, rowEnd);
    barrierWait(&ops->barrier, agentId);
    
    /* PHASE 7 */
    {
        int sp1 = s + 1;
        int sortStart, sortEnd;
        assignRowSlice(sp1, p, agentId, &sortStart, &sortEnd);
        sortRowBand(ops->shift, sortStart, sortEnd, r);
    }
    barrierWait(&ops->barrier, agentId);
    
    /* PHASE 8 */
    /* Destination-owned unshift back into s x r. */
    assignRowSlice(s, p, agentId, &rowStart, &rowEnd);
    shiftUpDstOwnedRange(ops->tmp, ops->shift, s, r, rowStart, rowEnd);
    barrierWait(&ops->barrier, agentId);
    
    if (agentId == 0) {
        int **t = ops->cur; ops->cur = ops->tmp; ops->tmp = t;
    }
    barrierWait(&ops->barrier, agentId);
    return NULL;
}

double threadRowSort(int *A, int numThreads, int length, int width) {
    int r = length;
    int s = width;
    
    if (numThreads < 1) {
        fprintf(stderr, "threadRowSort: numThreads must be >= 1\n");
        exit(1);
    }
    RowSortShared ops;
    ops.numThreads = numThreads;
    ops.rows = s;
    ops.cols = r;

    /* Allocate buffers */
    ops.cur   = allocGrid(s, r);
    ops.tmp   = allocGrid(s, r);
    ops.shift = allocGrid(s + 1, r);

    /* Init */
    packGridFromArray(ops.cur, s, r, A);

    /* Barrier setup */
    barrierInit(&ops.barrier, numThreads);

    /* Start clock */
    double t0 = missionClockSeconds();

    pthread_t *threads = (pthread_t *)malloc(numThreads * sizeof(pthread_t));
    Operative *agents  = (Operative *)malloc(numThreads * sizeof(Operative));
    if (!threads || !agents) {
        fprintf(stderr, "threadRowSort: failed to allocate thread handles\n");
        exit(1);
    }
    
    for (int agentId = 0; agentId < numThreads; agentId++) {
        agents[agentId].agentId = agentId;
        agents[agentId].ops = &ops;
        
        if (pthread_create(&threads[agentId], NULL, worker, &agents[agentId]) != 0) {
            fprintf(stderr, "threadRowSort: pthread_create failed for tid=%d\n", agentId);
            exit(1);
        }
    }
    
    for (int agentId = 0; agentId < numThreads; agentId++) {
        pthread_join(threads[agentId], NULL);
    }
    double t1 = missionClockSeconds();
    
    /* Finalization */
    unpackArrayFromGrid(A, s, r, ops.cur);

    /* Cleanup */
    barrierDestroy(&ops.barrier);
    free(threads);
    free(agents);
    freeGrid(ops.cur, s);
    freeGrid(ops.tmp, s);
    freeGrid(ops.shift, s + 1);
    return (t1 - t0);
}
