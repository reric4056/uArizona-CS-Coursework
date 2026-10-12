/* Simulates a physical AND gate.
 *
 * Author: Eric Romero
 */

public class Sim1_AND {
	public void execute() {
		// Boolean AND performed and stored in 'out'.
		out.set(a.get() && b.get());

	}

	public RussWire a, b; // inputs
	public RussWire out; // output

	public Sim1_AND() {
		// TODO: fill this in!
		a = new RussWire();
		b = new RussWire();
		out = new RussWire();

	}
}
