'''
    rhymes.py
    Eric Romero
    Purpose:
        Reads and coverts pronunciation dictionary file into dictionary.
        Takes input to find that words rhymes
        Rhymes based on following cases:
            1. Primary stress and subsequent sounds are identical
            2. Sound preceding primary stress unidentical

    CSc 120, Section 001, TA: Marissa
'''
def read_file():
    '''
        Reads in textfile containing words and phonemes.
        Converts into dictionary
        (key:word, value:2d list of pronunciations)
        Asks user for input to seek rhymes

        Returns:
            phoneme_dictionary: 
            Dictionary containing words and its pronunciations
            word: User inputed word to find rhymes for
    '''
    filename = input()
    fname = open(filename)
    phoneme_dictionary = {}

    # Loops through file, converts word into key, 
    # 2d list of pronunciations into values
    for line in fname:
        phoneme_dict = []
        line = line.strip('\n').split()
        phoneme_dict.append(line[1:])

        # Checks cases to see whether to create key/value
        # or append value to value list
        if line[0] in phoneme_dictionary:
            phoneme_dictionary[line[0]].append(line[1:])
        else:
            phoneme_dictionary[line[0]] = phoneme_dict

    word = input().upper()

    return phoneme_dictionary, word

def collect_words(phoneme_dictionary, word):
    '''
        Paramters:
            phoneme_dictionary:
                Dicitonary with word and pronunciations
            word:
                User inputed string
        
        Purpose:
            Creates 5 lists based on 
                1. Primary stress and subsequent sound of user word
                2. Primary stress and subsequent sounds of all words
                3. List of words within the phoneme dictionary
                4. Sound preceding Primary stress of user word
                5. Sound preceding primary stress of all words.


    '''
    if word in phoneme_dictionary.keys():
        rhyme = []
        rhyme_front = []
        word_list = []

        # Locates primary stress position
        # Appends preceding sound to rhyme_front
        # Appends Primary stress and following sounds to rhyme
        for i in phoneme_dictionary[word]:
            for e in range(len(i)):
                if '1' in i[e]:
                    rhyme.append(i[e:])
                    rhyme_front.append(i[e-1])


        other_rhyme = []
        other_front = []

        # Follows same process as previous for user word
        # This is for all words
        # Adds word to word_list to index to. 
        for key, values in phoneme_dictionary.items():
            for i in values:
                for e in range(len(i)):
                    if '1' in i[e]:
                        other_rhyme.append(i[e:])
                        other_front.append(i[e-1])
                        word_list.append(key)

        return rhyme, other_rhyme, word_list, rhyme_front, other_front
    
def output(rhyme, other_rhyme, word_list, rhyme_front, other_front):
    '''
        Parameter:
            rhyme:
                Primary stress and subsequent sound of user word
            other_rhyme:
                Primary stress and subsequent sounds of all words
            word_list:
                List of words within the phoneme dictionary
            rhyme_front:
                Sound preceding Primary stress of user word
            other_front:
                Sound preceding primary stress of all words.

        Purpose:
            Loops through lists for user rhyme and other rhyme
            Considers cases of:
                1. Primary stress and subsequent sounds are identical
                2. Sound preceding primary stress unidentical
    '''

    output_list = []
    for e in range(len(other_rhyme)):
        for i in range(len(rhyme)):
            # Checks if sound preceding primarys stress unidentical
            if rhyme_front[i] != other_front[e]:
                # Checks primary stress and subsequent sounds identical
                if rhyme[i] == other_rhyme[e]:
                    output_list.append(word_list[e])
                    
    # Sorts list of words
    output_list.sort()
    for i in output_list:
        print(i)

def main():
    phoneme_dictionary, word = read_file()
    rhyme, other_rhyme, word_list, word_front, other_front = \
        collect_words(phoneme_dictionary, word)
    output(rhyme, other_rhyme, word_list, word_front, other_front)

main()

