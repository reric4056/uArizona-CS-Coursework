# Name: asm4.s
# Author: Eric Romero
# Course: CSC 252, Spring 2025
# Purpose: Implementation of functions to initialize, debug, move, 
#  and manipulate Turtle objects. The turtle struct contains fields 
#  for position, direction, a name pointer, and an odometer.
#
# Functions to Implement:
#	turtle_init:                      Initialize a Turtle struct
#	turtle_debug:                     Pring the Turtle's fields
# 	turtle_turnLeft/turtle_turnRight: Update direction
# 	turtle_move:                      Move the Turtle and update its odometer
#	turtle_searchName:                Search an array of Turtles by name
#	turtle_sortByX_indirect:          Sort pointers to Turtles by x-coordinate

.data
turtleMsg:		.asciiz 	"Turtle \""
quoteMsg: 		.asciiz 	"\"\n"
posMsg:      		.asciiz 	"  pos "
commaMsg:    		.asciiz 	","
dirMsg:      		.asciiz 	"  dir "
odomMsg:     		.asciiz 	"  odometer "
newline:      		.asciiz 	"\n"
northMsg:    		.asciiz 	"North"
eastMsg:     		.asciiz 	"East"
southMsg:    		.asciiz 	"South"
westMsg:     		.asciiz 	"West"

.text
.globl turtle_init
#-------------------------------------------------------------------------------------------------
# Name: turtle_init(Turtle *obj, char *name)
# Purpose: Initializes the Turtle struct pointed to by 'obj' with default values
#	(x = 0, y = 0, dir = 0, odometer = 0)
#
# Inputs:
#	a0: Pointer to Turtle object
#	a1: Pointer to the name string
#
# Outputs:
#	None. Object fields are updated
#-------------------------------------------------------------------------------------------------
turtle_init:
	# Prologue
	addiu $sp, $sp, -24				# Allocate 24 bytes
	sw    $fp, 0($sp)				# Save frame pointer
	sw    $ra, 4($sp)				# Save return address
	addiu $fp, $sp, 20				# Set up new frame pointer
	
	addi  $t0, $zero, 0				# temp(t0) = 0
	sb    $t0, 0($a0)				# obj* -> x = 0
	sb    $t0, 1($a0)				# obj* -> y = 0
	sb    $t0, 2($a0)				# obj* -> dir = 0
	sw    $a1, 4($a0)				# obj* -> name = name*
	sw    $t0, 8($a0)				# obj* -> odometer = 0
	
	# Epilogue
	lw    $ra, 4($sp)				# Restore return address
	lw    $fp, 0($sp)				# Restore frame pointer
	addiu $sp, $sp, 24				# Restore stack pointer
	jr    $ra					# Return to Caller
	

