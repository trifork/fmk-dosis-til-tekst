package dk.medicinkortet.dosagetranslation;

public class SourceFactory {
	
	private int unitSingularColumnNumber = -1;
	private int unitPluralColumnNumber = -1;
	private int iterationIntervalColumnNumber = -1;
	private int typeColumnNumber = -1;
	private int mappingColumnNumber = -1;
	private int supplTextColumnNumber = -1;
	private int dosageTranslationColumnNumber = -1;
	private int errorColumnNumber = -1;
	
	private String unitSingular;
	private String unitPlural;
	private String iterationInterval;
	private String type;
	private String mapping;
	private String supplText;
	
	public void addColumn(int columnNumber, String columnName) {
		String n = columnName.toLowerCase();
		if(n.equals("enhed_e"))
			unitSingularColumnNumber = columnNumber;
		else if(n.equals("enhed_f"))
			unitPluralColumnNumber = columnNumber;
		else if(n.startsWith("iterationsinterval"))
			iterationIntervalColumnNumber = columnNumber;
		else if(n.equals("type"))
			typeColumnNumber = columnNumber;
		else if(n.equals("mapning"))
			mappingColumnNumber = columnNumber;
		else if(n.equals("supplerende tekst"))
			supplTextColumnNumber = columnNumber;
		else if(n.equals("doseringsoversættelse"))
			dosageTranslationColumnNumber = columnNumber;
		else if(n.equals("fejl"))
			errorColumnNumber = columnNumber;
	}
	
	public int getDosageTranslationColumnNumber() {
		return dosageTranslationColumnNumber;
	}

	public int getErrorColumnNumber() {
		return errorColumnNumber;
	}

	public void validate() throws ValidationException {
		String v = "";
		if(unitSingularColumnNumber<0)
			v += "kolonnen \"enhed_e\" ikke fundet, ";
		if(unitPluralColumnNumber<0)
			v += "kolonnen \"enhed_f\" ikke fundet, ";	
		if(iterationIntervalColumnNumber<0)
			v += "kolonnen \"iterationsinterval\" ikke fundet, ";
		if(typeColumnNumber<0)
			v += "kolonnen \"type\" ikke fundet, ";
		if(mappingColumnNumber<0)
			v += "kolonnen \"mapning\" ikke fundet";
		if(supplTextColumnNumber<0)
			v += "kolonnen \"supplerende tekst\" ikke fundet";
		if(dosageTranslationColumnNumber<0)
			v += "kolonnen \"doseringsoversættelse\" ikke fundet, ";
		if(errorColumnNumber<0)
			v += "kolonnen \"fejl\" ikke fundet, ";
		if(v.length()>0) {
			v = "Fejl i kolonner "+v.substring(0, v.length()-2);
			throw new ValidationException(v);
		}
	}

	public void reset() {
		unitSingular = null;
		unitPlural = null;
		iterationInterval = null;
		type = null;
		mapping = null;
		supplText = null;
	}

	public void setValue(int columnIndex, String value) {		
		if(columnIndex==unitSingularColumnNumber)
			unitSingular = value;
		else if(columnIndex==unitPluralColumnNumber)
			unitPlural = value;
		else if(columnIndex==iterationIntervalColumnNumber)
			iterationInterval = value;
		else if(columnIndex==typeColumnNumber)
			type = value;
		else if(columnIndex==mappingColumnNumber)
			mapping = value;
		else if(columnIndex==supplTextColumnNumber)
			supplText = value;
	}

	public void setValue(int columnIndex, double value) {
		if(columnIndex==unitSingularColumnNumber)
			throw new RuntimeException("Error reading unit singular: Found numeric value \""+value+"\"");
		else if(columnIndex==unitPluralColumnNumber)
			throw new RuntimeException("Error reading unit plural: Found numeric value \""+value+"\"");
		else if(columnIndex==iterationIntervalColumnNumber)
			iterationInterval = ""+value;
		else if(columnIndex==typeColumnNumber)
			throw new RuntimeException("Error reading type: Found numeric value \""+value+"\"");
		else if(columnIndex==mappingColumnNumber)
			mapping = ""+value;
		if(columnIndex==supplTextColumnNumber)
			throw new RuntimeException("Error reading suppl. text: Found numeric value \""+value+"\"");
	}

	public Source build() {
		return new Source(unitSingular, unitPlural, iterationInterval, type, mapping, supplText);
	}
	
	
	
}
