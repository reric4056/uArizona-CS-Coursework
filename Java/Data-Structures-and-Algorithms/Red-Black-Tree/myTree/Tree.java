/*
 * Tree.java
 * Author: Eric Romero
 * Purpose: Red-black search tree package. I have implemented method:
 * 		find
 * 		max
 * 		min
 * 		next
 * 		prev
 * 		insert
 */

package myTree;


// Red-black search tree class
//
public class Tree
{
    // Color constants
    //
    private final static boolean RED   = true;
    private final static boolean BLACK = false;

    // Pool of free nodes
    //
    private static Node pool = null;
    
    // Tree node
    //
    private static class Node
    {
	int     key;
	Object  item;
        Node    left;
	Node    right;
	boolean color;

	final static int BLOCK_SIZE = 32; // Number of nodes in a block

	// Node constructor
	//
	public Node ()
	{
	    key = 0;
	    item = null;
	    left = null;
	    right = null;
	    color = false;
	}
	public Node (int k, Object x, Node l, Node r, boolean c)
	{
	    key   = k;
	    item  = x;
	    left  = l;
	    right = r;
	    color = c;
	}

        // allocate -- Allocate a node by taking one from the pool
        //
        public static Node allocate ()
        {
            // If the pool is empty, allocate a block of nodes and put them
            // into the pool
            //
            if (pool == null)
		{
		    // Allocate a block of nodes
		    //
		    Node[] block = new Node[BLOCK_SIZE];
		    for (int i = 0; i < BLOCK_SIZE; i++)
			block[i] = new Node();

		    // Link the nodes in the block into a pool (reusing the
		    // field "right" to form a singly linked list)
		    //
		    pool = block[0];
		    for (int i = 0; i < BLOCK_SIZE - 1; i++)
			block[i].right = block[i + 1];
		    block[BLOCK_SIZE - 1].right = null;
		}

            // Take a node off the pool
            //
            Node v = pool;
            pool = v.right;

            return v;
        }
	public static Node allocate
	    (int k, Object x, Node l, Node r, boolean c)
        {
            Node v = allocate();

            // Initialize the node fields
            //
	    v.key   = k;
            v.item  = x;
            v.left  = l;
            v.right = r;
	    v.color = c;

            return v;
        }

        // free -- Free a node by putting it into the pool
        //
        public void free ()
        {
            this.right = pool;
            pool = this;
        }

	// Scaffolding
	//
	public boolean isBlack () { return color == BLACK; }
	public boolean isRed   () { return color == RED; }
    }

    // Null node
    //
    private static Node nil = new Node (0, null, null, null, BLACK);

    // Root of the tree
    //
    private Node root;

    // Tree constructor
    //
    public Tree () { root = nil; }
    
    /*
     * Method: 
     * 	find
     * 
     * Purpose: 
     * 	Searches for the item associated with a key 
     * within the tree.
     * 
     * Pre Condition: 
     * 	Tree must be initialized
     * 
     * Post Condition: 
     * 	No changes to the tree
     * 
     * Return:
     * 	Returns the associated item if the key is in the tree, 
     * or null otherwise.
     */
    public Object find (int k) {
    	Node currNode = root;
    	while (currNode != nil) {
    		// Key is found
    		if (k == currNode.key) {
    			return currNode.item;
    		// Move to left pointer
    		} else if (k < currNode.key) {
    			currNode = currNode.left;
    		// Move to right pointer
    		} else {
    			currNode = currNode.right;
    		}
    	}
    	
    	// Key is not found
		return null;
    	
    } 

    /*
     * Method:
     * 	min
     * 
     * Purpose:
     * 	Finds the minimum/smallest key found in the tree
     * 
     * Pre Condition:
     * 	Tree has to be initialized
     * 
     * Post Condition:
     * 	No changes to tree
     * 
     * Return:
     * 	Returns the minimum key if it exists. Otherwise, returns null.
     */
    public Integer min () {
    	// Tree is empty
    	if (root == nil) {
    		return null;
    	}
    	
    	Node currNode = root;
    	while (currNode.left != nil) {
    		// Move left. Min found in furthest left due to BST rules
    		currNode = currNode.left;
    	}
    	
		return currNode.key;
    	
    }

    
    // max -- Find the maximum key in a tree
    //
    // Returns the maximum key if the tree is not empty, or null otherwise.
    //
    
