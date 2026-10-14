/*
 * File: blueprint.h
 * Author: Eric Romero
 * Purpose:
 *      Declare core structures and operations used for the "mymake" utility.
 *      It is centered around a ForgeGraph made of CraftNodes:
 *          Each node represents a build rule:
 *              Target
 *              Dependencies
 *              Associated commands
 *
 *      Parsing, dependency outcome, traversal, and cleanup are
 *      funneled through this interface so that main() can remain focused.
 */

#ifndef _BLUEPRINT_H_
#define _BLUEPRINT_H_

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

/* Declarations for structures */
typedef struct CmdString CmdString;
typedef struct DepString DepString;
typedef struct CraftNode CraftNode;
typedef struct ForgeGraph ForgeGraph;

/* STRUCT DEFINTIONS */

/*
 * CmdString
 * Purpose:
 *      Linked-List node representing a single command line associated with a build rule.
 *      The text field stores the command.
 *      Printing routines are responsible for indents.
 *
 * Definitions of Fields:
 *      text: Dynamically allocated command text
 *      next: Next command in list or NULL
 */
struct CmdString {
    char *text;
    CmdString *next;
};

/*
 * DepString
 * Purpose:
 *      Linked-List node representing single dependency name for a build rule.
 *      During parsing, names are only known.
 *      After parsing, resolve_links() will translate into CraftNode pointers
 *
 * Definitions of Fields:
 *      name: Dynamically allocated dependency name
 *      next: Next dependency in list or NULL
 */
struct DepString {
    char *name;
    DepString *next;
};

/*
 * CraftNode
 * Purpose:
 *      Represents one build rule (aka target) in the graph
 *
 * Definitions of Fields:
 *      name: Target name
 *      depNames: Linked list of dependency names
 *      children: array fo pointers to child CraftNodes after dependency resolution
 *      childCount: number of entries in the children array
 *      commands: Linked List of CmdString objects
 *      visited: Visitation marker
 *      next: next CraftNode in ForgeGraph's node list, or NULL
 */
struct CraftNode {
    char *name;
    DepString *depNames;
    CraftNode **children;
    int childCount;
    CmdString *commands;
    int visited;
    CraftNode *next;
};

/*
 * ForgeGraph
 * Purpose:
 *      Container for CraftNodes in the build specification.
 *      Nodes are kept in a linked-list.
 *
 * Definitions of Fields:
 *      head: Head of the node list, or NULL
 */
struct ForgeGraph {
    CraftNode *head;
};

/* METHOD DEFINITIONS */

/*
 * load_blueprint
 * Purpose:
 *      Parses a given file stream as a "mymake" specification.
 *      Constructs a ForgeGraph representing:
 *          all rules
 *          dependency names
 *          associated commands
 *
 * Definitions of Fields:
 *      fp: open FILE for reading the input makefile
 *
 * Returns:
 *      Pointer to full ForgeGraph or informative message on memory error or fatal parsing
 */
ForgeGraph *load_blueprint(FILE *fp);

/*
 * craft_add_dep_name
 * Purpose:
 *      Adds a name for a dependency to the depNames list of the given CraftNode.
 *
 * Definitions of Fields:
 *      rule: pointer to a CraftNode
 *      depName: dependency name
 */
void craft_add_dep_name(CraftNode *rule, const char *depName);

/*
 * craft_add_command
 * Purpose:
 *      Adds a command line to the commands list of the given CraftNode
 *
 * Definitions of Fields:
 *      rule: pointer to a CraftNode
 *      depName: command text (no tab)
 */
void craft_add_command(CraftNode *rule, const char *cmdText);

/*
 * forge_create
 * Purpose:
 *      Pointer to a newly allocated ForgeGraph.
 *      If memory allocation fails, error message is printed with exit status of 1.
 */
ForgeGraph *forge_create(void);

/*
 * forge_lookup
 * Purpose:
 *      Searches the ForgeGraph for a CraftNode with the target name
 *
 * Parameters:
 *      graph: pointer to a ForgeGraph
 *      name: target name
 *
 * Returns:
 *      Pointer to matching CraftNode if exists in ForgeGraph, or NULL
 */
CraftNode *forge_lookup(ForgeGraph *graph, const char *name);

/*
 * forge_generate
 * Purpose:
 *      If ForgeGraph NULL:
 *          Creates a new node and inserts into it. Returns the node.
 *      Otherwise:
 *          Returns an existing CraftNode with the given name if it exists.
 *
 * Definitions of Fields:
 *      graph: pointer to a ForgeGraph
 *      name: target name
 */
CraftNode *forge_generate(ForgeGraph *graph, const char *name);

/*
 * forge_resolve_links
 * Purpose:
 *      Resolves dependency names in each CraftNode into pointers to other CraftNodes.
 *      Populates the children array and childCount fields.
 *
 * Definitions of Fields:
 *      graph: pointer to an existing ForgeGraph
 *
 * Returns:
 *      0: Successful
 *      1: Dependency name can't be match to a CraftNode.
 */
int forge_resolve_links(ForgeGraph *graph);

/*
 * forge_release
 * Purpose:
 *      Frees memory for ForgeGraph, CraftNodes, etc..
 *
 * Definitions of Fields:
 *      graph: Pointer to an existing ForgeGraph
 */
void forge_release(ForgeGraph *graph);

/*
 * postorder_walk
 * Purpose:
 *      Performs a post-order / depth-first traversal.
 *      Starts from a given root CraftNode.
 *      Prints node's name follows by associated commands.
 *
 * Definitions of Fields:
 *      root: Pointer to starting CraftNode, or NULL
 */
void postorder_walk(CraftNode *root);

/*
 * fail_usage(msg)
 * Purpose:
 *      Used when command-line arguments are invalid
 */
void fail_usage(const char *msg);

/*
 * fail_format(msg)
 * Purpose:
 *      Used when the input file is structurally invalid.
 */
void fail_format(const char *msg);

/*
 * fail_errno(ctx)
 * Purpose:
 *      Used for when a library/system call fails.
 */
void fail_errno(const char *ctx);

#endif /* _BLUEPRINT_H_ */
