# asm6.s
# Author: Eric Romero
# Class: CSC 252 - Spring 2025
# Due 4/30/2025
# Purpose:
#	This program simulates the behavior of a Cold-War era spy cipher system.
# 	Takes agent ID, a date, a key phrase, anagram, and secret message to encode.
#	Outputs an encoded version of the message using a muti step encryption based on digit permutation and a straddling checkerboard
#	Credit to Professor Lester Mccann's "The VIC (VIC INCOMPLETE) Cipher) Assignment Spec
#	Encoder/Decoder
#

.data
# Static Messages
welcomeMsg:		.asciiz 	"Welcome to my (incomplete) VIC Cipher Encoder!\n\n"
inputID: 		.asciiz 	"Enter your 5-digit Agent ID:\n"
inputDate: 		.asciiz 	"Enter a 6-digit Date (YYMMDD):\n"
inputPhrase: 		.asciiz 	"Enter your phrase (Min. 10 Uppercase Characters):\n"
inputAnagram: 		.asciiz 	"Enter your 10-character Anagram (8 letters, 2 spaces):\n"
inputMessage: 		.asciiz 	"Enter the message to encode:\n"

resultMsg: 		.asciiz 	"Encoded Message:\n"
errorMsg: 		.asciiz 	"Invalid Input. Please try again.\n"
newline: 		.asciiz 	"\n"

# Buffers
agentID: 		.space 8 	# 5 digits + newline + null terminator
date: 			.space 8 	# 6 digits + newline + null terminator
phrase: 		.space 32 	# Flexible space for longer phrases
anagram: 		.space 16 	# 10 characters max
message: 		.space 256 	# Input message to decode
encodedMessage: 	.space 512 	# Encoding output buffer

# Task Buffers
expandedID: 		.space 16 	# Chain addition result 
permBuffer1: 		.space 16
permut1: 		.space 16 	# Permutation from phrase
sumNoCarry: 		.space 16 
permBuffer2: 		.space 16
permut2: 		.space 16 	# Permutation from chain and permut1
checkerboard: 		.space 128 	# Stradding checkerboard map

# Constants
digits: 		.asciiz 	"0123456789" #Digits 0-9

.text

.globl main
main:
	la    $a0, welcomeMsg		# a0 = &welcomeMsg
	addi  $v0, $zero, 4		# Print
	syscall
	
	la    $a0, inputID		# a0 = &inputID message
	addi  $v0, $zero, 4		# print
	syscall
	
	la    $a0, agentID		# a0 = buffer for input
	addi  $v0, $zero, 8		# read in
	addi  $a1, $zero, 8		# a1 = max input size
	syscall
	
	la    $a0, inputDate		# a0 = &inputDate message
	addi  $v0, $zero, 4		# print
	syscall
	
	la    $a0, date			# a0 = &date
	addi  $v0, $zero, 8		# read in
	addi  $a1, $zero, 8		# a1 = max input size
	syscall
	
	la    $a0, inputPhrase		# a0 = &inputPhrase
	addi  $v0, $zero, 4		# print
	syscall
	
	la    $a0, phrase		# a0 = &phrase
	addi  $v0, $zero, 8		# read in
	addi  $a1, $zero, 32		# max input size
	syscall
	
	la    $a0, inputAnagram	# a0 = &inputAnagram
	addi  $v0, $zero, 4		# print
	syscall
	
	la    $a0, anagram		# a0 = &anagram
	addi  $v0, $zero, 8		# read in
	addi  $a1, $zero, 16		# a1 = max input size
	syscall
	
	la    $a0, inputMessage	# a0 = &inputMessage
	addi  $v0, $zero, 4		# print
	syscall
	
	la    $a0, message		# a0 = &message
	addi  $v0, $zero, 8		# read in 
	addi  $a1, $zero, 256		# a1 = max input size
	syscall
	
	# Perform Encoding
	
	jal   noCarryAddition
	
	jal   chainAddition
	
	jal   digitPermutation
	
	jal   secondNoCarryAddition
		
	jal   secondPermutation
	
	jal   buildCheckerboard
	
	jal   encodeMessage
	
	la    $a0, resultMsg		# a0 = &resultMsg
	addi  $v0, $zero, 4		# print
	syscall
	
	la    $a0, encodedMessage	# a0 = encodedMessage
	addi  $v0, $zero, 4		# print
	syscall
	
	addi  $v0, $zero, 10		# Exit program
	syscall
	