    /*
     * Method:
     * 	max
     * 
     * Purpose:
     * 	Finds the maximum/largest key in the tree. 
     * 
     * Pre Condition:
     * 	Tree is initialized
     * 
     * Post Condition:
     * 	No changes to the tree
     * 
     * Return:
     * 	If exists, max key is returned. If not, return null.
     */
    public Integer max () {
    	// Tree is empty
    	if (root == nil) {
    		return null;
    	}
    	
    	Node currNode = root;
    	
    	while (currNode.right != nil) {
    		// Move right. Max is found in most furthest right node. BST rules
    		currNode = currNode.right;
    	}
    	
		return currNode.key;
    	
    } 

    /*
     * Method:
     * 	next
     * 
     * Purpose:
     * 	Finds the successor (aka the next greater key 
     * (aka the smallest key greater than the parameter key))
     * 
     * Pre Condition:
     * 	Tree is initialized
     * 
     * Post Condition:
     * 	Tree is unchanged
     * 
     * Return:
     *  Returns the next greater key in the tree if it exists, 
     *  or null otherwise.
     */
    public Integer next (int k) {
    	Node currNode = root;
    	Node successor = null;
    	
    	while (currNode != nil) {
    		if (k < currNode.key) {
    			// Potential Successor found
    			successor = currNode;
    			currNode = currNode.left;
    		} else {
    			currNode = currNode.right;
    		}
    	}
    	
    	// If successor exists, key is returned. Otherwise, null
    	return (successor != null) ? successor.key : null;
    	
    } 


    // prev -- Find the next smaller key
    //
    //
    //

    /*
     * Method:
     * 	prev
     * 
     * Purpose:
     * 	Finds the predecessor (aka next smaller key 
     * (aka largest key smaller than parameter key))
     * 
     * Pre Condition:
     * 	Tree initialized
     * 
     * Post Condition:
     * 	No change
     * 
     * Return:
     * 	Returns the next smaller key in the tree if it exists, 
     * or null otherwise.
     */
    public Integer prev (int k) {
    	Node currNode = root;
    	Node predecessor = null;
    	
    	while (currNode != nil) {
    		if (k > currNode.key) {
    			// Potential predecessor found
    			predecessor = currNode;
    			currNode = currNode.right;
    		} else {
    			currNode = currNode.left;
    		}
    	}
    	
    	// Returns the next smaller key in the tree if it exists, 
    	//or null otherwise.
		return (predecessor != null) ? predecessor.key : null;
    	
    }


    // insert -- Insert a key-item pair into a tree
    //
    // If the key is already in the tree, its associated item is updated.
    //
    public void insert (int k, Object x)
    {
	root = insert(root, k, x);
	root.color = BLACK;
    }

    /*
     * Method:
     * 	insert
     * Purpose:
     * 	Inserts a key-item pair into the tree / update existing key's item
     * 
     * Pre Condition:
     * 	Tree is initialized
     * 
     * Post Condition:
     *	Tree possibly changes to maintain red-back property rules
     * 
     * Return:
     * 	Returns updated subtree rooted at node v
     */
    private static Node insert (Node v, int k, Object x) {
    	// New red node is inserted
    	if (v == nil) {
    		return Node.allocate(k, x, nil, nil, RED);
    	}
    	// Inserted into left sub tree
    	if (k < v.key) {
    		v.left = insert(v.left, k, x);
    	
    	// Inserted into right sub tree
    	} else if (k > v.key) {
    		v.right = insert(v.right, k, x);
    		
    	// This means key exists. Item at key is updated
    	} else {
    		v.item = x;
    	}
    	// Restored red-black properties. Returns new subtree. 
		return insertRestore(v, k);
    	
    } 
    
    // This method recursively inserts the key-item pair (k, x) into the
	// subtree rooted at node v, and returns the new subtree rooted at v

