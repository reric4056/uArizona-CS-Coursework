/* Simulates a physical NOT gate.
 *
 * Author: Eric Romero
 */

public class Sim1_NOT {
	public void execute() {
		// Negation performed on 'in' and result stored in 'out'
		out.set(!in.get());
	}

	public RussWire in; // input
	public RussWire out; // output

	public Sim1_NOT() {

		in = new RussWire();
		out = new RussWire();
	}
}
