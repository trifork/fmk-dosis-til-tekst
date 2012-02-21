package dk.medicinkortet.dosisstructuretext.vowrapper;

import java.math.BigDecimal;

import dk.medicinkortet.web.shared.jaxb.dkma.medicinecard.DosageTimeElementStructure;

public class TimedDoseWrapper extends DoseWrapper {

	private String time;
	
	public TimedDoseWrapper(DosageTimeElementStructure dose) {
		super(dose);
		this.time = dose.getDosageTimeTime();
	}

	public TimedDoseWrapper(dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageTimeElementStructure dose) {
		super(dose);
		this.time = dose.getDosageTimeTime();
	}

	private TimedDoseWrapper(
			String time, 
			BigDecimal doseQuantity, BigDecimal minimalDoseQuantity, BigDecimal maximalDoseQuantity, 
			String doseQuantityString, String minimalDoseQuantityString, String maximalDoseQuantityString) {
		super(doseQuantity, minimalDoseQuantity, maximalDoseQuantity, doseQuantityString, minimalDoseQuantityString, maximalDoseQuantityString);
		this.time = time;
	}

	public static TimedDoseWrapper makeDose(String time, BigDecimal quantity) {
		return new TimedDoseWrapper(time, quantity, null, null, null, null, null);
	}

	public static TimedDoseWrapper makeDose(String time, BigDecimal quantity, String supplText) {
		return new TimedDoseWrapper(time, quantity, null, null, supplText, null, null);
	}
	
	public static TimedDoseWrapper makeDose(String time, BigDecimal minimalQuantity, BigDecimal maximalQuantity) {
		return new TimedDoseWrapper(time, null, minimalQuantity, maximalQuantity, null, null, null);
	}	

	public static TimedDoseWrapper makeDose(String time, BigDecimal minimalQuantity, BigDecimal maximalQuantity, String minimalSupplText, String maximalSupplText) {
		return new TimedDoseWrapper(time, null, minimalQuantity, maximalQuantity, null, minimalSupplText, maximalSupplText);
	}	
	
	public final static String LABEL = "kl.";  
	
	@Override
	public String getLabel() {
		return LABEL+" "+time;
	}

	public String getTime() {
		return time;
	}
	
	@Override
	public boolean theSameAs(DoseWrapper other) {
		if(!(other instanceof TimedDoseWrapper))
			return false;
		if(!super.theSameAs(other))
			return false;
		return getTime().equals(((TimedDoseWrapper)other).getTime());
	}

}
