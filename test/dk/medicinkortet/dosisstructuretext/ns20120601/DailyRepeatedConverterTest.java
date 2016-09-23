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
import dk.medicinkortet.dosisstructuretext.LocalTime;
import dk.medicinkortet.dosisstructuretext.LongTextConverter;
import dk.medicinkortet.dosisstructuretext.ShortTextConverter;
import dk.medicinkortet.dosisstructuretext.longtextconverterimpl.DailyRepeatedConverterImpl;
import dk.medicinkortet.dosisstructuretext.shorttextconverterimpl.ParacetamolConverterImpl;
import dk.medicinkortet.dosisstructuretext.vowrapper.DateOrDateTimeWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.PlainDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructureWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuresWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.TimedDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.UnitOrUnitsWrapper;

/**
 * The purpose of this test class is to test new functionality added in FMK 1.4 (2012/06/01 namespace). 
 * The test of the general functionality is done in the testclass of the same name in the 
 * dk.medicinkortet.dosisstructuretext.ns2009 package. 
 */
public class DailyRepeatedConverterTest {

	@Test
	public void testUnits() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			StructuresWrapper.makeStructures(
				UnitOrUnitsWrapper.makeUnits("tablet", "tabletter"), 
				StructureWrapper.makeStructure(
					1, 
					"ved måltid", 
					DateOrDateTimeWrapper.makeDate("2011-01-01"), DateOrDateTimeWrapper.makeDate("2011-01-30"), 
					DayWrapper.makeDay(
						1, 
						PlainDoseWrapper.makeDose(new BigDecimal(1)), 
						PlainDoseWrapper.makeDose(new BigDecimal(1)), 
						PlainDoseWrapper.makeDose(new BigDecimal(1), true)))));
		Assert.assertEquals(
				"DailyRepeatedConverterImpl", 
				LongTextConverter.getConverterClassName(dosage));
		Assert.assertEquals(
				"Doseringsforløbet starter lørdag den 1. januar 2011, gentages hver dag, og ophører søndag den 30. januar 2011:\n"+
				"   Doseringsforløb:\n"+
				"   1 tablet ved måltid + 1 tablet ved måltid + 1 tablet efter behov ved måltid",
				LongTextConverter.convert(dosage));
		Assert.assertEquals(
				"ParacetamolConverterImpl", 
				ShortTextConverter.getConverterClassName(dosage));
		Assert.assertEquals(
				"1 tablet 2-3 gange daglig ved måltid", 
				ShortTextConverter.convert(dosage));
		Assert.assertNull(DailyDosisCalculator.calculate(dosage).getValue());
		Assert.assertEquals(DosageType.Combined, DosageTypeCalculator.calculate(dosage));
	}
	
	@Test
	public void testAccordingToNeed() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			StructuresWrapper.makeStructures(
				UnitOrUnitsWrapper.makeUnits("tablet", "tabletter"), 
				StructureWrapper.makeStructure(
					1, 
					"ved måltid", 
					DateOrDateTimeWrapper.makeDate("2011-01-01"), DateOrDateTimeWrapper.makeDate("2011-01-30"), 
					DayWrapper.makeDay(
						1, 
						PlainDoseWrapper.makeDose(new BigDecimal(1), false), 
						PlainDoseWrapper.makeDose(new BigDecimal(1), false), 
						PlainDoseWrapper.makeDose(new BigDecimal(1), true)))));
		Assert.assertEquals(
				"DailyRepeatedConverterImpl", 
				LongTextConverter.getConverterClassName(dosage));
		Assert.assertEquals(
				"Doseringsforløbet starter lørdag den 1. januar 2011, gentages hver dag, og ophører søndag den 30. januar 2011:\n"+
				"   Doseringsforløb:\n"+
				"   1 tablet ved måltid + 1 tablet ved måltid + 1 tablet efter behov ved måltid",
				LongTextConverter.convert(dosage));
		Assert.assertEquals(
				"ParacetamolConverterImpl", 
				ShortTextConverter.getConverterClassName(dosage));
		Assert.assertEquals(
				"1 tablet 2-3 gange daglig ved måltid", 
				ShortTextConverter.convert(dosage));
		Assert.assertNull(DailyDosisCalculator.calculate(dosage).getValue());
		Assert.assertEquals(DosageType.Combined, DosageTypeCalculator.calculate(dosage));
	}
	
	/* FMK-2444 Combined TimeDose/PlainDose was mistakenly converted by ParacetamolConverter */
	@Test
	public void testCombinedTimePlain() {
		
		DosageWrapper dosage = DosageWrapper.makeDosage(
			StructuresWrapper.makeStructures(
				UnitOrUnitsWrapper.makeUnits("tablet", "tabletter"), 
				StructureWrapper.makeStructure(
					1, 
					null, 
					DateOrDateTimeWrapper.makeDate("2011-01-01"), null, //DateOrDateTimeWrapper.makeDate("2011-01-01"), 
					DayWrapper.makeDay(
						1, 
						TimedDoseWrapper.makeDose(new LocalTime(8,00), new BigDecimal(1), false), 
						TimedDoseWrapper.makeDose(new LocalTime(12,00), new BigDecimal(1), false), 
						TimedDoseWrapper.makeDose(new LocalTime(16,00), new BigDecimal(1), false), 
						TimedDoseWrapper.makeDose(new LocalTime(20,00), new BigDecimal(1), false), 
						PlainDoseWrapper.makeDose(new BigDecimal(1), true),
						PlainDoseWrapper.makeDose(new BigDecimal(1), true),
						PlainDoseWrapper.makeDose(new BigDecimal(1), true)
					)
				)
			)
		);

		Assert.assertEquals(
				"Doseringsforløbet starter lørdag den 1. januar 2011 og gentages hver dag:\n" + 
				"   Doseringsforløb:\n" + 
				"   1 tablet kl. 08:00 + 1 tablet kl. 12:00 + 1 tablet kl. 16:00 + 1 tablet kl. 20:00 + 1 tablet efter behov + 1 tablet efter behov + 1 tablet efter behov",
				LongTextConverter.convert(dosage));

		Assert.assertEquals(
			"DailyRepeatedConverterImpl", 
			LongTextConverter.getConverterClassName(dosage));
		String shorttext= ShortTextConverter.convert(dosage);
		Assert.assertNull(ShortTextConverter.convert(dosage, 1000));	/* no known converter */
		Assert.assertNull(ShortTextConverter.getConverterClassName(dosage)); /* no known converter */
		Assert.assertNull(DailyDosisCalculator.calculate(dosage).getValue());
		Assert.assertEquals(DosageType.Combined, DosageTypeCalculator.calculate(dosage));
	}
}
