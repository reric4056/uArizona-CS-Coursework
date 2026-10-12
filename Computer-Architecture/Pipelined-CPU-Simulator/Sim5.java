/*
 * Sim5.java
 * Author: Eric Romero
 * Spring 2025 - 4/17/2025
 * Purpose:
 * 	This class implements a pipelined CPU simulator. It's an adaptation of Sim4's single-ctyle CPU design, 
 * evolved to simulate instruction parallilism through pipeline stages. Each stage is handled by a specific method,
 * and data is passed in between stages using pipeline registers.
 */
public class Sim5 {

	/*
	 * signExtend16to32
	 * 
	 * Purpose:
	 * 	Extends a 16-bit integer to a 32-bit signed integer.
	 * 
	 * Parameters:
	 * 	int val16 (input) : The 16-bit integer to sign-extend
	 * 
	 * PreConditions:
	 * 	'val16' must be a valid 16-bit value
	 * 
	 * PostConditions:
	 * 	Returns a 32-bit signed version of the input. Preserves the MSB
	 * 
	 * Returns:
	 * 	int : 32 bit sign extended variant of input
	 */
	public static int signExtend16to32(int val16) {
		if ((val16 & 0x8000) != 0)
			return val16 | 0xFFFF0000;
		else
			return val16;

	}
	
	/*
	 * extractInstructionsFields
	 * 
	 * Purpose: 
	 * 	Extract Fields from a 32-bit instruction and stores into InstructionField objects.
	 * 
	 * Parameters:
	 * 	int instruction (input) : The 32-bit binary instruction to decode.
	 * 	InstructionFields fields (output) : The object fields to fill with decoded fields.
	 * 
	 * PreConditions:
	 * 	'instruction' has to be a valid 32-bit number representing the instruction
	 * 	'fields' has to be a valid InstructionFields object
	 * 
	 * PostConditions:
	 * 	All fields in the 'fields' object are filled based on the instruction.
	 * 
	 * Returns:
	 * 	None
	 */
	public static void extractInstructionFields(int instruction, InstructionFields fields) {
		// Extract the opcode (bits 31 - 26)
		fields.opcode = (instruction >>> 26) & 0x3f;
		
		// ------ Extract register fields -----
		// bits 25 - 21
		fields.rs = (instruction >>> 21) & 0x1f;
		// bits 20 - 16
		fields.rt = (instruction >>> 16) & 0x1f;	
		// bits 15 - 11
		fields.rd = (instruction >>> 11) & 0x1f;	
		
		// Extract shamt (bits 10 - 6)
		fields.shamt = (instruction >>> 6) & 0x1f;
		
		// Extract funct (bits 5 - 0)
		fields.funct = instruction & 0x3f;
		
		// Extract immediate field (bits 15 - 0)
		fields.imm16 = instruction & 0xffff;
		
		// Sign-extend the 16-bit immediate value to 32 bits
		fields.imm32 = signExtend16to32(fields.imm16);
		
		// Extract address field for J-format (bits 25 - 0)
		fields.address = instruction & 0x3ffffff;
	}
	
