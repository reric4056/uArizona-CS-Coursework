/*
 * File: validator.c
 * Author: Eric Romero
 * Purpose:
 *      Parse and validate the "mymake" file.
 *      Distinguishes rule lines from commands.
 *      Enforces format rules.
 *      Populates the ForgeGraph structures.
 */

#include "blueprint.h"
#include <ctype.h>

/* Declarations */
static int   check_if_empty(const char *s);
static char *trim(char *s);
static void  check_rule_line(ForgeGraph *graph, const char *line, CraftNode **aliveRule);
static void  check_command_line(CraftNode *aliveRule, const char *line);
static char *next_word(char **cursor);

/*
 * next_word
 * Purpose:
 *      Extract next whitespace-separated word from a string.
 *
 * Behavior:
 *      Advances *cursor to next token or NULL.
 *      Returns malloc'ed copy of the word (caller frees).
 *      Enforces max length 64.
 */
static char *next_word(char **cursor) {
    char *s = *cursor;

    /* Skip leading spaces/tabs */
    while (*s == ' ' || *s == '\t') {
        s++;
    }

    if (*s == '\0') {        /* no more words */
        *cursor = s;
        return NULL;
    }

    char *start = s;

    /* Find end of word */
    while (*s != '\0' && *s != ' ' && *s != '\t') {
        s++;
    }

    size_t len = (size_t)(s - start);

    if (len > 64) {  /* Enforce word length limit */
        fail_format("word exceeds 64 characters");
    }

    /* Copy word into new buffer */
    char *word = malloc(len + 1);
    if (!word) fail_errno("mymake: malloc for word");

    strncpy(word, start, len);
    word[len] = '\0';

    *cursor = s;    /* Advance main cursor */

    return word;
}

/*
 * load_blueprint
 * Purpose:
 *      Parse the entire file.
 */
ForgeGraph *load_blueprint(FILE *fp) {
    if (fp == NULL) {
        fail_format("internal error: load_blueprint given NULL file");
    }

    ForgeGraph *graph = forge_create();
    char *origLine = NULL;
    size_t cap = 0;
    ssize_t len;

    CraftNode *aliveRule = NULL;

    while ((len = getline(&origLine, &cap, fp)) != -1) {

        if (len > 0 && origLine[len - 1] == '\n') {
            origLine[len - 1] = '\0';
        }

        if (check_if_empty(origLine)) continue;

        /* COMMAND LINE */
        if (origLine[0] == '\t') {
            if (origLine[1] == '\t')
                fail_format("illegal indentation");

            if (aliveRule == NULL)
                fail_format("command before rule");

            check_command_line(aliveRule, origLine);
            continue;
        }

        /* RULE LINE */
        char *colon = strchr(origLine, ':');
        if (!colon)
            fail_format("colon missing");

        check_rule_line(graph, origLine, &aliveRule);
    }

    free(origLine);
    return graph;
}

/* Helper Functions */

static int check_if_empty(const char *s) {
    if (!s) return 1;
    while (*s) {
        if (!isspace((unsigned char)*s)) return 0;
        s++;
    }
    return 1;
}

static char *trim(char *s) {
    if (!s) return NULL;

    /* skip leading */
    while (*s && isspace((unsigned char)*s))
        s++;

    if (*s == '\0')
        return s;

    char *end = s + strlen(s) - 1;
    while (end > s && isspace((unsigned char)*end))
        end--;

    end[1] = '\0';
    return s;
}

/*
 * check_rule_line
 * Purpose:
 *      Parse: target : dep1 dep2 dep3...
 */
static void check_rule_line(ForgeGraph *graph, const char *line, CraftNode **aliveRule) {
    char *twin = strdup(line);
    if (!twin)
        fail_errno("mymake: strdup rule line");

    char *colon = strchr(twin, ':');
    *colon = '\0';

    char *targetName = trim(twin);
    char *depsPart   = trim(colon + 1);

    if (*targetName == '\0') {
        free(twin);
        fail_format("empty target name");
    }

    if (strlen(targetName) > 64) {
        free(twin);
        fail_format("target exceeds 64 characters");
    }

    /* Create or validate rule */
    CraftNode *rule = forge_lookup(graph, targetName);
    if (rule) {
        if (rule->depNames || rule->commands) {
            free(twin);
            fail_format("target appears multiple times");
        }
    } else {
        rule = forge_generate(graph, targetName);
    }

    /* Parse dependencies manually */
    char *cursor = depsPart;
    char *word;

    while ((word = next_word(&cursor)) != NULL) {
        forge_generate(graph, word);
        craft_add_dep_name(rule, word);
        free(word);
    }

    *aliveRule = rule;
    free(twin);
}

/*
 * check_command_line
 * Purpose:
 *      Strip leading tab and store command text.
 */
static void check_command_line(CraftNode *aliveRule, const char *line) {
    if (!aliveRule)
        fail_format("internal error: NULL rule in command handler");

    /* Skip leading tab */
    const char *cmd = line + 1;

    /* Skip optional space */
    if (*cmd == ' ')
        cmd++;

    /* strdup once */
    char *dup = strdup(cmd);
    if (!dup)
        fail_errno("mymake: strdup cmd");

    craft_add_command(aliveRule, dup);
    free(dup);
}
