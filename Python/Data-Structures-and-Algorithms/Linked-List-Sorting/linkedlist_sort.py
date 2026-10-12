'''
    linkedlist_sort.py
    Eric Romero
    Purpose: Takes in text file with integers, converts into linked list, 
    and sorts in descending order using methods within class. 
    CSc 120, Section 1, TA: Marissa
'''
class LinkedList:
    def __init__(self):
        self._head = None
    
    # sort the nodes in the list
    def sort(self):
        sorted = LinkedList()
        while self._head != None:
            curr_element = self.remove()
            #Check 1: Sorted is empty, add as head
            if sorted._head == None:
                sorted.add(curr_element)
            
            # Check 2: Unordered element is greater than sorted head
            elif sorted._head._value <= curr_element._value:
                sorted.add(curr_element)

            # Check 3: Iterates through sorted, and finds the spot where 
            # the current value is greater than the current value in sorted.
            else:
                E = sorted._head
                prev = None
                while E != None:
                    if E._value <= curr_element._value:
                        break
                    prev = E
                    E = E._next
                prev._next = curr_element
                curr_element._next = E
        return sorted

    # add a node to the head of the list
    def add(self, node):
        node._next = self._head
        self._head = node
        
    # remove a node from the head of the list and return the node
    def remove(self):
        assert self._head != None
        _node = self._head
        self._head = _node._next
        _node._next = None
        return _node
    
    # insert node2 after node1
    def insert(self, node1, node2):
        assert node1 != None
        node2._next = node1._next
        node1._next = node2
    
    def __str__(self):
        string = 'List[ '
        curr_node = self._head
        while curr_node != None:
            string += str(curr_node)
            curr_node = curr_node.next()
        string += ']'
        return string

class Node:
    def __init__(self, value):
        self._value = value
        self._next = None
    
    def __str__(self):
        return str(self._value) + "; "
    
    def value(self):
        return self._value
    
    def next(self):
        return self._next
    
def main():
    file = input()
    ofile = open(file, 'r')

    line = ofile.readline()
    line = line.strip('\n').split()
    my_ll = LinkedList()
    for num in line:
        my_ll.add(Node(int(num)))
    sorted_list = my_ll.sort()
    print(sorted_list)

main()