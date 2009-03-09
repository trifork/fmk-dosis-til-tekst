package dk.medicinkortet.dosisstructuretext.shorttext.helper;

import dk.medicinkortet.dosisstructuretext.simpelxml.parser.XPathException;

public class ShortTextConverterHelper {
			
	private static MyDecimalFormatter FORMAT = new MyDecimalFormatter();
	
	public static String make(Value morning, Value noon, Value evening, Value night, String unit) throws XPathException {		
		return 
			"<DosageTimesStructure>\n"+
			"<DosageTimesIterationIntervalQuantity>1</DosageTimesIterationIntervalQuantity>\n"+
			"<DosageTimesStartDate>2008-01-01</DosageTimesStartDate>\n"+
			"<DosageTimesEndDate>2008-01-01</DosageTimesEndDate>\n"+
			replace(unit, "<DosageQuantityUnitText>%</DosageQuantityUnitText>\n") +
			"<DosageDayElementStructure>\n"+
			"<DosageDayIdentifier>1</DosageDayIdentifier>\n"+
			xmlify(morning, "<medicinecard:MorningDosageTimeElementStructure>\n%</medicinecard:MorningDosageTimeElementStructure>\n") +
			xmlify(noon, "<medicinecard:NoonDosageTimeElementStructure>\n%</medicinecard:NoonDosageTimeElementStructure>\n") +
			xmlify(evening, "<medicinecard:EveningDosageTimeElementStructure>\n%</medicinecard:EveningDosageTimeElementStructure>\n") +
			xmlify(night, "<medicinecard:NightDosageTimeElementStructure>\n%</medicinecard:NightDosageTimeElementStructure>\n") +
			"</DosageDayElementStructure>\n"+
			"</DosageTimesStructure>\n";	
	}
		
	private static final String MINIMAL = 
		"<medicinecard:MinimalDosageQuantityStructure>\n"+
			"<medicinecard:DosageQuantityValue>%</medicinecard:DosageQuantityValue>\n"+
		"</medicinecard:MinimalDosageQuantityStructure>\n";
	
	private static final String MAXIMAL = 
		"<medicinecard:MaximalDosageQuantityStructure>\n"+
			"<medicinecard:DosageQuantityValue>%</medicinecard:DosageQuantityValue>\n"+
		"</medicinecard:MaximalDosageQuantityStructure>\n";
	
	private static final String EXACT = 
		"<medicinecard:DosageQuantityStructure>\n"+
			"<medicinecard:DosageQuantityValue>%</medicinecard:DosageQuantityValue>\n"+
		"</medicinecard:DosageQuantityStructure>\n";		
	
	private static String xmlify(Value v, String tag) {
		if(v==null) {
			return "";
		}
		else if(v instanceof ExactValue) {
			return
				replace(replace(FORMAT.format(((ExactValue)v).getExactValue()), EXACT), tag);
		}
		else if(v instanceof IntervalValue) {
			return 
				replace(
					replace(FORMAT.format(((IntervalValue)v).getMinValue()), MINIMAL) +  
					replace(FORMAT.format(((IntervalValue)v).getMaxValue()), MAXIMAL), tag);
		}
		else {
			throw new RuntimeException(v.getClass().getName());
		}
	}
	
	private static String replace(String value, String tag) {
		return tag.replaceAll("%", value);
	}
	
	static class MyDecimalFormatter {
		public String format(double value) {
			String stringValue = Double.toString(value);
			while (stringValue.endsWith("0")) {
				stringValue = stringValue.substring(0, stringValue.length() -1);
			}
			return stringValue;
		}
	}
	
}
