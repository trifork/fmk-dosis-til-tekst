package dk.medicinkortet.dosagetranslation;

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

	public void validate() throws ValidationException {
		String v = "";
		if(unitSingular==null||unitSingular.length()==1)
			v += "enhed-ental, ";
		if(unitPlural==null||unitPlural.length()==1)
			v += "enhed-flertal, ";
		if(iterationInterval==null||iterationInterval.length()==1)
			v += "iterationsinterval, ";
		if(type==null||type.length()==1)
			v += "type, ";
		if(mapping==null||mapping.length()==1)
			v += " mapping, ";
		if(v.length()>0) {
			v = "Mangler "+v.substring(0, v.length()-2);
			validationError = v;
			throw new ValidationException(v);
		}
		
		if(type.equals("M+M+A+N") && mapping.indexOf(";")>=0)
			throw new ValidationException("Uoverensstemmelse mellem type og mapping");
		
		if(type.equals("N daglig") && mapping.indexOf("+")>=0)
			throw new ValidationException("Uoverensstemmelse mellem type og mapping");
	}
	

}
