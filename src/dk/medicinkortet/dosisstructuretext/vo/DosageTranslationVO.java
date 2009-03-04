package dk.medicinkortet.dosisstructuretext.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class DosageTranslationVO implements Serializable  {
	
	private String shortText;
	private String longText;
	private BigDecimal avgDailyDosage;
	private BigDecimal minAvgDailyDosage;
	private BigDecimal maxAvgDailyDosage;
	
	public String getShortText() {
		return shortText;
	}
	public void setShortText(String shortText) {
		this.shortText = shortText;
	}
	public String getLongText() {
		return longText;
	}
	public void setLongText(String longText) {
		this.longText = longText;
	}
	public BigDecimal getAvgDailyDosage() {
		return avgDailyDosage;
	}
	public void setAvgDailyDosage(BigDecimal avgDailyDosage) {
		this.avgDailyDosage = avgDailyDosage;
	}
	public BigDecimal getMinAvgDailyDosage() {
		return minAvgDailyDosage;
	}
	public void setMinAvgDailyDosage(BigDecimal minAvgDailyDosage) {
		this.minAvgDailyDosage = minAvgDailyDosage;
	}
	public BigDecimal getMaxAvgDailyDosage() {
		return maxAvgDailyDosage;
	}
	public void setMaxAvgDailyDosage(BigDecimal maxAvgDailyDosage) {
		this.maxAvgDailyDosage = maxAvgDailyDosage;
	}
	
}