#
# Method: noCarryAddition
# Purpose:
# 	Does no-carry additionn of each digit in the Agent ID and the date fields.
#
# Behavior:
# 	for i = 0 to 4:
#		expandedID[i] = (agentID[i] + date[i]) % 10
#
# Registers:
#	t0 : int - current agentID digit
#	t1 : int - current date digit 
#	t2 : int - Result
#	t3 : int - loop index
#	t4 : base address of agentID
#	t5 : base address of date
#	t6 : base address of expandedID
#	t7 : For Constants
#	t8 & t9 : Temp for pointer arithmetic
#
#
noCarryAddition:
	la    $t4, agentID 			# t4 = &agentID
	la    $t5, date 			# t5 = &date
	la    $t6, expandedID 			# t6 = &expandedID
	addi  $t3, $zero, 0 			# t3 = loop index = 0
	
noCarryLoop:
	addi  $t7, $zero, 5			# t7 = 5
	beq   $t3, $t7, noCarryEnd		# if (i == 5), exit loop
	
	add   $t8, $t4, $t3			# t8 = &agentID[i]
	lb    $t0, 0($t8)			# t0 = agentID[i]
	
	add   $t9, $t5, $t3			# t9 = &date[i]
	lb    $t1, 0($t9)			# t1 = date[i]
	
						# Convert ASCII to int
	addi  $t7, $zero, 48			# t7 = 48
	sub   $t0, $t0, $t7			# t0 = agentID[i] - 48
	sub   $t1, $t1, $t7			# t1 = date[i] - 48
	
	add   $t2, $t0, $t1			# sum = id + date
	addi  $t7, $zero, 10			# t7 = 10
	rem   $t2, $t2, $t7			# result %= 10
	
	addi  $t7, $zero, 48			# t7 = 48
	add   $t2, $t2, $t7			# t2 = result + 48
	
	add   $t8, $t6, $t3			# t8 = &expandedID[i]
	sb    $t2, 0($t8)			# Store result at location
	
	addi  $t3, $t3, 1			# i++
	j     noCarryLoop
	
noCarryEnd:
	addi  $t7, $zero, 0			# t7 = 0 = null terminator
	add   $t8, $t6, $t3			# t8 = &expandedID[5]
	sb    $t7, 0($t8)			# Stores null terminator at end
	
	jr    $ra				# Return to caller
	
# 
# Method: chainAddition
# Purpose: 
#	Expands the 5-digit number stored in expandedID into 10 digits using chain addition logic.
#
# Registers:
#	t0 : First digit from expandedID
#	t1 : Second digit from expandedID
#	t2 : Result of no carry addition of t0 and t1
# 	t3 : loop counter
# 	t4 : base address of expandedID
#	t5 : Address of current storage location
# 	t6 : Address of next digit in expandedID
#	t7 : Constant 10
#	t8 : Constant 5
# 	t9 : Constant 48
#	
chainAddition:
	la    $t4, expandedID 			# t4 = &expandedID
	addi  $t3, $zero, 0			# t3 = loop counter = 0
	
