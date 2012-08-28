package dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2009;

public class DosageDefinedTimeElementStructure {

    protected Double minimalDosageQuantityValue;
    protected Double maximalDosageQuantityValue;
    protected Double dosageQuantityValue;

    public Double getMinimalDosageQuantityValue() {
        return minimalDosageQuantityValue;
    }

    public void setMinimalDosageQuantityValue(Double value) {
        this.minimalDosageQuantityValue = value;
    }

    public Double getMaximalDosageQuantityValue() {
        return maximalDosageQuantityValue;
    }

    public void setMaximalDosageQuantityValue(Double value) {
        this.maximalDosageQuantityValue = value;
    }

    public Double getDosageQuantityValue() {
        return dosageQuantityValue;
    }

    public void setDosageQuantityValue(Double value) {
        this.dosageQuantityValue = value;
    }

}
