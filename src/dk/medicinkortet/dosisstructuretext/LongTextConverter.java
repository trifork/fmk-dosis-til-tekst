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

import dk.medicinkortet.dosisstructuretext.longtextconverterimpl.AdministrationAccordingToSchemaConverterImpl;
import dk.medicinkortet.dosisstructuretext.longtextconverterimpl.DailyRepeatedConverterImpl;
import dk.medicinkortet.dosisstructuretext.longtextconverterimpl.DefaultLongTextConverterImpl;
import dk.medicinkortet.dosisstructuretext.longtextconverterimpl.DefaultMultiPeriodeLongTextConverterImpl;
import dk.medicinkortet.dosisstructuretext.longtextconverterimpl.EmptyStructureConverterImpl;
import dk.medicinkortet.dosisstructuretext.longtextconverterimpl.FreeTextConverterImpl;
import dk.medicinkortet.dosisstructuretext.longtextconverterimpl.LongTextConverterImpl;
import dk.medicinkortet.dosisstructuretext.longtextconverterimpl.TwoDaysRepeatedConverterImpl;
import dk.medicinkortet.dosisstructuretext.longtextconverterimpl.WeeklyRepeatedConverterImpl;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;

/**
 * Converts dosage to long text. This must always be possible, DefaultLongTextConverterImpl ensures that
 */ 
public class LongTextConverter {

	private static ArrayList<LongTextConverterImpl> converters = new ArrayList<LongTextConverterImpl>();
	
	/**
	 * Populate a list of implemented converters 
	 * Consider the order: The tests are evaluated in order. DefaultLongTextConverterImpl is added 
	 * last, as it accepts all types of dosages.
	 */
	static {
		converters.add(new AdministrationAccordingToSchemaConverterImpl());
		converters.add(new FreeTextConverterImpl());
		converters.add(new EmptyStructureConverterImpl());
		converters.add(new DailyRepeatedConverterImpl());		
		converters.add(new TwoDaysRepeatedConverterImpl());		
		converters.add(new WeeklyRepeatedConverterImpl());		
		converters.add(new DefaultLongTextConverterImpl());
		converters.add(new DefaultMultiPeriodeLongTextConverterImpl());
	}

	/**
	 * Performs a conversion to a long text. 
	 * @param dosage
	 * @return A long text string describing the dosage 
	 */
	public static String convert(DosageWrapper dosage) {
		for(LongTextConverterImpl converter: converters) {
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
	private static Class<? extends LongTextConverterImpl> getConverterClass(DosageWrapper dosage) {
		for(LongTextConverterImpl converter: converters) {
			if(converter.canConvert(dosage))
				return converter.getClass();
		}
		return null;
	}
	
	public static String getConverterClassName(DosageWrapper dosage) {
		return getConverterClass(dosage).getSimpleName();
	}
}
