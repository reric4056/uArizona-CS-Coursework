
/*
 * File: reach.c
 * Author: Eric Romero
 *
 * Mission:
 *      Build a directed network of nodes and edges from incoming directives. Answer reachability queries using depth-first search.
 *
 * Rules of Engagement:
 *      a. Input arrives via stdin or an optional file argument (./reach [inFile])
 *      b. Directives
 *          i.   @n <codename> enlist a new node
 *          ii.  @e <from> <to> wire a directed edge from 'from' to 'to'
 *          iii. @q <from> <to> query whether a route exists from 'from' to 'to'
 *      c. Malformed lines and non-fatal anomalies are reported and flagged
 *      d. Fatal error: failing to open an input file
 *
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

/* Limits */
#define MAX_NAME 64
#define MAX_LEN  256

/* Forward Identities */
typedef struct AgentNode AgentNode;
typedef struct LinkEdge  LinkEdge;
typedef struct Network   Network;

/* Formats */

/*
 * LinkEdge:
 *      One directed link in an nodes outbox (adjacency node)
 */
struct LinkEdge {
    /* Destination node */
    AgentNode *to;
    /* Next edge in outbox chain */
    LinkEdge  *next;
};

/*
 * AgentNode:
 *      A single node in the global registry
 */
struct AgentNode {
    /* node handle */
    char      codename[MAX_NAME + 1];
    /* DFS visitation marker */
    int       isVisited;
    /* Head of outgoing edges */
    LinkEdge  *outbox;
    /* Next node in registry chain */
    AgentNode *next;};

/*
 * Network:
 *      The registry
 */
struct Network {
    /* Head of nodes list */
    AgentNode *registry;
};

/* Program State */
/* Tripwire for non-fatal errors */
static int anomalyFlag = 0;

/* Prototype Declarations */
static AgentNode *locate_node(AgentNode *head, const char *codename);
static void       enlist_node(Network *net, const char *codename);
static void       wire_edge(Network *net, const char *fromNode, const char *toNode);
static void       clear_flags(Network *net);
static int        infiltrate(AgentNode *from, AgentNode *to);
static void       burn_network(Network *net);


/*
 * locate_node
 * Purpose:
 *      Sweep the registry for the matching name
 *
 * Inputs:
 *      head: first node in the registry chain
 *      codename: target handle
 *
 * Returns:
 *      Pointer to the matching AgentNode, NULL if not found
 */
static AgentNode *locate_node(AgentNode *head, const char *codename) {
    for (AgentNode *scan = head; scan != NULL; scan = scan->next) {
        if (strcmp(scan->codename, codename) == 0) {
            return scan;
        }
    }
    return NULL;
}

/*
 * enlist_node
 * Purpose:
 *      Ensure a node exists in the registry. DUplicate @n is a no-op
 *
 * Inputs:
 *      net: network registry
 *      codename: new node handle
 *
 * Effects:
 *      Allocated a new AgentNode if absent. Otherwise, initializes fields
 *      Prepends the node to the registry chain
 */
static void enlist_node(Network *net, const char *codename) {
    if (locate_node(net->registry, codename) != NULL) {
        return;
    }
    
    AgentNode *a = (AgentNode *)malloc(sizeof(AgentNode));
    
    if (a == NULL) {
        perror("malloc failed for node");
        exit(1);
    }
    
    /* Secure copy with bounds and explicit terminator */
    strncpy(a->codename, codename, MAX_NAME);
    a->codename[MAX_NAME] = '\0';
    a->isVisited = 0;
    a->outbox = NULL;
    
    /* Fast enlist with head insertion */
    a->next = net->registry;
    net->registry = a;
}

