package dk.medicinkortet.dosisstructuretext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import dk.medicinkortet.dosisstructuretext.debug.Debug;
import dk.medicinkortet.dosisstructuretext.simpelxml.Node;
import dk.medicinkortet.dosisstructuretext.simpelxml.Nodes;

/**
 * Contains static methods for determining the version number (actually a string) of the dosage XML document. 
 * Additionally a method and helper methods for mapping version 1.0 to 1.1
 * <p>
 * If the version of the documents are known to be 1.1, as it should be in a production system, the class is not needed. 
 * Therefore: <b>This class should only be used in the test- and development phase!</b> 
 */
public class VersionHelper {
		
	private static Debug debug = new Debug(true); 
	
	/**
	 * Determines the version of the document based on the attributes of the root element. 
	 * @param root root node of the XML document to determine the version for
	 * @param result "1.0" or "1.1"
	 */
	public static void guessVersion(Node root, DosisStructureText result) {
		String schemaLocation = root.findAttribute("xsi:schemaLocation");
		if(schemaLocation==null)
			result.setVersion("1.1"); //throw new RuntimeException("Could not determine version: No schema location found");
		else if(schemaLocation.indexOf("/schemas-1.1/")>=0)
			result.setVersion("1.1");
		else if(schemaLocation.indexOf("/2008/06/01")>=0)
			result.setVersion("1.1");
		else if(schemaLocation.indexOf("/schemas/")>=0)
			result.setVersion("1.0");
		else if(schemaLocation.indexOf("/2008.01.01")>=0)
			result.setVersion("1.0");
		else
			throw new RuntimeException("Could not determine version: Unexpected content of xsi:schemaLocation");
	}
	
	/**
	 * Converts the called document of version 1.0 to version 1.1
	 * @param node node to convert
	 */
	public static void convert10To11(Node node) {
		convertSchemaLocation(node);		
		if(node.findChildNodeExists("*", "DosageTimesStructure")) {
			removeDosageType(node);
			moveDosageQuantityUnit(node);
			groupDosageTimeElementStructures(node);
		}
	}
	
	/**
	 * Converts the schema location
	 * @param node
	 */		
	private static void convertSchemaLocation(Node node) {
		String schemaLocation = node.getAttribute("xsi:schemaLocation");
		int i = schemaLocation.indexOf("/schemas/");
		if(i<0) {
			i = schemaLocation.indexOf("http://www.dkma.dk/medicinecard/xml.schema/2008.01.01 DKMA_CreateDrugMedicationRequestStructure.xsd");
			if(i==0) {
				// We could change the schema location here, but it doesn't really matter for the dosage-to-text functionality
				// as the document isn't validated
				return; 
			}
		}		
		if(i<0) { 
			debug.println("VersionHelper.convertSchemaLocation: Cannot find schema location to convert");
			return;
		}
		schemaLocation = schemaLocation.substring(0, i) + "/../schemas-1.1/" + schemaLocation.substring(i+9);		
		node.setAttribute("xsi:schemaLocation", schemaLocation);
	}

	/**
	 * The dosage type isn't used anymore, just remove it
	 * @param node
	 */
	private static void removeDosageType(Node node) {
		Node dosageStructure = node.findChildNode("*", "DosageStructure"); 
		Node dosageTimesStructure = dosageStructure.getChildNode("*", "DosageTimesStructure");
		dosageTimesStructure.removeChildNodes("*", "DosageType");
	}
	
