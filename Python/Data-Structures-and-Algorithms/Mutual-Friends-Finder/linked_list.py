'''
    linked_list.py
    Eric Romero
    Purpose:
    This program contains classes, along with methods, to create a 
    Linked List data structure containing people and their friends.

    A complement program to friends.py

    CSc 120, Section 001, TA: Marrissa
'''

class Node:
    '''
    This class is a complement to the LinkedList class, 
    representing individual element.

    The object created for this element are attributes for name, 
    given as a string, friends, given as a linked list, 
    and next, given as None.

    This class defines the special method __init__ to initialize 
    the attributes and __str__ to print out the name of the element
    '''
    def __init__(self, name):
        '''
        Parameter:
            name: string of name of person being added to Linked List

        Purpose: 
            This special method initializes the attributes for name, 
            friends, and next.
            
        '''
        self._name = name
        self._friends = LinkedList()
        self._next = None

    def __str__(self):
        return str(self._name)

class LinkedList:
    '''
    This class is a representation of an Object Oriented data structure, 
    a collection of nodes containing names(string) and 
    friends(Linked List containing more names)

    The object created initializes a head attribute, associated to None 
    at first, but will contain node class objects containing names

    There are a variety of methods defined in this class for operations:
        adding a name, 
        adding a friends to that names friends list, 
        retrieving that collection of friends, 
        add/remove a node, 
        sorting, 
        retrieving common friends, and 
        a printing method.
    '''
    def __init__(self):
        '''
        Purpose: Initializes the head of a linked list, originally empty.
        '''
        self._head = None

    def add_name(self, name):
        '''
        Parameter:
            name: name string

        Purpose:
            If the Linked List is empty, the node is added as a head
            Otherwise, it will be added to the end of the list.
        '''
        name_node = Node(name)
        if self._head is None:
            self._head = name_node

        else:
            curr_elem = self._head
            while curr_elem._next is not None:
                curr_elem = curr_elem._next
            curr_elem._next = name_node

    def add_friend(self, name, friend):
        '''
        Parameters: 
            name: name of person within main Linked list
            friend: name of person to be added to a specific 
            node elements linked list

        Purpose:
            Given that name exists within this main Linked List,
                the friend name will be added to the names sub-linked 
                list, dedicated to that names' friends.
        '''
        curr_elem = self._head
        while curr_elem is not None:
            if curr_elem._name == name:
                curr_elem._friends.add_name(friend)
            curr_elem = curr_elem._next

    def get_friends(self, name):
        '''
        Parameters:
            name: string name to retrieve friends for
        
        Purpose:
            Given that the parameter exists within the Linked List,
            The linked list of friends they have will be returned

        Returns:
            If the name exists within the main linked list, a linked list 
            with the collection of its friends is returned
            Otherwise, will return None.

        '''
        curr_elem = self._head
        while curr_elem is not None:
            if curr_elem._name == name:
                return curr_elem._friends
            curr_elem = curr_elem._next
        return None

    def add(self, node):
        '''
        Parameter:
            node: Node class object containing name

        Purpose: 
            Adds node to the head of a Linked List
            Complement method to self.sort()

        Source: 
            linkedlist_sort.py (Assignment 6)
        '''
        node._next = self._head
        self._head = node

    def remove(self):
        '''
        Purpose:
            Removes the node at the head of the list

        Return:
            Node at the head that is being removed
        
        Source:
            linkedlist_sort.py (Assignment 6)
        '''
        assert self._head != None
        _node = self._head
        self._head = _node._next
        _node._next = None
        return _node

    def sort(self):
        '''
        Purpose:
            Organizes a Linked List in 'ascending' order.
            The values of every node are strings, so the result 
            will be an alphabetically sorted linked list

        Returns: 
            sorted: alphabetically sorted linked list

        Source:
            linkedlist_sort.py (Assignment 6)
        '''
        sorted = LinkedList()
        while self._head != None:
            curr_element = self.remove()
            #Check 1: Sorted is empty, add as head
            if sorted._head == None:
                sorted.add(curr_element)
            
            # Check 2: Unordered element is greater than sorted head
            elif sorted._head._name >= curr_element._name:
                sorted.add(curr_element)

            # Check 3: Iterates through sorted, and finds the spot where 
            # the current name is greater than the current name in sorted.
            else:
                E = sorted._head
                prev = None
                while E != None:
                    if E._name >= curr_element._name:
                        break
                    prev = E
                    E = E._next
                prev._next = curr_element
                curr_element._next = E
        return sorted
    
    def common_friends(self, ll_1, ll_2):
        '''
        Parameters: 
            ll_1: Linked List of friends for name 1
            ll_2: Linked List of friends for name 2

        Purpose:
            A Linked List is created to contain the names found within 
            linked lists for name 1 and name 2, respectively

        Returns:
            common: Linked lists containing names found in both Parameters
        '''
        common = LinkedList()

        curr_elem1 = ll_1._head

        while curr_elem1 != None:
            curr_elem2 = ll_2._head
            while curr_elem2 != None:
                if curr_elem1._name == curr_elem2._name:
                    common.add_name(curr_elem1._name)
                curr_elem2 = curr_elem2._next
            curr_elem1 = curr_elem1._next

        return common
    
    def print_friends(self):
        '''
        Purpose: 
            Given a linked list, the name of every node is printed out
            line by line
        '''
        curr_elem = self._head

        while curr_elem is not None:
            name = curr_elem._name
            print(str(name))
            curr_elem = curr_elem._next
    
    def __str__(self):
        string = 'List[ '
        curr_node = self._head
        while curr_node != None:
            string += str(curr_node)
            curr_node = curr_node._next
        string += ']'
        return string