	/*
	 * IDtoIF_get_stall
	 * 
	 * Purpose:
	 * 	Determines if a stall is needed between the ID and IF pipeline stages.
	 * 
	 * Parameters:
	 * 	InstructionField fields (input) : Instruction in the ID stage
	 *  ID_EX old_idex (input) : Instriction in the EX stage
	 *  EX_MEM old_exmem (input) : Instruction in the MEM stage
	 *  
	 * PreConditions:
	 *  All pipeline stage objects have valid control signals and register values
	 *  'fields' is the decoded instruction
	 *  
	 * PostConditions:
	 * 	Input parameters remain unchanged
	 * 	Decision is made determining if stall is required
	 * 
	 * Returns:
	 * 	int: 1 if stall needed, 0 if else.
	 */
	public static int IDtoIF_get_stall(InstructionFields fields, ID_EX old_idex, EX_MEM old_exmem) {
		// Checks for LW Hazard : EX is loaded, ID uses loaded value
		if (old_idex.memRead == 1) {
			// For R-Type instructions : rs and rt are inputs
			if (fields.opcode == 0x00) {
				if (old_idex.rt == fields.rs || old_idex.rt == fields.rt) {
					return 1;
				}
			// For BEQ or BNE
			} else if (fields.opcode == 0x04 || fields.opcode == 0x05) {
				if (old_idex.rt == fields.rs || old_idex.rt == fields.rt) {
					return 1;
				}
			// For ADDI, ADDIU, SLTI, ANDI, ORI
			} else if (fields.opcode == 0x08 || fields.opcode == 0x09 || fields.opcode == 0x0a || fields.opcode == 0x0c || fields.opcode == 0x0d) {
				if (old_idex.rt == fields.rs) {
					return 1;
				}
			}
 		}
		
		// SW Hazard checked : ID is for Save Word
		if (fields.opcode == 0x2b) {	
			// Case: EX_MEM is writing to the register that is going to be stored
			if (old_exmem.regWrite == 1 && old_exmem.writeReg != 0 && old_exmem.writeReg == fields.rt) {
				return 1;
			}
			
			// Case: ID_EX is instead
			if (old_idex.regWrite == 1) {
				int writeReg = (old_idex.regDst == 1) ? old_idex.rd : old_idex.rt;
				
				if (writeReg != 0 && writeReg == fields.rt) {
					return 1;
				}
			}
		}
		// No stall!
		return 0;
	}

	/*
	 * IDtoIF_get_branchControl
	 * 
	 * Purpose:
	 * 	Determines branching or jumping based on decoded instruction fields.
	 * 
	 * Parameters:
	 * 	InstructionFields fields (input) : Decoded instruction field
	 * 	int rsVal (input) : Value in rs register
	 * 	int rtVal (input) : Value in rt register
	 * 
	 * PreConditions:
	 * 	Instruction is already retrieved and decoded
	 * 	Values of rs and rt are "current" and gotten from register file
	 * 
	 * PostConditions:
	 * 	Returns control signal determining how IF stage updates the PC
	 * 
	 * Returns:
	 * 	0 : No Branch or Jump
	 * 	1 : Conditional Branch
	 * 	2 : Unconditional Jump
	 * 	3 : Jump to value in rs register
	 */
	public static int IDtoIF_get_branchControl(InstructionFields fields, int rsVal, int rtVal) {
		switch (fields.opcode) {
			// R Type instruction
			case 0x00:
				// Jump Register
				if (fields.funct == 0x08) {
					return 3;
				}
				break;
				
			// Jump
			case 0x02:
				return 2;
				
			// Branch if Equal
			case 0x04:
				if (rsVal == rtVal) {
					return 1;
				}
				break;
				
			// Branch if Not Equal
			case 0x05:
				if (rsVal != rtVal) {
					return 1;
				}
				break;
				
			default:
				return 0;
		}
		// No Branch
		return 0;
	}

	/*
	 * calc_branchAddr
	 * 
	 * Purpose:
	 * 	Calculates address for conditional branches. 
	 * 
	 * Parameters:
	 * 	int pcPlus4 (input) : Program counter + 4, address of next instruction
	 * 	InstructionFields fields (input) : Decoded instruction
	 * 
	 * PreConditions:
	 * 	Instriction is a conditional branch
	 * 	The imm32 field in the 'fields' object is sign extended
	 * 
	 * PostConditions:
	 * 	Returns the address for branching
	 * 	Value is Passed into MUX in IF stage to determine actual next PC
	 * 
	 * Returns:
	 * 	int: The evaluated address for branching
	 */
	public static int calc_branchAddr(int pcPlus4, InstructionFields fields) {
		int offset = fields.imm32 << 2;
		return pcPlus4 + offset;
	}

