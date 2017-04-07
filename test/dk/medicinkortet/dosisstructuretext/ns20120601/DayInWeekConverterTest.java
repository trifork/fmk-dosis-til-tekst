package dk.medicinkortet.dosisstructuretext.ns20120601;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

import dk.medicinkortet.dosisstructuretext.LongTextConverter;
import dk.medicinkortet.dosisstructuretext.ShortTextConverter;
import dk.medicinkortet.dosisstructuretext.vowrapper.DateOrDateTimeWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.MorningDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructureWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuresWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.UnitOrUnitsWrapper;

public class DayInWeekConverterTest {
	
	// FMK-3273
	@Test
	public void testSupplText() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			StructuresWrapper.makeStructures(
				UnitOrUnitsWrapper.makeUnits("tablet", "tabletter"), 
				StructureWrapper.makeStructure(
					14, 
					"ved måltid", 
					DateOrDateTimeWrapper.makeDate("2011-01-01"), DateOrDateTimeWrapper.makeDate("2011-01-30"), 
					DayWrapper.makeDay(
						3, MorningDoseWrapper.makeDose(new BigDecimal(1))),
					DayWrapper.makeDay(
						10, MorningDoseWrapper.makeDose(new BigDecimal(1))))
				));
		
		Assert.assertEquals(
				"DayInWeekConverterImpl", 
				ShortTextConverter.getConverterClassName(dosage));
		Assert.assertEquals(
				"1 tablet morgen daglig mandag ved måltid", 
				ShortTextConverter.convert(dosage));
		
		Assert.assertEquals(
				"DefaultLongTextConverterImpl", 
				LongTextConverter.getConverterClassName(dosage));
		Assert.assertEquals(
				"Doseringsforløbet starter lørdag den 1. januar 2011, forløbet gentages efter 14 dage, og ophører søndag den 30. januar 2011.\nBemærk at doseringen har et komplekst forløb:\n"+
				"   Doseringsforløb:\n"+
				"   Mandag den 3. januar 2011: 1 tablet morgen ved måltid\n"+
				"   Mandag den 10. januar 2011: 1 tablet morgen ved måltid",
				LongTextConverter.convert(dosage));
	}

}
