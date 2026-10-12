// author: Eric Romero
// Sim2_FullAdder.java
// Purpose: This class represents an adder of three bits using half adders.
public class Sim2_FullAdder {
	public void execute() {
		// Computes sum between a and b
		halfAdderA.a.set(a.get());
		halfAdderA.b.set(b.get());
		halfAdderA.execute();
		
		// Computes sum between previous sum and carryIn
		halfAdderB.a.set(halfAdderA.sum.get());
		halfAdderB.b.set(carryIn.get());
		halfAdderB.execute();
		
		// Retrieves sum from previous half adder.
		sum.set(halfAdderB.sum.get());
		// Final carry out is the OR between the half adders
		or.a.set(halfAdderA.carry.get());
		or.b.set(halfAdderB.carry.get());
		or.execute();

		carryOut.set(or.out.get());
	}
	
	// Inputs
	public RussWire a, b, carryIn;
	// Outputs
	public RussWire sum, carryOut;
	// Half adders
	public Sim2_HalfAdder halfAdderA, halfAdderB;

	// OR Logic Gate
	public OR or;
	
	public Sim2_FullAdder() {
		// Initialize inputs as RussWires
		a = new RussWire();
		b = new RussWire();
		carryIn = new RussWire();
		
		// Initialize outputs as RussWires
		sum = new RussWire();
		carryOut = new RussWire();
		
		// Initialize half adders.
		halfAdderA = new Sim2_HalfAdder();
		halfAdderB = new Sim2_HalfAdder();

		// Initialize OR gate
		or = new OR();
	}

}
