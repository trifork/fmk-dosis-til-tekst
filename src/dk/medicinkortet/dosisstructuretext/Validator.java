package dk.medicinkortet.dosisstructuretext;

import dk.medicinkortet.dosisstructuretext.simpelxml.Node;
import dk.medicinkortet.dosisstructuretext.simpelxml.Nodes;
import dk.medicinkortet.dosisstructuretext.simpelxml.parser.XPathException;

/**
 * Performs validation of business rules, i.e. rules not expressed by the XML schema:
 * 
 * Validates rules for DosageDayIdentifier:
 * <ul>
 * 	<li>Negative values for DosageDayIdentifier isn't allowed</li>
 * 	<li>Values for DosageDayIdentifier must be sorted in order</li>
 * 	<li>Every value for the DosageDayIdentifier must only occur once</li> 
 * 	<li>The value for DosageDayIdentifier must not exceed the iteration interval if the iteration interval is not 0</li> 
 * </ul>
 * 
 * 
 * Validates rules for the DosageDayElementStructure:
 * <ul>
 * 	<li>Every DosageDayElementStructure must contain a DosageDayIdentifier follwed by DosageTimeElementStructure, AccordingToNeedDosageTimeElementStructure, MorningDosageTimeElementStructure, NoonDosageTimeElementStructure, EveningDosageTimeElementStructure or NightDosageTimeElementStructure</li>
 * </ul>
 * 
 * Validates rules for DosageDayElementStructure where DosageDayIdentifier is 0:
 * <ul>
 * 	<li>If the day number isn't specified only AccordingToNeedDosageTimeElementStructure elements are allowed for repeated dosages</li>
 * </ul>
 * 
 * If a rule isn't met a ValidationException is thrown.
 */
public class Validator {

	/**
	 * Validates all the DosageTimesStructure elements in or below the passed node. 
	 * The passed node is searched for all DosageTimesStructures while ignoring namespaces. If there is no DosageTimesStructure 
	 * nothing is done, and the call returns. 
	 * @param root node to search 
	 * @throws XPathException
	 * @throws ValidationException if a business rule isn't met. 
	 */
	public static void validate(Node root) throws XPathException, DosageValidationException {
		if(root.query("//*:DosageTimesStructure")!=null)
			validateDosageTimes(root);
	}
	
	/**
	 * Performs validations
	 * @param root
	 * @throws XPathException
	 * @throws ValidationException
	 */
	private static void validateDosageTimes(Node root) throws XPathException, DosageValidationException {
		validateDosageDayIdentifier(root);
		validateDosageDayElement(root);
		validateForZeroDosageTimesIterationInterval(root);
	}
	
	/**
	 * Validates rules for DosageDayIdentifier:
	 * - Negative values for DosageDayIdentifier isn't allowed
	 * - Values for DosageDayIdentifier must be sorted in order
	 * - Every value for the DosageDayIdentifier must only occur once 
	 * - The value for DosageDayIdentifier must not exceed the iteration interval if the iteration interval is not 0
	 * @param root
	 * @throws XPathException
	 * @throws ValidationException if a business rule isn't met. 
	 */
	private static void validateDosageDayIdentifier(Node root) throws XPathException, DosageValidationException {
		Nodes dosageStructures = (Nodes)root.query("//*:DosageStructure");				
		for(int i=0; i<dosageStructures.size(); i++) {
			Node dosageStructure = dosageStructures.getNode(i);
			int dosageTimesIterationInterval = ((Integer)dosageStructure.query("//*:DosageTimesIterationIntervalQuantity/integer()")).intValue();			
			Object obj = dosageStructure.query("//*:DosageDayIdentifier/integer()");
			if(obj instanceof Integer[]) {
				Integer[] dosageDayIdentifiers = (Integer[])obj;			
				int prev = -1;
				for(int j=0; j<dosageDayIdentifiers.length; j++) {
					if(dosageDayIdentifiers[j].intValue()<0)
						throw new DosageValidationException("DosageDayIdentifier has value "+dosageDayIdentifiers[j]+", negative values not allowed");
					if(dosageDayIdentifiers[j].intValue()<prev)
						throw new DosageValidationException("DosageDayIdentifier for day "+dosageDayIdentifiers[j]+" occurs after "+prev+", days must be in order");
					if(dosageDayIdentifiers[j].intValue()==prev)
						throw new DosageValidationException("DosageDayIdentifier has more than one definition of day "+dosageDayIdentifiers[j]+", only one definition for each day allowed");
					if(dosageDayIdentifiers[j].intValue()>dosageTimesIterationInterval && dosageTimesIterationInterval>0) 
						throw new DosageValidationException("DosageDayIdentifier has value "+dosageDayIdentifiers[j]+", the value must not exceed the iteration interval "+dosageTimesIterationInterval+" if the iteration interval is not 0");
					prev = dosageDayIdentifiers[j].intValue();
				}
			}
			else if(obj instanceof Integer) {
				Integer dosageDayIdentifier = (Integer)obj;
				if(dosageDayIdentifier.intValue()<0)
					throw new DosageValidationException("DosageDayIdentifier has value "+dosageDayIdentifier+", negative values not allowed");
				if(dosageDayIdentifier.intValue()>dosageTimesIterationInterval && dosageTimesIterationInterval>0) 
					throw new DosageValidationException("DosageDayIdentifier has value "+dosageDayIdentifier+", the value must not exceed the iteration interval "+dosageTimesIterationInterval+" if the iteration interval is not 0");
			}
		}
	}
	
