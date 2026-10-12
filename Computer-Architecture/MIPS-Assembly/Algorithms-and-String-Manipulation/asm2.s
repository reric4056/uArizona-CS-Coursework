# Eric Romero
# asm2.s
# CSC 252
# Purpose: This MIPS assembly project consolidates loops, arrays, and string manipulation by applying
# tasks like creating Fibonacci sequences, drawing a square, finding array order, word count, and reversing strings.
# Each task is managed via a variable to make sure only functions that are suitable execute. the project combines
# low-level programming skill with a focus on memory management, branching, and certain output formatting

.data
NEWLINE:			.asciiz 		"\n"
FIBONACCI_LABEL:		.asciiz 		"Fibonacci Numbers:\n"
FIBONACCI_ZERO:			.asciiz 		"  0: 1\n"
FIBONACCI_ONE:			.asciiz 		"  1: 1\n"
TWO_SPACE:			.asciiz 		"  "
COLON_SPACE:			.asciiz 		": "
ASCEND_MSG:			.asciiz 		"Run Check: ASCENDING\n"
DESCEND_MSG:			.asciiz 		"Run Check: DESCENDING\n"
NEITHER_MSG:			.asciiz 		"Run Check: NEITHER\n"
WORD_COUNT_MSG:			.asciiz 		"Word Count: "
REV_STRING_MSG:			.asciiz 		"String successfully swapped!\n"
	
.text
.globl studentMain

studentMain:
	addiu $sp, $sp, -24				# allocate stack space -- default of 24 here
	sw    $fp, 0($sp)				# save caller's frame pointer
	sw    $ra, 4 ($sp)				# save return address
	addiu $fp, $sp, 20				# setup main's frame pointer
	
	la    $t0, fib					# t0 = &fib
	lw    $t0, 0($t0)				# t0 = fib
	
# -----------------------------------------------------------------------------------------------------------------
# Task 1: Fibonacci
# This function calculates and prints the Fibonacci sequence up to a given index
#
# Registers:
# 	$t0 - Control Variable (input for number of Fibonacci numbers to compute
# 	$t1 - Previous Fibonacci number
# 	$t2 - Two Fibonacci numbers before
#	$t3 - Current Fibonacci index
#	$s0 - Used to check loop condition
#	$s1 - Holds the computed Fibonacci number
#
fibonacciTask:
	beq   $t0, $zero, squareTask			# If (fib == 0), jump to Square task
	
	# Print Fibonacci sequence header
	addi  $v0, $zero, 4				# Print str
	la    $a0, FIBONACCI_LABEL			# str = "Fibonacci Numbers:\n"
	syscall
	
	# Print first two numbers
	addi  $v0, $zero, 4				# Print str
	la    $a0, FIBONACCI_ZERO			# str = "  0: 1\n"
	syscall
	
	addi  $v0, $zero, 4				# Print str
	la    $a0, FIBONACCI_ONE			# str = "  1: 1\n"
	syscall
	
	# Initialize Fibonacci sequence variables
	addi  $t1, $zero, 1				# t1 = prev = 1
	addi  $t2, $zero, 1				# t2 = beforeThat = 1
	addi  $t3, $zero, 2				# t3 = n = 2
	
fibonacciLoop:		
	slt   $s0, $t0, $t3				# If (fib < n), s0 = 1		
	bne   $s0, $zero, fibonacciEnd			# If ((fib < n) != 0), jump
		
	add   $s1, $t1, $t2				# s1 = cur = prev + beforeThat
	
	# Print number formatting
	addi  $v0, $zero, 4				# Print str
	la    $a0, TWO_SPACE				# str = "  "
	syscall
	
	# Print index
	addi  $v0, $zero, 1				# Print int
	add   $a0, $zero, $t3				# int = n
	syscall
	
	addi  $v0, $zero, 4				# Print str 
	la    $a0, COLON_SPACE				# str = ": "
	syscall
	
	# Print Fibonacci value
	addi  $v0, $zero, 1				# Print int
	add   $a0, $zero, $s1				# int = cur
	syscall
	
	addi  $v0, $zero, 4				# Print str
	la    $a0, NEWLINE				# str = "\n"
	syscall
	
	# Increment Index
	addi  $t3, $t3, 1				# n++
	#Shift values
	add   $t2, $t1, $zero				# beforeThat = prev
	add   $t1, $s1, $zero 				# prev = curr
	
	# Repeat Loop
	j     fibonacciLoop				# next iteration
	
fibonacciEnd:
	addi $v0, $zero, 4				# Print str
	la   $a0, NEWLINE				# str = "\n"
	syscall
	
