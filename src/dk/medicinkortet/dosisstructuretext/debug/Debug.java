package dk.medicinkortet.dosisstructuretext.debug;

/**
 * Very simple debugging/logging class. 
 * Log4j (or another debugging/logging framework) isn't used, as GWT only supports a subset of the Java language.  
 */
public class Debug {

	private boolean debug = false;
	
	/**
	 * Constructor 
	 * @param b enables debug output to console if true
	 */
	public Debug(boolean b) {
		this.debug = b;		
	}
	
	/**
	 * Set if to debug
	 * @param b enables debug output to console if true
	 */
	public void debug(boolean b) {
		this.debug = b;
	}
	
	/**
	 * Debugs to console, if debugging is enabled 
	 * @param o prints o.toString() to console
	 */
	public void println(Object o) {
		if(debug)
			System.out.println(o.toString());
	}
	
}
