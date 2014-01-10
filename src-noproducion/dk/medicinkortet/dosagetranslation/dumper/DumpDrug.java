package dk.medicinkortet.dosagetranslation.dumper;

public class DumpDrug {
	
	private long releaseNumber;
	private Long drugId;
	private String drugName;
	private Integer dosageUnitCode;
	
	public DumpDrug(long releaseNumber, Long drugId, String drugName, Integer dosageUnitCode) {
		this.releaseNumber = releaseNumber;
		this.drugId = drugId;
		this.drugName = drugName;
		this.dosageUnitCode = dosageUnitCode;
	}

	public Long getReleaseNumber() {
		return releaseNumber;
	}
	
	public Long getDrugId() {
		return drugId;
	}

	public String getDrugName() {
		return drugName;
	}
	
	public Integer getDosageUnitCode() {
		return dosageUnitCode;
	}	
	
}
