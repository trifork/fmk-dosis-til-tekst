package dk.medicinkortet.web.shared.jaxb.dkma.medicinecard20120601;

public class UnitTexts {

	protected String singular;
	protected String plural;
	
	public UnitTexts(String singular, String plural) {
		this.singular = singular;
		this.plural = plural;
	}

	public String getSingular() {
		return singular;
	}

	public String getPlural() {
		return plural;
	}
	
}
