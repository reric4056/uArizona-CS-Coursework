###
### Author: Eric Romero
### Description: Uses code from chess.py. The goal is to checkmate the opposing KING.
#                 This follows 1 column rather than a whole board like the full game.
#                 Uses a 1 by 9 grid with two groups(White/Black) consisting of 1 KING and 2 KNIGHTS each
###

from graphics import graphics

# You should use these globals in the functions in this file!
W_KNIGHT = 'WKn'
W_KING   = 'WKi'
B_KNIGHT = 'BKn'
B_KING   = 'BKi'
EMPTY    = '   '
WHITE    = 'White'
BLACK    = 'Black'
LEFT     = 'l'
RIGHT    = 'r'

def is_valid_move(board, position, player):
# Checks if called piece matches piece on board
    '''
    :param board: list with 9 spaces
    :param position: position of player's pieces
    :param player: Determines if player is black team or white team
    :return: Determines valid moves
    '''
    if 0 <= position <= 9:
        if board[position][0] == player[0]:
            return True
        elif board[position][0] != player[0]:
            return False
    else:
        return False

def move_knight(board, position, direction):
# The use of 2 here is because Knights move 2 places
# EMPTY clears the space
    '''
    :param board: List with 9 spaces
    :param position: Position of player's pieces
    :param direction: moves piece left or right
    :return: Moves knights
    '''
    if direction == 'r':
        board[position + 2] = board[position]
        board[position] = EMPTY

    elif direction == 'l':
        board[position - 2] = board[position]
        board[position] = EMPTY

def move_king(board, position, direction):
    '''
    :param board: List with 9 spaces
    :param position: Position of player's piece
    :param direction: Moves piece left or right
    :return: Moves Kings
    '''
    if direction == 'r':
        for new_positions in range(position + 1, 9):
            if new_positions == 8 or board[new_positions] != EMPTY:
                board[new_positions] = board[position]
                board[position] = EMPTY
                return

    if direction == 'l':
        index = 8
        bking_Front = 1
        new_positions = position - bking_Front
        while index >= 0:
            if new_positions == 0 or board[new_positions] != EMPTY:
                board[new_positions] = board[position]
                board[position] = EMPTY
                index -= 1
                return
            if board[new_positions] == EMPTY:
                while bking_Front < position:
                    bking_Front += 1
                    new_positions = position - bking_Front
                    if board[new_positions] != EMPTY:
                        board[new_positions] = board[position]
                        board[position] = EMPTY
                        bking_Front += 1
                        index -= 1
                        return
def print_board(board):
    '''
    :param board: list with 9 spaces, includes player pieces
    :return: Prints board
    '''
    index = 0
    print('+' + '-' * 53 + '+')
    while index < len(board):
        print('|', board[index], end=' ')
        index = index + 1
    print('|')
    print('+' + '-' * 53 + '+')



def is_game_over(board):
    '''
    :param board: list with 9 spaces
    :return: Determines who wins by seeing which king is left.
    '''
    if B_KING not in board:
        print_board(board)
        print('White wins!')
        return True

    elif W_KING not in board:
        print_board(board)
        print('Black wins!')
        return True

    elif W_KING in board and B_KING in board:
        return False

def move(board, position, direction):
    '''
    :param board: List with 9 spaces
    :param position: Position of player's piece
    :param direction: Moves piece left or right
    :return:
    '''
    if board[position] == B_KNIGHT or board[position] == W_KNIGHT:
        move_knight(board, position, direction)

    elif board[position] == B_KING or board[position] == W_KING:
        move_king(board, position, direction)

def main():
   
    # DO NOT CHANGE THE CODE IN THE MAIN FUNCTION

    # Create the canvas
    gui = graphics(700, 200, '1 Dimensional Chess')

    # This is the starting board.
    # This board variable can and should be passed to other functions
    # and changed as moves are made.
    board = [W_KING, W_KNIGHT, W_KNIGHT, EMPTY, EMPTY, EMPTY, B_KNIGHT, B_KNIGHT, B_KING]
    
    # White typically starts in chess.
    # This will change between WHITE and BLACK as the turns progress.
    player = WHITE
    
    # This variable will be updated to be True if the game is over.
    # The game is over after one of the kings dies.
    is_game_won = False

    # This loop controls the repetitive nature of the turns of the game.
    while not is_game_won:
        
        print_board(board)

        # Draw the board
        draw_board(board, gui)

        position = int(input(player + ' enter index:\n'))
        direction = input(player + ' enter direction (l or r):\n')

        # If the desired move is valid, then call the move function.
        # Also, change the player variable.
        if is_valid_move(board, position, player):
            if player == WHITE:
                move(board, position, direction)
                player = BLACK
            else:
                move(board, position, direction)
                player = WHITE
            # Draw the board again
            draw_board(board, gui)
            is_game_won = is_game_over(board)

main()

### I was on a time crunch so I took out the draw_board function to work on at a later time.