.globl turtle_debug
# Name: turtle_debug(Turtle *obj)
# Purpose: Prints the fields of the Turtle struct in the following format
#	Format:
#		Turtle "name"
#		   pos x, y
#		   dir <North|East|South|West>
#		   odometer <value>
#
# Input:
#	a0: Pointer to Turtle object
#
# Output:
#	Prints the specified details
#-------------------------------------------------------------------------------------------------
turtle_debug:
	# Prologue
	addiu $sp, $sp, -24				# Allocate 24 bytes
	sw    $fp, 0($sp)				# Save frame pointer
	sw    $ra, 4($sp)				# Save return address
	addiu $fp, $sp, 20				# Set up new frame pointer

	add   $t5, $a0, $zero				# t5 = obj* = pointer to the turtle object
	
	addi  $v0, $zero, 4				# print "Turtle ""
	la    $a0, turtleMsg
	syscall
	
	addi  $v0, $zero, 4				# print turtle name
	lw    $a0, 4($t5)
	syscall
	
	addi  $v0, $zero, 4				# print """
	la    $a0, quoteMsg 
	syscall
	
	addi  $v0, $zero, 4				# print "  pos "
	la    $a0, posMsg
	syscall
	
	lb    $t0, 0($t5)				# load x*
	addi  $v0, $zero, 1				# print <x>
	add   $a0, $t0, $zero
	syscall
	
	addi  $v0, $zero, 4				# print ","
	la    $a0, commaMsg
	syscall
	
	lb    $t1, 1($t5)				# load y*
	addi  $v0, $zero, 1				# print <y>
	add   $a0, $t1, $zero
	syscall
	
	addi  $v0, $zero, 4				# print "\n"
	la    $a0, newline
	syscall
	
	addi  $v0, $zero, 4				# print "  dir "
	la    $a0, dirMsg
	syscall
	
	lb    $t2, 2($t5)				# load dir*
	
	addi  $t3, $zero, 0				# If T3 == 0, then North
	beq   $t2, $t3, debug_northMsg			#
	
	addi  $t3, $zero, 1				# If T3 == 1, then East
	beq   $t2, $t3, debug_eastMsg			#
	
	addi  $t3, $zero, 2				# If T3 == 2, the South
	beq   $t2, $t3, debug_southMsg			#
	
	j     debug_westMsg				# If T3 == else, then West
	
	
	# Direction Message Handlers
debug_northMsg:
	addi  $v0, $zero, 4				# print "North"
	la    $a0, northMsg
	syscall
	
	j     print_newline_after_dir			# jump to print newline
	
debug_eastMsg:
	addi  $v0, $zero, 4				# print "East"
	la    $a0, eastMsg
	syscall
	
	j     print_newline_after_dir			# jump to print newline

debug_southMsg:
	addi  $v0, $zero, 4				# print "South"
	la    $a0, southMsg
	syscall
	
	j     print_newline_after_dir			# jump to print newline

debug_westMsg:
	addi  $v0, $zero, 4				# print "West"
	la    $a0, westMsg
	syscall

print_newline_after_dir:
	addi  $v0, $zero, 4				# print "\n"
	la    $a0, newline
	syscall
	
	addi  $v0, $zero, 4				# print "  odometer "
	la    $a0, odomMsg
	syscall
	
	lw    $t4, 8($t5)				# load odom*
	addi  $v0, $zero, 1				# print <odom>
	add   $a0, $t4, $zero
	syscall
	
	addi  $v0, $zero, 4				# print "\n"
	la    $a0, newline
	syscall
	
	addi  $v0, $zero, 4				# print "\n"
	la    $a0, newline
	syscall
	
	# Epilogue
	lw    $ra, 4($sp)				# Restore return address
	lw    $fp, 0($sp)				# Restore frame pointer
	addiu $sp, $sp, 24				# Restore stack pointer
	jr    $ra					# Return to Caller
	
	
.globl turtle_turnLeft
#-------------------------------------------------------------------------------------------------
# Name: turtle_turnLeft(Turtle *obj)
# Purpose: Turns the Turtle 90 degrees to the left.
# The 'dir' field is adjiusted and ensured it wraps in the range of [0, 3]
#
# Input:
#	a0: Pointer to Turtle object 
#-------------------------------------------------------------------------------------------------
turtle_turnLeft:
	# Prologue
	addiu $sp, $sp, -24				# Allocate 24 bytes
	sw    $fp, 0($sp)				# Save frame pointer
	sw    $ra, 4($sp)				# Save return address
	addiu $fp, $sp, 20				# Set up new frame pointer
	
	lb    $t0, 2($a0)				# Load direction
	addi  $t0, $t0, 3				# Add 3 == -1 mod 4
	andi  $t0, $t0, 3				# Wraps to range [0, 3]
	sb    $t0, 2($a0)				# Store new direction
	
	# Epilogue
	lw    $ra, 4($sp)				# Restore return address
	lw    $fp, 0($sp)				# Restore frame pointer
	addiu $sp, $sp, 24				# Restore stack pointer
	jr    $ra					# Return to Caller
	
	
