package dk.medicinkortet.dosisstructuretext.shorttext;

import java.util.ArrayList;

import dk.medicinkortet.dosisstructuretext.DosisStructureText;
import dk.medicinkortet.dosisstructuretext.shorttext.converterimpl.ConverterImpl;
import dk.medicinkortet.dosisstructuretext.shorttext.converterimpl.LimitedNumberOfDaysConverterImpl;
import dk.medicinkortet.dosisstructuretext.shorttext.converterimpl.LimitedNumberOfDaysWithAccordingToNeedConverterImpl;
import dk.medicinkortet.dosisstructuretext.shorttext.converterimpl.ParacetamolConverterImpl;
import dk.medicinkortet.dosisstructuretext.shorttext.converterimpl.RepeatedConverterImpl;
import dk.medicinkortet.dosisstructuretext.shorttext.converterimpl.SimpleMorningNoonEveningNightConverterImpl;
import dk.medicinkortet.dosisstructuretext.shorttext.converterimpl.MorningNoonEveningNightConverterImpl;
import dk.medicinkortet.dosisstructuretext.shorttext.converterimpl.SimpleAccordingToNeedConverterImpl;
import dk.medicinkortet.dosisstructuretext.shorttext.converterimpl.SimpleLimitedAccordingToNeedConverterImpl;
import dk.medicinkortet.dosisstructuretext.shorttext.converterimpl.SimpleNonRepeatedConverterImpl;
import dk.medicinkortet.dosisstructuretext.simpelxml.Node;
import dk.medicinkortet.dosisstructuretext.simpelxml.Nodes;
import dk.medicinkortet.dosisstructuretext.simpelxml.parser.XPathException;

/**
 * Converts dosage to short text. This is only possible for a limited number of cases. 
 */
public class ShortTextConverter {

	private static ArrayList<ConverterImpl> converterImpls = new ArrayList<ConverterImpl>();
	 
	// Consider the order: The tests are evaluated in order, adding the most likely to succeed
	// first improves performance
	static {
		converterImpls.add(new RepeatedConverterImpl());		
		converterImpls.add(new SimpleNonRepeatedConverterImpl());
		converterImpls.add(new SimpleMorningNoonEveningNightConverterImpl());
		converterImpls.add(new MorningNoonEveningNightConverterImpl());
		converterImpls.add(new SimpleAccordingToNeedConverterImpl());
		converterImpls.add(new LimitedNumberOfDaysConverterImpl());
		converterImpls.add(new SimpleLimitedAccordingToNeedConverterImpl());
		converterImpls.add(new LimitedNumberOfDaysWithAccordingToNeedConverterImpl());
		converterImpls.add(new ParacetamolConverterImpl());		
	}
	

	/**
	 * Converts the dosage to short text, if possible
	 * @param root Node containing either a DosageMappingStructure or a DosageStructure
	 * @return result result object with short text
	 * @throws XPathException
	 */
	public static DosisStructureText make(Node root) throws XPathException {
		return make(root, new DosisStructureText());
	}
	
	/**
	 * Converts the dosage to short text, if possible
	 * @param root Node containing either a DosageMappingStructure or a DosageStructure
	 * @param result the passed result object is updated with the long text
	 * @return result the passed result object is updated with short text
	 * @throws XPathException
	 */
	public static DosisStructureText make(Node root, DosisStructureText result) throws XPathException {
		makeDosageMapping(root, result);
		if(root.query("//*:DosageTimesStructure")!=null) {
			makeFromDosageTimesStructure(root, result);
		}
		else if(root.query("//*:AdministrationAccordingToSchemeInLocalSystemIndicator")!=null) {
			result.setShortTextFilter("Tag");
			result.setShortText("Dosering efter skema i lokalt system");
		}
		else { 
			result.setShortTextFilter("FreeText");
			result.setShortText((String)root.query("//*:DosageFreeText/text()"));
		}
		return result;
	}

	/**
	 * If the passed node contains a DosageMappingStructure this adds the PriceListDosageCode
	 * and PriceListDosageText to the result. 
	 * @param root
	 * @param result
	 * @throws XPathExceptionmakeDosageMapping
	 */
	private static void makeDosageMapping(Node root, DosisStructureText result) throws XPathException {		
		result.setPriceListDosageCode((String)root.query("//*:PriceListDosageCode/text()"));
		result.setPriceListDosageText((String)root.query("//*:PriceListDosageText/text()"));
	}
	
	/**
	 * Calls the converters one by one to perform the conversion. 
	 * @param root
	 * @param result
	 * @throws XPathException
	 */
	private static void makeFromDosageTimesStructure(Node root, DosisStructureText result) throws XPathException {
		Nodes dosageTimesStructures = (Nodes)root.query("//*:DosageTimesStructure");
		if(dosageTimesStructures.size()!=1)
			throw new RuntimeException("Only one DosageTimesStructure expected, got "+dosageTimesStructures.size());
		Node dosageTimesStructure = dosageTimesStructures.getNode(0);
		// The first converter that can do the conversion will update the result and return true.  
		for(int converterNo=0; converterNo<converterImpls.size(); converterNo++) {
			if(converterImpls.get(converterNo).convert(dosageTimesStructure, result))
				return;
		}		
	}
	
}
