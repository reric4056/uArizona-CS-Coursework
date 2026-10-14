/*
 * File: mymake.c
 * Author: Eric Romero
 * Purpose:
 *      Entry point for the mymake process.
 *      Responsibilities:
 *          Validates command-line arguments
 *          Opens the makefile
 *          Builds the ForgeGraph via load_blueprint
 *          Resolves dependency links
 *          Finds the requested target
 *          Performs post-order traversal and prints output
 */

#include "blueprint.h"

int main(int argc, const char *argv[]) {
    /* Expect arguments: makefile and target */
    if (argc != 3) {
        fail_usage("mymake <makefile> <target>");
    }
    
    const char *makeFileName = argv[1];
    const char *targetName   = argv[2];

    /* Open the makefile */
    FILE *fp = fopen(makeFileName, "r");
    if (fp == NULL) {
        fail_errno("Failed to open makefile");
    }

    /* Parse and build the graph */
    ForgeGraph *graph = load_blueprint(fp);
    fclose(fp);

    /* Resolve dependency links (names -> CraftNode pointers) */
    if (forge_resolve_links(graph) != 0) {
        forge_release(graph);
        fail_format("unresolved dependency in graph");
    }

    /* Look up the requested target */
    CraftNode *root = forge_lookup(graph, targetName);
    if (root == NULL) {
        forge_release(graph);
        fail_format("unknown target");
    }

    /* Perform post-order traversal and print nodes/commands */
    postorder_walk(root);

    /* Clean up all allocated memory */
    forge_release(graph);

    /* Normal completion */
    return 0;
}
