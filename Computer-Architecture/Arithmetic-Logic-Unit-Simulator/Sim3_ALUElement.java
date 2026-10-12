/*
 * author: Eric Romero
 * Sim3_ALUElement.java
 * purpose: This class represents an element belonging to the ALU class
 */
public class Sim3_ALUElement {
	
	public void execute_pass1() {
		// Negates b if flagged
		boolean bValue = b.get() ^ bInvert.get();
				
		// Performs full adder logic
		addResult.set((a.get() ^ bValue) ^ carryIn.get());
		carryOut.set((a.get() && bValue) || ((a.get() ^ bValue) && carryIn.get()));
		
	}
	
	public void execute_pass2() {
		boolean op0 = aluOp[0].get();
		boolean op1 = aluOp[1].get();
		boolean op2 = aluOp[2].get();
		
		// Determines aluOp operations
		boolean isAnd = !op2 && !op1 && !op0;
		boolean isOr = !op2 && !op1 && op0;
		boolean isXor = op2 && !op1 && !op0;
		boolean isAdd = !op2 && op1 && !op0;
		boolean isLess = !op2 && op1 && op0;
		
		// Finds result per operation
		boolean andResult = a.get() && b.get();
		boolean orResult = a.get() || b.get();
		boolean xorResult = a.get() ^ b.get();
		boolean lessResult = less.get();
		boolean addResultFinal = addResult.get();
		
		// Final result based on flagged operation
		result.set((isAnd && andResult) || 
				(isOr && orResult) || 
				(isXor && xorResult) || 
				(isAdd && addResultFinal) || 
				(isLess && lessResult));
		
	}
	// Inputs
	
	public RussWire[] aluOp;
	public RussWire bInvert;
	public RussWire a;
	public RussWire b;
	public RussWire carryIn;
	public RussWire less;
	
	// Outputs
	public RussWire result;
	public RussWire addResult;
	public RussWire carryOut;
		
	public Sim3_ALUElement() {
		// All objects initialized as RussWires
		
		// Inputs
		aluOp = new RussWire[3];
		
		for (int i = 0; i < 3; i++) {
			aluOp[i] = new RussWire();
		}
		
		bInvert = new RussWire();
		a = new RussWire();
		b = new RussWire();
		carryIn = new RussWire();
		less = new RussWire();
		
		// Outputs
		result = new RussWire();
		addResult = new RussWire();
		carryOut = new RussWire();
		 	
	}

}
