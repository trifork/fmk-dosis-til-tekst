package dk.medicinkortet.dosisstructuretext.ns20120601;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

import dk.medicinkortet.dosisstructuretext.DailyDosisCalculator;
import dk.medicinkortet.dosisstructuretext.DosageType;
import dk.medicinkortet.dosisstructuretext.DosageTypeCalculator;
import dk.medicinkortet.dosisstructuretext.LongTextConverter;
import dk.medicinkortet.dosisstructuretext.ShortTextConverter;
import dk.medicinkortet.dosisstructuretext.longtextconverterimpl.DailyRepeatedConverterImpl;
import dk.medicinkortet.dosisstructuretext.shorttextconverterimpl.RepeatedEyeOrEarConverterImpl;
import dk.medicinkortet.dosisstructuretext.vowrapper.DateOrDateTimeWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.PlainDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructureWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuresWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.UnitOrUnitsWrapper;

public class RepeatedEyeOrEarConverterTest {

	@Test
	public void testOneInEachEyeTwice() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			StructuresWrapper.makeStructures(
				UnitOrUnitsWrapper.makeUnits("dråbe", "dråber"), 
				StructureWrapper.makeStructure(
					1, ", 1 i hvert øje", DateOrDateTimeWrapper.makeDate("2011-01-01"), DateOrDateTimeWrapper.makeDate("2011-01-30"), 
					DayWrapper.makeDay(
						1, 
						PlainDoseWrapper.makeDose(new BigDecimal(2)), 
						PlainDoseWrapper.makeDose(new BigDecimal(2)))))); 
		Assert.assertEquals(
				DailyRepeatedConverterImpl.class, 
				LongTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
			"Doseringsforløbet starter lørdag den 1. januar 2011 og gentages hver dag:\n"+
			"   Doseringsforløb:\n"+
			"   2 dråber 2 gange daglig, 1 i hvert øje",
			LongTextConverter.convert(dosage));
		Assert.assertEquals(
			RepeatedEyeOrEarConverterImpl.class, 
			ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
			"1 dråbe 2 gange daglig i begge øjne", 
			ShortTextConverter.convert(dosage));
		Assert.assertEquals(
				4.0, 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001); 			
		Assert.assertEquals(DosageType.Temporary, DosageTypeCalculator.calculate(dosage));		
	}
}
