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

package dk.medicinkortet.dosisstructuretext.ns20130601;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

import dk.medicinkortet.dosisstructuretext.DailyDosisCalculator;
import dk.medicinkortet.dosisstructuretext.DosageType;
import dk.medicinkortet.dosisstructuretext.DosageTypeCalculator;
import dk.medicinkortet.dosisstructuretext.LongTextConverter;
import dk.medicinkortet.dosisstructuretext.ShortTextConverter;
import dk.medicinkortet.dosisstructuretext.shorttextconverterimpl.AdministrationAccordingToSchemaConverterImpl;
import dk.medicinkortet.dosisstructuretext.shorttextconverterimpl.FreeTextConverterImpl;
import dk.medicinkortet.dosisstructuretext.vowrapper.AdministrationAccordingToSchemaWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DateOrDateTimeWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.EveningDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.FreeTextWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.MorningDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.NoonDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.PlainDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructureWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuresWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.UnitOrUnitsWrapper;

public class MultiplePeriodesTest {
	
	@Test
	public void testTwoFollwingPeriodes() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			StructuresWrapper.makeStructures(
				UnitOrUnitsWrapper.makeUnits("tablet", "tabletter"), 
				StructureWrapper.makeStructure(
					0, null, DateOrDateTimeWrapper.makeDate("2013-06-01"), DateOrDateTimeWrapper.makeDate("2013-06-3"),
					DayWrapper.makeDay(
						1, 
						MorningDoseWrapper.makeDose(new BigDecimal(2)), 
						NoonDoseWrapper.makeDose(new BigDecimal(2)), 
						EveningDoseWrapper.makeDose(new BigDecimal(2))),
					DayWrapper.makeDay(
						2, 
						MorningDoseWrapper.makeDose(new BigDecimal(2)), 
						EveningDoseWrapper.makeDose(new BigDecimal(2))),
					DayWrapper.makeDay(
						3, 
						MorningDoseWrapper.makeDose(new BigDecimal(2)))), 
						
						
				StructureWrapper.makeStructure(
					1, null, DateOrDateTimeWrapper.makeDate("2013-06-04"), null, 
					DayWrapper.makeDay(
						1, 
						MorningDoseWrapper.makeDose(new BigDecimal(1)))
					)
				)				
			); 
		Assert.assertEquals(
			"Doseringen indeholder flere perioder:\n" +
			"\n" +
			"Doseringsforløbet starter lørdag den 1. juni 2013, og ophører mandag den 3. juni 2013.\n" +
			"Bemærk at doseringen varierer:\n" +
			"   Doseringsforløb:\n" +
			"   Lørdag den 1. juni 2013: 2 tabletter morgen + 2 tabletter middag + 2 tabletter aften\n" +
			"   Søndag den 2. juni 2013: 2 tabletter morgen + 2 tabletter aften\n" +
			"   Mandag den 3. juni 2013: 2 tabletter morgen\n" +
			"\n" +
			"Doseringsforløbet starter tirsdag den 4. juni 2013 og gentages hver dag:\n" +
			"   Doseringsforløb:\n" +
			"   1 tablet morgen",
			LongTextConverter.convert(dosage));
		Assert.assertNull(ShortTextConverter.getConverterClass(dosage));
		Assert.assertNull(DailyDosisCalculator.calculate(dosage).getValue()); 		
		Assert.assertEquals(DosageType.Combined, DosageTypeCalculator.calculate(dosage));		
	}

	@Test
	public void testTwoFollwingPeriodes_Nedtrapning_DailyRepeated() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			StructuresWrapper.makeStructures(
				UnitOrUnitsWrapper.makeUnits("tablet", "tabletter"), 
				StructureWrapper.makeStructure(
					1, null, DateOrDateTimeWrapper.makeDate("2013-06-01"), DateOrDateTimeWrapper.makeDate("2013-06-03"),
					DayWrapper.makeDay(
						1, 
						MorningDoseWrapper.makeDose(new BigDecimal(2)), 
						NoonDoseWrapper.makeDose(new BigDecimal(2)), 
						EveningDoseWrapper.makeDose(new BigDecimal(2)))),
				StructureWrapper.makeStructure(
					1, null, DateOrDateTimeWrapper.makeDate("2013-06-04"), DateOrDateTimeWrapper.makeDate("2013-06-10"), 
					DayWrapper.makeDay(
						1, 
						MorningDoseWrapper.makeDose(new BigDecimal(1)))
					)
				)				
			); 
		Assert.assertEquals(
			"Doseringen indeholder flere perioder:\n" +
			"\n" +
			"Doseringsforløbet starter lørdag den 1. juni 2013, gentages hver dag, og ophører mandag den 3. juni 2013:\n" +
			"   Doseringsforløb:\n" +
			"   2 tabletter morgen + 2 tabletter middag + 2 tabletter aften\n" +
			"\n" +
			"Doseringsforløbet starter tirsdag den 4. juni 2013, gentages hver dag, og ophører mandag den 10. juni 2013:\n" +
			"   Doseringsforløb:\n" +
			"   1 tablet morgen",
			LongTextConverter.convert(dosage));
		Assert.assertNull(ShortTextConverter.getConverterClass(dosage));
		Assert.assertNull(DailyDosisCalculator.calculate(dosage).getValue()); 		
		Assert.assertEquals(DosageType.Combined, DosageTypeCalculator.calculate(dosage));		
	}
	
	@Test
	public void testTwoFollwingPeriodes_Nedtrapning_2DaysRepeated() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			StructuresWrapper.makeStructures(
				UnitOrUnitsWrapper.makeUnits("tablet", "tabletter"), 
				StructureWrapper.makeStructure(
					2, null, DateOrDateTimeWrapper.makeDate("2013-06-01"), DateOrDateTimeWrapper.makeDate("2013-06-03"),
					DayWrapper.makeDay(
						1, 
						MorningDoseWrapper.makeDose(new BigDecimal(2)), 
						NoonDoseWrapper.makeDose(new BigDecimal(2)), 
						EveningDoseWrapper.makeDose(new BigDecimal(2)))),
				StructureWrapper.makeStructure(
					1, null, DateOrDateTimeWrapper.makeDate("2013-06-04"), DateOrDateTimeWrapper.makeDate("2013-06-10"), 
					DayWrapper.makeDay(
						1, 
						MorningDoseWrapper.makeDose(new BigDecimal(1)))
					)
				)				
			); 
		Assert.assertEquals(
			"Doseringen indeholder flere perioder:\n" +
			"\n" +
			"Doseringsforløbet starter lørdag den 1. juni 2013, forløbet gentages hver 2. dag, og ophører mandag den 3. juni 2013:\n" +
			"   Doseringsforløb:\n" +
			"   Dag 1: 2 tabletter morgen + 2 tabletter middag + 2 tabletter aften\n" +
			"\n" +
			"Doseringsforløbet starter tirsdag den 4. juni 2013, gentages hver dag, og ophører mandag den 10. juni 2013:\n" +
			"   Doseringsforløb:\n" +
			"   1 tablet morgen",
			LongTextConverter.convert(dosage));
		Assert.assertNull(ShortTextConverter.getConverterClass(dosage));
		Assert.assertNull(DailyDosisCalculator.calculate(dosage).getValue()); 		
		Assert.assertEquals(DosageType.Combined, DosageTypeCalculator.calculate(dosage));		
	}
	
	@Test
	public void testTwoFollwingPeriodes_Nedtrapning_3DaysRepeated() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			StructuresWrapper.makeStructures(
				UnitOrUnitsWrapper.makeUnits("tablet", "tabletter"), 
				StructureWrapper.makeStructure(
					3, null, DateOrDateTimeWrapper.makeDate("2013-06-01"), DateOrDateTimeWrapper.makeDate("2013-06-09"),
					DayWrapper.makeDay(
						1, 
						MorningDoseWrapper.makeDose(new BigDecimal(3)))),
				StructureWrapper.makeStructure(
						1, null, DateOrDateTimeWrapper.makeDate("2013-06-09"), DateOrDateTimeWrapper.makeDate("2013-06-18"),
						DayWrapper.makeDay(
							1, 
							MorningDoseWrapper.makeDose(new BigDecimal(2)))),
				StructureWrapper.makeStructure(
					1, null, DateOrDateTimeWrapper.makeDate("2013-06-18"), DateOrDateTimeWrapper.makeDate("2013-06-27"), 
					DayWrapper.makeDay(
						1, 
						MorningDoseWrapper.makeDose(new BigDecimal(1)))
					)
				)				
			); 
		Assert.assertEquals(
			"Doseringen indeholder flere perioder:\n" +
			"\n" +
			"Doseringsforløbet starter lørdag den 1. juni 2013, forløbet gentages efter 3 dage, og ophører søndag den 9. juni 2013:\n" +
			"   Doseringsforløb:\n" +
			"   Lørdag den 1. juni 2013: 3 tabletter morgen\n" +
			"\n" +
			"Doseringsforløbet starter søndag den 9. juni 2013, gentages hver dag, og ophører tirsdag den 18. juni 2013:\n" +
			"   Doseringsforløb:\n" +
			"   2 tabletter morgen\n" +
			"\n" +
			"Doseringsforløbet starter tirsdag den 18. juni 2013, gentages hver dag, og ophører torsdag den 27. juni 2013:\n" +
			"   Doseringsforløb:\n" +
			"   1 tablet morgen",
			LongTextConverter.convert(dosage));
		Assert.assertNull(ShortTextConverter.getConverterClass(dosage));
		Assert.assertNull(DailyDosisCalculator.calculate(dosage).getValue()); 		
		Assert.assertEquals(DosageType.Combined, DosageTypeCalculator.calculate(dosage));		
	}
	
	@Test
	public void testTwoFololwingPeriodesWithEmptyStructure() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			StructuresWrapper.makeStructures(
				UnitOrUnitsWrapper.makeUnits("tablet", "tabletter"), 
				StructureWrapper.makeStructure(
					0, null, DateOrDateTimeWrapper.makeDate("2013-06-01"), DateOrDateTimeWrapper.makeDate("2013-06-3"),
					DayWrapper.makeDay(
						1, 
						MorningDoseWrapper.makeDose(new BigDecimal(2)), 
						NoonDoseWrapper.makeDose(new BigDecimal(2)), 
						EveningDoseWrapper.makeDose(new BigDecimal(2))),
					DayWrapper.makeDay(
						2, 
						MorningDoseWrapper.makeDose(new BigDecimal(2)), 
						EveningDoseWrapper.makeDose(new BigDecimal(2))),
					DayWrapper.makeDay(
						3, 
						MorningDoseWrapper.makeDose(new BigDecimal(2)))), 
						
						
				StructureWrapper.makeStructure(
					1, null, DateOrDateTimeWrapper.makeDate("2013-06-04"), null))); 
		Assert.assertEquals(
			"Doseringen indeholder flere perioder:\n" +
			"\n" +
			"Doseringsforløbet starter lørdag den 1. juni 2013, og ophører mandag den 3. juni 2013.\n" +
			"Bemærk at doseringen varierer:\n" +
			"   Doseringsforløb:\n" +
			"   Lørdag den 1. juni 2013: 2 tabletter morgen + 2 tabletter middag + 2 tabletter aften\n" +
			"   Søndag den 2. juni 2013: 2 tabletter morgen + 2 tabletter aften\n" +
			"   Mandag den 3. juni 2013: 2 tabletter morgen\n" +
			"\n" +
			"Doseringsforløbet starter tirsdag den 4. juni 2013:\n" +
			"   Bemærk: skal ikke anvendes i denne periode!",
			LongTextConverter.convert(dosage));
		Assert.assertNull(ShortTextConverter.getConverterClass(dosage));
		Assert.assertNull(DailyDosisCalculator.calculate(dosage).getValue()); 		
		Assert.assertEquals(DosageType.Combined, DosageTypeCalculator.calculate(dosage));		
	}
	
	@Test
	public void testEmptyStructure() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			StructuresWrapper.makeStructures(
				UnitOrUnitsWrapper.makeUnits("tablet", "tabletter"), 
				StructureWrapper.makeStructure(
					0, null, DateOrDateTimeWrapper.makeDate("2013-06-01"), DateOrDateTimeWrapper.makeDate("2013-06-3"),
					DayWrapper.makeDay(
						1, 
						MorningDoseWrapper.makeDose(new BigDecimal(2)), 
						NoonDoseWrapper.makeDose(new BigDecimal(2)), 
						EveningDoseWrapper.makeDose(new BigDecimal(2))),
					DayWrapper.makeDay(
						2, 
						MorningDoseWrapper.makeDose(new BigDecimal(2)), 
						EveningDoseWrapper.makeDose(new BigDecimal(2))),
					DayWrapper.makeDay(
						3, 
						MorningDoseWrapper.makeDose(new BigDecimal(2)))), 
						
						
				StructureWrapper.makeStructure(
					1, null, DateOrDateTimeWrapper.makeDate("2013-06-04"), null))); 
		Assert.assertEquals(
			"Doseringen indeholder flere perioder:\n" +
			"\n" +
			"Doseringsforløbet starter lørdag den 1. juni 2013, og ophører mandag den 3. juni 2013.\n" +
			"Bemærk at doseringen varierer:\n" +
			"   Doseringsforløb:\n" +
			"   Lørdag den 1. juni 2013: 2 tabletter morgen + 2 tabletter middag + 2 tabletter aften\n" +
			"   Søndag den 2. juni 2013: 2 tabletter morgen + 2 tabletter aften\n" +
			"   Mandag den 3. juni 2013: 2 tabletter morgen\n" +
			"\n" +
			"Doseringsforløbet starter tirsdag den 4. juni 2013:\n" +
			"   Bemærk: skal ikke anvendes i denne periode!",
			LongTextConverter.convert(dosage));
		Assert.assertNull(ShortTextConverter.getConverterClass(dosage));
		Assert.assertNull(DailyDosisCalculator.calculate(dosage).getValue()); 		
		Assert.assertEquals(DosageType.Combined, DosageTypeCalculator.calculate(dosage));		
	}
	
	
	
	@Test
	public void testTwoNotFollwingPeriodes() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			StructuresWrapper.makeStructures(
				UnitOrUnitsWrapper.makeUnits("tablet", "tabletter"), 
				StructureWrapper.makeStructure(
					0, null, DateOrDateTimeWrapper.makeDate("2013-06-01"), null,
					DayWrapper.makeDay(
						1, 
						MorningDoseWrapper.makeDose(new BigDecimal(2)), 
						NoonDoseWrapper.makeDose(new BigDecimal(2)), 
						EveningDoseWrapper.makeDose(new BigDecimal(2))),
					DayWrapper.makeDay(
						2, 
						MorningDoseWrapper.makeDose(new BigDecimal(2)), 
						EveningDoseWrapper.makeDose(new BigDecimal(2))),
					DayWrapper.makeDay(
						3, 
						MorningDoseWrapper.makeDose(new BigDecimal(2)))), 
						
						
				StructureWrapper.makeStructure(
					1, null, DateOrDateTimeWrapper.makeDate("2013-06-04"), null, 
					DayWrapper.makeDay(
						1, 
						MorningDoseWrapper.makeDose(new BigDecimal(1)))
					)
				)				
			); 
		Assert.assertEquals(
			"Doseringen indeholder flere perioder, bemærk at der er overlappende perioder:\n" +
			"\n" +
			"Doseringsforløbet starter lørdag den 1. juni 2013 og ophører efter det angivne forløb.\n" +
			"Bemærk at doseringen varierer:\n" +
			"   Doseringsforløb:\n" +
			"   Lørdag den 1. juni 2013: 2 tabletter morgen + 2 tabletter middag + 2 tabletter aften\n" +
			"   Søndag den 2. juni 2013: 2 tabletter morgen + 2 tabletter aften\n" +
			"   Mandag den 3. juni 2013: 2 tabletter morgen\n" +
			"\n" +
			"Doseringsforløbet starter tirsdag den 4. juni 2013 og gentages hver dag:\n" +
			"   Doseringsforløb:\n" +
			"   1 tablet morgen",
			LongTextConverter.convert(dosage));
		Assert.assertNull(ShortTextConverter.getConverterClass(dosage));
		Assert.assertNull(DailyDosisCalculator.calculate(dosage).getValue()); 		
		Assert.assertEquals(DosageType.Combined, DosageTypeCalculator.calculate(dosage));		
	}
	
	
	@Test
	public void testTwoFollwingPeriodesWithOverlappingPN() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			StructuresWrapper.makeStructures(
				UnitOrUnitsWrapper.makeUnits("tablet", "tabletter"), 
				StructureWrapper.makeStructure(
					0, null, DateOrDateTimeWrapper.makeDate("2013-06-01"), DateOrDateTimeWrapper.makeDate("2013-06-3"),
					DayWrapper.makeDay(
						1, 
						MorningDoseWrapper.makeDose(new BigDecimal(2)), 
						NoonDoseWrapper.makeDose(new BigDecimal(2)), 
						EveningDoseWrapper.makeDose(new BigDecimal(2))),
					DayWrapper.makeDay(
						2, 
						MorningDoseWrapper.makeDose(new BigDecimal(2)), 
						EveningDoseWrapper.makeDose(new BigDecimal(2))),
					DayWrapper.makeDay(
						3, 
						MorningDoseWrapper.makeDose(new BigDecimal(2)))), 
						
						
				StructureWrapper.makeStructure(
					1, null, DateOrDateTimeWrapper.makeDate("2013-06-04"), null, 
					DayWrapper.makeDay(
						1, 
						MorningDoseWrapper.makeDose(new BigDecimal(1)))),
				
				
				StructureWrapper.makeStructure(
					0, "ved smerter", DateOrDateTimeWrapper.makeDate("2013-06-01"), null, 
					DayWrapper.makeDay(
						0, 
						PlainDoseWrapper.makeDose(new BigDecimal(2), true))))
						
			); 
				
		Assert.assertEquals(
			"Doseringen indeholder flere perioder, bemærk at der er overlappende perioder:\n" +
			"\n" +
			"Doseringsforløbet starter lørdag den 1. juni 2013, og ophører mandag den 3. juni 2013.\n" +
			"Bemærk at doseringen varierer:\n" +
			"   Doseringsforløb:\n" +
			"   Lørdag den 1. juni 2013: 2 tabletter morgen + 2 tabletter middag + 2 tabletter aften\n" +
			"   Søndag den 2. juni 2013: 2 tabletter morgen + 2 tabletter aften\n" +
			"   Mandag den 3. juni 2013: 2 tabletter morgen\n" +
			"\n" +
			"Doseringsforløbet starter lørdag den 1. juni 2013:\n" + 
			"   Doseringsforløb:\n" +
			"   Efter behov: 2 tabletter efter behov ved smerter\n" +
			"\n" + 
			"Doseringsforløbet starter tirsdag den 4. juni 2013 og gentages hver dag:\n" +
			"   Doseringsforløb:\n" +
			"   1 tablet morgen",
			LongTextConverter.convert(dosage));
		Assert.assertNull(ShortTextConverter.getConverterClass(dosage));
		Assert.assertNull(DailyDosisCalculator.calculate(dosage).getValue()); 		
		Assert.assertEquals(DosageType.Combined, DosageTypeCalculator.calculate(dosage));		
	}
	
	@Test
	public void freeTextWithStartEndDateTest() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			FreeTextWrapper.makeFreeText(
				DateOrDateTimeWrapper.makeDate("2013-06-01"), DateOrDateTimeWrapper.makeDate("2013-06-03"), 
				"Efter aftale"));
				
		Assert.assertEquals(
			"Doseringsforløbet starter lørdag den 1. juni 2013 og ophører mandag den 3. juni 2013.\n" +
			"   Doseringsforløb:\n" +
			"   Efter aftale",
			LongTextConverter.convert(dosage));
		Assert.assertEquals(FreeTextConverterImpl.class, ShortTextConverter.getConverterClass(dosage));
		Assert.assertNull(DailyDosisCalculator.calculate(dosage).getValue()); 		
		Assert.assertEquals(DosageType.Unspecified, DosageTypeCalculator.calculate(dosage));		
	}
	
	@Test
	public void structuredWithStartEndDateTimeTest() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
				StructuresWrapper.makeStructures(
					UnitOrUnitsWrapper.makeUnits("tablet", "tabletter"), 
					StructureWrapper.makeStructure(
						0, null, DateOrDateTimeWrapper.makeDateTime("2013-06-01 08:00:00"), DateOrDateTimeWrapper.makeDateTime("2013-06-03 10:00:00"),
						DayWrapper.makeDay(
							1, 
							MorningDoseWrapper.makeDose(new BigDecimal(2)), 
							NoonDoseWrapper.makeDose(new BigDecimal(2)), 
							EveningDoseWrapper.makeDose(new BigDecimal(2))),
						DayWrapper.makeDay(
							2, 
							MorningDoseWrapper.makeDose(new BigDecimal(2)), 
							EveningDoseWrapper.makeDose(new BigDecimal(2))),
						DayWrapper.makeDay(
							3, 
							MorningDoseWrapper.makeDose(new BigDecimal(2)))), 
							
							
					StructureWrapper.makeStructure(
						1, null, DateOrDateTimeWrapper.makeDateTime("2013-06-04 10:30:00"), DateOrDateTimeWrapper.makeDateTime("2013-06-06 15:30:00"), 
						DayWrapper.makeDay(
							1, 
							MorningDoseWrapper.makeDose(new BigDecimal(1)))),
					
					
					StructureWrapper.makeStructure(
						0, "ved smerter", DateOrDateTimeWrapper.makeDateTime("2013-06-01 14:20:00"), DateOrDateTimeWrapper.makeDateTime("2013-06-10 20:30:00"), 
						DayWrapper.makeDay(
							0, 
							PlainDoseWrapper.makeDose(new BigDecimal(2), true))))
							
				); 
		
				
		Assert.assertEquals(
				"Doseringen indeholder flere perioder, bemærk at der er overlappende perioder:\n" +
				"\n" +
				"Doseringsforløbet starter lørdag den 1. juni 2013 kl. 08:00, og ophører mandag den 3. juni 2013 kl. 10:00.\n" +
				"Bemærk at doseringen varierer:\n" +
				"   Doseringsforløb:\n" +
				"   Lørdag den 1. juni 2013: 2 tabletter morgen + 2 tabletter middag + 2 tabletter aften\n" +
				"   Søndag den 2. juni 2013: 2 tabletter morgen + 2 tabletter aften\n" +
				"   Mandag den 3. juni 2013: 2 tabletter morgen\n" +
				"\n" +
				"Doseringsforløbet starter lørdag den 1. juni 2013 kl. 14:20:\n" + 
				"   Doseringsforløb:\n" +
				"   Efter behov: 2 tabletter efter behov ved smerter\n" +
				"\n" + 
				"Doseringsforløbet starter tirsdag den 4. juni 2013 kl. 10:30, gentages hver dag, og ophører torsdag den 6. juni 2013 kl. 15:30:\n" +
				"   Doseringsforløb:\n" +
				"   1 tablet morgen",
				LongTextConverter.convert(dosage));
		Assert.assertNull(DailyDosisCalculator.calculate(dosage).getValue()); 		
		Assert.assertEquals(DosageType.Combined, DosageTypeCalculator.calculate(dosage));		
	}
	
	@Test
	public void freeTextWithSameStartEndDateTest() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			FreeTextWrapper.makeFreeText(
				DateOrDateTimeWrapper.makeDate("2013-06-01"), DateOrDateTimeWrapper.makeDate("2013-06-01"), 
				"Efter aftale"));
				
		Assert.assertEquals(
			"Doseringen foretages kun lørdag den 1. juni 2013.\n" +
			"   Dosering:\n" +
			"   Efter aftale",
			LongTextConverter.convert(dosage));
		Assert.assertEquals(FreeTextConverterImpl.class, ShortTextConverter.getConverterClass(dosage));
		Assert.assertNull(DailyDosisCalculator.calculate(dosage).getValue()); 		
		Assert.assertEquals(DosageType.Unspecified, DosageTypeCalculator.calculate(dosage));		
	}
	
	@Test
	public void freeTextWithStartDateTest() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			FreeTextWrapper.makeFreeText(
				DateOrDateTimeWrapper.makeDate("2013-06-01"), null, 
				"Efter aftale"));
				
		Assert.assertEquals(
			"Doseringsforløbet starter lørdag den 1. juni 2013.\n" +
			"   Doseringsforløb:\n" +
			"   Efter aftale",
			LongTextConverter.convert(dosage));
		Assert.assertEquals(FreeTextConverterImpl.class, ShortTextConverter.getConverterClass(dosage));
		Assert.assertNull(DailyDosisCalculator.calculate(dosage).getValue()); 		
		Assert.assertEquals(DosageType.Unspecified, DosageTypeCalculator.calculate(dosage));		
	}	
	
	@Test
	public void freeTextWithEndDateTest() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			FreeTextWrapper.makeFreeText(
				null, DateOrDateTimeWrapper.makeDate("2013-06-03"), 
				"Efter aftale"));
				
		Assert.assertEquals(
			"Doseringsforløbet ophører mandag den 3. juni 2013.\n" +
			"   Doseringsforløb:\n" +
			"   Efter aftale",
			LongTextConverter.convert(dosage));
		Assert.assertEquals(FreeTextConverterImpl.class, ShortTextConverter.getConverterClass(dosage));
		Assert.assertNull(DailyDosisCalculator.calculate(dosage).getValue()); 		
		Assert.assertEquals(DosageType.Unspecified, DosageTypeCalculator.calculate(dosage));		
	}
	
	@Test
	public void accordingToSchemaWithStartEndDateTest() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			AdministrationAccordingToSchemaWrapper.makeAdministrationAccordingToSchema(
				DateOrDateTimeWrapper.makeDate("2013-06-01"), DateOrDateTimeWrapper.makeDate("2013-06-03")));
				
		Assert.assertEquals(
			"Doseringsforløbet starter lørdag den 1. juni 2013 og ophører mandag den 3. juni 2013.\n" +
			"   Doseringsforløb:\n" +
			"   Dosering efter skriftlig anvisning",
			LongTextConverter.convert(dosage));
		Assert.assertEquals(AdministrationAccordingToSchemaConverterImpl.class, ShortTextConverter.getConverterClass(dosage));
		Assert.assertNull(DailyDosisCalculator.calculate(dosage).getValue()); 		
		Assert.assertEquals(DosageType.Unspecified, DosageTypeCalculator.calculate(dosage));		
	}
	
	@Test
	public void accordingToSchemaWithSameStartEndDateTest() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			AdministrationAccordingToSchemaWrapper.makeAdministrationAccordingToSchema(
				DateOrDateTimeWrapper.makeDate("2013-06-01"), DateOrDateTimeWrapper.makeDate("2013-06-01")));
				
		Assert.assertEquals(
			"Doseringen foretages kun lørdag den 1. juni 2013.\n" +
			"   Dosering:\n" +
			"   Dosering efter skriftlig anvisning",
			LongTextConverter.convert(dosage));
		Assert.assertEquals(AdministrationAccordingToSchemaConverterImpl.class, ShortTextConverter.getConverterClass(dosage));
		Assert.assertNull(DailyDosisCalculator.calculate(dosage).getValue()); 		
		Assert.assertEquals(DosageType.Unspecified, DosageTypeCalculator.calculate(dosage));		
	}
	
	@Test
	public void accordingToSchemaWithStartDateTest() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			AdministrationAccordingToSchemaWrapper.makeAdministrationAccordingToSchema(
				DateOrDateTimeWrapper.makeDate("2013-06-01"), null));
				
		Assert.assertEquals(
			"Doseringsforløbet starter lørdag den 1. juni 2013.\n" +
			"   Doseringsforløb:\n" +
			"   Dosering efter skriftlig anvisning",
			LongTextConverter.convert(dosage));
		Assert.assertEquals(AdministrationAccordingToSchemaConverterImpl.class, ShortTextConverter.getConverterClass(dosage));
		Assert.assertNull(DailyDosisCalculator.calculate(dosage).getValue()); 		
		Assert.assertEquals(DosageType.Unspecified, DosageTypeCalculator.calculate(dosage));		
	}	
	
	@Test
	public void accordingToSchemaWithEndDateTest() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			AdministrationAccordingToSchemaWrapper.makeAdministrationAccordingToSchema(
				null, DateOrDateTimeWrapper.makeDate("2013-06-03")));
				
		Assert.assertEquals(
			"Doseringsforløbet ophører mandag den 3. juni 2013.\n" +
			"   Doseringsforløb:\n" +
			"   Dosering efter skriftlig anvisning",
			LongTextConverter.convert(dosage));
		Assert.assertEquals(AdministrationAccordingToSchemaConverterImpl.class, ShortTextConverter.getConverterClass(dosage));
		Assert.assertNull(DailyDosisCalculator.calculate(dosage).getValue()); 		
		Assert.assertEquals(DosageType.Unspecified, DosageTypeCalculator.calculate(dosage));		
	}

}
