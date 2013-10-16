package dk.medicinkortet.dosagetranslation.dumper;

public class DumpDrugsDosageStructure {
	
	private Long releaseNumber;
	private Long drugId;
	private Long dosageStructureCode;
	
	public DumpDrugsDosageStructure(Long releaseNumber, Long drugId,
			Long dosageStructureCode) {
		this.releaseNumber = releaseNumber;
		this.drugId = drugId;
		this.dosageStructureCode = dosageStructureCode;
	}	
	
}
