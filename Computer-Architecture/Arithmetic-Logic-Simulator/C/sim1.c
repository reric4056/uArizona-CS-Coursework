/* Implementation of a 32-bit adder in C.
 *
 * Author: Eric Romero
 */


#include "sim1.h"

int getBit(int num, int pos)
{
	if (((num >> pos) & 1) != 0)
	{
		return 1;
	}
	else
	{
		return 0;
	}
}

int setBit(int num, int pos, int val)
{
	return num | val << pos;
}

void execute_add(Sim1Data *obj)
{
	// TODO: implement me!
	int carryIn = 0;
	int aVal;
	int bVal;
	int sub = obj->isSubtraction;
	int carry = 0;
	
	if (sub)
	{
		carryIn = 1;
	}

	for (int i = 0; i < 32; i++) {
		aVal = getBit(obj->a, i);
		
		if(sub)
		{
			bVal = !getBit(obj->b, i);
		}
		else
		{
			bVal = getBit(obj->b, i);
		}

		if (carryIn == 0)
		{
			if (aVal && bVal)
			{
				obj->sum = setBit(obj->sum, i, 0);
				carryIn = 1;
				carry = 0;
			}
			else if (aVal || bVal)
			{
				obj->sum = setBit(obj->sum, i, 1);
				carryIn = 0;
				carry = 1;
			}
			else
			{
				obj->sum = setBit(obj->sum, i, 0);
				carryIn = 0;
				carry = 0;
			}
		}
		else 
		{
			if (aVal && bVal)
			{
				obj->sum = setBit(obj->sum, i, 1);
				carryIn = 1;
				carry = 0;
			}
			else if (aVal || bVal)
			{
				obj->sum = setBit(obj->sum, i, 0);
				carryIn = 1;
				carry = 0;
			}
			else 
			{
				obj->sum = setBit(obj->sum, i, 1);
				carryIn = 0;
				carry = 1;
			
			}
		}


	}

	obj->carryOut = carryIn;
	obj->aNonNeg = !aVal;
	obj->bNonNeg = !getBit(obj->b, 31);
	obj->sumNonNeg = !getBit(obj->sum,31);
	if ((aVal && bVal) && carry == 0) {
		obj->overflow =1;
	}

}
