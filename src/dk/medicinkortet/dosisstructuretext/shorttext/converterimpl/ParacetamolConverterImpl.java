package dk.medicinkortet.dosisstructuretext.shorttext.converterimpl;

import dk.medicinkortet.dosisstructuretext.TextHelper;
import dk.medicinkortet.dosisstructuretext.simpelxml.Node;
import dk.medicinkortet.dosisstructuretext.simpelxml.parser.XPathException;

/**
 * Conversion of dosages like "1-2 stk 3-4 gange daglig"
 * <p>
 * Example:<br>
 * 637: "1-2 stk 3-4 gange daglig"
 */
public class ParacetamolConverterImpl extends ConverterImpl {

	protected boolean doTest(Node dosageTimesStructure) throws XPathException {
		Integer interval = dosageTimesStructure.queryForInteger("//*:DosageTimesIterationIntervalQuantity/integer()");
		if(interval != null && interval != 1)
			return false;
		if(dosageTimesStructure.queryForSize("///*:DosageTimesIterationIntervalQuantity") != 1)
			return false;
		if(dosageTimesStructure.queryForSize("//*:DosageDayIdentifier[0]") != 1)
			return false;
		if(dosageTimesStructure.queryForSize("//*:AccordingToNeedDosageTimeElementStructure") == 0)
			return false;
		if(dosageTimesStructure.queryForSize("//*:DosageTimeElementStructure") == 0)
			return false;
		if(dosageTimesStructure.queryForSize("//*:MorningDosageTimeElementStructure") != 0)
			return false;
		if(dosageTimesStructure.queryForSize("//*:NoonDosageTimeElementStructure") != 0)
			return false;
		if(dosageTimesStructure.queryForSize("//*:EveningDosageTimeElementStructure") != 0)
			return false;
		if(dosageTimesStructure.queryForSize("//*:NightDosageTimeElementStructure") != 0)
			return false;
		if(dosageTimesStructure.queryForSize("//*:DosageQuantityFreeText")>0 &&
		   dosageTimesStructure.queryForSize("//*:DosageQuantityFreeText") != dosageTimesStructure.queryForSize("//*:DosageQuantityValue"))
			return false;
		if(!allEquals(dosageTimesStructure.query("//*:DosageQuantityFreeText")))
			return false;
		if(dosageTimesStructure.queryForSize("//*:MinimalDosageQuantityValue") == 0 &&
		   dosageTimesStructure.queryForSize("//*:MinimalDosageQuantityStructure") == 0 )
			return false;
		if(dosageTimesStructure.queryForSize("//*:MaximalDosageQuantityValue") == 0 &&
		   dosageTimesStructure.queryForSize("//*:MaximalDosageQuantityStructure") == 0)
			return false;
		if(!allEquals(dosageTimesStructure.query("//*:MinimalDosageQuantityValue")))
			return false;
		if(!allEquals(dosageTimesStructure.query("//*:MaximalDosageQuantityValue")))
			return false;
		if(!allEquals(dosageTimesStructure.query("//*:MinimalDosageQuantityStructure")))
			return false;
		if(!allEquals(dosageTimesStructure.query("//*:MaximalDosageQuantityStructure")))
			return false;
		return true;
	}
	
	protected void doConvert(Node dosageTimesStructure) throws XPathException {

		// For FMK 1.2
		String minimalDosageQuantityValue = (String)dosageTimesStructure.query("//*:DosageTimeElementStructure[0]/*:MinimalDosageQuantityValue/text()");
		String maximalDosageQuantityValue = (String)dosageTimesStructure.query("//*:DosageTimeElementStructure[0]/*:MaximalDosageQuantityValue/text()");
		String dosageQuantityValue = (String)dosageTimesStructure.query("//*:DosageTimeElementStructure[0]/*:DosageQuantityValue/text()");

		// For FMK 1.0
		if(minimalDosageQuantityValue==null&&maximalDosageQuantityValue==null&&dosageQuantityValue==null) {
			minimalDosageQuantityValue = (String)dosageTimesStructure.query("//*:DosageTimeElementStructure[0]/*:MinimalDosageQuantityStructure/*:DosageQuantityValue/text()");
			maximalDosageQuantityValue = (String)dosageTimesStructure.query("//*:DosageTimeElementStructure[0]/*:MaximalDosageQuantityStructure/*:DosageQuantityValue/text()");
			dosageQuantityValue = (String)dosageTimesStructure.query("//*:DosageTimeElementStructure[0]/*:DosageQuantityStructure/*:DosageQuantityValue/text()");
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
		append(" ");
		
		// number of times daily
		int fixed = dosageTimesStructure.queryForSize("//*:DosageTimeElementStructure");
		int pn = dosageTimesStructure.queryForSize("//*:AccordingToNeedDosageTimeElementStructure");
		
		append(fixed);
		append("-");
		append(pn+fixed);
		
		// String "gange daglig"
		append(" gange daglig");	
		
		// supplementary text for FMK 1.2
		String dosageSupplementaryText = (String)dosageTimesStructure.query("//*:DosageSupplementaryText/text()");
		if(dosageSupplementaryText!=null) {
			append(" ");
			append(dosageSupplementaryText);
		}
		else { // free text for FMK 1.0 / 1.1
			String dosageQuantityFreeText = (String)dosageTimesStructure.query("///*:DosageQuantityFreeText[0]/text()");
			if(dosageQuantityFreeText!=null) {
				append(" ");
				append(dosageQuantityFreeText);
			}
		}

				
	}
	
}
