package dk.medicinkortet.dosisstructuretext.ns2008;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

import dk.medicinkortet.dosisstructuretext.LongTextConverter;
import dk.medicinkortet.dosisstructuretext.ShortTextConverter;
import dk.medicinkortet.dosisstructuretext.TestHelper;
import dk.medicinkortet.dosisstructuretext.converterimpl.LimitedNumberOfDaysConverterImpl;
import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuredDosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.PlainDoseWrapper;

public class LimitedNumberOfDaysConverterTest {
	
	@Test
	public void test4Stk2GangeDagligI3DageVedMaaltid() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			StructuredDosageWrapper.makeStructuredDosage(
				0, "stk", "ved måltid", TestHelper.toDate("2011-01-01"), TestHelper.toDate("2011-01-03"),
				DayWrapper.makeDay(
					1, 
					PlainDoseWrapper.makeDose(new BigDecimal(4)), 
					PlainDoseWrapper.makeDose(new BigDecimal(4))), 
				DayWrapper.makeDay(
					2, 
					PlainDoseWrapper.makeDose(new BigDecimal(4)), 
					PlainDoseWrapper.makeDose(new BigDecimal(4))), 
				DayWrapper.makeDay(
					3, 
					PlainDoseWrapper.makeDose(new BigDecimal(4)), 
					PlainDoseWrapper.makeDose(new BigDecimal(4))), 
				DayWrapper.makeDay(
					4, 
					PlainDoseWrapper.makeDose(new BigDecimal(4)), 
					PlainDoseWrapper.makeDose(new BigDecimal(4))))); 
		Assert.assertEquals(
				"Doseringsforløbet starter lørdag den 1. januar 2011 og ophører efter det angivne forløb:\n"+
				"   Doseringsforløb:\n"+
				"   Lørdag den 1. januar 2011: 4 stk ved måltid 2 gange\n"+
				"   Søndag den 2. januar 2011: 4 stk ved måltid 2 gange\n"+
				"   Mandag den 3. januar 2011: 4 stk ved måltid 2 gange\n"+
				"   Tirsdag den 4. januar 2011: 4 stk ved måltid 2 gange",
				LongTextConverter.convert(dosage));
		Assert.assertEquals(
			LimitedNumberOfDaysConverterImpl.class, 
			ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
			"4 stk 2 gange daglig i 4 dage ved måltid", 
			ShortTextConverter.convert(dosage));
		// assertEquals(10.0, result.getAvgDailyDosis().doubleValue()); 				
	}

	@Test
	public void test4Til6Stk2GangeDagligI3DageVedMaaltid() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			StructuredDosageWrapper.makeStructuredDosage(
				0, "stk", "ved måltid", TestHelper.toDate("2011-01-01"), TestHelper.toDate("2011-01-03"),
				DayWrapper.makeDay(
					1, 
					PlainDoseWrapper.makeDose(new BigDecimal(4), new BigDecimal(6)), 
					PlainDoseWrapper.makeDose(new BigDecimal(4), new BigDecimal(6))), 
				DayWrapper.makeDay(
					2, 
					PlainDoseWrapper.makeDose(new BigDecimal(4), new BigDecimal(6)), 
					PlainDoseWrapper.makeDose(new BigDecimal(4), new BigDecimal(6))), 
				DayWrapper.makeDay(
					3, 
					PlainDoseWrapper.makeDose(new BigDecimal(4), new BigDecimal(6)), 
					PlainDoseWrapper.makeDose(new BigDecimal(4), new BigDecimal(6))), 
				DayWrapper.makeDay(
					4, 
					PlainDoseWrapper.makeDose(new BigDecimal(4), new BigDecimal(6)), 
					PlainDoseWrapper.makeDose(new BigDecimal(4), new BigDecimal(6))))); 
		Assert.assertEquals(
				"Doseringsforløbet starter lørdag den 1. januar 2011 og ophører efter det angivne forløb:\n"+
				"   Doseringsforløb:\n"+
				"   Lørdag den 1. januar 2011: 4-6 stk ved måltid 2 gange\n"+
				"   Søndag den 2. januar 2011: 4-6 stk ved måltid 2 gange\n"+
				"   Mandag den 3. januar 2011: 4-6 stk ved måltid 2 gange\n"+
				"   Tirsdag den 4. januar 2011: 4-6 stk ved måltid 2 gange",
			LongTextConverter.convert(dosage));
		Assert.assertEquals(
			LimitedNumberOfDaysConverterImpl.class, 
			ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
			"4-6 stk 2 gange daglig i 4 dage ved måltid", 
			ShortTextConverter.convert(dosage));
		// assertEquals(10.0, result.getAvgDailyDosis().doubleValue()); 				
	}
	
	@Test
	public void testRejected() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			StructuredDosageWrapper.makeStructuredDosage(
				0, "stk", "ved måltid", TestHelper.toDate("2011-01-01"), TestHelper.toDate("2011-01-03"),
				DayWrapper.makeDay(
					1, 
					PlainDoseWrapper.makeDose(new BigDecimal(4)), 
					PlainDoseWrapper.makeDose(new BigDecimal(4))), 
				DayWrapper.makeDay(
					2, 
					PlainDoseWrapper.makeDose(new BigDecimal(4)), 
					PlainDoseWrapper.makeDose(new BigDecimal(4))), 
				DayWrapper.makeDay(
					3, 
					PlainDoseWrapper.makeDose(new BigDecimal(4)), 
					PlainDoseWrapper.makeDose(new BigDecimal(4))), 
				DayWrapper.makeDay(
					4, 
					PlainDoseWrapper.makeDose(new BigDecimal(4)), 
					PlainDoseWrapper.makeDose(new BigDecimal(1))))); 				
		Assert.assertEquals(
				"Doseringsforløbet starter lørdag den 1. januar 2011 og ophører efter det angivne forløb:\n"+
				"   Doseringsforløb:\n"+
				"   Lørdag den 1. januar 2011: 4 stk ved måltid 2 gange\n"+
				"   Søndag den 2. januar 2011: 4 stk ved måltid 2 gange\n"+
				"   Mandag den 3. januar 2011: 4 stk ved måltid 2 gange\n"+
				"   Tirsdag den 4. januar 2011: 4 stk ved måltid + 1 stk ved måltid",
				LongTextConverter.convert(dosage));
		Assert.assertNull(
				ShortTextConverter.getConverterClass(dosage));
		// assertEquals(10.0, result.getAvgDailyDosis().doubleValue()); 				
	}
	
}
