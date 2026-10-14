/*
 * File: count2.c
 * Author: Eric Romero
 * Purpose:
 *      Read a sequence of integers and report back how many times each distinct integer occurs, ascending.
 *
 * Input/Output Contract:
 *      Input:
 *          Zero or more integers. Whitespace allowed
 *      Output:
 *          One line per distinct integer.
 *          Ascending order
 *          Format: <value> <count>
 *
 * Data Structure:
 *      Sorted single linked list with nodes for value and count.
 *
 * Notes:
 *      getline() used to read lines.
 *      sscanf advances pointer across characters
 *      Ledger remains sorted when inserting.
 */

#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>

/*
 * Field Manual: Data Model; aka the ledger
 * Objective:
 *      Each node is a dossier summarizing sightings of an integer.
 *      Invariant: The ledger remains strictly ascending by 'value' after every insertion
 */
typedef struct Dossier {
    /* The observed integer/target */
    int value;
    /* Sighting accumulated so far */
    int count;
    /* Next dossier */
    struct Dossier *next;
} Dossier;

/*
 * ledgerTally(ledgerp, value)
 * Role:
 *      Insert or update the record in the ascending ledger.
 *
 * Parameters:
 *      ledgerp : Address of the head pointer for the ledger list.
 *      value   : The integer to record
 *
 * Behavior & Invariants:
 *      If a node for a value exists, incrmement its count ~ No allocation
 *      Allocate exactly one new node and splice at correct sorted position
 *      The ledger should remain strictly ascending by value
 *
 * Returns:
 *      0 : Successful process
 *      1 : Allocation fails
 */
static int ledgerTally(Dossier **ledgerp, int value) {
    Dossier *prev = NULL;
    Dossier *curr = *ledgerp;
    
    /* Advance to dirst dossier with value >= input value */
    while (curr && curr -> value < value) {
        prev = curr;
        curr = curr -> next;
    }
    
    /* Target known: Update dossier */
    if (curr && curr -> value == value) {
        curr -> count += 1;
        return 0;
    }
    
    /* Target unknown: Allocate a dossier and splice into place */
    Dossier *node = (Dossier *)malloc(sizeof(Dossier));
    if (!node) {
        /* Failure to allocate */
        return 1;
    }
    
    node -> value = value;
    node -> count = 1;
    node -> next = curr;
    
    if (prev) {
        prev -> next = node;
    } else {
        /* New head */
        *ledgerp = node;
    }
    return 0;
}

/*
 * ledgerEmit(ledger)
 * Role:
 *      Write each pair to the output by walking the ledger
 *
 * Notes:
 *      Emits noth if the ledger is empty.
 *      Nodes are not freed
 */
static void ledgerEmit(const Dossier *ledger) {
    for (const Dossier *p = ledger; p != NULL; p = p -> next) {
        printf("%d %d\n", p -> value, p -> count);
    }
}

/*
 * ledgerPurge(ledger)
 * Role:
 *      Deallocate all nodes in the ledger. aka burn after reading
 *
 * Notes:
 *      Safe to call with NULL
 */
static void ledgerPurge(Dossier *ledger) {
    while (ledger) {
        Dossier *next = ledger -> next;
        free(ledger);
        ledger = next;
    }
}

/*
 * main()
 *
 * Ingress & Error Policy:
 *      Lines are read with getline(). Parsing advances a pointer across the line.
 *      Characters must be valid integers. Any non integer (whitespace excluded) is a fatal input error.
 */
int main(void) {
    Dossier *ledger = NULL;
    char *line = NULL;
    size_t capacity = 0;
    ssize_t len;
    
    /* Ingress: Read input. Any hostile character will trigger abort */
    while ((len = getline(&line, &capacity, stdin)) != -1) {
        char *p = line;
        
        /* Walk the line. Extract integers and advance */
        for (;;) {
            /* Skip whitespace */
            while (*p != '\0' && isspace((unsigned char)*p)) {
                p++;
            }
            if (*p == '\0') {
                break;
            }
            
            int value = 0;
            int consumed = 0;
            int matched = sscanf(p, " %d%n", &value, &consumed);
            
            if (matched == 1) {
                if (ledgerTally(&ledger, value) != 0) {
                    fprintf(stderr, "Error: Memory allocation failure.\n");
                    free(line);
                    ledgerPurge(ledger);
                    return 1;
                }
                p += consumed;
            } else {
                /* Non-Integers found */
                fprintf(stderr, "Error: Encountered non-integer value.\n");
                free(line);
                ledgerPurge(ledger);
                return 1;
            }
        }
    }
    
    /* Distinguish EOF from input error for full return-value checking coverage */
    if (ferror(stdin)) {
        fprintf(stderr, "Error: Input stream failure.\n");
        free(line);
        ledgerPurge(ledger);
        return 1;
    }
    
    ledgerEmit(ledger);
    ledgerPurge(ledger);
    free(line);
    return 0;
}
