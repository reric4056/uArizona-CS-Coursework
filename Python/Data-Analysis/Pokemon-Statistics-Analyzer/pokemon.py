'''
    pokemon.py
    Eric Romero
    Purpose:
        Reads in CSV file.
        The file is converted into a 2D dictionary.
        (First level: types, Second level: names)
        This dictionary processed finding averages values
        based on types.
        A new dictionary is created with the types and the maximum 
        average attributes as the values. 
        Given user input on a specific attribute, 
        type and maximum average value is output.
    CSc 120 001, Fall 2023
'''
def readCSV(filename):
    '''
    Parameters:
        filename: string from user input function. 

    Purpose:
        File is opened and a dictionary created with
        the information from the CVS file.
        {type1:{name1:attributes1}, ...}

    Returns:
        poke_dictionary: 2D dictionary containing 
        {type1:{name1:attributes1}, ...}
    '''
    file = open(filename, 'r').readlines()[1:]
    poke_dictionary = {}

    # Loops through every row of CSV file, cleans every line of commas.
    for line in file:
        pokemon_info = {}
        info = line.strip('\n').split(',')
        # Separates by type(1), name, and attributes
        type, name, atrbtes = info[2], info[1], info[4:11]
        pokemon_info[name] = atrbtes
        # Updates dictionary
        if type not in poke_dictionary:
            poke_dictionary[type] = pokemon_info

        else:
            poke_dictionary[type][name] = atrbtes

    return poke_dictionary

def get_average(poke_dictionary):
    '''
    Parameters: 
        poke_dictionary: Two dimensional dictionary.
                {type1:{name1:attributes1}, ...}

    Purpose:
        Processes average values for each type,
        Creates new level2 key for average, and places attribute values within this 

    Returns:
        poke_dictionary: Updated version of the parameters.
        For level 2(names), a new key is created to keep record of 
        averages for every type. 
    '''
    # Loops through dictionary, pokemon type then names.
    # Retrives attributes and finds average
    for type in poke_dictionary:
        len_dict_type = len(poke_dictionary[type])
        attributes = [0,0,0,0,0,0,0]

        for pokemon in poke_dictionary[type]:
            atrbtes = poke_dictionary[type][pokemon]
            for atrbte in range(len(atrbtes)):
                attributes[atrbte] += int(atrbtes[atrbte])

        # An average list added to the pokemon dictionary
        # Average kept as a reference for locating maximums.
        average_list =[]
        for idx in attributes:
            average_list.append(idx/len_dict_type)

        poke_dictionary[type]['Average'] = average_list

    return poke_dictionary

def max_average(poke_dict, index):
    '''
    Parameters: 
        poke_dict: 2D dicitonary, updated level 2 key : 'Average'
        index: Integer locating to certain attribute. 

    Purpose:
        A dictionary is created to hold
        {type(string):avergae(float)}
        Considers more than 1 type.
        Final dicitonary is sorted.

    Returns:
        Outputs the pair for max pokemon type and max average
    '''
    max_dict = {}
    max_avg = 0
    # Loops through pokemon dictionary, and average values list, 
    # locates attribute, compares max_average
    for types in poke_dict:
        average_values = poke_dict[types]['Average']
        if average_values[index] > max_avg:
            max_avg = average_values[index]
            max_dict = {}
            max_dict[types] = max_avg

        elif average_values[index] == max_avg:
            max_dict[types] = max_avg

    sorted_type = sorted(list(max_dict.keys()))
    for types in sorted_type:
        print("{}: {}".format(types, max_dict[types]))

def main():
    file_name = input()
    poke_dictionary = readCSV(file_name)
    poke_dict = get_average(poke_dictionary)

    query = input().lower()
    # Attribute list to index 
    atrbte_idx = ['total', 'hp', 'attack', 'defense', 'specialattack',\
                  'specialdefense', 'speed']
    # Loops and ndexes to location within attribute index to process 
    while query != '':
        if query in atrbte_idx:
            max_values = max_average(poke_dict, atrbte_idx.index(query))
        query = input().lower()
    else:
        return

main()
