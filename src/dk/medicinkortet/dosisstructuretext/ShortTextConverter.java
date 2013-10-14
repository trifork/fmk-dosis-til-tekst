/**
* The contents of this file are subject to the Mozilla Public
* License Version 1.1 (the "License"); you may not use this file
* except in compliance with the License. You may obtain a copy of
* the License at http://www.mozilla.org/MPL/
*
* Software distributed under the License is distributed on an "AS
* IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
* implied. See the License for the specific language governing
* rights and limitations under the License.
*
* Contributor(s): Contributors are attributed in the source code
* where applicable.
*
* The Original Code is "Dosis-til-tekst".
*
* The Initial Developer of the Original Code is Trifork Public A/S.
*
* Portions created for the FMK Project are Copyright 2011,
* National Board of e-Health (NSI). All Rights Reserved.
*/

package dk.medicinkortet.dosisstructuretext;

import java.util.ArrayList;

import dk.medicinkortet.dosisstructuretext.shorttextconverterimpl.AdministrationAccordingToSchemaConverterImpl;
import dk.medicinkortet.dosisstructuretext.shorttextconverterimpl.CombinedTwoPeriodesConverterImpl;
import dk.medicinkortet.dosisstructuretext.shorttextconverterimpl.FreeTextConverterImpl;
import dk.medicinkortet.dosisstructuretext.shorttextconverterimpl.LimitedNumberOfDaysConverterImpl;
import dk.medicinkortet.dosisstructuretext.shorttextconverterimpl.MorningNoonEveningNightAndAccordingToNeedConverterImpl;
import dk.medicinkortet.dosisstructuretext.shorttextconverterimpl.MorningNoonEveningNightConverterImpl;
import dk.medicinkortet.dosisstructuretext.shorttextconverterimpl.MorningNoonEveningNightInNDaysConverterImp;
import dk.medicinkortet.dosisstructuretext.shorttextconverterimpl.MultipleDaysNonRepeatedConverterImpl;
import dk.medicinkortet.dosisstructuretext.shorttextconverterimpl.NumberOfWholeWeeksConverterImpl;
import dk.medicinkortet.dosisstructuretext.shorttextconverterimpl.ParacetamolConverterImpl;
import dk.medicinkortet.dosisstructuretext.shorttextconverterimpl.RepeatedConverterImpl;
import dk.medicinkortet.dosisstructuretext.shorttextconverterimpl.ShortTextConverterImpl;
import dk.medicinkortet.dosisstructuretext.shorttextconverterimpl.SimpleAccordingToNeedConverterImpl;
import dk.medicinkortet.dosisstructuretext.shorttextconverterimpl.SimpleLimitedAccordingToNeedConverterImpl;
import dk.medicinkortet.dosisstructuretext.shorttextconverterimpl.SimpleNonRepeatedConverterImpl;
import dk.medicinkortet.dosisstructuretext.shorttextconverterimpl.WeeklyMorningNoonEveningNightConverterImpl;
import dk.medicinkortet.dosisstructuretext.shorttextconverterimpl.WeeklyRepeatedConverterImpl;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;

/**
 * Converts dosage to short text. This is only possible for a limited number of dosages, as the result must not exceed 70 
 * characters. 
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
		converters.add(new MorningNoonEveningNightInNDaysConverterImp());
		converters.add(new SimpleAccordingToNeedConverterImpl());
		converters.add(new LimitedNumberOfDaysConverterImpl());
		converters.add(new SimpleLimitedAccordingToNeedConverterImpl());
		converters.add(new WeeklyMorningNoonEveningNightConverterImpl());
		converters.add(new WeeklyRepeatedConverterImpl());
		converters.add(new ParacetamolConverterImpl());
		converters.add(new MorningNoonEveningNightAndAccordingToNeedConverterImpl());
		converters.add(new MultipleDaysNonRepeatedConverterImpl());
		converters.add(new NumberOfWholeWeeksConverterImpl());
		// Converters for more than one periode:
		converters.add(new CombinedTwoPeriodesConverterImpl()); 
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
	
	public static boolean canConvert(DosageWrapper dosage) {
		for(ShortTextConverterImpl converter: converters) {
			if(converter.canConvert(dosage))
				return true;
		}
		return false;
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
