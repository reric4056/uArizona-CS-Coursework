'''
    streets.py
    Eric Romero
    Purpose:
    Given a string user input consisitng of elements representing 
    Buildings, Parks, and Empty Lots, an ASCII rendering 
    of that street is returned.
    CSc 120 001, Fall 2023, TA Marrisa
'''

class Building:
    '''
    This class is the representation of an ASCII-made building.

    The object created has attributes for the buildings width, 
    height, and the brick it should be made up of.

    There are simple getter methods defined as well as a method to 
    construct the building given a certain height.
    '''
    def __init__(self, width, height, brick):
        '''
        Parameter:
            width: integer representing the width of the building
            height: integer representing height of the building
            brick: string representing the ASCII character the 
                building is made up of
        '''
        self._width = int(width)
        self._height = int(height)
        self._brick = str(brick)

    # Getter Methods
    def width(self):
        return self._width
    
    def height(self):
        return self._height
    
    def brick(self):
        return self._brick
    
    def at_height(self, height):
        '''
        Parameter:
            height: integer to retrieve the output of the building
        Purpose:
            Given the height parameter, the building is either built 
            in relation to self._height or an empty string with the 
            length of self._width.
        '''
        if 0 <= height < self._height:
            return self._brick * self._width
        else:
            return ' ' * self._width
        
class Park:
    '''
    This class is the representation of an ASCII-made Park 
    (a tree really).

    The object created has attributes for the Parks width and 
    foliage for the tree.

    There are getter methods as well as the method to construct 
    it given a certain height.
    '''
    def __init__(self, width, foliage):
        '''
        Parameter:
            width: integer representing the width of the park
            foliage: String representing the ASCII character of 
            which the tree is made up of
        '''
        self._width = int(width)
        self._foliage = str(foliage)

    # Getter Methods
    def width(self):
        return self._width
    
    def foliage(self):
        return self._foliage
    
    def at_height(self, height):
        '''
        Parameter:
            height: integer to retrieve the output of the building
        Purpose:
            Given the height parameter, the bottom 5 lines will make up a 
            tree in the center the park, any other line is an empty string
            the length of the width attribute.
        '''
        mid_width = int(self._width // 2)
        if 0 <= height < 2:
            return (' ' * mid_width) + '|' + (' ' * mid_width)
        elif height == 2:
            return (' ' * (mid_width - 2)) + (self._foliage * 5) + \
                (' ' * (mid_width - 2))
        elif height == 3:
            return (' ' * (mid_width - 1)) + (self._foliage * 3) + \
                (' ' * (mid_width - 1))
        elif height == 4:
            return (' ' * mid_width) + self._foliage + (' ' * mid_width)
        else:
            return ' ' * self._width
        
class EmptyLot:
    '''
    '''
    def __init__(self, width, trash):
        '''
        Parameter:
            width: integer representing the width of the Empty Lot
            trash: String representing the ASCII character of which 
                the empty lot is riddled with
        '''
        self._width = width
        self._trash = trash

    # Getter methods
    def width(self):
        return self._width
    
    def trash(self):
        return self._trash
    
    def at_height(self, height):
        '''
        Parameter:
            height: integer to retrieve the output of the building
        Purpose:
            Given the height parameter, the Empty lot is either built in 
            relation to self._height or an empty string with the length 
            of self._width.
        '''
        if height == 0:
            space = self._trash * self._width
            space = space.replace('_', ' ')
            return space[:self._width]
        else:
            return ' ' * self._width
        
def max_width(street_spec):
    '''
    Parameter: 
        street_spec: List of strings representing either buildings, 
            parks, and/or empty lots and their attributes

    Purpose:
        Given street_specs, the widths from every element are added 
            together to return a total length of the street
    '''
    if len(street_spec) == 0:
        return 0
    else:
        elem = street_spec[0].split(',')
    return int(elem[0][2:]) + max_width(street_spec[1:])

def street_heights(street_spec):
    '''
    Parameter:
        street_spec: List of strings representing either buildings, parks,
            and/or empty lots and their attributes

    Purpose:
        Given street_specs, every height is added to a list.
            This will be of use for max_height() function
    '''
    # Base Case
    if len(street_spec) == 0:
        return []
    # Recursive Case
    else:
        if street_spec[0].startswith('b'):
            spec = street_spec[0].split(',')
            return [int(spec[1])] + street_heights(street_spec[1:])
        elif street_spec[0].startswith('p'):
            return [5] + street_heights(street_spec[1:])
        elif street_spec[0].startswith('e'):
            return [1] + street_heights(street_spec[1:])
        
def max_height(street_heights):
    '''
    Parameter:
        street_heights: A list of all the collected heights from 
            street_spec

    Purpose:
        Given street_heights, the maximum height will be returned.
    '''
    # Base Case
    if len(street_heights) == 1:
        return street_heights[0]
    # Recursive Case
    else:
        maximum = max_height(street_heights[1:])
        if street_heights[0] > maximum:
            return street_heights[0]
        else:
            return maximum
        
def render_street(street_spec):
    '''
    Parameter:
        street_spec: List of strings representing either buildings, 
            parks, and/or empty lots and their attributes

    Purpose:
        For every element within street_specs, a class object 
            respective to it is created and added to a list.
    '''
    # Base Case
    if len(street_spec) == 0:
        return []
    
    # Recursive Case
    else:
        if street_spec[0].startswith('b'):
            spec = street_spec[0].split(',')
            return [Building(int(spec[0][2:]), int(spec[1]), spec[2])] + \
                render_street(street_spec[1:])
        
        elif street_spec[0].startswith('p'):
            spec = street_spec[0].split(',')
            return [Park(int(spec[0][2:]), spec[1])] + \
                render_street(street_spec[1:])
        
        elif street_spec[0].startswith('e'):
            spec = street_spec[0].split(',')
            return [EmptyLot(int(spec[0][2:]), spec[1])] + \
                render_street(street_spec[1:])
    
def print_at_street_height(street, height):
    '''
    Purpose:
        street: A list of Building, Park, and/or EmptyLot class objects
        height: an integer representing the height of focus for the street

    Purpose:
        Given street and height, an ASCII rendered street is returned 
        using the at_height method for every class
    '''
    # Base Case
    if len(street) == 0:
        return '|'
    # Recursive case
    return street[0].at_height(height) + \
        print_at_street_height(street[1:], height)

def output(street, max_height):
    '''
    Parameters:
        street: A list of Building, Park, and/or EmptyLot class objects
        max_height: An integer representing the maximum height of the street

    Purpose:
        Given street and an index from maximum height decreasing, a string 
        representing the street at that height is printed.
    '''
    # Base Case
    if max_height == 0:
        print('|' + print_at_street_height(street, 0))
        return
    # Recursive Case
    else:
        print('|' + print_at_street_height(street, max_height))
        output(street, max_height - 1)
        return

def main():
    street = input('Street: ')
    street_spec = street.split()
# Retrieves the width and height of the entire street
    street_width = max_width(street_spec)
    height_list = street_heights(street_spec)
    street_height = max_height(height_list)
# Builds the street
    street = render_street(street_spec)
# Outputs the final street
    print('+' + ('-' * street_width) + '+')
    output(street, street_height)
    print('+' + ('-' * street_width) + '+')

main()