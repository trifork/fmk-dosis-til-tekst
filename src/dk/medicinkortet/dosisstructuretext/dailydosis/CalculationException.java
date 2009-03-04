package dk.medicinkortet.dosisstructuretext.dailydosis;

/*
 * Thrown when the calculation of avg. daily dosis fails
 */
public class CalculationException extends Exception {

	public CalculationException(String message) {
		super(message);
	}
	
}
