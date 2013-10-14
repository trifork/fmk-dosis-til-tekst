package dk.medicinkortet.dosagetranslation;

import java.util.ArrayList;


public class RawDefinition {
	
	// Read from the source (spreadsheet)
	private int rowNumber; 
	private String unitSingular;
	private String unitPlural;
	private String type;
	private String iterationInterval; 
	private String mapping;
	private String supplementaryText;
	
	// Intermediate values
	private ArrayList<Integer> iterationIntervals;
	private ArrayList<String> types;
	private ArrayList<String> mappings;
	
	// Written to the source (spreadsheet)
	private String error;
	private String shortText; 
	private String longText;
	
	public RawDefinition(int rowNumber, String unitSingular, String unitPlural, String type, String iterationInterval, String mapping, String supplementaryText) {
		this.rowNumber = rowNumber;
		this.unitSingular = unitSingular;
		this.unitPlural = unitPlural;
		this.type = type;
		this.iterationInterval = iterationInterval;
		this.mapping = mapping;
		this.supplementaryText = supplementaryText;
	}
	
	public int getRowNumber() {
		return rowNumber;
	}
	
	public String getUnitSingular() {
		return unitSingular;
	}

	public String getUnitPlural() {
		return unitPlural;
	}

	public String getType() {
		return type;
	}

	public String getIterationInterval() {
		return iterationInterval;
	}

	public String getMapping() {
		return mapping;
	}

	public String getSupplementaryText() {
		return supplementaryText;
	}

	public void addError(String errorMessage) {
		if(this.error==null)
			error = errorMessage;
		else 
			error += "; "+errorMessage;
	}

	public String getError() {
		return error;
	}

	public void setDosageTranslation(String shortText, String longText) {
		this.shortText = shortText;
		this.longText = longText;
	}

	public String getShortText() {
		return shortText;
	}

	public String getLongText() {
		return longText;
	}

	public ArrayList<Integer> getIterationIntervals() {
		return iterationIntervals;
	}

	public void setIterationIntervals(ArrayList<Integer> iterationIntervals) {
		this.iterationIntervals = iterationIntervals;
	}

	public ArrayList<String> getTypes() {
		return types;
	}

	public void setTypes(ArrayList<String> types) {
		this.types = types;
	}

	public ArrayList<String> getMappings() {
		return mappings;
	}

	public void setMappings(ArrayList<String> mappings) {
		this.mappings = mappings;
	}

}
