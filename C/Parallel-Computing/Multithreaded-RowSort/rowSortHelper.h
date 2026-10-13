/*
 * rowSortHelper.h
 * Author: Eric Romero
 *
 * Special Notes:
 * - Store data as an s x r matrix (s rows, r cols). C is row-major.
 * - Driver passes length = r, width = s.
 * - “Transpose and reshape” is implemented via direct index mapping.
 * - Parallel version uses a dissemination barrier
 */
#ifndef ROW_SORT_HELPER_H
#define ROW_SORT_HELPER_H

#include <pthread.h>

/* TIMING */
/* Returns wall-clock time in seconds (gettimeofday-based). */
double missionClockSeconds(void);

/* MATRIX */
/* Allocate/Free a dynamic 2D int matrix: rows x cols. */
int **allocGrid(int rows, int cols);
void freeGrid(int **grid, int rows);

/*
 * Copy between 1D row-major array A and 2D matrix grid (rows x cols).
 * These are the init/finalization phases and mustn't be parallelized or timed.
 */
void packGridFromArray(int **grid, int rows, int cols, const int *A);
void unpackArrayFromGrid(int *A, int rows, int cols, int **grid);

/* ROW SORT */
/* Sort rows [rowStart, rowEnd) in-place using qsort(). */
void sortRowBand(int **grid, int rowStart, int rowEnd, int cols);

/* Compute an evenish contiguous row range for a given agent. */
void assignRowSlice(int totalRows, int numAgents, int agentId, int *outStartRow, int *outEndRow);

/* ROWSORT TRANSFORMS (PHASES) */
/*
 * Columnsort/rowsort uses 8 phases;
 *  The even-numbered phases are permutations:
 *      - transpose + reshape (two times)
 *      - shift down by floor(r/2), fill sentinels, later unshift
 *
 * Convention:
 * - rows = s, cols = r
 * - src and dst are both rows x cols unless explicitly stated otherwise
 */

/* Phase 2 / Phase 4: transpose + reshape permutation. */
void permuteTransposeReshape(int **dst, int **src, int rows, int cols);

/*
 * Shift steps (rowsort version).
 * Sentinel choice for nonnegative input:
 * - SHIFT_PAD_LOW  acts as “-infinity”
 * - SHIFT_PAD_HIGH acts as “+infinity”
 */
#define SHIFT_PAD_LOW   (-1)
#define SHIFT_PAD_HIGH  (2147483647)

void shiftDownHalf(int **dst, int **src, int rows, int cols);
void shiftUpHalf(int **dst, int **src, int rows, int cols);

/* Utility: swap two matrix pointers. */
void swapGridBuffers(int ***A, int ***B);

/* DISSEMINATION BARRIER */
/*
 * Dissemination barrier (power of two threads).
 * Each round k synchronizes with partner: tid XOR (1<<k).
 *
 * To make barrier reuse safe (no overwriting / missed signals),
 * we keep two "mailbox" slots per (thread, round): one per sense (0/1).
 */
typedef struct {
    int numThreads; /* p */
    int rounds; /* log2(p) */
    /* flags[thread][round][sense] (sense is 0 or 1) */
    volatile int ***flags;
} DisseminationBarrier;

void barrierInit(DisseminationBarrier *b, int numThreads);
void barrierWait(DisseminationBarrier *b, int tid);
void barrierDestroy(DisseminationBarrier *b);

/* THREAD PAYLOAD */
/* Shared “agent dossier” passed to each worker thread. */
typedef struct {
    int agentId;
    int numThreads;
    int rows;
    int cols;
    int **ping; /* ping buffer */
    int **pong; /* pong buffer */
    DisseminationBarrier *barrier;
} OperativeTask;

#endif /* ROW_SORT_HELPER_H */
