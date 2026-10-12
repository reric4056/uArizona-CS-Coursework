/* Simulates a physical OR gate.
 *
 * Author: TODO
 */

public class Sim1_OR {
	public void execute() {
		// Boolean OR performed and stored in 'out'.
		out.set(a.get() || b.get());

	}

	public RussWire a, b; // inputs
	public RussWire out; // output

	public Sim1_OR() {
		// TODO: fill this in!
		a = new RussWire();
		b = new RussWire();
		out = new RussWire();
	}
}
