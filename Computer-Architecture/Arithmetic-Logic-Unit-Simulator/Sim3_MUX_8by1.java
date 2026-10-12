/*
 * author: Eric Romero
 * Sim3_MUX_8by1.java
 * purpose: This class models a 8-input MUX.
 */
public class Sim3_MUX_8by1 {
	
	public void execute() {
		// Extracts control signals
		boolean sig0 = control[0].get();		
		boolean sig1 = control[1].get();
		boolean sig2 = control[2].get();		
		
		// Determines input to choose from control signals and index
		out.set( (!sig2 && !sig1 && !sig0 && in[0].get()) ||
				 (!sig2 && !sig1 && sig0 && in[1].get()) ||
				 (!sig2 && sig1 && !sig0 && in[2].get()) ||
				 (!sig2 && sig1 && sig0 && in[3].get()) ||
				 (sig2 && !sig1 && !sig0 && in[4].get()) ||
				 (sig2 && !sig1 && sig0 && in[5].get()) ||
				 (sig2 && sig1 && !sig0 && in[6].get()) ||
				 (sig2 && sig1 && sig0 && in[7].get()));
	}
	
	// Inputs
	public RussWire[] control;

	public RussWire[] in;
	// Output
	public RussWire out;
	
	public Sim3_MUX_8by1() {
		// All objects initialized as RussWires
		
		// Initialize Input : Control bits
		control = new RussWire[3];
		
		for (int i = 0; i < 3; i++) {
			control[i] = new RussWire();
		}
		
		// Initialize Input : in bits
		in = new RussWire[8];

		for (int i = 0; i < 8; i++) {
			in[i] = new RussWire();
		}
		
		// Initialize Output
		out = new RussWire();
	}

}
