/*
 * File: cipher.c
 * Author: Eric Romero
 * Purpose: Read an integer shift N, then rotate each subsequent alphanumeric string by N.
 *      Letters rotate with case preserved. Digits pass unchanged.
 */

#include <stdio.h>
#include <ctype.h>

/*  HELPERS  */

/*
 * agentIsAlnumWord
 * Mission: Verify word is strictly alphanumeric
 * Returns: valid -> 1, otherwise -> 0
 */
static int agentIsAlnumWord(const char *word) {
    for (const unsigned char *let = (const unsigned char *)word; *let; ++let) {
        if (!isalnum(*let)) {
            return 0;
        }
    }
    return 1;
}

/*
 * normalizeShift
 * Mission: Normalize shift to -25 to 25 for properly rotating by mod 26
 */
static int normalizeShift(int n) {
    int num = n % 26;
    if (num < 0) {
        num += 26;
    }
    return num;
}

/*
 * rotateLetter
 * Mission: Rotate ASCII letter ch by r, preserving case.
 * Returns: The new rotated character or unchanged if not a letter.
 */
static int rotateLetter(int let, int num) {
    if (isupper((unsigned char)let)) {
        return 'A' + ((let - 'A' + num) % 26);
    } else if (islower((unsigned char)let)) {
        return 'a' + ((let - 'a' + num) % 26);
    }
    return let;
}

/*  MAIN  */
int main(void) {
    int rawShift;
    if (scanf("%d", &rawShift) != 1) {
        /* First 'word' from line has to be a number */
        fprintf(stderr, "error\n");
        return 1;
    }
    const int shift = normalizeShift(rawShift);
    
    char payload[65];
    int missionCompromised = 0;
    
    /* Read in more 'words'. Each alphanumeric and <= 64 chars */
    while (scanf("%64s", payload) == 1) {
        if (!agentIsAlnumWord(payload)) {
            /* Flag as error, skip 'word' and set exit code*/
            fprintf(stderr, "error\n");
            missionCompromised = 1;
            continue;
        }
        
        /* Rotate letters in place, not digits */
        for (char *p = payload; *p; ++p) {
            unsigned char c = (unsigned char)*p;
            if (isalpha(c)) {
                *p = (char)rotateLetter(c, shift);
            }
        }
        
        /* One rotated string per line */
        printf("%s\n", payload);
    }
    return missionCompromised ? 1 : 0;
}
