###
### Eric Romero
### CSc 110
### Benjamin Dicken
### Mix and shuffles lines of a text file which can also be undone(by a separate program)
### Calls a file and uses for loops to encrypt

import random
def main():
    random.seed(125)
    file_name = input('Enter a name of a text file to encrypt:\n')  #User inputs file name they want to call
    file = open(file_name, 'r') #Opens file
    lines = file.readlines()    #Initializes text from file
    line_count = len(lines)
    num_of_steps = line_count * 5   #Step repeater
    list = []
    for x in range(len(lines)):
        list.append(x+1)
    for num in range(num_of_steps):
        #Calls two random numbers used as indexes
        rand_choice_1 = random.randint(0, line_count - 1)
        rand_choice_2 = random.randint(0, line_count - 1)
        if rand_choice_1 != rand_choice_2:
            #Swaps content at previously states indexes
            lines[rand_choice_1], lines[rand_choice_2] = lines[rand_choice_2], lines[rand_choice_1]
        list[rand_choice_1], list[rand_choice_2] = list[rand_choice_2], list[rand_choice_1]
        encrypted_file = open('encrypted.txt', 'w')     #Creates new text file 'encrypted.txt'
        for number in range(len(lines)):
            encrypted_file.write(str(lines[number]))    # Overwrites 'encrypted.txt' with new content from file
        encrypted_file.close()
        encrypted_file_index = open('index.txt', 'w')   #Creates new text file 'index.txt'
        for new_x in list:
            encrypted_file_index.write(str(new_x) + '\n')   #Overwrites 'index.txt' with index information
        encrypted_file_index.close()
main()