	/**
	 * Validates rules for the DosageDayElementStructure
	 * - Every DosageDayElementStructure must contain a DosageDayIdentifier follwed by DosageTimeElementStructure, 
	 *   AccordingToNeedDosageTimeElementStructure, MorningDosageTimeElementStructure, NoonDosageTimeElementStructure, 
	 *   EveningDosageTimeElementStructure or NightDosageTimeElementStructure
	 * @param root
	 * @throws XPathException
	 * @throws ValidationException if a business rule isn't met. 
	 */
	private static void validateDosageDayElement(Node root) throws XPathException, DosageValidationException {
		Nodes dosageStructures = (Nodes)root.query("//*:DosageStructure");				
		for(int i=0; i<dosageStructures.size(); i++) {
			Node dosageStructure = dosageStructures.getNode(i);
			Nodes dosageDayElementStructures = (Nodes)dosageStructure.query("//*:DosageDayElementStructure");
			if(dosageDayElementStructures==null)
				throw new DosageValidationException("DosageStructure has no DosageDayElementStructures");				
			for(int j=0; j<dosageDayElementStructures.size(); j++) {
				Node dosageDayElementStructure = dosageDayElementStructures.getNode(j);
				if(dosageDayElementStructures.size()==0)
					throw new DosageValidationException("DosageDayElementStructure number "+(j+1)+" has no sub-elements, expected is DosageDayIdentifier follwed by DosageTimeElementStructure, AccordingToNeedDosageTimeElementStructure, MorningDosageTimeElementStructure, NoonDosageTimeElementStructure, EveningDosageTimeElementStructure or NightDosageTimeElementStructure");
				Node dosageDayIdentifier = dosageDayElementStructure.getChildNode("*", "DosageDayIdentifier");
				String day = dosageDayIdentifier.getText();
				if(dosageDayIdentifier==null)
					throw new DosageValidationException("DosageDayElementStructure has no DosageDayIdentifier-element");
				if(dosageDayElementStructure.getChildNodes().size()==1)
					throw new DosageValidationException("DosageDayElementStructure for DosageDayIdentifier "+day+" has no elements following DosageDayIdentifier, expected is DosageTimeElementStructure, AccordingToNeedDosageTimeElementStructure, MorningDosageTimeElementStructure, NoonDosageTimeElementStructure, EveningDosageTimeElementStructure or NightDosageTimeElementStructure");
			}
		}
	}	
	
	/**
	 * Validates rules for DosageDayElementStructure where DosageDayIdentifier is 0 
	 * - If the day number isn't specified only AccordingToNeedDosageTimeElementStructure elements are allowed for repeated dosages.
	 * @param root
	 * @throws XPathException
	 * @throws ValidationException if a business rule isn't met. 
	 */
	private static void validateForZeroDosageTimesIterationInterval(Node root) throws XPathException, DosageValidationException {
		Nodes dosageStructures = (Nodes)root.query("//*:DosageStructure");				
		for(int i=0; i<dosageStructures.size(); i++) {
			Node dosageStructure = dosageStructures.getNode(i);	
			Integer dosageTimesIterationInterval = (Integer)dosageStructure.query("//*:DosageTimesIterationIntervalQuantity/integer()");
			if(dosageTimesIterationInterval!=null&&dosageTimesIterationInterval.intValue()==1) {
				Nodes dosageDayElementStructures = (Nodes)dosageStructure.query("//*:DosageDayElementStructure");
				if(dosageDayElementStructures!=null&&dosageDayElementStructures.size()>0) {
					for(int j=0; j<dosageDayElementStructures.size(); j++) {
						Node dosageDayElementStructure = dosageDayElementStructures.getNode(j);
						if(dosageDayElementStructure.getChildNode(0).getInt()==0) {							
							for(int k=1; k<dosageDayElementStructure.countChildren(); k++) {
								Node n = dosageDayElementStructure.getChildNode(k);
								if(!n.getName().equals("AccordingToNeedDosageTimeElementStructure")) {
									throw new DosageValidationException("Element "+n.getNamespaceAndName()+" cannot occur in DosageDayElementStructure where DosageDayIdentifier is 0 if DosageTimesIterationIntervalQuantity is not 0. If the day number isn't specified only AccordingToNeedDosageTimeElementStructure elements are allowed for repeated dosages");
								}								
							}
						}
					}
				}
			}
		}
	}
	
	
}
