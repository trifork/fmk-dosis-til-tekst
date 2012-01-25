package dk.medicinkortet.dosisstructuretext.vowrapper;

import java.math.BigDecimal;

import dk.medicinkortet.web.shared.jaxb.dkma.medicinecard.DosageDefinedTimeElementStructure;

public class NoonDoseWrapper extends DoseWrapper {

	public NoonDoseWrapper(DosageDefinedTimeElementStructure dose) {
		super(dose);
	}

	public NoonDoseWrapper(dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageDefinedTimeElementStructure dose) {
		super(dose);
	}
	
	private NoonDoseWrapper(
			BigDecimal doseQuantity, BigDecimal minimalDoseQuantity, BigDecimal maximalDoseQuantity, 
			String doseQuantityString, String minimalDoseQuantityString, String maximalDoseQuantityString) {
		super(doseQuantity, minimalDoseQuantity, maximalDoseQuantity, doseQuantityString, minimalDoseQuantityString, maximalDoseQuantityString);
	}

	public static NoonDoseWrapper makeDose(BigDecimal quantity) {
		return new NoonDoseWrapper(quantity, null, null, null, null, null);
	}

	public static NoonDoseWrapper makeDose(BigDecimal quantity, String supplText) {
		return new NoonDoseWrapper(quantity, null, null, supplText, null, null);
	}
	
	public static NoonDoseWrapper makeDose(BigDecimal minimalQuantity, BigDecimal maximalQuantity) {
		return new NoonDoseWrapper(null, minimalQuantity, maximalQuantity, null, null, null);
	}	

	public static NoonDoseWrapper makeDose(BigDecimal minimalQuantity, BigDecimal maximalQuantity, String minimalSupplText, String maximalSupplText) {
		return new NoonDoseWrapper(null, minimalQuantity, maximalQuantity, null, minimalSupplText, maximalSupplText);
	}	
	
	public final static String LABEL = "middag";  
	
	@Override
	public String getLabel() {
		return LABEL;
	}
	
}
