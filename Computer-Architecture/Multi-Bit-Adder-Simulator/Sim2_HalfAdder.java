// author: Eric Romero
// Sim2_HalfAdder.java
// Purpose: This class represents a simple adder between two binary digits.


public class Sim2_HalfAdder {
	
	public void execute() {
		// Initializes XOR inputs to calculate sum
		xor.a.set(a.get());
		xor.b.set(b.get());
		xor.execute();
		sum.set(xor.out.get());
		
		// Initializes AND inputs to calculate carry
		and.a.set(a.get());
		and.b.set(b.get());
		and.execute();
		carry.set(and.out.get());
	}
	
	//Inputs
	public RussWire a, b;
	
	// Outputs
	public RussWire sum, carry;

	// Logic Gates	
	public AND and;
	public XOR xor;
	
	public Sim2_HalfAdder() {
		// Initializes RussWires for inputs and outputs
		a = new RussWire();
		b = new RussWire();
		sum = new RussWire();
		carry = new RussWire();
		
		// Initializes AND and XOR logic gates
		and = new AND();
		xor = new XOR();
		
	}

}
