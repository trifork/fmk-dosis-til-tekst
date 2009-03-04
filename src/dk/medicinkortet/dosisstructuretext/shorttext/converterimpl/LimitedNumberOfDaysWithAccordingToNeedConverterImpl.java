package dk.medicinkortet.dosisstructuretext.shorttext.converterimpl;

import dk.medicinkortet.dosisstructuretext.TextHelper;
import dk.medicinkortet.dosisstructuretext.simpelxml.Node;
import dk.medicinkortet.dosisstructuretext.simpelxml.Nodes;
import dk.medicinkortet.dosisstructuretext.simpelxml.parser.XPathException;

/**
 * Conversion of: Dosage limited to N days, the same every day, and according to need
 * <p>
 * Example:<br>
 * 42: 20 ml f&oslash;r sengetid og ved behov
 */
public class LimitedNumberOfDaysWithAccordingToNeedConverterImpl extends ConverterImpl {
	
	protected boolean doTest(Node dosageTimesStructure) throws XPathException {
		if(dosageTimesStructure.queryForInt("//*:DosageTimesIterationIntervalQuantity/integer()") != 1)
			return false;
		if(dosageTimesStructure.queryForSize("//*:DosageDayIdentifier") != 2)
			return false;		
		if(dosageTimesStructure.queryForInt("//*:DosageDayIdentifier[0]/integer()") > 1)
			return false;
		if(dosageTimesStructure.queryForInt("//*:DosageDayIdentifier[1]/integer()") > 1)
			return false;
		if(dosageTimesStructure.queryForSize("//*:AccordingToNeedDosageTimeElementStructure") != 1)
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
		if(dosageTimesStructure.queryForSize("//*:DosageQuantityValue") == 0)
			return false;		
		if(!allEquals(dosageTimesStructure.query("//*:DosageQuantityValue")))
			return false;
		if(!allEquals(dosageTimesStructure.query("//*:DosageQuantityFreeText")))
			return false;

		return true;
	}
	
	protected void doConvert(Node dosageTimesStructure) throws XPathException {
		append(TextHelper.decimalToFraction((String)dosageTimesStructure.query("//*:DosageQuantityValue[0]/text()"))); 
		append(" ");
		if( ((Double)dosageTimesStructure.query("//*:DosageQuantityValue[0]/double()")).doubleValue() == 1.0 )
			append(TextHelper.unitToSingular((String)dosageTimesStructure.query("//*:DosageQuantityUnitText/text()")));
		else 
			append(TextHelper.unitToPlural((String)dosageTimesStructure.query("//*:DosageQuantityUnitText/text()")));
		int numberOfDosageTimeElementStructures = ((Nodes)dosageTimesStructure.query("//*:DosageTimeElementStructure")).size();
		if(numberOfDosageTimeElementStructures>1) {
			append(" ");
			append(numberOfDosageTimeElementStructures);
			append(" gange");
		}				
		append(" daglig");			
		String dosageQuantityFreeText = (String)dosageTimesStructure.query("///*:DosageQuantityFreeText[0]/text()");
		if(dosageQuantityFreeText!=null) {
			append(" ");
			append(dosageQuantityFreeText);
		}			
		append(" og ved behov");		
	}
	
}
