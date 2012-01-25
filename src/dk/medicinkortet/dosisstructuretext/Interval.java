package dk.medicinkortet.dosisstructuretext;

public class Interval<T extends Number> {

	private T minimumValue;
	private T maximumValue;

	public Interval(T minimumValue, T maximumValue) {
		this.minimumValue = minimumValue;
		this.maximumValue = maximumValue;
	}

	public T getMinimum() {
		return minimumValue;
	}

	public T getMaximum() {
		return maximumValue;
	}
		
	public String toString() {
		return "["+minimumValue+","+maximumValue+"]";
	}
	
}
