package dk.medicinkortet.dosisstructuretext.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class DosageTime20090101VO implements Serializable {
	
	private long drugMedicationId;
	private int dayIdentifier;
	private Date time;
	private BigDecimal dosageQuantityValue;
	private BigDecimal minDosageQuantityValue;
	private BigDecimal maxDosageQuantityValue;
	private boolean isAccordingToNeed;
	private boolean isMorning;
	private boolean isNoon;
	private boolean isEvening;
	private boolean isNight;
	private boolean isDosageTime;
		
	public DosageTime20090101VO() {}

	public DosageTime20090101VO(long drugMedicationId, int dayIdentifier, Date time,
			BigDecimal dosageQuantityValue, BigDecimal minDosageQuantityValue,BigDecimal maxDosageQuantityValue, 
			boolean isAccordingToNeed, boolean isMorning, boolean isNoon,
			boolean isEvening, boolean isNight, boolean isDosageTime) {
		this.drugMedicationId = drugMedicationId;
		this.dayIdentifier = dayIdentifier;
		this.time = time;
		this.dosageQuantityValue = dosageQuantityValue;
		this.minDosageQuantityValue = minDosageQuantityValue;
		this.maxDosageQuantityValue = maxDosageQuantityValue;
		this.isAccordingToNeed = isAccordingToNeed;
		this.isMorning = isMorning;
		this.isNoon = isNoon;
		this.isEvening = isEvening;
		this.isNight = isNight;
		this.isDosageTime = isDosageTime;
	}

	public int getDayIdentifier() {
		return dayIdentifier;
	}

	public Date getTime() {
		return time;
	}

	public BigDecimal getDosageQuantityValue() {
		return dosageQuantityValue;
	}
	
	public long getDrugMedicationId() {
		return drugMedicationId;
	}

	public boolean isAccordingToNeed() {
		return isAccordingToNeed;
	}

	public BigDecimal getMinDosageQuantityValue() {
		return minDosageQuantityValue;
	}

	public BigDecimal getMaxDosageQuantityValue() {
		return maxDosageQuantityValue;
	}

	public boolean isMorning() {
		return isMorning;
	}

	public boolean isNoon() {
		return isNoon;
	}

	public boolean isEvening() {
		return isEvening;
	}

	public boolean isNight() {
		return isNight;
	}

	public boolean isDosageTime() {
		return isDosageTime;
	}

	public String getDosageTimeStructureType() {
		if( isAccordingToNeed ) return "AccordingToNeedDosageTimeElementStructure";
		if( isMorning ) return "MorningDosageTimeElementStructure";
		if( isNoon ) return "NoonDosageTimeElementStructure";
		if( isEvening ) return "EveningDosageTimeElementStructure";
		if( isNight ) return "NightDosageTimeElementStructure";
		if( isDosageTime ) return "DosageTimeElementStructure";
		throw new RuntimeException("The type of the DosageTimeStructure does not match any of AccordingToNeedDosageTimeElementStructure, MorningDosageTimeElementStructure, "+
				"NoonDosageTimeElementStructure, EveningDosageTimeElementStructure, NightDosageTimeElementStructure, DosageTimeElementStructure");
	}
	
	public String toXml() {
		return 
			"\t\t\t"+getStartTag()+"\n"+
			(time!=null ? "\t\t\t\t<DosageTimeTime>"+formatTime(time)+"</DosageTimeTime>\n" : "") + 
			(dosageQuantityValue!=null ? "\t\t\t\t<DosageQuantityValue>"+dosageQuantityValue+"</DosageQuantityValue>\n" : "") +
			(minDosageQuantityValue!=null ? "\t\t\t\t<MinimalDosageQuantityValue>"+minDosageQuantityValue+"</MinimalDosageQuantityValue>\n" : "") +
			(maxDosageQuantityValue!=null ? "\t\t\t\t<MaximalDosageQuantityValue>"+maxDosageQuantityValue+"</MaximalDosageQuantityValue>\n" : "") +
			"\t\t\t"+getEndTag()+"\n";			
	}

	public String dosageToXml(BigDecimal value) {
		return 
			"\t\t\t\t\t<DosageQuantityValue>"+value+"</DosageQuantityValue>";
	}
	
	// We don't have java.text in GWT so we have to format the date manually
	private static String formatTime(Date date) { 
		int h = date.getHours();
		int m = date.getMinutes();
		int s = date.getSeconds();
		return (h<10 ? "0"+h : h) + ":" + (m<10 ? "0"+m : m) + ":" + (s<10 ? "0"+s : s);  
	}
	
	
	private String getStartTag() {
		return "<"+getTagName()+">";
	}

	private String getEndTag() {
		return "</"+getTagName()+">";
	}
	
	private String getTagName() {
		if(isAccordingToNeed)
			return "AccordingToNeedDosageTimeElementStructure";
		else if(isMorning)
			return "MorningDosageTimeElementStructure";
		else if(isNoon)
			return "NoonDosageTimeElementStructure";
		else if(isEvening)
			return "EveningDosageTimeElementStructure";
		else if(isNight)
			return "NightDosageTimeElementStructure";
		else if(isDosageTime)
			return "DosageTimeElementStructure";
		else 
			throw new RuntimeException(); 
	}
	
}
