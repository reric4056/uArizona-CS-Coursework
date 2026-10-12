/*
 * Sim4.java
 * Milestone 1
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
	 * Pre-Conditions: The method takes a 32-bit instruction and a reference to 'fieldsOut'
	 *  (an instance of 'InstructionFields') where extracted values will be stored.
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
	 * instruction fields. Determines the type of instruction (R-Type, I-Type, J-Type)
	 * and sets the control signals accordingly to guide the execution in a single-cycle CPU
	 * 
	 * Pre-Conditions: Takes extracted instruction fields ('fields') and an empty control structure 'controlOut').
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
				controlOut.regDst = 1;			// regDst = Destination Register
				controlOut.regWrite = 1;		// regWrite = Enable writing to registers
				
				// Decode funct fields for specific R-type operations
				switch (fields.funct) {
					case 0x20:		// add
					case 0x21:		// addu
						controlOut.ALU.op = 2;
						break;
						
					case 0x22:		// sub
					case 0x23:		// subu
						controlOut.ALU.op = 2;
						controlOut.ALU.bNegate = 1;
						break;
						
					case 0x24:		// and
						break;
						
					case 0x25:		// or
						controlOut.ALU.op = 1;
						break;
						
					case 0x26:		// xor
						controlOut.ALU.op = 4;
						break;
						
					case 0x2a:		// slt
						controlOut.ALU.op = 3;
						controlOut.ALU.bNegate = 1;
						break;
						
					default:
						return 0;	// Invalid R-Format instruction
				}
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
				
			case 0x04:		// beq (branch if equal)
				controlOut.branch = 1;
				controlOut.ALU.op = 2;
				controlOut.ALU.bNegate = 1;
				break;
				
			case 0x02:		// j (jump)
				controlOut.jump = 1;
				break;
				
			default:
				return 0;	// Invalid Operation
			}
			
		
        return 1;	// Control Signals successfuly set
	}
    
    
    // Method signatures corresponding to function prototypes in sim4.h
	public int getInstruction(int curPC, int[] instructionMemory) {
		// TODO on milestone 2: Your code goes here
        
        return 0;
	}

	public int getALUinput1(CPUControl controlIn, InstructionFields fieldsIn,
                                   int rsVal, int rtVal, int reg32, int reg33, int oldPC) {
		// TODO on milestone 2: Your code goes here
        return 0;
	}

	public int getALUinput2(CPUControl controlIn, InstructionFields fieldsIn,
                                   int rsVal, int rtVal, int reg32, int reg33, int oldPC) {
		// TODO on milestone 2: Your code goes here
        return 0;
	}

	public void executeALU(CPUControl controlIn, int input1, int input2, ALUResult aluResultOut) {
		// TODO on milestone 2: Your code goes here

	}

	public void executeMEM(CPUControl controlIn, ALUResult aluResultIn,
                                  int rsVal, int rtVal, int[] memory, MemResult resultOut) {
		// TODO on milestone 2: Your code goes here

	}

	public int getNextPC(InstructionFields fields, CPUControl controlIn, int aluZero,
                                int rsVal, int rtVal, int oldPC) {
		// TODO on milestone 2: Your code goes here
        return 0;
	}

	public void executeUpdateRegs(InstructionFields fields, CPUControl controlIn,
                                         ALUResult aluResultIn, MemResult memResultIn, int[] regs) {
		// TODO on milestone 2: Your code goes here

	}
}