'''
    word_grid.py
    Eric Romero
    Purpose: 
    This file should take two user inputs(Grid size and Seed value),
    Of which a randomly generated grid will be composed.
    Comprised of 4 functions(including main) and importing the random
    module, the process derives of 
    1. initializing the inputs
    2. Randomly generating a grid
    3. Printing out the grid
    4. Main, which is in charge of calling all previous functions
    CSc 120 002 Fall 2023

'''

import random

def init():
    '''
        No Parameters

        1. Reads in input for Grid Size
        2. Reads in input for Seed Value
        3. Initializes the seed with the random module
        
        Return:
            grid_size: integer representing the size of the grid
    '''
    grid_size = int(input())
    seed_value = input()
    random.seed(seed_value)

    return grid_size 

def make_grid(grid_size):
    '''
        Parameters:
            grid_size: integer representing the size of the grid

        A grid the size of grid_size x grid_size is created

        1. Each row represents list of length grid_size
        2. Grid contains list of length of grid_size

        Returns: 
            grid: Two-Dimensional list containing 1 character 
            long strings as elements.
    '''
    grid = []

    for a in range(grid_size):
        row = []
        for b in range(grid_size):
            rand_let = chr(random.randrange(97, 123))
            row.append(rand_let)
        grid.append(row)
    return grid

def print_grid(grid):
    '''
        Parameters:
            grid: two dimensional list containing strings as elements

        Prints out lists of grid, ouputting randomly generated grid.
    '''
    for row in grid:
        print(','.join(row))

def main():
    '''
        init(): Initializes user inputs for grid size and seed number
            Returns grid_size integer to main variable grid_size
        make_grid(grid_size): Dependant on the variable as a parameter,
        a randomly generated grid is constructed.
        For loops, use of ascii character functions, and random module 
        functions are used.
            Returns a two dimensional list containing strings as elements
            within a variable named "grid"
        print_grid(grid): On terminal, every "row"/list of the two 
        dimensional grid is printed out.
    '''
    grid_size = init()
    grid = make_grid(grid_size)
    print_grid(grid)

main()
