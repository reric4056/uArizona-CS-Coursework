###
### Eric Romero
### CSc 110
### Benjamin Dicken
### Unmixes / deshuffles lines of an encrypted text file done by a separate program
### Calls a file and uses for loops to decrypt

def main():
    encrypted_file_name = input('Enter the name of an encrypted text file:\n')  #User calls for encrypted text file
    encrypted_index_file_name = input('Enter the name of the encryption index file:\n') #User calls for index text file
    encrypted_file = open(encrypted_file_name, 'r')     #Opens file
    encrypted_index_file = open(encrypted_index_file_name, 'r')         #Opens file
    encrypt_lines = encrypted_file.readlines()          #Initializes content in encrpyted text
    encrypt_index = encrypted_index_file.readlines()    #Initializes content in index text
    new_index_list = []
    decrypt_list = []
    for x in range(len(encrypt_lines)):
        new_index_list.append(x + 1)
    for number in new_index_list:
        #Reads file from first line to max length it is at.
        index = 1
        while index <= len(encrypt_lines) and number != int(encrypt_index[index - 1]):
            index += 1
        if index <= len(encrypt_lines):
            decrypt_list.append(encrypt_lines[index - 1])   #Adds decrypted lines from encrypted text to decrypted list
        encrypted_file.close()
        encrypted_index_file.close()
        decrypt_file = open('decrypted.txt', 'w')   # Creates new file 'decrypted.txt'
        for num in range(len(decrypt_list)):
            decrypt_file.write(str(decrypt_list[num]))  #Overwrites 'decrypted.txt' with original decrypted content.
        decrypt_file.close()
main()