package dk.medicinkortet.dosisstructuretext.shorttext.converterimpl;

import dk.medicinkortet.dosisstructuretext.TextHelper;
import dk.medicinkortet.dosisstructuretext.simpelxml.Node;
import dk.medicinkortet.dosisstructuretext.simpelxml.parser.XPathException;

/**
 * Conversion of: Dosage limited to N days, the same every day, and according to need. 
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
		return true;
	}

		
	protected void doConvert(Node dosageTimesStructure) throws XPathException {
		String unit = (String)dosageTimesStructure.query("//*:DosageQuantityUnitText/text()");			
		
		Double morning = nonnegative((Double)dosageTimesStructure.query("//*:MorningDosageTimeElementStructure/*:DosageQuantityStructure/*:DosageQuantityValue/double()"));
		Double morningMin = nonnegative((Double)dosageTimesStructure.query("//*:MorningDosageTimeElementStructure/*:MinimalDosageQuantityStructure/*:DosageQuantityValue/double()"));
		Double morningMax = nonnegative((Double)dosageTimesStructure.query("//*:MorningDosageTimeElementStructure/*:MaximalDosageQuantityStructure/*:DosageQuantityValue/double()"));
		boolean hasMorning = morning!=null || (morningMin!=null && morningMax!=null);
		String morningFreeText = join(				
				(String)dosageTimesStructure.query("//*:MorningDosageTimeElementStructure//*:DosageQuantityFreeText[0]/text()"), 
				(String)dosageTimesStructure.query("//*:MorningDosageTimeElementStructure//*:DosageQuantityFreeText[1]/text()"));

		Double noon = nonnegative((Double)dosageTimesStructure.query("//*:NoonDosageTimeElementStructure/*:DosageQuantityStructure/*:DosageQuantityValue/double()"));			
		Double noonMin = nonnegative((Double)dosageTimesStructure.query("//*:NoonDosageTimeElementStructure/*:MinimalDosageQuantityStructure/*:DosageQuantityValue/double()"));
		Double noonMax = nonnegative((Double)dosageTimesStructure.query("//*:NoonDosageTimeElementStructure/*:MaximalDosageQuantityStructure/*:DosageQuantityValue/double()"));
		boolean hasNoon = noon!=null || (noonMin!=null && noonMax!=null);
		if(noon!=null&&noon.doubleValue()<=0)
			noon = null;
		String noonFreeText = join( 
			(String)dosageTimesStructure.query("//*:NoonDosageTimeElementStructure//*:DosageQuantityFreeText[0]/text()"),
			(String)dosageTimesStructure.query("//*:NoonDosageTimeElementStructure//*:DosageQuantityFreeText[1]/text()"));
		
		Double evening = nonnegative((Double)dosageTimesStructure.query("//*:EveningDosageTimeElementStructure/*:DosageQuantityStructure/*:DosageQuantityValue/double()"));			
		Double eveningMin = nonnegative((Double)dosageTimesStructure.query("//*:EveningDosageTimeElementStructure/*:MinimalDosageQuantityStructure/*:DosageQuantityValue/double()"));
		Double eveningMax = nonnegative((Double)dosageTimesStructure.query("//*:EveningDosageTimeElementStructure/*:MaximalDosageQuantityStructure/*:DosageQuantityValue/double()"));
		boolean hasEvening = evening!=null || (eveningMin!=null && eveningMax!=null);
		if(evening!=null&&evening.doubleValue()<=0)
			evening = null;
		String eveningFreeText = join( 
			(String)dosageTimesStructure.query("//*:EveningDosageTimeElementStructure//*:DosageQuantityFreeText[0]/text()"),
			(String)dosageTimesStructure.query("//*:EveningDosageTimeElementStructure//*:DosageQuantityFreeText[1]/text()"));

		Double night = nonnegative((Double)dosageTimesStructure.query("//*:NightDosageTimeElementStructure/*:DosageQuantityStructure/*:DosageQuantityValue/double()"));			
		Double nightMin = nonnegative((Double)dosageTimesStructure.query("//*:NightDosageTimeElementStructure/*:MinimalDosageQuantityStructure/*:DosageQuantityValue/double()"));
		Double nightMax = nonnegative((Double)dosageTimesStructure.query("//*:NightDosageTimeElementStructure/*:MaximalDosageQuantityStructure/*:DosageQuantityValue/double()"));
		boolean hasNight = night!=null || (nightMin!=null && nightMax!=null);
		if(night!=null&&night.doubleValue()<=0)
			night = null;		
		String nightFreeText = join( 
			(String)dosageTimesStructure.query("//*:NightDosageTimeElementStructure//*:DosageQuantityFreeText[0]/text()"),
			(String)dosageTimesStructure.query("//*:NightDosageTimeElementStructure//*:DosageQuantityFreeText[1]/text()"));
		
		boolean allDosagesEqual = 
			allEquals(dosageTimesStructure.query("//*:DosageQuantityStructure")) &&
			allEquals(dosageTimesStructure.query("//*:MinimalDosageQuantityStructure")) &&
			allEquals(dosageTimesStructure.query("//*:MaximalDosageQuantityStructure")) &&
			(dosageTimesStructure.queryForSize("//*:DosageQuantityStructure")==0 ||
			   dosageTimesStructure.queryForSize("//*:MinimalDosageQuantityStructure")==0 &&
			   dosageTimesStructure.queryForSize("//*:MaximalDosageQuantityStructure")==0)	;
		
		boolean allTextsEqual = allEquals(
				dosageTimesStructure.query("//*:MorningDosageTimeElementStructure//*:DosageQuantityFreeText"), 
			    dosageTimesStructure.query("//*:NoonDosageTimeElementStructure//*:DosageQuantityFreeText"),
			    dosageTimesStructure.query("//*:EveningDosageTimeElementStructure//*:DosageQuantityFreeText"),
			    dosageTimesStructure.query("//*:NightDosageTimeElementStructure//*:DosageQuantityFreeText"));
		
		
		if(hasMorning) {
			append(value(morning, morningMin, morningMax));
			append(" ");
			if(morning!=null&&morning.doubleValue()==1.0)
				append(TextHelper.unitToSingular(unit));
			else 
				append(TextHelper.unitToPlural(unit));
			append(" morgen");
			if(!allTextsEqual&&(morningFreeText!=null)) {
				append(" ");
				append(morningFreeText);
			}			
		}
		
		if(hasNoon) { 
			if(hasMorning&&hasEvening&&hasNight)
				append(",");
			else if(hasMorning&&(hasEvening||hasNight))
				append(" og");
			if(!(allDosagesEqual&&hasMorning)) {
				if(hasMorning)
					append(" ");
				append(value(noon, noonMin, noonMax));
				append(" ");
				if(noon!=null&&noon.doubleValue()==1.0)
					append(TextHelper.unitToSingular(unit));
				else 
					append(TextHelper.unitToPlural(unit));
			}
			append(" middag");
			if(!allTextsEqual&&noonFreeText!=null) {
				append(" ");
				append(noonFreeText);
			}			
		}

		if(hasEvening) {
			if((hasMorning||hasNoon)&&!hasNight)
				append(" og");
			else if((hasMorning||hasNoon)&&hasNight)
				append(",");
			if(!(allDosagesEqual&&(hasMorning||hasNoon))) {
				 if(hasMorning||hasNoon)
					append(" ");
				append(value(evening, eveningMin, eveningMax));
				append(" ");
				if(evening!=null&&evening.doubleValue()==1.0)
					append(TextHelper.unitToSingular(unit));
				else 
					append(TextHelper.unitToPlural(unit));
			}
			append(" aften");
			if(!allTextsEqual&&eveningFreeText!=null) {
				append(" ");
				append(eveningFreeText);
			}						
		}

		if(hasNight) {
			if(hasMorning||hasNoon||hasEvening)
				append(" og");
			if(!(allDosagesEqual&&(hasMorning||hasNoon||hasEvening))) {
				if(hasMorning||hasNoon||hasEvening)
					append(" ");
				append(value(night, nightMin, nightMax));
				append(" ");
				if(night!=null&&night.doubleValue()==1.0)
					append(TextHelper.unitToSingular(unit));
				else 
					append(TextHelper.unitToPlural(unit));
			}
			append(" nat");
			if(!allTextsEqual&&nightFreeText!=null) {
				append(" ");
				append(nightFreeText);
			}						
		}
		
		if(allTextsEqual&&(morningFreeText!=null||noonFreeText!=null||eveningFreeText!=null||nightFreeText!=null)) {
			append(" ");
			if(morningFreeText!=null)
				append(morningFreeText);
			else if(noonFreeText!=null)
				append(noonFreeText);
			else if(eveningFreeText!=null)
				append(eveningFreeText);
			else if(nightFreeText!=null)
				append(nightFreeText);
		}	
		
		Double accordingToNeed = (Double)dosageTimesStructure.query("//*:AccordingToNeedDosageTimeElementStructure/*:DosageQuantityStructure/*:DosageQuantityValue/double()");			
		if(accordingToNeed!=null&&accordingToNeed.intValue()<=0)
			accordingToNeed = null;
		
		if(accordingToNeed!=null) {
			append(" og efter behov");
		}			
		
	}
	
	private String value(Double value, Double valueMin, Double valueMax) {
		if(value!=null) {
			return TextHelper.decimalToFraction(value);
		}
		else if(valueMin!=null&&valueMax!=null) {
			return TextHelper.decimalToFraction(valueMin) + "-" + TextHelper.decimalToFraction(valueMax);	
		}
		else {
			return null;
		}		
	}
	
	private Double nonnegative(Double d) {
		if(d!=null&&d.doubleValue()<=0)
			return null;
		else
			return d;
	}
	
	private String join(String s0, String s1) {
		if(s0==null&&s1==null)
			return null;
		if(s0!=null&&s1==null)
			return s0;
		if(s0==null&&s1!=null)
			return s1;
		if(s0.equals(s1))
			return s0;
		return s0 + " / " + s1;		
	}
	
}