chainLoop:
	addi  $t8, $zero, 5			# t8 = 5
	beq   $t3, $t8, chainEnd		# If i == 5, exit loop
	
	add   $t5, $t4, $t3			# t5 = &expandedID[i]
	lb    $t0, 0($t5)			# t0 = expandedID[i]
	addi  $t9, $zero, 48			# t9 = 48 (ASCII '0')
	sub   $t0, $t0, $t9			# Convert into integer
	
	addi  $t6, $t3, 1			# i + 1
	add   $t6, $t4, $t6			# t6 = &expandedID[i + 1]
	lb    $t1, 0($t6)			# t1 = expandedID[i + 1]
	sub   $t1, $t1, $t9			# Convert into integer
	
	add   $t2, $t0, $t1			# t2 = digit 0 + digit 1
	addi  $t7, $zero, 10			# t7 = 10
	rem   $t2, $t2, $t7			# t2 = (digit 0 + digit 1) % 10 
	
	add   $t2, $t2, $t9			# Convert result back to ASCII
	
	addi  $t5, $t3, 5			# Offset to write into expandedID[i + 5]
	add   $t5, $t4, $t5			# t5 = &expandedID[i + 5]
	sb    $t2, 0($t5)			# Store result char 
	
	addi  $t3, $t3, 1			# i++
	j     chainLoop				# Repeat
	
chainEnd:
	addi  $t7, $zero, 0			# t7 = 0
	addi  $t5, $t4, 10			# t5 = &expandedID[10]
	sb    $t7, 0($t5)			# Store null terminator
	
	jr    $ra				# Return to caller
	
# Method:
# 	DigitPermutation
#
# Purpose:
#	Sorts the first 10 characters of the input phrase 
#	and assigns each a digit based on position in sorted order.
#
# Registers:
#	t0 : outer index / copy loop index
#	t1 : inner index / match index
#	t2 : character from phrase
#	t3 : character from permBuffer1 (sorted version)
#	t4 : temp
#	t5 : base address of phrase
#	t6 : Base address of permBuffer1 (sorted)
#	t7 : Based assdress of permut1
#	t8 : address pointer
#	t9 : temporary constant register
# 	
digitPermutation:
	la    $t5, phrase			# t5 = &phrase
	la    $t6, permBuffer1			# t6 = &permBuffer1
	
	addi  $t0, $zero, 0			# t0 = i = 0

	# Copy first 10 chars of phrase to permBuffer1
copyLoop:
	addi  $t7, $zero, 10			# t7 = 10
	beq   $t0, $t7, sortPhrase		# if (i == 10), exit loop
	
	add   $t8, $t5, $t0			# t7 = &phrase[i]
	lb    $t2, 0($t8)			# t2 = phrase[i]
	
	add   $t9, $t6, $t0			# t9 = &permBuffer1[i]
	sb    $t2, 0($t9)			# permBuffer1[i] = phrase[i]
	
	addi  $t0, $t0, 1			# i++
	j     copyLoop				# Repeat
	
	# Sort conents of permBuffer1 using Bubble Sort
sortPhrase:
	addi  $t0, $zero, 0			# t0 = outer loop index (i) = 0
	
outerLoopSort:
	addi  $t7, $zero, 9			# t7 = 9
	bge   $t0, $t7, sortEnd		# if (i >= 9), exit outer loop
	
	addi  $t1, $t0, 1			# t1 = inner loop count(j) = outer + 1
	
innerLoopSort:
	addi  $t7, $zero, 10			# t7 = 10
	bge   $t1, $t7, nextOuterIter		# If (j >= 10), exit inner loop
	
	add   $t8, $t6, $t0			# t9 = &permBuffer1[i]
	lb    $t2, 0($t8)			# t2 = permBuffer1[i]
	
	add   $t9, $t6, $t1			# t9 = &permBuffer1[j]
	lb    $t3, 0($t9)			# t3 = permBuffer1[j]
	
	ble   $t2, $t3, skipSwap		# if permBuffer1[i] <= permBuffer1[j], skip swap
	
	sb    $t3, 0($t8)			# permBuffer1[i] = permBuffer1[j]
	sb    $t2, 0($t9)			# permBuffer1[j] = permBuffer1[i]
	
skipSwap:
	addi  $t1, $t1, 1			# j++
	j     innerLoopSort			# Repeat inner loop
	
nextOuterIter:
	addi  $t0, $t0, 1			# i++
	j     outerLoopSort			# Repeat outer loop
	
sortEnd:
	addi  $t4, $zero, 0			# t4 = 0 = index
	la    $t5, phrase			# t5 = &phrase
	la    $t6, permBuffer1			# t6 = &permBuffer1
	la    $t7, permut1			# t7 = digit permutation result
	
	addi  $t0, $zero, 0			# t0 = phrase index (k) = 0
	
	# For each letter in original phrase, find position in sorted buffer and assign corresponding digit
