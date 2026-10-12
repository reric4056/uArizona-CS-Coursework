###
### Author: Eric Romero
### Class: CSc 110
### Description: The following program is a MadLibs that uses the input() and print() functions.

# Below displays a list of questions the user has to input. These responses will then play a part in the story inscribed.
female = input('A female name:\n')
street = input('A street name:\n')
male = input('A male name:\n')
object = input('An object:\n')
vehicle = input('A vehicle:\n')
adjective = input('An adjective:\n')

# Below displays the statements that will print out in regard to the responses to the previous questions.

print('----------')
print(female + ' decided to move from her apartment on 5th')
print('to a condo on ' + street + '. She called her friend ' + male)
print('for help. However, they could not fit ' + object + ' into')
print('the moving truck, so they had to rent a ' + adjective + ' ' + vehicle)
print('in order to move it!')
