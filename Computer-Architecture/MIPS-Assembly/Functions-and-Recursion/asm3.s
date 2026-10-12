# asm3.s - Intro to Functions
# CSC 252 (Spring 2025)
# Author: Eric Romero
# Due: 3/5/2025

# Purpose: 

# Functions:
# 1. strlen()
# 2. gcf(int a, int b)
# 3. bottles(int count, char *thing)
# 4. longestSorted(int *array, int len)
# 5. rotate(int count, int a, int b, int c, int d, int e, int f)

#--------------------------------------------------------------------------
# DATA SEGMENT
#--------------------------------------------------------------------------
.data
bottleOf:		.asciiz		" bottles of " 
onTheWall_1:		.asciiz		" on the wall, "
exclaim:		.asciiz		"!\n"
takeOneDown:		.asciiz		"Take one down, pass it around, "
onTheWall_2:		.asciiz		" on the wall.\n"
noMoreBottles:		.asciiz		"No more bottles of "
onTheWall_3:		.asciiz		" on the wall!\n"
newline:		.asciiz 	"\n"
#--------------------------------------------------------------------------
# TEXT SEGMENT
#--------------------------------------------------------------------------
.text

.globl strlen
.globl gcf
.globl bottles
.globl longestSorted
.globl rotate

#--------------------------------------------------------------------------
# Task: strlen
# Purpose: Computes the length of a null-terminated string
# Parameters:
#	a0: Address of the string
# Returns:
#	v0: Length of the string
# C-Code:
#	int strlen(char *x)
#		int count = 0;
#		while (*x != '\0') {
#			count++;
#			x++;
#		}
#		return count;
#--------------------------------------------------------------------------
strlen:
	addiu $sp, $sp, -24 				# allocate stack space -- default of 24 here
	sw    $fp, 0($sp) 				# save caller's frame pointer
	sw    $ra, 4($sp) 				# save return address
	addiu $fp, $sp, 20 				# setup main's frame pointer
	
	addi  $t0, $zero, 0  				# count = 0
	
strlenLoop:
	lb    $t1, 0($a0)				# curr = A[x]
	beq   $t1, $zero, strlenEnd			# If curr == null, jump to end of function
	
	addi  $t0, $t0, 1				# count++
	addi  $a0, $a0, 1				# curr = A[x++]
	
	j strlenLoop					# repeat loop
	
strlenEnd:
	add   $v0, $zero, $t0				# output = count
	
	lw    $ra, 4($sp)				# get return address from stack
	lw    $fp, 0($sp)				# restore the caller's frame pointer
	addiu $sp, $sp, 24 				# restore the caller's stack pointer
	jr    $ra					# return to caller's code
#--------------------------------------------------------------------------
# Task: gcf
# Purpose: Computes the greatest common factor using recursion
# Parameters:
#	a0: First integer (a)
#	a1: Second integer (b)
# Returns:
#	v0: Greatest common factor of a and b
# Registers:
#
# C-Code:
# 	int gcf(int a, int b)
# 	{
#		if (a < b)
#			swap(a, b)
#		if (b == 1)
#			return 1;
#
#		if (a % b == 0)
#			return b;
#		else
#			return gcf(b, a % b)
#		}
#--------------------------------------------------------------------------
gcf:
	addiu $sp, $sp, -24 				# allocate stack space -- default of 24 here
	sw    $fp, 0($sp) 				# save caller's frame pointer
	sw    $ra, 4($sp) 				# save return address
	addiu $fp, $sp, 20			 	# setup main's frame pointer
	
	slt   $t0, $a0, $a1				# If (a < b), then t0 = 1
	beq   $t0, $zero, checkB			# If (a >= b), do not swap
	
	add   $t1, $zero, $a0				# temp1 = a
	add   $a0, $zero, $a1				# a = b
	add   $a1, $zero, $t1				# b = temp1
	
checkB:
	addi  $t2, $zero, 1				# temp2 = 1
	beq   $a1, $t2, returnOne			# If (b == 1), return 1
	
	div   $a0, $a1					# a divided by b; quotient in LO, remainder in HI
	mfhi  $t3					# temp3 = a % b (remainder
	
	beq   $t3, $zero, returnB			# (if (a % b == 0), return B
	
	add   $a0, $zero, $a1				# a = b
	add   $a1, $zero, $t3				# b = remainder 
	
	jal gcf						# Recursive Call
	
	j gcfEnd					# Ending Call
	
returnOne:
	addi  $v0, $zero, 1				# Return 1
	j gcfEnd					# Ending Call
	
returnB:
	add   $v0, $zero, $a1				# Return B
	
gcfEnd:
	lw    $ra, 4($sp) 				# get return address from stack
	lw    $fp, 0($sp) 				# restore the caller's frame pointer
	addiu $sp, $sp, 24 				# restore the caller's stack pointer
	jr    $ra					# return to caller's code
