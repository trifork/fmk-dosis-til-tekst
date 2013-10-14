package dk.medicinkortet.dosagetranslation.spreadsheet;


public class Source {
	
	private String unitSingular;
	private String unitPlural;
	private String iterationInterval;
	private String type;
	private String mapping;
	private String supplText;
	
	private String validationError;
	
	public Source(String unitSingular, String unitPlural, String iterationInterval, String type, String mapping, String supplText) {
		this.unitSingular = unitSingular;
		this.unitPlural = unitPlural;
		this.iterationInterval = iterationInterval;
		this.type = type;
		this.mapping = mapping;
		this.supplText = supplText;
	}

	public String getUnitSingular() {
		return unitSingular;
	}
	public String getUnitPlural() {
		return unitPlural;
	}

	public String getIterationInterval() {
		return iterationInterval;
	}

	public String getType() {
		return type;
	}

	public String getMapping() {
		return mapping;
	}

	public String getSupplText() {
		return supplText;
	}

	public String getValidationError() {
		return validationError;
	}


	

}
