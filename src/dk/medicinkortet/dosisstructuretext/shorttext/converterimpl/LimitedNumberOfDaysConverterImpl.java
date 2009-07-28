package dk.medicinkortet.dosisstructuretext.shorttext.converterimpl;

import dk.medicinkortet.dosisstructuretext.TextHelper;
import dk.medicinkortet.dosisstructuretext.simpelxml.Node;
import dk.medicinkortet.dosisstructuretext.simpelxml.Nodes;
import dk.medicinkortet.dosisstructuretext.simpelxml.parser.XPathException;

/**
 * Conversion of: Dosage limited to N days, the same every day
 * <p>
 * Example:<br>
 * 67: 3 tabletter 4 gange daglig i 3 dage<br>
 * 279: 2 tabletter 2 gange daglig i 6 dage
 */
public class LimitedNumberOfDaysConverterImpl extends ConverterImpl {

	protected boolean doTest(Node dosageTimesStructure) throws XPathException {
		if(dosageTimesStructure.queryForInt("//*:DosageTimesIterationIntervalQuantity/integer()") != 0)
			return false;
		if(!isSequence(dosageTimesStructure.query("//*:DosageDayIdentifier/integer()")))
			return false;
		if(dosageTimesStructure.queryForSize("//*:DosageTimeElementStructure") == 0)
			return false;
		if(!allEquals(dosageTimesStructure.query("//*:DosageTimeElementStructure")))
			return false;
		if(dosageTimesStructure.queryForSize("//*:MorningDosageTimeElementStructure") != 0)
			return false;
		if(dosageTimesStructure.queryForSize("//*:NoonDosageTimeElementStructure") != 0)
			return false;
		if(dosageTimesStructure.queryForSize("//*:EveningDosageTimeElementStructure") != 0)
			return false;
		if(dosageTimesStructure.queryForSize("//*:NightDosageTimeElementStructure") != 0)
			return false;
		if(dosageTimesStructure.queryForSize("//*:DosageQuantityValue") == 0 && dosageTimesStructure.queryForSize("//*:MinimalDosageQuantityValue") == 0)
			return false;
		if(!allEquals(dosageTimesStructure.query("//*:DosageQuantityValue")))
			return false;
		if(!allEquals(dosageTimesStructure.query("//*:DosageTimeElementStructure//*:DosageQuantityStructure")))
			return false;
		if(!allEquals(dosageTimesStructure.query("//*:MinimalDosageQuantityStructure")))
			return false;
		if(!allEquals(dosageTimesStructure.query("//*:MaximalDosageQuantityStructure")))
			return false;
		if(!allEquals(dosageTimesStructure.query("//*:DosageTimeElementStructure//*:DosageQuantityFreeText")))
			return false;
		if(!allEquals(dosageTimesStructure.query("//*:AccordingToNeedDosageTimeElementStructure//*:DosageQuantityFreeText")))
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
		
		
		int numberOfDosageTimeElementStructures = ((Nodes)dosageTimesStructure.query("//*:DosageDayElementStructure[0]/*:DosageTimeElementStructure")).size();
		if(numberOfDosageTimeElementStructures>1) {
			append(" ");
			append(numberOfDosageTimeElementStructures);
			append(" gange");
		}				
		
		int numberOfDosageDayElements = ((Nodes)dosageTimesStructure.query("//*:DosageDayElementStructure")).size();
		int numberOfDosageTimeElements = ((Nodes)dosageTimesStructure.query("//*:DosageTimeElementStructure")).size();
		int numberOfEntireWeeks = numberOfDosageDayElements/7;
		if(7*numberOfEntireWeeks==numberOfDosageDayElements) {
			if(numberOfEntireWeeks==1) {
				append(" daglig i ");
			 	append(numberOfEntireWeeks);
			 	append(" uge");
			}
			else {
				append(" daglig i ");
				append(numberOfEntireWeeks);
				append(" uger");
			}
		}
		else {
			if(numberOfDosageDayElements==1&&numberOfDosageTimeElements==1) {
				append(" som engangsdosis");
			}
			else if(numberOfDosageDayElements==1&&numberOfDosageTimeElements>1) {
				// "samme dag"
			}
			else { 
				append(" daglig i ");
				append(numberOfDosageDayElements);
				append(" dage");
			}
		}
		
		Integer fromDay = ((Integer)dosageTimesStructure.query("//*:DosageDayElementStructure[0]/*:DosageDayIdentifier/integer()"));			
		if(fromDay.intValue()>1) {
			append(" fra dag ");
			append(fromDay.intValue());
		}			

				
		String dosageSupplementaryText = (String)dosageTimesStructure.query("//*:DosageSupplementaryText/text()");
		if(dosageSupplementaryText==null)
			dosageSupplementaryText = (String)dosageTimesStructure.query("//*:DosageTimeElementStructure[0]//*:DosageQuantityFreeText[0]/text()");
		if(dosageSupplementaryText!=null) { 
			append(" ");
			append(dosageSupplementaryText);	
		}
		
		Nodes accordingToNeed = (Nodes)dosageTimesStructure.query("//*:AccordingToNeedDosageTimeElementStructure");
		int numberOfaccordingToNeed = accordingToNeed==null ? 0 : accordingToNeed.size();
		if(accordingToNeed!=null) {
			append(" samt");
			if(accordingToNeed.size()==1) {
				append(" efter behov");
			}
			else {
				append(" højst ");
				append(accordingToNeed.size());
				append(" gange efter behov");
			}
			if(dosageSupplementaryText!=null) {
				append(" ");
				append(dosageSupplementaryText);
			}
		}
		
	}
	
}
