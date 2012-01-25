package dk.medicinkortet.dosisstructuretext.vowrapper;

import java.math.BigDecimal;

import dk.medicinkortet.web.shared.jaxb.dkma.medicinecard.DosageDefinedTimeElementStructure;

public class MorningDoseWrapper extends DoseWrapper {

	public MorningDoseWrapper(DosageDefinedTimeElementStructure dose) {
		super(dose);
	}

	public MorningDoseWrapper(dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageDefinedTimeElementStructure dose) {
		super(dose);
	}
	
	private MorningDoseWrapper(
			BigDecimal doseQuantity, BigDecimal minimalDoseQuantity, BigDecimal maximalDoseQuantity, 
			String doseQuantityString, String minimalDoseQuantityString, String maximalDoseQuantityString) {
		super(doseQuantity, minimalDoseQuantity, maximalDoseQuantity, doseQuantityString, minimalDoseQuantityString, maximalDoseQuantityString);
	}

	public static MorningDoseWrapper makeDose(BigDecimal quantity) {
		return new MorningDoseWrapper(quantity, null, null, null, null, null);
	}

	public static MorningDoseWrapper makeDose(BigDecimal quantity, String supplText) {
		return new MorningDoseWrapper(quantity, null, null, supplText, null, null);
	}
	
	public static MorningDoseWrapper makeDose(BigDecimal minimalQuantity, BigDecimal maximalQuantity) {
		return new MorningDoseWrapper(null, minimalQuantity, maximalQuantity, null, null, null);
	}	

	public static MorningDoseWrapper makeDose(double minimalQuantity, double maximalQuantity, String minimalSupplText, String maximalSupplText) {
		return new MorningDoseWrapper(null, toBigDecimal(minimalQuantity), toBigDecimal(maximalQuantity), null, minimalSupplText, maximalSupplText);
	}	
	
	public final static String LABEL = "morgen";  

	@Override
	public String getLabel() {
		return LABEL;
	}

}
