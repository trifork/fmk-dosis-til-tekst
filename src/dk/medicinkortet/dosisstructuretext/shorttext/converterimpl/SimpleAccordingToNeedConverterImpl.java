package dk.medicinkortet.dosisstructuretext.shorttext.converterimpl;

import dk.medicinkortet.dosisstructuretext.TextHelper;
import dk.medicinkortet.dosisstructuretext.simpelxml.Node;
import dk.medicinkortet.dosisstructuretext.simpelxml.parser.XPathException;

/**
 * Conversion of: Simple "according to need" dosage, with or without suppl. dosage free text
 * <p>
 * Example<br>
 * 2: 2 stk efter behov
 */
public class SimpleAccordingToNeedConverterImpl extends ConverterImpl {
	
	protected boolean doTest(Node dosageTimesStructure) throws XPathException {
		Integer interval = dosageTimesStructure.queryForInteger("//*:DosageTimesIterationIntervalQuantity/integer()");
		if(interval != null && interval != 0)
			return false;
		if(dosageTimesStructure.queryForSize("///*:DosageTimesIterationIntervalQuantity") != 1)
			return false;
		Integer interval0 = dosageTimesStructure.queryForInteger("//*:DosageTimesIterationIntervalQuantity[0]/integer()");
		if(interval0 != null && interval0 != 0)
			return false;
		if(dosageTimesStructure.queryForSize("//*:AccordingToNeedDosageTimeElementStructure") != 1)
			return false;
		if(dosageTimesStructure.queryForSize("//*:DosageTimeElementStructure") != 0)
			return false;
		if(dosageTimesStructure.queryForSize("//*:MorningDosageTimeElementStructure") != 0)
			return false;
		if(dosageTimesStructure.queryForSize("//*:NoonDosageTimeElementStructure") != 0)
			return false;
		if(dosageTimesStructure.queryForSize("//*:EveningDosageTimeElementStructure") != 0)
			return false;
		if(dosageTimesStructure.queryForSize("//*:NightDosageTimeElementStructure") != 0)
			return false;
		if(dosageTimesStructure.queryForSize("//*:AccordingToNeedDosageTimeElementStructure/*:DosageQuantityValue") != 1 && // FMK 1.2 exact dosage
		   dosageTimesStructure.queryForSize("//*:AccordingToNeedDosageTimeElementStructure/*:MinimalDosageQuantityValue") != 1 && // FMK 1.2 min-max dosages								
		   dosageTimesStructure.queryForSize("//*:AccordingToNeedDosageTimeElementStructure/*:DosageQuantityStructure") != 1 &&  // FMK 1.0 exact dosage		   		
		   dosageTimesStructure.queryForSize("//*:AccordingToNeedDosageTimeElementStructure/*:MinimalDosageQuantityStructure") != 1) // FMK 1.0 min-max dosages
			return false;

		return true;
	}
	
	protected void doConvert(Node dosageTimesStructure) throws XPathException {
		
		// For FMK 1.2
		String minimalDosageQuantityValue = (String)dosageTimesStructure.query("//*:AccordingToNeedDosageTimeElementStructure[0]/*:MinimalDosageQuantityValue/text()");
		String maximalDosageQuantityValue = (String)dosageTimesStructure.query("//*:AccordingToNeedDosageTimeElementStructure[0]/*:MaximalDosageQuantityValue/text()");
		String dosageQuantityValue = (String)dosageTimesStructure.query("//*:AccordingToNeedDosageTimeElementStructure[0]/*:DosageQuantityValue/text()");

		// For FMK 1.0
		if(minimalDosageQuantityValue==null&&maximalDosageQuantityValue==null&&dosageQuantityValue==null) {
			minimalDosageQuantityValue = (String)dosageTimesStructure.query("//*:AccordingToNeedDosageTimeElementStructure[0]/*:MinimalDosageQuantityStructure/*:DosageQuantityValue/text()");
			maximalDosageQuantityValue = (String)dosageTimesStructure.query("//*:AccordingToNeedDosageTimeElementStructure[0]/*:MaximalDosageQuantityStructure/*:DosageQuantityValue/text()");
			dosageQuantityValue = (String)dosageTimesStructure.query("//*:AccordingToNeedDosageTimeElementStructure[0]/*:DosageQuantityStructure/*:DosageQuantityValue/text()");
		}
				
		if(dosageQuantityValue!=null) {
			append(TextHelper.decimalToFraction(dosageQuantityValue));
		}
		else if(minimalDosageQuantityValue!=null&&maximalDosageQuantityValue!=null) {
			append(TextHelper.decimalToFraction(minimalDosageQuantityValue));
			append("-");
			append(TextHelper.decimalToFraction(maximalDosageQuantityValue));			
		}
		else {
			throw new RuntimeException("Error getting dosage quantity values");			
		}
		append(" ");

		// dosage unit
		String unit = (String)dosageTimesStructure.query("/*:DosageTimesStructure/*:DosageQuantityUnitText/text()");
		if(dosageQuantityValue!=null && Double.parseDouble(dosageQuantityValue)==1.0) {
			append(TextHelper.unitToSingular(unit));
		}
		else {
			append(TextHelper.unitToPlural(unit));				
		}

		// String "efter behov"
		append(" efter behov");
		
		// Optional free text
		String dosageSupplementaryText = (String)dosageTimesStructure.query("//*:DosageSupplementaryText/text()");
		if(dosageSupplementaryText!=null) { 
			append(" ");
			append(dosageSupplementaryText);
			
		}
		else {	
			String dosageQuantityFreeText = (String)dosageTimesStructure.query("/*:DosageTimesStructure/*:DosageDayElementStructure/*:AccordingToNeedDosageTimeElementStructure/*:DosageQuantityStructure/*:DosageQuantityFreeText/text()");
			if(dosageQuantityFreeText!=null) {
				append(" ");
				append(dosageQuantityFreeText);
			}
		}
		
	}
	
}