    private static Node insertRestore (Node v, int k)
	// Restore the color invariants at node v after an insertion of key k
    {
	if (v != nil && v.isBlack())
	    if (k < v.key)
	    {
		Node w = v.left;
		if (w.isRed())
		    if (v.right.isRed())
		    {
			if ((k < w.key && w.left.isRed())
			    || (k > w.key && w.right.isRed())) // Cases 3 and 4
			    flipBoth(v);
		    }
		    else if (k < w.key && w.left.isRed()) // Case 5
		    {
			flipLeft(v);
			v = rotateRight(v);
		    }
		    else if (k > w.key && w.right.isRed()) // Case 6
		    {
			v.left = rotateLeft(v.left);
			flipLeft(v);
			v = rotateRight(v);
		    }
	    }
	    else
	    {
		Node w = v.right;
		if (w.isRed())
		    if (v.left.isRed())
		    {
			if ((k > w.key && w.right.isRed())
			    || (k < w.key && w.left.isRed())) // Cases 7 and 8
			    flipBoth(v);
		    }
		    else if (k > w.key && w.right.isRed()) // Case 9
		    {
			flipRight(v);
			v = rotateLeft(v);
		    }
		    else if (k < w.key && w.left.isRed()) // Case 10
		    {
			v.right = rotateRight(v.right);
			flipRight(v);
			v = rotateLeft(v);
		    }
	    }
	return v;
    }

    private static boolean defect; // true iff the tree needs an extra black
        // field "defect" is only used by method "delete"
    
    // delete -- Delete a key-item pair from a tree
    //
    // If the key is not in the tree, nothing is changed.
    //
    public void delete (int k)
    {
	defect = false;
	root = delete(root, k);
	root.color = BLACK;
    }
    private static Node delete (Node v, int k) // Helper function
    {
	if (v == nil)
	    return nil;
	else if (k < v.key)
	{
	    v.left = delete(v.left, k);
	    return deleteRestore(v, v.left);
	}
	else if (k > v.key)
	{
	    v.right = delete(v.right, k);
	    return deleteRestore(v, v.right);
	}
	else if (v.left == nil)
	    return deleteBasis(v, v.right);
	else if (v.right == nil)
	    return deleteBasis(v, v.left);
	else
	{
	    v.right = deleteGeneral(v.right, v);
	    return deleteRestore(v, v.right);
	}
    }
    private static Node deleteGeneral (Node w, Node p)
	// General case for deleting a node p with two children
    {
	if (w.left != nil) // Find the minimum node in the subtree rooted at w
	{
	    w.left = deleteGeneral(w.left, p);
	    return deleteRestore(w, w.left);
	}
	else // Copy the minimum node's contents into the ancestral node p
	{
	    p.key = w.key;
	    p.item = w.item;
	    return deleteBasis(w, w.right);
	}
    }
    private static Node deleteBasis (Node v, Node w)
	// Base case of splicing out a node v with at most one child w
    {
	if (v.isRed())
	    defect = false;
	else if (w.isBlack())
	    defect = true;
	else
	{
	    flip(w);
	    defect = false;
	}
	v.free();
	return w;
    }
    private static Node deleteRestore (Node v, Node w)
	// Restore the color invariants at a node w after a deletion
    {
	if (defect)
	{
	    if (w.isRed()) // Case 0
	    {
		flip(w);
		defect = false;
	    }
	    else if (w == v.left)
	    {
		Node u = v.right;
		if (u.isRed()) // Case 1
		{
		    flipRight(v);
		    v = rotateLeft(v);
		    v.left = deleteRestore(v.left, v.left.left);
		        // This call will encounter Cases 2-4 at v.left
		    if (defect) // Case 2 occurred at v.left
		    {
			flip(v.left);
			defect = false;
		    }
		}
		else if (u.left.isBlack() && u.right.isBlack()) // Case 2
		    flip(u);
		else // Cases 3 and 4
		{
		    if (u.left.isRed()) // Case 3
		    {
			v.right = rotateRight(v.right);
			flipRight(v.right);
		    }
		    v = rotateLeft(v);
		    flipLeft(v);
		    flip(v.right);
		    defect = false;
		}
	    }
	    else
	    {
		Node u = v.left;
		if (u.isRed()) // Case 5
		{
		    flipLeft(v);
		    v = rotateRight(v);
		    v.right = deleteRestore(v.right, v.right.right);
		        // This call will encounter Cases 6-8 at v.right
		    if (defect) // Case 6 occurred at v.right
		    {
			flip(v.right);
			defect = false;
		    }
		}
		else if (u.right.isBlack() && u.left.isBlack()) // Case 6
		    flip(u);
		else // Cases 7 and 8
		{
		    if (u.right.isRed()) // Case 7
		    {
			v.left = rotateLeft(v.left);
			flipLeft(v.left);
		    }
		    v = rotateRight(v);
		    flipRight(v);
		    flip(v.left);
		    defect = false;
		}
	    }
	}
	return v;
    }
   
