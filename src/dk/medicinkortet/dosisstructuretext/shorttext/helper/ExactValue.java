package dk.medicinkortet.dosisstructuretext.shorttext.helper;


public class ExactValue implements Value {
		
	private double exactValue;
	
	public ExactValue(double exactValue) {
		this.exactValue = exactValue;
	}
		
	public double getExactValue() {
		return exactValue;
	}
	
}
