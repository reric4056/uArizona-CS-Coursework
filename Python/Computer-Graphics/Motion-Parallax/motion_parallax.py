###
### Eric Romero
### CSc 110
### Benjamin Dicken
### Description:
###

from graphics import graphics
import random

def main():
    x = 0
    gui = graphics(750, 750, 'Motion Parallax')

### Mountains Pt 1
    mount_1 = gui.get_color_string(random.randint(0, 255), random.randint(0, 255), random.randint(0, 255))
    mount_2 = gui.get_color_string(random.randint(0, 255), random.randint(0, 255), random.randint(0, 255))
    mount_3 = gui.get_color_string(random.randint(0, 255), random.randint(0, 255), random.randint(0, 255))
### Motion
    while True:
        x = gui.mouse_x - 375
        y = gui.mouse_y - 375
        gui.clear()
### Background
        gui.rectangle(0, 0, 750, 750, 'light blue')
### Sun
        gui.ellipse(700 + (x / 100), 50 + (y / 100), 75, 75, 'yellow')
### Mountains Pt 2
        gui.triangle(250 + (x / 75), 700 + (y / 75), 375 + (x / 75), 250 + (y / 75), 600, 700, mount_1)
        gui.triangle(-250 + (x / 50), 700 + (y / 50), 100 + (x / 50), 300 + (y / 50), 500, 700, mount_2)
        gui.triangle(250 + (x / 50), 700 + (y / 50), 500 + (x / 50), 300 + (y / 50), 900, 700, mount_3)
### Grass
        gui.rectangle(-100 + (x / 25), 600 + (y / 25), 1000 + (x / 25), 250 + (y / 20), 'light green')
        index = -50
        while index < 1000:
            gui.rectangle(index + (x / 60), 550 + (y / 60), 5, 50 + (y / 25), 'light green')
            index += 10
### Tree
        gui.rectangle(550 + (x / 10), 550 + (y / 10), 30, 90, 'brown')
        gui.ellipse(550 + (x / 10), 525 + (y / 10), 100, 150, 'green')
### Birds
        index = 0
        while index < 6:
            middle_x = x + index * 50
            middle_y = 250 + index * 20
            gui.line(middle_x, middle_y, middle_x - 20, middle_y - 9, 'black', 5)
            gui.line(middle_x, middle_y, middle_x + 20, middle_y - 9, 'black', 5)
            index += 1
        x += 2
        if x > 900:
            x = -200
        gui.update_frame(60)

main()