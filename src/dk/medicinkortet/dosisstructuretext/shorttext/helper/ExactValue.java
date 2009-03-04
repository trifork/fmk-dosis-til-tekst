package dk.medicinkortet.dosisstructuretext.shorttext.helper;

import java.math.BigDecimal;

public class ExactValue implements Value {
		
	private double exactValue;
	
	public ExactValue(double exactValue) {
		this.exactValue = exactValue;
	}
		
	public double getExactValue() {
		return exactValue;
	}
	
}
