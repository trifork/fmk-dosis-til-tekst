package dk.medicinkortet.dosisstructuretext.shorttext.helper;

public class IntervalValue implements Value {
	
	private double minValue;
	private double maxValue;
	
	public IntervalValue(double minValue, double maxValue) {
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	public double getMinValue() {
		return minValue;
	}

	public double getMaxValue() {
		return maxValue;
	}
	
}
