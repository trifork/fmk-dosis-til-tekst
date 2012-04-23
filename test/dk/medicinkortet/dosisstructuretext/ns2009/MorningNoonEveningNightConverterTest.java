package dk.medicinkortet.dosisstructuretext.ns2009;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

import dk.medicinkortet.dosisstructuretext.DailyDosisCalculator;
import dk.medicinkortet.dosisstructuretext.LongTextConverter;
import dk.medicinkortet.dosisstructuretext.ShortTextConverter;
import dk.medicinkortet.dosisstructuretext.TestHelper;
import dk.medicinkortet.dosisstructuretext.converterimpl.MorningNoonEveningNightConverterImpl;
import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.EveningDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.MorningDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.NightDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.NoonDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuredDosageWrapper;

public class MorningNoonEveningNightConverterTest {
	
	@Test
	public void testMorningNoonEveningAndNight() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			StructuredDosageWrapper.makeStructuredDosage(
				1, "stk", "ved måltid", TestHelper.toDate("2011-01-01"), TestHelper.toDate("2011-01-30"),
				DayWrapper.makeDay(
					1, 
					MorningDoseWrapper.makeDose(new BigDecimal(1)), 
					NoonDoseWrapper.makeDose(new BigDecimal(2)), 
					EveningDoseWrapper.makeDose(new BigDecimal(3)), 
					NightDoseWrapper.makeDose(new BigDecimal(4))))); 
		Assert.assertEquals(
			"Doseringsforløbet starter lørdag den 1. januar 2011 og gentages dagligt:\n"+
			"   Doseringsforløb:\n"+
			"   Lørdag den 1. januar 2011: 1 stk morgen ved måltid + 2 stk middag ved måltid + 3 stk aften ved måltid + 4 stk nat ved måltid",
			LongTextConverter.convert(dosage));
		Assert.assertEquals(
			MorningNoonEveningNightConverterImpl.class, 
			ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
			"1 stk morgen, 2 stk middag, 3 stk aften og 4 stk nat ved måltid", 
			ShortTextConverter.convert(dosage));
		Assert.assertEquals(
			10.0, 
			DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
			0.000000001); 				
	}

	@Test
	public void testMorningNoonEveningAndNightWithEqualDoses() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			StructuredDosageWrapper.makeStructuredDosage(
				1, "stk", "ved måltid", TestHelper.toDate("2011-01-01"), TestHelper.toDate("2011-01-30"),
				DayWrapper.makeDay(
					1, 
					MorningDoseWrapper.makeDose(new BigDecimal(2)), 
					NoonDoseWrapper.makeDose(new BigDecimal(2)), 
					EveningDoseWrapper.makeDose(new BigDecimal(2)), 
					NightDoseWrapper.makeDose(new BigDecimal(2))))); 
		Assert.assertEquals(
			"Doseringsforløbet starter lørdag den 1. januar 2011 og gentages dagligt:\n"+
			"   Doseringsforløb:\n"+
			"   Lørdag den 1. januar 2011: 2 stk morgen ved måltid + 2 stk middag ved måltid + 2 stk aften ved måltid + 2 stk nat ved måltid",
			LongTextConverter.convert(dosage));
		Assert.assertEquals(
			MorningNoonEveningNightConverterImpl.class, 
			ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
			"2 stk morgen, middag, aften og nat ved måltid", 
			ShortTextConverter.convert(dosage));
		Assert.assertEquals(
				8.0, 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001); 				
	}
	
	@Test
	public void testNoonEveningAndNight() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			StructuredDosageWrapper.makeStructuredDosage(
				1, "stk", "ved måltid", TestHelper.toDate("2011-01-01"), TestHelper.toDate("2011-01-30"),
				DayWrapper.makeDay(
					1, 
					NoonDoseWrapper.makeDose(new BigDecimal(2)), 
					EveningDoseWrapper.makeDose(new BigDecimal(3)), 
					NightDoseWrapper.makeDose(new BigDecimal(4))))); 
		Assert.assertEquals(
			"Doseringsforløbet starter lørdag den 1. januar 2011 og gentages dagligt:\n"+
			"   Doseringsforløb:\n"+
			"   Lørdag den 1. januar 2011: 2 stk middag ved måltid + 3 stk aften ved måltid + 4 stk nat ved måltid",
			LongTextConverter.convert(dosage));
		Assert.assertEquals(
			MorningNoonEveningNightConverterImpl.class, 
			ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
			"2 stk middag, 3 stk aften og 4 stk nat ved måltid", 
			ShortTextConverter.convert(dosage));
		Assert.assertEquals(
				9.0, 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001); 				
	}
	
	@Test
	public void testMorningNoonAndEvening() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			StructuredDosageWrapper.makeStructuredDosage(
				1, "stk", "ved måltid", TestHelper.toDate("2011-01-01"), TestHelper.toDate("2011-01-30"),
				DayWrapper.makeDay(
					1, 
					MorningDoseWrapper.makeDose(new BigDecimal(1)), 
					NoonDoseWrapper.makeDose(new BigDecimal(2)), 
					EveningDoseWrapper.makeDose(new BigDecimal(3))))); 
		Assert.assertEquals(
			"Doseringsforløbet starter lørdag den 1. januar 2011 og gentages dagligt:\n"+
			"   Doseringsforløb:\n"+
			"   Lørdag den 1. januar 2011: 1 stk morgen ved måltid + 2 stk middag ved måltid + 3 stk aften ved måltid",
			LongTextConverter.convert(dosage));
		Assert.assertEquals(
			MorningNoonEveningNightConverterImpl.class, 
			ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
			"1 stk morgen, 2 stk middag og 3 stk aften ved måltid", 
			ShortTextConverter.convert(dosage));
		Assert.assertEquals(
				6.0, 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001); 				
	}

	@Test
	public void testMorningAndNoonWithZeroIntervals() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			StructuredDosageWrapper.makeStructuredDosage(
				1, "stk", "ved måltid", TestHelper.toDate("2011-01-01"), TestHelper.toDate("2011-01-30"),
				DayWrapper.makeDay(
					1, 
					MorningDoseWrapper.makeDose(new BigDecimal(0), new BigDecimal(1)), 
					NoonDoseWrapper.makeDose(new BigDecimal(2), new BigDecimal(3))))); 
		Assert.assertEquals(
			"Doseringsforløbet starter lørdag den 1. januar 2011 og gentages dagligt:\n"+
			"   Doseringsforløb:\n"+
			"   Lørdag den 1. januar 2011: 0-1 stk morgen ved måltid + 2-3 stk middag ved måltid",
			LongTextConverter.convert(dosage));
		Assert.assertEquals(
			MorningNoonEveningNightConverterImpl.class, 
			ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
			"0-1 stk morgen og 2-3 stk middag ved måltid", 
			ShortTextConverter.convert(dosage));
		Assert.assertEquals(
				2, 
				DailyDosisCalculator.calculate(dosage).getInterval().getMinimum().doubleValue(), 
				0.000000001); 				
		Assert.assertEquals(
				4, 
				DailyDosisCalculator.calculate(dosage).getInterval().getMaximum().doubleValue(), 
				0.000000001); 				
	}
	
	public void testNight() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			StructuredDosageWrapper.makeStructuredDosage(
				1, "stk", "ved måltid", TestHelper.toDate("2011-01-01"), TestHelper.toDate("2011-01-30"),
				DayWrapper.makeDay(
					1, 
					NightDoseWrapper.makeDose(new BigDecimal(2))))); 				
		Assert.assertEquals(
			"Daglig 2 stk nat ved måltid", 
			LongTextConverter.convert(dosage));
		Assert.assertEquals(
			MorningNoonEveningNightConverterImpl.class, 
			ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
			"2 stk nat ved måltid", 
			ShortTextConverter.convert(dosage));
		Assert.assertEquals(
				2.0, 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001); 				
	}

	@Test
	public void test1DråbeMiddagOgAften() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			StructuredDosageWrapper.makeStructuredDosage(
				1, "dråber", null, TestHelper.toDate("2011-01-01"), TestHelper.toDate("2011-01-30"),
				DayWrapper.makeDay(
					1, 
					NoonDoseWrapper.makeDose(new BigDecimal(1)), 
					EveningDoseWrapper.makeDose(new BigDecimal(1))))); 				
		Assert.assertEquals(
			"Doseringsforløbet starter lørdag den 1. januar 2011 og gentages dagligt:\n"+
			"   Doseringsforløb:\n"+
			"   Lørdag den 1. januar 2011: 1 dråbe middag + 1 dråbe aften",
			LongTextConverter.convert(dosage));
		Assert.assertEquals(
			MorningNoonEveningNightConverterImpl.class, 
			ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
			"1 dråbe middag og aften", 
			ShortTextConverter.convert(dosage));
		Assert.assertEquals(
				2.0, 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001); 				
	}

	@Test
	public void test1DråbeAftenOgNat() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			StructuredDosageWrapper.makeStructuredDosage(
				1, "dråber", null, TestHelper.toDate("2011-01-01"), TestHelper.toDate("2011-01-30"),
				DayWrapper.makeDay(
					1, 
					EveningDoseWrapper.makeDose(new BigDecimal(1)), 
					NightDoseWrapper.makeDose(new BigDecimal(1))))); 				
		Assert.assertEquals(
			"Doseringsforløbet starter lørdag den 1. januar 2011 og gentages dagligt:\n"+
			"   Doseringsforløb:\n"+
			"   Lørdag den 1. januar 2011: 1 dråbe aften + 1 dråbe nat",
			LongTextConverter.convert(dosage));
		Assert.assertEquals(
			MorningNoonEveningNightConverterImpl.class, 
			ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
			"1 dråbe aften og nat", 
			ShortTextConverter.convert(dosage));
		Assert.assertEquals(
				2.0, 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001); 				
	}

	@Test
	public void test1DråbeNat() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			StructuredDosageWrapper.makeStructuredDosage(
				1, "dråber", null, TestHelper.toDate("2011-01-01"), TestHelper.toDate("2011-01-30"),
				DayWrapper.makeDay(
					1, 
					NightDoseWrapper.makeDose(new BigDecimal(1))))); 				
		Assert.assertEquals(
			"Doseringsforløbet starter lørdag den 1. januar 2011 og gentages dagligt:\n"+
			"   Doseringsforløb:\n"+
			"   Lørdag den 1. januar 2011: 1 dråbe nat",
			LongTextConverter.convert(dosage));
		Assert.assertEquals(
			MorningNoonEveningNightConverterImpl.class, 
			ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
			"1 dråbe nat", 
			ShortTextConverter.convert(dosage));
		Assert.assertEquals(
				1.0, 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001); 				
	}

	@Test
	public void test400MilligramNat() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			StructuredDosageWrapper.makeStructuredDosage(
				1, "milligram", null, TestHelper.toDate("2011-01-01"), TestHelper.toDate("2011-01-30"),
				DayWrapper.makeDay(
					1, 
					NightDoseWrapper.makeDose(new BigDecimal(400.000))))); 				
		Assert.assertEquals(
			"Doseringsforløbet starter lørdag den 1. januar 2011 og gentages dagligt:\n"+
			"   Doseringsforløb:\n"+
			"   Lørdag den 1. januar 2011: 400 milligram nat",
			LongTextConverter.convert(dosage));
		Assert.assertEquals(
			MorningNoonEveningNightConverterImpl.class, 
			ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
			"400 milligram nat", 
			ShortTextConverter.convert(dosage));
		Assert.assertEquals(
				400.0, 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001); 				
	}
	
}
