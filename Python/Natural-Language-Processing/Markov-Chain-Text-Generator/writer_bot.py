'''
    writer_bot.py
    Eric Romero 
    Purpose: Given a source text, a Markov chain table is constructed. 
    A randomly generated text based off of it is created 
    and then printed out.
    CSc 120, Section 001, TA Marissa
'''

import random 
SEED = 8
random.seed(SEED)

def enter():
    source_text = input()
    prefix_size = int(input())
    random_size = int(input())
    return source_text, prefix_size, random_size


def readfile(source_text, prefix_size):
    '''
    Reads the source text file, breaks it up into words, and creates 
    a list of words with a specifed prefix size.

    Parameter:
        source_text (str): string representing text file
        prefix_size (int): The size of the prefix 
        (Number of consecutive words)

    Returns:
        word_list (list): List of words extracted from the source text
        reserve (tuple): Initial reserve data, 
        a tuple of 'NONWORD' repeated 'prefix_size' times.
    '''
    word_list = []
    NONWORD = ' '

    # Initializes the list with 'NONWORD repeated 'prefix_size' times
    for i in range(prefix_size):
        word_list.append(NONWORD) 
    reserve = tuple(word_list)

    # Opens the text file and processes each line
    open_file = open(source_text) 
    for line in open_file:
        line = line.split() 
        for word in line:
            word_list.append(word) 

    return word_list, reserve

def store_data(word_list, prefix_size):
    '''
    Creates a dictionary where keys are tuples of 
    consecutive words of length 'prefix_size', 
    and values are lists of words that follow the 
    key in the source text

    Parameter:
        word_list (list): List of words extracted 
        from the source text
        prefix_size (int): The size of the prefix 
        (number of consecutive words).

    Return:
        markov_dict (dict): Dictionary representing 
        the Markov Chain model
    '''
    markov_dict = {}
    prefix = tuple(word_list) 

    # Iterates through the list of words to populate the dictionary
    for i in range(len(word_list)-prefix_size):
        suffix = []
        if prefix[i:prefix_size + i] in markov_dict: 
            markov_dict[prefix[i:prefix_size + i]].append(word_list[\
                i + prefix_size])
            str(prefix[i:prefix_size + i]), word_list[ i + prefix_size]
        else:
            suffix.append(word_list[i + prefix_size])
            markov_dict[prefix[i:prefix_size + i]] = suffix 
    return markov_dict


def output(markov_dict, random_size, reserve_data):
    '''
    Generates a list of words based on the Markov Chain Model,
    using the reserve data and random selection

    Parameter:
        markov_dict (dict): Dictionary representing the Markov Chain Model
        random_size (int): The desired size of the generated text
        reserve_data (tuple): Initial reserve data

    Returns:
        output_list (list): List of generated words
    '''

    output_list = []

    # Generating words until the desired random size is reached
    while len(output_list) < random_size: 
        values = markov_dict[reserve_data] 

        # Randomly selecting a value from the list of possible next words
        if len(values) > 1: 
            position = random.randint(0,len(values)-1)
            value = values[position] 
        else:
            value = values[0]
        output_list.append(value)

        # Updates the reserve data for the next iteration
        reserve_data = reserve_data[1:]+tuple([value]) 
    return output_list


def printout(output_list):
    '''
    Prints the generated text in a formatted way, breaking it into lines 
    of 10 words each

    Parameters:
        output_list (list): List of generated words
        
    '''
    output = []
    beginning = 0
    ending = 10 
    
    # Breaks the output into lines of 10 worfs each
    for times in range(len(output_list)):
        data = []
        data.append(output_list[beginning:ending])
        output.append(data[0])
        beginning += 10
        ending += 10 

        # Breaks the loop if the end of the output list is reached
        if output_list[beginning:ending] == []:
            break 
    
    # Prints each line of the formatted output
    for i in output:
        print(' '.join(i)) 


def main():
    source_text, prefix_size, random_size = enter()
    word_list, reserve_data = readfile(source_text, prefix_size)
    markov_dict = store_data(word_list, prefix_size)
    output_list = output(markov_dict,random_size, reserve_data)
    printout(output_list)

main()