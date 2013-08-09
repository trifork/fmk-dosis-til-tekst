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

package dk.medicinkortet.dosisstructuretext.ns2009;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

import dk.medicinkortet.dosisstructuretext.DailyDosisCalculator;
import dk.medicinkortet.dosisstructuretext.DosageType;
import dk.medicinkortet.dosisstructuretext.DosageTypeCalculator;
import dk.medicinkortet.dosisstructuretext.LongTextConverter;
import dk.medicinkortet.dosisstructuretext.ShortTextConverter;
import dk.medicinkortet.dosisstructuretext.longtextconverterimpl.DefaultLongTextConverterImpl;
import dk.medicinkortet.dosisstructuretext.shorttextconverterimpl.SimpleAccordingToNeedConverterImpl;
import dk.medicinkortet.dosisstructuretext.shorttextconverterimpl.SimpleLimitedAccordingToNeedConverterImpl;
import dk.medicinkortet.dosisstructuretext.shorttextconverterimpl.WeeklyMorningNoonEveningNightConverterImpl;
import dk.medicinkortet.dosisstructuretext.vowrapper.DateOrDateTimeWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.EveningDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.MorningDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.NoonDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.PlainDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructureWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuresWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.UnitOrUnitsWrapper;

/**
 * Examples of translation to long dosage text, as discussed on the FMK-teknik forum: 
 * http://www.fmk-teknik.dk/index.php?topic=135.0
 */
public class LongTextComplexConverterTest {