# -----------------------------------------------------------------------------------------------------------------
# Task 2: Square
# This function prints a square of size 'square_size', filled with 'square_fill.
# It uses ASCII characters '+' & '-' for borders and '|' for the sides
#
# Registers:
# 	$t0 - Control Variable
#	$t1 - Character for square fill
#	$t2 - Size of the square
#	$t3 - Row counter
#	$t4 - Temporary storage for comparisons
#	$t5 - Column counter
#
squareTask:
	# Load square control variable
	la    $t0, square				# t0 = &square
	# Retrieve value
	lw    $t0, 0($t0)				# t0 = square
	
	beq   $t0, $zero, runCheckTask			# If (square == 0), jump to Run Check task
	
	# Load fill character
	la    $t1, square_fill				# t1 = &square_fill
	lb    $t1, 0($t1)				# t1 = square_fill
	
	# Load square size
	la    $t2, square_size				# t2 = &square_size
	lw    $t2, 0($t2)				# t2 = square_size
	
	# Initialize row counter
	addi  $t3, $zero, 0				# t3 = row = 0
	
	# for ( int row = 0; row < square_size; row++)
squareLoop:
	slt   $t4, $t3, $t2				# If (row < square_size), then t4 = 1
	beq   $t4, $zero, squareEnd			# If (row < square_size = 0, jump ahead
	
	# First row: border
	beq   $t3, $zero, borderStart			# If (row == 0), go to borderStart
	addi  $t4, $t2, -1				# t4 = square_size - 1
	# Last row: border
	beq   $t3, $t4, borderStart			# If (row == square_size - 1), go to BorderStart
	
	# Otherwise: print middle
	j     middleStart				# Otherwise, go to middle row
	
	#Print Border row
borderStart:
	addi  $v0, $zero, 11				# Print char
	addi  $a0, $zero, 43				# char = '+'
	syscall
	
	# Column counter
	addi  $t5, $zero, 1				# t5 = i = 1
	
borderMiddle:
	addi  $t4, $t2, -1
	slt   $t4, $t5, $t4				# If (i < square_size - 1), t4 = 1
	beq   $t4, $zero, borderEnd			# If (i >= square_size), go to BorderEnd
	
	addi  $v0, $zero, 11				# Print char
	addi  $a0, $zero, 45				# char = '-'
	syscall
	
	addi  $t5, $t5, 1				# i++
	j     borderMiddle				# Otherwise, repeat loop
	
borderEnd:
	addi  $v0, $zero, 11				# Print char
	addi  $a0, $zero,43				# char = '+'
	syscall
	
	j     printNewline				# Jump to printNewline
	
	# Print Middle Row
middleStart:
	addi  $v0, $zero, 11				# Print char
	addi  $a0, $zero, 124				# char = '|'
	syscall
	
	addi  $t5, $zero, 1				# t5 = i = 1
	
middleMiddle:
	addi  $t4, $t2, -1
	slt   $t4, $t5, $t4				# If (i < square_size), t4 = 1
	beq   $t4, $zero, middleEnd			# if (i >= square_size), jump to middleEnd
	
	addi  $v0, $zero, 11				# Print char
	add   $a0, $zero, $t1				# char = square_fill
	syscall
	
	addi  $t5, $t5, 1				# i++
	j     middleMiddle				# Repeat loop
	
middleEnd:
	addi  $v0, $zero, 11				# Print char
	addi  $a0, $zero, 124				# char = '|'
	syscall

printNewline:
	addi  $v0, $zero, 4				# Print str
	la    $a0, NEWLINE				# str = '\n'
	syscall
	
	# Increment row
	addi  $t3, $t3, 1				# row++
	j     squareLoop				# Repeat main square loop
	
squareEnd:
	addi  $v0, $zero, 4				# Print str
	la    $a0, NEWLINE				# str = "\n"
	syscall
	
	j     runCheckTask				# Jump to next task
	
