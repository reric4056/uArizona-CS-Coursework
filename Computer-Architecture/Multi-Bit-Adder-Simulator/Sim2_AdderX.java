// author: Eric Romero
// Sim2_AdderX.java
// purpose: This class represents a multi-bit adder that chained full adders together.
public class Sim2_AdderX {
	
	public void execute() {
		// First bit handler with no carryIn for first adder
		fullAdderArray[0].a.set(a[0].get());
		fullAdderArray[0].b.set(b[0].get());
		fullAdderArray[0].carryIn.set(false);
		fullAdderArray[0].execute();
		
		// Result added to sum array
		sum[0].set(fullAdderArray[0].sum.get());
		
		// Iterates through the rest of the bits; uses reference to previous adder
		for (int i = 1; i < bitNumber; i++) {
			fullAdderArray[i].a.set(a[i].get());
			fullAdderArray[i].b.set(b[i].get());
			fullAdderArray[i].carryIn.set(fullAdderArray[i - 1].carryOut.get());
			fullAdderArray[i].execute(); 
			
			sum[i].set(fullAdderArray[i].sum.get());
		}
		
		// Checks for carry out from MSB
		carryOut.set(fullAdderArray[bitNumber - 1].carryOut.get());
		// Checks for overflow
		xor.a.set(fullAdderArray[bitNumber - 1].carryOut.get());
		xor.b.set(fullAdderArray[bitNumber - 2].carryOut.get());
		xor.execute();
		
		overflow.set(xor.out.get());
	}
	
	// Inputs and Outputs
	public RussWire[] a, b, sum;
	// Extra Outputs
	public RussWire carryOut, overflow;
	// Array of full adders
	public Sim2_FullAdder[] fullAdderArray;

	// XOR logic gate
	public XOR xor;
	// Number of bits in adder
	public int bitNumber;
	
	public Sim2_AdderX(int X) {
		// Variable to store number of bits
		bitNumber = X;
		
		// Initialize input arrays
		a = new RussWire[X];
		b = new RussWire[X];
		// Initialize output array
		sum = new RussWire[X];
		
		// Initializes as RussWires
		for (int i = 0; i < X; i++) {
			a[i] = new RussWire();
			b[i] = new RussWire();
			sum[i] = new RussWire();
		}
		
		// Initialize a single carry out and overflow as single RussWire
		carryOut = new RussWire();
		overflow = new RussWire();
		
		// Initialize array of full adders per bit.
		fullAdderArray = new Sim2_FullAdder[X];
		for (int i = 0; i < X; i++) {
			fullAdderArray[i] = new Sim2_FullAdder();
		}
		// Initializes xor logic gate
		xor = new XOR();
	}

}
