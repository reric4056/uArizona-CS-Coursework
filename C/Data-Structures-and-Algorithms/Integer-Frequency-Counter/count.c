/*
 * File: count.c
 * Author: Eric Romero
 * Purpose: Read a declared count N, then exactly N integers. Reports each distinct integer with the number of sightings, sorted in ascending order.
 *
 * Exit Codes:
 *      0 : Mission accomplished
 *      1 : Mission Compromises {invalid N, too few integers, allocation failure}
 *
 * Notes:
 *      Input Format: N followed by N integers
 *      Extra integers beyond the first N are ignored (burn after reading)
 *      Uses dynmaic allocation and quicksort.
 *      Prints "[value] [count]" per line
 */

#include <stdio.h>
#include <stdlib.h>

/* ----------------------------------------------------------------*/

/*
 * compareInts(a, b)
 * Mission: Comparator for quicksort. Establishes ascending order on integers
 * Inputs:
 *      a, b : addresses of integers under surveillance
 * Returns < 0 if *a < *b. 0 if equal. >0 if *a > *b
 * Assume a and b are non-null pointers to valid integers.
 */
static int compareInts(const void *a, const void *b) {
    int intA = *(const int *)a;
    int intB = *(const int *)b;
    return (intA > intB) - (intA < intB);
}

/*
 * readPositiveN(adrsN)
 * Mission: Extract the decalred operation size N from stdin. Validate it.
 * Inputs:
 *      adrsN : address where the extracted N is to be stored.
 * Returns: 0 with success. 1 with failure {No int read. N <= 0.}
 */
static int readPositiveN(int *adrsN) {
    if (scanf("%d", adrsN) != 1) {
        fprintf(stderr, "Error: Failed to read N. \n");
        return 1;
    }
    
    if (*adrsN <= 0) {
        fprintf(stderr, "Error: N must be positive. \n");
        return 1;
    }
    return 0;
}

/*
 * readIntegers(buf, N)
 * Mission: Read exactly N integers from input into the buffer.
 * Inputs:
 *      buf: pointer to allocated buffer of at least N ints.
 *      N : Required number of integers to read.
 * Returns 0 if successful. 1 if fewer than N integers are available.
 */
static int readIntgers(int *buf, int N) {
    for (int i = 0; i < N; i++) {
        if (scanf("%d", &buf[i]) != 1) {
            fprintf(stderr, "Error: fewer integers than desired by N.\n");
            return 1;
        }
    }
    return 0;
}

/*
 * reportCountsSorted(a, N)
 * Mission: Sort array of length N by quicksort and print each distinct value and count.
 * Inputs:
 *      a : pointer to N integers.
 *      N : total number of elements
 * Returns void
 */
static void reportCountsSorted(int *a, int N) {
    qsort(a, (size_t)N, sizeof(int), compareInts);
    
    int current = a[0];
    int tally = 1;
    
    for (int i = 1; i < N; i++) {
        if (a[i] == current) {
            tally++;
        } else {
            /* Debrief current subject before switching targets */
            printf("%d %d\n", current, tally);
            current = a[i];
            tally = 1;
        }
    }
    
    printf("%d %d\n", current, tally);
}

int main(void) {
    int N;
    
    /* Phase 1: Acquire operation size */
    if (readPositiveN(&N) != 0) {
        return 1;
    }
    
    /* Phase 2: Provision memory for field intel */
    int *arr = (int *)malloc((size_t)N * sizeof(int));
    if (arr == NULL) {
        fprintf(stderr, "Error: out of memory. \n");
        return 1;
    }
    
    /* Phase 3: Ingest exactly N integers  */
    if (readIntgers(arr, N) != 0) {
        free(arr);
        return 1;
    }
    
    /* Phase 4: Sort and report sightings */
    reportCountsSorted(arr, N);
    
    /* Phase 5: Sanitize & exit */
    free(arr);
    return 0;
}

