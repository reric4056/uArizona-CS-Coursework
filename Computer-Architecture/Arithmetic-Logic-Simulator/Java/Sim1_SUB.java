/* Simulates a physical device that performs (signed) subtraction on
 * a 32-bit input.
 *
 * Author: Eric Romero
 */

public class Sim1_SUB
{
	public void execute()
	{
		// TODO: fill this in!
		//
		// REMEMBER: You may call execute() on sub-objects here, and
		//           copy values around - but you MUST NOT create
		//           objects while inside this function.
		
		bComplement.in = b;
		bComplement.execute();
		bComplementOut = bComplement.out;
		
		
		add.a = a;
		add.b = bComplementOut;
		add.execute();
		sum = add.sum;
		
	}



	// --------------------
	// Don't change the following standard variables...
	// --------------------

	// inputs
	public RussWire[] a,b;

	// output
	public RussWire[] sum;

	// --------------------
	// But you should add some *MORE* variables here.
	// --------------------
	// TODO: fill this in
	
	public Sim1_ADD add;
	public Sim1_2sComplement bComplement;
	public RussWire[] bComplementOut;
	


	public Sim1_SUB()
	{
		// TODO: fill this in!
		a = new RussWire[32];
		b = new RussWire[32];
		sum = new RussWire[32];
		
		bComplement = new Sim1_2sComplement();
		bComplementOut = new RussWire[32];
		
		add = new Sim1_ADD();
		
		for (int i = 0; i < 32; i++) {
			a[i] = new RussWire();
			b[i] = new RussWire();
			sum[i] = new RussWire();
			bComplementOut[i] = new RussWire();
		}
		
		
		
	}
}

