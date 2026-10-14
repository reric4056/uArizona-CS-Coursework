/*
 * File: strmath.c
 * Author: Eric Romero
 * Purpose: Perform big-integer arithmetic on decimal strings.
 * Profile:
 *          Accepts three lines of intel. operation(add or sub), str1, and str2
 *          Executes the operation in the field. Outputs the regulated result
 *          No unaccessary leading zeroes
 *
 * Exit codes:
 *   0 — mission accomplished.
 *   1 — mission compromised (invalid input, allocation failure, etc).
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

/* Ingress & Validation : Securing the Perimeter */

/*
 * removeNewline(s)
 * Mission : Sweep the string tail for a newline symbol and neutralize it.
 * Assumptions: s is a null terminated string that is safe to modify.
 * No returns
 */
static void removeNewline(char *s) {
    if (!s) {
        return;
    }
    size_t n = strlen(s);
    if (n && s[n - 1] == '\n') {
        s[n - 1] = '\0';
    }
}

/*
 * checkIfBlankLine(s)
 * Mission: Detect whether a line is empty or just noise (whitespace)
 * Returns 1 if blank or null (compromised). 0 otherwise
 */
static int checkIfBlankLine(const char *s) {
    if (!s) return 1;
    for (; *s; ++s) {
        if (!isspace((unsigned char)*s)) {
            return 0;
        }
    }
    return 1;
}

/*
 * checkIfAllNumbers(s)
 * Mission: Verify that string is composed strictly of digits
 * Assumptions: null terminated C string
 * Returns 1 if all digits. 0 otherwise.
 */
static int checkIfAllNumbers(const char *s) {
    if (!s || *s == '\0') return 0;
    for (const unsigned char *p = (const unsigned char *)s; *p; ++p) {
        if (!isdigit(*p)) return 0;
    }
    return 1;
}

/*
 * duplicateString(origingalStr)
 * Mission: Clone string into new memory.
 * Return: Newly allocated copy of string, or NULL if out of resources.
 */
static char *duplicateString(const char *originalStr) {
    size_t n = strlen(originalStr) + 1;
    char *newStr = malloc(n);
    if (!newStr) {
        return NULL;
    }
    memcpy(newStr, originalStr, n);
    return newStr;
}

/* Canonicalization (Cover Identity) */

/*
 * cleanNumbers(s)
 * Mission: Forges canonical form of digit string
 *      - Strips leading zeroes.
 *      - If all zeroes, return with a single zero
 * Returns a newly allocated sanitized string.
 */
static char *cleanNumbers(const char *s) {
    const char *p = s;
    while (*p == '0') {
        ++p;
    }
    if (*p == '\0') {
        char *z = malloc(2);
        if (!z) {
            return NULL;
        }
        z[0] = '0';
        z[1] = '\0';
        return z;
    }
    return duplicateString(p);
}

/* Mission Sizing (Logistics) */

/*
 * evaluateAddBufferSize(len1, len2)
 * Mission: Estimate maximum storage required for |A| + |B|
 * Returns buffer size including null terminator
 */
static size_t evaluateAddBufferSize(size_t len1, size_t len2) {
    size_t maxDigits = (len1 > len2 ? len1 : len2) + 1;
    return maxDigits + 1;
}

/*
 * evaluateSubBufferSize(len1, len2, neg)
 * Mission Estimate maximum storage required for |A| - |B|
 * Adds space for negative sign if needed
 */
static size_t evaluateSubBufferSize(size_t len1, size_t len2, int neg) {
    size_t maxDigits = (len1 > len2 ? len1 : len2);
    return maxDigits + (neg ? 1 : 0) + 1;
}

/*
 * compareMagnitude(a, b)
 * Mission: Compare two numeric strings by absolute magnitutde
 * Returns 0 if equal, -1 if a < b, 1 if a > b
 */
static int compareMagnitude(const char *a, const char *b) {
    size_t lenA = strlen(a);
    size_t lenB = strlen(b);
    
    if (lenA != lenB) {
        return (lenA < lenB) ? -1 : 1;
    }
    int cmp = strcmp(a, b);
    return (cmp < 0) ? -1 : (cmp > 0 ? 1 : 0);
}

/* Arithmetic Core (Field Work) */

/*
 * addMagnitudes(a, b, outResult)
 * Mission: Computes |A| + |B|. Stores result into outResult buffer.
 * Notes: Operates right to left number by number. Handles carry.
 * Returns pointer to outResult String.
 */
static char *addMagnitudes(const char *a, const char *b, char *outResult) {
    size_t lenA = strlen(a);
    size_t lenB = strlen(b);
    size_t maxLen  = (lenA > lenB ? lenA : lenB);
    
    /* Null terminator at end of buffer */
    outResult[maxLen + 1] = '\0';

    size_t indexA = lenA;
    size_t indexB = lenB;
    size_t writePos = maxLen;
    
    
    int carry = 0;

    while (writePos > 0) {
        int digitA = (indexA ? a[--indexA] - '0' : 0);
        int digitB = (indexB ? b[--indexB] - '0' : 0);
        int sum  = digitA + digitB + carry;
        /* Uses [0 ... maxlen]*/
        outResult[writePos--] = (char)('0' + (sum % 10));
        carry = sum / 10;
    }

    if (carry) {
        /* [0 ... maxLen] */
        outResult[0] = (char)('0' + carry);
        return outResult;
    } else {
        /* Includes the \0*/
        memmove(outResult, outResult + 1, maxLen + 1);
        /* [0 ... maxLen - 1]*/
        return outResult;
    }
}

