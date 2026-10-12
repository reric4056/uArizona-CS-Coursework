###
### Author : Eric Romero
### Class : CSc 110
### Description: The following program is a MadLibs that uses the input() and print() functions.
### It describes a situation in which two friends plan to go on vacation but have to deal with a pet.

# This portion gives a user input option. Said inputs will be used in the printed statements.
male = input('A male name:\n')
female = input('A female name:\n')
pet = input('A pet name:\n')
place = input('A place:\n')
adjective = input('An adjective:\n')
animal = input('An animal:\n')
verb = input('A verb ending in ing:\n')
adverb = input('An adverb:\n')

# This portion details the statements that will print in regard to the user input information.
print('----------')
print(male + ' and ' + female + ' were best friends.')
print('One day ' + male + ' and ' + female + ' decided to go on a')
print('vacation to ' + place + '. However, they didn\'t know')
print('what to do with their ' + adjective + ' pet ' + animal + ' named ' + pet + '.')
print(pet + ' had been causing problems, due to constant ' + verb + '.')
print(male + ' found a sitter for their pet, and ' + adverb + ' went on the trip.')