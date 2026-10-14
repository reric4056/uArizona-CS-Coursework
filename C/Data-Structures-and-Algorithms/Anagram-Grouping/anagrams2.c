/*
 * File: anagrams2.c
 * Author: Eric Romero
 * Purpose:
 *      This program reads words from the input and groups together words that are anagrams of each other.
 *
 * Notes:
 *      Duplicates are preserved.
 *      Original case is preserved in output.
 *      Memory allocation errors are flagged and result in early termination.
 */

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <ctype.h>

/* DATA STRUCTURES */

/*
 * Representation of a word in a group
 */
typedef struct WordEntry {
    /* Original word */
    char *word;
    /* Pointer to next word in same group */
    struct WordEntry *next;
} WordEntry;

/*
 * Representation of group (words share same sorted key)
 */
typedef struct GroupEntry {
    /* Sorted lowercase key */
    char *key;
    /* Linked List of words */
    WordEntry *words;
    /* Pointer to next group */
    struct GroupEntry *next;
} GroupEntry;

/* PROTOTYPES */

/*
 * validate_word(s)
 * Purpose:
 *      Checks if a string is made up of only alphabetical letters
 *
 * Parameters:
 *      s : Pointer to string to validate
 *
 * Returns:
 *      0 : string is not alphabetical
 *      1 : string is alphabetical
 */
static int validate_word(const char *s);

/*
 * normalize_case(s)
 * Purpose:
 *      Converts all characters in string to lowercase
 *
 * Parameters:
 *      s : Pointer to string to update
 *
 * Returns:
 *      None
 */
static void normalize_case(char *s);

/*
 * cipher_sort(s)
 * Purpose:
 *      Sorts the characters in a string alphabetically. Used insertion sort
 *
 * Parameters:
 *      s : Pointer to string to sort
 *
 * Returns:
 *      None
 */
static void cipher_sort(char *s);

/*
 * locate_group(head, key)
 * Purpose:
 *      Search for an anagram group with a given key
 *
 * Parameters:
 *      head : Pointer to the head of the list of groups
 *      key  : Key to find
 *
 * Returns:
 *      Pointer to a corresponding group
 *      NULL otherwise
 */
static GroupEntry *locate_group(GroupEntry *head, const char *key);

/*
 * attach_group(headp, key)
 * Purpose:
 *      Creation of a new grouping and adds to the linked list of groups.
 *
 * Parameters:
 *      headp : Pointer to head pointer of linked list of groups
 *      key   : Key String for corresponding group
 *
 * Returns:
 *     Pointer to new group node if successful
 *     NULL if memory allocation is unsuccessful
 */
static GroupEntry *attach_group(GroupEntry **headp, const char *key);

/*
 * attach_word(headp, orig)
 * Purpose:
 *      Adds a word to an existing groups node in the linked list of groups
 *
 * Parameters:
 *      headp : Pointer to head pointer of linked list of groups
 *      orig  : String
 *
 * Returns:
 *      0 if process is successful
 *      -1 if memory allocation is unsuccessful
 */
static int attach_word(WordEntry **headp, const char *orig);

/*
 * display_groups(head)
 * Purpose:
 *      Prints each group node from the linked list of group nodes.
 *
 * Parameters:
 *      head : Pointer to the head of the linked list of group nodes
 *
 * Returns:
 *      None
 */
static void display_groups(const GroupEntry *head);

/* METHOD DEFINITIONS */

static int validate_word(const char *str) {
    for (; *str; ++str) {
        if (!isalpha((unsigned char) *str)) {
            return 0;
        }
    }
    return 1;
}

static void normalize_case(char *str) {
    for (; *str; ++str) {
        *str = (char)tolower((unsigned char) *str);
    }
}

static void cipher_sort(char *str) {
    for (int i = 1; str[i] != '\0'; ++i) {
        char key = str[i];
        int j = i - 1;
        
        while (j >= 0 && str[j] > key) {
            str[j + 1] = str[j];
            j--;
        }
        
        str[j + 1] = key;
    }
    
}

static GroupEntry *locate_group(GroupEntry *head, const char *key) {
    for (GroupEntry *p = head; p != NULL; p = p->next) {
        if (strcmp(p->key, key) == 0) {
            return p;
        }
    }
    
    return NULL;
}

static GroupEntry *attach_group(GroupEntry **headp, const char *key) {
    GroupEntry *group = (GroupEntry *)malloc(sizeof(*group));
    
    if (!group) {
        return NULL;
    }
    
    group->key = strdup(key);
    
    if (!group->key) {
        free(group);
        return NULL;
    }
    
    group->words = NULL;
    group->next = NULL;
    
    if (*headp == NULL) {
        *headp = group;
    } else {
        GroupEntry *iter = *headp;
        
        while (iter->next) {
            iter = iter->next;
        }
        
        iter->next = group;

    }
    
    return group;
}

static int attach_word(WordEntry **headp, const char *str) {
    WordEntry *currWord = (WordEntry *) malloc(sizeof(*currWord));
    
    if (!currWord) {
        return -1;
    }
    
    currWord->word = strdup(str);
    
    if (!currWord->word) {
        free(currWord);
        return -1;
    }
    
    currWord->next = NULL;
    
    if (*headp == NULL) {
        *headp = currWord;
    } else {
        WordEntry *iter = *headp;
        
        while (iter->next) {
            iter = iter->next;
        }
        
        iter->next = currWord;
    }
    
    return 0;
}

static void display_groups(const GroupEntry *head) {
    for (const GroupEntry *group = head; group != NULL; group = group->next) {
        const WordEntry *currWord = group->words;
        
        int first = 1;
        
        while (currWord) {
            if (!first) {
                putchar(' ');
            }
            
            fputs(currWord->word, stdout);
            first = 0;
            currWord = currWord->next;
            
        }
        
        putchar('\n');
    }
}

/* MAIN */

/*
 * main()
 * Purpose:
 *      Central program control platform. Calls previously defined methods for anagram process to successfully (or unsuccessfully) evaluate.
 *
 * Parameters:
 *      NONE
 *
 * Returns:
 *      0 : Program runs successfully
 *      1 : Invalid words encountered. Memory allocation unsuccessful
 */
int main(void) {
    GroupEntry *groups = NULL;
    
    char buf[64 + 1];
    
    int errorFound = 0;
    
    while (scanf("%63s", buf) == 1) {
        if (!validate_word(buf)) {
            fprintf(stderr, "Error: invalid string \"%s\" (non-alphabetic)\n", buf);
            errorFound = 1;
            continue;
            
        }
        
        char key[sizeof(buf)];
        strcpy(key, buf);
        
        normalize_case(key);
        cipher_sort(key);
        
        GroupEntry *group = locate_group(groups, key);
        
        if (group == NULL) {
            group = attach_group(&groups, key);
            
            if (group == NULL) {
                fprintf(stderr, "Error: out of memory creating group\n");
                return 1;
            }
        }
        
        if (attach_word(&group->words, buf) != 0) {
            fprintf(stderr, "Error: out of memory adding word\n");
            return 1;
        }
    }
    
    display_groups(groups);
    
    return errorFound ? 1 : 0;
}