	/*
	 * calc_jumpAddr
	 * 
	 * Purpose:
	 * 	Evaluates the absolute jump address used for unconditional jumps.
	 * 
	 * Parameters:
	 * 	int pcPlus4 (input) : Value of PC incremented by 4
	 * 	InstructionFields fields (input) : Decoded fields for instructions
	 * 
	 * PreConditions:
	 * 	'fields' extracted for instruction in ID
	 * 
	 * PostConditions:
	 * 	No changes to inputs. 
	 * 
	 * Returns:
	 * 	int : Evaluated address for jumping
	 */
	public static int calc_jumpAddr(int pcPlus4, InstructionFields fields) {
		int upperPCBits = pcPlus4 & 0xf0000000;
		int shiftedAddr = fields.address << 2;
		return upperPCBits | shiftedAddr;
	}

	/*
	 * execute_ID
	 * 
	 * Purpose:
	 * 	Executes the instruction decode phase. Handles decoding, 
	 *  generates control signals, and 
	 *  fills the ID_EX register with relevant values.
	 * 
	 * Parameters:
	 * 	int stall (input) : Set to 1 if stall is required, 0 otherwise
	 * 	InstructionFields fields (input) : Decoded instruction in ID phase
	 * 	int pcPlus4 (input) : Program count + 4 (address of next instruction
	 * 	int rsVal (input) : Value of register in rs field
	 * 	int rtVal (input) : Valye of register in rt field
	 * 	ID_EX out (output) :The new register (ID/EX) that will be filled for EX phase
	 * 
	 * PreConditions:
	 * 	Instruction in the ID phase is retrieved and decoded.
	 * 	Register values for rsVal and rtVal have to be correct
	 * 
	 * PostConditions:
	 * 	The ID_EX pipeline register is filled with relevant data needed for execution.
	 * 	If stall is true, then filled to prevent execution
	 * 
	 * Returns:
	 * 	int: 1 if instruction is valid and recognized, 0 if not
	 */
	public static int execute_ID(int stall, InstructionFields fields, int pcPlus4, int rsVal, int rtVal, ID_EX out) {
		// Check if need to zero out registers for pipeline
		if (stall == 1 || fields.opcode == 0x02 || fields.opcode == 0x04 || fields.opcode == 0x05) {
			out.rs = 0;
			out.rt = 0;
			out.rd = 0;
			out.rsVal = 0;
			out.rtVal = 0;
			out.imm16 = 0;
			out.imm32 = 0;
			out.ALUsrc = 0;
			out.ALU.bNegate = 0;
			out.ALU.op = 0;
			out.memRead = 0;
			out.memWrite = 0;
			out.memToReg = 0;
			out.regDst = 0;
			out.regWrite = 0;
			return 1;
			
		}
		
		// Common data fields are filled
		out.rs = fields.rs;
		out.rt = fields.rt;
		out.rd = fields.rd;
		out.rsVal = rsVal;
		out.rtVal = rtVal;
		out.imm16 = fields.imm16;
		out.imm32 = fields.imm32;
		
		// All control signals set to 0
		out.ALUsrc = 0;
		out.ALU.bNegate = 0;
		out.ALU.op = 0;
		out.memRead = 0;
		out.memWrite = 0;
		out.memToReg = 0;
		out.regDst = 0;
		out.regWrite = 0;
		
		switch (fields.opcode) {
			// R Type Instruction
			case 0x00:
				switch (fields.funct) {
					// Add
					case 0x20:
					// Addu
					case 0x21:
						out.ALU.bNegate = 0;
						out.ALU.op = 2;
						break;
						
					// Sub
					case 0x22:
					// Subu
					case 0x23:
						out.ALU.bNegate = 1;
						out.ALU.op = 2;
						break;
						
					// And
					case 0x24:
						out.ALU.bNegate = 0;
						out.ALU.op = 0;
						break;
					
					// Or
					case 0x25:
						out.ALU.bNegate = 0;
						out.ALU.op = 1;
						break;
						
					// Xor
					case 0x26:
						out.ALU.bNegate = 0;
						out.ALU.op = 4;
						break;
						
					// Nor
					case 0x27:
						out.ALU.bNegate = 0;
						out.ALU.op = 6;
						break;
						
					// Slt
					case 0x2a:
						out.ALU.bNegate = 1;
						out.ALU.op = 3;
						break;
						
					// Nop (Sll $zero, $zero, 0)
					case 0x00:
						if (fields.rd == 0 && fields.rt == 0 && fields.shamt == 0) {
							out.ALU.op = 5;
							break;
						} else {
							return 0;
						}
					// R-Type is unknown
					default:
						return 0;
						
				}
				out.regDst = 1;
				out.regWrite = 1;
				return 1;
				
			// J
			case 0x02:
			// Beq
			case 0x04:
			// Bne
			case 0x05:
				// Branching is handled in ID control logic
				return 1;
				
			// Addi
			case 0x08:
			// Addiu
			case 0x09:
				out.ALUsrc = 1;
				out.ALU.bNegate = 0;
				out.ALU.op = 2;
				out.regDst = 0;
				out.regWrite = 1;
				return 1;
				
			// Slti
			case 0x0a:
				out.ALUsrc = 1;
				out.ALU.bNegate = 1;
				out.ALU.op = 3;
				out.regDst = 0;
				out.regWrite = 1;
				return 1;
				
			// Andi
			case 0x0c:
				out.ALUsrc = 2;
				out.ALU.bNegate = 0;
				out.ALU.op = 0;
				out.regDst = 0;
				out.regWrite = 0;
				return 1;
				
			// Ori
			case 0x0d:
				out.ALUsrc = 2;
				out.ALU.bNegate = 0;
				out.ALU.op = 1;
				out.regDst = 0;
				out.regWrite = 1;
				return 1;
			
			// Lui
			case 0x0f:
				out.ALUsrc = 4;
				out.ALU.op = 0;
				out.regDst = 0;
				out.regWrite = 1;
				return 1;
				
			// Lw
			case 0x23:
				out.ALUsrc = 1;
				out.ALU.bNegate = 0;
				out.ALU.op = 2;
				out.memRead = 1;
				out.memToReg = 1;
				out.regDst = 0;
				out.regWrite = 1;
				return 1;
				
			// Sw
			case 0x2b:
				out.ALUsrc = 1;
				out.ALU.bNegate = 0;
				out.ALU.op = 2;
				out.memWrite = 1;
				return 1;
			
			// Instruction unknown
			default:
				return 0;
			
		}
	}