#--------------------------------------------------------------------------
# Task: bottles
# Purpose: Prints the modified version of the "99 Bottles" song
# Parameters:
#	a0: Number of bottles (integer count)
#	a1: Address of string (thing)
# C-Code:
#	void bottles(int count, char *thing)
#		for (int i = count; i > 0; i--)
#		{
#			printf("%d bottles of %d on the wall, %d bottles of %d!\n", i, thing, i, thing);
#			printf("Take one down, pass it around, %d bottles of %s on the wall.\n", i-1, thing);
# 			printf("\n")
#		}
#		printf("No more bottles of %s on the wall!\n", thing);
#		printf("\n");
#	}
#--------------------------------------------------------------------------
bottles:
	addiu $sp, $sp, -24 				# allocate stack space -- default of 24 here
	sw    $fp, 0($sp) 				# save caller's frame pointer
	sw    $ra, 4($sp) 				# save return address
	addiu $fp, $sp, 20 				# setup main's frame pointer
	
	add   $t0, $zero, $a0				# temp = count
	add   $t1, $zero, $a1				# thing pointer
	
bottlesLoop:
	beq   $t0, $zero, bottlesEnd
	
	# First Line:"%d bottles of %s on the wall, %d bottles of %s!\n"

	addi  $v0, $zero, 1				# print count
	add   $a0, $zero, $t0
	syscall
	
	addi  $v0, $zero, 4				# print " bottles of "
	la    $a0, bottleOf
	syscall
	
	addi  $v0, $zero, 4				# print string (thing)
	add   $a0, $zero, $t1
	syscall
	
	addi  $v0, $zero, 4				# print " on the wall, "
	la    $a0, onTheWall_1
	syscall
	
	addi  $v0, $zero, 1				# print count
	add   $a0, $zero, $t0
	syscall
	
	addi  $v0, $zero, 4				# print " bottles of "
	la    $a0, bottleOf
	syscall
	
	addi  $v0, $zero, 4				# print string (thing)
	add   $a0, $zero, $t1
	syscall
	
	addi  $v0, $zero, 4				# "!\n"
	la    $a0, exclaim
	syscall
	
	# Line 2: "Take one down, pass it around, %d bottles of %s on the wall.\n"

	addi  $v0, $zero, 4				# print "Take one down, pass it around, "
	la    $a0, takeOneDown
	syscall
	
	addi  $t2, $t0, -1				# temp = count - 1
	
	addi  $v0, $zero, 1				# print (count - 1)
	add   $a0, $zero, $t2
	syscall
	
	addi  $v0, $zero, 4				# print " bottles of "
	la    $a0, bottleOf
	syscall
	
	addi  $v0, $zero, 4				# print string (thing)
	add   $a0, $zero, $t1
	syscall
	
	addi  $v0, $zero, 4				# print " on the wall. \n"
	la    $a0, onTheWall_2
	syscall
	
	addi  $v0, $zero, 4				# print "\n"
	la    $a0, newline
	syscall
	
	addi  $t0, $t0, -1				# count--
	
	j bottlesLoop					# repeat loop

bottlesEnd:
	# Line 3: "No more bottles of %s on the wall!\n"
	addi  $v0, $zero, 4				# print "No more bottles of "
	la    $a0, noMoreBottles
	syscall
	
	addi  $v0, $zero, 4				# print string (thing)
	add   $a0, $zero, $t1
	syscall
	
	addi  $v0, $zero, 4				# print " on the wall!\n"
	la    $a0, onTheWall_3
	syscall
	
	addi  $v0, $zero, 4				# print "\n"
	la    $a0, newline
	syscall
	
	lw    $ra, 4($sp) 				# get return address from stack
	lw    $fp, 0($sp) 				# restore the caller's frame pointer
	addiu $sp, $sp, 24 				# restore the caller's stack pointer
	jr    $ra 					# return to caller's code
#--------------------------------------------------------------------------
# Task: longestSorted
# Purpose: Determines the longest ascending sequence in an integer array.
# Parameters:
#	a0: Address of the array
#	a1: Length of the array
# Returns:
#	v0: Length of the longest sorted sequence
# C-Code:
#	int longestSorted(int *array, int len) {
#		if (len == 0) {
#			return 0;
#		}
#
#		int maxLen = 1;
#		int currLen = 1;
#
#		for (int i = 1; i < len; i++) {
#			if (array[i] >= array[i - 1) {
#				currLen++;
#			} else {
#				currLen = 1;
#			}
#
#			if (currLen > maxLen) {
#				maxLen = currLen;
#			}
#		}
#		return maxLen;
#--------------------------------------------------------------------------
longestSorted:
	addiu $sp, $sp, -24 				# allocate stack space -- default of 24 here
	sw    $fp, 0($sp)				# save caller's frame pointer
	sw    $ra, 4($sp) 				# save return address
	addiu $fp, $sp, 20 				# setup main's frame pointer
	
	beq   $a1, $zero, longSortReturn0		# If len == 0, return 0
	
	addi  $v0, $zero, 1				# Longest run = 1
	addi  $t0, $zero, 1				# Current run = 1
	addi  $t1, $zero, 1				# index = i = 1
	
