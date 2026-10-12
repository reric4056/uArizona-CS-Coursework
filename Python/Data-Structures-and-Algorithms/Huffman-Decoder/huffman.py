'''
    huffman.py
    Eric Romero
    The purpose of this program is to 
    reconstruct a Binary Search Tree from a preorder and inorder string, 
    and decode a given sequence of values.
    CSc 120, Section 001, TA Marissa
'''
class Tree:
    '''
    Represents a binary search tree node.

    Attributes:
        _value: The value stored in the tree node
        _left: Reference to the left subtree
        _right: Reference to the right subtree

    Methods:
        __init__: Initializes a Tree object with default values
        add_value: Sets the value of the current tree node
        __str__: Returns a string representation of the current tree 
        node value
    '''
    def __init__(self):

        self._value = None
        self._left = None
        self._right = None

    def add_value(self, value):
        self._value = value

    def __str__(self):
        return self._value
    
def build_tree(preorder, inorder, tree):
    '''
    Constructs a Binary Search Tree recursively from preorder and inorder 
    traversals

    Parameter:
        preorder(list): List containing string type values in preorder 
        traversal
        inorder(list): List containing string type values in inorder 
        traversal
        tree(object): Tree class object to store the constructed tree.

    Algorithm:
        1. The first value in preorder is set as the current tree node.
        2. The position of the value in inorder determines the left and 
        right subtrees.
        3. Recursively constructs left subtree with values to the left 
        in inorder.
        4. Recursively constructs right subtree with values to the right 
        in inorder.
    '''
    if len(preorder) > 0:
        # Sets current tree node
        tree.add_value(preorder[0])

        # Determines left and right subtrees based on inorder traversal
        left_in = inorder[:inorder.index(preorder[0])]
        right_in = inorder[inorder.index(preorder[0]) + 1:]

        # Recurively constructs left subtree
        left_pre = preorder[1:1+len(left_in)]
        if len(left_pre) > 0:
            tree._left = Tree()
            build_tree(left_pre, left_in, tree._left)
        
        # Recursively constructs right subtree
        right_pre = preorder[1+len(left_in):]
        if len(right_pre) > 0:
            tree._right = Tree()
            build_tree(right_pre, right_in, tree._right)
    else:
        return
    
def postorder_str(tree):
    '''
    Generates a string of tree values in the postorder traversal

    Parameter:
        tree(object): Tree class object representing the root of the tree

    Returns:
        str: A space-separated string of tree values in postorder 
        traversal
    '''
    string = ''
    # Processes the right subtree before the current node
    if tree._right != None:
        string += postorder_str(tree._right) + ' ' + str(tree._value)

    else:
        # If there is no right subtree, the current value is appended
        string = ' ' + str(tree._value) + string

    # Processes the left subtree after the current node
    if tree._left != None:
        string = postorder_str(tree._left) + string

    return string
            
def decoder(sequence, tree):
    '''
    Decodes a binary sequence using the provided Binary Search Tree

    Parameter:
        sequence(str): Binary sequence to be decoded 
        (consists of '0' and '1')
        tree(object): Tree class object representing the decoding structure

    Returns:
        str: Decoded string based on the binary sequence and the tree.
    '''
    curr_node = tree
    decoded_values = []

    # Iterates through each char in the sequence
    for char in sequence:
        # Move to the left subtree if char is '0' and 
        # left subtree exists
        if char == '0' and curr_node._left is not None:
            curr_node = curr_node._left
        # Move to the right subtree if the char is '1' and 
        # right subtree exists
        elif char == '1' and curr_node._right is not None:
            curr_node = curr_node._right

        # If a leaf node is reached, the corresponding value 
        # is appened to decoded_values
        if curr_node._left is None and curr_node._right is None:
            decoded_values.append(curr_node._value)
            # Resets the current node to the root 
            # for the next iteration
            curr_node = tree

    output = ''.join(decoded_values)  

    return output
    
def main():
    filename = input('Input file: ')
    ofile = open(filename).readlines()

    preorder = ofile[0].split()
    inorder = ofile[1].split()
    sequence = ofile[2]
    tree = Tree()

    build_tree(preorder, inorder, tree)
    print(postorder_str(tree)[1:])
    print(decoder(sequence, tree))
main()

