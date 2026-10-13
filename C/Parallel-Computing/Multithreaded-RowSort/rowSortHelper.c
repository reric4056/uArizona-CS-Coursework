/*
 * rowSortHelper.c -> Field kit implementation
 * Author: Eric Romero
 *
 *  Purpose: Timing, matrix utilities, row sorting, rowsort permutations, dissemination barrier.
 */

#include "rowSortHelper.h"
#include <stdlib.h>
#include <stdio.h>
#include <sys/time.h>
#include <string.h>

/* TIMING */

double missionClockSeconds(void) {
    struct timeval tv;
    gettimeofday(&tv, NULL);
    return (double)tv.tv_sec + (double)tv.tv_usec / 1000000.0;
}

/* MATRIX*/

int **allocGrid(int rows, int cols) {
    int **grid = (int **)malloc(rows * sizeof(int *));
    if (!grid) {
        fprintf(stderr, "allocGrid: failed to allocate row pointers\n");
        exit(1);
    }
    
    for (int r = 0; r < rows; r++) {
        grid[r] = (int *)malloc(cols * sizeof(int));
        if (!grid[r]) {
            fprintf(stderr, "allocGrid: failed to allocate row %d\n", r);
            exit(1);
        }
    }
    return grid;
}

void freeGrid(int **grid, int rows) {
    if (!grid) return;
    for (int r = 0; r < rows; r++) free(grid[r]);
    free(grid);
}

void packGridFromArray(int **grid, int rows, int cols, const int *A) {
    int idx = 0;
    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            grid[r][c] = A[idx++];
        }
    }
}

void unpackArrayFromGrid(int *A, int rows, int cols, int **grid) {
    int idx = 0;
    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            A[idx++] = grid[r][c];
        }
    }
}

/* ROW SORT */

static int cmpIntAsc(const void *a, const void *b) {
    int x = *((const int *)a);
    int y = *((const int *)b);
    return (x > y) - (x < y);
}

void sortRowBand(int **grid, int rowStart, int rowEnd, int cols) {
    for (int r = rowStart; r < rowEnd; r++) {
        qsort(grid[r], cols, sizeof(int), cmpIntAsc);
    }
}

void assignRowSlice(int totalRows, int numAgents, int agentId, int *outStartRow, int *outEndRow) {
    int baseRows = totalRows / numAgents;
    int extras   = totalRows % numAgents;
    int startRow = agentId * baseRows + (agentId < extras ? agentId : extras);
    int sliceLen = baseRows + (agentId < extras ? 1 : 0);
    *outStartRow = startRow;
    *outEndRow   = startRow + sliceLen;
}

/* ROWSORT TRANSFORMS */

/*
 * Step 2 permutation
 * Let rows=s, cols=r, and t = cols/rows (assumes rows | cols).
 * dst[i][ rr*t + k ] = src[ rr ][ i + k*rows ]
 */
void permuteTransposeReshape(int **dst, int **src, int rows, int cols) {
    int t = cols / rows;
    for (int i = 0; i < rows; i++) {
        for (int rr = 0; rr < rows; rr++) {
            for (int k = 0; k < t; k++) {
                dst[i][rr * t + k] = src[rr][i + k * rows];
            }
        }
    }
}

/*
 * Step 6: shift RIGHT by cols/2 in row-major linearization, wrapping into the next row; creates an extra bottom row.
 *
 * Caller passes rows = s+1.
 * src contains valid data in first (rows-1) rows.
 */
void shiftDownHalf(int **dst, int **src, int rows, int cols) {
    int activeRows = rows - 1;
    int half = cols / 2;
    
    /* Default to infinity. */
    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            dst[r][c] = SHIFT_PAD_HIGH;
        }
    }
    
    /* Neg-infinity pads: top row first half */
    for (int c = 0; c < half; c++) {
        dst[0][c] = SHIFT_PAD_LOW;
    }
    
    /* Move real entries by +half in linear index. */
    for (int r = 0; r < activeRows; r++) {
        for (int c = 0; c < cols; c++) {
            int linear  = r * cols + c;
            int shifted = linear + half;
            int dr = shifted / cols;
            int dc = shifted % cols;
            dst[dr][dc] = src[r][c];
        }
    }
    
    /* Bottom row last half are +infinity (already), kept explicit. */
    for (int c = cols - half; c < cols; c++) {
        dst[rows - 1][c] = SHIFT_PAD_HIGH;
    }
}

