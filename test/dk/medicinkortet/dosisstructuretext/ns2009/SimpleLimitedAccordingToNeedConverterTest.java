package dk.medicinkortet.dosisstructuretext.ns2009;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

import dk.medicinkortet.dosisstructuretext.DailyDosisCalculator;
import dk.medicinkortet.dosisstructuretext.LongTextConverter;
import dk.medicinkortet.dosisstructuretext.ShortTextConverter;
import dk.medicinkortet.dosisstructuretext.TestHelper;
import dk.medicinkortet.dosisstructuretext.converterimpl.SimpleLimitedAccordingToNeedConverterImpl;
import dk.medicinkortet.dosisstructuretext.vowrapper.AccordingToNeedDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuredDosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;

public class SimpleLimitedAccordingToNeedConverterTest {
	
	@Test
	public void test1pustVedAnfaldHoejst3GangeDaglig() {
		DosageWrapper dosage = 
			DosageWrapper.makeStructuredDosage(
				StructuredDosageWrapper.makeStructuredDosage(
					1, "pust", "ved anfald", TestHelper.toDate("2011-01-01"), TestHelper.toDate("2011-01-11"), 
					DayWrapper.makeDay(1,
						AccordingToNeedDoseWrapper.makeDose(new BigDecimal(1)),
						AccordingToNeedDoseWrapper.makeDose(new BigDecimal(1)),
						AccordingToNeedDoseWrapper.makeDose(new BigDecimal(1)))));
		Assert.assertEquals(
				"Doseringsforløbet starter lørdag den 1. januar 2011 og gentages dagligt:\n"+
				"   Doseringsforløb:\n"+
				"   Lørdag den 1. januar 2011: 1 pust efter behov ved anfald højst 3 gange",
				LongTextConverter.convert(dosage));
		Assert.assertEquals(
				SimpleLimitedAccordingToNeedConverterImpl.class, 
				ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
				"1 pust efter behov ved anfald højst 3 gange daglig", 
				ShortTextConverter.convert(dosage));
		Assert.assertTrue(DailyDosisCalculator.calculate(dosage).isNone()); 
	}
	
}