/*
 * wire_edge
 * Purpose:
 *      Add a directed edge from 'fromNode' to 'toNode' if both dossiers exist
 *
 * Inputs:
 *      net : network registry
 *      fromNode: origin node name
 *      toNode: target node name
 *
 * Effects:
 *      a. If both exist and edge is new, allocated LinkEdge and prepends to outbox
 *      b. If edge already exists, no change
 *      c. If either endpoint is undeclared, reports anomaly and flags
 *      d. Reports and exit with allocation falure.
 */
static void wire_edge(Network *net, const char *fromNode, const char *toNode) {
    AgentNode *from = locate_node(net->registry, fromNode);
    AgentNode *to   = locate_node(net->registry, toNode);
    
    if (from == NULL || to == NULL) {
        fprintf(stderr, "Undeclared node in @e: %s %s\n", fromNode, toNode);
        anomalyFlag = 1;
        return;
    }
    
    /* Checks for duplicate edge */
    for (LinkEdge *e = from->outbox; e != NULL; e = e->next) {
        if (e->to == to) {
            return;
        }
    }
    
    LinkEdge *e = (LinkEdge *)malloc(sizeof(LinkEdge));
    if (e == NULL) {
        perror("malloc failed for edge");
        exit(1);
    }
    
    e->to = to;
    e->next = from->outbox;
    from->outbox = e;
}

/*
 * clear_flags
 * Purpose:
 *      Resets DFS visitation markers across the entire registry
 *
 * Inputs:
 *      net: network registry
 *
 * Effects:
        Marks all nodes as unvisited prior to a new trace
 */
static void clear_flags(Network *net) {
    for (AgentNode *scan = net->registry; scan != NULL; scan = scan->next) {
        scan->isVisited = 0;
    }
}

/*
 * infiltrate
 * Purpose:
 *      Determine whether a route exists from 'from' to 'to' via DFS
 *
 * Inputs:
 *      from : starting node
 *      to : destination node
 * Returns:
 *      0: Route does not exists
 *      1: Route exists
 */
static int infiltrate(AgentNode *from, AgentNode *to) {
    /* Direct match, route confirmed */
    if (from == to) {
        return 1;
    }
    
    /* Already scouted from here */
    if (from->isVisited) {
        return 0;
    }
    
    /* Flag dossier */
    from->isVisited = 1;
    
    /* Walk all outgoing edges. Recurse on each destination */
    for (LinkEdge *edge = from->outbox; edge != NULL; edge = edge->next) {
        /* Short-circuit on success*/
        if (infiltrate(edge->to, to)) {
            return 1;
        }
    }
    /* Dead end*/
    return 0;
}

/*
 * burn_network
 * Purpose:
 *      Decommission all allocations. First edges, then nodes, finally the registry.
 *
 * Inputs:
 *      net: network registry
 *
 * Effects:
 *      Leaves net as a sterile field.
 */
static void burn_network(Network *net) {
    if (net == NULL) {
        return;
    }
    AgentNode *curr = net->registry;
    
    while (curr != NULL) {
        /* Purge outbox */
        LinkEdge *edge = curr->outbox;
        
        while (edge != NULL) {
            LinkEdge *nextEdge = edge->next;
            free(edge);
            edge = nextEdge;
        }
        
        /* Reture node dossier */
        AgentNode *nextNode = curr->next;
        free(curr);
        curr = nextNode;
    }
    net->registry = NULL;
}

/*
 * main
 * Purpose:
 *      Entry point. Select intel source, parse directives, build graph, answer queries
 *
 * Behavior:
 *      a. stdin if no file provided. Otherwise, open and read provided file
 *      b. Extra arguments beyond the first are ignored
 *      c. On EOF, sanitize memory and return status by anomalyFlag
 *
 * Errors:
 *      fopen and fclose failures are reported.
 */