digitMapLoop:
	addi  $t9, $zero, 10			# t9 = 10
	beq   $t0, $t9, digitMapEnd		# If (k > 10), exit loop
	
	add   $t8, $t5, $t0			# t8 = &phrase[i]
	lb    $t2, 0($t8)			# t2 = phrase[i]
	
	addi  $t1, $zero, 0			# t2 = 0 = index into sorted buffer (l)
	
findMatch:
	addi  $t9, $zero, 10			# t9 = 10
	beq   $t1, $t9, storeDigit		# If l == 10, fallback (but it should not happen)
	
	add   $t8, $t6, $t1			# t8 = &permBuffer1[l]
	lb    $t3, 0($t8)			# t3 = permBuffer1[l]
	
	bne   $t2, $t3, keepFinding		# If phrase[i] != sorted[j], keep searching
	
	add   $t8, $t7, $t0			# t8 = &permut1[i]
	addi  $t9, $zero, 48			# ASCII offset for digit
	add   $t9, $t9, $t1			# ASCII for digit l
	sb    $t9, 0($t8)			# Stores digit into permut1[k]
	
	li    $t9, '~'				# Placehold to avoid duplicates
	add   $t8, $t6, $t1			# Mark sorted[l] as used
	sb    $t9, 0($t8)			# Stores place holder in spot
	
	j     nextDigit				# Move to next(k)
	
keepFinding:
	addi  $t1, $t1, 1			# l++
	j     findMatch				# Repeat search
	
storeDigit:
	addi  $t9, $zero, 48			# Store ASCII '0' as fallback
	add   $t8, $t7, $t0
	sb    $t9, 0($t8)
	
nextDigit:
	addi  $t0, $t0, 1			# k++
	j     digitMapLoop			# Repeat loop
	
	# Null Terminate the permut1 string
digitMapEnd:
	add   $t8, $t7, $t0			# t8 = &permut1[10]
	sb    $zero, 0($t8)			# Store null terminator
	
	jr    $ra
	
# Method: secondNoCarryAddition
# Purpose: Performs no-carry addition between two 10-figit sequences (expandedID and permut1)
#	Stores the result in sumNoCarry. 
#
# Registers:
#	t0: Digit from expandedID
#	t1 : Digit from permut1
#	t2 : No Carry result of t0 and t1
#	t3 : Loop index
# 	t4: address of expandedID
#	t5 : address of permut1
#	t6 : address of sumNoCarry
#	t7 : Constant 48 for null terminator
#	t8 : Constant 10
# 	t9 : Temporary address pointer
secondNoCarryAddition:
	la    $t4, expandedID			# t4 = &expandedID
	la    $t5, permut1			# t5 = &permut1
	la    $t6, sumNoCarry			# t6 = &sumNoCarry
	
	addi  $t3, $zero, 0			# t3 = 0 = index 
	addi  $t7, $zero, 48			# t7 = offset for null terminator
	
secondCarryLoop:
	addi  $t8, $zero, 10			#t8 = 10 
	beq   $t3, $t8, secondCarryEnd		# If index == 10, exit loop
	
	add   $t9, $t4, $t3			# t9 = &expandedID[i]
	lb    $t0, 0($t9)			# t0 = expandedID[i]
	sub   $t0, $t0, $t7			# t0 = expandedID[i]
	
	add   $t9, $t5, $t3			# t9 = &permut1[i]
	lb    $t1, 0($t9)			# t1 = permut1[i] - ASCII
	sub   $t1, $t1, $t7			# t1 = permut1[i] - INT
	
	add   $t2, $t0, $t1			# t2 = digit sum
	addi  $t8, $zero, 10			# t8 = 10
	rem   $t2, $t2, $t8			# t2 = (t0 + t1) % 10
	
	add   $t2, $t2, $t7			# t2 = convert to ASCII
	add   $t9, $t6, $t3			# t9 = &sumNoCarry[i]
	sb    $t2, 0($t9)			# sumNoCarry[i] = result digit
	
	addi  $t3, $t3, 1			# i++
	j     secondCarryLoop			# Repeat
	
