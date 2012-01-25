package dk.medicinkortet.dosisstructuretext;

import java.util.ArrayList;

import dk.medicinkortet.dosisstructuretext.converterimpl.AdministrationAccordingToSchemaConverterImpl;
import dk.medicinkortet.dosisstructuretext.converterimpl.FreeTextConverterImpl;
import dk.medicinkortet.dosisstructuretext.converterimpl.LimitedNumberOfDaysConverterImpl;
import dk.medicinkortet.dosisstructuretext.converterimpl.LimitedNumberOfDaysWithAccordingToNeedConverterImpl;
import dk.medicinkortet.dosisstructuretext.converterimpl.MorningNoonEveningNightConverterImpl;
import dk.medicinkortet.dosisstructuretext.converterimpl.ParacetamolConverterImpl;
import dk.medicinkortet.dosisstructuretext.converterimpl.RepeatedConverterImpl;
import dk.medicinkortet.dosisstructuretext.converterimpl.ShortTextConverterImpl;
import dk.medicinkortet.dosisstructuretext.converterimpl.SimpleAccordingToNeedConverterImpl;
import dk.medicinkortet.dosisstructuretext.converterimpl.SimpleLimitedAccordingToNeedConverterImpl;
import dk.medicinkortet.dosisstructuretext.converterimpl.SimpleNonRepeatedConverterImpl;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;

/**
 * Converts dosage to short text. This is only possible for a limited number of dosages. Currently 
 * dosages in the 2008/06/01 and 2009/01/01 namespaces are supported.  
 */ 
public class ShortTextConverter {

	private static ArrayList<ShortTextConverterImpl> converters = new ArrayList<ShortTextConverterImpl>();
	 
	/**
	 * Populate a list of implemented converters 
	 * Consider the order: The tests are evaluated in order, adding the most likely to succeed
	 * first improves performance
	 */
	static {
		converters.add(new AdministrationAccordingToSchemaConverterImpl());
		converters.add(new FreeTextConverterImpl());
		converters.add(new MorningNoonEveningNightConverterImpl());
		converters.add(new RepeatedConverterImpl());		
		converters.add(new SimpleNonRepeatedConverterImpl());
		converters.add(new SimpleAccordingToNeedConverterImpl());
		converters.add(new LimitedNumberOfDaysConverterImpl());
		converters.add(new SimpleLimitedAccordingToNeedConverterImpl());
		converters.add(new LimitedNumberOfDaysWithAccordingToNeedConverterImpl());
		converters.add(new ParacetamolConverterImpl());		
	}
	
	/**
	 * Performs a conversion to a short text if possible. Note that the length of the 
	 * "short text" might be longer than the XML schema allows for this text. This must 
	 * be checked by the caller, as we don't want the implementation to depend on 
	 * what context it is used in. 
	 * @param dosage
	 * @return A short text string describing the dosage 
	 */
	public static String convert(DosageWrapper dosage) {
		for(ShortTextConverterImpl converter: converters) {
			if(converter.canConvert(dosage))
				return converter.doConvert(dosage);
		}
		return null;
	}

	/**
	 * This method returns the converter class handing the conversion to a short, if
	 * the dosage can be converted. The metod is useful for test, logging etc. 
	 * @param dosage
	 * @return The converter class 
	 */
	public static Class<? extends ShortTextConverterImpl> getConverterClass(DosageWrapper dosage) {
		for(ShortTextConverterImpl converter: converters) {
			if(converter.canConvert(dosage))
				return converter.getClass();
		}
		return null;
	}
	
}
