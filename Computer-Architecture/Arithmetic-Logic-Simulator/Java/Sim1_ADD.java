/* Simulates a physical device that performs (signed) addition on
 * a 32-bit input.
 *
 * Author: Eric Romero
 */

public class Sim1_ADD {
	public void execute() {
		boolean currA;
		boolean currB;
		boolean carryIn = false;

		// TODO: fill this in!
		for (int i = 0; i < 32; i++) {
			currA = this.a[i].get();
			currB = this.b[i].get();

			// Input: a = 0, b = 0, carryIn = 0
			// Output: sum = 0, carryIn = 0
			if (currA == false && currB == false && carryIn == false) {
				this.sum[i].set(false);
				carryIn = false;
			}
			// Input: a = 0, b = 1, carryIn = 0
			// Output:sum = 1, carryIn = 0
			else if (currA == false && currB == true && carryIn == false) {
				this.sum[i].set(true);
				carryIn = false;

			}

			// Input: a = 1, b = 0, carryIn = 0
			// Output: sum = 1, carryIn = 0
			else if (currA == true && currB == false && carryIn == false) {
				this.sum[i].set(true);
				carryIn = false;

			}

			// Input: a = 1, b = 1, carryIn = 0
			// Output: sum = 0, carryIn = 1
			else if (currA == true && currB == true && carryIn == false) {
				this.sum[i].set(false);
				carryIn = true;

			}

			// Input: a = 0, b = 0, carryIn = 1
			// Output: sum = 1, carryIn = 0
			else if (currA == false && currB == false && carryIn == true) {
				this.sum[i].set(true);
				carryIn = false;

			}

			// Input: a=1, b=0, carryIn=1
			// Output:sum = 0, carryIn = 1
			else if (currA == true && currB == false && carryIn == true) {
				this.sum[i].set(false);
				carryIn = true;

			}

			// Input: a=0, b=1, carryIn=1
			// Output: sum = 0, carryIn = 1
			else if (currA == false && currB == true && carryIn == true) {
				this.sum[i].set(false);
				carryIn = true;

			}

			// Input: a=1, b=1, carryIn=1
			// Output: sum = 1, carryIn = 1
			else if (currA == true && currB == true && carryIn == true) {
				this.sum[i].set(true);
				carryIn = true;

			}

		}

		// Check for carryOut
		if (carryIn == true) {
			carryOut.set(true);
		} else {
			carryOut.set(false);
		}

		// If MSB of a and b are unequal, and no carryIn, there is no overflow.
		if (a[31].get() != b[31].get() && carryIn == false) {
			overflow.set(false);

			// Cases for overflow
			// 1: a = 1, b = 1, sum = 1, carryIn = 0
		} else if (a[31].get() == true && b[31].get() == true && sum[31].get() == true && carryIn == false) {
			overflow.set(true);

			// 2: a = 0, b = 0, sum = 1, carryIn = 0
		} else if (a[31].get() == false && b[31].get() == false && sum[31].get() == true && carryIn == false) {
			overflow.set(true);

			// 3: a = 1, b = 1, sum = 0, carryIn = 1
		} else if (a[31].get() == true && b[31].get() == true && sum[31].get() == false && carryIn == true) {
			overflow.set(true);

			// 4: a = 0, b = 0, sum = 1, carryIn = 1
		} else if (a[31].get() == false && b[31].get() == false && sum[31].get() == true && carryIn == true) {
			overflow.set(true);
		} else {
			overflow.set(false);
		}
	}

	// ------
	// It should not be necessary to change anything below this line,
	// although I'm not making a formal requirement that you cannot.
	// ------

	// inputs
	public RussWire[] a, b;

	// outputs
	public RussWire[] sum;
	public RussWire carryOut, overflow;

	public Sim1_ADD() {
		/*
		 * Instructor's Note:
		 *
		 * In Java, to allocate an array of objects, you need two steps: you first
		 * allocate the array (which is full of null references), and then a loop which
		 * allocates a whole bunch of individual objects (one at a time), and stores
		 * those objects into the slots of the array.
		 */

		a = new RussWire[32];
		b = new RussWire[32];
		sum = new RussWire[32];

		for (int i = 0; i < 32; i++) {
			a[i] = new RussWire();
			b[i] = new RussWire();
			sum[i] = new RussWire();
		}

		carryOut = new RussWire();
		overflow = new RussWire();
	}
}
