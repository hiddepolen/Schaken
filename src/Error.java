
public class Error {
	Error (Object o, int n) {
		Info.addText ("Error (" + o.getClass () + "): " + n + ".");
	}
	Error (Class<?> c, int n) {
		Info.addText ("Error (" + c + "): " + n + ".");
	}
}
