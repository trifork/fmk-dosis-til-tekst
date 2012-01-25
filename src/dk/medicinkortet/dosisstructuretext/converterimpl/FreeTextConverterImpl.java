package dk.medicinkortet.dosisstructuretext.converterimpl;

import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;

public class FreeTextConverterImpl extends ShortTextConverterImpl {

	@Override
	public boolean canConvert(DosageWrapper dosage) {
		return dosage.getFreeText()!=null;
	}

	@Override
	public String doConvert(DosageWrapper dosage) {
		return dosage.getFreeText();
	}

}
