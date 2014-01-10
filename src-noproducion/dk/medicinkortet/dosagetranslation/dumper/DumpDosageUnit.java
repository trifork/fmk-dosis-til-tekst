package dk.medicinkortet.dosagetranslation.dumper;


public class DumpDosageUnit {

	private Long releaseNumber;
	private Integer code;
	private String textSingular;
	private String textPlural;
	
	public DumpDosageUnit(Long releaseNumber, Integer code, String textSingular, String textPlural) {
		this.releaseNumber = releaseNumber;
		this.code = code;
		this.textSingular = textSingular;
		this.textPlural = textPlural;
	}
	
	public Long getReleaseNumber() {
		return releaseNumber;
	}

	public Integer getCode() {
		return code;
	}

	public String getTextSingular() {
		return textSingular;
	}

	public String getTextPlural() {
		return textPlural;
	}
	
}
