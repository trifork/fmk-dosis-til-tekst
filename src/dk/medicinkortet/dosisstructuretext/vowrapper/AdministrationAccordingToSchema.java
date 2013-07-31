package dk.medicinkortet.dosisstructuretext.vowrapper;

import java.util.Date;

public class AdministrationAccordingToSchema {
	
	private Date startDate;
	private Date startDateTime;
	private boolean dosageStartedPreviously;

	private Date endDate;
	private Date endDateTime;
	private boolean dosageEndingUndetermined;
	
	public AdministrationAccordingToSchema(Date startDate, Date startDateTime,	boolean dosageStartedPreviously, Date endDate, Date endDateTime, boolean dosageEndingUndetermined) {
		this.startDate = startDate;
		this.startDateTime = startDateTime;
		this.dosageStartedPreviously = dosageStartedPreviously;
		this.endDate = endDate;
		this.endDateTime = endDateTime;
		this.dosageEndingUndetermined = dosageEndingUndetermined;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getStartDateTime() {
		return startDateTime;
	}

	public boolean isDosageStartedPreviously() {
		return dosageStartedPreviously;
	}

	public Date getEndDate() {
		return endDate;
	}

	public Date getEndDateTime() {
		return endDateTime;
	}

	public boolean isDosageEndingUndetermined() {
		return dosageEndingUndetermined;
	}
	
}
