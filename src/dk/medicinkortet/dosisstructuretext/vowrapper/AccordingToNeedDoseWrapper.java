package dk.medicinkortet.dosisstructuretext.vowrapper;

import java.math.BigDecimal;

import dk.medicinkortet.web.shared.jaxb.dkma.medicinecard.DosageTimeElementStructure;

public class AccordingToNeedDoseWrapper extends DoseWrapper {

	public AccordingToNeedDoseWrapper(DosageTimeElementStructure dose) {
		super(dose);
	}

	public AccordingToNeedDoseWrapper(dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageTimeElementStructure dose) {
		super(dose);
	}
	
	private AccordingToNeedDoseWrapper(
			BigDecimal doseQuantity, BigDecimal minimalDoseQuantity, BigDecimal maximalDoseQuantity, 
			String doseQuantityString, String minimalDoseQuantityString, String maximalDoseQuantityString) {
		super(doseQuantity, minimalDoseQuantity, maximalDoseQuantity, doseQuantityString, minimalDoseQuantityString, maximalDoseQuantityString);
	}

	public static AccordingToNeedDoseWrapper makeDose(BigDecimal quantity) {
		return new AccordingToNeedDoseWrapper(quantity, null, null, null, null, null);
	}

	public static AccordingToNeedDoseWrapper makeDose(BigDecimal quantity, String supplText) {
		return new AccordingToNeedDoseWrapper(quantity, null, null, supplText, null, null);
	}
	
	public static AccordingToNeedDoseWrapper makeDose(BigDecimal minimalQuantity, BigDecimal maximalQuantity) {
		return new AccordingToNeedDoseWrapper(null, minimalQuantity, maximalQuantity, null, null, null);
	}	

	public static AccordingToNeedDoseWrapper makeDose(BigDecimal minimalQuantity, BigDecimal maximalQuantity, String minimalSupplText, String maximalSupplText) {
		return new AccordingToNeedDoseWrapper(null, minimalQuantity, maximalQuantity, null, minimalSupplText, maximalSupplText);
	}	
	
	public final static String LABEL = "efter behov";  
	
	@Override
	public String getLabel() {
		return LABEL;
	}
	
	
}