	/*
	 * EX_getALUinput1
	 * 
	 * Purpose:
	 * 	Determines the first operand for the ALU by resolving dependencies and applying forwarding if needed
	 * 
	 * Parameters:
	 * 	ID_EX in (input) : Current instruction and data in ID/EX stage
	 * 	EX_MEM old_exMem (input) : Data in EX/MEM stage
	 * 	MEM_WB old_memWb (input) : Data in MEM/WB stage
	 * 
	 * PreConditions:
	 * 	All register objects have valid data
	 * 	EX/MEM and MEM/WB stages correct from previous instruction output
	 * 
	 * PostConditions:
	 * 	No changes to input objects.
	 * 	The correct value for input 1 for the ALU is returned.
	 * 
	 * Returns:
	 * 	int : Represents resolved ALU input 'a' value used in EX stage
	 */
	public static int EX_getALUinput1(ID_EX in, EX_MEM old_exMem, MEM_WB old_memWb) {
		// If writes to same register, forward from Ex_Mem stage
		if (old_exMem.regWrite == 1 && old_exMem.writeReg != 0 && old_exMem.writeReg == in.rs) {
			return old_exMem.aluResult;
		}
		
		// If Writes to same register, forward frm Mem_Wb stage
		if (old_memWb.regWrite == 1 && old_memWb.writeReg != 0 && old_memWb.writeReg == in.rs) {
			// Checks if value is from memory
			if (old_memWb.memToReg == 1) {
				return old_memWb.memResult;
			} else {
				return old_memWb.aluResult;
			}
		}
		// original rs value 
		return in.rsVal;
	}

