package dk.medicinkortet.dosisstructuretext.vowrapper;

import java.math.BigDecimal;

import dk.medicinkortet.web.shared.jaxb.dkma.medicinecard.DosageDefinedTimeElementStructure;

public class EveningDoseWrapper extends DoseWrapper {

	public EveningDoseWrapper(DosageDefinedTimeElementStructure dose) {
		super(dose);
	}

	public EveningDoseWrapper(dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageDefinedTimeElementStructure dose) {
		super(dose);
	}
	
	private EveningDoseWrapper(
			BigDecimal doseQuantity, BigDecimal minimalDoseQuantity, BigDecimal maximalDoseQuantity, 
			String doseQuantityString, String minimalDoseQuantityString, String maximalDoseQuantityString) {
		super(doseQuantity, minimalDoseQuantity, maximalDoseQuantity, doseQuantityString, minimalDoseQuantityString, maximalDoseQuantityString);
	}

	public static EveningDoseWrapper makeDose(BigDecimal quantity) {
		return new EveningDoseWrapper(quantity, null, null, null, null, null);
	}

	public static EveningDoseWrapper makeDose(BigDecimal quantity, String supplText) {
		return new EveningDoseWrapper(quantity, null, null, supplText, null, null);
	}
	
	public static EveningDoseWrapper makeDose(BigDecimal minimalQuantity, BigDecimal maximalQuantity) {
		return new EveningDoseWrapper(null, minimalQuantity, maximalQuantity, null, null, null);
	}	

	public static EveningDoseWrapper makeDose(BigDecimal minimalQuantity, BigDecimal maximalQuantity, String minimalSupplText, String maximalSupplText) {
		return new EveningDoseWrapper(null, minimalQuantity, maximalQuantity, null, minimalSupplText, maximalSupplText);
	}	
	
	public final static String LABEL = "aften";  

	@Override
	public String getLabel() {
		return LABEL;
	}

}
