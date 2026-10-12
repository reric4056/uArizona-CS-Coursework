# Filename: asm5.s
# Author: Eric Romero (Revised)
# Course: CSC 252 - Spring 2025
# Purpose: Implementation of functions countLetters and subsCipher 
#          using extra-large stack frames.
#

.data
dashline:   .asciiz    "----------------\n"
other:      .asciiz    "<other>: "
newline:    .asciiz    "\n"

.text
.globl countLetters
#--------------------------------------------------------------------------------------
# Function: countLetters
# C-Code:
#   void countLetters(char *str) 
#    {
#       int letters[26]; // this function must fill these with zeroes 
#       int other = 0;
#
#       printf("----------------\n%s\n----------------\n", str);
#
#       char *cur = str; 
#       while (*cur != '\0') 
#       { 
#           if (*cur >= 'a' && *cur <= 'z') 
#               letters[*cur-'a']++; 
#           else if (*cur >= 'A' && *cur <= 'Z') 
#               letters[*cur-'A']++; 
#           else 
#               other++;
#
#           cur++; 
#       }
#
#       for (int i=0; i<26; i++) 
#           printf("%c: %d\n", 'a'+i, letters[i]); 
#       printf("<other>: %d\n", other); 
#    }
#
# Uses a stack frame of 120 bytes:
#   27 words for letters and <other> (27 * 4 = 108 bytes)
#   Saving $fp, $ra, and $a0 (3 words; 12 bytes)
#   Total: 108 + 12 = 120 bytes
#---------------------------------------------------------------------------------------
countLetters:
	addiu $sp, $sp, -120				# Allocate 120 bytes
	sw    $fp, 0($sp)				# Save frame pointer
	sw    $ra, 4($sp)				# Save return address
	sw    $a0, 8($sp)				# Save *str
	addiu $fp, $sp, 12				# Adjust frame pointer

	addi  $t0, $zero, 0				# i = 0
	addi  $t1, $zero, 27				# Loop bound = 27 (0 to 26)
	
setupLetters:
	slt   $t2, $t0, $t1				# if (i < 27) then t2 = true
	beq   $t2, $zero, printHeader			# if false, exit loop
	sll   $t3, $t0, 2				# offset = i * 4
	add   $t4, $fp, $t3				# address = FP + offset
	sw    $zero, 0($t4)				# letters[i] (or 'other' for i=26) = 0
	addi  $t0, $t0, 1				# i++
	j     setupLetters

printHeader:
	addi  $v0, $zero, 4				# Print dash line
	la    $a0, dashline
	syscall

	lw    $t5, 8($sp)				# Load string pointer from saved location
	addi  $v0, $zero, 4				# Print string
	add   $a0, $zero, $t5
	syscall

	addi  $v0, $zero, 4				# Print newline
	la    $a0, newline
	syscall

	addi  $v0, $zero, 4				# Print dash line
	la    $a0, dashline
	syscall

countLoop:
	lb    $t6, 0($t5)				# Load current character from string
	beq   $t6, $zero, printLetters			# if (char == '\0') break

	addi  $t7, $zero, 97				# $t7 = 97 ('a')
	slt   $t8, $t6, $t7				# if char < 'a'
	bne   $t8, $zero, uppercaseCheck		# if true, check uppercase
	addi  $t7, $zero, 122				# $t7 = 122 ('z')
	slt   $t8, $t7, $t6				# if 'z' < char
	bne   $t8, $zero, uppercaseCheck		# if true, check uppercase

	addi  $t7, $zero, 97				# $t7 = 97 ('a'
	sub   $t4, $t6, $t7				# index = char - 'a'
	j     updateCount

uppercaseCheck:
	addi  $t7, $zero, 65				# $t7 = 65 ('A')
	slt   $t8, $t6, $t7				# if char < 'A'
	bne   $t8, $zero, otherCheck			# if true, then classify as other
	addi  $t7, $zero, 90				# $t7 = 90 ('Z')
	slt   $t8, $t7, $t6				# if 'Z' < char
	bne   $t8, $zero, otherCheck			# if true, then classify as other

	addi  $t7, $zero, 65				# $t7 = 65 ('A')
	sub   $t4, $t6, $t7				# index = char - 'A'
	j     updateCount

otherCheck:
	addi  $t0, $zero, 104				# Offset 104 = 26 * 4 bytes
	add   $t0, $fp, $t0				# Address for "other"
	lw    $t1, 0($t0)				# Load value for other frequency
	addi  $t1, $t1, 1				# value++
	sw    $t1, 0($t0)				# Store value
	j     nextCharacter

updateCount:
	sll   $t0, $t4, 2				# offset = index * 4
	add   $t1, $fp, $t0				# address for letters[index]
	lw    $t2, 0($t1)				# Load value for letter frequency
	addi  $t2, $t2, 1				# value++
	sw    $t2, 0($t1)				# Store value
	j     nextCharacter

nextCharacter:
	addi  $t5, $t5, 1				# Move to the next character in the string
	j     countLoop

printLetters:
	addi  $t0, $zero, 0				# i = 0
	addi  $t1, $zero, 26				# Loop bound = 26