    // empty -- Returns true iff the tree is empty
    //
    public boolean empty ()
    {
	return root == nil;
    }

    // size -- Returns the number of nodes in the tree
    //
    public int size ()
    {
	return size(root);
    }
    private static int size (Node v) // Helper function
    {
	return (v != nil) ? 1 + size(v.left) + size(v.right) : 0;
    }

    // clear -- Frees all nodes in the tree
    //
    public void clear ()
    {
	clear(root);
	root = nil;
    }
    private static void clear (Node v) // Helper function
    {
	if (v != nil)
	{
	    clear(v.left);
	    clear(v.right);
	    v.free();
	}
    }

    // copy -- Returns a shallow copy of the tree
    //
    public Tree copy ()
    {
	Tree t = new Tree();
	t.root = copy(root);
	return t;
    }
    private static Node copy (Node v) // Helper function
    {
       return (v != nil)
	   ? Node.allocate(v.key, v.item, copy(v.left), copy(v.right), v.color)
	   : nil;
    }

    // Scaffolding
    //
    private static void flip (Node v) // Flip the color at a node
    {
	v.color = v.isBlack() ? RED : BLACK;
    }
    private static void flipLeft (Node v) // Exchange colors with left child
    {
	boolean c = v.color;
	v.color = v.left.color;
	v.left.color = c;
    }
    private static void flipRight (Node v) // Exchange colors with right child
    {
	boolean c = v.color;
	v.color = v.right.color;
	v.right.color = c;
    }
    private static void flipBoth (Node v) // Exchange colors with both children
    {
	boolean c = v.color;
	v.color = v.left.color;
	v.left.color = c;
	v.right.color = c;
    }
    private static Node rotateRight (Node v) // Right rotation
    {
	Node w = v.left;
	v.left = w.right;
	w.right = v;
	return w;
    }
    private static Node rotateLeft (Node v) // Left rotation
    {
	Node w = v.right;
	v.right = w.left;
	w.left = v;
	return w;
    }
    private boolean valid () // Is this a valid red-black tree?
    {
	// Count the number of black nodes on the left chain
	//
	int b = 0;
	for (Node v = root; v != nil; v = v.left)
	    b += (v.isBlack() ? 1 : 0);
	
	return root.isBlack() && valid(root, 0, b);
    }
    private static boolean valid (Node v, int i, int b) // Helper function
    {
	if (v != nil)
	    return (v.isBlack()
		    || (v.isRed() && v.left.isBlack() && v.right.isBlack()))
		&& valid(v.left, i + (v.isBlack() ? 1 : 0), b)
		&& valid(v.right, i + (v.isBlack() ? 1 : 0), b);
	else
	    return i == b;
    }
    private void dump () // Prints out a red-black tree in preorder
    {
        dump(root, 0);
    }
    private static void dump (Node v, int d) // Helper function
    {
        if (v != nil)
	{
	    for (int i = 1; i <= d; i++)
		System.out.print(" ");
	    System.out.print(v.key + " " + v.item);
	    System.out.println(v.isBlack() ? " black" : " red");
	    dump(v.left, d+1);
	    dump(v.right, d+1);
	}
    }
}
