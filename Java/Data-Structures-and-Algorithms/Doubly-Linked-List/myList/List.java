/*
 *  Version 1
 *  Author (for required methods): Eric Romero
 *  Spring 2025
 *  CSC 345
 *  Purpose:
 *  	This file implements a doubly linked list with a custom memory pool for node reuse.
 *  The list supports stack, queue, and deque operations through the 'Handle' class.
 *  The implemented methods support advanced list manipulation such as copying, concatenation,
 *  splitting, reversing, and clearing.
 */

package myList;

public class List
{
    private static Node pool = null; // Pool of free nodes

    private static class Node // List node
    {
	int  item;
	Node next;
	Node prev;

        final static int BLOCK_SIZE = 32; // Number of nodes in a block

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
		    block[i] = new Node(); // (Darn you, Java!)

		// Link the nodes in the block into a pool
		//
		pool = block[0];
		for (int i = 0; i < BLOCK_SIZE - 1; i++)
		    block[i].next = block[i + 1];
		block[BLOCK_SIZE - 1].next = null;
	    }

            // Take a node off the pool
            //
	    Node v = pool;
	    pool = v.next;

	    return v;
	}
	public static Node allocate (int item, Node next, Node prev)
	{
	    Node v = allocate();

	    // Initialize the node fields
            //
	    v.item = item;
	    v.next = next;
	    v.prev = prev;

	    return v;
	}

        // free -- Free a node by putting it into the pool
        //
        public void free ()
	{
	    this.next = pool;
	    pool = this;
	}
    };

    public static class Handle // Safe pointer to a list node
    {
	private Node myNode; // Node pointed at 
	private Node myHead; // Header node of the list containing myNode

        // Handle constructors
        //
        public Handle (Node myNode, Node myHead)
        {
	    this.myNode = myNode;
	    this.myHead = myHead;
	}
        public Handle (Handle h)
	{
	    this(h.myNode, h.myHead);
	}
        public Handle (List l)
	{
	    this(l.header, l.header);
	}

        // copy -- Copy a handle
        //
        public Handle copy ()
	{
	    return new Handle(this);
	}

	// item -- Return the item in the node pointed at by a handle
	//
	public int item ()
	{
//	    return (myNode != myHead) ? myNode.item : null;
	    return myNode.item;
	}

        // forward -- Move the handle to the next node; returns true iff
	//            this does not fall off the list
	//
	public boolean forward ()
	{
	    myNode = myNode.next;
	    return myNode != myHead;
	}

        // backward -- Move the handle to the previous node; returns true iff
	//             this does not fall off the list
	//
	public boolean backward ()
	{
	    myNode = myNode.prev;
	    return myNode != myHead;
	}

        // next -- Return a new handle that points to the next node
	//
	public Handle next ()
	{
	    return new Handle(myNode.next, myHead);
	}

        // prev -- Return a new handle that points to the previous node
	//
	public Handle prev ()
	{
	    return new Handle(myNode.prev, myHead);
	}

	// insertAfter -- Insert an item after the handle
	//
	public Handle insertAfter (int item)
	{
	    Node v = Node.allocate(item, myNode.next, myNode);
	    myNode.next.prev = v;
	    myNode.next = v;
	    return new Handle(v, myHead);
	}

	// insertBefore -- Insert an item before the handle
	//
	public Handle insertBefore (int item)
	{
	    return prev().insertAfter(item);
	}

        // equal -- Are handles equal?
	//
	public boolean equal (Handle h)
	{
	    return myNode == h.myNode && myHead == h.myHead;
	}

        // delete -- Remove from the list the element pointed at by the handle
	//
	// Returns the item stored in the deleted element.
	//
	public int delete ()
	{
	    myNode.next.prev = myNode.prev;
	    myNode.prev.next = myNode.next;
	    int item = myNode.item;
	    myNode.free();
	    return item;
	}

        // eject -- Unlink from the list the node pointed at by the handle
	//
	// This does not free the ejected node.
	//
	public void eject ()
	{
	    myNode.next.prev = myNode.prev;
	    myNode.prev.next = myNode.next;
	}

        // inject -- Link back into the list an ejected node
	//
	// An eject followed by an inject restores the prior state of the list
	// if the series of eject and inject operations are performed in a
	// stack-like manner.
	//
	public void inject ()
	{
	    myNode.next.prev = myNode;
	    myNode.prev.next = myNode;
	}
    }

    Node header; // Header node for the list

    // List constructor
    //
    // Creates an empty list.
    //
    public List ()
    {
	header = Node.allocate();
        header.next = header;
        header.prev = header;
    }

    // handle -- Return a new handle for the list
    //
    // The handle is initialized ready for iteration over the list.
    //
    public Handle handle ()
    {
	return new Handle(this);
    }


    /*
     * copy: Creates a new List with a copy of all items in the current list.
     * Pre-Condition: The list is empty or populated
     * Post-Condition: New list is returned containing items in same order from original list.
     */
    public List copy ()
    { 
    	List list = new List();
    	for (Handle h = this.handle(); h.forward();)
    	{
    		// Append each item from original list to the new list.
    		list.put(h.item());
    	}
    	return list;
    } 

    // push -- Push an element onto the top of a stack
    //
    public Handle push (int item)
    {
	return (new Handle(this)).insertAfter(item);
    }

    // pop -- Pop the element off the top of a stack
    //
    public int pop ()
    {
	return (new Handle(header.next, header)).delete();
    }

    // put -- Put an element onto the rear of a queue
    //
    public Handle put (int item)
    {
	return (new Handle(this)).insertBefore(item);
    }

    // get -- Get the element off the front of a queue
    //
    public int get ()
    {
	return pop();
    }

    /*
     * pull: Removes and returns the item at the rear of the list.
     * Pre-Condition: List must not be empty
     * Post-Condition: The last element is removed and returned.
     */
    public int pull () {
    	// Deletes the last item
		return (new Handle(header.prev, header)).delete();
	} 

    /*
     * top: Retruns the item at the top of the list (stack front)
     * Pre-Condition: List isn't empty
     * Post-Condition: The first item is returned. The list remains unchanged.
     */
    public int top () {
    	// Returns the first element's value
		return header.next.item;
    }

    /*
     * front: Returns the item at the front of the queue.
     * Pre-Condition: List isn't empty
     * Post-Condition: The first item is returned. The list remains the same.
     */
    public int front ()
    { return top(); }

    /*
     * rear: Returns the item at the rear of the list.
     * Pre-Condition: List isn't empty
     * Post-Condition: The last item is returned. List the same.
     */
    public int rear ()
    {
    	// Returns the last element's value
		return header.prev.item; 
	} // implement this

    /*
     * Concatentae: Merges another list into original in O(1) time.
     * Pre-Condition: Both lists are intialized
     * Post-Condition: All elements of 'l' are appended to the original list. 'l' is empty.
     */
    public List concatenate (List l)
    {
    	if (l.empty()) {
    		return this;
    	}
    	
    	if (this.empty()) {
    		// All items from list l linked to empty original list.
    		this.header.next = l.header.next;
    		this.header.prev = l.header.prev;
    		this.header.next.prev = this.header;
    		this.header.prev.next = this.header;
    		
    	} else {
    		// The end of the original list is linked the beginning of list l
    		this.header.prev.next = l.header.next;
    		l.header.next.prev = this.header.prev;
    		
    		// End of list l is linked the original list's header
    		l.header.prev.next = this.header;
    		this.header.prev = l.header.prev;
    	}
    	
    	// List L is reset to be empty.
    	l.header.next = l.header;
    	l.header.prev = l.header;
    	
		return this; 
	} 

    /*
     * splitBefore: Splits the list before the node pointed by Handle H.
     * Pre-Condition: h must not point to header.
     * Post-Condition: A new list is returned containing h and all nodes after. 
     */
    public List splitBefore (Handle h)
    {
    	List list = new List();
    	
    	if (h == null || h.myNode == header) {
    		return list;
    	}
    	
    	// H is attached to the end of a new list.
    	list.header.next = h.myNode;
    	list.header.prev = header.prev;
    	
    	// Original list is updated to exclude handle h and then on.
    	h.myNode.prev.next = header;
    	header.prev.next = list.header;
    	
    	// Pointers are updated 
    	Node temp = h.myNode.prev;
    	h.myNode.prev = list.header;
    	list.header.prev = temp;
    	
    	header.prev = temp;
    	
    	return list;
    	
	} // implement this

    /*
     * splitAfter: Splits the list after the node pointed by handle h
     * Pre-condition: h must not point to the last node.
     * Post-Condition: A new list is returned containing all nodes after h
     */
    public List splitAfter (Handle h)
    {
    	List list = new List();
    	
    	if (h == null || h.myNode.next == header) {
    		return list;
    	}
    	
    	// Starts a new list after handle h
    	list.header.next = h.myNode.next;
    	list.header.prev = header.prev;
    	
    	// New list is rewired
    	h.myNode.next.prev = list.header;
    	header.prev.next = list.header;
    	
    	// Original list is closed.
    	h.myNode.next = header;
    	header.prev = h.myNode;
    	
		return list; 
	} // implement this

    /*
     * Empty: Checks if the list contains no elements.
     * Pre-Condition: List is initialized.
     * Post-Condition: Returns true if list is empty. False if otherwise.
     */
    public boolean empty ()
    {
		return header.next == header; 
	} // implement this



    // size -- Count the number of nodes on the list
    //
    // This is a linear-time operation.
    //
    public long size ()
    {
	long i = 0;
	for (Handle h = new Handle(this); h.forward(); )
	    i++;
	return i;
    }

    /*
     * Reverse: Reverses the order of elements in the list in-place.
     * Pre-Condition: List is initialized.
     * Post-Condition: The order of nodes is reversed.
     */
    public void reverse ()
    { 
    	Node curr = header;
    	do {
    		// Temporarily holds the next node
    		Node temp = curr.next;
    		// Swap next and prev
    		curr.next = curr.prev;
    		curr.prev = temp;
    		// Move to the next node (originally curr.next)
    		curr = temp;
    	} while (curr != header);
    } 

    /*
     * Clear: Empties the list and returns all nodes to the memory pool.
     * Pre-Condition: List is initialized.
     * Post-Condition: List is empty and memory is reclaimed. 
     */
    public void clear ()
    { 
    	Node curr = header.next;
    	
    	// Return each node to the pool.
    	while (curr != header) {
    		Node next = curr.next;
    		curr.free();
    		curr = next;
    	}
    	
    	// Reset list pointers.
    	header.next = header;
    	header.prev = header;
    } 

}