printLoop:
	slt   $t2, $t0, $t1				# while (i < 26)
	beq   $t2, $zero, printOther			# If i >= 26, then exit
	addi  $t3, $zero, 97				# t3 = 97 ('a')
	add   $t3, $t3, $t0				# letter = 'a' + i

	addi  $v0, $zero, 11				# Print letter character
	add   $a0, $zero, $t3
	syscall

	addi  $v0, $zero, 11				# Print colon (':')
	addi  $a0, $zero, 58
	syscall

	addi  $v0, $zero, 11				# Print space (' ')
	addi  $a0, $zero, 32
	syscall

	sll   $t4, $t0, 2				# offset = i * 4
	add   $t5, $fp, $t4				# address of letters[i]
	lw    $t6, 0($t5)				# Load value for letter frequency
	addi  $v0, $zero, 1				# Print value
	add   $a0, $zero, $t6
	syscall

	addi  $v0, $zero, 11				# Print newline
	addi  $a0, $zero, 10
	syscall

	addi  $t0, $t0, 1				# i++
	j     printLoop

printOther:
	addi  $v0, $zero, 4				# Print "<other>: "
	la    $a0, other
	syscall

	addi  $t0, $zero, 104				# Offset for "other"
	add   $t0, $fp, $t0				# t0 = address location for other
	lw    $t1, 0($t0)				# Load value for other frequency
	addi  $v0, $zero, 1				# Print other value
	add   $a0, $zero, $t1
	syscall

	addi  $v0, $zero, 11				# Print newline
	addi  $a0, $zero, 10
	syscall  

	lw    $a0, 8($sp)				# Restore *str
	lw    $ra, 4($sp)				# Restore return address
	lw    $fp, 0($sp)				# Restore frame pointer
	addiu $sp, $sp, 120				# Deallocate 120 bytes
	jr    $ra					# Return to caller

.globl subsCipher
#----------------------------------------------------------------------------------------
# Function: subsCipher
# C-Code:
#  void subsCipher(char [] str, char [] map)
#  {
#       int len = strlen(str)+1;
#       int len_roundUp = (len+3) & ~0x3;
#       char dup[len_roundUp];  // allocated on the stack
#
#       for (int i=0; i<len-1; i++)
#           dup[i] = map[str[i]];
#       dup[len-1] = '\0';
#
#       printSubstitutedString(dup);
#  }
#
# For this stub, we simply set up and tear down a small frame.
#----------------------------------------------------------------------------------------
subsCipher:
	addiu $sp, $sp, -24				# Allocate 24 bytes
	sw    $fp, 0($sp)				# Save frame pointer
	sw    $ra, 4($sp)				# Save return address
	sw    $a0, 8($sp)				# save char[] str
	sw    $a1, 12($sp)				# save char[] map
	addiu $fp, $sp, 20				# Save frame pointer 
	
	add   $t9, $zero, $sp				# t9 = fixed frame base
	addu  $t0, $zero, $a0				# t0 = str pointer
	addi  $t1, $zero, 0				# t1 = 0 = length
	
strlenLoop:
	lb    $t2, 0($a0)				# t2 = byte from str
	beq   $t2, $zero, strlenEnd			# If '\0' found, exit loop
	addi  $t1, $t1, 1				# length++
	addi  $a0, $a0, 1				# Move to next char in str
	j     strlenLoop
	
strlenEnd:
	addi  $t1, $t1, 1				# t1 = len = strlen(str) + 1

	addi  $t3, $t1, 3				# t3 = len + 3
	addi  $t4, $zero, -4				# t4 = -4 (0xFFFFFFFC)
	and   $t3, $t3, $t4				# t3 = (len+3) & ~0x3

	sw    $t3, 16($sp)				# Save allocation size

	sub   $sp, $sp, $t3				# sp = base of dup buffer
	add   $t5, $zero, $sp				# t5 = dup buffer base pointer
	add   $t6, $zero, $t5				# t6 = write pointer for dup buffer

	add   $a0, $zero, $t0				# a0 = restored string pointer

copyLoop:
	lb    $t7, 0($a0)				# t7 = current str char
	beq   $t7, $zero, copyEnd			# If character is null, exit loop
	add   $t8, $a1, $t7				# address = map pointer + (char value)
	lb    $t8, 0($t8)				# t8 = substituted character from map 
	sb    $t8, 0($t6)				# Store character at dup pointer
	addi  $a0, $a0, 1				# Move str pointer
	addi  $t6, $t6, 1				# Move dup pointer 
	j     copyLoop

copyEnd:
	sb    $zero, 0($t6)				# Store null terminator at dup buffer
	add   $a0, $zero, $t5				# Restore dup base pointer
	jal   printSubstitutedString			# Print the dup string
	add   $v0, $zero, $t5				# v0 = returned dup pointer
	lw    $t3, 16($sp)				# Reload allocation size
	add   $sp, $zero, $t9				# Resotre sp to fixed frame
	
	lw    $a1, 12($sp)				# Restore str
	lw    $a0, 8($sp)				# Restore map
	lw    $ra, 4($sp)				# Restore return address
	lw    $fp, 0($sp)				# Restore frame pointer
	addiu $sp, $sp, 24				# Deallocate 24 bytes
	jr    $ra					# Return to caller
