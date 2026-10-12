/* Simulates a physical device that performs 2's complement on a 32-bit input.
 *
 * Author: Eric Romero
 */

public class Sim1_2sComplement
{
	public void execute()
	{
		// TODO: fill this in!
		//
		// REMEMBER: You may call execute() on sub-objects here, and
		//           copy values around - but you MUST NOT create
		//           objects while inside this function.
		
		for (int i = 0; i < 32; i++) {
			notIn[i].in = in[i];
			notIn[i].execute();
			negatedIn[i].set(notIn[i].out.get());
		}
		
		add.a = negatedIn;
		add.b = add1;
		add.execute();
		out = add.sum;
	}



	// you shouldn't change these standard variables...
	public RussWire[] in;
	public RussWire[] out;


	// TODO: add some more variables here.  You must create them
	//       during the constructor below.  REMEMBER: You're not
	//       allowed to create any object inside the execute()
	//       method above!
	
	public Sim1_NOT[] notIn;
	public Sim1_ADD add;
	
	public RussWire[] negatedIn;
	public RussWire[] add1;


	public Sim1_2sComplement()
	{
		// TODO: this is where you create the objects that
		//       you declared up above.
		in = new RussWire[32];
		out = new RussWire[32];
		
		notIn = new Sim1_NOT[32];
		add = new Sim1_ADD();
		
		negatedIn = new RussWire[32];
		add1 = new RussWire[32];
		
		
		for (int i = 0; i < 32; i++) {
			in[i] = new RussWire();
			out[i] = new RussWire();
			notIn[i] = new Sim1_NOT();
			negatedIn[i] = new RussWire();
			add1[i] = new RussWire();
		}
		
		add1[0].set(true);
		for (int i = 1; i < 32; i++) {
			add1[i].set(false);
		}
	}
}

