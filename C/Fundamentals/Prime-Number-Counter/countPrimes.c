/*
 * File: countPrimes.c
 * Author: Eric Romero
 * Purpose: Reads two positive integers m and n from stdin.
 * Prints the count of primes p. Prints 0 if m >= n.
 * Prints error message (exit status = 1) on non-integer or
 * non-positive inputs.
 */

#include <stdio.h>
#include <math.h>

/* Prototype
 */
int isPrime(int num);

/*
 * isPrime(n)
 * Purpose:
 *      Checks if the input integer is a prime number
 * Arguments:
 *      N : Integer to check if prime.
 * Returns:
 *      1: If n is prime
 *      0: If n isn't prime
 */
int isPrime(int n) {
    if (n < 2) {
        return 0;
    }
    
    if (n == 2) {
        return 1;
    }
    
    if (n % 2 == 0) {
        return 0;
    }
    
    
    int limit = (int) sqrt((double) n);
    for (int a = 3; a <= limit; a += 2) {
        if (n % a == 0) {
            return 0;
        }
    }
    return 1;
}

/*
 * main()
 * Purpose:
 *      Checks in two positive integers m and n and prints the number of prime numbers between
 *      the two.
 *
 * System Status Returns:
 *      0: If build is successful
 *      1: If error is met.
 *
 * Outputs:
 *      If build is successful, prints the total number of primes between m and n.
 *      If unsuccessful, prints an error message explaining what happened.
 */
int main(int argc, const char * argv[]) {
    int m = 0, n = 0;
    
    /* Reads in the input integers*/
    int input = scanf("%d %d", &m, &n);
    
    /* Checks that the two integers were inputed */
    if (input != 2) {
        fprintf(stderr, "Error: Expected two integers. \n");
        return 1;
    }
    
    /* Ensures input integers are strictly positive.*/
    if (m <= 0 || n <= 0) {
        fprintf(stderr, "Error: Boths integers have to be positive. \n");
        return 1;
    }
    
    /* Build is successful but returns a prime count of 0 if m is greater or equal to n*/
    if (m >= n) {
        printf("0\n");
        return 0;
    }
    
    /* Computes the number of primes between m and n*/
    int count = 0;
    for (int p = m + 1; p < n; p++) {
        if (isPrime(p)) {
            count++;
        }
    }
    
    printf("%d\n", count);
    return 0;
}
