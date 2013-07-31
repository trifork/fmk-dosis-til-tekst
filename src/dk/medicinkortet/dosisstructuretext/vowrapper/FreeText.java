package dk.medicinkortet.dosisstructuretext.vowrapper;

import java.util.Date;

public class FreeText {
	
	private Date startDate;
	private Date startDateTime;
	private boolean dosageStartedPreviously;

	private Date endDate;
	private Date endDateTime;
	private boolean dosageEndingUndetermined;
	
	private String text;
	
	public FreeText(Date startDate, Date startDateTime, boolean dosageStartedPreviously, Date endDate, Date endDateTime, boolean dosageEndingUndetermined, String text) {
		this.startDate = startDate;
		this.startDateTime = startDateTime;
		this.dosageStartedPreviously = dosageStartedPreviously;
		this.endDate = endDate;
		this.endDateTime = endDateTime;
		this.dosageEndingUndetermined = dosageEndingUndetermined;
		this.text = text;
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

	public String getText() {
		return text;
	}
	
}
