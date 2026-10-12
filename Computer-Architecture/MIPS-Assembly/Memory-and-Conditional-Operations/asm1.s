# Author:     Eric Romero
# Assignment: ASM1
# Course:     CSC 252
# Purpose: This program perfoms basic tasks such as comparing values, checking order, reversing data, and printing output.
# This project emphasizes concepts such as memory access and conditional branching.

.data
EQUALS_MSG:
	.asciiz "EQUALS\n"

NOTHING_EQUALS_MSG:
	.asciiz "NOTHING EQUALS\n"

ASCENDING_MSG:
	.asciiz "ASCENDING\n"

DESCENDING_MSG:
	.asciiz "DESCENDING\n"

ALL_EQUAL_MSG:
	.asciiz "ALL EQUAL\n"

UNORDERED_MSG:
	.asciiz "UNORDERED\n"

REVERSE_MSG:
	.asciiz "REVERSE\n"

NEWLINE_MSG:
	.asciiz "\n"

RED_LABEL:
	.asciiz "red: "

ORANGE_LABEL:
	.asciiz "orange: "

YELLOW_LABEL:

	.asciiz "yellow: "

GREEN_LABEL:
	.asciiz "green: "

BLUE_LABEL:
	.asciiz "blue: "

PURPLE_LABEL:
	.asciiz "purple: "

.text

.globl studentMain

studentMain:

	addiu $sp, $sp, -24			# allocate stack space -- default of 24 here
	sw    $fp, 0($sp)			# save caller's frame pointer
	sw    $ra, 4($sp)			# save return address
	addiu $fp, $sp, 20			# setup main's frame pointer

	la    $s0, red				# s0 = &red
	lw    $s0, 0($s0)			# s0 = red

	la    $s1, orange			# s1 = &orange
	lw    $s1, 0($s1)			# s1 = orange

	la    $s2, yellow			# s2 = &yellow
	lw    $s2, 0($s2)			# s2 = yellow

	la    $s3, green			# s3 = &green
	lw    $s3, 0($s3)			# s3 = green

	la    $s4, blue				# s4 = &blue
	lw    $s4, 0($s4)			# s4 = blue

	la    $s5, purple			# s5 = &purple
	lw    $s5, 0($s5)			# s5 = purple

	la    $t0, equals			# t0 = &equals
	lw    $t0, 0($t0)			# t0 = equals

	la    $t1, order			# t1 = &order
	lw    $t1, 0($t1)			# t1 = order

	la    $t2, reverse			# t2 = &reverse
	lw    $t2, 0($t2)			# t2 = reverse
	
	la    $t3, print			# t3 = &print
	lw    $t3, 0($t3)			# t3 = print

	beq   $t0, $zero, orderTask		# If (equals == 0), then jump to next task

	beq   $s0, $s1, printEqual		# red == orange

	beq   $s0, $s2, printEqual		# red == yellow

	beq   $s0, $s3, printEqual		# red == green

	beq   $s1, $s2, printEqual		# orange == yellow

	beq   $s1, $s3, printEqual		# orange == green

	beq   $s2, $s3, printEqual		# yellow == green
	
	addi  $v0, $zero, 4			# print 'NOTHING EQUALS'
	la    $a0, NOTHING_EQUALS_MSG		
	syscall
	
	j     orderTask

printEqual:
	addi  $v0, $zero, 4			# print 'EQUALS'
	la    $a0, EQUALS_MSG			
	syscall

orderTask:
	beq   $t1, $zero, reverseTask		# If (order == 0), then jump to next task
	
	beq   $s0, $s1, equality1		# If (red == orange), then jump to next value
	j ascending
	
equality1:
	beq   $s1, $s2, equality2		# If (orange == yellow), then jump to next value
	j ascending
	
equality2:
	beq   $s2, $s3, equality3		# If (yellow == green), then jump to next value
	j ascending
	
equality3:
	beq   $s3, $s4, equality4		# If (green == blue), then jump to next value
	j ascending
	
equality4:
	beq   $s4, $s5, printAllEqual		# If (blue == purple), then jump to print
	j ascending
	
printAllEqual:
	addi  $v0, $zero, 4			# print 'ALL EQUAL'
	la    $a0, ALL_EQUAL_MSG
	syscall
	
	j reverseTask
	
ascending:
	slt   $t0, $s1, $s0			# t0 = orange < red
	bne   $t0, $zero, descending		# If (t0 == 0), then not ascending
	
	slt   $t0, $s2, $s1			# t0 = yellow < orange
	bne   $t0, $zero, descending		# If (t0 == 0), then not ascending
	
	slt   $t0, $s3, $s2			# t0 = green < yellow
	bne   $t0, $zero, descending		# If (t0 == 0), then not ascending
	
	slt   $t0, $s4, $s3			# t0 = blue < green
	bne   $t0, $zero, descending		# If (t0 == 0), then not ascending
	
	slt   $t0, $s5, $s4			# t0 = purple < blue
	bne   $t0, $zero, descending		# If (t0 == 0), then not ascending
	
	j printAscend
	
