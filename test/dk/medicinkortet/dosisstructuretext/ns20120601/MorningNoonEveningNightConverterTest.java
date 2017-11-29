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
import dk.medicinkortet.dosisstructuretext.longtextconverterimpl.DailyRepeatedConverterImpl;
import dk.medicinkortet.dosisstructuretext.shorttextconverterimpl.MorningNoonEveningNightConverterImpl;
import dk.medicinkortet.dosisstructuretext.vowrapper.DateOrDateTimeWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.EveningDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.MorningDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.NightDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.NoonDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructureWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuresWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.UnitOrUnitsWrapper;

/**
 * The purpose of this test class is to test new functionality added in FMK 1.4 (2012/06/01 namespace). 
 * The test of the general functionality is done in the testclass of the same name in the 
 * dk.medicinkortet.dosisstructuretext.ns2009 package. 
 */
public class MorningNoonEveningNightConverterTest {
	
	@Test
	public void testUnits() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			StructuresWrapper.makeStructures(
				UnitOrUnitsWrapper.makeUnits("tablet", "tabletter"), 
				StructureWrapper.makeStructure(
					1, "ved måltid", DateOrDateTimeWrapper.makeDate("2011-01-01"), DateOrDateTimeWrapper.makeDate("2011-01-30"), 
					DayWrapper.makeDay(
						1, 
						MorningDoseWrapper.makeDose(new BigDecimal(1)), 
						EveningDoseWrapper.makeDose(new BigDecimal(2)))))); 
		Assert.assertEquals(
				"DailyRepeatedConverterImpl", 
				LongTextConverter.getConverterClassName(dosage));
		Assert.assertEquals(
			"Doseringsforløbet starter lørdag den 1. januar 2011, gentages hver dag, og ophører søndag den 30. januar 2011:\n"+
			"   Doseringsforløb:\n"+
			"   1 tablet morgen ved måltid + 2 tabletter aften ved måltid",
			LongTextConverter.convert(dosage));
		Assert.assertEquals(
			"MorningNoonEveningNightConverterImpl", 
			ShortTextConverter.getConverterClassName(dosage));
		Assert.assertEquals(
			"1 tablet morgen og 2 tabletter aften ved måltid", 
			ShortTextConverter.convert(dosage));
		Assert.assertEquals(
				3.0, 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001); 			
		Assert.assertEquals(DosageType.Temporary, DosageTypeCalculator.calculate(dosage));		
	}
	
	@Test
	public void testWhereEquals() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			StructuresWrapper.makeStructures(
				UnitOrUnitsWrapper.makeUnits("tablet", "tabletter"), 
				StructureWrapper.makeStructure(
					1, "ved måltid", DateOrDateTimeWrapper.makeDate("2011-01-01"), DateOrDateTimeWrapper.makeDate("2011-01-30"), 
					DayWrapper.makeDay(
						1, 
						MorningDoseWrapper.makeDose(new BigDecimal(2)), 
						EveningDoseWrapper.makeDose(new BigDecimal(2)))))); 
		Assert.assertEquals(
				"DailyRepeatedConverterImpl", 
				LongTextConverter.getConverterClassName(dosage));
		Assert.assertEquals(
			"Doseringsforløbet starter lørdag den 1. januar 2011, gentages hver dag, og ophører søndag den 30. januar 2011:\n"+
			"   Doseringsforløb:\n"+
			"   2 tabletter morgen ved måltid + 2 tabletter aften ved måltid",
			LongTextConverter.convert(dosage));
		Assert.assertEquals(
			"MorningNoonEveningNightConverterImpl", 
			ShortTextConverter.getConverterClassName(dosage));
		Assert.assertEquals(
			"2 tabletter morgen og aften ved måltid", 
			ShortTextConverter.convert(dosage));
		Assert.assertEquals(
				4.0, 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001); 			
		Assert.assertEquals(DosageType.Temporary, DosageTypeCalculator.calculate(dosage));		
	}
	
	@Test
	public void testTooLong() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			StructuresWrapper.makeStructures(
				UnitOrUnitsWrapper.makeUnits("tablet", "tabletter"), 
				StructureWrapper.makeStructure(
					1, "ved måltid og hvornår man ellers skulle føle trang", DateOrDateTimeWrapper.makeDate("2011-01-01"), DateOrDateTimeWrapper.makeDate("2011-01-30"), 
					DayWrapper.makeDay(
						1, 
						MorningDoseWrapper.makeDose(new BigDecimal(1.5)), 
						NoonDoseWrapper.makeDose(new BigDecimal(2.5)),
						EveningDoseWrapper.makeDose(new BigDecimal(3.5)),
						NightDoseWrapper.makeDose(new BigDecimal(4.5)))))); 
		Assert.assertEquals(
				"DailyRepeatedConverterImpl", 
				LongTextConverter.getConverterClassName(dosage));
		Assert.assertEquals(
			"Doseringsforløbet starter lørdag den 1. januar 2011, gentages hver dag, og ophører søndag den 30. januar 2011:\n"+
			"   Doseringsforløb:\n"+
			"   1,5 tabletter morgen ved måltid og hvornår man ellers skulle føle trang + 2,5 tabletter middag ved måltid og hvornår man ellers skulle føle trang + 3,5 tabletter aften ved måltid og hvornår man ellers skulle føle trang + 4,5 tabletter nat ved måltid og hvornår man ellers skulle føle trang",
			LongTextConverter.convert(dosage));
		Assert.assertNull(ShortTextConverter.getConverterClassName(dosage));
		Assert.assertNull(ShortTextConverter.convert(dosage));
		Assert.assertEquals(
				12.0, 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001); 			
		Assert.assertEquals(DosageType.Temporary, DosageTypeCalculator.calculate(dosage));		
	}
	
	@Test
	public void testAccordingToNeed() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			StructuresWrapper.makeStructures(
				UnitOrUnitsWrapper.makeUnits("mg", "mg"),  
				StructureWrapper.makeStructure(
					1, "ved måltid", DateOrDateTimeWrapper.makeDate("2011-01-01"), DateOrDateTimeWrapper.makeDate("2011-01-30"), 
					DayWrapper.makeDay(
						1, 
						MorningDoseWrapper.makeDose(new BigDecimal(1), true), 
						EveningDoseWrapper.makeDose(new BigDecimal(2), true))))); 
		Assert.assertEquals(
				"DailyRepeatedConverterImpl", 
				LongTextConverter.getConverterClassName(dosage));
		Assert.assertEquals(
			"Doseringsforløbet starter lørdag den 1. januar 2011, gentages hver dag, og ophører søndag den 30. januar 2011:\n"+
			"   Doseringsforløb:\n"+
			"   1 mg morgen efter behov ved måltid + 2 mg aften efter behov ved måltid",
			LongTextConverter.convert(dosage));
		Assert.assertEquals(
			"MorningNoonEveningNightConverterImpl", 
			ShortTextConverter.getConverterClassName(dosage));
		Assert.assertEquals(
			"1 mg morgen efter behov og 2 mg aften efter behov ved måltid", 
			ShortTextConverter.convert(dosage));
		Assert.assertNull(DailyDosisCalculator.calculate(dosage).getValue()); 			
		Assert.assertEquals(DosageType.AccordingToNeed, DosageTypeCalculator.calculate(dosage));		
	}
}
