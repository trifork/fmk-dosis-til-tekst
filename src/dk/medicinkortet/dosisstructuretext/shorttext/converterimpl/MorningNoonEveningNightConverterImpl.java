package dk.medicinkortet.dosisstructuretext.shorttext.converterimpl;

import dk.medicinkortet.dosisstructuretext.TextHelper;
import dk.medicinkortet.dosisstructuretext.simpelxml.Node;
import dk.medicinkortet.dosisstructuretext.simpelxml.parser.XPathException;

/**
 * Conversion of: Dosage limited to N days, the same every day, and according to need. But not all dosages the same.
 * <p>
 * Examples<br>
 * 130: 2 tabletter om morgenen og 1 tablet om aftenen<br>
 * 915: 2 tabletter om morgenen, 1 tablet til middag og 1 tablet om aftenen
 */
public class MorningNoonEveningNightConverterImpl extends ConverterImpl {
	
	protected boolean doTest(Node dosageTimesStructure) throws XPathException {
		if(dosageTimesStructure.queryForInt("//*:DosageTimesIterationIntervalQuantity/integer()") != 1)
			return false;
		if(dosageTimesStructure.queryForSize("//*:DosageDayIdentifier") > 1)
			return false;		
		if(dosageTimesStructure.queryForInt("//*:DosageDayIdentifier[0]/integer()") != 1)
			return false;
		if(dosageTimesStructure.queryForSize("//*:AccordingToNeedDosageTimeElementStructure") > 0)
			return false;
		if(dosageTimesStructure.queryForSize("//*:DosageTimeElementStructure") > 0)
			return false;
		if(dosageTimesStructure.queryForSize("//*:MorningDosageTimeElementStructure") == 0 &&
		   dosageTimesStructure.queryForSize("//*:NoonDosageTimeElementStructure") == 0 &&
		   dosageTimesStructure.queryForSize("//*:EveningDosageTimeElementStructure") == 0 &&
		   dosageTimesStructure.queryForSize("//*:NightDosageTimeElementStructure") == 0)		   
			return false;
		if(dosageTimesStructure.queryForSize("//*:DosageQuantityValue") == 0)
			return false;
		if(allEquals(dosageTimesStructure.query("//*:DosageQuantityValue"))) // not all equals, see the simple version otherwise
			return false;

		return true;
	}

		
	protected void doConvert(Node dosageTimesStructure) throws XPathException {
		String unit = (String)dosageTimesStructure.query("//*:DosageQuantityUnitText/text()");			
		
		Double morning = (Double)dosageTimesStructure.query("//*:MorningDosageTimeElementStructure/*:DosageQuantityStructure/*:DosageQuantityValue/double()");
		if(morning!=null&&morning.doubleValue()<=0)
			morning = null;

		Double noon = (Double)dosageTimesStructure.query("//*:NoonDosageTimeElementStructure/*:DosageQuantityStructure/*:DosageQuantityValue/double()");			
		if(noon!=null&&noon.doubleValue()<=0)
			noon = null;
		
		Double evening = (Double)dosageTimesStructure.query("//*:EveningDosageTimeElementStructure/*:DosageQuantityStructure/*:DosageQuantityValue/double()");			
		if(evening!=null&&evening.doubleValue()<=0)
			evening = null;

		Double night = (Double)dosageTimesStructure.query("//*:NightDosageTimeElementStructure/*:DosageQuantityStructure/*:DosageQuantityValue/double()");			
		if(night!=null&&night.doubleValue()<=0)
			night = null;
		
		
		if(morning!=null)  {
			append(TextHelper.decimalToFraction(morning));
			append(" ");
			if(morning.doubleValue() == 1.0)
				append(TextHelper.unitToSingular(unit));
			else 
				append(TextHelper.unitToPlural(unit));			
			append(" morgen");
		}
		
		if(noon!=null) { 
			if(morning!=null&&(evening==null&&night==null))
				append(" og ");
			else if(morning!=null&&(evening!=null||night!=null))
				append(", ");
			append(TextHelper.decimalToFraction(noon));
			append(" ");
			if(noon.doubleValue() == 1.0)
				append(TextHelper.unitToSingular(unit));
			else 
				append(TextHelper.unitToPlural(unit));			
			append(" middag");
		}

		if(evening!=null) {
			if((morning!=null||noon!=null)&&night==null)
				append(" og ");
			else if((morning!=null||noon!=null)&&night!=null)
				append(", ");
			append(TextHelper.decimalToFraction(evening));
			append(" ");
			if(evening.doubleValue() == 1.0)
				append(TextHelper.unitToSingular(unit));
			else 
				append(TextHelper.unitToPlural(unit));			
			append(" aften");
		}

		if(night!=null) {
			if(morning!=null||noon!=null||night!=null)
				append(" og ");
			append(TextHelper.decimalToFraction(night));
			append(" ");
			if(night.doubleValue() == 1.0)
				append(TextHelper.unitToSingular(unit));
			else 
				append(TextHelper.unitToPlural(unit));			
			append(" nat");
		}

		String dosageQuantityFreeText = (String)dosageTimesStructure.query("///*:DosageQuantityFreeText[0]/text()");
		if(dosageQuantityFreeText!=null) {
			append(" ");
			append(dosageQuantityFreeText);
		}	
		
		Double accordingToNeed = (Double)dosageTimesStructure.query("//*:AccordingToNeedDosageTimeElementStructure/*:DosageQuantityStructure/*:DosageQuantityValue/double()");			
		if(accordingToNeed!=null&&accordingToNeed.intValue()<=0)
			accordingToNeed = null;
		
		if(accordingToNeed!=null) {
			append(" og efter behov");
		}			
		
	}
	
}
