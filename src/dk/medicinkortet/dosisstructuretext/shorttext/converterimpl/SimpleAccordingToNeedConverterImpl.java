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
		if(dosageTimesStructure.queryForInt("//*:DosageTimesIterationIntervalQuantity/integer()") != 0)
			return false;
		if(dosageTimesStructure.queryForSize("///*:DosageTimesIterationIntervalQuantity") != 1)
			return false;
		if(dosageTimesStructure.queryForInt("//*:DosageTimesIterationIntervalQuantity[0]/integer()") != 0)
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
		if(dosageTimesStructure.queryForSize("//*:AccordingToNeedDosageTimeElementStructure/*:DosageQuantityStructure") != 1 &&
		   dosageTimesStructure.queryForSize("//*:AccordingToNeedDosageTimeElementStructure/*:MinimalDosageQuantityStructure") != 1) 
			return false;

		return true;
	}
	
	protected void doConvert(Node dosageTimesStructure) throws XPathException {
		
		// dosage
		Object dosageOrDosageinterval = dosageTimesStructure.query("//*:DosageQuantityValue/text()");
		if(dosageOrDosageinterval instanceof String)
			append(TextHelper.decimalToFraction((String)dosageOrDosageinterval));
		else {
			append(TextHelper.decimalToFraction(((String[])dosageOrDosageinterval)[0]));
			append("-");
			append(TextHelper.decimalToFraction(((String[])dosageOrDosageinterval)[1]));
		}			
		append(" ");

		// dosage unit
		String unit = (String)dosageTimesStructure.query("/*:DosageTimesStructure/*:DosageQuantityUnitText/text()");
		if(dosageOrDosageinterval instanceof String && Double.parseDouble((String)dosageOrDosageinterval)==1.0) {
			append(TextHelper.unitToSingular(unit));
		}
		else {
			append(TextHelper.unitToPlural(unit));				
		}

		// String "efter behov"
		append(" efter behov");
		
		// Optional free text
		String dosageQuantityFreeText = (String)dosageTimesStructure.query("/*:DosageTimesStructure/*:DosageDayElementStructure/*:AccordingToNeedDosageTimeElementStructure/*:DosageQuantityStructure/*:DosageQuantityFreeText/text()");
		if(dosageQuantityFreeText!=null) {
			append(" ");
			append(dosageQuantityFreeText);
		}
	}
	
}
