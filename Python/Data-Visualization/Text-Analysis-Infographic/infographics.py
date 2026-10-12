### Eric Romero
### CSc 110
### Benjamin Dicken
### Description: This program reads a text file. It then identifies how many times a word is repeated.
###              It identifies the count of small, medium, and large words. It can also identify the count of
###              capitalized and non-capitalized words, as well as punctuated and non-punctuated words.
###              It should be displayed on a graph, but I will admit I had some trouble with this.



from graphics import graphics

def main():
    '''
        Takes user input of a real text file in the same directory. It then calls on the word_counting function as well
        as the count_check function.
    :return:
    '''
    file_name = input('Enter the name of the file:\n')
    gui = graphics(660, 720, 'Infographic')
    gui.rectangle(0, 0, 660, 720, 'grey')
    word_count = word_counting(file_name)
    count_check(gui, word_count, file_name)


def word_counting(file_name):
    '''
    This function reads the called file and stores each line in a list. It then takes the list, identifies words into
    a dictionary.
    :param file_name: String
    :return: Dictionary
    '''
    file = open(file_name, 'r')
    lines = file.readlines()
    word_list = []
    word_count_dictionary = {}
    for line in lines:
        cut_word = line.strip('\n').split()
        word_list = word_list + cut_word
    for word in word_list:
        if word not in word_count_dictionary:
            word_count_dictionary[word] = 0
        if word in word_list:
            word_count_dictionary[word] += 1
    file.close()
    return word_count_dictionary

def count_check(gui, word_count, file_name):
    '''
    This function uses information from the word_count_dictionary and stores them in their respective identities.
    It then calls the graph and text functions that will create the bar graphs for this program.
    :param gui: Function
    :param word_count: Dictionary
    :param file_name: String
    :return:
    '''
    small = 0
    medium = 0
    large = 0
    small_max = 0
    medium_max = 0
    large_max = 0
    small_word = ''
    medium_word = ''
    large_word = ''

    cap_count = 0
    #Punctaution list
    punctuation = ['!','.', ',', '?']
    punct = 0

    for key in word_count:
        #Checks for small words
        if len(key) >= 0 and len(key) <= 4:
            small += 1
            if word_count[key] > small_max:
                small_max = word_count[key]
                small_word = key
        #Checks for medium words
        elif len(key) >= 5 and len(key) <= 7:
            medium += 1
            if word_count[key] > medium_max:
                medium_max = word_count[key]
                medium_word = key
        #Checks for large words
        elif len(key) >= 8:
            large += 1
            if word_count[key] > large_max:
                large_max = word_count[key]
                large_word = key
        #Checks for capitalization
        if key[0].isupper():
            cap_count += 1
        #Checks for punctuation
        if key[len(key) - 1] in punctuation:
            punct += 1


    unique_words = len(word_count)
    non_punct = unique_words - punct
    non_cap = unique_words - cap_count

    graph(gui, unique_words, small, medium, large, cap_count, non_cap, punct, non_punct)
    text(gui, file_name, small_word, medium_word, large_word, small_max, medium_max, large_max, unique_words)


def graph(gui, unique_words, small, medium, large, cap_count, non_cap, punct, non_punct):
    '''
    This function is supposed to create the bar graphs for the called file. It also creates labels for each section
    :param gui: Function
    :param unique_words: Integer
    :param small: Integer
    :param medium: Integer
    :param large: Integer
    :param cap_count: Integer
    :param non_cap: Integer
    :param punct: Integer
    :param non_punct: Integer
    :return:
    '''
    medium_y_value = 210 + ((450 / unique_words) * small)
    large_y_value = 210 + ((450 / unique_words) * (small + medium))
    non_cap_y_value = 210 + ((450 / unique_words) * cap_count)
    non_punct_y_value = 210 + ((450 / unique_words) * punct)
    # Words recording Section
    gui.rectangle(30, 210, 180, ((450/unique_words)*small), 'blue')
    gui.text(40, 218, 'small words', 'white', 10)

    gui.rectangle(30, medium_y_value, 180, ((450/unique_words)*medium), 'green')
    gui.text(40, 3 + medium_y_value, 'medium words', 'white', 10)

    gui.rectangle(30, large_y_value, 180, ((450/unique_words)*large), 'blue')
    gui.text(40, 3 + large_y_value, 'large words', 'white', 10)
    # Capitalization Section
    gui.rectangle(240, 210, 180, ((450/unique_words)*cap_count), 'blue')
    gui.text(40, 218, 'Capitalized', 'white', 10)

    gui.rectangle(240, non_cap_y_value, 180, ((450/unique_words)*non_cap), 'green')
    gui.text(240, 3 + non_cap_y_value, 'Non Capitalized', 'white', 10)
    # Punctuation Section
    gui.rectangle(450, 210, 180, ((450/unique_words)*punct), 'blue')
    gui.text(450, 218, 'Punctuated', 'white', 10)

    gui.rectangle(450, non_punct_y_value, 180, ((450/unique_words)*non_punct), 'green')
    gui.text(450, 3 + non_punct_y_value, 'Non Punctuated', 'white', 10)


def text(gui, file_name, small_word, medium_word, large_word, small_max, medium_max, large_max, unique_words):
    '''
    This function creates the headings/subheadings for the bar graphs.
    :param gui: Function
    :param file_name: String
    :param small_word: String
    :param medium_word: String
    :param large_word: String
    :param small_max: Integer
    :param medium_max: Integer
    :param large_max: Integer
    :param unique_words: Integer
    :return:
    '''
    results = str(small_word) + ' (' + str(small_max) + 'x) ' \
            + str(medium_word) + ' (' + str(medium_max) + 'x) ' \
            + str(large_word) + ' (' + str(large_max) + 'x) '
    gui.text(30, 30, file_name, 'cyan', 25)
    gui.text(30, 90, 'Total unique words: ' + str(unique_words), 'white', 20)
    gui.text(30, 150, 'Most used words (s/m/l): ', 'white', 15)
    gui.text(240, 150, results, 'cyan', 15)
    gui.text(30, 180, 'Word lengths', 'white', 20)
    gui.text(240, 180, 'Cap/Non-Cap', 'white', 20)
    gui.text(450, 180, 'Punct/Non-Punct', 'white', 20)


main()