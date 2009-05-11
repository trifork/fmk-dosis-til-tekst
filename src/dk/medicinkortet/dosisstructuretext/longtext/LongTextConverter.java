package dk.medicinkortet.dosisstructuretext.longtext;

import dk.medicinkortet.dosisstructuretext.DosageValidationException;
import dk.medicinkortet.dosisstructuretext.DosisStructureText;
import dk.medicinkortet.dosisstructuretext.TextHelper;
import dk.medicinkortet.dosisstructuretext.simpelxml.Node;
import dk.medicinkortet.dosisstructuretext.simpelxml.Nodes;

/**
 * Converts the dosage to long text. This type of conversion must always be possible, and should resemble the dosage
 * structure in text form.
 */
public class LongTextConverter {

	/**
	 * Converts the dosage to long text, only one Dosage
	 * @param root Node containing either a DosageMappingStructure or a DosageStructure
	 * @param result the passed result object is updated with the long text
	 */
	public static void make(Node root, DosisStructureText result) throws DosageValidationException {	
		Nodes dosageMappingStructures = root.findChildNodes("*", "DosageMappingStructure");
		if(dosageMappingStructures!=null&&dosageMappingStructures.size()>0) {
			for(int i=0; i<dosageMappingStructures.size(); i++) { 
				Node dosageMappingStructure = dosageMappingStructures.getNode(i);
				makeFromDosageMappingStructure(dosageMappingStructure, result);
			}
			return;
		}

		Nodes dosageStructures = root.findChildNodes("*", "DosageStructure");
		if(dosageStructures!=null&&dosageStructures.size()>0) { 
			for(int i=0; i<dosageStructures.size(); i++) {
				Node dosageStructure = dosageStructures.getNode(i);
				makeFromDosageStructure(dosageStructure, result);
			}
			return;
		}

		throw new RuntimeException("No DosageMappingStructure or DosageStructure in XML");
	}
	
	/**
	 * Generates the long text from a DosageMappingStructure
	 * @param dosageMappingStructure
	 * @param result
	 */
	private static void makeFromDosageMappingStructure(Node dosageMappingStructure, DosisStructureText result) throws DosageValidationException {
		String priceListDosageCode = dosageMappingStructure.findChildNodeText("*", "PriceListDosageCode");
		String priceListDosageText = dosageMappingStructure.findChildNodeText("*", "PriceListDosageText");
		if(priceListDosageCode!=null&&priceListDosageText!=null) {
			result.setPriceListDosageCode(priceListDosageCode);
			result.setPriceListDosageText(priceListDosageText);
		}
		Node dosageStructure = dosageMappingStructure.getChildNode("*", "DosageStructure");
		makeFromDosageStructure(dosageStructure, result);		
	}	
	
	/**
	 * Generates the long text from a DosageStructure
	 * @param dosageStructure
	 * @param result
	 */
	private static void makeFromDosageStructure(Node dosageStructure, DosisStructureText result) throws DosageValidationException {		
		// Is free text?
		boolean isDosageFreeText = dosageStructure.findChildNodeExists("*", "DosageFreeText");
		if(isDosageFreeText) {
			result.setLongText(dosageStructure.findChildNodeText("*", "DosageFreeText"));
			return;
		}

		// Is tag "in local system"?
		boolean administrationAccordingToSchemeInLocalSystem = dosageStructure.findChildNodeExists("*", "AdministrationAccordingToSchemeInLocalSystemIndicator");
		if(administrationAccordingToSchemeInLocalSystem) {
			result.setLongText("Dosering efter skema i lokalt system");
			return;
		}
		
		// Must be DosageTimesStructure
		Node dosageTimesStructure = dosageStructure.getChildNode("*", "DosageTimesStructure");
		result.setLongText(makeFromDosageTimesStructure(dosageTimesStructure).toString().trim());
		return;
	}
	
