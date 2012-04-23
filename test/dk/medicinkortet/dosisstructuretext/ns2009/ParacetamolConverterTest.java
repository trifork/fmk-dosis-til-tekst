package dk.medicinkortet.dosisstructuretext.ns2009;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

import dk.medicinkortet.dosisstructuretext.DailyDosisCalculator;
import dk.medicinkortet.dosisstructuretext.LongTextConverter;
import dk.medicinkortet.dosisstructuretext.ShortTextConverter;
import dk.medicinkortet.dosisstructuretext.TestHelper;
import dk.medicinkortet.dosisstructuretext.converterimpl.ParacetamolConverterImpl;
import dk.medicinkortet.dosisstructuretext.vowrapper.AccordingToNeedDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.PlainDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuredDosageWrapper;

public class ParacetamolConverterTest {
	
	@Test
	public void test1Til2stk3Til4GangeDagligVedSmerter() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			StructuredDosageWrapper.makeStructuredDosage(
				1, "stk", "ved smerter", TestHelper.toDate("2011-01-01"), TestHelper.toDate("2011-01-30"),
				DayWrapper.makeDay(
					1, 
					PlainDoseWrapper.makeDose(new BigDecimal(1), new BigDecimal(2)), 
					PlainDoseWrapper.makeDose(new BigDecimal(1), new BigDecimal(2)), 
					PlainDoseWrapper.makeDose(new BigDecimal(1), new BigDecimal(2)), 
					AccordingToNeedDoseWrapper.makeDose(new BigDecimal(1), new BigDecimal(2)))));		
		Assert.assertEquals(
			"Doseringsforløbet starter lørdag den 1. januar 2011 og gentages dagligt:\n"+
			"   Doseringsforløb:\n"+
			"   Lørdag den 1. januar 2011: 1-2 stk ved smerter + 1-2 stk ved smerter + 1-2 stk ved smerter + 1-2 stk efter behov ved smerter",
			LongTextConverter.convert(dosage));
		Assert.assertEquals(
			ParacetamolConverterImpl.class, 
			ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
			"1-2 stk 3-4 gange daglig ved smerter", 
			ShortTextConverter.convert(dosage));
		Assert.assertTrue(DailyDosisCalculator.calculate(dosage).isNone()); 				
	}

}
