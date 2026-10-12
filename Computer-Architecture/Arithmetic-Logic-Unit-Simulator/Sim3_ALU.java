/*
 * author: Eric Romero
 * Sim3_ALU.java
 * purpose: This class represents a complete ALU.
 */
public class Sim3_ALU {
	
	public void execute() {
		// Handles full adder logic per column of bits
		for (int i = 0; i < bits; i++) {
			aluElements[i].a.set(a[i].get());
			aluElements[i].b.set(b[i].get());
			aluElements[i].bInvert.set(bNegate.get());
			
			// CarryIn based on bNegate if first bit, Else is previous carryOut
			boolean carryInBool = (i == 0 && bNegate.get()) || (i > 0 && aluElements[i - 1].carryOut.get());
			aluElements[i].carryIn.set(carryInBool);
						
			// Sets up aluOp bits for element
			aluElements[i].aluOp[0].set(aluOp[0].get());
			aluElements[i].aluOp[1].set(aluOp[1].get());
			aluElements[i].aluOp[2].set(aluOp[2].get());
			
			// Performs full adder logic
			aluElements[i].execute_pass1();
		}
		
		// Deals with Less. Copies addResult output of MSB to less of LSB
		aluElements[0].less.set(aluElements[bits - 1].addResult.get());
		
		// Sets rest of Less inputs to false
		for (int i = 1; i < bits; i++) {
			aluElements[i].less.set(false);
		}
		
		// Determines final result.
		for (int i = 0; i < bits; i++) {
			aluElements[i].execute_pass2();
			result[i].set(aluElements[i].result.get());
		}
		

	}
	
	// Inputs
	public RussWire[] aluOp;
	public RussWire bNegate;
	public RussWire[] a;
	public RussWire[] b;
	
	// Outputs
	public RussWire[] result;
	
	// 
	private Sim3_ALUElement[] aluElements;
	private int bits;

	public Sim3_ALU(int X) {
		bits = X;
		
		// Inputs
		aluOp = new RussWire[3];
		for (int i = 0; i < 3; i++) {
			aluOp[i] = new RussWire();
		}
		
		bNegate = new RussWire();
	
		// Initialize X-Element arrays
		a = new RussWire[X];
		b = new RussWire[X];
		result = new RussWire[X];
		
		
		aluElements = new Sim3_ALUElement[X];
		
		for(int i = 0; i < X; i++) {
			a[i] = new RussWire();
			b[i] = new RussWire();
			result[i] = new RussWire();
		
			aluElements[i] = new Sim3_ALUElement();
		}
				
	}

}
