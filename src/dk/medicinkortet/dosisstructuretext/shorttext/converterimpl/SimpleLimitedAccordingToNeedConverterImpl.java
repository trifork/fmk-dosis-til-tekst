package dk.medicinkortet.dosisstructuretext.shorttext.converterimpl;

import dk.medicinkortet.dosisstructuretext.TextHelper;
import dk.medicinkortet.dosisstructuretext.simpelxml.Node;
import dk.medicinkortet.dosisstructuretext.simpelxml.parser.XPathException;

/**
 * Conversion of: Simple but limited "according to need" dosage, with or without suppl. dosage free text
 * <p>
 * Example:<br>
 * 283: 1 pust ved anfald h&oslash;jst 3 gange daglig
 */
public class SimpleLimitedAccordingToNeedConverterImpl extends ConverterImpl {

	protected boolean doTest(Node dosageTimesStructure) throws XPathException {
		if(dosageTimesStructure.queryForInt("//*:DosageTimesIterationIntervalQuantity/integer()") != 1)
			return false;
		if(dosageTimesStructure.queryForSize("///*:DosageTimesIterationIntervalQuantity") != 1)
			return false;
		if(dosageTimesStructure.queryForSize("//*:DosageDayIdentifier[0]") != 1)
			return false;
		if(dosageTimesStructure.queryForSize("//*:AccordingToNeedDosageTimeElementStructure") == 0)
			return false;
		if(!allEquals(dosageTimesStructure.query("//*:AccordingToNeedDosageTimeElementStructure")))
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
		if(dosageTimesStructure.queryForSize("//*:DosageQuantityFreeText")>0 &&
		   dosageTimesStructure.queryForSize("//*:DosageQuantityFreeText") != dosageTimesStructure.queryForSize("//*:DosageQuantityValue"))
			return false;
		return true;
	}
	
	protected void doConvert(Node dosageTimesStructure) throws XPathException {
		// dosage
		Object dosageOrDosageinterval = dosageTimesStructure.query("//*:AccordingToNeedDosageTimeElementStructure[0]//*:DosageQuantityValue/text()");
		if(dosageOrDosageinterval instanceof String)
			append(TextHelper.decimalToFraction((String)dosageOrDosageinterval));
		else {
			append(TextHelper.decimalToFraction(((String[])dosageOrDosageinterval)[0]));
			append("-");
			append(TextHelper.decimalToFraction(((String[])dosageOrDosageinterval)[1]));
		}			
		append(" ");
		
		// dosage unit
		if( ((Double)dosageTimesStructure.query("//*:DosageQuantityValue[0]/double()")).doubleValue() == 1.0 )
			append(TextHelper.unitToSingular((String)dosageTimesStructure.query("//*:DosageQuantityUnitText/text()")));
		else 
			append(TextHelper.unitToPlural((String)dosageTimesStructure.query("//*:DosageQuantityUnitText/text()")));
		
		// String "efter behov"
		append(" efter behov");
		
		// Optional free text
		String dosageQuantityFreeText = (String)dosageTimesStructure.query("//*:DosageQuantityFreeText[0]/text()");
		if(dosageQuantityFreeText!=null) {
			append(" ");
			append(dosageQuantityFreeText);
		}
		
		// String "højst"
		append(" højst ");
		
		// number of according to need dosages
		int size = dosageTimesStructure.queryForSize("//*:AccordingToNeedDosageTimeElementStructure");
		append(size); 
		
		// String "gange daglig"
		if(size>1)
			append(" gange daglig");
		else
			append(" gang daglig");
		
	}
	
}
