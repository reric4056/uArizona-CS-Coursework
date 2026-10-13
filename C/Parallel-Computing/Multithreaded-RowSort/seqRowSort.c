/*
 * seqRowSort.c — Sequential RowSort
 * Author: Eric Romero
 *
 * Mission Parameters:
 * - A: flat row-major vector of n = r*s nonnegative ints
 * - length = r (columns), width = s (rows)
 * - numThreads is ignored here (driver passes 1 to keep interfaces uniform)
 *
 * Required behavior :
 * 1) Initialization (NOT timed): copy A -> s x r grid
 * 2) Start clock
 * 3) Execute 8 phases:
 *      1) sort rows
 *      2) transpose+reshape permutation
 *      3) sort rows
 *      4) inverse of step 2
 *      5) sort rows
 *      6) shift down by r/2 with sentinels
 *      7) sort rows
 *      8) inverse of step 6
 * 4) Stop clock
 * 5) Finalization: copy grid -> A
 */
#include "rowSortHelper.h"
#include <stdio.h>
#include <stdlib.h>

/*
 * Phase 4: inverse of the Phase 2 permutation.
 *
 * In our helper, Phase 2 is implemented as a direct index map.
 * This function reverses that map without an explicit transpose.
 *
 * Let idx = i*cols + j.
 * Then: dst[i][j] = src[idx % rows][idx / rows]
 */
static void inversePermuteTransposeReshape(int **dst, int **src, int rows, int cols) {
    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            int idx = r * cols + c;
            int sr  = idx % rows;
            int sc  = idx / rows;
            dst[r][c] = src[sr][sc];
        }
    }
}

double seqRowSort(int *A, int numThreads, int length, int width) {
    (void)numThreads; /* sequential op: no agents in the field */
    
    /* Framing: s rows, r cols (driver passes length=r, width=s). */
    int r = length; /* cols */
    int s = width;  /* rows */
    
    /* Two s x r buffers for ping-pong during permutations. */
    int **ping = allocGrid(s, r);
    int **pong = allocGrid(s, r);
    
    /* Shift step uses (s+1) x r (one extra row for sentinels/wrap). */
    int **shiftGrid = allocGrid(s + 1, r);
    
    /* Initialization */
    packGridFromArray(ping, s, r, A);
    
    /* Start clock */
    double t0 = missionClockSeconds();
    
    /* PHASE 1 */
    sortRowBand(ping, 0, s, r);
    
    /* PHASE 2 */
    permuteTransposeReshape(pong, ping, s, r);
    swapGridBuffers(&ping, &pong);
    
    /* PHASE 3 */
    sortRowBand(ping, 0, s, r);
    
    /* PHASE 4 */
    inversePermuteTransposeReshape(pong, ping, s, r);
    swapGridBuffers(&ping, &pong);
    
    /* PHASE 5 */
    sortRowBand(ping, 0, s, r);
    
    /* PHASE 6 */
    shiftDownHalf(shiftGrid, ping, s + 1, r);
    
    /* PHASE 7 */
    sortRowBand(shiftGrid, 0, s + 1, r);
    
    /* PHASE 8 */
    shiftUpHalf(pong, shiftGrid, s + 1, r);
    swapGridBuffers(&ping, &pong);
    
    /* Stop clock */
    double t1 = missionClockSeconds();
    
    /* Finalization */
    unpackArrayFromGrid(A, s, r, ping);
    
    /* Cleanup  */
    freeGrid(ping, s);
    freeGrid(pong, s);
    freeGrid(shiftGrid, s + 1);
    return (t1 - t0);
}
