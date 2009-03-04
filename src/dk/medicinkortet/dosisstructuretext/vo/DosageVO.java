package dk.medicinkortet.dosisstructuretext.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

public class DosageVO implements Serializable  {
	
	private boolean administrationAccordingToSchemeInLocalSystemIndicator;
	private String dosageFreeText;
	private int dosageTimesIterationIntervalQuantity;
	private Date dosageTimesStartDate;
	private Date dosageTimesEndDate;
	private String dosageUnit;
		
	private Collection<DosageTimeVO> dosageTimes = new ArrayList<DosageTimeVO>();
	
	public void setAdministrationAccordingToSchemeInLocalSystemIndicator(
			boolean administrationAccordingToSchemeInLocalSystemIndicator) {
				this.administrationAccordingToSchemeInLocalSystemIndicator = administrationAccordingToSchemeInLocalSystemIndicator;
	}

	public void setDosageFreeText(String dosageFreeText) {
		this.dosageFreeText = dosageFreeText;
	}

	public boolean isAdministrationAccordingToSchemeInLocalSystemIndicator() {
		return administrationAccordingToSchemeInLocalSystemIndicator;
	}

	public String getDosageFreeText() {
		return dosageFreeText;
	}
	
	public Collection<DosageTimeVO> getDosageTimes() {
		return dosageTimes ;
	}
	
	public void setDosageTimes(Collection<DosageTimeVO> dosageTimes) {
		this.dosageTimes = dosageTimes;
	}
	
	public void addDosageTime(DosageTimeVO dosageTimeVO) {
		dosageTimes.add(dosageTimeVO);
	}
	
	public void setDosageTimesIterationIntervalQuantity(int dosageTimesIterationIntervalQuantity) {
		this.dosageTimesIterationIntervalQuantity = dosageTimesIterationIntervalQuantity;
	}

	public void setDosageTimesStartDate(Date dosageTimesStartDate) {
		this.dosageTimesStartDate = dosageTimesStartDate;
	}

	public void setDosageTimesEndDate(Date dosageTimesEndDate) {
		this.dosageTimesEndDate = dosageTimesEndDate;
	}
	
	public int getDosageTimesIterationIntervalQuantity() {
		return dosageTimesIterationIntervalQuantity;
	}

	public Date getDosageTimesStartDate() {
		return dosageTimesStartDate;
	}

	public Date getDosageTimesEndDate() {
		return dosageTimesEndDate;
	}

	public void setDosageUnit(String dosageUnit) {
		this.dosageUnit = dosageUnit;
	}

	public String getDosageUnit() {
		return dosageUnit;
	}
	
	public String toXml() {
		if(administrationAccordingToSchemeInLocalSystemIndicator) {
			return 
				"<DosageStructure>\n"+
				"\t<AdministrationAccordingToSchemeInLocalSystemIndicator/>\n"+
				"<DosageStructure>";				
			
		}
		else if(dosageFreeText!=null) {
			return 
				"<DosageStructure>\n"+
				"\t<DosageFreeText>"+dosageFreeText+"</DosageFreeText>\n"+
				"<DosageStructure>";				
		}
		else {
			return 
				"<DosageStructure>\n"+
				"\t<DosageTimesStructure>\n"+
				"\t\t<DosageTimesIterationIntervalQuantity>"+dosageTimesIterationIntervalQuantity+"</DosageTimesIterationIntervalQuantity>\n"+
				(dosageTimesStartDate!=null ? "\t\t<DosageTimesStartDate>"+formatDate(dosageTimesStartDate)+"</DosageTimesStartDate>\n" : "") +
				(dosageTimesEndDate!=null ? "\t\t<DosageTimesEndDate>"+formatDate(dosageTimesEndDate)+"</DosageTimesEndDate>\n" : "") +
				"\t\t<DosageQuantityUnitText>"+dosageUnit+"</DosageQuantityUnitText>\n"+
				toXml(dosageTimes) +
				"\t</DosageTimesStructure>\n"+
				"<DosageStructure>";				
		}
	}
	
	// We don't have java.text in GWT so we have to format the date manually
	private static String formatDate(Date date) { 
		int y = 1900+date.getYear();
		int m = 1+date.getMonth();
		int d = date.getDate();
		return y + "-" + (m<10 ? "0"+m : m) + "-" + (d<10 ? "0"+d : d);  
	}
	
	private ArrayList<Integer> getDays(Collection<DosageTimeVO> dosageTimes) {
		ArrayList<Integer> days = new ArrayList<Integer>();
		for(DosageTimeVO dosageTime : dosageTimes) {
			if(!days.contains(dosageTime.getDayIdentifier())) {
				days.add(dosageTime.getDayIdentifier());
			}	
		}
		Collections.sort(days);
		return days;
	}
	
	private String toXml(Collection<DosageTimeVO> dosageTimes) {
		if(dosageTimes==null)
			return "";
		ArrayList<Integer> days = getDays(dosageTimes);
		StringBuffer b = new StringBuffer();
		for(int day: days) {
			b.append("\t\t<DosageDayElementStructure>\n");
			b.append("\t\t\t<DosageDayIdentifier>"+day+"</DosageDayIdentifier>\n");
			for(DosageTimeVO dosageTime : dosageTimes)
				if(dosageTime.getDayIdentifier()==day)
					b.append(dosageTime.toXml());
			b.append("\t\t</DosageDayElementStructure>\n");			
		}
		return b.toString();
	}	
	
}
