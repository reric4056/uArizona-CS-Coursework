/*
 * File: mirrorSum.c
 * Author: Eric Romero
 * Purpose: Reads in a sequence of positive integers for each input n, the
 * console returns the sum of the reverse of the input and the original input.
 */

#include <stdio.h>
#include <stdlib.h>

/* Prototype */
int reverseInt(int n);

/*
 * reverseInt
 * Purpose: Returns the reverse of the input.
 * Arguments:
 *      n: A positive integer input
 * Returns:
 *      reverserN: the reverse of n
 */
int reverseInt(int n) {
    int reverseN = 0;
    while (n > 0) {
        reverseN = reverseN * 10 + (n % 10);
        n /= 10;
    }
    
    return reverseN;
}

/*
 * main()
 * Purpose: Reads in integers, checks if the input number is greater
 * than 0, finds the reverse of that integer, and finally computes
 * the sum of the two integers.
 * System Status Returns:
 *      0: Build is successful
 *      1: Build is unsuccessful, returns respective error message.
 * Console Output:
 *      Integer representing the sum of the original input number and its reverse.
 */
int main(int argc, const char * argv[]) {
    int n;
    int errorExist = 0;
    
    while (1) {
        int input = scanf("%d", &n);
        
        /* Checks if end of file */
        if (input == EOF) {
            break;
        } else if (input != 1) {
            /* Input is a non-integer. */
            fprintf(stderr, "Error: non-integer input\n");
            return 1;
        }
        
        /* Input is a non-positive integer*/
        if (n <= 0) {
            fprintf(stderr, "Non-positive input\n");
            errorExist = 1;
            continue;
        }
        
        /* Computes the sum of the original input and its mirror*/
        int reverse = reverseInt(n);
        int sum = n + reverse;
        printf("%d\n", sum);
    }
    
    if (errorExist) {
        return 1;
    } else {
        return 0;
    }
}
