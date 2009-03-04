package dk.medicinkortet.dosisstructuretext;

import java.math.BigDecimal;

/**
 * Container class for holding the result of a conversion to short text, long text and/or calculation of 
 * avg. daily dosis   
 */
public class DosisStructureText {
	
	private String version = null;
	private String priceListDosageCode;
	private String priceListDosageText;
	
	private String longText = null;
	private String shortText = null;
	private String shortTextFilter = null;
	
	private BigDecimal avgDailyDosis = null;
	private BigDecimal minAvgDailyDosis = null;
	private BigDecimal maxAvgDailyDosis = null;
	private String unit = null;
		
	public String getLongText() {
		return longText;
	}
	
	public void setLongText(String longText) {
		this.longText = longText;
	}
	
	public String getShortText() {
		return shortText;
	}
	
	public void setShortText(String shortText) {
		this.shortText = shortText;
	}
	
	public BigDecimal getAvgDailyDosis() {
		return avgDailyDosis;
	}
	
	public void setAvgDailyDosis(BigDecimal avgDailyDosis) {
		this.avgDailyDosis = avgDailyDosis;
	}
	
	public BigDecimal getMinAvgDailyDosis() {
		return minAvgDailyDosis;
	}
	
	public void setMinAvgDailyDosis(BigDecimal minAvgDailyDosis) {
		this.minAvgDailyDosis = minAvgDailyDosis;
	}
	
	public BigDecimal getMaxAvgDailyDosis() {
		return maxAvgDailyDosis;
	}
	
	public void setMaxAvgDailyDosis(BigDecimal maxAvgDailyDosis) {
		this.maxAvgDailyDosis = maxAvgDailyDosis;
	}
	
	public String getUnit() {
		return unit;
	}
	
	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getPriceListDosageCode() {
		return priceListDosageCode;
	}

	public void setPriceListDosageCode(String priceListDosageCode) {
		this.priceListDosageCode = priceListDosageCode;
	}

	public String getPriceListDosageText() {
		return priceListDosageText;
	}

	public void setPriceListDosageText(String priceListDosageText) {
		this.priceListDosageText = priceListDosageText;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getShortTextFilter() {
		return shortTextFilter;
	}

	public void setShortTextFilter(String shortTextFilter) {
		this.shortTextFilter = shortTextFilter;
	}
	
}
