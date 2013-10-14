package dk.medicinkortet.dosagetranslation;

import dk.medicinkortet.dosisstructuretext.LongTextConverter;
import dk.medicinkortet.dosisstructuretext.ShortTextConverter;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;

public class DosageToTextTranslator {

	public static void translate(DosageWrappers dosageWrappers, RawDefinitions rawDefinitions) {
		for(int rowNumber: dosageWrappers.getRowNumberIterator()) {		
			translate(dosageWrappers.getDosageWrapper(rowNumber), rawDefinitions.get(rowNumber));
		}
	}
	
	public static void translate(DosageWrapper dosageWrapper, RawDefinition rawDefinition) {
		if(dosageWrapper!=null && rawDefinition!=null) {
			String shortText = ShortTextConverter.convert(dosageWrapper);
			String longText = LongTextConverter.convert(dosageWrapper);
			rawDefinition.setDosageTranslation(shortText, longText);
		}
	}

}
