/*
 * Sim4.java
 * Milestone 2
 * Eric Romero
 * CSC 252 - Spring 2025
 * Purpose:
 * 		This program implements a single-cycle CPU simulator 
 * for executing MIPS instructions. For this milestone, it 
 * focuses on extracting instruction fields and 
 * setting control signals for execution.
 */

public class Sim4 {

    /* HELPER FUNCTIONS THAT YOU CAN CALL */
	public int signExtend16to32(int val16) {
		if ((val16 & 0x8000) != 0)
			return val16 | 0xFFFF0000;
		else
			return val16;

	}

	/*
	 * Method: extractInstructionFields
	 * 
	 * Purpose: Extracts different instruction fields from a 32-Bit 
	 * MIPS instruction. This function will decompose the instruction into
	 * opcode, registers (rs, rt, rd), shamt, funct, immediate values (16-bit
	 * and 32-bit), and jump.
	 * 
	 * Pre-Conditions: The method takes a 32-bit instruction and a reference 
	 * to 'fieldsOut' (an instance of 'InstructionFields') where extracted 
	 * values will be stored.
	 * 
	 * Post-Conditions: Fills the 'fieldsOut' object with parsed instruction components
	 */
	public void extractInstructionFields(int instruction, InstructionFields fieldsOut) {
		// Extract the opcode (bits 31 - 26)
		fieldsOut.opcode = (instruction >>> 26) & 0x3f;
		
		// Extract register fields
		fieldsOut.rs = (instruction >>> 21) & 0x1f;		// bits 25 - 21
		fieldsOut.rt = (instruction >>> 16) & 0x1f;		// bits 20 - 16
		fieldsOut.rd = (instruction >>> 11) & 0x1f;		// bits 15 - 11
		
		// Extract shamt (bits 10 - 6)
		fieldsOut.shamt = (instruction >>> 6) & 0x1f;
		
		// Extract funct (bits 5 - 0)
		fieldsOut.funct = instruction & 0x3f;
		
		// Extract immediate field (bits 15 - 0)
		fieldsOut.imm16 = instruction & 0xffff;
		
		// Sign-extend the 16-bit immediate value to 32 bits
		fieldsOut.imm32 = signExtend16to32(fieldsOut.imm16);
		
		// Extract address field for J-format (bits 25 - 0)
		fieldsOut.address = instruction & 0x3ffffff;
	}

	/*
	 * Method: fillCPUControl
	 * 
	 * Purpose: Fills the CPU control signals based on the decoded 
	 * instruction fields. Determines the type of instruction (R-Type, I-Type, 
	 * J-Type) and sets the control signals accordingly to guide the execution 
	 * in a single-cycle CPU
	 * 
	 * Pre-Conditions: Takes extracted instruction fields ('fields') and an 
	 * empty control structure 'controlOut').
	 * 
	 * Post-Conditions: Returns 1 if the instruction is valid, 0 otherwise.
	 */
	public int fillCPUControl(InstructionFields fields, CPUControl controlOut) {
		// Reset all control bits
		controlOut.ALUsrc = 0;
		controlOut.ALU.op = 0;
		controlOut.ALU.bNegate = 0;
		controlOut.memRead = 0;
		controlOut.memWrite = 0;
		controlOut.memToReg = 0;
		controlOut.regDst = 0;
		controlOut.regWrite = 0;
		controlOut.branch = 0;
		controlOut.jump = 0;
		
		/*
		 * Decode instruction type based on the opcode:
		 * 	- R-type (opcode = 0x00, uses funct fields for the operation)
		 * 	- I-type (Immediate-Based operations)
		 * 	- J-type (Jump operation)
		 */
		switch (fields.opcode) {
			case 0x00:		// R-Format Instruction
				// Decode funct fields for specific R-type operations
				switch (fields.funct) {
						
					case 0x20:		// add
					case 0x21:		// addu
						controlOut.ALU.op = 2;
						controlOut.ALU.bNegate = 0;
						break;
						
					case 0x22:		// sub
					case 0x23:		// subu
						controlOut.ALU.op = 2;
						controlOut.ALU.bNegate = 1;
						break;
						
					case 0x24:		// and
						controlOut.ALU.op = 0;
						break;
						
					case 0x25:		// or
						controlOut.ALU.op = 1;
						break;
						
					case 0x26:		// xor
						controlOut.ALU.op = 4;
						break;
						
					case 0x27:		// nor
						controlOut.ALU.op = 5;
						break;
						
					case 0x2a:		// slt
						controlOut.ALU.op = 3;
						controlOut.ALU.bNegate = 1;
						break;
						
					default:
						return 0;	// Invalid R-Format instruction
				}
				// Only set control signals after confirming valid funct.
				controlOut.regDst = 1;
				controlOut.regWrite = 1;
				controlOut.ALUsrc = 0;
				break;
				
			case 0x02:		// j (jump)
				controlOut.jump = 1;
				break;
				
			case 0x04:		// beq (branch if equal)
				controlOut.ALUsrc = 0;
				controlOut.branch = 1;
				controlOut.ALU.op = 2;
				controlOut.ALU.bNegate = 1;
				break;
				
			case 0x08:		// addi
			case 0x09:		// addiu 
				controlOut.ALUsrc = 1;
				controlOut.regWrite = 1;
				controlOut.ALU.op = 2;
				break;
				
			case 0x0a:		// slti
				controlOut.ALUsrc = 1;
				controlOut.regWrite = 1;
				controlOut.ALU.op = 3;
				controlOut.ALU.bNegate = 1;
				
				break;
				
			case 0x0c:		// andi
				controlOut.ALUsrc = 1;
				controlOut.regWrite = 1;
				controlOut.ALU.op = 0;
				break;
				
			case 0x0d:		// ori
				controlOut.ALUsrc = 1;
				controlOut.regWrite = 1;
				controlOut.ALU.op = 1;
				break;
				
			case 0x23:		// lw (load word)
				controlOut.ALUsrc = 1;
				controlOut.memRead = 1;
				controlOut.memToReg = 1;
				controlOut.regWrite = 1;
				controlOut.ALU.op = 2;
				break;
				
			case 0x2b:		// sw (store word)
				controlOut.ALUsrc = 1;
				controlOut.memWrite = 1;
				controlOut.ALU.op = 2;
				break;
				
			default:
				return 0;	// Invalid Operation
			}
			
		
        return 1;	// Control Signals successfuly set
	}
    
    
    // Method signatures corresponding to function prototypes in sim4.h
	
