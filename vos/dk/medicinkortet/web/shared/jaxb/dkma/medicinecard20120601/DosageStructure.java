package dk.medicinkortet.web.shared.jaxb.dkma.medicinecard20120601;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DosageStructure {

    protected int iterationInterval;
    protected Date startDate;
    protected Date startDateTime;
    protected Date endDateTime;
    protected Date endDate;
    protected String unitText;
    protected UnitTexts unitTexts;
    protected String supplementaryText;
    protected List<DosageDay> dosageDays;
    
    public DosageStructure(int iterationInterval, Date startDate, Date startDateTime, Date endDateTime, Date endDate, String unitText, UnitTexts unitTexts, String supplementaryText, List<DosageDay> dosageDays) {
		this.iterationInterval = iterationInterval;
		this.startDate = startDate;
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
		this.endDate = endDate;
		this.unitText = unitText;
		this.unitTexts = unitTexts;
		this.supplementaryText = supplementaryText;
		this.dosageDays = dosageDays;
	}

	public int getIterationInterval() {
        return iterationInterval;
    }

    public Date getStartDateTime() {
        return startDateTime;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public Date getEndDateTime() {
        return endDateTime;
    }

    public String getUnitText() {
        return unitText;
    }

    public UnitTexts getUnitTexts() {
        return unitTexts;
    }
    
    public String getSupplementaryText() {
        return supplementaryText;
    }

    public List<DosageDay> getDosageDays() {
        if (dosageDays == null) {
        	dosageDays = new ArrayList<DosageDay>();
        }
        return this.dosageDays;
    }

}