	/**
	 * The quantity unit is moved from the DosageTimeElementStructure to the DosageTimesStructure. If varying units are used 
	 * in version 1.0 a RuntimeException is thrown. 
	 * @param node
	 */
	private static void moveDosageQuantityUnit(Node node) {
		Node dosageStructure = node.findChildNode("*", "DosageStructure"); 
		Node dosageTimesStructure = dosageStructure.getChildNode("*", "DosageTimesStructure");
		Nodes dosageTimeElementStructures = dosageTimesStructure.getChildNodes("*", "DosageTimeElementStructure");
		String unit = null;
		for(int i=0; i<dosageTimeElementStructures.size(); i++) {
			Node dosageTimeElementStructure = dosageTimeElementStructures.getNode(i);
			Node dosageQuantityStructure = dosageTimeElementStructure.getChildNode("*", "DosageQuantityStructure");
			Node dosageQuantityUnitText = dosageQuantityStructure.getChildNode("*", "DosageQuantityUnitText");
			if(unit==null) {
				unit = dosageQuantityUnitText.getText();
			}
			else {
				if(!unit.equals(dosageQuantityUnitText.getText())) {
					throw new RuntimeException("Cannot convert version 1.0 to 1.1: Different units found: "+unit+" and "+dosageQuantityUnitText.getText());
				}
			}				
			dosageQuantityStructure.removeChildNode(dosageQuantityUnitText);
		}
		Node before = null;
		if(dosageTimesStructure.hasChildNode("*", "DosageTimesEndDate")) 
			before = dosageTimesStructure.getChildNode("*", "DosageTimesEndDate");
		else 
			before = dosageTimesStructure.getChildNode("*", "DosageTimesStartDate");
		Node dosageQuantityUnitText = new Node(before.getNamespace(), "DosageQuantityUnitText", null, unit, null);
		dosageTimesStructure.addChildNodeAfter(before, dosageQuantityUnitText);		
	}
	
	/**
	 * in version 1.1 DosageTimeElementStructure are grouped under one or more DosageDayElementStructure, according to the day defined
	 * in the DosageTimeElementStructure. The DosageTimeDayIdentifier in the DosageTimeElementStructure is removed, this information 
	 * is now found under the DosageDayElementStructure node. 
	 * @param node
	 */
	private static void groupDosageTimeElementStructures(Node node) {
		Node dosageStructure = node.findChildNode("*", "DosageStructure"); 
		Node dosageTimesStructure = dosageStructure.getChildNode("*", "DosageTimesStructure");
		Nodes dosageTimeElementStructures = dosageTimesStructure.getChildNodes("*", "DosageTimeElementStructure");
		HashMap<String, ArrayList<Node>> dayMap = new HashMap<String, ArrayList<Node>>();
		for(int i=0; i<dosageTimeElementStructures.size(); i++) {
			Node dosageTimeElementStructure = dosageTimeElementStructures.getNode(i);
			Node dosageTimeDayIdentifier = dosageTimeElementStructure.getChildNode("*", "DosageTimeDayIdentifier");
			String day = dosageTimeDayIdentifier.getText();
			dosageTimeElementStructure.removeChildNode(dosageTimeDayIdentifier);
			if(!dayMap.containsKey(day)) {
				ArrayList<Node> dayList = new ArrayList<Node>();
				dayList.add(dosageTimeElementStructure);
				dayMap.put(day, dayList);
			}
			else {
				ArrayList<Node> dayList = dayMap.get(day);
				dayList.add(dosageTimeElementStructure);
			}
		}
		ArrayList<String> sortedDayList = new ArrayList<String>(dayMap.keySet());
		Collections.sort(sortedDayList);
		Node before = dosageTimesStructure.findChildNode("*", "DosageQuantityUnitText");
		for(int i=0; i<sortedDayList.size(); i++) {
			String day = sortedDayList.get(i);			
			Node dosageDayIdentifier = new Node(dosageTimesStructure.getNamespace(), "DosageDayIdentifier", null, day, null);
			Nodes dosageDayIdentifiers = new Nodes();
			dosageDayIdentifiers.addNode(dosageDayIdentifier);
			Node dosageDayElementStructure = new Node(dosageTimesStructure.getNamespace(), "DosageDayElementStructure", null, null, dosageDayIdentifiers);
			dosageTimesStructure.addChildNodeAfter(before, dosageDayElementStructure);
			before = dosageDayElementStructure;
			ArrayList<Node> dayList = dayMap.get(day);
			for(int j=0; j<dayList.size(); j++) {
				Node dosageTimeElementStructure = dayList.get(j);
				dosageTimesStructure.removeChildNode(dosageTimeElementStructure);
				dosageDayElementStructure.addChildNode(dosageTimeElementStructure);
			}
		}	
	}

}
