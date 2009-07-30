package dk.medicinkortet.dosisstructuretext.shorttext.converterimpl;

import dk.medicinkortet.dosisstructuretext.TextHelper;
import dk.medicinkortet.dosisstructuretext.simpelxml.Node;
import dk.medicinkortet.dosisstructuretext.simpelxml.parser.XPathException;

//// Currently not used!!!

/**
 * Conversion of: Dosage limited to N days, the same every day, and according to need
 * <p>
 * Example:<br> 
 * 81: 1 tablet om morgenen<br>
 * 105: 1 tablet om aftenen
 */
public class SimpleMorningNoonEveningNightConverterImpl extends ConverterImpl {
	
	protected boolean doTest(Node dosageTimesStructure) throws XPathException {
		Integer interval = dosageTimesStructure.queryForInteger("//*:DosageTimesIterationIntervalQuantity/integer()");
		if(interval != null && interval != 1)
			return false;
		if(dosageTimesStructure.queryForSize("//*:DosageDayIdentifier") > 2)
			return false;
		Integer dayId0 = dosageTimesStructure.queryForInteger("//*:DosageDayIdentifier[0]/integer()");
		if(dayId0 != null && dayId0 > 1)
			return false;
		Integer dayId1 = dosageTimesStructure.queryForInteger("//*:DosageDayIdentifier[1]/integer()");
		if(dosageTimesStructure.queryForSize("//*:DosageDayIdentifier") > 1 && dayId1 != null && dayId1 > 1)
			return false;
		if(dosageTimesStructure.queryForSize("//*:MorningDosageTimeElementStructure") == 0 &&
		   dosageTimesStructure.queryForSize("//*:NoonDosageTimeElementStructure") == 0 &&
		   dosageTimesStructure.queryForSize("//*:EveningDosageTimeElementStructure") == 0 &&
		   dosageTimesStructure.queryForSize("//*:NightDosageTimeElementStructure") == 0)		   
			return false;
		if(dosageTimesStructure.queryForSize("//*:AccordingToNeedDosageTimeElementStructure") > 1)
			return false;
		if(dosageTimesStructure.queryForSize("//*:DosageQuantityValue") == 0)
			return false;		
		if(!allEquals(dosageTimesStructure.query("//*:DosageQuantityStructure//*:DosageQuantityValue"))) // all equals, see the non-simple version otherwise
			return false;
		if(!allEquals(dosageTimesStructure.query("//*:MinimalDosageQuantityStructure//*:DosageQuantityValue"))) // all equals, see the non-simple version otherwise
			return false;
		if(!allEquals(dosageTimesStructure.query("//*:MaximalDosageQuantityStructure//*:DosageQuantityValue"))) // all equals, see the non-simple version otherwise
			return false;
		if(!allEquals(dosageTimesStructure.query("//*:MorningDosageTimeElementStructure//*:DosageQuantityFreeText"), 
				      dosageTimesStructure.query("//*:NoonDosageTimeElementStructure//*:DosageQuantityFreeText"),
				      dosageTimesStructure.query("//*:EveningDosageTimeElementStructure//*:DosageQuantityFreeText"),
				      dosageTimesStructure.query("//*:NightDosageTimeElementStructure//*:DosageQuantityFreeText")))
			return false;
		if(!allEquals(dosageTimesStructure.query("//*:DosageTimeElementStructure//*:DosageQuantityFreeText")))
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
		
		Object any = dosageTimesStructure.query("//*:DosageTimeElementStructure/*:DosageQuantityStructure/*:DosageQuantityValue/double()");
		String dosageQuantityFreeText = (String)dosageTimesStructure.query("//*:DosageTimeElementStructure[0]//*:DosageQuantityFreeText/text()");
		if(any!=null && any instanceof Double && ((Double)any).doubleValue()<=0)
			any = null;
		
		if(any!=null) {
			if(any instanceof Double) {
				append(" 1 gang");
			}
			else if(any instanceof Double[]) {
				append(" ");
				append(((Double[])any).length);
				append(" gange");					
			}
			if(dosageQuantityFreeText!=null) {
				append(" ");
				append(dosageQuantityFreeText);
			}
			append(" samt");					
		}				
		
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
			append(" morgen");
		}
		
		if(noon!=null) { 
			if(morning!=null)
				append(" og");
			append(" middag");
		}

		if(evening!=null) {
			if(morning!=null||noon!=null)
				append(" og");
			append(" aften");
		}

		if(night!=null) {
			if(morning!=null||noon!=null||evening!=null)
				append(" og");
			append(" nat");
		}
		
		dosageQuantityFreeText = (String)dosageTimesStructure.query("//*:MorningDosageTimeElementStructure[0]//*:DosageQuantityFreeText/text()");
		if(dosageQuantityFreeText==null)
			dosageQuantityFreeText = (String)dosageTimesStructure.query("//*:NoonDosageTimeElementStructure[0]//*:DosageQuantityFreeText/text()");
		if(dosageQuantityFreeText==null)
			dosageQuantityFreeText = (String)dosageTimesStructure.query("//*:EveningDosageTimeElementStructure[0]//*:DosageQuantityFreeText/text()");
		if(dosageQuantityFreeText==null)
			dosageQuantityFreeText = (String)dosageTimesStructure.query("//*:NightDosageTimeElementStructure[0]//*:DosageQuantityFreeText/text()");
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