int main(int argc, char *argv[]) {
    /* Source selection */
    FILE *intelStream;
    
    if (argc == 1) {
        intelStream = stdin;
    } else {
        if (argc > 2) {
            fprintf(stderr, "Extra arguments ignored\n");
            anomalyFlag = 1;
        }
        
        intelStream = fopen(argv[1], "r");
        
        if (intelStream == NULL) {
            perror("Error opening input file");
            return 1;
        }
    }
    
    /* Establish an empty, zeroed registry */
    Network *net = (Network *)calloc(1, sizeof(Network));
    
    if (net == NULL) {
        perror("calloc failed for network");
        if (intelStream != stdin) fclose(intelStream);
        return 1;
    }
    
    char dossier[MAX_LEN];
    
    /* Intake loop: parse, validate, execute */
    while (fgets(dossier, sizeof dossier, intelStream) != NULL) {
        /* trim leading whitespace */
        char *scan = dossier;
        while (*scan == ' ' || *scan == '\t') {
            ++scan;
        }
        
        /* Quiet line. No effect */
        if (*scan == '\0' || *scan == '\n') {
            continue;
        }
        
        /* OpCode and bounded tokens */
        char opCode[3] = {0, 0, 0};
        
        char aliasA[MAX_NAME + 1], aliasB[MAX_NAME + 1];
        
        aliasA[0] = aliasB[0] = '\0';
        
        /* Must at least recover the opCode*/
        if (sscanf(scan, "%2s", opCode) != 1) {
            fprintf(stderr, "Invalid line: %s", scan);
            anomalyFlag = 1;
            continue;
        }
        
        if (strcmp(opCode, "@n") == 0) {
            /* @n <codename>*/
            if (sscanf(scan, "%2s %64s", opCode, aliasA) != 2) {
                fprintf(stderr, "Invalid @n line: %s", scan);
                anomalyFlag = 1;
                continue;
            }
            
            if (strlen(aliasA) > MAX_NAME) {
                fprintf(stderr, "Name too long (> %d): %s\n", MAX_NAME, aliasA);
                anomalyFlag = 1;
                continue;
            }
            
            enlist_node(net, aliasA);
            
        } else if (strcmp(opCode, "@e") == 0) {
            /* @e <from> <to> */
            if (sscanf(scan, "%2s %64s %64s", opCode, aliasA, aliasB) != 3) {
                fprintf(stderr, "Invalid @e line: %s", scan);
                anomalyFlag = 1;
                continue;
            }
            
            if (strlen(aliasA) > MAX_NAME || strlen(aliasB) > MAX_NAME) {
                fprintf(stderr, "Name too long in @e line\n");
                anomalyFlag = 1;
                continue;
            }
            
            wire_edge(net, aliasA, aliasB);
        } else if (strcmp(opCode, "@q") == 0) {
            /* @q <from> <to> */
            if (sscanf(scan, "%2s %64s %64s", opCode, aliasA, aliasB) != 3) {
                fprintf(stderr, "Invalid @q line: %s", scan);
                anomalyFlag = 1;
                continue;
            }
            if (strlen(aliasA) > MAX_NAME || strlen(aliasB) > MAX_NAME) {
                fprintf(stderr, "Name too long in @q line\n");
                anomalyFlag = 1;
                continue;
            }
            
            AgentNode *origin = locate_node(net->registry, aliasA);
            AgentNode *target = locate_node(net->registry, aliasB);
            
            if (origin == NULL || target == NULL) {
                /* malformed query. Flag and continue */
                fprintf(stderr, "Undeclared node in @q: %s %s\n", aliasA, aliasB);
                anomalyFlag = 1;
                continue;
            }
            
            clear_flags(net);
            printf("%d\n", infiltrate(origin, target));
        } else {
            /* Unknown directive. Flag and continue */
            fprintf(stderr, "Unknown directive: %s", scan);
            anomalyFlag = 1;
            continue;
        }
    }
    
    /* Clean shutdown */
    if (intelStream != stdin) {
        if (fclose(intelStream) == EOF) {
            perror("Error closing input file");
            anomalyFlag = 1;
        }
    }
    
    burn_network(net);
    free(net);
    return (anomalyFlag ? 1 : 0);
}