/*
 * subMagnitudes(a, b, outResult)
 * Mission: Computes |A| - |B|. Assume A >= B
 * Notes:
 *      Operates right to left.
 *      Deploys a borrow protocal when A's digit is too weak.
 * Returns to pointer of outResult string
 */
static char *subMagnitudes(const char *a, const char *b, char *outResult) {
    size_t lenA = strlen(a);
    size_t lenB = strlen(b);
    
    outResult[lenA] = '\0';

    size_t indexA = lenA;
    size_t indexB = lenB;
    size_t writePos = lenA;
    
    int borrow = 0;

    while (writePos > 0) {
        int digitA = (indexA ? a[--indexA] - '0' : 0) - borrow;
        int digitB = (indexB ? b[--indexB] - '0' : 0);
        if (digitA < digitB) {
            digitA += 10;
            /* Borrow executed*/
            borrow = 1;
        } else {
            borrow = 0;
        }
        outResult[--writePos] = (char)('0' + (digitA - digitB));
    }
    return outResult;
}

/* Main Briefing : Command & COntrol */

int main(void) {
    /* Setup: Secure inputs for op, str1, str2*/
    char *op = NULL;
    char *str1 = NULL;
    char *str2 = NULL;
    size_t opBufSize = 0;
    size_t aBufSize = 0;
    size_t bBufSize = 0;

    if (getline(&op, &opBufSize, stdin) == -1 || getline(&str1, &aBufSize, stdin) == -1 || getline(&str2, &bBufSize, stdin) == -1) {
        fprintf(stderr, "Error: not enough input strings.\n");
        free(op);
        free(str1);
        free(str2);
        return 1;
    }
    removeNewline(op);
    removeNewline(str1);
    removeNewline(str2);

    /* Perimeter checks: blank lines, invalid commands, rogue characters*/
    if (checkIfBlankLine(op) || checkIfBlankLine(str1) || checkIfBlankLine(str2)) {
        fprintf(stderr, "Error: a line contains only whitespace.\n");
        free(op);
        free(str1);
        free(str2);
        return 1;
    }
    if (!(strcmp(op, "add") == 0 || strcmp(op, "sub") == 0)) {
        fprintf(stderr, "Error: 1st line not equal to 'add' or 'sub'.\n");
        free(op);
        free(str1);
        free(str2);
        return 1;
    }
    if (!checkIfAllNumbers(str1) || !checkIfAllNumbers(str2)) {
        fprintf(stderr, "Error: non-numeric characters detected.\n");
        free(op);
        free(str1);
        free(str2);
        return 1;
    }

    /* Canonicalize. Trim disguises (leading zeroes*/
    char *A = cleanNumbers(str1);
    char *B = cleanNumbers(str2);
    
    if (!A || !B) {
        fprintf(stderr, "Error: out of memory.\n");
        free(op);
        free(str1);
        free(str2);
        free(A);
        free(B);
        return 1;
    }

    char *result = NULL;
    
    if (strcmp(op, "add") == 0) {
        size_t bufSize = evaluateAddBufferSize(strlen(A), strlen(B));
        result = malloc(bufSize);
        if (!result) {
            fprintf(stderr, "Error: out of memory.\n");
            free(op);
            free(str1);
            free(str2);
            free(A);
            free(B);
            return 1;
        }
        
        addMagnitudes(A, B, result);
        char *normal = cleanNumbers(result);
        if (!normal) {
            fprintf(stderr, "Error: out of memory.\n");
            free(result);
            free(op);
            free(str1);
            free(str2);
            free(A);
            free(B);
            return 1;
        }
        free(result);
        result = normal;
        printf("%s\n", result);
    } else {
        /* Operation: subtraction*/
        int cmp = compareMagnitude(A, B);
        int negative = (cmp < 0);

        size_t bufSize = evaluateSubBufferSize(strlen(A), strlen(B), negative);
        result = malloc(bufSize);
        if (!result) {
            fprintf(stderr, "Error: out of memory.\n");
            free(op);
            free(str1);
            free(str2);
            free(A);
            free(B);
            return 1;
        }

        if (cmp == 0) {
            result[0] = '0';
            result[1] = '\0';
        } else if (cmp > 0) {
            subMagnitudes(A, B, result);
        } else {
            subMagnitudes(B, A, result);
            char *normal = cleanNumbers(result);
            free(result);
            size_t L = strlen(normal);
            char *signalOut = malloc(L + 2);
            signalOut[0] = '-';
            memcpy(signalOut + 1, normal, L + 1);
            free(normal);
            result = signalOut;
            printf("%s\n", result);
            free(op);
            free(str1);
            free(str2);
            free(A);
            free(B);
            free(result);
            return 0;
        }

        char *normal = cleanNumbers(result);
        free(result);
        result = normal;
        if (negative && !(result[0] == '0' && result[1] == '\0')) {
            size_t L = strlen(result);
            char *signalOut = malloc(L + 2);
            signalOut[0] = '-';
            memcpy(signalOut + 1, result, L + 1);
            free(result);
            result = signalOut;
        }
        printf("%s\n", result);
    }

    /* Wrapping up. Clean the scene. Exit. */
    free(op);
    free(str1);
    free(str2);
    free(A);
    free(B);
    free(result);
    return 0;
}