/*
 * Step 8: inverse of shiftDownHalf
 * Subtract half from linear index and trash out-of-range entries.
 *
 * Caller passes rows = s+1.
 * Only first (rows-1) rows of dst are meaningful.
 */
void shiftUpHalf(int **dst, int **src, int rows, int cols) {
    int activeRows = rows - 1;
    int half = cols / 2;
    
    /* Clear destination real rows. */
    for (int r = 0; r < activeRows; r++) {
        for (int c = 0; c < cols; c++) {
            dst[r][c] = SHIFT_PAD_HIGH;
        }
    }
    
    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            int linear  = r * cols + c;
            int shifted = linear - half;
            
            if (shifted < 0) continue;
            if (shifted >= activeRows * cols) continue;
            
            int dr = shifted / cols;
            int dc = shifted % cols;
            dst[dr][dc] = src[r][c];
        }
    }
}

void swapGridBuffers(int ***A, int ***B) {
    int **tmp = *A;
    *A = *B;
    *B = tmp;
}

/* DISSEMINATION BARRIER */

static int isPowerOfTwo(int x) {
    return (x > 0) && ((x & (x - 1)) == 0);
}

void barrierInit(DisseminationBarrier *b, int numThreads) {
    if (!isPowerOfTwo(numThreads)) {
        fprintf(stderr, "barrierInit: numThreads must be a power of two (got %d)\n", numThreads);
        exit(1);
    }
    
    b->numThreads = numThreads;
    
    /* rounds = log2(p) */
    b->rounds = 0;
    while ((1 << b->rounds) < numThreads) b->rounds++;

    /* flags[thread][round][sense] */
    b->flags = (volatile int ***)malloc(numThreads * sizeof(volatile int **));
    if (!b->flags) {
        fprintf(stderr, "barrierInit: failed to allocate flags rows\n");
        exit(1);
    }
    
    for (int t = 0; t < numThreads; t++) {
        b->flags[t] = (volatile int **)malloc(b->rounds * sizeof(volatile int *));
        if (!b->flags[t]) {
            fprintf(stderr, "barrierInit: failed to allocate flags[%d] rounds\n", t);
            exit(1);
        }
        
        for (int round = 0; round < b->rounds; round++) {
            b->flags[t][round] = (volatile int *)malloc(2 * sizeof(int));
            if (!b->flags[t][round]) {
                fprintf(stderr, "barrierInit: failed to allocate flags[%d][%d]\n", t, round);
                exit(1);
            }
            b->flags[t][round][0] = 0;
            b->flags[t][round][1] = 0;
        }
    }
}

void barrierWait(DisseminationBarrier *b, int tid) {
    static __thread int sense = 1;
    
    for (int round = 0; round < b->rounds; round++) {
        int partner = tid ^ (1 << round);
        
        /* Signal partner for 'this' sense. */
        b->flags[partner][round][sense] = 1;
        __sync_synchronize();
        
        /* Wait until partner signaled me for 'this' sense. */
        while (b->flags[tid][round][sense] == 0) {
            __sync_synchronize();
        }
        
        /* Clear my slot for this sense . */
        b->flags[tid][round][sense] = 0;
        __sync_synchronize();
    }
    
    sense ^= 1;
}

void barrierDestroy(DisseminationBarrier *b) {
    if (!b || !b->flags) return;
    
    for (int t = 0; t < b->numThreads; t++) {
        for (int round = 0; round < b->rounds; round++) {
            free((void *)b->flags[t][round]);
        }
        free((void *)b->flags[t]);
    }
    
    free((void *)b->flags);
    b->flags = NULL;
    b->numThreads = 0;
    b->rounds = 0;
}
