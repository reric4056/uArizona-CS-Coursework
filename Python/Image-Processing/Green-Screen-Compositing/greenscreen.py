###
### Author: Eric Romero
### Description: This program combines an image (containing a red, green, or blue screen) with a background image. 
###

def get_image_dimensions_string(file_name):
    '''
    Given the file name for a valid PPM file, this function will return the
    image dimensions as a string. For example, if the image stored in the
    file is 150 pixels wide and 100 pixels tall, this function should return
    the string '150 100'.
    file_name: A string. A PPM file name.
    '''
    image_file = open(file_name, 'r')
    image_file.readline()
    return image_file.readline().strip('\n')

def load_image_pixels(file_name):
    ''' Load the pixels from the image saved in the file named file_name.
    The pixels will be stored in a 3d list, and the 3d list will be returned.
    Each list in the outer-most list are the rows of pixels.
    Each list within each row represents and individual pixel.
    Each pixels is representd by a list of three ints, which are the RGB values of that pixel.
    '''
    pixels = []
    image_file = open(file_name, 'r')

    image_file.readline()
    image_file.readline()
    image_file.readline()

    width_height = get_image_dimensions_string(file_name)
    width_height = width_height.split(' ')
    width = int(width_height[0])
    height = int(width_height[1])

    for line in image_file:
        line = line.strip('\n ')
        rgb_row = line.split(' ')
        row = []
        for i in range(0, len(rgb_row), 3):
            pixel = [int(rgb_row[i]), int(rgb_row[i+1]), int(rgb_row[i+2])]
            row.append(pixel)
        pixels.append(row)

    return pixels


def greenscreen_algorithm(chan, chan_dif, gs_pix, fi_pix):
    '''
    Analyzes pixels in GreenScreen image. Determines if it will stay or be replaced when in contact with fill image.
    Organizes the new pixels in a new 3D list, which is returned.
    :param chan: String representing an RGB value
    :param chan_dif: float value(1.0 - 10.0)
    :param gs_pix: 3D list of pixels for green screen image
    :param fi_pix: 3D list of pixels for fill screen image
    :return:
    '''
    new_pix = []
    row_i = 0
    pix_i = 0
    if chan == 'r':
        rgb_i = 0
    elif chan == 'g':
        rgb_i = 1
    elif chan == 'b':
        rgb_i = 2

    for row in gs_pix:
        new_pix.append([])
        for pixel in row:
            if int(pixel[rgb_i]) > chan_dif * int(pixel[rgb_i - 1]) and \
            int(pixel[rgb_i]) > chan_dif * int(pixel[rgb_i - 2]):
                new_pix[row_i].append(fi_pix[row_i][pix_i])

            else:
                new_pix[row_i].append(pixel)
            pix_i += 1
        row_i += 1
        pix_i = 0
    return new_pix

def new_file_write(new_pix, out_file, gs_dimension):
    '''
    This writes the ppm file
    :param new_pix: 3D list of new pixels for output.
    :param out_file: string containing new ppm file name.
    :param gs_dimension: String containing GreenScreen file dimensions, used for the new file.
    :return:
    '''

    out_ppm_file = open(out_file, 'w')
    out_ppm_file.write('P3\n')
    out_ppm_file.write(gs_dimension + '\n')
    out_ppm_file.write('255\n')

    for row in new_pix:
        for pixel in row:
            for rgb in pixel:
                out_ppm_file.write(str(rgb) + ' ')
        out_ppm_file.write('\n')
    out_ppm_file.close()
    print('Output file written. Exiting.')


def main():

    # Get the 5 input values from the user, as described in the PA specification
    # These input values will be validated later in main
    channel = input('Enter color channel\n')
    if channel not in ['r', 'g', 'b']:
        print('Channel must be r, g, or b. Will exit.')
        exit()
    channel_difference = float(input('Enter color channel difference\n'))
    if channel_difference < 1.0 or channel_difference > 10.0:
        print('Invalid channel difference. Will exit.')
        exit()
    gs_file = input('Enter greenscreen image file name\n')
    gs_dimension = get_image_dimensions_string(gs_file)
    fi_file = input('Enter fill image file name\n')
    if gs_dimension != get_image_dimensions_string(fi_file):
        print("Images not the same size. Will exit.")
        exit()
    out_file = input('Enter output file name\n')
    
    # Next, Do some valiation of the input values
    # The PA specification tells you what you need to validate
    
    # If the the input is valid, implement the greenscreen.
    # You should:
    #    * Load in the image data from the two input image files.
    #      Use the provided load_image_pixels functions for this!
    #    * Generate a NEW image based on the two input values,
    #      using the greenscreen algorithm described in the specification
    #    * Save the newly-generated image to a file
    # You probably will want to create other function(s) that you call from here.
    gs_pixel = load_image_pixels(gs_file)
    fi_pixel = load_image_pixels(fi_file)
    new_pix = greenscreen_algorithm(channel, channel_difference, gs_pixel, fi_pixel)
    new_file_write(new_pix, out_file, gs_dimension)


main()

