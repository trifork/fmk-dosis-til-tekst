package dk.medicinkortet.dosisstructuretext.ns2009;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

import dk.medicinkortet.dosisstructuretext.DailyDosisCalculator;
import dk.medicinkortet.dosisstructuretext.LongTextConverter;
import dk.medicinkortet.dosisstructuretext.ShortTextConverter;
import dk.medicinkortet.dosisstructuretext.TestHelper;
import dk.medicinkortet.dosisstructuretext.vowrapper.AccordingToNeedDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.EveningDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.MorningDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.NoonDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuredDosageWrapper;

public class LongTextComplexConverterTest {

	@Test /* 1 tablet morgen */
	public void test1TabletMorgen() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			StructuredDosageWrapper.makeStructuredDosage(
				1, "tablet", null, TestHelper.toDate("2012-04-18"), null, 
				DayWrapper.makeDay(
					1, 
					MorningDoseWrapper.makeDose(new BigDecimal(1)))));
		Assert.assertEquals(
				"Doseringsforløbet starter onsdag den 18. april 2012 og gentages dagligt:\n"+
				"   Doseringsforløb:\n"+
				"   Onsdag den 18. april 2012: 1 tablet morgen", 
				LongTextConverter.convert(dosage));
//		System.out.println("\n\n\"1 tablet morgen\":\n"+LongTextConverter.convert(dosage));
		Assert.assertEquals("1 tablet morgen", ShortTextConverter.convert(dosage));
		Assert.assertEquals(
				1, 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001); 				
	}

	@Test /* Hjerdyl */
	public void testHjerdyl() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			StructuredDosageWrapper.makeStructuredDosage(
				2, "tablet", null, TestHelper.toDate("2012-04-18"), null, 
				DayWrapper.makeDay(
					1, 
					MorningDoseWrapper.makeDose(new BigDecimal(1))), 
				DayWrapper.makeDay(
					2, 
					MorningDoseWrapper.makeDose(new BigDecimal(1)), 
					EveningDoseWrapper.makeDose(new BigDecimal(1)))));
		Assert.assertEquals(
				"Doseringsforløbet starter onsdag den 18. april 2012, forløbet gentages efter 2 dage.\n"+
				"Bemærk at doseringen varierer:\n"+
				"   Doseringsforløb:\n"+
				"   Onsdag den 18. april 2012: 1 tablet morgen\n"+
				"   Torsdag den 19. april 2012: 1 tablet morgen + 1 tablet aften",			
				LongTextConverter.convert(dosage));
		System.out.println("\n\n\"Hjerdyl\":\n"+LongTextConverter.convert(dosage));
		Assert.assertNull(ShortTextConverter.convert(dosage));
		Assert.assertEquals(
				1.5, 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001); 				
	}		
	
	@Test /* Alendronat */
	public void testAledronat() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			StructuredDosageWrapper.makeStructuredDosage(
				7, "tablet", null, TestHelper.toDate("2012-04-18"), null, 
				DayWrapper.makeDay(
					1, 
					MorningDoseWrapper.makeDose(new BigDecimal(1)))));
		Assert.assertEquals(
				"Doseringsforløbet starter onsdag den 18. april 2012, forløbet gentages efter 7 dage:\n"+
				"   Doseringsforløb:\n"+
				"   Onsdag den 18. april 2012: 1 tablet morgen",
				LongTextConverter.convert(dosage));
		System.out.println("\n\n\"Alendronat\":\n"+LongTextConverter.convert(dosage));
		Assert.assertNull(ShortTextConverter.convert(dosage));
		Assert.assertEquals(
				1/7.0, 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001); 				
	}
	
	@Test /* Marevan ugeskema 1 tablet */
	public void testMarevanUgeskema1Tablet() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			StructuredDosageWrapper.makeStructuredDosage(
				7, "tablet", null, TestHelper.toDate("2012-04-18"), null, 
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
					MorningDoseWrapper.makeDose(new BigDecimal(1)))));
		Assert.assertEquals(
				"Doseringsforløbet starter onsdag den 18. april 2012, forløbet gentages efter 7 dage.\n"+
				"Bemærk at doseringen har et komplekst forløb:\n"+
				"   Doseringsforløb:\n"+
				"   Onsdag den 18. april 2012: 1 tablet morgen\n"+
				"   Fredag den 20. april 2012: 1 tablet morgen\n"+
				"   Søndag den 22. april 2012: 1 tablet morgen\n"+
				"   Tirsdag den 24. april 2012: 1 tablet morgen", 
				LongTextConverter.convert(dosage));
		System.out.println("\n\n\"Marevan ugeskema 1 tablet\":\n"+LongTextConverter.convert(dosage));
		Assert.assertNull(ShortTextConverter.convert(dosage));
		Assert.assertEquals(
				4/7.0, 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001); 				
	}
	
	@Test /* Marevan ugeskema 2 tabletter */
	public void testMarevanUgeskema2Tabletter() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			StructuredDosageWrapper.makeStructuredDosage(
				7, "tabletter", null, TestHelper.toDate("2012-04-19"), null, 
				DayWrapper.makeDay(
					1, 
					MorningDoseWrapper.makeDose(new BigDecimal(2))), 
				DayWrapper.makeDay(
					3, 
					MorningDoseWrapper.makeDose(new BigDecimal(2))), 
				DayWrapper.makeDay(
					5, 
					MorningDoseWrapper.makeDose(new BigDecimal(2)))));
		Assert.assertEquals(
				"Doseringsforløbet starter torsdag den 19. april 2012, forløbet gentages efter 7 dage.\n"+
				"Bemærk at doseringen har et komplekst forløb:\n"+
				"   Doseringsforløb:\n"+
				"   Torsdag den 19. april 2012: 2 tabletter morgen\n"+
				"   Lørdag den 21. april 2012: 2 tabletter morgen\n"+
				"   Mandag den 23. april 2012: 2 tabletter morgen",
				LongTextConverter.convert(dosage));
		System.out.println("\n\n\"Marevan ugeskema 2 tabletter\":\n"+LongTextConverter.convert(dosage));
		Assert.assertNull(ShortTextConverter.convert(dosage));
		Assert.assertEquals(
				6/7.0, 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001); 				
	}	
	
	@Test /* Naragan ugeskema 1 tablet */
	public void testNaraganUgeskema1Tablet() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			StructuredDosageWrapper.makeStructuredDosage(
				7, "tablet", null, TestHelper.toDate("2012-04-18"), null, 
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
					MorningDoseWrapper.makeDose(new BigDecimal(1)))));
		Assert.assertEquals(
				"Doseringsforløbet starter onsdag den 18. april 2012, forløbet gentages efter 7 dage.\n"+
				"Bemærk at doseringen har et komplekst forløb:\n"+
				"   Doseringsforløb:\n"+
				"   Onsdag den 18. april 2012: 1 tablet morgen\n"+
				"   Fredag den 20. april 2012: 1 tablet morgen\n"+
				"   Søndag den 22. april 2012: 1 tablet morgen\n"+
				"   Tirsdag den 24. april 2012: 1 tablet morgen",
				LongTextConverter.convert(dosage));
		System.out.println("\n\n\"Naragan ugeskema 1 tablet\":\n"+LongTextConverter.convert(dosage));
		Assert.assertNull(ShortTextConverter.convert(dosage));
		Assert.assertEquals(
				4/7.0, 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001); 				
	}
	
	@Test /* Naragan ugeskema 2 tabletter */
	public void testNaraganUgeskema2Tabletter() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			StructuredDosageWrapper.makeStructuredDosage(
				7, "tabletter", null, TestHelper.toDate("2012-04-18"), null, 
				DayWrapper.makeDay(
					2, 
					MorningDoseWrapper.makeDose(new BigDecimal(2))), 
				DayWrapper.makeDay(
					4, 
					MorningDoseWrapper.makeDose(new BigDecimal(2))), 
				DayWrapper.makeDay(
					6, 
					MorningDoseWrapper.makeDose(new BigDecimal(2)))));
		Assert.assertEquals(
				"Doseringsforløbet starter onsdag den 18. april 2012, forløbet gentages efter 7 dage.\n"+
				"Bemærk at doseringen har et komplekst forløb:\n"+
				"   Doseringsforløb:\n"+
				"   Torsdag den 19. april 2012: 2 tabletter morgen\n"+
				"   Lørdag den 21. april 2012: 2 tabletter morgen\n"+
				"   Mandag den 23. april 2012: 2 tabletter morgen",
				LongTextConverter.convert(dosage));		
		System.out.println("\n\n\"Naragan ugeskema 2 tabletter\":\n"+LongTextConverter.convert(dosage));
		Assert.assertNull(ShortTextConverter.convert(dosage));
		Assert.assertEquals(
				6/7.0, 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001); 				
	}	
	
	@Test /* Morfin nedtrapning */
	public void testMorfinNedtrapning() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			StructuredDosageWrapper.makeStructuredDosage(
				0, "stk", null, TestHelper.toDate("2012-04-18"), null, 
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
					EveningDoseWrapper.makeDose(new BigDecimal(1)))));
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
//		SchemaConverter.convert(dosage);
		Assert.assertNull(ShortTextConverter.convert(dosage));
		System.out.println("\n\n\"Morfin nedtrapning\":\n"+LongTextConverter.convert(dosage));
		Assert.assertNull(
				DailyDosisCalculator.calculate(dosage).getValue());
	}		
	
	@Test 
	public void testDag0Iterationsinterval0() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			StructuredDosageWrapper.makeStructuredDosage(
				0, "sug", "ved anstrengelse", TestHelper.toDate("2012-04-18"), null, 
				DayWrapper.makeDay(
					0, 
					AccordingToNeedDoseWrapper.makeDose(new BigDecimal(1), new BigDecimal(2)))));
		Assert.assertEquals(
				"Doseringsforløbet starter onsdag den 18. april 2012:\n"+ 
				"   Doseringsforløb:\n"+
				"   Efter behov: 1-2 sug efter behov ved anstrengelse", 
				LongTextConverter.convert(dosage));
		System.out.println("\n\n\"Pulmicort 1-2 sug efter behov ved anstrengelse\":\n"+LongTextConverter.convert(dosage));
		Assert.assertEquals(
				"1-2 sug efter behov ved anstrengelse", 
				ShortTextConverter.convert(dosage));
		Assert.assertNull(DailyDosisCalculator.calculate(dosage).getValue()); 
	}

	@Test 
	public void testDag0Iterationsinterval1() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			StructuredDosageWrapper.makeStructuredDosage(
				1, "tabletter", "ved smerter", TestHelper.toDate("2012-04-18"), null, 
				DayWrapper.makeDay(
					0, 
					AccordingToNeedDoseWrapper.makeDose(new BigDecimal(1), new BigDecimal(2)))));
		Assert.assertEquals(
				"Doseringsforløbet starter onsdag den 18. april 2012 og gentages dagligt:\n"+
				"   Doseringsforløb:\n"+
				"   Efter behov: 1-2 tabletter efter behov ved smerter", 
				LongTextConverter.convert(dosage));
		Assert.assertNull(ShortTextConverter.convert(dosage));
		Assert.assertNull(DailyDosisCalculator.calculate(dosage).getValue()); 
	}
	
	@Test 
	public void testDag0Iterationsinterval1_1() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			StructuredDosageWrapper.makeStructuredDosage(
				1, "tabletter", "ved smerter", TestHelper.toDate("2012-04-18"), null, 
				DayWrapper.makeDay(
					0, 
					AccordingToNeedDoseWrapper.makeDose(new BigDecimal(1), new BigDecimal(2)), 
					AccordingToNeedDoseWrapper.makeDose(new BigDecimal(1), new BigDecimal(2)), 
					AccordingToNeedDoseWrapper.makeDose(new BigDecimal(1), new BigDecimal(2)))));
		Assert.assertEquals(
				"Doseringsforløbet starter onsdag den 18. april 2012 og gentages dagligt:\n"+
				"   Doseringsforløb:\n"+
				"   Efter behov: 1-2 tabletter efter behov ved smerter højst 3 gange", 
				LongTextConverter.convert(dosage));
		System.out.println("\n\n\"Ipren : 1-2 tabletter efter behov ved smerter højst 3 gange\":\n"+LongTextConverter.convert(dosage));
		Assert.assertNull(ShortTextConverter.convert(dosage));
		Assert.assertNull(DailyDosisCalculator.calculate(dosage).getValue()); 
	}	
	
	@Test 
	public void testDag0Iterationsinterval7() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			StructuredDosageWrapper.makeStructuredDosage(
				7, "tabletter", "ved smerter", TestHelper.toDate("2012-04-18"), null, 
				DayWrapper.makeDay(
					0, 
					AccordingToNeedDoseWrapper.makeDose(new BigDecimal(1), new BigDecimal(2)))));
		Assert.assertEquals(
				"Doseringsforløbet starter onsdag den 18. april 2012, forløbet gentages efter 7 dage:\n"+
				"   Doseringsforløb:\n"+
				"   Efter behov: 1-2 tabletter efter behov ved smerter",
				LongTextConverter.convert(dosage));
		Assert.assertNull(ShortTextConverter.convert(dosage));
		Assert.assertNull(DailyDosisCalculator.calculate(dosage).getValue()); 				
	}	
	
	@Test 
	public void testWeirdnessDag012Iterationsinterval0() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			StructuredDosageWrapper.makeStructuredDosage(
				0, "ml", "mod smerter", TestHelper.toDate("2012-04-18"), null, 
				DayWrapper.makeDay(
					0, 
					AccordingToNeedDoseWrapper.makeDose(new BigDecimal(1))),
				DayWrapper.makeDay(
					1, 
					AccordingToNeedDoseWrapper.makeDose(new BigDecimal(20)),
					AccordingToNeedDoseWrapper.makeDose(new BigDecimal(20))),
				DayWrapper.makeDay(
					2, 
					AccordingToNeedDoseWrapper.makeDose(new BigDecimal(20)),
					AccordingToNeedDoseWrapper.makeDose(new BigDecimal(20)))));
		Assert.assertEquals(
				"Doseringsforløbet starter onsdag den 18. april 2012 og ophører efter det angivne forløb.\n"+
				"Bemærk at doseringen varierer og har et komplekst forløb:\n"+
				"   Doseringsforløb:\n"+
				"   Efter behov: 1 ml efter behov mod smerter\n"+
				"   Onsdag den 18. april 2012: 20 ml efter behov mod smerter højst 2 gange\n"+
				"   Torsdag den 19. april 2012: 20 ml efter behov mod smerter højst 2 gange", 
				LongTextConverter.convert(dosage));
