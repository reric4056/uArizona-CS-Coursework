/*
 * Filename: anagrams.c
 * Author: Eric Romero
 * Purpose/Mission: Given an string input, print every string that is an anagram (case-sensitive) of the first string (master key). Original casing preserved.
 * Protocol:
    - First token alphabetical-only. If not, emit error, exit 1.
    - Subsequent tokens: if non-alphabetic, emit error and skip; continue scanning.
    - Input strings are <= 64 chars. All scanf calls are length-capped.
    - If no errors encountered, Exit 0. Else, exit 1.
 */

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <ctype.h>

#define MAXLEN 64

/* Quick alpha check (letters only) */
static int isAlphabeticString(const char *s) {
    for ( ; *s; ++s) {
        if (!isalpha((unsigned char) *s)) {
            return 0;
        }
    }
    return 1;
}

/* Normalize to lowercase and tally counts for A to Z (case-insensitive)*/
static void tallyLowerCounts(const char *s, int counts[26]) {
    int i;
    for (i = 0; i < 26; ++i) {
        counts[i] = 0;
    }
    for ( ; *s; ++s) {
        unsigned char c = (unsigned char) *s;
        if (isalpha(c)) {
            int index = tolower(c) - 'a';
            counts[index]++;
        }
    }
}

/* Check if 'cand' is a case-insensitive anagram of 'master' */
static int isAnagram(const char *master, const char *cand) {
    size_t len1 = 0, len2 = 0;
    const char *p;
    int c1[26], c2[26];
    
    /* Length check over letters. Tokens are alphabetic if valid */
    for (p = master; *p; ++p) {
        if (isalpha((unsigned char) *p)) {
            ++len1;
        }
    }
    for (p = cand; *p; ++p) {
        if (isalpha((unsigned char) *p)) {
            ++len2;
        }
    }
    
    if (len1 != len2) {
        return 0;
    }
    
    tallyLowerCounts(master, c1);
    tallyLowerCounts(cand, c2);
    
    for (int i = 0; i < 26; ++i) {
        if (c1[i] != c2[i]) {
            return 0;
        }
    }
    return 1;
}

int main(int argc, const char * argv[]) {
    // insert code here...
    char masterRaw[MAXLEN + 1];
    int errorFlag = 0;
    
    /* Acquire master key (first token). Protect buffer, handle immediate EOF*/
    if (scanf("%64s", masterRaw) != 1) {
        /* Immediate EOF. Normal termination. No output */
        return 0;
        /* Successful end, no errors found*/
    }
    
    /* Validate first token. Must be alphabetic-only */
    if (!isAlphabeticString(masterRaw)) {
        fprintf(stderr, "protocol error: master token contains non-alpha\n");
        return 1;
        /* First string invalid. Error and exit*/
    }
    
    /* Print master (always printed. It's an anagram of itself */
    printf("%s\n", masterRaw);
    
    /* Precompute for anagram checks */
    int masterCounts[26];
    tallyLowerCounts(masterRaw, masterCounts);
    /* Tokens are alphabetic if valid*/
    size_t masterLen = strlen(masterRaw);
    
    /* Sweep remaining tokens */
    char agentWord[MAXLEN + 1];
    while (scanf("%64s", agentWord) == 1) {
        if (!isAlphabeticString(agentWord)) {
            fprintf(stderr, "input error: non-alpha token encountered. Skipped\n");
            errorFlag = 1;
            continue;
        }
        
        /* Fast length check before full tally */
        if (strlen(agentWord) != masterLen) {
            continue;
        }
        
        if (isAnagram(masterRaw, agentWord)) {
            printf("%s\n", agentWord);
        }
    }
    
    /* Exit Discipline. 0 if clean run. 1 if any errors found */
    return errorFlag ? 1 : 0;
}