	/*
	 * Method: getInstruction
	 *
	 * Purpose: Given the current program counter, return the instruction 
	 * located at that position in the instruction memory.
	 * 
	 * Parameters:
	 * 	curPC: The current program counter in bytes
	 *  instructionMemory: An array of WORDs representing instruction memory.
	 *  
	 * Returns:
	 * 	The 32-bit instruction located at the index (curPX / 4)
	 * 
	 * Pre-Conditons: curr must be aligned to a word boundary
	 * 
	 * Post-Conditions: Returns the instruction at the current PC address
	 */
	public int getInstruction(int curPC, int[] instructionMemory) {
		        
        // Return Instruction at that memory location
        return instructionMemory[curPC / 4];
	}

	/*
	 * Method: getALUinput1
	 * 
	 * Purpose: Determines the first operand to send to the ALU. 
	 * Should be the value stored in the rs register. 
	 * 
	 * Parameters:
	 * 	controlIn: The CPU control signals
	 * 	fieldsIn:  The decoded instruction fields
	 * 	rsVal:     The value from register rs
	 * 	rtVal:     The value from register rt
	 *  reg32:     Constant zero
	 *  reg33:     PC + 4
	 *  oldPC:	   Current value of the PC
	 *  
	 *  Returns:
	 *  	The first ALU operand, should be value of rs
	 *  
	 *  Pre-Conditions:
	 *  	rsVal contains the correct value
	 *  
	 *  Post-Conditions:
	 *  	Returns rsVal
	 */
	public int getALUinput1(CPUControl controlIn, InstructionFields fieldsIn,
                                   int rsVal, int rtVal, int reg32, int reg33, int oldPC) {
		// TODO on milestone 2: Your code goes here
		
        return rsVal;
	}

	/*
	 * Method: getALUinput2
	 * 
	 * Purpose: Determines the second input to the ALU. This will either be a register or an immediate value, depending on the control signal.
	 * 
	 * Parameters:
	 * 	controlIn: Input CPU control signals that specify ALU behavior
	 *  fieldsIn: The instruction fields, containing register and immediate info
	 *  rsVal: value from the rs register
	 *  rtVal: Value from rt register
	 *  reg32: Reserved register 32
	 *  reg33: Resevred register 33
	 *  oldPC: The current program counter
	 * 
	 * Return:
	 * 	The value to be passed as the second input to the ALU
	 */
	public int getALUinput2(CPUControl controlIn, InstructionFields fieldsIn,
                                   int rsVal, int rtVal, int reg32, int reg33, int oldPC) {
		// TODO on milestone 2: Your code goes here
        int input2;
        
        // For andi/ori
        if (controlIn.ALUsrc == 1 && (fieldsIn.opcode == 0x0c || fieldsIn.opcode == 0x0d)) {
        	input2 = fieldsIn.imm16 & 0x0000ffff;
        } else if (controlIn.ALUsrc == 1) {
        	input2 = fieldsIn.imm32;
        } else {
        	input2 = rtVal;
        } return input2;
	}

