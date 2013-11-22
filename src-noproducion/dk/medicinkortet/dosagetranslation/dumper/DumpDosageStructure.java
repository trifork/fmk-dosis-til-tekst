package dk.medicinkortet.dosagetranslation.dumper;

public class DumpDosageStructure {

	private Long releaseNumber;
	private Long code;
		
	private String type;
	private String simpleString;
	private String supplementaryText;
	private String xml;
	private String shortTranslation;
	private String longTranslation;
	
	public DumpDosageStructure(Long releaseNumber,
			Long code, String type, String simpleString, 
			String supplementaryText, String xml, String shortTranslation,
			String longTranslation) {
		this.releaseNumber = releaseNumber;
		this.code = code;
		this.type = type;
		this.simpleString = simpleString;
		this.supplementaryText = supplementaryText;
		if(supplementaryText!=null && supplementaryText.trim().length()==0)
			this.supplementaryText = null;
		this.xml = xml;
		this.shortTranslation = shortTranslation;
		this.longTranslation = longTranslation;
	}

	public void setCode(Long code) {
		this.code = code;
	}
	
	public Long getCode() {
		return code;
	}

	public String getShortTranslation() {
		return shortTranslation;
	}
	
}
