/*
 * File: calcFactorial.c
 * Author: Eric Romero
 * Purpose: Reads a sequence of integers and prints the factorial
 *      of each valid input N. Should look like:
 *          N! = factorialCount
 *      Input should end at end of file EOF. An error occurs if input
 *      is out of range (0 <= N <= 12), or non integer.
 */

#include <stdio.h>
#include <stdlib.h>

int factorial(int n);

/*
 * factorial(n)
 * Purpose: Finds the factorial of n.
 * Arguments:
 *      n: Input integer with a range of 0 <= n <= 12)
 * Return:
 *      n!: Factorial of input integer n
 */
int factorial(int n) {
    int result = 1;
    for (int i = 2; i <= n; ++i) {
        result *= i;
    }
    
    return result;
}
/*
 * main()
 * Purpose: Reads integers continuously until EOF. For each valid integer. the factorial of n will be printed.
 * System Status:
 *      0: build is successful
 *      1: build is unsuccessful with error met.
 * Console Output:
 *      Console will display a successful message in the form of:
 *          "n! = factorialCount"
 *
 */
int main(int argc, const char * argv[]) {
    int n;
    int checkError = 0;
    
    while (1) {
        int input = scanf("%d", &n);
        
        if (input == EOF) {
            break;
        } else if (input != 1) {
            fprintf(stderr, "Error: Inputed a non-integer value. \n");
            return 1;
        }
        
        if (n < 0 || n > 12) {
            fprintf(stderr, "Error: %d is out of range 0 to 12. \n", n);
            checkError = 1;
            continue;
        }
        
        printf("%d! = %d\n", n, factorial(n));
    }
    
    if (checkError) {
        return 1;
    } else {
        return 0;
    }
}
