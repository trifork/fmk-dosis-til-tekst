package dk.medicinkortet.web.shared.jaxb.dkma.medicinecard;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DosageTimesStructure {

    protected int dosageTimesIterationIntervalQuantity;
    protected Date dosageTimesStartDateTime;
    protected Date dosageTimesStartDate;
    protected Date dosageTimesEndDateTime;
    protected Date dosageTimesEndDate;
    protected String dosageQuantityUnitText;
    protected String dosageSupplementaryText;
    protected List<DosageDayElementStructure> dosageDayElementStructures;

    public int getDosageTimesIterationIntervalQuantity() {
        return dosageTimesIterationIntervalQuantity;
    }

    public void setDosageTimesIterationIntervalQuantity(int value) {
        this.dosageTimesIterationIntervalQuantity = value;
    }

    public Date getDosageTimesStartDateTime() {
        return dosageTimesStartDateTime;
    }

    public void setDosageTimesStartDateTime(Date value) {
        this.dosageTimesStartDateTime = value;
    }

    public Date getDosageTimesStartDate() {
        return dosageTimesStartDate;
    }

    public void setDosageTimesStartDate(Date value) {
        this.dosageTimesStartDate = value;
    }

    public Date getDosageTimesEndDateTime() {
        return dosageTimesEndDateTime;
    }

    public void setDosageTimesEndDateTime(Date value) {
        this.dosageTimesEndDateTime = value;
    }

    public Date getDosageTimesEndDate() {
        return dosageTimesEndDate;
    }

    public void setDosageTimesEndDate(Date value) {
        this.dosageTimesEndDate = value;
    }

    public String getDosageQuantityUnitText() {
        return dosageQuantityUnitText;
    }

    public void setDosageQuantityUnitText(String value) {
        this.dosageQuantityUnitText = value;
    }

    public String getDosageSupplementaryText() {
        return dosageSupplementaryText;
    }

    public void setDosageSupplementaryText(String value) {
        this.dosageSupplementaryText = value;
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
