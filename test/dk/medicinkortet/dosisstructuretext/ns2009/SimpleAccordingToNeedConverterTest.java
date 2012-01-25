package dk.medicinkortet.dosisstructuretext.ns2009;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

import dk.medicinkortet.dosisstructuretext.DailyDosisCalculator;
import dk.medicinkortet.dosisstructuretext.LongTextConverter;
import dk.medicinkortet.dosisstructuretext.ShortTextConverter;
import dk.medicinkortet.dosisstructuretext.TestHelper;
import dk.medicinkortet.dosisstructuretext.converterimpl.SimpleAccordingToNeedConverterImpl;
import dk.medicinkortet.dosisstructuretext.vowrapper.AccordingToNeedDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuredDosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;

public class SimpleAccordingToNeedConverterTest {
	
	@Test
	public void test2StkEfterBehov() throws Exception {
		DosageWrapper dosage = 
			DosageWrapper.makeStructuredDosage(
				StructuredDosageWrapper.makeStructuredDosage(
					0, 
					"stk", 
					null,
					TestHelper.toDate("2011-01-01"), TestHelper.toDate("2011-01-11"), 
					DayWrapper.makeDay(0,
						AccordingToNeedDoseWrapper.makeDose(new BigDecimal(2)))));
		Assert.assertEquals(
				"2 stk efter behov", 
				LongTextConverter.convert(dosage));
		Assert.assertEquals(
				SimpleAccordingToNeedConverterImpl.class, 
				ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
				"2 stk efter behov", 
				ShortTextConverter.convert(dosage));
		Assert.assertTrue(DailyDosisCalculator.calculate(dosage).isNone()); 
	}

	@Test
	public void test2StkEfterBehovVedSmerter() throws Exception {
		DosageWrapper dosage = 
			DosageWrapper.makeStructuredDosage(
				StructuredDosageWrapper.makeStructuredDosage(
					0, 
					"stk", 
					"ved smerter",
					TestHelper.toDate("2011-01-01"), TestHelper.toDate("2011-01-11"), 
					DayWrapper.makeDay(0,
						AccordingToNeedDoseWrapper.makeDose(new BigDecimal(2)))));
		Assert.assertEquals(
				"2 stk efter behov ved smerter", 
				LongTextConverter.convert(dosage));
		Assert.assertEquals(
				SimpleAccordingToNeedConverterImpl.class, 
				ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
				"2 stk efter behov ved smerter", 
				ShortTextConverter.convert(dosage));
		Assert.assertTrue(DailyDosisCalculator.calculate(dosage).isNone()); 
	}

	@Test
	public void test1Til2StkEfterBehov() throws Exception {
		DosageWrapper dosage = 
			DosageWrapper.makeStructuredDosage(
				StructuredDosageWrapper.makeStructuredDosage(
					0, 
					"stk", 
					null,
					TestHelper.toDate("2011-01-01"), TestHelper.toDate("2011-01-11"), 
					DayWrapper.makeDay(0,
						AccordingToNeedDoseWrapper.makeDose(new BigDecimal(1), new BigDecimal(2)))));
		Assert.assertEquals(
				"1-2 stk efter behov", 
				LongTextConverter.convert(dosage));
		Assert.assertEquals(
				SimpleAccordingToNeedConverterImpl.class, 
				ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
				"1-2 stk efter behov", 
				ShortTextConverter.convert(dosage));
		Assert.assertTrue(DailyDosisCalculator.calculate(dosage).isNone()); 
	}
	
}
