package dk.medicinkortet.web.shared.jaxb.dkma.medicinecard;

import java.util.ArrayList;
import java.util.List;

public class DosageDayElementStructure {

    protected int dosageDayIdentifier;
    protected List<DosageTimeElementStructure> dosageTimeElementStructures;
    protected List<DosageTimeElementStructure> accordingToNeedDosageTimeElementStructures;
    protected DosageDefinedTimeElementStructure morningDosageTimeElementStructure;
    protected DosageDefinedTimeElementStructure noonDosageTimeElementStructure;
    protected DosageDefinedTimeElementStructure eveningDosageTimeElementStructure;
    protected DosageDefinedTimeElementStructure nightDosageTimeElementStructure;

    public int getDosageDayIdentifier() {
        return dosageDayIdentifier;
    }

    public void setDosageDayIdentifier(int value) {
        this.dosageDayIdentifier = value;
    }

    public List<DosageTimeElementStructure> getDosageTimeElementStructures() {
        if (dosageTimeElementStructures == null) {
            dosageTimeElementStructures = new ArrayList<DosageTimeElementStructure>();
        }
        return this.dosageTimeElementStructures;
    }

    public List<DosageTimeElementStructure> getAccordingToNeedDosageTimeElementStructures() {
        if (accordingToNeedDosageTimeElementStructures == null) {
            accordingToNeedDosageTimeElementStructures = new ArrayList<DosageTimeElementStructure>();
        }
        return this.accordingToNeedDosageTimeElementStructures;
    }

    public DosageDefinedTimeElementStructure getMorningDosageTimeElementStructure() {
        return morningDosageTimeElementStructure;
    }

    public void setMorningDosageTimeElementStructure(DosageDefinedTimeElementStructure value) {
        this.morningDosageTimeElementStructure = value;
    }

    public DosageDefinedTimeElementStructure getNoonDosageTimeElementStructure() {
        return noonDosageTimeElementStructure;
    }

    public void setNoonDosageTimeElementStructure(DosageDefinedTimeElementStructure value) {
        this.noonDosageTimeElementStructure = value;
    }

    public DosageDefinedTimeElementStructure getEveningDosageTimeElementStructure() {
        return eveningDosageTimeElementStructure;
    }

    public void setEveningDosageTimeElementStructure(DosageDefinedTimeElementStructure value) {
        this.eveningDosageTimeElementStructure = value;
    }

    public DosageDefinedTimeElementStructure getNightDosageTimeElementStructure() {
        return nightDosageTimeElementStructure;
    }

    public void setNightDosageTimeElementStructure(DosageDefinedTimeElementStructure value) {
        this.nightDosageTimeElementStructure = value;
    }

}
