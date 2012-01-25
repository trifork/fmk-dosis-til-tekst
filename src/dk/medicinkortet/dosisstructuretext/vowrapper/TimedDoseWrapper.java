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

	public static TimedDoseWrapper makeDose(String time, double quantity) {
		return new TimedDoseWrapper(time, toBigDecimal(quantity), null, null, null, null, null);
	}

	public static TimedDoseWrapper makeDose(String time, double quantity, String supplText) {
		return new TimedDoseWrapper(time, toBigDecimal(quantity), null, null, supplText, null, null);
	}
	
	public static TimedDoseWrapper makeDose(String time, double minimalQuantity, double maximalQuantity) {
		return new TimedDoseWrapper(time, null, toBigDecimal(minimalQuantity), toBigDecimal(maximalQuantity), null, null, null);
	}	

	public static TimedDoseWrapper makeDose(String time, double minimalQuantity, double maximalQuantity, String minimalSupplText, String maximalSupplText) {
		return new TimedDoseWrapper(time, null, toBigDecimal(minimalQuantity), toBigDecimal(maximalQuantity), null, minimalSupplText, maximalSupplText);
	}	
	
	public final static String LABEL = "kl.";  
	
	@Override
	public String getLabel() {
		return LABEL;
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
