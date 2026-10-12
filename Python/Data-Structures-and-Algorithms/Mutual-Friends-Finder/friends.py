'''
    friends.py
    Eric Romero
    Purpose:
    This program will read in a text file to 
        organize names into a Linked List, 
        organizing their friends, and 
        given user input for two names, 
            will retrieve the common friend between the two.
    CSc 120, Section 001, TA: Marrissa
'''

from linked_list import *

def main():
    file = input('Input file: ')
    open_file = open(file).readlines()

    my_ll = LinkedList()

    for pair in open_file:
        names = pair.strip('\n').split()
        my_ll.add_name(names[0])
        my_ll.add_name(names[1])
        my_ll.add_friend(names[0], names[1])
        my_ll.add_friend(names[1], names[0])
    
    name1 = input('Name 1: ').strip()
    name2 = input('Name 2: ').strip()

    friends_1 = my_ll.get_friends(name1)
    friends_2 = my_ll.get_friends(name2)

    # Checkpoint: Name 1 or Name 2 cannot be found within the list of names
    if friends_1 == None or friends_2 == None:
        if friends_1 == None:
            print('ERROR: Unknown person ' + name1)
        else:
            print('ERROR: Unknown person ' + name2)
    
    else:
        common = my_ll.common_friends(friends_1, friends_2)
        # Checkpoint: There are no common friends, thus an empty Linked List
        if common._head == None:
            return 
        else:
            sorted = common.sort()
            print('Friends in common:')
            sorted.print_friends()

main()