printAscend:
	addi  $v0, $zero, 4			# print 'ASCENDING
	la    $a0, ASCENDING_MSG
	syscall
	
	j reverseTask

descending:
	slt   $t0, $s0, $s1			# t0 = red < orange
	bne   $t0, $zero, printUnordered	# If (t0 == 0), then unordered
	
	slt   $t0, $s1, $s2			# t0 = orange < yellow
	bne   $t0, $zero, printUnordered	# If (t0 == 0), then unordered
	
	slt   $t0, $s2, $s3			# t0 = yellow < green
	bne   $t0, $zero, printUnordered	# If (t0 == 0), then unordered
	
	slt   $t0, $s3, $s4			# t0 = green < blue
	bne   $t0, $zero, printUnordered	# If (t0 == 0), then unordered
	
	slt   $t0, $s4, $s5			# t0 = blue < purple
	bne   $t0, $zero, printUnordered	# If (t0 == 0), then unordered
	
	j printDescend
	
printDescend:
	addi  $v0, $zero, 4			# print 'DESCENDING'
	la    $a0, DESCENDING_MSG
	syscall
	
	j reverseTask
	
printUnordered:
	addi  $v0, $zero, 4			# print 'UNORDERED'
	la    $a0, UNORDERED_MSG
	syscall

reverseTask:
	beq   $t2, $zero, printTask		# If (reverse == 0), then jump
	
	la    $t0, red				# t0 = &red
	sw    $s5, 0($t0)			# red = purple

	la    $t0, orange			# t0 = orange
	sw    $s4, 0($t0)			# orange = blue

	la    $t0, yellow			# t0 = yellow
	sw    $s3, 0($t0)			# yellow = green

	la    $t0, green			# t0 = green
	sw    $s2, 0($t0)			# green = yellow

	la    $t0, blue				# t0 = blue
	sw    $s1, 0($t0)			# blue = orange

	la    $t0, purple			# t0 = purple
	sw    $s0, 0($t0)			# purple = red

	add   $t7, $s0, $zero			# temp = red
	
	add   $s0, $s5, $zero			# red = purple

	add   $s5, $t7, $zero			# purple = temp

	add   $t7, $s1, $zero			# temp = orange

	add   $s1, $s4, $zero			# orange = blue

	add   $s4, $t7, $zero			# blue = temp

	add   $t7, $s2, $zero			# temp = yellow

	add   $s2, $s3, $zero			# yellow = green

	add   $s3, $t7, $zero			# green = temp

	addi  $v0, $zero, 4			# print 'REVERSED'
	la    $a0, REVERSE_MSG
	syscall

	j     printTask


printTask:
	beq   $t3, $zero, epilogue		# If (print == 0), then jump to epilogue

	addi  $v0, $zero, 4			# Print "red: "
	la    $a0, RED_LABEL
	syscall

	addi  $v0, $zero, 1			# Print s0
	addu  $a0, $zero, $s0
	syscall

	addi  $v0, $zero, 4			# Print \n
	la    $a0, NEWLINE_MSG
	syscall

	addi  $v0, $zero, 4			# Print "orange: "
	la    $a0, ORANGE_LABEL
	syscall

	addi  $v0, $zero, 1			# Print s1
	addu  $a0, $zero, $s1
	syscall

	addi  $v0, $zero, 4			# Print \n
	la    $a0, NEWLINE_MSG
	syscall

	addi  $v0, $zero, 4			# Print "yellow: "
	la    $a0, YELLOW_LABEL
	syscall

	addi  $v0, $zero, 1			# Print s2
	addu  $a0, $zero, $s2
	syscall

	addi  $v0, $zero, 4			# Print \n
	la    $a0, NEWLINE_MSG
	syscall

	addi  $v0, $zero, 4			# Print "green: "
	la    $a0, GREEN_LABEL
	syscall

	addi  $v0, $zero, 1			# Print s3
	addu  $a0, $zero, $s3
	syscall

	addi  $v0, $zero, 4			# Print \n
	la    $a0, NEWLINE_MSG
	syscall

	addi  $v0, $zero, 4			# Print "blue: "
	la    $a0, BLUE_LABEL
	syscall

	addi  $v0, $zero, 1			# Print s4
	addu  $a0, $zero, $s4
	syscall

	addi  $v0, $zero, 4			# Print \n
	la    $a0, NEWLINE_MSG
	syscall

	addi  $v0, $zero, 4			# Print "purple: "
	la    $a0, PURPLE_LABEL
	syscall

	addi  $v0, $zero, 1			# Print s5
	addu  $a0, $zero, $s5
	syscall

	addi  $v0, $zero, 4			# Print \n
	la    $a0, NEWLINE_MSG
	syscall

	j     epilogue
	
epilogue:
	lw    $ra, 4($sp)			# get return address from stack
	lw    $fp, 0($sp)			# restore the caller's frame pointer
	addiu $sp, $sp, 24			# restore the caller's stack pointer
	jr    $ra				# return to caller's code