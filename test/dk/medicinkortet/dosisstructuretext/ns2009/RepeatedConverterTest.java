package dk.medicinkortet.dosisstructuretext.ns2009;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

import dk.medicinkortet.dosisstructuretext.DailyDosisCalculator;
import dk.medicinkortet.dosisstructuretext.LongTextConverter;
import dk.medicinkortet.dosisstructuretext.ShortTextConverter;
import dk.medicinkortet.dosisstructuretext.TestHelper;
import dk.medicinkortet.dosisstructuretext.converterimpl.RepeatedConverterImpl;
import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.PlainDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuredDosageWrapper;

public class RepeatedConverterTest {
	
	@Test
	public void test3stk2gangeDaglig() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			StructuredDosageWrapper.makeStructuredDosage(
				1, "stk", null, TestHelper.toDate("2011-01-01"), TestHelper.toDate("2011-01-30"), 
				DayWrapper.makeDay(
					1, 
					PlainDoseWrapper.makeDose(new BigDecimal(3)), 
					PlainDoseWrapper.makeDose(new BigDecimal(3)))));
		Assert.assertEquals(
				"Daglig 3 stk 2 gange", 
				LongTextConverter.convert(dosage));
		Assert.assertEquals(
				RepeatedConverterImpl.class, 
				ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
				"3 stk 2 gange daglig", 
				ShortTextConverter.convert(dosage));
		Assert.assertEquals(
				6.0, 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001); 				
	}
	
	@Test
	public void test1stk3gangeDagligVedMaaltid() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			StructuredDosageWrapper.makeStructuredDosage(
				1, "stk", "ved måltid", TestHelper.toDate("2011-01-01"), TestHelper.toDate("2011-01-30"), 
				DayWrapper.makeDay(
					1, 
					PlainDoseWrapper.makeDose(new BigDecimal(3)), 
					PlainDoseWrapper.makeDose(new BigDecimal(3)))));
		Assert.assertEquals(
				"Daglig 3 stk ved måltid 2 gange", 
				LongTextConverter.convert(dosage));
		Assert.assertEquals(
				RepeatedConverterImpl.class, 
				ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
				"3 stk 2 gange daglig ved måltid", 
				ShortTextConverter.convert(dosage));
		Assert.assertEquals(
				6.0, 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001); 				
	}
	
	@Test
	public void test1stkDagligVedMaaltid() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
				StructuredDosageWrapper.makeStructuredDosage(
					1, "stk", "ved måltid", TestHelper.toDate("2011-01-01"), TestHelper.toDate("2011-01-30"), 
					DayWrapper.makeDay(
						1, 
						PlainDoseWrapper.makeDose(new BigDecimal(1)))));
		Assert.assertEquals(
				"Daglig 1 stk ved måltid", 
				LongTextConverter.convert(dosage));
		Assert.assertEquals(
				RepeatedConverterImpl.class, 
				ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
				"1 stk daglig ved måltid", 
				ShortTextConverter.convert(dosage));
		// assertEquals(10.0, result.getAvgDailyDosis().doubleValue()); 				
	}

	@Test
	public void test1Til2stk2GangeDaglig() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
				StructuredDosageWrapper.makeStructuredDosage(
					1, "stk", null, TestHelper.toDate("2011-01-01"), TestHelper.toDate("2011-01-30"), 
					DayWrapper.makeDay(
						1, 
						PlainDoseWrapper.makeDose(new BigDecimal(1), new BigDecimal(2)), 
						PlainDoseWrapper.makeDose(new BigDecimal(1), new BigDecimal(2)))));
		Assert.assertEquals(
				"Daglig 1-2 stk 2 gange", 
				LongTextConverter.convert(dosage));
		Assert.assertEquals(
				RepeatedConverterImpl.class, 
				ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
				"1-2 stk 2 gange daglig", 
				ShortTextConverter.convert(dosage));
		Assert.assertEquals(
				2.0, 
				DailyDosisCalculator.calculate(dosage).getInterval().getMinimum().doubleValue(), 
				0.000000001);
		Assert.assertEquals(
				4.0, 
				DailyDosisCalculator.calculate(dosage).getInterval().getMaximum().doubleValue(), 
				0.000000001); 				
	}

	@Test
	public void test1stkHver2DagVedMaaltid() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			StructuredDosageWrapper.makeStructuredDosage(
				2, "stk", "ved måltid", TestHelper.toDate("2011-01-01"), TestHelper.toDate("2011-01-30"), 
				DayWrapper.makeDay(
					1, 
					PlainDoseWrapper.makeDose(new BigDecimal(1)))));
		Assert.assertEquals(
				"Hver 2. dag 1 stk ved måltid", 
				LongTextConverter.convert(dosage));
		Assert.assertEquals(
				RepeatedConverterImpl.class, 
				ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
				"1 stk hver 2. dag ved måltid", 
				ShortTextConverter.convert(dosage));
		Assert.assertEquals(
				0.5, 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001); 				
	}
	
	
	@Test
	public void test1stkOmUgenVedMaaltid() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			StructuredDosageWrapper.makeStructuredDosage(
				7, "stk", "ved måltid", TestHelper.toDate("2011-01-01"), TestHelper.toDate("2011-01-30"), 
				DayWrapper.makeDay(
					1, 
					PlainDoseWrapper.makeDose(new BigDecimal(1)))));
		Assert.assertEquals(
				"Hver 7. dag 1 stk ved måltid", 
				LongTextConverter.convert(dosage));
		Assert.assertEquals(
				RepeatedConverterImpl.class, 
				ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
				"1 stk 1 gang om ugen ved måltid", 
				ShortTextConverter.convert(dosage));
		Assert.assertEquals(
				1/7., 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001); 				
	}

	@Test
	public void test1stkOmMaanedenVedMaaltid() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			StructuredDosageWrapper.makeStructuredDosage(
				30, "stk", "ved måltid", TestHelper.toDate("2011-01-01"), TestHelper.toDate("2011-01-30"), 
				DayWrapper.makeDay(
					1, 
					PlainDoseWrapper.makeDose(new BigDecimal(1)))));
		Assert.assertEquals(
				"Hver 30. dag 1 stk ved måltid", 
				LongTextConverter.convert(dosage));
		Assert.assertEquals(
				RepeatedConverterImpl.class, 
				ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
				"1 stk 1 gang om måneden ved måltid", 
				ShortTextConverter.convert(dosage));
		Assert.assertEquals(
				1/30., 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001); 				
	}
	
	@Test
	public void test2_5stk1GangOmUgenVedMaaltid() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			StructuredDosageWrapper.makeStructuredDosage(
				7, "stk", "ved måltid", TestHelper.toDate("2011-01-01"), TestHelper.toDate("2011-01-30"), 
				DayWrapper.makeDay(
					1, 
					PlainDoseWrapper.makeDose(new BigDecimal(2.5)))));
		Assert.assertEquals(
				"Hver 7. dag 2,5 stk ved måltid", 
				LongTextConverter.convert(dosage));
		Assert.assertEquals(
				RepeatedConverterImpl.class, 
				ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
				"2,5 stk 1 gang om ugen ved måltid", 
				ShortTextConverter.convert(dosage));
		Assert.assertEquals(
				2.5/7., 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001); 				
	}
	
	@Test
	public void test2_5Stk2GangeSammeDag1GangOmUgenVedMaaltid() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			StructuredDosageWrapper.makeStructuredDosage(
				7, "stk", "ved måltid", TestHelper.toDate("2011-01-01"), TestHelper.toDate("2011-01-30"), 
				DayWrapper.makeDay(
					1, 
					PlainDoseWrapper.makeDose(new BigDecimal(2.5)), 
					PlainDoseWrapper.makeDose(new BigDecimal(2.5)))));
		Assert.assertEquals(
				"Hver 7. dag 2,5 stk ved måltid 2 gange", 
				LongTextConverter.convert(dosage));
		Assert.assertEquals(
				RepeatedConverterImpl.class, 
				ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
				"2,5 stk 2 gange samme dag 1 gang om ugen ved måltid", 
				ShortTextConverter.convert(dosage));
		Assert.assertEquals(
				5/7., 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001); 				
	}

	@Test
	public void test2_5stk1GangOmMaanedenVedMaaltid() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			StructuredDosageWrapper.makeStructuredDosage(
				30, "stk", "ved måltid", TestHelper.toDate("2011-01-01"), TestHelper.toDate("2011-01-30"), 
				DayWrapper.makeDay(
					1,
					PlainDoseWrapper.makeDose(new BigDecimal(2.5)))));
		Assert.assertEquals(
				"Hver 30. dag 2,5 stk ved måltid", 
				LongTextConverter.convert(dosage));
		Assert.assertEquals(
				RepeatedConverterImpl.class, 
				ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
				"2,5 stk 1 gang om måneden ved måltid", 
				ShortTextConverter.convert(dosage));
		Assert.assertEquals(
				2.5/30., 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001); 				
	}
	
	@Test
	public void test2_5Stk2GangeSammeDag1GangOmMaanedenVedMaaltid() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			StructuredDosageWrapper.makeStructuredDosage(
				30, "stk", "ved måltid", TestHelper.toDate("2011-01-01"), TestHelper.toDate("2011-01-30"), 
				DayWrapper.makeDay(
					1, 
					PlainDoseWrapper.makeDose(new BigDecimal(2.5)), 
					PlainDoseWrapper.makeDose(new BigDecimal(2.5)))));
		Assert.assertEquals(
				"Hver 30. dag 2,5 stk ved måltid 2 gange", 
				LongTextConverter.convert(dosage));
		Assert.assertEquals(
				RepeatedConverterImpl.class, 
				ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
				"2,5 stk 2 gange samme dag 1 gang om måneden ved måltid", 
				ShortTextConverter.convert(dosage));
		Assert.assertEquals(
				5/30., 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001); 				
	}

	@Test
	public void test2_5stkHver5DagVedMaaltid() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			StructuredDosageWrapper.makeStructuredDosage(
				5, "stk", "ved måltid", TestHelper.toDate("2011-01-01"), TestHelper.toDate("2011-01-30"), 
				DayWrapper.makeDay(
					1, 
					PlainDoseWrapper.makeDose(new BigDecimal(2.5)))));
		Assert.assertEquals(
				"Hver 5. dag 2,5 stk ved måltid", 
				LongTextConverter.convert(dosage));
		Assert.assertEquals(
				RepeatedConverterImpl.class, 
				ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
				"2,5 stk hver 5. dag ved måltid", 
				ShortTextConverter.convert(dosage));
		Assert.assertEquals(
				2.5/5., 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001); 				
	}
	
	@Test
	public void test2_5stk2GangeSammeDagHver5DagVedMaaltid() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			StructuredDosageWrapper.makeStructuredDosage(
				5, "stk", "ved måltid", TestHelper.toDate("2011-01-01"), TestHelper.toDate("2011-01-30"), 
				DayWrapper.makeDay(
					1, 
					PlainDoseWrapper.makeDose(new BigDecimal(2.5)), 
					PlainDoseWrapper.makeDose(new BigDecimal(2.5)))));
		Assert.assertEquals(
				"Hver 5. dag 2,5 stk ved måltid 2 gange", 
				LongTextConverter.convert(dosage));
		Assert.assertEquals(
				RepeatedConverterImpl.class, 
				ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
				"2,5 stk 2 gange samme dag hver 5. dag ved måltid", 
				ShortTextConverter.convert(dosage));
		Assert.assertEquals(
				1, 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001); 				
	}
	
	@Test
	public void test0_5stk1GangSammeDagHver5DagVedMaaltid() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			StructuredDosageWrapper.makeStructuredDosage(
				5, "stk", "ved måltid", TestHelper.toDate("2011-01-01"), TestHelper.toDate("2011-01-30"), 
				DayWrapper.makeDay(
					1, 
					PlainDoseWrapper.makeDose(new BigDecimal(0.5)), 
					PlainDoseWrapper.makeDose(new BigDecimal(0.5)))));
		Assert.assertEquals(
				"Hver 5. dag 0,5 stk ved måltid 2 gange", 
				LongTextConverter.convert(dosage));
		Assert.assertEquals(
				RepeatedConverterImpl.class, 
				ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
				"1/2 stk 2 gange samme dag hver 5. dag ved måltid", 
				ShortTextConverter.convert(dosage));
		Assert.assertEquals(
				1/5., 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001); 				
	}
	
}
