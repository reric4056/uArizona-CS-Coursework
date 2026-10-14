/*
 * File: baseChanger.c
 * Author: Eric Romero
 * Purpose/Mission: Read a base (2 - 36), convert each following token (max 6 chars) from that base into an unsigned long.
 *
 * Protocol:
    - Base outside 2 to 36 : Flag error, exit(1)
    - For each number:
        - If invalid symbol, then Flag error, skip, continue.
        - Else, Convert with Horner's rule and print.
    - Return 0 if no errors found. Else, return 1.
 */

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <ctype.h>

#define MAXNUM 6

/* Convert single character to digit value, or -1 */
static int charToVal(char c) {
    if (isdigit((unsigned char) c)) {
        return c - '0';
    } else if (isalpha((unsigned char) c)) {
        return tolower((unsigned char) c) - 'a' + 10;
    } else {
        return -1;
    }
}

/* Horner's rule conversaion. Return 0 on error, 1 if success */
static int convertToken(const char *s, int base, unsigned long *out) {
    unsigned long value = 0;
    for ( ; *s; ++s) {
        int digit = charToVal(*s);
        if (digit < 0 || digit >= base) {
            return 0;
        }
        value = value * (unsigned) base + (unsigned) digit;
    }
    *out = value;
    return 1;
}

int main(int argc, const char * argv[]) {
    int base;
    int errorFlag = 0;
    
    /* Acquire mission base */
    if (scanf("%d", &base) != 1) {
        fprintf(stderr, "protocol error: failed to read base\n");
        return 1;
    }
    if (base < 2 || base > 36) {
        fprintf(stderr, "protocol error: illegal base %d (must be 2 - 36)\n", base);
        return 1;
    }
    
    /* Sweep numeric tokens */
    char buf[MAXNUM + 1];
    while (scanf("%6s", buf) == 1) {
        unsigned long val;
        if (!convertToken(buf, base, &val)) {
            fprintf(stderr, "input error: illegal digit in \"%s\"\n", buf);
            errorFlag = 1;
            continue;
        }
        printf("%lu\n", val);
    }
    return errorFlag ? 1 : 0;
}

