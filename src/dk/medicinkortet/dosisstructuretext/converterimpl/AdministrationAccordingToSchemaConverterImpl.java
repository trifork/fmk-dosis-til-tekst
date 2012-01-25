package dk.medicinkortet.dosisstructuretext.converterimpl;

import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;

public class AdministrationAccordingToSchemaConverterImpl extends ShortTextConverterImpl {

	@Override
	public boolean canConvert(DosageWrapper dosage) {
		return dosage.isAdministrationAccordingToSchema();
	}

	@Override
	public String doConvert(DosageWrapper dosage) {
		return "Dosering efter skema i lokalt system";
	}

}
