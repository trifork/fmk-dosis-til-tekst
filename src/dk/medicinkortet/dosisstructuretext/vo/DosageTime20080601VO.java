package dk.medicinkortet.dosisstructuretext.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class DosageTime20080601VO implements Serializable {
	
	private long drugMedicationId;
	private int dayIdentifier;
	private Date time;
	private BigDecimal dosageQuantityValue;
	private String dosageQuantityFreeText;
	private BigDecimal minDosageQuantityValue;
	private String minDosageQuantityFreeText;
	private BigDecimal maxDosageQuantityValue;
	private String maxDosageQuantityFreeText;
	private boolean isAccordingToNeed;
	private boolean isMorning;
	private boolean isNoon;
	private boolean isEvening;
	private boolean isNight;
	private boolean isDosageTime;
		
	public DosageTime20080601VO() {}

	public DosageTime20080601VO(long drugMedicationId, int dayIdentifier, Date time,
			BigDecimal dosageQuantityValue, String dosageQuantityFreeText, BigDecimal minDosageQuantityValue, String minDosageQuantityFreeText,
			BigDecimal maxDosageQuantityValue, String maxDosageQuantityFreeText, boolean isAccordingToNeed, boolean isMorning, boolean isNoon,
			boolean isEvening, boolean isNight, boolean isDosageTime) {
		this.drugMedicationId = drugMedicationId;
		this.dayIdentifier = dayIdentifier;
		this.time = time;
		this.dosageQuantityValue = dosageQuantityValue;
		this.dosageQuantityFreeText = dosageQuantityFreeText;
		this.minDosageQuantityValue = minDosageQuantityValue;
		this.minDosageQuantityFreeText = minDosageQuantityFreeText;
		this.maxDosageQuantityValue = maxDosageQuantityValue;
		this.maxDosageQuantityFreeText = maxDosageQuantityFreeText;
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

	public String getDosageQuantityFreeText() {
		return dosageQuantityFreeText;
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

	public String getMinDosageQuantityFreeText() {
		return minDosageQuantityFreeText;
	}

	public BigDecimal getMaxDosageQuantityValue() {
		return maxDosageQuantityValue;
	}

	public String getMaxDosageQuantityFreeText() {
		return maxDosageQuantityFreeText;
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
			(dosageQuantityValue!=null ? "\t\t\t\t<DosageQuantityStructure>\n"+dosageToXml(dosageQuantityValue, dosageQuantityFreeText)+"\n\t\t\t\t</DosageQuantityStructure>\n" : "") +
			(minDosageQuantityValue!=null ? "\t\t\t\t<MinimalDosageQuantityStructure>\n"+dosageToXml(minDosageQuantityValue, minDosageQuantityFreeText)+"\n\t\t\t\t</MinimalDosageQuantityStructure>\n" : "") +
			(maxDosageQuantityValue!=null ? "\t\t\t\t<MaximalDosageQuantityStructure>\n"+dosageToXml(maxDosageQuantityValue, maxDosageQuantityFreeText)+"\n\t\t\t\t</MaximalDosageQuantityStructure>\n" : "") +
			"\t\t\t"+getEndTag()+"\n";			
	}

	public String dosageToXml(BigDecimal value, String optionalText) {
		return 
			"\t\t\t\t\t<DosageQuantityValue>"+value+"</DosageQuantityValue>"+
			(optionalText!=null ? "\n\t\t\t\t\t<DosageQuantityFreeText>"+optionalText+"</DosageQuantityFreeText>" : "");
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
