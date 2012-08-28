package dk.medicinkortet.web.shared.jaxb.dkma.medicinecard20120601;


public class Dosage {

    protected AdministrationAccordingToSchemaInLocalSystem administrationAccordingToSchemaInLocalSystem;
    protected String freeText;
    protected DosageStructure dosageStructure;

    public Dosage(AdministrationAccordingToSchemaInLocalSystem administrationAccordingToSchemaInLocalSystem) {
    	this.administrationAccordingToSchemaInLocalSystem = administrationAccordingToSchemaInLocalSystem;
    }
    
    public Dosage(String freeText) {
    	this.freeText = freeText;
    }
    
    public Dosage(DosageStructure dosageStructure) {
    	this.dosageStructure = dosageStructure;
    }

	public AdministrationAccordingToSchemaInLocalSystem getAdministrationAccordingToSchemaInLocalSystem() {
		return administrationAccordingToSchemaInLocalSystem;
	}

	public String getFreeText() {
		return freeText;
	}

	public DosageStructure getDosageStructure() {
		return dosageStructure;
	}    
    
}