.globl turtle_turnRight
#-------------------------------------------------------------------------------------------------
# Name: turtle_turnRight
# Purpose: Turns the Turtle 90 degrees to the right.
# The 'dir' field is adjusted and ensured to wrape in range of [0, 3]
#
# Input:
# 	a0: Pointer to Turtle object
#-------------------------------------------------------------------------------------------------
turtle_turnRight:
	# Prologue
	addiu $sp, $sp, -24				# Allocate 24 bytes
	sw    $fp, 0($sp)				# Save frame pointer
	sw    $ra, 4($sp)				# Save return address
	addiu $fp, $sp, 20				# Set up new frame pointer
	
	lb    $t0, 2($a0)				# Load direction
	addi  $t0, $t0, 1				# Turn right - add 1
	andi  $t0, $t0, 3				# Wrap to range [0, 3]
	sb    $t0, 2($a0)				# Store new direction
	
	# Epilogue
	lw    $ra, 4($sp)				# Restore return address
	lw    $fp, 0($sp)				# Restore frame pointer
	addiu $sp, $sp, 24				# Restore stack pointer
	jr    $ra					# Return to Caller
	
	
.globl turtle_move
#-------------------------------------------------------------------------------------------------
# Function: turtle_move(Turtle *obj, int dist)
# Purpose: Moves the Turtle by 'dist' units in the current direction.
# 	If dist is positive, move forward
#	If dist is negative, move backwards
#	X and Y axis limited from -10 to 10 unites
#	Odometer is increased by abs(dist)
#
# Inputs:
# 	a0: Pointer to Turtle object
#	a1: Integer distance (dist)
#-------------------------------------------------------------------------------------------------
turtle_move:
	# Prologue
	addiu $sp, $sp, -24				# Allocate 24 bytes
	sw    $fp, 0($sp)				# Save frame pointer
	sw    $ra, 4($sp)				# Save return address
	addiu $fp, $sp, 20				# Set up new frame pointer

	add   $t5, $zero, $a0				# t5 = obj pointer
	
	# t3 = abs(dist)
	slt   $t4, $a1, $zero				# if (dist < 0), t4 = 1
	bne   $t4, $zero, absoluteNegative		# if (dist < 0) != 0, then jump to absoluteNegative
	add   $t3, $zero, $a1				# t3 = dist (positive)
	j     absoluteDone				
	
absoluteNegative:
	sub   $t3, $zero, $a1				# t3 = -dist

absoluteDone:
	lb    $t0, 2($t5)				# t0 = obj* -> dir

	# Set direction multiplier (t2) based on dir and dist
	addi  $t1, $zero, 0				
	beq   $t0, $t1, northCase			# dir == 0 -> North
	addi  $t1, $zero, 1				
	beq   $t0, $t1, eastCase 			# dir == 1 -> East
	addi  $t1, $zero, 2				
	beq   $t0, $t1, southCase			# dir == 2 -> South
	addi  $t1, $zero, 3  				
	beq   $t0, $t1, westCase			# sir == 3 -> West
	
northCase:
	bne   $t4, $zero, northNegative		# If (dist < 0), then move backward
	addi  $t2, $zero, 1				# move forward // y += dist
	j     multiplier
	
northNegative:
	addi  $t2, $zero, -1				# Move backward // y -= dist
	j     multiplier

eastCase:
	bne   $t4, $zero, eastNegative			# Move Backward // x += dist
	addi  $t2, $zero, 1				
	j     multiplier

eastNegative:
	addi  $t2, $zero, -1				# Move backward // x -= dist
    	j     multiplier

southCase:
	bne   $t4, $zero, southNegative		# Move forward // y -= dist
	addi  $t2, $zero, -1				
	j     multiplier
	
southNegative:
	addi  $t2, $zero, 1				# Move backward // y += dist
	j     multiplier