	@Test /* Dosage "1 tablet morgen" */
	public void test1TabletMorgen() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			StructuresWrapper.makeStructures(
				UnitOrUnitsWrapper.makeUnit("tablet"), 
				StructureWrapper.makeStructure(
					1, null, DateOrDateTimeWrapper.makeDate("2012-04-18"), null, 
					DayWrapper.makeDay(
						1, 
						MorningDoseWrapper.makeDose(new BigDecimal(1))))));
		Assert.assertEquals(
				"Doseringsforløbet starter onsdag den 18. april 2012 og gentages hver dag:\n"+
				"   Doseringsforløb:\n"+
				"   1 tablet morgen", 
				LongTextConverter.convert(dosage));
		Assert.assertEquals("1 tablet morgen", ShortTextConverter.convert(dosage));
		Assert.assertEquals(
				1, 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001);
		Assert.assertEquals(DosageType.Fixed, DosageTypeCalculator.calculate(dosage));		
	}

	@Test /* Dosage "1 tablet morgen" with datetimes */
	public void test1TabletMorgenWithDatetimes() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			StructuresWrapper.makeStructures(
				UnitOrUnitsWrapper.makeUnit("tablet"), 
				StructureWrapper.makeStructure(
				1, null, DateOrDateTimeWrapper.makeDateTime("2012-04-18 08:30:00"), null,
				DayWrapper.makeDay(
					1, 
					MorningDoseWrapper.makeDose(new BigDecimal(1))))));
		Assert.assertEquals(
				"Doseringsforløbet starter onsdag den 18. april 2012 kl. 08:30 og gentages hver dag:\n"+
				"   Doseringsforløb:\n"+
				"   1 tablet morgen", 
				LongTextConverter.convert(dosage));
		Assert.assertEquals("1 tablet morgen", ShortTextConverter.convert(dosage));
		Assert.assertEquals(
				1, 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001); 			
		Assert.assertEquals(DosageType.Fixed, DosageTypeCalculator.calculate(dosage));
	}		
	
	@Test /* Dosage like the "Hjerdyl"-example */
	public void testHjerdyl() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			StructuresWrapper.makeStructures(
				UnitOrUnitsWrapper.makeUnit("tablet"), 
				StructureWrapper.makeStructure(
				2, null, DateOrDateTimeWrapper.makeDate("2012-04-18"), null, 
				DayWrapper.makeDay(
					1, 
					MorningDoseWrapper.makeDose(new BigDecimal(1))), 
				DayWrapper.makeDay(
					2, 
					MorningDoseWrapper.makeDose(new BigDecimal(1)), 
					EveningDoseWrapper.makeDose(new BigDecimal(1))))));
		Assert.assertEquals(
				"Doseringsforløbet starter onsdag den 18. april 2012, forløbet gentages hver 2. dag.\n"+
				"Bemærk at doseringen varierer:\n"+
				"   Doseringsforløb:\n"+
				"   Dag 1: 1 tablet morgen\n"+
				"   Dag 2: 1 tablet morgen + 1 tablet aften",			
				LongTextConverter.convert(dosage));
		Assert.assertNull(ShortTextConverter.convert(dosage));
		Assert.assertEquals(
				1.5, 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001); 				
		Assert.assertEquals(DosageType.Fixed, DosageTypeCalculator.calculate(dosage));		
	}		
	
	@Test /* Dosage like the "Alendronat" example */
	public void testAledronat() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			StructuresWrapper.makeStructures(
				UnitOrUnitsWrapper.makeUnit("tablet"), 
				StructureWrapper.makeStructure(
				7, null, DateOrDateTimeWrapper.makeDate("2012-04-18"), null, 
				DayWrapper.makeDay(
					1, 
					MorningDoseWrapper.makeDose(new BigDecimal(1))))));
		Assert.assertEquals(
				"Doseringsforløbet starter onsdag den 18. april 2012, forløbet gentages hver uge:\n"+
				"   Doseringsforløb:\n"+
				"   Onsdag: 1 tablet morgen",
				LongTextConverter.convert(dosage));
		Assert.assertEquals(WeeklyMorningNoonEveningNightConverterImpl.class, ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals("1 tablet morgen onsdag hver uge", ShortTextConverter.convert(dosage));
		Assert.assertEquals(
				1/7.0, 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001); 				
		Assert.assertEquals(DosageType.Fixed, DosageTypeCalculator.calculate(dosage));		
	}
	
	@Test /* Dosage like the "Marevan 14-dages skema 1+2 stk" example */
	public void testMarevan14DagesSkema1_2Tablet() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			StructuresWrapper.makeStructures(
				UnitOrUnitsWrapper.makeUnit("stk"), 
				StructureWrapper.makeStructure(
					14, null, DateOrDateTimeWrapper.makeDate("2012-04-19"), null, 
					DayWrapper.makeDay(
						1, // torsdag
						MorningDoseWrapper.makeDose(new BigDecimal(2))), 
					DayWrapper.makeDay(
						2, 
						MorningDoseWrapper.makeDose(new BigDecimal(1))), 
					DayWrapper.makeDay(
						3, // lørdag
						MorningDoseWrapper.makeDose(new BigDecimal(2))), 
					DayWrapper.makeDay(
						4, 
						MorningDoseWrapper.makeDose(new BigDecimal(1))), 
					DayWrapper.makeDay(
						5, // mandag
						MorningDoseWrapper.makeDose(new BigDecimal(2))), 
					DayWrapper.makeDay(
						6, 
						MorningDoseWrapper.makeDose(new BigDecimal(1))), 
					DayWrapper.makeDay(
						7, // onsdag
						MorningDoseWrapper.makeDose(new BigDecimal(2))),	
					DayWrapper.makeDay(
						8, 
						MorningDoseWrapper.makeDose(new BigDecimal(1))),	
					DayWrapper.makeDay(
						9, // fredag
						MorningDoseWrapper.makeDose(new BigDecimal(2))),	
					DayWrapper.makeDay(
						10, 
						MorningDoseWrapper.makeDose(new BigDecimal(1))),	
					DayWrapper.makeDay(
						11, // søndag
						MorningDoseWrapper.makeDose(new BigDecimal(2))),	
					DayWrapper.makeDay(
						12, 
						MorningDoseWrapper.makeDose(new BigDecimal(1))),	
					DayWrapper.makeDay(
						13, // tirsdag
						MorningDoseWrapper.makeDose(new BigDecimal(2))),	
					DayWrapper.makeDay(
						14, 
						MorningDoseWrapper.makeDose(new BigDecimal(1))))));
		Assert.assertEquals(
				"Doseringsforløbet starter torsdag den 19. april 2012, forløbet gentages efter 14 dage.\n"+
				"Bemærk at doseringen varierer:\n"+
				"   Doseringsforløb:\n"+
				"   Torsdag den 19. april 2012: 2 stk morgen\n"+
				"   Fredag den 20. april 2012: 1 stk morgen\n"+
				"   Lørdag den 21. april 2012: 2 stk morgen\n"+
				"   Søndag den 22. april 2012: 1 stk morgen\n"+
				"   Mandag den 23. april 2012: 2 stk morgen\n"+
				"   Tirsdag den 24. april 2012: 1 stk morgen\n"+
				"   Onsdag den 25. april 2012: 2 stk morgen\n"+
				"   Torsdag den 26. april 2012: 1 stk morgen\n"+
				"   Fredag den 27. april 2012: 2 stk morgen\n"+
				"   Lørdag den 28. april 2012: 1 stk morgen\n"+
				"   Søndag den 29. april 2012: 2 stk morgen\n"+
				"   Mandag den 30. april 2012: 1 stk morgen\n"+
				"   Tirsdag den 1. maj 2012: 2 stk morgen\n"+
				"   Onsdag den 2. maj 2012: 1 stk morgen",
				LongTextConverter.convert(dosage));
		Assert.assertNull(ShortTextConverter.convert(dosage));
		Assert.assertEquals(
				1.5, 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001); 		
		Assert.assertEquals(DosageType.Fixed, DosageTypeCalculator.calculate(dosage));		
	}
	
	@Test /* Dosage like the "Marevan ugeskema 1+2 stk" example */
	public void testMarevanUgeskema1_2Tabletter() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			StructuresWrapper.makeStructures(
				UnitOrUnitsWrapper.makeUnit("stk"), 
				StructureWrapper.makeStructure(
					7, null, DateOrDateTimeWrapper.makeDate("2012-04-19"), null, 
					DayWrapper.makeDay(
						1, // torsdag
						MorningDoseWrapper.makeDose(new BigDecimal(2))), 
					DayWrapper.makeDay(
						2, 
						MorningDoseWrapper.makeDose(new BigDecimal(1))), 
					DayWrapper.makeDay(
						3, // lørdag
						MorningDoseWrapper.makeDose(new BigDecimal(2))), 
					DayWrapper.makeDay(
						4, 
						MorningDoseWrapper.makeDose(new BigDecimal(1))), 
					DayWrapper.makeDay(
						5, // mandag
						MorningDoseWrapper.makeDose(new BigDecimal(2))), 
					DayWrapper.makeDay(
						6, 
						MorningDoseWrapper.makeDose(new BigDecimal(1))), 
					DayWrapper.makeDay(
						7, // onsdag
						MorningDoseWrapper.makeDose(new BigDecimal(1))))));
		Assert.assertEquals(
				"Doseringsforløbet starter torsdag den 19. april 2012, forløbet gentages hver uge.\n"+
				"Bemærk at doseringen varierer:\n"+
				"   Doseringsforløb:\n"+
				"   Mandag: 2 stk morgen\n"+
				"   Tirsdag: 1 stk morgen\n"+
				"   Onsdag: 1 stk morgen\n"+
				"   Torsdag: 2 stk morgen\n"+
				"   Fredag: 1 stk morgen\n"+
				"   Lørdag: 2 stk morgen\n"+
				"   Søndag: 1 stk morgen",
				LongTextConverter.convert(dosage));
		Assert.assertNull(ShortTextConverter.convert(dosage));
		Assert.assertEquals(
				10/7.0, 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001); 		
		Assert.assertEquals(DosageType.Fixed, DosageTypeCalculator.calculate(dosage));		
	}	
	
	@Test /* Dosage like the "Naragan ugeskema 1 tablet" example */
	public void testNaraganUgeskema1Tablet() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			StructuresWrapper.makeStructures(
				UnitOrUnitsWrapper.makeUnit("tablet"), 
				StructureWrapper.makeStructure(
					7, null, DateOrDateTimeWrapper.makeDate("2012-04-18"), null, 
					DayWrapper.makeDay(
						1, 
						MorningDoseWrapper.makeDose(new BigDecimal(1))), 
					DayWrapper.makeDay(
						3, 
						MorningDoseWrapper.makeDose(new BigDecimal(1))), 
					DayWrapper.makeDay(
						5, 
						MorningDoseWrapper.makeDose(new BigDecimal(1))), 
					DayWrapper.makeDay(
						7, 
						MorningDoseWrapper.makeDose(new BigDecimal(1))))));
		Assert.assertEquals(
				"Doseringsforløbet starter onsdag den 18. april 2012, forløbet gentages hver uge.\n"+
				"Bemærk at doseringen har et komplekst forløb:\n"+
				"   Doseringsforløb:\n"+
				"   Tirsdag: 1 tablet morgen\n"+
				"   Onsdag: 1 tablet morgen\n"+
				"   Fredag: 1 tablet morgen\n"+
				"   Søndag: 1 tablet morgen",
				LongTextConverter.convert(dosage));
		Assert.assertEquals(WeeklyMorningNoonEveningNightConverterImpl.class, ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals("1 tablet morgen tirsdag, onsdag, fredag og søndag hver uge", ShortTextConverter.convert(dosage));
		Assert.assertEquals(
				4/7.0, 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001); 			
		Assert.assertEquals(DosageType.Fixed, DosageTypeCalculator.calculate(dosage));		
	}
	
	@Test /* Dosage like the "Naragan ugeskema 2 tabletter" example */
	public void testNaraganUgeskema2Tabletter() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			StructuresWrapper.makeStructures(
				UnitOrUnitsWrapper.makeUnit("tabletter"), 
				StructureWrapper.makeStructure(
					7, null, DateOrDateTimeWrapper.makeDate("2012-04-18"), null, 
					DayWrapper.makeDay(
						2, 
						MorningDoseWrapper.makeDose(new BigDecimal(2))), 
					DayWrapper.makeDay(
						4, 
						MorningDoseWrapper.makeDose(new BigDecimal(2))), 
					DayWrapper.makeDay(
						6, 
						MorningDoseWrapper.makeDose(new BigDecimal(2))))));
		Assert.assertEquals(
				"Doseringsforløbet starter onsdag den 18. april 2012, forløbet gentages hver uge.\n"+
				"Bemærk at doseringen har et komplekst forløb:\n"+
				"   Doseringsforløb:\n"+
				"   Mandag: 2 tabletter morgen\n"+
				"   Torsdag: 2 tabletter morgen\n"+
				"   Lørdag: 2 tabletter morgen",
				LongTextConverter.convert(dosage));		
		Assert.assertEquals(WeeklyMorningNoonEveningNightConverterImpl.class, ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals("2 tabletter morgen mandag, torsdag og lørdag hver uge", ShortTextConverter.convert(dosage));
		Assert.assertEquals(
				6/7.0, 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001); 
		Assert.assertEquals(DosageType.Fixed, DosageTypeCalculator.calculate(dosage));		
	}	
	
	@Test /* Dosage like the "Morfin nedtrapning" example */
	public void testMorfinNedtrapning() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			StructuresWrapper.makeStructures(
				UnitOrUnitsWrapper.makeUnit("stk"), 
				StructureWrapper.makeStructure(
					0, null, DateOrDateTimeWrapper.makeDate("2012-04-18"), null, 
					DayWrapper.makeDay(
						1, 
						MorningDoseWrapper.makeDose(new BigDecimal(2)), 
						NoonDoseWrapper.makeDose(new BigDecimal(2)),
						EveningDoseWrapper.makeDose(new BigDecimal(2))), 
					DayWrapper.makeDay(
						2, 
						MorningDoseWrapper.makeDose(new BigDecimal(2)), 
						NoonDoseWrapper.makeDose(new BigDecimal(1)),
						EveningDoseWrapper.makeDose(new BigDecimal(2))), 
					DayWrapper.makeDay(
						3, 
						MorningDoseWrapper.makeDose(new BigDecimal(1)), 
						NoonDoseWrapper.makeDose(new BigDecimal(1)),
						EveningDoseWrapper.makeDose(new BigDecimal(2))), 
					DayWrapper.makeDay(
						4, 
						MorningDoseWrapper.makeDose(new BigDecimal(1)), 
						EveningDoseWrapper.makeDose(new BigDecimal(1))), 
					DayWrapper.makeDay(
						5, 
						MorningDoseWrapper.makeDose(new BigDecimal(1)), 
						EveningDoseWrapper.makeDose(new BigDecimal(1))), 
					DayWrapper.makeDay(
						6, 
						EveningDoseWrapper.makeDose(new BigDecimal(1))))));
		Assert.assertEquals(
				"Doseringsforløbet starter onsdag den 18. april 2012 og ophører efter det angivne forløb.\n"+
				"Bemærk at doseringen varierer:\n"+
				"   Doseringsforløb:\n"+
				"   Onsdag den 18. april 2012: 2 stk morgen + 2 stk middag + 2 stk aften\n"+
				"   Torsdag den 19. april 2012: 2 stk morgen + 1 stk middag + 2 stk aften\n"+
				"   Fredag den 20. april 2012: 1 stk morgen + 1 stk middag + 2 stk aften\n"+
				"   Lørdag den 21. april 2012: 1 stk morgen + 1 stk aften\n"+
				"   Søndag den 22. april 2012: 1 stk morgen + 1 stk aften\n"+
				"   Mandag den 23. april 2012: 1 stk aften",
				LongTextConverter.convert(dosage));
		Assert.assertNull(ShortTextConverter.convert(dosage));
		Assert.assertNull(
				DailyDosisCalculator.calculate(dosage).getValue());
		Assert.assertEquals(DosageType.Temporary, DosageTypeCalculator.calculate(dosage));		
	}		
	
	@Test /* Dosage like the "Pulmicort" example */ 
	public void testDag0Iterationsinterval0() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			StructuresWrapper.makeStructures(
				UnitOrUnitsWrapper.makeUnit("sug"), 
				StructureWrapper.makeStructure(
					0, "ved anstrengelse", DateOrDateTimeWrapper.makeDate("2012-04-18"), null, 
					DayWrapper.makeDay(
						0, 
						PlainDoseWrapper.makeDose(new BigDecimal(1), new BigDecimal(2), true)))));
		Assert.assertEquals(
				DefaultLongTextConverterImpl.class,
				LongTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
				"Doseringsforløbet starter onsdag den 18. april 2012:\n"+ 
				"   Doseringsforløb:\n"+
				"   Efter behov: 1-2 sug efter behov ved anstrengelse", 
				LongTextConverter.convert(dosage));
		Assert.assertEquals(
				SimpleAccordingToNeedConverterImpl.class, 
				ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
				"1-2 sug efter behov ved anstrengelse", 
				ShortTextConverter.convert(dosage));
		Assert.assertNull(DailyDosisCalculator.calculate(dosage).getValue());
		Assert.assertEquals(DosageType.AccordingToNeed, DosageTypeCalculator.calculate(dosage));		
	}
	
	@Test /* Dosage like the "Ipren" example */
	public void testDag1Iterationsinterval1() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			StructuresWrapper.makeStructures(
				UnitOrUnitsWrapper.makeUnit("tabletter"), 
				StructureWrapper.makeStructure(
					1, "ved smerter", DateOrDateTimeWrapper.makeDate("2012-04-18"), null, 
					DayWrapper.makeDay(
						1, 
						PlainDoseWrapper.makeDose(new BigDecimal(1), new BigDecimal(2), true), 
						PlainDoseWrapper.makeDose(new BigDecimal(1), new BigDecimal(2), true), 
						PlainDoseWrapper.makeDose(new BigDecimal(1), new BigDecimal(2), true)))));
		Assert.assertEquals(
				"Doseringsforløbet starter onsdag den 18. april 2012 og gentages hver dag:\n"+
				"   Doseringsforløb:\n"+
				"   1-2 tabletter efter behov ved smerter højst 3 gange daglig", 
				LongTextConverter.convert(dosage));
		Assert.assertEquals(
				SimpleLimitedAccordingToNeedConverterImpl.class, 
				ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
				"1-2 tabletter efter behov ved smerter højst 3 gange daglig", 
				ShortTextConverter.convert(dosage));
		Assert.assertNull(DailyDosisCalculator.calculate(dosage).getValue());
		Assert.assertEquals(DosageType.AccordingToNeed, DosageTypeCalculator.calculate(dosage));		
	}	

	@Test /* Dosage like the "Ipren" example, with a minor variation */
	public void testDag0Iterationsinterval1() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			StructuresWrapper.makeStructures(
				UnitOrUnitsWrapper.makeUnit("tabletter"), 
				StructureWrapper.makeStructure(
					1, "ved smerter", DateOrDateTimeWrapper.makeDate("2012-04-18"), null, 
					DayWrapper.makeDay(
						0, 
						PlainDoseWrapper.makeDose(new BigDecimal(1), new BigDecimal(2), true)))));
		Assert.assertEquals(
				"Doseringsforløbet starter onsdag den 18. april 2012 og gentages hver dag:\n"+
				"   Doseringsforløb:\n"+
				"   Efter behov: 1-2 tabletter efter behov ved smerter", 
				LongTextConverter.convert(dosage));
		Assert.assertNull(ShortTextConverter.convert(dosage));
		Assert.assertNull(DailyDosisCalculator.calculate(dosage).getValue());
		Assert.assertEquals(DosageType.AccordingToNeed, DosageTypeCalculator.calculate(dosage));		
	}
		
	@Test /* Test dosage without meaning, this dosage must still be translated */
	public void test012Iterationsinterval0() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			StructuresWrapper.makeStructures(
				UnitOrUnitsWrapper.makeUnit("ml"), 
				StructureWrapper.makeStructure(
					0, "mod smerter", DateOrDateTimeWrapper.makeDate("2012-04-18"), null, 
					DayWrapper.makeDay(
						0, 
						PlainDoseWrapper.makeDose(new BigDecimal(1), true)),
					DayWrapper.makeDay(
						1, 
						PlainDoseWrapper.makeDose(new BigDecimal(20), true),
						PlainDoseWrapper.makeDose(new BigDecimal(20), true)),
					DayWrapper.makeDay(
						2, 
						PlainDoseWrapper.makeDose(new BigDecimal(20), true),
						PlainDoseWrapper.makeDose(new BigDecimal(20), true)))));
		Assert.assertEquals(
				"Doseringsforløbet starter onsdag den 18. april 2012 og ophører efter det angivne forløb.\n"+
				"Bemærk at doseringen varierer og har et komplekst forløb:\n"+
				"   Doseringsforløb:\n"+
				"   Efter behov: 1 ml efter behov mod smerter\n"+
				"   Onsdag den 18. april 2012: 20 ml efter behov mod smerter højst 2 gange daglig\n"+
				"   Torsdag den 19. april 2012: 20 ml efter behov mod smerter højst 2 gange daglig", 
				LongTextConverter.convert(dosage));
		Assert.assertNull(ShortTextConverter.convert(dosage));
		Assert.assertNull(DailyDosisCalculator.calculate(dosage).getValue());
		Assert.assertEquals(DosageType.AccordingToNeed, DosageTypeCalculator.calculate(dosage));		
	}

	@Test /* Test dosage without meaning, must still be translated */
	public void testDag012Iterationsinterval2() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			StructuresWrapper.makeStructures(
				UnitOrUnitsWrapper.makeUnit("sug"), 
				StructureWrapper.makeStructure(
					2, "ved anstrengelse", DateOrDateTimeWrapper.makeDate("2012-04-18"), null, 
					DayWrapper.makeDay(
						0, 
						PlainDoseWrapper.makeDose(new BigDecimal(1), new BigDecimal(2), true)),
					DayWrapper.makeDay(
						1, 
						PlainDoseWrapper.makeDose(new BigDecimal(1), new BigDecimal(2), true)),
					DayWrapper.makeDay(
						2, 
						PlainDoseWrapper.makeDose(new BigDecimal(1), new BigDecimal(2), true)))));
		Assert.assertEquals(
				"Doseringsforløbet starter onsdag den 18. april 2012, forløbet gentages efter 2 dage.\n"+
				"Bemærk at doseringen har et komplekst forløb:\n"+
				"   Doseringsforløb:\n"+
				"   Efter behov: 1-2 sug efter behov ved anstrengelse\n"+
				"   Onsdag den 18. april 2012: 1-2 sug efter behov ved anstrengelse højst 1 gang daglig\n"+
				"   Torsdag den 19. april 2012: 1-2 sug efter behov ved anstrengelse højst 1 gang daglig",
				LongTextConverter.convert(dosage));
		Assert.assertNull(ShortTextConverter.convert(dosage));
		Assert.assertNull(DailyDosisCalculator.calculate(dosage).getValue());
		Assert.assertEquals(DosageType.AccordingToNeed, DosageTypeCalculator.calculate(dosage));		
	}
	
	@Test /* Weekly dosage */ 
	public void testDag0Iterationsinterval7() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			StructuresWrapper.makeStructures(
				UnitOrUnitsWrapper.makeUnit("tabletter"), 
				StructureWrapper.makeStructure(
					7, "ved smerter", DateOrDateTimeWrapper.makeDate("2012-04-18"), null, 
					DayWrapper.makeDay(
						0, 
						PlainDoseWrapper.makeDose(new BigDecimal(1), new BigDecimal(2), true)))));
		Assert.assertEquals(
				"Doseringsforløbet starter onsdag den 18. april 2012, forløbet gentages efter 7 dage:\n"+
				"   Doseringsforløb:\n"+
				"   Efter behov: 1-2 tabletter efter behov ved smerter",
				LongTextConverter.convert(dosage));
		Assert.assertNull(ShortTextConverter.convert(dosage));
		Assert.assertNull(DailyDosisCalculator.calculate(dosage).getValue());
		Assert.assertEquals(DosageType.AccordingToNeed, DosageTypeCalculator.calculate(dosage));		
	}	
	
	
	@Test /* Pure PN dosage */
	public void test1TabletEfterBehov() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			StructuresWrapper.makeStructures(
				UnitOrUnitsWrapper.makeUnit("tablet"), 
				StructureWrapper.makeStructure(
					0, null, DateOrDateTimeWrapper.makeDate("2012-05-29"), null, 
					DayWrapper.makeDay(
						0, 
						PlainDoseWrapper.makeDose(new BigDecimal(1), true)))));
		Assert.assertEquals(
				"Doseringsforløbet starter tirsdag den 29. maj 2012:\n"+
				"   Doseringsforløb:\n"+
				"   Efter behov: 1 tablet efter behov",
				LongTextConverter.convert(dosage));
		Assert.assertEquals("1 tablet efter behov", ShortTextConverter.convert(dosage));
		Assert.assertNull(DailyDosisCalculator.calculate(dosage).getValue());
		Assert.assertEquals(DosageType.AccordingToNeed, DosageTypeCalculator.calculate(dosage));		
	}
	
	@Test /* Pure PN dosage with max */
	public void test1TabletEfterBehovHoejstEnGangDaglig() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			StructuresWrapper.makeStructures(
				UnitOrUnitsWrapper.makeUnit("tablet"), 
				StructureWrapper.makeStructure(
				1, null, DateOrDateTimeWrapper.makeDate("2012-05-29"), null, 
				DayWrapper.makeDay(
					1, 
					PlainDoseWrapper.makeDose(new BigDecimal(1), true)))));
		Assert.assertEquals(
				"Doseringsforløbet starter tirsdag den 29. maj 2012 og gentages hver dag:\n"+
				"   Doseringsforløb:\n"+
				"   1 tablet efter behov højst 1 gang daglig",
				LongTextConverter.convert(dosage));
		Assert.assertEquals("1 tablet efter behov højst 1 gang daglig", ShortTextConverter.convert(dosage));
		Assert.assertNull(DailyDosisCalculator.calculate(dosage).getValue());
		Assert.assertEquals(DosageType.AccordingToNeed, DosageTypeCalculator.calculate(dosage));		
	}
	
	@Test /* Dosage "2 stk efter behov højst 1 gang daglig", see https://jira.trifork.com/browse/FMK-784*/
	public void testJiraFMK784() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			StructuresWrapper.makeStructures(
				UnitOrUnitsWrapper.makeUnit("stk"), 
				StructureWrapper.makeStructure(
					1, null, DateOrDateTimeWrapper.makeDateTime("2012-04-13 20:06:01"), null,
					DayWrapper.makeDay(
						1, 
						PlainDoseWrapper.makeDose(new BigDecimal(2), true)))));
		Assert.assertEquals(
				"Doseringsforløbet starter fredag den 13. april 2012 kl. 20:06:01 og gentages hver dag:\n"+
				"   Doseringsforløb:\n"+
				"   2 stk efter behov højst 1 gang daglig",
				LongTextConverter.convert(dosage));
		Assert.assertEquals(SimpleLimitedAccordingToNeedConverterImpl.class, ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals("2 stk efter behov højst 1 gang daglig", ShortTextConverter.convert(dosage));
		Assert.assertNull(DailyDosisCalculator.calculate(dosage).getValue());
		Assert.assertEquals(DosageType.AccordingToNeed, DosageTypeCalculator.calculate(dosage));		
	}	
	
	@Test /* Dosage "2 stk efter behov højst 2 gange daglig", see https://jira.trifork.com/browse/FMK-784*/
	public void testJiraFMK784Variant() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			StructuresWrapper.makeStructures(
				UnitOrUnitsWrapper.makeUnit("stk"), 
				StructureWrapper.makeStructure(
					1, null, DateOrDateTimeWrapper.makeDateTime("2012-04-13 20:06:00"), null, 
					DayWrapper.makeDay(
						1, 
						PlainDoseWrapper.makeDose(new BigDecimal(2), true), 
						PlainDoseWrapper.makeDose(new BigDecimal(2), true)))));
		Assert.assertEquals(
				"Doseringsforløbet starter fredag den 13. april 2012 kl. 20:06 og gentages hver dag:\n"+
				"   Doseringsforløb:\n"+
				"   2 stk efter behov højst 2 gange daglig",
				LongTextConverter.convert(dosage));
		Assert.assertEquals(SimpleLimitedAccordingToNeedConverterImpl.class, ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals("2 stk efter behov højst 2 gange daglig", ShortTextConverter.convert(dosage));
		Assert.assertNull(DailyDosisCalculator.calculate(dosage).getValue());
		Assert.assertEquals(DosageType.AccordingToNeed, DosageTypeCalculator.calculate(dosage));		
	}

    @Test /* Jira FMK-784 */
    public void testSecondsAreNotIncluded() {
        DosageWrapper dosage = DosageWrapper.makeDosage(
                StructuresWrapper.makeStructures(
                        UnitOrUnitsWrapper.makeUnit("stk"),
                        StructureWrapper.makeStructure(
                                1, null, DateOrDateTimeWrapper.makeDateTime("2012-04-13 20:06:00"), null,
                                DayWrapper.makeDay(
                                        1,
                                        PlainDoseWrapper.makeDose(new BigDecimal(2), true),
                                        PlainDoseWrapper.makeDose(new BigDecimal(2), true)))));
        Assert.assertEquals(
                "Doseringsforløbet starter fredag den 13. april 2012 kl. 20:06 og gentages hver dag:\n"+
                        "   Doseringsforløb:\n"+
                        "   2 stk efter behov højst 2 gange daglig",
                LongTextConverter.convert(dosage));
    }

    @Test /* Jira FMK-784 */
    public void testSecondsAreIncludedWhenProvided() {
        DosageWrapper dosage = DosageWrapper.makeDosage(
                StructuresWrapper.makeStructures(
                        UnitOrUnitsWrapper.makeUnit("stk"),
                        StructureWrapper.makeStructure(
                                1, null, DateOrDateTimeWrapper.makeDateTime("2012-04-13 20:06:10"), null,
                                DayWrapper.makeDay(
                                        1,
                                        PlainDoseWrapper.makeDose(new BigDecimal(2), true),
                                        PlainDoseWrapper.makeDose(new BigDecimal(2), true)))));
        Assert.assertEquals(
                "Doseringsforløbet starter fredag den 13. april 2012 kl. 20:06:10 og gentages hver dag:\n"+
                        "   Doseringsforløb:\n"+
                        "   2 stk efter behov højst 2 gange daglig",
                LongTextConverter.convert(dosage));
    }
		
}