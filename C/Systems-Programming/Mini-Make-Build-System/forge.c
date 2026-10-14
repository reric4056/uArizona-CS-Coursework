/*
 * File: forge.c
 * Author: Eric Romero
 * Purpose:
 *      Implements the core routines for mymake:
 *      Creation and management of the ForgeGraph and its CraftNodes.
 */

#include "blueprint.h"

/* Declarations */
static CraftNode *craft_create(const char *name);
static void free_dep_list(DepString *head);
static void free_cmd_list(CmdString *head);

/* CraftNode Helper*/

/*
 * craft_create
 * Purpose:
 *      Allocate and initialize a new CraftNode with the given name.
 *      On memory failure, prints an error and exits(1).
 */
static CraftNode *craft_create(const char *name) {
    CraftNode *node = (CraftNode *)malloc(sizeof(CraftNode));
    
    if (node == NULL) {
        fail_errno("mymake: malloc CraftNode");
    }
    
    node->name = strdup(name);
    
    if (node->name == NULL) {
        free(node);
        fail_errno("mymake: strdup target name");
    }
    
    node->depNames = NULL;
    node->children = NULL;
    node->childCount = 0;
    node->commands = NULL;
    node->visited = 0;
    node->next = NULL;
    
    return node;
}

/* Free Helpers */

/*
 * free_dep_list
 * Purpose:
 *      Free a linked list of DepString nodes.
 */
static void free_dep_list(DepString *head) {
    DepString *curr = head;
    
    while (curr != NULL) {
        DepString *next = curr->next;
        free(curr->name);
        free(curr);
        curr = next;
    }
}

/*
 * free_cmd_list
 * Purpose:
 *      Free a linked list of CmdString nodes.
 */
static void free_cmd_list(CmdString *head) {
    CmdString *curr = head;
    
    while (curr != NULL) {
        CmdString *next = curr->next;
        free(curr->text);
        free(curr);
        curr = next;
    }
}

/* ForgeGraph Methods */

/*
 * forge_create
 * Purpose:
 *      Allocate and initialize an empty ForgeGraph.
 *      On memory allocation failure, print an error and exit(1).
 */
ForgeGraph *forge_create(void) {
    ForgeGraph *graph = (ForgeGraph *)malloc(sizeof(ForgeGraph));
    
    if (graph == NULL) {
        fail_errno("mymake: malloc ForgeGraph");
    }
    
    graph->head = NULL;
    return graph;
}

/*
 * forge_lookup
 * Purpose:
 *      Searches the ForgeGraph for a CraftNode with the given name.
 *
 * Parameters:
 *      graph: pointer to a ForgeGraph
 *      name:  target/dependency name
 *
 * Returns:
 *      Pointer to matching CraftNode if it exists, or NULL.
 */
CraftNode *forge_lookup(ForgeGraph *graph, const char *name) {
    if (graph == NULL || name == NULL) {
        return NULL;
    }
    
    CraftNode *curr = graph->head;
    
    while (curr != NULL) {
        if (strcmp(curr->name, name) == 0) {
            return curr;
        }
        curr = curr->next;
    }
    
    return NULL;
}

/*
 * forge_generate
 * Purpose:
 *      Ensures a CraftNode with the given name exists in the ForgeGraph.
 *          If it already exists, returns the existing node.
 *          Otherwise, creates a new node, links it into the list, and returns it.
 *
 * Parameters:
 *      graph: pointer to a ForgeGraph (must not be NULL)
 *      name:  target/dependency name (must not be NULL)
 */
CraftNode *forge_generate(ForgeGraph *graph, const char *name) {
    if (graph == NULL || name == NULL) {
        fail_format("internal error: forge_generate given NULL");
    }
    
    CraftNode *found = forge_lookup(graph, name);
    
    if (found != NULL) {
        return found;
    }
    
    CraftNode *newNode = craft_create(name);
    newNode->next = graph->head;
    graph->head   = newNode;
    
    return newNode;
}

/*
 * forge_release
 * Purpose:
 *      Frees memory for ForgeGraph, CraftNodes, etc.
 *
 * Parameters:
 *      graph: Pointer to an existing ForgeGraph (may be NULL)
 */
void forge_release(ForgeGraph *graph) {
    if (graph == NULL) {
        return;
    }
    
    CraftNode *curr = graph->head;
    
    while (curr != NULL) {
        CraftNode *nextNode = curr->next;
        
        free(curr->name);
        free_dep_list(curr->depNames);
        free_cmd_list(curr->commands);
        free(curr->children);   /* safe when NULL */
        free(curr);
        
        curr = nextNode;
    }
    free(graph);
}

/*   Link Resolution   */

/*
 * forge_resolve_links
 * Purpose:
 *      Resolves dependency names in each CraftNode into pointers to other CraftNodes.
 *      Populates the children array and childCount fields.
 *
 * Parameters:
 *      graph: pointer to an existing ForgeGraph
 *
 * Returns:
 *      0: Successful
 *      1: At least one dependency name could not be matched to a CraftNode.
 *
 */