	/*
	 * Method: executeALU
	 * 
	 * Purpose: Executes the arithmetic or logical operation specified by the ALU control signals.
	 * 		Sets the result and the zero flag in the provided ALUResult object.Handles core operation such as addition, subtraction, AND, OR, XOR, SLT, and custom instructionssuch as NOR, based on ALU.op and bNegate control signals.
	 * 
	 * Parameters:
	 * 	controlIn: The CPUControl object specifying ALU operation and bNegate signal
	 *  input1: The first operand to the ALU
	 *  input2: The second operand
	 *  aluResultOut: The ALUResult object where the result and zero flag will be stored.
	 * 
	 * Post-Conditions:
	 * 	aluResultOut.result contains the output of the operation.
	 *  aluResultOut.zero is set to 1 is the result is 0, otherwise 0.
	 */
	public void executeALU(CPUControl controlIn, int input1, int input2, ALUResult aluResultOut) {
		// TODO on milestone 2: Your code goes here
		
		int result = 0;
		
		if (controlIn.ALU.op == 2 && controlIn.ALU.bNegate == 1) {
			// Subtraction
			result = input1 - input2;
		
		// ALU operation handling
		} else if (controlIn.ALU.op == 0) {
			result = input1 & input2;
		} else if (controlIn.ALU.op == 1) {
			result = input1 | input2;
		} else if (controlIn.ALU.op == 2) {
			result = input1 + input2;
		} else if (controlIn.ALU.op == 3) {
			if (input1 < input2) {
				result = 1;
			} else {
				result = 0;
			}
		} else if (controlIn.ALU.op == 4) {
			result = input1 ^ input2;
		} else if (controlIn.ALU.op == 5) {
			result = ~(input1 | input2);
		}
		
		aluResultOut.result = result;
		
		if (result == 0) {
			aluResultOut.zero = 1;
		} else {
			aluResultOut.zero = 0;
		}
	}

	/*
	 * Method: executeMEM
	 * 
	 * Purpose: Performs memory access based on control signals. For load instructions, reads a value from memory into resultOut. For store instruction, writes the value of rt to memory.
	 * 
	 * Parameters:
	 * 
	 * Pre-Conditions:
	 * 
	 * Post-Conditions:
	 */
	public void executeMEM(CPUControl controlIn, ALUResult aluResultIn,
                                  int rsVal, int rtVal, int[] memory, MemResult resultOut) {
		// TODO on milestone 2: Your code goes here
		// Convert byte address to word index
		int adder = aluResultIn.result / 4;
		
		if (controlIn.memRead == 1) {
			resultOut.readVal = memory[adder];
		}
		
		if (controlIn.memWrite == 1) {
			memory[adder] = rtVal;
		}
	}

	/*
	 * Method: getNextPC
	 * 
	 * Purpose: Computes the address of the next instruction to execute. Based on wwhether the instruction is a regular instruction or etc., as this will return the appropriate next PC value
	 * 
	 * Parameters:
	 * 	fields: The decoded instruction fields
	 *  controlIn: Control signals
	 *  aluZero: ALU zero flag used for determining branch conditions
	 *  rsVal: Value of the rs register
	 *  rtVal: Value of the rt register
	 *  oldPC: The address of the current instruction
	 * 
	 * Return:
	 * 	The address of the next instruction to be fetched.
	 */
	public int getNextPC(InstructionFields fields, CPUControl controlIn, int aluZero,
                                int rsVal, int rtVal, int oldPC) {
		// TODO on milestone 2: Your code goes here
		int nextPC = oldPC + 4;
		
		if (controlIn.branch == 1 && aluZero == 1) {
			// BEQ
			nextPC = oldPC + 4 + (fields.imm32 << 2);
		}
		
		if (controlIn.jump == 1) {
			// JUMP
			nextPC = ((oldPC + 4) & 0xF0000000) | (fields.address << 2);
		}
        return nextPC;
	}

	/*
	 * Method: executeUpdateRegs
	 * 
	 * Purpose: Write a value to a destination register if regWrite is enabled. Destination and source values depend on the instruction type and control signals. This method prevents any writes to register. 
	 * 
	 * Parameters:
	 * 	fields: Decoded instruction fields
	 *  controlIn: CPU control signals
	 *  aluResultIn: Result from the ALU execution
	 *  memResultIn: Result from memory read
	 *  regs: The full register file
	 * 
	 * Post-Conditions:
	 * 	The appropriate register is updates if allowed by control signals.
	 */
	public void executeUpdateRegs(InstructionFields fields, CPUControl controlIn,
                                         ALUResult aluResultIn, MemResult memResultIn, int[] regs) {
		// TODO on milestone 2: Your code goes here
		if (controlIn.regWrite == 1) {
			int destination = (controlIn.regDst == 1) ? fields.rd : fields.rt;
			
			if (destination != 0) {
				// Prevent writing to $zero.
				regs[destination] = (controlIn.memToReg == 1) ? memResultIn.readVal : aluResultIn.result;
			}
		}
	}
}