longSortLoop:
	slt   $t5, $t1, $a1				# If (i < len), then t5 = 1
	beq   $t5, $zero, longSortLoopEnd		# If ((i < len) == 0), then exit
	
	addi  $t6, $t1, -1				# t6 = i - 1
	sll   $t6, $t6, 2				# t6 = (i - 1) * 4
	add   $t6, $t6, $a0				# t6 - &(array[i - 1]
	lw    $t3, 0($t6)				# t3 = array[i - 1]
	
	sll   $t7, $t1, 2				# t7 = i * 4
	add   $t7, $t7, $a0				# t7 = &(array[i]
	lw    $t4, 0($t7)				# t4 = array[i]
	
	slt   $t8, $t4, $t3				# if (array[i] < array[i - 1], then t8 = 1
	bne   $t8, $zero, longSortLoopReset		# If ((array[i] < array[i - 1]) = 1), reset current
	
	addi  $t0, $t0, 1				# current = current++
	
longSortUpdate:
	slt   $t9, $v0, $t0				# If (longestRun < currentRun), t9 = 1
	beq   $t9, $zero, longSortContinueLoop		# If ((longestRun < currentRun) == 0), continue loop
	add   $v0, $zero, $t0				# output = current run
	
longSortContinueLoop:
	addi  $t1, $t1, 1				# index = index++
	j     longSortLoop				# jump to start loop again
	
longSortLoopReset:
	addi  $t0, $zero, 1				# current = 1
	j     longSortUpdate				# jump to update longest run
	
longSortLoopEnd:
	j longSortEpilogue				# jump to epilogue
	
longSortReturn0:
	addi  $v0, $zero, 0				# output = longest run = 0

longSortEpilogue:
	lw    $ra, 4($sp) 				# get return address from stack
	lw    $fp, 0($sp) 				# restore the caller's frame pointer
	addiu $sp, $sp, 24 				# restore the caller's stack pointer
	jr    $ra 					# return to caller's code
#--------------------------------------------------------------------------
# Task: rotate
# Purpose: Rotates six integer values
# Parameters:
#	a0: Count of rotations
# Returns:
#	v0: Computed result after applying util()
# C-Code:
#	int rotate(int count, int a, int b, int c, int d, int e, int f)
#		int retval = 0
#		
#		for (int i = 0; i < count; i++)
#		{
#			retval += util(a, b, c, d, e, f);
#		
#			int tmp = a;
#			a = b;
#			b = c;
#			c = d;
#			d = e;
#			e = f;
#			f = tmp;
#		}
#		
#		return retval;
#	}
#--------------------------------------------------------------------------
					# return to caller's code
rotate:
	addiu $sp, $sp, -40 				# allocate stack space -- default of 24 here
	sw    $fp, 0($sp)				# save caller's frame pointer
	sw    $ra, 4($sp) 				# save return address
	addiu $fp, $sp, 36 				# setup main's frame pointer
	
	sw    $s0, 8($sp)
	sw    $s1, 12($sp)
	sw    $s2, 16($sp)
	sw    $s3, 20($sp)
	sw    $s4, 24($sp)
	sw    $s5, 28($sp)
	sw    $s6, 32($sp)
	sw    $s7, 36($sp)
	
	addu   $s1, $zero, $a0
	addu   $s2, $zero, $a1
	addu   $s3, $zero, $a2
	addu   $s4, $zero, $a3
	
	lw    $s5, -8($fp)
	lw    $s6, -4($fp)
	lw    $s7, 0($fp)
	
	addu   $s0, $zero, $zero
	
rotateLoop:
	beq   $s1, $zero, rotateEnd
	
	addu   $a0, $zero, $s2
	addu   $a1, $zero, $s3
	addu   $a2, $zero, $s4
	addu   $a3, $zero, $s5
	
	addiu $sp, $sp, -8
	sw    $s6, 4($sp)
	sw    $s7, 0($sp)
	
	jal   util

	addiu $sp, $sp, 8
	
	addu   $s0, $s0, $v0
	
	addu   $t0, $zero, $s2
	addu   $s2, $zero, $s3
	addu   $s3, $zero, $s4
	addu   $s4, $zero, $s5
	addu   $s5, $zero, $s6
	addu   $s6, $zero, $s7
	addu   $s7, $zero, $t0
	
	addiu  $s1, $s1, -1
	j     rotateLoop

rotateEnd:
	addu   $v0, $zero, $s0
	
	lw    $s0, 8($sp)
	lw    $s1, 12($sp)
	lw    $s2, 16($sp)
	lw    $s3, 20($sp)
	lw    $s4, 24($sp)
	lw    $s5, 28($sp)
	lw    $s6, 32($sp)
	lw    $s7, 36($sp)
	
	lw    $ra, 4($sp) 				# get return address from stack
	lw    $fp, 0($sp) 				# restore the caller's frame pointer
	addiu $sp, $sp, 40 				# restore the caller's stack pointer
	jr    $ra 					# return to caller's code
