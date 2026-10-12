'''
    writer_bot_ht.py
    Eric Romero
    Purpose: Takes an input text and constructs a Markov chain model to be 
        used to generate new text based on the original text 
        and a hashing algorithm.
    CSc 120, Section 001, TA Marissa
'''
import random
import sys
SEED = 8
random.seed(SEED)

class Hashtable:
    '''
    This class represents a Hashtable implementation for key-value pairs. 

    The constructor creates an empty Hashtable with a specified size.

    This class provides methods for 
        inserting key-value pairs
        retrieving values based on keys
        checking if a key is present
    
    This Hashtable implementation uses linear probing for collision resolution
    '''
    def __init__(self, size):
        '''
        Initialize an empty Hashtable with the specified size

        Parameter:
            size (int): The size of the Hashtable
        '''
        self._pairs = [None] * size
        self._size = size

    def _hash(self, key):
        '''
        Source: Assignment 11 Hash Function
        '''
        p = 0
        for c in key:
            p = 31 * p + ord(c)
        return p % self._size

    def put(self, key, value):
        '''
        Insert a key-value pair into the Hashtable.

        Parameter:
            key (hashable): The key for the pair
            value: Value associated with the key
        '''
        index = self._hash(key)

        # Handles collisions using linear probing
        while self._pairs[index] != None and self._pairs[index][0] != key:
            index = (index - 1) % self._size
        
        if self._pairs[index] == None:
            # If slot is empty, insert the new key-value pair
            self._pairs[index] = [key, [value]]

        else:
            # If the key already exists, value is appended to existing list
            self._pairs[index][1] = self._pairs[index][1] + [value]

    def get(self, key):
        '''
        Retrieves the values associated with a key

        Parameters:
            key (hashable): The key to retrieve values for

        Returns:
            value: Value associated with key
            None: If key is not present
        '''
        index = self._hash(key)

        # Searches for key in the hashtable
        while self._pairs[index] != None:
            if self._pairs[index][0] == key:
                return self._pairs[index][1]
            index = (index - 1) % self._size

        return None
    
    def __contains__(self, key):
        '''
        Checks if a key is present in the Hashtable

        Parameter:
            key (hashable): The key to check for presence

        Returns:
            boolean: True if key is present, False otherwise
        '''
        index = self._hash(key)

        # Searches for key in the hashtable
        while self._pairs[index] != None:
            if self._pairs[index][0] == key:
                return True
            index -= 1
            if index < 0:
                index = len(self._pairs) - 1

        return False
    
    def __str__(self):
        pairs_str = ', '.join([f'({pair[0]}, {pair[1]})' for pair in \
                               self._pairs if pair is not None])
        return f'Hashtable([{pairs_str}])'
    
    
def user_inputs():
    '''
    Prompts the use for input values required for the text generation

    Returns:
        Tuple:
            list of strings: Lines of text from the input file
            M (int): The size of the hashtable
            n (int): Prefix size for Markov Chain
            num_words (int): Number of words to generate
    '''
    sfile = input()
    open_file = open(sfile, 'r')
    M = input()
    M = int(M)
    n = input()
    n = int(n)
    if n < 1:
        print('ERROR: specified prefix size is less than one')
        sys.exit(0)

    num_words = input()
    num_words = int(num_words)
    if num_words < 1:
        print('ERROR: specified size of the generated text is less than one')
        sys.exit(0)

    return open_file.readlines(), M, n, num_words

def build_markov_chain(sfile, M, n):
    '''
    Builds a markov chain from the input text.

    Parameters:
        sfile (list of str): List of lines from the input file
        M (int): Size of the hashtable
        n (int): Prefix size for the Markov chain

    Returns:
        markov_chain (Hashtable): The constructed Markov Chain hashtable
    '''
    markov_chain = Hashtable(M)
    text = (' '.join(sfile)).split()
    NONWORD = '@'
    prefix_holder = [NONWORD] * n
    prefix_holder += text

    prefix_non = ' '.join(prefix_holder[:n])

    # Builds the Markov Chain by iterating through the input text
    for idx in range(len(prefix_holder) - n):
        prefix = ' '.join(prefix_holder[idx : idx + n])
        suffx = prefix_holder[idx + n]
        markov_chain.put(prefix, suffx)
    markov_chain.put(prefix_non, prefix_holder[n])
    return markov_chain

def generate_text(sfile, markov_chain, num_words, n):
    '''
    Generates text using the Markov chain

    Parameter:
        sfile (list of str): List of lines from input file
        markov_chain (Hashtable): The markov chain hashtable
        num_words (int): Number of words to generate
        n (int): Prefix size for the markov chain

    Returns:
        output (list of str): The generated text as a list of words
    '''
    text = (' '.join(sfile)).split()

    initial_words = text[:n]

    words = initial_words[:]

    output_count = len(initial_words)
    output = initial_words

    # Genertaes text based on Markov Chain
    while output_count < num_words:
        prefix = ' '.join(words[-n:])
        suffix = markov_chain.get(prefix)
        if not suffix:
            break
        if len(suffix) > 1:
            idx = random.randint(0, len(suffix) - 1)
            word = str(suffix[idx])
        else:
            word = suffix[0]

        words.append(word)
        output_count += 1
        output.append(word)
    return output

def print_output(output_text):
    '''
    Print the genertaed text in formatted output

    Parameters:
        output_text (list of str): Generated text as a list of words
    '''
    for i in range(0, len(output_text), 10):
        line = output_text[i : i + 10]
        print(' '.join(line))

def main():
    sfile, M, n, num_words = user_inputs()
    markov_chain = build_markov_chain(sfile, M, n)
    output = generate_text(sfile, markov_chain, num_words, n)
    print_output(output)

main()