	/*
	 * EX_getALUinput2
	 * 
	 * Purpose:
	 * 	Determines the second input for the ALU for the EX stage.
	 * 
	 * Parameters:
	 * 	ID_EX in (input) : Decoded instruction data from ID stage
	 * 	EX_MEM old_exMem (input) : Register from previous EX stage
	 * 	MEM_WB old_memWb (input) : Register from previous MEM stage
	 * 
	 * PreConditions:
	 * 	All registers passed are correct.
	 * 	The ID_EX register is filled with correct data
	 * 
	 * PostConditions:
	 * 	Input remain unchanged
	 * 	Returns correct value for second ALU input 'b'
	 * 
	 * Returns:
	 * 	int : 32 bit value for second ALU operand 'b'
	 */
	public static int EX_getALUinput2(ID_EX in, EX_MEM old_exMem, MEM_WB old_memWb) {
		// If AluSrc is 1, then reutrn the sign-extended 32 bit immediate value
		if (in.ALUsrc == 1) {
			return in.imm32;
		}
		
		// If AluSrc is 2, then use a zero extended 16 bit immediate value
		if (in.ALUsrc == 2) {
			return in.imm16 & 0xffff;
		}
		
		// If written to same rt register, then forward from Ex_Mem stage
		if (old_exMem.regWrite == 1 && old_exMem.writeReg != 0 && old_exMem.writeReg == in.rt) {
			in.rtVal = old_exMem.aluResult;
		// If writted to same rt register, then forward from Mem_Wb stage
		} else if (old_memWb.regWrite == 1 && old_memWb.writeReg != 0 && old_memWb.writeReg == in.rt) {
			in.rtVal = (old_memWb.memToReg == 1) ? old_memWb.memResult : old_memWb.aluResult;
		}
		
		// Use original rtVal
		return in.rtVal;
	}

	/*
	 * execute_EX
	 * 
	 * Purpose:
	 * 	Executes the Execute phase of the pipeline. 
	 * 	Determines destination register based instruction type.
	 * 	Destination register stored in EX/MEM register
	 * 	Performs ALU operation
	 * 
	 * Parameters:
	 * 	ID_EX in (input) : Current ID/EX register with decoded instruction
	 * 	int input1 (input) : First operand of ALU
	 * 	int input2 (input) : Second operand of ALY
	 * 	EX_MEM out (output) : EX/MEM register to be written
	 * 
	 * PreConditions:
	 * 	Input are already resolved
	 * 	ID/EX register is valid
	 * 
	 * PostConditions:
	 * 	ALU result stored in EX/MEM register
	 * 	Correct destination register is chosen and stored
	 * 
	 * Returns:
	 * 	None
	 */
	public static void execute_EX(ID_EX in, int input1, int input2, EX_MEM out) {
		int result = 0;
		
		// Execute Lui
		if (in.ALUsrc == 4) {
			result = in.imm16 << 16;
		} else {
			// ALU executed based on ALU op
			switch (in.ALU.op) {
				// And
				case 0:
					result = input1 & input2;
					break;
				// Or
				case 1:
					result = input1 | input2;
					break;
				// Add
				case 2:
					result = input1 + input2;
					break;
				// Slt	
				case 3:
					result = (input1 < input2) ? 1 : 0;
					break;
				// Xor
				case 4:
					result = input1 ^ input2;
					break;
				// Nop	
				case 5:
					result = 0;
					break;
				// Nor
				case 6:
					result = ~(input1 | input2);
					break;
				// Default
				default:
					result = 0;
			}		
		}
		
		// Destination regsiter is selected (rd if R type, Rt if I type)
		if (in.regDst == 0) {
			out.writeReg = in.rt;
		} else {
			out.writeReg = in.rd;
		}
		
		// Copy result and other related values (regarding forwarding)
		out.aluResult = result;
		out.rtVal = in.rtVal;
		out.rt = in.rt;
		
		// Control signals passed to MEM
		out.memRead = in.memRead;
		out.memWrite = in.memWrite;
		out.memToReg = in.memToReg;
		out.regWrite = in.regWrite;
	}