secondCarryEnd:
	add   $t9, $t6, $t3			# t9 = &sumNoCarry[10]
	sb    $zero, 0($t9)			# null terminate sumNoCarry string
	
	jr    $ra				# Return

# Method: secondPermutation
# Purpose:
# 	Permutation of digits 0-9 generated based on sumNoCarry result string.
#	Mimics digit permutation, works from numeric string.
#
# Process:
#	1. Copy sumNoCarry into permBuffer2
#	2. Sort permBuffer2 digits in ascending order
#	3. For each original digit in sumNoCary
#		a. Find first occurance in sorted permBuffer2
#		b. Map index as ASCII ('0' + j) into permut2[i]
#		c. Replace used value in permBuffer2 with placeholder '~'
#
# Register:
# 	t0 : outer/copy index
#	t1 : inner/match index
#	t2 : digit from sumNoCarry
#	t3 : digit from permBuffer2
#	t4: result from digit + ASCII offset
#	t5 : base address of sumNoCarry
#	t6 : base address of permBuffer2
#	t7 : base address of permut2
#	t8 : address pointer
# 	t9 : constant or pointer register

secondPermutation:
	la    $t5, sumNoCarry			# t5 = &sumNoCarry
	la    $t6, permBuffer2			# t6 = &permBuffer2
	la    $t7, permut2			# t7 = &permut2
	
	addi  $t0, $zero, 0			# t0 = i = 0 (copy index
	
secondCopy:
	addi  $t9, $zero, 10			# t9 = 10
	beq   $t0, $t9, secondSort		# If (i == 10), exit loop
	
	add   $t8, $t5, $t0			# t8 = &sumNoCarry[i]
	lb    $t2, 0($t8)			# t2 = sumNoCarry[i]
	
	add   $t8, $t6, $t0			# t8 = &permBuffer2[i]
	sb    $t2, 0($t8)			# permBuffer2[i] = sumNoCarry[i]
	
	addi  $t0, $t0, 1			# i++
	j     secondCopy
	
secondSort:
	addi  $t0, $zero, 0			# outer loop index i = 0
	
secondOuter:
	addi  $t9, $zero, 9			# t9 = 9
	bge   $t0, $t9, secondMap		# If (i >= 9), done sorting
	
	addi  $t1, $t0, 1			# t1 = j = i + 1
	
secondInner:
	addi  $t9, $zero, 10			# t9 = 10
	bge   $t1, $t9, secondNextOuter	# If j >= 10, next i
	
	add   $t8, $t6, $t0			# t8 = &permBuffer2[i]
	lb    $t2, 0($t8)			# t2 = permBuffer2[i]
	
	add   $t9, $t6, $t1			# t9 = &permBuffer2[j]
	lb    $t3, 0($t9)			# t3 = permBuffer2[j]
	
	ble   $t2, $t3, secondSkipSwap		# Skip if sorted
	
	sb    $t3, 0($t8)			# Swap values
	sb    $t2, 0($t9)
	
secondSkipSwap:
	addi  $t1, $t1, 1			# t1 = j++
	j     secondInner
	
secondNextOuter:
	addi  $t0, $t0, 1			# t0 = i++
	j     secondOuter
	
secondMap:
	addi  $t0, $zero, 0			# i = 0

secondMapLoop:
	addi  $t9, $zero, 10			# t9 = 10
	beq   $t0, $t9, secondPermEnd		# If (i == 10), exit
	
	add   $t8, $t5, $t0			# t8 = &sumNoCarry[i]
	lb    $t2, 0($t8)			# t2 = sumNoCarry[i]
	
	addi  $t1, $zero, 0			# j = 0
	