	/**
	 * Makes the header with the valid dates and calls on to add the days to the string buffer
	 * @param dosageTimesStructure
	 * @return long text
	 */
	private static StringBuffer makeFromDosageTimesStructure(Node dosageTimesStructure) throws DosageValidationException {
		StringBuffer s = new StringBuffer();

		// Make header, daily / every
		int dosageTimesIterationInterval = dosageTimesStructure.getChildNodeInt("*", "DosageTimesIterationIntervalQuantity");
		String startDate = dosageTimesStructure.findChildNodeText("*", "DosageTimesStartDate");
		String endDate = dosageTimesStructure.findChildNodeText("*", "DosageTimesEndDate");
		if(startDate!=null&&endDate!=null&&startDate.equals(endDate)) // Special case: If there is only one day write just it  
			{}
		else if(dosageTimesIterationInterval==0)
			{} 
		else if(dosageTimesIterationInterval==1)
			s.append("Daglig ");
		else if(dosageTimesIterationInterval>0)
			s.append("Hver "+dosageTimesIterationInterval+". dag ");				
		
		/* 
		// Make header, date or dates
		String dosageTimesStartDate = TextHelper.formatDateString(dosageTimesStructure.getChildNodeText("*", "DosageTimesStartDate"));
		String dosageTimesEndDate = TextHelper.formatDateString(dosageTimesStructure.findChildNodeText("*", "DosageTimesEndDate"));
		
		
		if(dosageTimesStartDate.equals(dosageTimesEndDate)) {
			s.append(dosageTimesStartDate);
		}
		else if(dosageTimesEndDate!=null) {
			if(s.length()==0) 
				s.append("Fra og med ");
			else 
				s.append("fra og med ");
			s.append(dosageTimesStartDate).append(" til og med ").append(dosageTimesEndDate);
		}
		else {
			if(s.length()==0) 
				s.append("Fra og med ");
			else 
				s.append("fra og med ");
			s.append(dosageTimesStartDate);			
		} 
		s.append(":\n");
		*/
		
		// Get the unit, don't add for now				
		String dosageQuantityUnitText = dosageTimesStructure.getChildNodeText("*", "DosageQuantityUnitText");				
		
		// Days...
		Nodes dosageDayElements = dosageTimesStructure.getChildNodes("*", "DosageDayElementStructure"); 
		dosageDayElements.sortBy("*", "DosageDayIdentifier", Integer.class);
		boolean onlyDay1 = false;
		if(dosageDayElements.size()==1 && dosageDayElements.getNode(0).getChildNode(0).getInt()==1) 
			onlyDay1 = true;
		else
			s.append("\n");
		for(int i=0; i<dosageDayElements.size(); i++) {
			s.append(makeFromDosageDayElementStructure(dosageDayElements.getNode(i), dosageQuantityUnitText, onlyDay1));			
		}		
		
		return s;
	}	
	
		
	/**
	 * Makes the dosage from day
	 * @param dosageDayElementStructure
	 * @param dosageQuantityUnitText
	 * @param onlyDay1 If the dosage only contains one day (or is the same very day) make a slightly simpler text 
	 * @return
	 */
	private static StringBuffer makeFromDosageDayElementStructure(Node dosageDayElementStructure, String dosageQuantityUnitText, boolean onlyDay1) throws DosageValidationException {		
		StringBuffer s = new StringBuffer();
		
		String dosageDayIdentifier = dosageDayElementStructure.getChildNodeText(0);
		if(dosageDayIdentifier.equals("0")) {
			{} // add nothing, "no specific day"
		}
		else if(!onlyDay1){
			s.append("Dag ").append(dosageDayIdentifier).append(": ");
		}		
		Nodes allNodes = new Nodes();
		
		Nodes accordingToNeedDosageTimeElementStructure = dosageDayElementStructure.findChildNodes("*", "AccordingToNeedDosageTimeElementStructure");
		accordingToNeedDosageTimeElementStructure.sortBy("*", "DosageTimeTime", String.class);
		allNodes.addNodes(accordingToNeedDosageTimeElementStructure);
		
		Nodes dosageTimeElementStructures1 = dosageDayElementStructure.findChildNodes("*", "DosageTimeElementStructure");
		dosageTimeElementStructures1.sortBy("*", "DosageTimeTime", String.class);
		allNodes.addNodes(dosageTimeElementStructures1);

		allNodes.addNodes(dosageDayElementStructure.findChildNodes("*", "MorningDosageTimeElementStructure"));
		allNodes.addNodes(dosageDayElementStructure.findChildNodes("*", "NoonDosageTimeElementStructure"));
		allNodes.addNodes(dosageDayElementStructure.findChildNodes("*", "EveningDosageTimeElementStructure"));
		allNodes.addNodes(dosageDayElementStructure.findChildNodes("*", "NightDosageTimeElementStructure"));
				
		if(allNodes.allEquals()&&allNodes.size()>1) {
			s.append(makeFromDosageTimeElementStructure(allNodes.getNode(0), dosageQuantityUnitText));	
			s.append(" ").append(allNodes.size()).append(" gange");
		}
		else {
			for(int j=0; j<allNodes.size(); j++) {
				s.append(makeFromDosageTimeElementStructure(allNodes.getNode(j), dosageQuantityUnitText));	
				if(j<allNodes.size()-1)
					s.append(" + ");
			}
		}
		s.append("\n");
		
		return s;
	}
	