	/*
	 * execute_MEM
	 * 
	 * Purpose:
	 * 	Handles the memory access phase. 
	 * 	For 'lw', value is read from memory.
	 * 	For 'sw', value is written to memory
	 * 
	 * Parameters:
	 * 	EX_MEM in (input) : Current information for register in EX/MEM phase
	 * 	MEM_WB old_memWb (input) : Register in MEM/WB phase.
	 * 	int[] mem (input / output) : Memory array simulating main memory
	 * 	MEM_WB out (output) : Register to pass values from MEM to WB stage
	 * 
	 * PreConditions:
	 * 	'in' contains valid data from EX stage
	 * 	Memory addresses are within the bounds of the 'mem' array
	 * 
	 * PostConditions:
	 * 	For 'lw' : Data from memory is loaded and stored in the MEM_WB.
	 *  For 'sw' : Stores given valye from EX/MEM to specified memory address
	 *  Fills necessary fields in 'out' for WB stage
	 * 
	 * Returns:
	 * 	None
	 */
	public static void execute_MEM(EX_MEM in, MEM_WB old_memWb, int[] mem, MEM_WB out) {
		// Control signals passed to next stage
		out.memToReg = in.memToReg;
		out.aluResult = in.aluResult;
		out.writeReg = in.writeReg;
		out.regWrite = in.regWrite;
		
		// If LW, read from memory
		if (in.memRead == 1) {
			int addr = in.aluResult / 4;
			if (addr >= 0 && addr < mem.length) {
				out.memResult = mem[addr];
			} else {
				// Default if out of bounds
				out.memResult = 0;
			}
		}
		
		// If SW, write to memory
		if (in.memWrite == 1) {
			int addr = in.aluResult / 4;
			
			// If previous instruction wrote to rt, then forward to store data
			if (old_memWb.regWrite == 1 && old_memWb.writeReg != 0 && old_memWb.writeReg == in.rt) {
				in.rtVal = (old_memWb.memToReg == 1) ? old_memWb.memResult : old_memWb.aluResult;
			}
			
			// Performs write to memory
			if (addr >= 0 && addr < mem.length) {
				mem[addr] = in.rtVal;
			}
		}
	}

	/*
	 * execute_WB
	 *
	 * Purpose:
	 * 	Implements the Write Back phase. Writes results into register.
	 * 
	 * Parameters:
	 * 	MEM_WB in (input) : Register holding values from MEM
	 * 	int[] regs (input/output) : The register file to be modified if regWrite is activated
	 * 
	 * PreConditions:
	 * 	MEM_WB is correctly filled from MEM
	 * 	regs is an array of integers representing registers
	 * 
	 * PostConditions:
	 * 	If regWrite == 1 & writeReg != 0, regs[writeReg] updated (memOut or aluResult)
	 * 	Otherwise, no update
	 * 
	 * Returns:
	 * 	None
	 */
	public static void execute_WB(MEM_WB in, int[] regs) {
		// Write if write is activated. Not to $zero register
		if (in.regWrite == 1 && in.writeReg != 0) {
			if (in.memToReg == 0) {
				// Write from result from ALU
				regs[in.writeReg] = in.aluResult;
			} else if (in.memToReg == 1) {
				// Write from result from memory.
				regs[in.writeReg] = in.memResult;
			}
		}
	}

}
