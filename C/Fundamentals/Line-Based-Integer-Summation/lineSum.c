/*
 * File: lineSum,c
 * Author : Eric Romero
 *
 * Purpose:
 *      Read intercepted messages one line at a time. For each line, decode zero or more non-negative integers.
 *      Print the cumulative for that line.
 *
 * Input/Output Contract:
 *      Input: Arbitrary lines
 *      For each line:
 *          If contains one valid non-negative integer and no invalid character, then print sum
 *          If empty or contains any negative integer, then print nothing except for an error message.
 *
 *
 *
 * Notes:
 *      Emphasis on getline and dynamic memory usage. sscanf used to move across characters.
 */

#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>

/* Logistics: Transmission buffer */
typedef struct Transmission {
    char *buffer;
    size_t capacity;
} Transmission;

/*
 * decryptLine(message, sumTotal)
 * Role:
 *      Sweep one intercepted line and sum all non-negative decimal integers.
 *
 * Parameters:
 *      message   : Null-Terminated string. single line.
 *      sumTotal  : set to the per line sum if line is valid.
 *
 * Rules:
 *      Characters separated by whitespace
 *      Each character must be a non negative decimal integer
 *      Must contain at least one valid integer
 *
 * Returns:
 *      0 : successful process. Sum of integers from line written into *sumTotal
 *      1 : If line is invalid
 */
static int decryptLine(const char *message, int *sumTotal) {
    const char *p = message;
    int sum = 0;
    int found = 0;
    
    for (;;) {
        /* Skip whitespace */
        while (*p != '\0' && isspace((unsigned char)*p)) {
            p++;
        }
        
        if (*p == '\0' || *p == '\n') {
            break;
        }
        
        /* Enforce non-negative integers */
        if (*p == '+' || *p == '-') {
            return 1;
        }
        
        /* Attempt to parse single integer and learn how many characters were consumed */
        int value = 0;
        int consumed = 0;
        if (sscanf(p, "%d%n", &value, &consumed) == 1) {
            if (value < 0) {
                return 1;
            }
            sum += value;
            found = 1;
            p += consumed;
        } else {
            /* Non inteer junk */
            return 1;
        }
    }
    
    if (!found) {
        /* Empty line */
        return 1;
    }
    
    *sumTotal = sum;
    return 0;
}

/* Main: Intercept. Decode. Report. Repeat. */
int main(void) {
    Transmission signal = { NULL, 0};
    ssize_t bytes = 0;
    int compromised = 0;
    
    /* Recon: Read each transmission until EOF or error */
    while ((bytes = getline(&signal.buffer, &signal.capacity, stdin)) != -1) {
        int sum = 0;
        if (decryptLine(signal.buffer, &sum) != 0) {
            fprintf(stderr, "Error: Negative value or invalid character found.\n");
            compromised = 1;
            continue;
        }
        printf("%d\n", sum);
    }
    
    /* Distinguish normal EOF from input stream failure */
    if (ferror(stdin)) {
        fprintf(stderr, "Error: input stream failed.\n");
        compromised = 1;
    }
    
    /* Debriefing: Secure the buffer */
    free(signal.buffer);
    return compromised ? 1 : 0;
}
