package dk.medicinkortet.dosisstructuretext;

import java.math.BigDecimal;

public class DailyDosis {
	
	private BigDecimal value;
	private Interval<BigDecimal> interval;
	private String unit;
	
	public DailyDosis() {
	}

	public DailyDosis(BigDecimal value, String unit) {
		this.value = value;
		this.unit = unit;
	}
	
	public DailyDosis(Interval<BigDecimal> interval, String unit) {
		this.interval = interval;
		this.unit = unit;
	}
	
	public BigDecimal getValue() {
		return value;
	}
	
	public Interval<BigDecimal> getInterval() {
		return interval;
	}
	
	public String getUnit() {
		return unit;
	}
	
	public boolean isValue() {
		return value!=null;
	}
	
	public boolean isInterval() {
		return interval!=null;
	}
	
	public boolean isNone() {
		return value==null && interval==null;
	}
	
}
