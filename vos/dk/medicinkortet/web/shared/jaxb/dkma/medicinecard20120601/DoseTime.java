package dk.medicinkortet.web.shared.jaxb.dkma.medicinecard20120601;

public class DoseTime {

	public static enum Fixed {
		morning, noon, evening, night
	}
	
	private Fixed fixed;
	private String time;
	
	public DoseTime(String value) {
		this.time = value;
	}
	
	public DoseTime(Fixed value) {
		this.fixed = value;
		this.time = value.toString();
	}
	
	public String getTime() {
		return time;
	}
	
	public boolean isMorning() {
		return Fixed.morning == fixed; 
	}

	public boolean isNoon() {
		return Fixed.noon == fixed; 
	}
	
	public boolean isEvening() {
		return Fixed.evening == fixed; 
	}
	
	public boolean isNight() {
		return Fixed.night == fixed; 
	}
	
}