//		System.out.println("\n\n\"(Ny) dosering som måske ikke giver så meget mening?\":\n"+LongTextConverter.convert(dosage));
		Assert.assertNull(ShortTextConverter.convert(dosage));
		Assert.assertNull(DailyDosisCalculator.calculate(dosage).getValue()); 
	}

	@Test 
	public void testWeirdnessDag012Iterationsinterval2() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			StructuredDosageWrapper.makeStructuredDosage(
				2, "sug", "ved anstrengelse", TestHelper.toDate("2012-04-18"), null, 
				DayWrapper.makeDay(
					0, 
					AccordingToNeedDoseWrapper.makeDose(new BigDecimal(1), new BigDecimal(2))),
				DayWrapper.makeDay(
					1, 
					AccordingToNeedDoseWrapper.makeDose(new BigDecimal(1), new BigDecimal(2))),
				DayWrapper.makeDay(
					2, 
					AccordingToNeedDoseWrapper.makeDose(new BigDecimal(1), new BigDecimal(2)))));
		Assert.assertEquals(
				"Doseringsforløbet starter onsdag den 18. april 2012, forløbet gentages efter 2 dage.\n"+
				"Bemærk at doseringen har et komplekst forløb:\n"+
				"   Doseringsforløb:\n"+
				"   Efter behov: 1-2 sug efter behov ved anstrengelse\n"+
				"   Onsdag den 18. april 2012: 1-2 sug efter behov ved anstrengelse\n"+
				"   Torsdag den 19. april 2012: 1-2 sug efter behov ved anstrengelse",
				LongTextConverter.convert(dosage));
		Assert.assertNull(ShortTextConverter.convert(dosage));
		Assert.assertNull(DailyDosisCalculator.calculate(dosage).getValue()); 
	}
	
	
}
