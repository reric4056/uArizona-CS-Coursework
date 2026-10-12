'''
    battleship.py
    Eric Romero
    It begins with reading in a placement file containing a selection of 
    ships to be placed on a gameboard.
    Secondly, it reads a second file containing guess positions to determine 
    hits, misses, and sunk ships
'''
import sys

class GridPos:
    '''
    Represents a position on the game board

    Attributes:
        _x (int): The x-coordinate of the position
        _y (int): The y-coordinate of the position
        _ship (Ship): The ship occupying the position, otherwise None
        _guessed (Bool): Flag indicatoing whether position has been guessed 
    '''
    def __init__(self, x, y):
        '''
        Initializes a GridPos object

        Arguments:
            x (int): The x-coordinate
            y (int): The y-coordinate
        '''
        self._x = x
        self._y = y
        self._ship = None
        self._guessed = False

    def space_occupied(self):
        '''
        Checks if the position is occupied by a ship

        Returns:
            bool: True if the position is occupied, False otherwise
        '''
        return self._ship != None

    def __str__(self):
        return f"({self._x}, {self._y})"

class Ship:
    '''
    Represents a ship in the game.

    Attributes:
        _abbreviation (str): The abbreviation representing the ship
        _size (int): The size of the ship
        _positions (list): List of GridPos objects occupied by the ship
        _health (int): the health of the ship
    '''
    def __init__(self, board, abbreviation, size, start, end, line):
        '''
        Initializes a Ship object

        Arguments:
            board (Board): The game board
            abbreviation (str): The abbreviation representing the ship
            size (int): The size of the ship
            start (GridPos): The starting positions of the ship
            end (GridPos): The ending position of the ship
            line (str): The line representing ship configuration

        Will exit the program if ship orientation, size, or overlapping 
        issues occur
            
        '''
        self._abbreviation = abbreviation
        self._size = size
        self._positions = []
        self._health = size

        # Error: not Horizontal or Vertical
        if start._x != end._x and start._y != end._y:
            print("ERROR: ship not horizontal or vertical: " + line)
            sys.exit(0)

        # Error: Incorrect ship size
        if start._x == end._x:
            if (max(start._y, end._y) - min(start._y, end._y)) + 1 != size:
                print("ERROR: incorrect ship size: " + line)
                sys.exit(0)
        elif start._y == end._y:
            if (max(start._x, end._x) - min(start._x, end._x)) + 1 != size:
                print("ERROR: incorrect ship size: " + line)
                sys.exit(0)

        # Error: Overlapping ship
        for row in range(min(start._x, end._x), max(start._x, end._x) + 1):
            for column in range(min(start._y, end._y), max(start._y, end._y) \
                                + 1):
                position = board._grid[column][row]
                if position.space_occupied():
                    print("ERROR: overlapping ship: " + line)
                    sys.exit(0)
                else:
                    position._ship = self
                    self._positions.append(position)

    def __str__(self):
        return f"{self._abbreviation} Ship"

class Board:
    '''
    Represents the game board in the Battleship game

    Attributes:
        _grid (list): 2d list of GridPos objects representing the game board
        _ships (list): List of Ship objects on the board
    '''
    def __init__(self):
        '''
        Initilaizes a Board object.

        Creates a 10 x 10 grid of GridPos objects and an empty list of ships.
        '''
        self._grid = []
        for x in reversed(range(10)):
            row = []
            for y in range(10):
                row.append(GridPos(x, y))
            self._grid.append(row)
        self._ships = []

    def process_guess(self, x, y):
        '''
        Process a player's guess and updates the game.

        Arguments:
            x (int): The x-coordinate of the guess
            y (int): The y-coordinate of the guess

        Prints the result of the guess (hit, miss, sunk) and updates the game.
        '''
        position = self._grid[y][x]
        if position._ship == None:
            print('miss' if not position._guessed else 'miss (again)')
        else:
            if not position._guessed:
                position._ship._health -= 1
            if position._ship._health == 0:
                print(f'{position._ship._abbreviation} sunk')
                self._ships.remove(position._ship)
                if not self._ships:
                    print('all ships sunk: game over')
            else:
                print(f'hit' if not position._guessed else 'hit (again)')
        position._guessed = True

    def __str__(self):
        board = ''
        for row in self._grid:
            for pos in row:
                board += str(pos) + '\n'
        return board.strip()

def create_board():
    '''
    Creates a game board based on the input file

    Returns:
        Board: The initialized game board

    Will exit the program iff coordinates out of bounds or incorrect fleet 
    compositions.
    '''
    file = input()
    player1 = open(file, 'r').readlines()

    board = Board()

    size_index = {'A':5, 'B':4, 'S':3, 'D':3, 'P':2}
    
    for line in player1:
        parts = line.split()
        abbreviation, x1, y1, x2, y2 = parts[0], int(parts[1]), int(parts[2])\
            , int(parts[3]), int(parts[4])

        if 0 <= x1 < 10 and 0 <= y1 < 10 and 0 <= x2 < 10 and 0 <= y2 < 10:
            ship = Ship(board, abbreviation, size_index[abbreviation], \
                        GridPos(x1, y1), GridPos(x2, y2), line)
            board._ships.append(ship)
        else:
            print("ERROR: ship out-of-bounds: " + line)
            sys.exit(0)

    if len(board._ships) != 5:
        print("ERROR: fleet composition incorrect")
        sys.exit(0)
    else:
        ship_list = []
        for ship in board._ships:
            ship_list.append(ship._abbreviation)

        ship_abbreviations = ['A', 'B', 'S', 'D', 'P']
        for ship in ship_abbreviations:
            if ship not in ship_list:
                print("ERROR: fleet composition incorrect")
                sys.exit(0)

    return board

def guess(board):
    '''
    Process player2 guesses based on the input file

    Arguments:
        board (Board): The game board
    '''
    file = input()
    guesses = open(file, 'r')
    

    for line in guesses:
        if len(line.split()) != 2:
            return
        coord = line.split()
        x, y = int(coord[0]), int(coord[1])

        if 0 <= x < 10 and 0 <= y < 10:
            board.process_guess(x, y)
        else:
            print('illegal guess')

    guesses.close()


def main():
    gameboard = create_board()
    guess(gameboard)

main()