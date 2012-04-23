package dk.medicinkortet.dosisstructuretext.ns2009;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

import dk.medicinkortet.dosisstructuretext.DailyDosisCalculator;
import dk.medicinkortet.dosisstructuretext.LongTextConverter;
import dk.medicinkortet.dosisstructuretext.ShortTextConverter;
import dk.medicinkortet.dosisstructuretext.TestHelper;
import dk.medicinkortet.dosisstructuretext.converterimpl.SimpleNonRepeatedConverterImpl;
import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.PlainDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuredDosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.TimedDoseWrapper;

public class SimpleNonRepeatedConverterTest {
	
	@Test
	public void test1Plaster5TimerFoerVirkningOenskes() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			StructuredDosageWrapper.makeStructuredDosage(
				0, "plaster", "5 timer før virkning ønskes", TestHelper.toDate("2011-01-01"), TestHelper.toDate("2011-01-30"), 
					DayWrapper.makeDay(
						0, 
						PlainDoseWrapper.makeDose(new BigDecimal(1)))));				
		Assert.assertEquals(
			"Doseringsforløbet starter lørdag den 1. januar 2011 og ophører efter det angivne forløb:\n"+
			"   Doseringsforløb:\n"+
			"   Dag ikke angivet: 1 plaster 5 timer før virkning ønskes",
			LongTextConverter.convert(dosage));
		Assert.assertEquals(
			SimpleNonRepeatedConverterImpl.class, 
			ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
			"1 plaster 5 timer før virkning ønskes", 
			ShortTextConverter.convert(dosage));
		Assert.assertTrue(DailyDosisCalculator.calculate(dosage).isNone());
	}
	
	@Test
	public void test1StkKl0730FoerIndlaeggelse() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			StructuredDosageWrapper.makeStructuredDosage(
				0, "stk", "før indlæggelse", TestHelper.toDate("2011-01-01"), TestHelper.toDate("2011-01-01"), 
					DayWrapper.makeDay(
						0, 
						TimedDoseWrapper.makeDose("07:30", new BigDecimal(1)))));				
		Assert.assertEquals(
			"Doseringen foretages kun lørdag den 1. januar 2011:\n"+
			"   Doseringsforløb:\n"+
			"   Dag ikke angivet: 1 stk kl. 07:30 før indlæggelse",
			LongTextConverter.convert(dosage));
		Assert.assertEquals(
			SimpleNonRepeatedConverterImpl.class, 
			ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
			"1 stk kl. 07:30 før indlæggelse", 
			ShortTextConverter.convert(dosage));
		Assert.assertTrue(DailyDosisCalculator.calculate(dosage).isNone());
	}	

	@Test
	public void test1StkKl0730() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			StructuredDosageWrapper.makeStructuredDosage(
				0, "stk", null, TestHelper.toDate("2011-01-01"), TestHelper.toDate("2011-01-01"), 
					DayWrapper.makeDay(
						0, 
						TimedDoseWrapper.makeDose("07:30", new BigDecimal(1)))));				
		Assert.assertEquals(
			"Doseringen foretages kun lørdag den 1. januar 2011:\n"+
			"   Doseringsforløb:\n"+
			"   Dag ikke angivet: 1 stk kl. 07:30",
			LongTextConverter.convert(dosage));
		Assert.assertEquals(
			SimpleNonRepeatedConverterImpl.class, 
			ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
			"1 stk kl. 07:30", 
			ShortTextConverter.convert(dosage));
		Assert.assertTrue(DailyDosisCalculator.calculate(dosage).isNone());
	}	
		
}
