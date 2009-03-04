package dk.medicinkortet.dosisstructuretext;

/**
 * Thrown from the Validator class when a business rule for the XML isn't met   
 */
public class DosageValidationException extends Exception {
	
	/**
	 * Constructs a ValidationException with the passed message
	 * @param message
	 */
	public DosageValidationException(String message) {
		super(message);
	}

}
