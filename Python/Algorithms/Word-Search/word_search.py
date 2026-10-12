'''
    word_search.py
    Eric Romero
    Purpose: 
        This program takes two text files containing a grid and word list.
        The purpose of this program is to search the grid for valid words 
        originating from the word list.
    CSc 120 002, Fall 2023
'''

def read_files(word_file, grid_file):
    '''
        Parameters:
            word_file: Text file containing words, one on each line
            grid_file: Text file containing grid of letters separated
            by whitespace

        Purpose:
            Reads file containing collection of words and file containing 
            selection of letters. Converts files into list of words and
            two dimensional list, respectively.

        Returns:
            word_list: Python list containing all words from word_file
            grid_list: Two dimensional Python list containing the string
            letters from the file. Whitespace not included
    '''
    wordF, gridF = open(word_file), open(grid_file)

    word_list = []
    
    # Loops through word file, appends clean word to word_list. 
    for line in wordF:
        stripped_word = line.strip("!,'.;;\n")
        low_word = stripped_word.lower()
        word_list.append(low_word)

    # Loops through grid file, rows converted into lists,
    # Appends to empty list
    grid_list = []
    for line in gridF:
        split_line = line.split()
   
        letter_row = []
        for element in split_line:
            letter_row.append(element.lower())
        grid_list.append(letter_row)
    
    return word_list, grid_list

def occurs_in(substr, line_str, results):
    '''
        Parameters:
            substr: word string originating from word_list
            line_str: string of joined letters
            results: list containing substrings found in line string
        
        Purpose: 
            If word exists within line string, 
            word appended to results list.

            Considers reversed list and checks case above.


        Returns:
            "results" list is affected, which returns to main function.
    '''
    
    if substr in line_str:
        results.append(substr)

    reverse = line_str[::-1]
    if substr in reverse:
        results.append(substr)

def vertical(grid_list, word_list, results):
    '''
        Parameters:
            grid_list: two dimensional list containing letters as elements
            word_list: list containing word strings as elements
            results: list of valid words from word_list found within grid.

        Purpose:
            Loops through grid to create list containing columns.
            Calls in occurs_in function to check whether words from
            word list exists within column list.

        Returns:
            "results" list is affected, which returns to main function.


    '''
    i = 0
    while i < len(grid_list):
        column = []
        j = 0
        while j < len(grid_list):
            column.append(grid_list[j][i])
            j += 1
        columnResult = ''.join(column)

        for word in word_list:
            occurs_in(word, columnResult, results)
        i += 1

def diagonal(grid_list, word_list, direction, grid_size, results):
    '''
        Parameters:
            grid_list: two dimensional list containing letters as elements
            word_list: list containing word strings as elements
            direction: Grid is split in half from top left to bottom right.
            UpRight represents upper right side of grid
            DownLeft represents bottom left side of grid
            grid_size: integer representing size of grid.
            results: list of valid words from word_list found within grid.

        Purpose:
            Loops through grid to create list containing words from slants
            Checks whether words from word list exists within slanted list.

        Returns:
            "results" list is affected, which returns to main function.
    '''
    a = 0

    while a < grid_size:
        if direction == 'UpRight':
            b = 0
            c = a + 1
        if direction == 'DownLeft':
            b = a 
            c = 0

        slant = []

        while b < grid_size and c < grid_size:
            slant.append(grid_list[b][c])
            b += 1
            c += 1
        
        diagonalResult = ''.join(slant)
        for word in word_list:
            if word in diagonalResult:
                results.append(word)
        a += 1
    
def main():
    '''
    MAIN FUNCTION
    '''
    word_file = str(input())
    grid_file = str(input())
    word_list, grid_list = read_files(word_file, grid_file)

    results = []

    # The occurs_function will work as a horizontal check as well
    for word in word_list:
        for row in grid_list:
            new_row = ''.join(row)
            occurs_in(word, new_row, results)
    
    vertical(grid_list, word_list, results)
    diagonal(grid_list, word_list, 'UpRight', len(grid_list), results)
    diagonal(grid_list, word_list, 'DownLeft', len(grid_list), results)

    # alphabetically sorts out resulting list and outputs those
    # of length greater than or equal to 3/
    output = sorted(results)
    for word in output:
        if len(word) >= 3:
            print(word)

main()