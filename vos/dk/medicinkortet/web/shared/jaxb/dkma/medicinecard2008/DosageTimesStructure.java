package dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DosageTimesStructure {

    protected int dosageTimesIterationIntervalQuantity;
    protected Date dosageTimesStartDate;
    protected Date dosageTimesEndDate;
    protected DosageQuantityUnitTextType dosageQuantityUnitText;
    protected List<DosageDayElementStructure> dosageDayElementStructures;

    public int getDosageTimesIterationIntervalQuantity() {
        return dosageTimesIterationIntervalQuantity;
    }

    public void setDosageTimesIterationIntervalQuantity(int value) {
        this.dosageTimesIterationIntervalQuantity = value;
    }

    public Date getDosageTimesStartDate() {
        return dosageTimesStartDate;
    }

    public void setDosageTimesStartDate(Date value) {
        this.dosageTimesStartDate = value;
    }

    public Date getDosageTimesEndDate() {
        return dosageTimesEndDate;
    }

    public void setDosageTimesEndDate(Date value) {
        this.dosageTimesEndDate = value;
    }

    public DosageQuantityUnitTextType getDosageQuantityUnitText() {
        return dosageQuantityUnitText;
    }

    public void setDosageQuantityUnitText(DosageQuantityUnitTextType value) {
        this.dosageQuantityUnitText = value;
    }

    public List<DosageDayElementStructure> getDosageDayElementStructures() {
        if (dosageDayElementStructures == null) {
            dosageDayElementStructures = new ArrayList<DosageDayElementStructure>();
        }
        return this.dosageDayElementStructures;
    }

    public void setDosageDayElementStructures(List<DosageDayElementStructure> value) {
    	this.dosageDayElementStructures = value;
    }
 
}