westCase:
	bne   $t4, $zero, westNegative			# Move forward // x -= dist
	addi  $t2, $zero, -1				
	j     multiplier
	
westNegative:
	addi  $t2, $zero, 1				# Move backward // x -+ dist
    
	# Multiply direction and distance
multiplier:
	mult  $t2, $t3					
	mflo  $t4					# t4 = movement delta
	
	# X and Y values updated 
	addi  $t1, $zero, 0				# If dir == 0, then Y Axix / North	
	beq   $t0, $t1, updateY			
	
	addi  $t1, $zero, 1				# If dir == 1, then X Axis / East
	beq   $t0, $t1, updateX			
	
	addi  $t1, $zero, 2				# If (dir == 2, then Y axis / South
	beq   $t0, $t1, updateY			
    
updateX:
	lb    $t5, 0($a0)				# Load x value		
	add   $t5, $t5, $t4				# x += movement	

	# Restrict to [-10, 10]
	addi  $t6, $zero, 10				
	slt   $t7, $t6, $t5				# if (x > 10), then t7 = 1	
	bne   $t7, $zero, setXmax			# if t7 != 0, then set maximum x
	addi  $t6, $zero, -10				
	slt   $t7, $t5, $t6				# if (x < 10), then t7 = 1
	bne   $t7, $zero, setXmin			# if t7 != 0, then set minimum x
	j     storeX					
	
setXmax:
	addi  $t5, $zero, 10				# Limit x to 10
	j     storeX					
    
setXmin:
	addi  $t5, $zero, -10				# Limit x to -10
    
storeX:
	sb    $t5, 0($a0)  				# Store x
	j     updateOdometer				

updateY:
	lb    $t5, 1($a0)				# Load y value	
	add   $t5, $t5, $t4				# y += movement	

	# Restrict to [-10, 10]
	addi  $t6, $zero, 10			
	slt   $t7, $t6, $t5				# if (y > 10), then t7 = 1		
	bne   $t7, $zero, setYmax			# if t7 != 0, then set maximum y
	addi  $t6, $zero, -10				
	slt   $t7, $t5, $t6				# if (y < -10), then t7 = 1	
	bne   $t7, $zero, setYmin			# if t7 != 0, then set minimum y
	j     storeY					
	
setYmax:
	addi  $t5, $zero, 10				# Limit y to 10
	j     storeY					
	
setYmin:
	addi  $t5, $zero, -10				# Limit y to -10		
	
storeY:
	sb    $t5, 1($a0)				# Store y		

updateOdometer:
	# Odometer updated
	lw    $t6, 8($a0)				# load odometer			
	add   $t6, $t6, $t3				# add abs(dist)
	sw    $t6, 8($a0)				# store odometer

	# Epilogue
	lw    $ra, 4($sp)				# Restore return address
	lw    $fp, 0($sp)				# Restore frame pointer
	addiu $sp, $sp, 24				# Restore stack pointer
	jr    $ra					# Return to Caller
	
	
