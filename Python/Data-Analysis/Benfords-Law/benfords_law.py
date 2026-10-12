###
### Eric Romero
### CSc 110
### Benjamin Dicken
### Description:

def file():
    '''
    :return: Reads the data of a file, returns a formatted data
    '''
    # Empty list, appends found numbers
    number_list = []
    # Asks user for file name
    file_name = input('Data file name: \n')
    # Opens and reads the file
    true_file = open(file_name, 'r')
    # Loops every line
    for line in true_file:
        info = line.strip()
        # Splits line that has a comma (',')
        new_info = info.split(',')
        # Loops each word in each line, checks if digit or not
        for word in new_info:
            if word[0].isdigit() and word[len(word)-1].isdigit() and word[0] != '0':
                number_list.append(float(word))
    true_file.close()
    return number_list


def number_count(number_list):
    '''
    :param number_list: Comes from file function
    :return: Returns count occurrence of each number in the number list
    '''
    #Dictionary with keys with 0 value
    count = {1:0, 2:0, 3:0, 4:0, 5:0, 6:0, 7:0, 8:0, 9:0}
    # Loops through the number list
    for number in number_list:
        # Counts each occurrence
        digit = int(str(number)[0])
        count[digit] += 1
    return count


def percent(count):
    '''
    :param count: Count dictionary
    :param number_list: Number list
    :return: Calculates the percent of each number occurrence in the counting dictionary
    '''
    # Total number of numeric values (divisor)
    total = 0
    # percentage list
    per_list = [0]
    # Loops through digits in count
    for x in count.values():
        total += x
    # Text - Prints Number and hashtags dependent on the number
    mode = input('Mode: \n')
    if mode == 'text':
        print()
        for number in range(1, 10):
            print(str(number) + ' | ', end='')
            hashtags = int((count[number] / total) * 100)
            per_list.append(hashtags)
            hashtag_string = '#' * hashtags
            print(hashtag_string)
        return per_list

    # GUI - Void
    elif mode == 'gui':
        pass
    print()


def benford_check(per_list):
    '''
    :param percentage: Percent dictionary
    :return: Checks if follows Benford's Law or not
    '''
    benfords_law = {1:[25,40], 2:[12,27], 3:[7,17], 4:[4,19], 5:[2,17], 6:[1,16], 7:[0,15], 8:[0,15], 9:[0, 14]}
    # Loops through each number percent
    for x in range(1,10):
        if (per_list[x] < benfords_law[x][0]) or (per_list[x] > benfords_law[x][1]):
            return False
    return True


def main():
    number = file()
    counts = number_count(number)
    percentage = percent(counts)
    if benford_check(percentage):
        print("\nFollows Benford's Law")
    else:
        print("\nDoes not follow Benford's Law")


main()