secondFindMatch:
	addi  $t9, $zero, 10			# t9 = 10
	beq   $t1, $t9, secondFallBack		# Fallback if no match
	
	add   $t8, $t6, $t1			# t8 = &permBuffer2[j]
	lb    $t3, 0($t8)			# t3 = permBuffer2[j]
	
	li    $t9, '~'				# t9 = placeholder ascii character
	beq   $t3, $t9, secondSkipMatch	# Skip if already used
	
	bne   $t2, $t3, secondSkipMatch	# If sumNoCarry[i] != permBuffer2[j], then exit
	
	add   $t8, $t7, $t0			# t8 = &permut2[i]
	addi  $t4, $zero, 48			# t4 = offset from null terminator
	add   $t4, $t4, $t1			# t4 = ASCII(j}
	sb    $t4, 0($t8)			
	
	li    $t4, '~'				# t4 = ASCII '~'
	add   $t8, $t6, $t1			# t8 = &permBuffer2[j]
	sb    $t4, 0($t8)			# j is marked with '~'
	
	j     secondNext			# move to next i
	
secondSkipMatch:
	addi  $t1, $t1, 1			# j++
	j     secondFindMatch			# keep searching
	
secondFallBack:
	addi  $t9, $zero, 48			# fall back with ASCII '0'
	add   $t8, $t7, $t0			# t8 = &permut2[i]
	sb    $t9, 0($t8)			# fallback is written
	
secondNext:
	addi  $t0, $t0, 1			# i++
	j     secondMapLoop			# loop to next digit
	
secondPermEnd:
	add   $t8, $t7, $t0			# t8 = &permut2[10]
	sb    $zero, 0($t8)			# null terminate string
	
	jr    $ra				# return to caller
	
# Method: BuildCheckerboard
# Purpose: Constructs a straddling checkerboard from a digit permutation and a 10 character anagram.
#  The checkerboard is a structure where row 0 holds the anagram, row 2 hold the remaining letters,
#  and row 3 holds the remaining letters.
#
# Registers
# 	t0: loop/index counter
#	t1: letter from anagram
#	t2 : target address for storage in checkerboard
#	t3 : digit at permut2[0]
#	t4 : digit at permut2[1]
#	t5 : base address of anagram
#	t6 : base address of permut2
#	t7 : base address of checkerboard
# 	t8 : character counter through anagram
#	t9 : temp address or constant
buildCheckerboard:
	la    $t5, anagram			# t5 = &anagram
	la    $t6, permut2			# t6 = &permu2
	la    $t7, checkerboard		# t7 = &checkerboard
	
	lb    $t3, 0($t6)			# t3 = permut2[0]
	lb    $t4, 1($t6)			# t4 = permut2[1]
	
	addi  $t3, $t3, -48			# convert ASCII '0' + x into integer x
	addi  $t4, $t4, -48			# Convert ASCII '0' + y into integer y
	
	addi  $t0, $zero, 0			# t0 = i = 0
	addi  $t8, $zero, 0			# t8 = charIndex = 0
	
assignAnagram:
	beq   $t0, $t3, skipAnagram		# Skip if curr idx if first space lavel
	beq   $t0, $t4, skipAnagram		# skip if curr idx is second space label
	
	add   $t9, $t5, $t8			# t9 = &anagram[charIndex]
	lb    $t1, 0($t9)			# t1 = anagram[charIndex]
	
	add   $t2, $t7, $t0			# t2 = &checkerboard[column]
	sb    $t1, 0($t2)			# checkerboard[column] = anagram[charIndex]
	
	addi  $t8, $t8, 1			# charIndex++
	
skipAnagram:
	addi  $t0, $t0, 1			# i++
	addi  $t9, $zero, 10			# t9 = 10
	blt   $t0, $t9, assignAnagram		# loop while i < 10
	
	addi  $t0, $zero, 0			# t0 = j = 0
	
assignRow1:
	bge   $t8, 26, doneCheckerboard	# If 18 letters placed, exit
	
	add   $t9, $t5, $t8			# t9 = &anagram[t8]
	lb    $t1, 0($t9)			# t1 = next available letter
	addi  $t8, $t8, 1			# charIndex++
	
	mul   $t2, $t3, 10			# base offset for Row 1
	add   $t2, $t2, $t0			# Column offset: + j
	add   $t2, $t2, $t7			# Full address: checkerboard + row offset
	sb    $t1, 0($t2)			# checkerboard[row1][j] = letter
	
	addi  $t0, $t0, 1			# j++
	addi  $t9, $zero, 10			# t9 = 10
	blt   $t0, $t9, assignRow1		# repeat until j == 10
	
	addi  $t0, $zero, 0			# reset column index for Row 2
	
