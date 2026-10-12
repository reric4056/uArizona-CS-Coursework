'''
    Eric Romero
    fake_news.py
    This program will read in a CSV file, retrieve the titles of fake news
    article, and keep track of words and their occurrences using 
    Linked Lists
'''
import string
import csv

class Node:
    '''
    This class represents Nodes for words and their occurrences. 

    The object created has attributes for word(string of the word from 
    title), count(number of occurrences), and next(a reference to the 
    next element within the list)

    This class uses simple getter and setter methods, it should be 
    clear what is to happen. 

    '''
    def __init__(self, word):
        self._word = word
        self._count = 1
        self._next = None

    def word(self):
        return self._word
    
    def count(self):
        return self._count
    
    def next(self):
        return self._next
    
    def set_next(self, target):
        '''
        Parameters:
            target: Node element
        Sets the _next attribute to target
        '''
        self._next = target

    def incr(self):
        self._count += 1

    def __str__(self):
        return '{} : {}'.format(str(self._word), str(self._count))
    
class LinkedList:
    '''
    This class represents a Linked List, a collection of Node class object

    The object created intiliaez a head attribute, originally at None. 

    This class defines methods to 
        check if the LL is empty, 
        update count for a word, 
        remove a node from head, 
        adding a node to the head, 
        inserting a node, 
        sorting a Linkedlist, 
        get the count from a specific position, 
        and printing all the words that have a count of at least a 
        specific number

    This class also defines a string special method. 
    '''
    def __init__(self):
        self._head = None

    def is_empty(self):
        return self._head == None
        
    def head(self):
        return self._head
    
    def update_count(self, word):
        '''
        Parameter:
            word: A string representing a word from a title
        Purpose:
            If the word is present in the list, the count will be 
            incremented.
            Else, it will create a node for the word with a count 
            starting at 1
        '''
        curr_elem = self._head
        while curr_elem != None:
            if curr_elem._word == word:
                curr_elem.incr()
                return
            curr_elem = curr_elem._next
        self.add(Node(word))

    def rm_from_hd(self):
        '''
        Purpose: 
            The first node in the Linked list is removed from the 
            beginning of the list. This way, the head attribute 
            refers to the following node.
        Returns:
            _node: The node removed from the head
        '''
        _node = self._head
        self._head = _node._next
        _node._next = None
        return _node
    
    def insert_after(self, node1, node2):
        '''
        Parameters:
            node1, node2: references to nodes within the linked list
        Purpose:
            Node 2 will be inserted into a Linked List after node 1
        '''
        node2._next = node1._next
        node1._next = node2

    def add(self, node):
        '''
        Purpose:
            node: a reference to a node within the linked list
        Purpose:
            Adds a node to the head of the Linked List
        '''
        node._next = self._head
        self._head = node

    def sort(self):
        '''
        Purpose: 
            Sorts a linked list by descending order by the _count 
            attribute

        Return:
            sorted: A new Linked List containing the nodes from the 
            original Linked List
        '''
        sorted = LinkedList()
        while self._head != None:
            curr_element = self.rm_from_hd()
            # Check 1: Sorted is empty, add as head
            if sorted._head == None:
                sorted.add(curr_element)
            
            # Check 2: Unordered element is greater than sorted head
            elif sorted._head._count <= curr_element._count:
                sorted.add(curr_element)

            # Check 3: Iterates through sorted, and finds the spot where 
            # the current value is greater than the current value in sorted.
            else:
                E = sorted._head
                prev = None
                while E != None:
                    if E._count <= curr_element._count:
                        break
                    prev = E
                    E = E._next
                prev._next = curr_element
                curr_element._next = E
        return sorted
    
    def get_nth_highest_count(self, n):
        '''
        Parameters:
            n: integer representing the wanted position of an element

        Purpose:
            Find and return the count associated with the node within 
            the linked list at the position of n. 
        '''
        curr_elem = self._head
        pos = 0
        while curr_elem != None:
            if pos == n:
                return curr_elem.count()
            curr_elem = curr_elem._next
            pos += 1
    
    def print_upto_count(self, n):
        '''
        Parameter:
            n: integer representing a count from a node

        Purpose:
            Eveyr every with the count of at least n will be printed out
        '''
        curr_elem = self._head
        while curr_elem != None:
            if curr_elem.count() >= n:
                print(curr_elem)
            curr_elem = curr_elem._next

    def __str__(self):
        current = self._head
        while current != None:
            word = current.word()
            count = current.count()
            print('{} : {}'.format(str(word), str(count)))
            current = current._next

def main():
    file = input()
    openfile = open(file)

    my_ll = LinkedList()

    csvreader = csv.reader(openfile)

    # This process will retrieve a title per line, split into a list, 
    # and depending whether the word is of greater length than 2, 
    # a Node class object within the Linked List is initlialized and 
    # updatedgiven the word.
    for line in csvreader:
        title = line[4]
        if '#' not in line[0]:
            for char in title:
                if char in string.punctuation:
                    title = title.replace(char, ' ')
            clean_words = title.split()
            for word in clean_words:
                if len(word) >= 3:
                    my_ll.update_count(word.lower())
    sorted = my_ll.sort()
    n = input()
    k = sorted.get_nth_highest_count(int(n))
    sorted.print_upto_count(k)
    openfile.close()
   
main()