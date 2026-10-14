/*
 * File: vowels.c
 * Author: Eric Romero
 * Purpose: Scan in words and report whether the word's vowles appear in ascending alphabetical order.
 */

#include <stdlib.h>
#include <stdio.h>
#include <ctype.h>
#include <string.h>

/* */

/*
 * agentIsWord(s)
 * Mission: Verify that s is a "word". Letters only (A to Z / a to z)
 * Returns: 1 if s is a valid word. 0 otherwise
 * Assumptions: s is null terminated
 */
static int agentIsWord(const char *word) {
    for (const unsigned char *let = (const unsigned char *)word; *let; ++let) {
        if (!isalpha(*let)) {
            return 0;
        }
    }
    return 1;
}

/*
 * agentVowelRank(c)
 * Mission: Classify lowercase c as a vowel in alphabetic track order.
 * Returns: 'a' -> 0, 'e' -> 1, 'i' -> 2, 'o' -> 3, 'u' -> 4, constant -> -1
 * Assumptions: input c is lowercase
 */
static int agentVowelRank(int letter) {
    switch (letter) {
        case 'a' : return 0;
        case 'e' : return 1;
        case 'i' : return 2;
        case 'o' : return 3;
        case 'u' : return 4;
        default:   return -1;
    }
}

/*
 * vowelsInOrder(s)
 * Mission : Determine if vowels in s occur in non-decreasing alphabetical blocks.
 * Returns : If ordered -> 1, otherwise -> 0
 * Process: Scan left to right. Track highest vowel rank seen. Any later vowel with lower rank compromises the mission
 */
static int vowelsInOrder(const char *word) {
    int lastClearance = -1;
    for (const unsigned char *let = (const unsigned char *)word; *let; ++let) {
        int lowerLet = tolower(*let);
        int letter = agentVowelRank(lowerLet);
        if (letter >= 0) {
            if (letter < lastClearance) {
                /* Regression check */
                return 0;
            }
            if (letter > lastClearance) {
                /* Advance clearance level*/
                lastClearance = letter;
            }
            /* Equal rank (same vowel) is expected to be fine*/
        }
    }
    
    return 1;
}

/* Field Operation : main */
int main(void) {
    /* Each input word is at most 64 chars. +1 for '\0' */
    char dossier[65];
    /* Set to 1 if any error encountered*/
    int missionCompromised = 0;
    
    /* Read words until EOF. Guard buffer with %63s */
    while (scanf("%63s", dossier) == 1) {
        /* Validate whether word is really a letter-only word. */
        if (!agentIsWord(dossier)) {
            fprintf(stderr, "error\n");
            missionCompromised = 1;
            /* Continues to next word without output */
            continue;
        }
        /* Computes vowel ordering */
        int verdict = vowelsInOrder(dossier);
        
        /* Output exactly one integer per line*/
        printf("%d\n", verdict);
    }
    /* Exit. 0 if no errors. 1 if otherwise.*/
    return missionCompromised ? 1 : 0;
}

