package dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008;

public class DosageQuantityStructure {

    protected Double dosageQuantityValue;
    protected String dosageQuantityFreeText;

    public Double getDosageQuantityValue() {
        return dosageQuantityValue;
    }

    public void setDosageQuantityValue(Double value) {
        this.dosageQuantityValue = value;
    }

    public String getDosageQuantityFreeText() {
        return dosageQuantityFreeText;
    }

    public void setDosageQuantityFreeText(String value) {
        this.dosageQuantityFreeText = value;
    }

}
