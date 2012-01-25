package dk.medicinkortet.dosisstructuretext.vowrapper;

import java.math.BigDecimal;

import dk.medicinkortet.web.shared.jaxb.dkma.medicinecard.DosageTimeElementStructure;

public class PlainDoseWrapper extends DoseWrapper {

	public PlainDoseWrapper(DosageTimeElementStructure dose) {
		super(dose);
	}

	public PlainDoseWrapper(dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageTimeElementStructure dose) {
		super(dose);
	}

	private PlainDoseWrapper(
			BigDecimal doseQuantity, BigDecimal minimalDoseQuantity, BigDecimal maximalDoseQuantity, 
			String doseQuantityString, String minimalDoseQuantityString, String maximalDoseQuantityString) {
		super(doseQuantity, minimalDoseQuantity, maximalDoseQuantity, doseQuantityString, minimalDoseQuantityString, maximalDoseQuantityString);
	}

	public static PlainDoseWrapper makeDose(BigDecimal quantity) {
		return new PlainDoseWrapper(quantity, null, null, null, null, null);
	}

	public static PlainDoseWrapper makeDose(BigDecimal quantity, String supplText) {
		return new PlainDoseWrapper(quantity, null, null, supplText, null, null);
	}
	
	public static PlainDoseWrapper makeDose(BigDecimal minimalQuantity, BigDecimal maximalQuantity) {
		return new PlainDoseWrapper(null, minimalQuantity, maximalQuantity, null, null, null);
	}	

	public static PlainDoseWrapper makeDose(BigDecimal minimalQuantity, BigDecimal maximalQuantity, String minimalSupplText, String maximalSupplText) {
		return new PlainDoseWrapper(null, minimalQuantity, maximalQuantity, null, minimalSupplText, maximalSupplText);
	}	
		
	public final static String LABEL = "";  
	
	@Override
	public String getLabel() {
		return LABEL;
	}

}