# -----------------------------------------------------------------------------------------------------------------
# Task 3: Run Check 
# This function checks whether an integer array is sorted in ascending,
# descending, or neither order, and prints the corresponding message
#
# Registers:
# 	$t0 - Run Check control variable
# 	$t1 - Integer array length
# 	$t2 - Base address of integer array
#	$t3 - Previous element in array comparison
#	$t4 - Pointer to next element in array
# 	$t5 - Loop index
#	$t6 - Ascending Flag
#	$t7 - Descending Flag
#	$t8, $t9 - Temporary storage for conditions
# 	$s0 - Used for comparison results
#
runCheckTask:
	# Load control variable
	la    $t0, runCheck				# t0 = &runCheck
	lw    $t0, 0($t0)				# t0 = runCheck
	beq   $t0, $zero, countWordsTask		# If (runCheck == 0), jump to Count Words task
	
	# Load array length
	la    $t1, intArray_len			# t1 = &intArray_len
	lw    $t1, 0($t1)				# t1 = intArray_len
	
	addi  $t8, $zero, 0				# t8 = 0 
	beq   $t1, $t8, printBoth			# If (intArray_len == 0), printBoth
	
	addi  $t8, $zero, 1				# t8 = 1
	beq   $t1, $t8, printBoth			# If (intArray_len == 1), printBoth
	
	# Load base address of array
	la    $t2, intArray				# t2 = &intArray
	# Load first element as previous value
	lw    $t3, 0($t2)				# t3 = intArray[0] (prev)
	
	addi  $t4, $t2, 4				# t4 = pointer to next element
	# Loop Index
	addi  $t5, $zero, 1				# t5 = i = 1
	
	addi  $t6, $zero, 1				# t6 = ascendFlag = 1
	addi  $t7, $zero, 1				# t7 = descendFlag = 1
	
runCheckLoop:
	slt   $t8, $t5, $t1				# If (i < intArrayLen), t8 = 1
	beq   $t8, $zero, evaluateAscend		# If (i >= intArrayLen), jump to evaluate if ascending
	
	# Load current element
	lw    $t9, 0($t4)				# t9 = current element (curr)
	# Move to next element
	addi  $t4, $t4, 4				# t4 = next element
	
	slt   $s0, $t9, $t3				# If (curr < prev), s0 = 1
	bne   $s0, $zero, flagNotAscending		# If (curr < prev), not ascending
	
	slt   $s0, $t3, $t9				# If (prev < curr), s0 = 1
	bne   $s0, $zero, flagNotDescending		# If (prev < curr), not descending
	
	j     incrementIndex
	
flagNotAscending:
	# Disable ascending flag
	addi  $t6, $zero, 0				# t6 = ascendFlag = 0
	j     incrementIndex				# Jump to move to next number
	
flagNotDescending:
	# Disable descending flag
	addi  $t7, $zero, 0				# t7 = descendFlag = 0
	j     incrementIndex				# Jump to move to next number
	
incrementIndex:
	# Update previous value
	add   $t3, $zero, $t9				# Prev = curr
	# Increment index
	addi  $t5, $t5, 1				# i++
	j     runCheckLoop				# Repeat main loop
	
evaluateAscend:
	beq   $t6, $zero, evaluateDescend		# If (ascendFlag == 0), jump to check Descend
	
printAscend:
	addi  $v0, $zero, 4				# Print str
	la    $a0, ASCEND_MSG				# str = "Run Check: ASCENDING\n"
	syscall
	
	beq   $t7, $zero, runCheckEnd			# If DecendFlag == 0, jump to newline
	
evaluateDescend:
	beq   $t7, $zero, evaluateNeither		# If (DescendFlag == 0), jump to evaluate if Neither	
	
printDescend:
	addi  $v0, $zero, 4				# Print str
	la    $a0, DESCEND_MSG				# str = "Run Check: DESCENDING\n"
	syscall
	
	j     runCheckEnd				# Jump to print newline
	
evaluateNeither:
	beq   $t6, $zero, printNeither			# If (AscendFlag == 0), then print Neither message
	j     runCheckEnd				# Jump to print newline

printNeither:
	addi  $v0, $zero, 4				# Print str
	la    $a0, NEITHER_MSG				# str = "Run Check: NEITHER\n"
	syscall
	
	j     runCheckEnd
	
printBoth:
	addi  $v0, $zero, 4				# Print str
	la    $a0, ASCEND_MSG				# str = "Run Check: ASCENDING\n"
	syscall
	
	addi  $v0, $zero, 4				# Print str
	la    $a0, DESCEND_MSG				# str = "Run Check: DESCENDING\n
	syscall
	
	j     runCheckEnd				# Jump to print newline
	
runCheckEnd:
	addi  $v0, $zero, 4				# Print str
	la    $a0, NEWLINE				# str = "\n"
	syscall
	
	j     countWordsTask				# jump to next task
		
# -----------------------------------------------------------------------------------------------------------------
# Task 4 : Word Count
# This function counts the number of words in a string. Words are separated by spaces (' ')
# or newlines ('\n'). A word is detected when a non-space character follows a space
#
# Registers:
#	$t0 - Control variable
#	$t1 - Pointer to string
#	$t2 - Word count counter
#	$t3 - Word Flag (indicates if currently in a word
#	$t4 - Current character being processed
#	$t5 - Temporary storage for space and newline comparisons
#
countWordsTask:
	# Load control variable
	la    $t0, countWords				# t0 = &countWords
	lw    $t0, 0($t0)				# t0 = countWords
	
	beq   $t0, $zero, revStringTask		# If (countWords == 0), jump to revString task
	
	# Load address of string
	la    $t1, str					# t1 = &str
	
	addi  $t2, $zero, 0				# t2 = wordCount = 0
	addi  $t3, $zero, 0				# t3 = wordFlag = 0
	