.globl turtle_searchName
#-------------------------------------------------------------------------------------------------
# Name: turtle_searchName(Turtle *arr, int arrLen, char *needle)
# Purpose: Searches an array of Turtle structs for a Turtle whose 'name' matches 'needle'.
#
# Inputs:
#	a0: Pointer to the first Turtle in the array
#	a1: Integer number of elements in the array
#	a2: Pointer to the target name
#
# Outputs:
#	Returns the index of the matching Turtle, or -1 if not found.
#-------------------------------------------------------------------------------------------------
turtle_searchName: # INCOMPLETE
	# Prologue
	addiu $sp, $sp, -24				# Allocate 24 bytes
	sw    $fp, 0($sp)				# Save frame pointer
	sw    $ra, 4($sp)				# Save return address
	addiu $fp, $sp, 20				# Set up new frame pointer
	
	addi  $t0, $zero, 0				# t0 = i (array index)
	
	###########################
	# Method: 
	#	1. Loop over each pointer in the array (offset by 12 bytes each turtle)
	#	2. For each Turtle*, load the name field (offset by 4 in the struct)
	#	3. Compare using strcmp with the input string (needle*)
	#	3. If all characters match, return index i
	#	4. If no match found, return -1
	#
	# 
	# Loop Comment:
	# 	Outer loop over array of Turtle pointers in array
	#		Index is stored in t0
	#		Stop when i == a1 (no more elements to check)
	#
	# Author Note:
	# 	I couldn't finish on time due to family emergency. Very unfortunate timing
	###########################
	
	# Epilogue
	lw    $ra, 4($sp)				# Restore return address
	lw    $fp, 0($sp)				# Restore frame pointer
	addiu $sp, $sp, 24				# Restore stack pointer
	jr    $ra					# Return to Caller
	
.globl turtle_sortByX_indirect
#-------------------------------------------------------------------------------------------------
# Name: turtle_sortByX_indirect
# Purpose: Sorts and array of pointers to Turtle structs based on the 'x' field using a bubble sort.
# Pointers are swapped rather than actual structs.
#
# Inputs:
#	a0: Pointer to the first element of the array
#	a1: Number of elements in the array
#-------------------------------------------------------------------------------------------------
turtle_sortByX_indirect:
	# Prologue
	addiu $sp, $sp, -24				# Allocate 24 bytes
	sw    $fp, 0($sp)				# Save frame pointer
	sw    $ra, 4($sp)				# Save return address
	addiu $fp, $sp, 20				# Set up new frame pointer
	
	addi  $t0, $zero, 0				# t0 = i (outer loop index)
	
outerLoopStart:
	addi  $t1, $a1, -1				# t1 = count - 1 (loop bound
	slt   $t2, $t0, $t1				# if (i < count - 1), then t2 = 1
	beq   $t2, $zero, outerLoopEnd			# If not, break outer loop
	
	addi  $t3, $zero, 0				# t3 = j (inner loop index)
	
innerLoopStart:
	sub   $t1, $a1, $t0				# t1 = count - i
	addi  $t1, $t1, -1				# t1 = count - i - 1
	
	slt   $t2, $t3, $t1				# if (j < count - i - 1), then t2 = 1
	beq   $t2, $zero, innerLoopEnd			# If not, break inner loop
	
	sll   $t4, $t3, 2				# t4 = j * 4
	add   $t4, $a0, $t4				# t4 = &array[i]
	lw    $t5, 0($t4)				# t5 = array[j] 
	
	addi  $t6, $t3, 1				# t6 = j + 1
	sll   $t6, $t6, 2				# t6 = (j + 1) * 4
	add   $t6, $a0, $t6				# t6 = &array[j + 1]
	lw    $t7, 0($t6)				# t7 = array[j + 1]
	
	lb    $t8, 0($t5)				# t8 = array[j] -> x
	lb    $t9, 0($t7)				# t9 = array[j + 1] -> x
	
	slt   $t1, $t9, $t8				# if (x[j + 1] < x[j]), then t1 = 1
	beq   $t1, $zero, noSwap			# if not, skip swap
	
	sw    $t7, 0($t4)				# array[j] = array[j + 1]
	sw    $t5, 0($t6)				# array[j + 1] = array[j]
	
noSwap:
	addi  $t3, $t3, 1				# j++
	j     innerLoopStart				# Repeat inner loop
	
innerLoopEnd:
	addi  $t0, $t0, 1				# i++
	j     outerLoopStart				# Repeat outer loop
	
outerLoopEnd:
	# Epilogue
	lw    $ra, 4($sp)				# Restore return address
	lw    $fp, 0($sp)				# Restore frame pointer
	addiu $sp, $sp, 24				# Restore stack pointer
	jr    $ra					# Return to Caller