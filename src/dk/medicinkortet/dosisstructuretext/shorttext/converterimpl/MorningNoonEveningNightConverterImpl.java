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
		if(dosageTimesStructure.queryForSize("//*:DosageQuantityValue") + 
		   dosageTimesStructure.queryForSize("//*:MinimalDosageQuantityValue") +	
		   dosageTimesStructure.queryForSize("//*:MaximalDosageQuantityValue") == 0)
			return false;
		return true;
	}

		
	protected void doConvert(Node dosageTimesStructure) throws XPathException {
		String unit = (String)dosageTimesStructure.query("//*:DosageQuantityUnitText/text()");			
		String dosageSupplementaryText = (String)dosageTimesStructure.query("//*:DosageSupplementaryText/text()");
		
		Double morning = queryDoubles(dosageTimesStructure,
			"//*:MorningDosageTimeElementStructure/*:DosageQuantityStructure/*:DosageQuantityValue/double()", 
			"//*:MorningDosageTimeElementStructure/*:DosageQuantityValue/double()");
		Double morningMin = queryDoubles(dosageTimesStructure, 
			"//*:MorningDosageTimeElementStructure/*:MinimalDosageQuantityStructure/*:DosageQuantityValue/double()", 
			"//*:MorningDosageTimeElementStructure/*:MinimalDosageQuantityValue/double()");
		Double morningMax = queryDoubles(dosageTimesStructure, 
			"//*:MorningDosageTimeElementStructure/*:MaximalDosageQuantityStructure/*:DosageQuantityValue/double()",
			"//*:MorningDosageTimeElementStructure/*:MaximalDosageQuantityValue/double()");
		boolean hasMorning = morning!=null || (morningMin!=null && morningMax!=null);
		String morningFreeText = null;				
		if(dosageSupplementaryText==null) {
			morningFreeText = join(				
					(String)dosageTimesStructure.query("//*:MorningDosageTimeElementStructure//*:DosageQuantityFreeText[0]/text()"), 
					(String)dosageTimesStructure.query("//*:MorningDosageTimeElementStructure//*:DosageQuantityFreeText[1]/text()"));
		}

		Double noon = queryDoubles(dosageTimesStructure, 
			"//*:NoonDosageTimeElementStructure/*:DosageQuantityStructure/*:DosageQuantityValue/double()",
			"//*:NoonDosageTimeElementStructure/*:DosageQuantitValue/double()");			
		Double noonMin = queryDoubles(dosageTimesStructure, 
			"//*:NoonDosageTimeElementStructure/*:MinimalDosageQuantityStructure/*:DosageQuantityValue/double()",
			"//*:NoonDosageTimeElementStructure/*:MinimalDosageQuantityValue/double()");
		Double noonMax = queryDoubles(dosageTimesStructure, 
			"//*:NoonDosageTimeElementStructure/*:MaximalDosageQuantityStructure/*:DosageQuantityValue/double()",
			"//*:NoonDosageTimeElementStructure/*:MaximalDosageQuantityValue/double()");
		boolean hasNoon = noon!=null || (noonMin!=null && noonMax!=null);
		String noonFreeText = null;
		if(dosageSupplementaryText==null) {		
			noonFreeText = join( 
				(String)dosageTimesStructure.query("//*:NoonDosageTimeElementStructure//*:DosageQuantityFreeText[0]/text()"),
				(String)dosageTimesStructure.query("//*:NoonDosageTimeElementStructure//*:DosageQuantityFreeText[1]/text()"));
		}
		
		Double evening = queryDoubles(dosageTimesStructure, 
			"//*:EveningDosageTimeElementStructure/*:DosageQuantityStructure/*:DosageQuantityValue/double()",
			"//*:EveningDosageTimeElementStructure/*:DosageQuantityValue/double()");			
		Double eveningMin = queryDoubles(dosageTimesStructure, 
			"//*:EveningDosageTimeElementStructure/*:MinimalDosageQuantityStructure/*:DosageQuantityValue/double()",
			"//*:EveningDosageTimeElementStructure/*:MinimalDosageQuantityValue/double()");
		Double eveningMax = queryDoubles(dosageTimesStructure, 
			"//*:EveningDosageTimeElementStructure/*:MaximalDosageQuantityStructure/*:DosageQuantityValue/double()",
			"//*:EveningDosageTimeElementStructure/*:MaximalDosageQuantityValue/double()");
		boolean hasEvening = evening!=null || (eveningMin!=null && eveningMax!=null);
		String eveningFreeText = null; 
		if(dosageSupplementaryText==null) {		
			eveningFreeText = join( 
				(String)dosageTimesStructure.query("//*:EveningDosageTimeElementStructure//*:DosageQuantityFreeText[0]/text()"),
				(String)dosageTimesStructure.query("//*:EveningDosageTimeElementStructure//*:DosageQuantityFreeText[1]/text()"));
		}

		Double night = queryDoubles(dosageTimesStructure,
			"//*:NightDosageTimeElementStructure/*:DosageQuantityStructure/*:DosageQuantityValue/double()", 
			"//*:NightDosageTimeElementStructure/*:DosageQuantityValue/double()");	
		Double nightMin = queryDoubles(dosageTimesStructure,
			"//*:NightDosageTimeElementStructure/*:MinimalDosageQuantityStructure/*:DosageQuantityValue/double()",
			"//*:NightDosageTimeElementStructure/*:MinimalDosageQuantityValue/double()");				
		Double nightMax = queryDoubles(dosageTimesStructure, 
			"//*:NightDosageTimeElementStructure/*:MaximalDosageQuantityStructure/*:DosageQuantityValue/double()", 
			"//*:NightDosageTimeElementStructure/*:MaximalDosageQuantityValue/double()");							
		boolean hasNight = night!=null || (nightMin!=null && nightMax!=null);
		String nightFreeText = null; 
		if(dosageSupplementaryText==null) {		
			nightFreeText = join( 
				(String)dosageTimesStructure.query("//*:NightDosageTimeElementStructure//*:DosageQuantityFreeText[0]/text()"),
				(String)dosageTimesStructure.query("//*:NightDosageTimeElementStructure//*:DosageQuantityFreeText[1]/text()"));
		}
		
		boolean allDosagesEqual = 
			allEquals(dosageTimesStructure.query("//*:DosageQuantityStructure")) &&
			allEquals(dosageTimesStructure.query("//*:MinimalDosageQuantityStructure")) &&
			allEquals(dosageTimesStructure.query("//*:MaximalDosageQuantityStructure")) &&
			(dosageTimesStructure.queryForSize("//*:DosageQuantityStructure")==0 ||
			   dosageTimesStructure.queryForSize("//*:MinimalDosageQuantityStructure")==0 &&
			   dosageTimesStructure.queryForSize("//*:MaximalDosageQuantityStructure")==0)	;
		
		boolean allTextsEqual = dosageSupplementaryText!=null || allEquals(
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
		
		if(dosageSupplementaryText!=null) {
			append(" ");
			append(dosageSupplementaryText);			
		}
		else if(allTextsEqual&&(morningFreeText!=null||noonFreeText!=null||eveningFreeText!=null||nightFreeText!=null)) {
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
	
	private Double queryDoubles(Node node, String xpathExpression0, String xpathExpression1) throws XPathException {
		Double d = (Double)node.query(xpathExpression0);
		if(d!=null)
			return nonnegative(d);
		d = (Double)node.query(xpathExpression1);
		if(d!=null)
			return nonnegative(d);
		return null;
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
