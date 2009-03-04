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
		if(dosageTimesStructure.queryForInt("//*:DosageTimesIterationIntervalQuantity/integer()") != 1)
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
		if(dosageTimesStructure.queryForSize("//*:MinimalDosageQuantityStructure") == 0)
			return false;
		if(dosageTimesStructure.queryForSize("//*:MaximalDosageQuantityStructure") == 0)
			return false;
		if(!allEquals(dosageTimesStructure.query("//*:MinimalDosageQuantityStructure")))
			return false;
		if(!allEquals(dosageTimesStructure.query("//*:MaximalDosageQuantityStructure")))
			return false;
		return true;
	}
	
	protected void doConvert(Node dosageTimesStructure) throws XPathException {
		// dosage
		
		append(TextHelper.decimalToFraction((String)dosageTimesStructure.query("//*:MinimalDosageQuantityStructure[0]/*:DosageQuantityValue/text()")));
		append("-");
		append(TextHelper.decimalToFraction((String)dosageTimesStructure.query("//*:MaximalDosageQuantityStructure[0]/*:DosageQuantityValue/text()")));
		append(" ");
		
		// dosage unit
		if( ((Double)dosageTimesStructure.query("//*:DosageQuantityValue[0]/double()")).doubleValue() == 1.0 )
			append(TextHelper.unitToSingular((String)dosageTimesStructure.query("//*:DosageQuantityUnitText/text()")));
		else 
			append(TextHelper.unitToPlural((String)dosageTimesStructure.query("//*:DosageQuantityUnitText/text()")));
		append(" ");
		
		// number of times daily
		int fixed = dosageTimesStructure.queryForSize("//*:DosageTimeElementStructure");
		int pn = dosageTimesStructure.queryForSize("//*:AccordingToNeedDosageTimeElementStructure");
		
		append(fixed);
		append("-");
		append(pn+fixed);
		
		// String "gange daglig"
		append(" gange daglig");	
		
		// Optional free text
		String dosageQuantityFreeText = (String)dosageTimesStructure.query("//*:DosageQuantityFreeText[0]/text()");
		if(dosageQuantityFreeText!=null) {
			append(" ");
			append(dosageQuantityFreeText);
		}
				
	}
	
}