assignRow2:
	bge   $t8, 36, doneCheckerboard	# If all 26 letters used, exit
	
	add   $t9, $t5, $t8			# t9 = &anagram[t8]
	lb    $t1, 0($t9)			# t1 = next letter
	addi  $t8, $t8, 1			# charIndex++
	
	mul   $t2, $t4, 10			# base offset for Row 2 : rowIndex2 * 10
	add   $t2, $t2, $t0			# column offset : (+ j)
	add   $t2, $t2, $t7			# full adress : checkerboard + row2 offset
	sb    $t1, 0($t2)			# checkerboard[row2][j] = letter
	
	addi  $t0, $t0, 1			# j++
	addi  $t9, $zero, 10			# t9 = 10
	blt   $t0, $t9, assignRow2		# repeat until j = 10

doneCheckerboard:
	jr    $ra				# return to caller
	
# Method:
# 	encodemessage
#
# Purpose:
#	Encode a message by looking up each letter in straddling checkerboard
#	If letter in first row, uses single digit - column
#	if letter is in row 1 or 2, uses two digit value
#
# Registers:
#	t0 : input index - i
#	t1 : checkerboard scan index  - j
#	t2 : curr character from message
#	t3 : curr checkerboard character
#	t4 : ASCII row digit or division quotient
#	t5 : ASCII column digit or division remainder
#	t6 : &message
# 	t7 : &checkerboard
#	t8 : ouput index - k
#	t9 : temp pointer or constant
#
#
encodeMessage:
	la    $t6, message			# t6 = &message
	la    $t7, checkerboard		# t7 = &checkerboard
	la    $a0, encodedMessage		# a- = &encodedMessage
	la    $a1, permut2			# a1 = &permut2
	
	lb    $t4, 0($a1)			# t4 = permut2[0]
	lb    $t5, 1($a1)			# t5 = permut2[1]
	
	addi  $t0, $zero, 0			# i = 0
	addi  $t8, $zero, 0			# k = 0
	
encodeLoop:
	add   $t9, $t6, $t0			# t9 = &message[i]
	lb    $t2, 0($t9)			# t2 = message[i]
	beqz  $t2, encodeEnd			# if null, exit
	
	addi  $t1, $zero, 0			# j = 0
	
findChar:
	add   $t9, $t7, $t1			# t9 = &checkerboard[j]
	lb    $t3, 0($t9)			# t2 = checkerboard[j]
	beq   $t3, $t2, foundMatch		# If match found, exit
	
	addi  $t1, $t1, 1			# j++
	addi  $t9, $zero 100			# t9 = 100
	blt   $t1, $t9, findChar		# If j < 100, keep search
	
	addi  $t0, $t0, 1			# i ++
	j     encodeLoop			# check next char

foundMatch:
	addi  $t9, $zero, 10
	blt   $t1, $t9, oneDigit
	
	div   $t1, $t1, 10
	mflo  $t4
	mfhi  $t5
	
	addi  $t4, $t4, 48
	addi  $t5, $t5, 48
	
	add   $t9, $a0, $t8
	sb    $t4, 0($t9)
	addi  $t8, $t8, 1
	
	add   $t9, $a0, $t8
	sb    $t5, 0($t9)
	addi  $t8, $t8, 1
	
	j nextChar
	
oneDigit:
	addi  $t1, $t1, 48
	add   $t9, $a0, $t8
	sb    $t1, 0($t9)
	add   $t8, $t8, 1
	
nextChar:
	addi  $t0, $t0, 1
	j     encodeLoop
	
encodeEnd:
	add   $t9, $a0, $t8
	sb    $zero, 0($t9)
	
	jr    $ra
	