	/**
	 * Makes the dosage from a single dosage time 
	 * @param dosage
	 * @param dosageQuantityUnitText
	 * @return
	 */
	private static StringBuffer makeFromDosageTimeElementStructure(Node dosage, String dosageQuantityUnitText) throws DosageValidationException {
		StringBuffer s = new StringBuffer();
				
		// Optional time of dosage 
		String dosageTime = dosage.findChildNodeText("*", "DosageTimeTime");
		if(dosageTime!=null) {
			s.append("kl. ").append(TextHelper.formatTimeString(dosageTime)).append(" ");
			
		}
		
		String name = dosage.getName();
		String insertText = null;
		if(name.equals("DosageTimeElementStructure")) {
			// nothing to add in front
		}
		else if(name.equals("AccordingToNeedDosageTimeElementStructure")) {
			insertText = "efter behov";
		}
		
		// Add fixed dosage
		Node dosageQuantityStructure = dosage.findChildNode("*", "DosageQuantityStructure");
		Node minimalDosageQuantityStructure = dosage.findChildNode("*", "MinimalDosageQuantityStructure");
		Node maximalDosageQuantityStructure = dosage.findChildNode("*", "MaximalDosageQuantityStructure");

		String when = null;
		if(name.equals("MorningDosageTimeElementStructure")) {
			when = "morgen";		
		}
		else if(name.equals("NoonDosageTimeElementStructure")) {
			when = "middag";					
		}
		else if(name.equals("EveningDosageTimeElementStructure")) {
			when = "aften";					
		}
		else if(name.equals("NightDosageTimeElementStructure")) {
			when = "nat";					
		}				
		
		if(dosageQuantityStructure!=null) 
			s.append(makeFromDosageQuantityStructure(dosageQuantityStructure, dosageQuantityUnitText, when, insertText));
		else 
			s.append(makeFromMinMaxDosageQuantityStructure(minimalDosageQuantityStructure, maximalDosageQuantityStructure, dosageQuantityUnitText, when, insertText));									
		
		return s;
	}
	
	/**
	 * Makes the exact dosage quantity
	 * @param dosageQuantityStructure
	 * @param dosageQuantityUnitText
	 * @param insertText
	 * @return
	 */
	private static StringBuffer makeFromDosageQuantityStructure(Node dosageQuantityStructure, String dosageQuantityUnitText, String when, String insertText) {
		StringBuffer s = new StringBuffer();
		String dosageQuantityValue = dosageQuantityStructure.findChildNodeText("*", "DosageQuantityValue");
		String dosageQuantityFreeText = dosageQuantityStructure.findChildNodeText("*", "DosageQuantityFreeText");
		if(dosageQuantityValue!=null)
			s.append(TextHelper.decimalToFraction(dosageQuantityValue)).append(" ");
		if(dosageQuantityValue!=null&&dosageQuantityValue.equals("1"))
			s.append(TextHelper.unitToSingular(dosageQuantityUnitText));
		else 
			s.append(TextHelper.unitToPlural(dosageQuantityUnitText));
		if(when!=null) {
			s.append(" ").append(when);
		}		
		if(insertText!=null)
			s.append(" ").append(insertText);
		if(dosageQuantityFreeText!=null)
			s.append(" ").append(dosageQuantityFreeText);			
		return s;
	}
	
	/**
	 * Makes the min-max dosage quantity
	 * @param minimalDosageQuantityStructure
	 * @param maximalDosageQuantityStructure
	 * @param dosageQuantityUnitText
	 * @param insertText
	 * @return
	 */
	private static StringBuffer makeFromMinMaxDosageQuantityStructure(Node minimalDosageQuantityStructure, Node maximalDosageQuantityStructure, String dosageQuantityUnitText, String when, String insertText) {
		StringBuffer s = new StringBuffer();
		String minimalDosageQuantityValue = minimalDosageQuantityStructure.findChildNodeText("*", "DosageQuantityValue");
		String minimalDosageQuantityFreeText = minimalDosageQuantityStructure.findChildNodeText("*", "DosageQuantityFreeText");
		String maximalDosageQuantityValue = maximalDosageQuantityStructure.findChildNodeText("*", "DosageQuantityValue");
		String maximalDosageQuantityFreeText = maximalDosageQuantityStructure.findChildNodeText("*", "DosageQuantityFreeText");
		
		boolean sameText = minimalDosageQuantityFreeText!=null&&maximalDosageQuantityValue!=null&&minimalDosageQuantityFreeText.equals(maximalDosageQuantityFreeText)
			|| (minimalDosageQuantityFreeText==null&&maximalDosageQuantityValue==null);
		
		if(minimalDosageQuantityValue==null) {
			s.append(minimalDosageQuantityFreeText);
		}
		else {
			s.append(TextHelper.decimalToFraction(minimalDosageQuantityValue));
			if(!sameText&&minimalDosageQuantityFreeText!=null)
				s.append(" ").append(minimalDosageQuantityFreeText);
		}
		s.append("-");
		if(maximalDosageQuantityValue==null) {
			s.append(maximalDosageQuantityFreeText);
		}
		else {
			s.append(TextHelper.decimalToFraction(maximalDosageQuantityValue));
			if(!sameText&&maximalDosageQuantityFreeText!=null)
				s.append(" ").append(maximalDosageQuantityFreeText);
		}
		s.append(" ").append(TextHelper.unitToPlural(dosageQuantityUnitText));
		if(when!=null) {
			s.append(" ").append(when);
		}				
		if(insertText!=null)
			s.append(" ").append(insertText);
		if(sameText&&minimalDosageQuantityFreeText!=null)
			s.append(" ").append(minimalDosageQuantityFreeText);
		
		return s;
	}
	
	
}