int forge_resolve_links(ForgeGraph *graph) {
    if (graph == NULL) {
        fail_format("internal error: forge_resolve_links given NULL graph");
    }
    
    for (CraftNode *rule = graph->head; rule != NULL; rule = rule->next) {
        int count = 0;
        for (DepString *dep = rule->depNames; dep != NULL; dep = dep->next) {
            count++;
        }
        
        if (count == 0) {
            rule->children   = NULL;
            rule->childCount = 0;
            continue;
        }
        
        rule->children = (CraftNode **)malloc(sizeof(CraftNode *) * count);
        if (rule->children == NULL) {
            fail_errno("mymake: malloc children array");
        }
        
        rule->childCount = count;
        int index = 0;
        
        for (DepString *dep = rule->depNames; dep != NULL; dep = dep->next) {
            CraftNode *child = forge_lookup(graph, dep->name);
            
            if (child == NULL) {
                /* This should not happen if validator created nodes for all deps */
                fprintf(stderr, "mymake: unknown dependency '%s' for target '%s'\n", dep->name, rule->name);
                return 1;
            }
            
            rule->children[index++] = child;
        }
    }
    return 0;
}

/* CraftNode Methods */

/*
 * craft_add_dep_name
 * Purpose:
 *      Adds a name for a dependency to the depNames list of the given CraftNode.
 *
 * Parameters:
 *      rule: pointer to a CraftNode
 *      depName: dependency name
 */
void craft_add_dep_name(CraftNode *rule, const char *depName) {
    if (rule == NULL || depName == NULL) {
        fail_format("internal error: craft_add_dep_name given NULL");
    }
    
    DepString *node = (DepString *)malloc(sizeof(DepString));
    
    if (node == NULL) {
        fail_errno("mymake: malloc DepString");
    }
    
    node->name = strdup(depName);
    
    if (node->name == NULL) {
        free(node);
        fail_errno("mymake: strdup depName");
    }

    node->next = NULL;
    
    if (rule->depNames == NULL) {
        rule->depNames = node;
    } else {
        DepString *tail = rule->depNames;
        while (tail->next != NULL) {
            tail = tail->next;
        }
        tail->next = node;
    }
}

/*
 * craft_add_command
 * Purpose:
 *      Adds a command line to the commands list of the given CraftNode.
 *
 * Parameters:
 *      rule: pointer to a CraftNode
 *      cmdText: command text (no leading tab)
 */
void craft_add_command(CraftNode *rule, const char *cmdText) {
    if (rule == NULL || cmdText == NULL) {
        fail_format("internal error: craft_add_command given NULL");
    }
    
    CmdString *node = (CmdString *)malloc(sizeof(CmdString));
    if (node == NULL) {
        fail_errno("mymake: malloc CmdString");
    }
    
    node->text = strdup(cmdText);
    
    if (node->text == NULL) {
        free(node);
        fail_errno("mymake: strdup cmdText");
    }

    node->next = NULL;

    if (rule->commands == NULL) {
        rule->commands = node;
    } else {
        CmdString *tail = rule->commands;
        while (tail->next != NULL) {
            tail = tail->next;
        }
        tail->next = node;
    }
}

/* Failure Methods */

/*
 * fail_usage(msg)
 * Purpose:
 *      Used when command-line arguments are invalid.
 */
void fail_usage(const char *msg) {
    if (msg != NULL) {
        fprintf(stderr, "%s\n", msg);
    }
    exit(1);
}

/*
 * fail_format(msg)
 * Purpose:
 *      Used when the input file is structurally invalid.
 */
void fail_format(const char *msg) {
    if (msg != NULL) {
        fprintf(stderr, "mymake: %s\n", msg);
    } else {
        fprintf(stderr, "mymake: format is invalid\n");
    }
    exit(1);
}

/*
 * fail_errno(ctx)
 * Purpose:
 *      Used when a library/system call fails.
 */
void fail_errno(const char *ctx) {
    if (ctx != NULL) {
        perror(ctx);
    } else {
        perror("mymake");
    }
    exit(1);
}

/*   Traversal   */

/*
 * postorder_walk
 * Purpose:
 *      Performs a post-order / depth-first traversal.
 *      Starts from a given root CraftNode.
 *      Prints node's name followed by associated commands.
 */
void postorder_walk(CraftNode *root) {
    if (root == NULL) {
        return;
    }
    
    if (root->visited) {
        return;
    }
    
    root->visited = 1;
    
    for (int i = 0; i < root->childCount; i++) {
        postorder_walk(root->children[i]);
    }
    
    printf("%s\n", root->name);
    
    for (CmdString *cmd = root->commands; cmd != NULL; cmd = cmd->next) {
        printf("  %s\n", cmd->text);
    }
}
