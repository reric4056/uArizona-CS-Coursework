###
### Eric Romero
### CSc 110
### Description: Takes two inputs (an axis mode, plot string). Horizontal mode creates a horizontal graph.
###              Vertical mode creates a Vertical graph. The horizontal function uses a nested while loop
###              This program uses a variety of if statements.
###
index = 0
position = 8
variation = 0

mode = input('Enter stock plotter mode:\n')
plot_string = input('Enter stock plot input string:\n')

if mode == 'vertical':
    print('#' * 19)
    while len(plot_string) / 2 > variation:
        if plot_string[index] == 'u':
            position += int(plot_string[index + 1])
            print('#' + ' ' * position + '*' + ' ' * (16 - position) + '#')
        elif plot_string[index] == 'd':
            position -= int(plot_string[index + 1])
            print('#' + ' ' * position + '*' + ' ' * (16 - position) + '#')

        index += 2
        variation += 1
    print('#' * 19)


elif mode == 'horizontal':
    top = 16
    print('#' * (len(plot_string) // 2))
    while variation <= 16:
        level = 8
        while len(plot_string) > index:
            if plot_string[index] == 'u':
                level += int(plot_string[index + 1])
                print('#' + ' ' * level + '*' + ' ' * (16 - level) + '#')
            elif plot_string[index] == 'd':
                level -= int(plot_string[index + 1])
                print('#' + ' ' * level + '*' + ' ' * (16 - level) + '#')

            index += 2
            variation += 1
    print('#' * (len(plot_string) // 2))

else:
    print('what?')
    exit()
