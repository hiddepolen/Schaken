public class Check {
	Check(Object o, String mes) {
		System.out.println ("Message (" + o.getClass () + "): " + mes);
	}
	Check(Class<?> c, String mes) {
		System.out.println ("Message (" + c + "): " + mes);
	}
	Check(Object o, int mes) {
		System.out.println ("" + o.getClass () + ": " + mes);
	}
	Check(Class<?> c, int mes) {
		System.out.println ("" + c + ": " + mes);
	}

	Check(Object o, int... mes) {
		System.out.print ("" + o.getClass () + ": ");
		for (int i = 0; i < mes.length; i++)
			System.out.print (mes[i] + ", ");
		System.out.println (".");
	}
}
