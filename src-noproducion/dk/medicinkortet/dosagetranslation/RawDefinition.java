package dk.medicinkortet.dosagetranslation;

import java.util.ArrayList;

import dk.medicinkortet.dosagetranslation.wrapper.DosageWrapperWrapper;
import dk.medicinkortet.dosisstructuretext.LongTextConverter;
import dk.medicinkortet.dosisstructuretext.ShortTextConverter;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;


public class RawDefinition {
	
	// Read from the source (text file)
	private int rowNumber; 
	private Long drugIdentifier;
	private String drugName;
	private String unitSingular;
	private String unitPlural;
	private String type;
	private String iterationInterval; 
	private String mapping;
	private String supplementaryText;
	
	// Intermediate values
	private ArrayList<Integer> iterationIntervals;
	private ArrayList<String> types;
	private ArrayList<String> mappings;
	
	private boolean isComplete;
	private String incompleteCause;
	
	private DosageWrapper wrapper;
	
	private String shortText; 
	private String longText;
	
	public RawDefinition(int rowNumber, Long drugIdentifier, String drugName, String unitSingular, String unitPlural, String type, String iterationInterval, String mapping, String supplementaryText) {
		this.rowNumber = rowNumber;
		this.drugIdentifier = drugIdentifier;
		this.drugName = drugName;
		this.unitSingular = unitSingular;
		this.unitPlural = unitPlural;
		this.type = type;
		this.iterationInterval = iterationInterval;
		this.mapping = mapping;
		this.supplementaryText = supplementaryText;
		if(supplementaryText!=null && supplementaryText.trim().length()==0)
			this.supplementaryText = null;
		this.isComplete = RawDefinitionValidator.isComplete(this);
		if(!isComplete)
			incompleteCause = RawDefinitionValidator.getIncompleteCause(this);
		
		if(isComplete) {
			try {
				wrapper = DosageWrapperWrapper.wrap(this);
				shortText = ShortTextConverter.convert(wrapper);
				longText = LongTextConverter.convert(wrapper);
				if(shortText!=null && shortText.length()>70)
					shortText = null;
			}
			catch(ValidationException e) {
				throw new RuntimeException(e);
			}
		}
		
	}
	
	public DosageWrapper getDosageWrapper() {
		return wrapper;
	}
	
	public Long getDrugIdentifier() {
		return drugIdentifier;
	}
	
	public String getDrugName() {
		return drugName;
	}
	
	public int getRowNumber() {
		return rowNumber;
	}
	
	public String getUnitSingular() {
		return unitSingular;
	}

	public String getUnitPlural() {
		return unitPlural;
	}

	public String getType() {
		return type;
	}

	public String getIterationInterval() {
		return iterationInterval;
	}

	public String getMapping() {
		return mapping;
	}

	public String getSupplementaryText() {
		return supplementaryText;
	}

	public boolean isComplete() {
		return isComplete;
	}
	
	public boolean hasUnits() {
		return unitSingular!=null && unitPlural!=null && unitSingular.trim().length()>0 && unitPlural.trim().length()>0;
	}

	public String getIncompleteCause() {
		return incompleteCause;
	}
	
	public void setDosageTranslation(String shortText, String longText) {
		this.shortText = shortText;
		this.longText = longText;
	}

	public String getShortText() {
		return shortText;
	}

	public String getLongText() {
		return longText;
	}

	public ArrayList<Integer> getIterationIntervals() {
		return iterationIntervals;
	}

	public void setIterationIntervals(ArrayList<Integer> iterationIntervals) {
		this.iterationIntervals = iterationIntervals;
	}

	public ArrayList<String> getTypes() {
		return types;
	}
	
	public String getSimpleString() {
		if(mapping==null || mapping.trim().length()==0)
			return null;
		if(mappings.size()>1)
			return null;
		if(mapping.indexOf(":")>0)
			return null;
		return mapping;
	}

	public void setTypes(ArrayList<String> types) {
		this.types = types;
	}

	public ArrayList<String> getMappings() {
		return mappings;
	}

	public void setMappings(ArrayList<String> mappings) {
		this.mappings = mappings;
	}
	
}
