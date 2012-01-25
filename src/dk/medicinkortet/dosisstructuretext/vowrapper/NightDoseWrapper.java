package dk.medicinkortet.dosisstructuretext.vowrapper;

import java.math.BigDecimal;

import dk.medicinkortet.web.shared.jaxb.dkma.medicinecard.DosageDefinedTimeElementStructure;

public class NightDoseWrapper extends DoseWrapper {

	public NightDoseWrapper(DosageDefinedTimeElementStructure dose) {
		super(dose);
	}

	public NightDoseWrapper(dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageDefinedTimeElementStructure dose) {
		super(dose);
	}

	private NightDoseWrapper(
			BigDecimal doseQuantity, BigDecimal minimalDoseQuantity, BigDecimal maximalDoseQuantity, 
			String doseQuantityString, String minimalDoseQuantityString, String maximalDoseQuantityString) {
		super(doseQuantity, minimalDoseQuantity, maximalDoseQuantity, doseQuantityString, minimalDoseQuantityString, maximalDoseQuantityString);
	}

	public static NightDoseWrapper makeDose(BigDecimal quantity) {
		return new NightDoseWrapper(quantity, null, null, null, null, null);
	}

	public static NightDoseWrapper makeDose(BigDecimal quantity, String supplText) {
		return new NightDoseWrapper(quantity, null, null, supplText, null, null);
	}
	
	public static NightDoseWrapper makeDose(BigDecimal minimalQuantity, BigDecimal maximalQuantity) {
		return new NightDoseWrapper(null, minimalQuantity, maximalQuantity, null, null, null);
	}	

	public static NightDoseWrapper makeDose(BigDecimal minimalQuantity, BigDecimal maximalQuantity, String minimalSupplText, String maximalSupplText) {
		return new NightDoseWrapper(null, minimalQuantity, maximalQuantity, null, minimalSupplText, maximalSupplText);
	}	
	
	public final static String LABEL = "nat";  
	
	@Override
	public String getLabel() {
		return LABEL;
	}

}
