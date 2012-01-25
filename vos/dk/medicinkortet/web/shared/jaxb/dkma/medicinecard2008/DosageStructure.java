package dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008;

public class DosageStructure {

    protected AdministrationAccordingToSchemeInLocalSystemIndicator administrationAccordingToSchemeInLocalSystemIndicator;
    protected String dosageFreeText;
    protected DosageTimesStructure dosageTimesStructure;

    public AdministrationAccordingToSchemeInLocalSystemIndicator getAdministrationAccordingToSchemeInLocalSystemIndicator() {
        return administrationAccordingToSchemeInLocalSystemIndicator;
    }

    public void setAdministrationAccordingToSchemeInLocalSystemIndicator(AdministrationAccordingToSchemeInLocalSystemIndicator value) {
        this.administrationAccordingToSchemeInLocalSystemIndicator = value;
    }

    public String getDosageFreeText() {
        return dosageFreeText;
    }

    public void setDosageFreeText(String value) {
        this.dosageFreeText = value;
    }

    public DosageTimesStructure getDosageTimesStructure() {
        return dosageTimesStructure;
    }

    public void setDosageTimesStructure(DosageTimesStructure value) {
        this.dosageTimesStructure = value;
    }

}
