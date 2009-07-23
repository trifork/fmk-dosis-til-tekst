package dk.medicinkortet.dosisstructuretext.shorttext.converterimpl;

import dk.medicinkortet.dosisstructuretext.TextHelper;
import dk.medicinkortet.dosisstructuretext.simpelxml.Node;
import dk.medicinkortet.dosisstructuretext.simpelxml.parser.XPathException;

/**
 * Conversion of: Simple non repeated dosage (like "according to need") with suppl. dosage free text. All dosages the same.
 * <p>
 * Example:<br> 
 * 204: 1 plaster 5 timer f&oslash;r virkning &oslash;nskes, 
 */
public class SimpleNonRepeatedConverterImpl extends ConverterImpl {
	
	protected boolean doTest(Node dosageTimesStructure) throws XPathException {
		if(dosageTimesStructure.queryForInt("//*:DosageTimesIterationIntervalQuantity/integer()") != 0)
			return false;
		if(dosageTimesStructure.queryForSize("//*:DosageDayIdentifier") != 1)
			return false;
		if(dosageTimesStructure.queryForInt("//*:DosageDayIdentifier[0]/integer()") != 0)
			return false;
		if(dosageTimesStructure.queryForSize("//*:AccordingToNeedDosageTimeElementStructure") != 0)
			return false;
		if(dosageTimesStructure.queryForSize("//*:DosageTimeElementStructure") != 1)
			return false;
		if(dosageTimesStructure.queryForSize("//*:MorningDosageTimeElementStructure") != 0)
			return false;
		if(dosageTimesStructure.queryForSize("//*:NoonDosageTimeElementStructure") != 0)
			return false;
		if(dosageTimesStructure.queryForSize("//*:EveningDosageTimeElementStructure") != 0)
			return false;
		if(dosageTimesStructure.queryForSize("//*:NightDosageTimeElementStructure") != 0)
			return false;
		if(dosageTimesStructure.queryForSize("//*:DosageQuantityValue") != 1)
			return false;
		if(dosageTimesStructure.queryForSize("//*:DosageSupplementaryText") != 1 && dosageTimesStructure.queryForSize("//*:DosageQuantityFreeText") != 1)
			return false;
		return true;
	}
	
	protected void doConvert(Node dosageTimesStructure) throws XPathException {
		// dosage
		append(TextHelper.decimalToFraction((String)dosageTimesStructure.query("//*:DosageQuantityValue[0]/text()")));
		append(" ");
		
		// dosage unit
		if( ((Double)dosageTimesStructure.query("//*:DosageQuantityValue[0]/double()")).doubleValue() == 1.0 )
			append(TextHelper.unitToSingular((String)dosageTimesStructure.query("//*:DosageQuantityUnitText/text()")));
		else 
			append(TextHelper.unitToPlural((String)dosageTimesStructure.query("//*:DosageQuantityUnitText/text()")));
		append(" ");

		// free text
		String dosageSupplementaryText = (String)dosageTimesStructure.query("//*:DosageSupplementaryText/text()");
		if(dosageSupplementaryText!=null) {			
			append(dosageSupplementaryText);
		}
		else {			
			String dosageQuantityFreeText = (String)dosageTimesStructure.query("//*:DosageQuantityFreeText/text()");
			append(dosageQuantityFreeText);
		}
	}
	
}
