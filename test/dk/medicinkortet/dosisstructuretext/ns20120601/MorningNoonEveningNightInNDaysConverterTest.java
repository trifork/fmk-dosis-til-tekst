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

package dk.medicinkortet.dosisstructuretext.ns20120601;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

import dk.medicinkortet.dosisstructuretext.DailyDosisCalculator;
import dk.medicinkortet.dosisstructuretext.DosageType;
import dk.medicinkortet.dosisstructuretext.DosageTypeCalculator;
import dk.medicinkortet.dosisstructuretext.LongTextConverter;
import dk.medicinkortet.dosisstructuretext.ShortTextConverter;
import dk.medicinkortet.dosisstructuretext.longtextconverterimpl.DefaultLongTextConverterImpl;
import dk.medicinkortet.dosisstructuretext.shorttextconverterimpl.MorningNoonEveningNightInNDaysConverterImpl;
import dk.medicinkortet.dosisstructuretext.vowrapper.DateOrDateTimeWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.MorningDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructureWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuresWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.UnitOrUnitsWrapper;

/**
 * The purpose of this test class is to test new functionality added in FMK 1.4 (2012/06/01 namespace). 
 * The test of the general functionality is done in the testclass of the same name in the 
 * dk.medicinkortet.dosisstructuretext.ns2009 package. 
 */
public class MorningNoonEveningNightInNDaysConverterTest {
	
	@Test
	public void testUnits() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			StructuresWrapper.makeStructures(
				UnitOrUnitsWrapper.makeUnits("tablet", "tabletter"),
				StructureWrapper.makeStructure(
					0, "ved måltid", DateOrDateTimeWrapper.makeDate("2011-01-01"), DateOrDateTimeWrapper.makeDate("2011-01-05"), 
					DayWrapper.makeDay(
						1, 
						MorningDoseWrapper.makeDose(new BigDecimal(1), false)),
					DayWrapper.makeDay(
						2, 
						MorningDoseWrapper.makeDose(new BigDecimal(1), false)),
					DayWrapper.makeDay(
						3, 
						MorningDoseWrapper.makeDose(new BigDecimal(1), false)),
					DayWrapper.makeDay(
						4, 
						MorningDoseWrapper.makeDose(new BigDecimal(1), false)),
					DayWrapper.makeDay(
						5, 
						MorningDoseWrapper.makeDose(new BigDecimal(1), false)))));				
		Assert.assertEquals(
				"DefaultLongTextConverterImpl", 
				LongTextConverter.getConverterClassName(dosage));
		Assert.assertEquals(
				"Doseringsforløbet starter lørdag den 1. januar 2011, og ophører onsdag den 5. januar 2011:\n"+
				"   Doseringsforløb:\n"+
				"   Lørdag den 1. januar 2011: 1 tablet morgen ved måltid\n"+
				"   Søndag den 2. januar 2011: 1 tablet morgen ved måltid\n"+
				"   Mandag den 3. januar 2011: 1 tablet morgen ved måltid\n"+
				"   Tirsdag den 4. januar 2011: 1 tablet morgen ved måltid\n"+
				"   Onsdag den 5. januar 2011: 1 tablet morgen ved måltid",
				LongTextConverter.convert(dosage));
		Assert.assertEquals(
				"MorningNoonEveningNightInNDaysConverterImpl", 
				ShortTextConverter.getConverterClassName(dosage));
		Assert.assertEquals(
				"1 tablet morgen ved måltid i 5 dage", 
				ShortTextConverter.convert(dosage));
		Assert.assertEquals(
				1.0, 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001); 			
		Assert.assertEquals(DosageType.Temporary, DosageTypeCalculator.calculate(dosage));						
	}
	
	@Test
	public void testAccordingToNeed() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			StructuresWrapper.makeStructures(
				UnitOrUnitsWrapper.makeUnits("tablet", "tabletter"),
				StructureWrapper.makeStructure(
					0, "ved måltid", DateOrDateTimeWrapper.makeDate("2011-01-01"), DateOrDateTimeWrapper.makeDate("2011-01-05"), 
					DayWrapper.makeDay(
						1, 
						MorningDoseWrapper.makeDose(new BigDecimal(1), true)),
					DayWrapper.makeDay(
						2, 
						MorningDoseWrapper.makeDose(new BigDecimal(1), true)),
					DayWrapper.makeDay(
						3, 
						MorningDoseWrapper.makeDose(new BigDecimal(1), true)),
					DayWrapper.makeDay(
						4, 
						MorningDoseWrapper.makeDose(new BigDecimal(1), true)),
					DayWrapper.makeDay(
						5, 
						MorningDoseWrapper.makeDose(new BigDecimal(1), true)))));				
		Assert.assertEquals(
				"DefaultLongTextConverterImpl", 
				LongTextConverter.getConverterClassName(dosage));
		Assert.assertEquals(
				"Doseringsforløbet starter lørdag den 1. januar 2011, og ophører onsdag den 5. januar 2011:\n"+
				"   Doseringsforløb:\n"+
				"   Lørdag den 1. januar 2011: 1 tablet morgen efter behov højst 1 gang ved måltid\n"+
				"   Søndag den 2. januar 2011: 1 tablet morgen efter behov højst 1 gang ved måltid\n"+
				"   Mandag den 3. januar 2011: 1 tablet morgen efter behov højst 1 gang ved måltid\n"+
				"   Tirsdag den 4. januar 2011: 1 tablet morgen efter behov højst 1 gang ved måltid\n"+
				"   Onsdag den 5. januar 2011: 1 tablet morgen efter behov højst 1 gang ved måltid",
				LongTextConverter.convert(dosage));
		Assert.assertEquals(
				"MorningNoonEveningNightInNDaysConverterImpl", 
				ShortTextConverter.getConverterClassName(dosage));
		Assert.assertEquals(
				"1 tablet morgen efter behov ved måltid i 5 dage", 
				ShortTextConverter.convert(dosage));
		Assert.assertNull(DailyDosisCalculator.calculate(dosage).getValue());
		Assert.assertEquals(DosageType.AccordingToNeed, DosageTypeCalculator.calculate(dosage));						
	}
		
}