countWordsLoop:
	# Load current character
	lb    $t4, 0($t1)				# t4 = curr char in str
	beq   $t4, $zero, printWordCount		# If null terminator, end loop
	
	addi  $t5, $zero, 32				# t5 = ' '
	beq   $t4, $t5, spaceCase			# If curr == ' ', jump to spaceCase
	
	addi  $t5, $zero, 10				# t5 = '\n'
	beq   $t4, $t5, spaceCase			# If curr == '\n', jump to spaceCase
	
	beq   $t3, $zero, newWord			# If wordFlag == 0, jump to next word
	j     nextCharacter				# Continue loop
	
spaceCase:
	# Reset word flag
	addi  $t3, $zero, 0				# Set wordFlag = 0
	j     nextCharacter				# Jump to next character
	
newWord:
	# Increment word count and set word flag
	addi  $t2, $t2, 1				# wordCount++
	addi  $t3, $zero, 1				# Set wordFlag = 1
	
nextCharacter:
	addi  $t1, $t1, 1				# Move to next character
	j     countWordsLoop				# Repeat loop
	
printWordCount:
	addi  $v0, $zero, 4				# Print str
	la    $a0, WORD_COUNT_MSG			# str = "Word Count: "
	syscall
	
	addi  $v0, $zero, 1				# Print int
	add   $a0, $zero, $t2				# int = wordCount
	syscall
	
	addi  $v0, $zero, 4				# Print str
	la    $a0, NEWLINE				# str = "\n"
	syscall
	
	addi  $v0, $zero, 4				# Print str
	la    $a0, NEWLINE				# str = "\n"
	syscall
	
	j     revStringTask
# -----------------------------------------------------------------------------------------------------------------
# Task 4 : Reverse String
# This function reverses a given string in-place by swapping characters
# from the start and end, moving toward the center
#
# Registers:
#	$t0 - Control variable
#	$t1 - Pointer to start of the string (head)
#	$t2 - Pointer to end of the string (tail
#	$t3 - Current character beign procssed
#	$t4 - Temporary head pointer
#	$t5 - Condition flag for swap loop
#	$t6 - Character at head position
# 	$t7 - Character at tail position
#
revStringTask:
	# Load control variable
	la    $t0, revString				# t0 = &revString
	lw    $t0, 0($t0)				# t0 = revString
	
	beq   $t0, $zero, epilogue			# If (revString == 0), jump to epilogue
	
	# Load address of string
	la    $t1, str					# t1 = &str
	# Initialize tail pointer to start of string
	add   $t2, $t1, $zero				# t2 = &str (for tail)
	
findTail:
	# Load current character
	lb    $t3, 0($t2)				# t3 = curr
	beq   $t3, $zero, adjustTail			# If null terminator, exit loop
	addi  $t2, $t2, 1				# Move to next character
	j     findTail					# Jump to findTail
	
adjustTail:
	# Move tail and set head
	addi  $t2, $t2, -1				# Tail = last valid character
	add   $t4, $zero, $t1				# Head = Beginning of string
	
swapLoop:
	slt   $t5, $t4, $t2				# If head < tail, continue swapping
	beq   $t5, $zero, printSuccess			# If head >= tail, exit loop
	
	lb    $t6, 0($t4)				# Load head character
	lb    $t7, 0($t2)				# Load tail character
	
	sb    $t7, 0($t4)				# Store tail character at head
	sb    $t6, 0($t2)				# Store head character at tail
	
	addi  $t4, $t4, 1				# head++
	addi  $t2, $t2, -1				# tail--
	
	j     swapLoop					# repeat swaps
	
printSuccess:
	addi  $v0, $zero, 4				# Print str
	la    $a0, REV_STRING_MSG			# str = "String successfully swapped!\n"
	syscall
		
	addi  $v0, $zero, 4				# Print str
	la    $a0, NEWLINE				# str = "\n"
	syscall
	
	j     epilogue					# Jump to epilogue
									
epilogue:
	lw    $ra, 4($sp)				# get return address from stack	
	lw    $fp, 0($sp)				# restore the caller's frame pointer
	addiu $sp, $sp, 24				# restore the caller's stack pointer
	jr    $ra					# return to caller's code
	
	
