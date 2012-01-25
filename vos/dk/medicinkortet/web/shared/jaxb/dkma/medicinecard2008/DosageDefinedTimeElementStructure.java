package dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008;

public class DosageDefinedTimeElementStructure {

    protected DosageQuantityStructure minimalDosageQuantityStructure;
    protected DosageQuantityStructure maximalDosageQuantityStructure;
    protected DosageQuantityStructure dosageQuantityStructure;

    public DosageQuantityStructure getMinimalDosageQuantityStructure() {
        return minimalDosageQuantityStructure;
    }

    public void setMinimalDosageQuantityStructure(DosageQuantityStructure value) {
        this.minimalDosageQuantityStructure = value;
    }

    public DosageQuantityStructure getMaximalDosageQuantityStructure() {
        return maximalDosageQuantityStructure;
    }

    public void setMaximalDosageQuantityStructure(DosageQuantityStructure value) {
        this.maximalDosageQuantityStructure = value;
    }

    public DosageQuantityStructure getDosageQuantityStructure() {
        return dosageQuantityStructure;
    }

    public void setDosageQuantityStructure(DosageQuantityStructure value) {
        this.dosageQuantityStructure = value;
    }

}
