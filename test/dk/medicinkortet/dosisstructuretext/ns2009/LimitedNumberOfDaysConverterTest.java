/**
* The contents of this file are subject to the Mozilla Public
* License Version 1.1 (the "License"); you may not use this file
* except in compliance with the License. You may obtain a copy of
* the License at http://www.mozilla.org/MPL/
*
* Software distributed under the License is distributed on an "AS
* IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
* implied. See the License for the specific language governing
* rights and limitations under the License.
*
* Contributor(s): Contributors are attributed in the source code
* where applicable.
*
* The Original Code is "Dosis-til-tekst".
*
* The Initial Developer of the Original Code is Trifork Public A/S.
*
* Portions created for the FMK Project are Copyright 2011,
* National Board of e-Health (NSI). All Rights Reserved.
*/

package dk.medicinkortet.dosisstructuretext.ns2009;

import java.math.BigDecimal;

import dk.medicinkortet.dosisstructuretext.LocalTime;
import org.junit.Assert;
import org.junit.Test;

import dk.medicinkortet.dosisstructuretext.DailyDosisCalculator;
import dk.medicinkortet.dosisstructuretext.DosageType;
import dk.medicinkortet.dosisstructuretext.DosageTypeCalculator;
import dk.medicinkortet.dosisstructuretext.LongTextConverter;
import dk.medicinkortet.dosisstructuretext.ShortTextConverter;
import dk.medicinkortet.dosisstructuretext.longtextconverterimpl.DefaultLongTextConverterImpl;
import dk.medicinkortet.dosisstructuretext.shorttextconverterimpl.LimitedNumberOfDaysConverterImpl;
import dk.medicinkortet.dosisstructuretext.vowrapper.DateOrDateTimeWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.PlainDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructureWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuresWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.TimedDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.UnitOrUnitsWrapper;

public class LimitedNumberOfDaysConverterTest {
	
	@Test
	public void test4Stk2GangeDagligI3DageVedMaaltid() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			StructuresWrapper.makeStructures(
				UnitOrUnitsWrapper.makeUnit("stk"), 
				StructureWrapper.makeStructure(
					0, "ved måltid", 
					DateOrDateTimeWrapper.makeDate("2011-01-01"), null, 
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
						PlainDoseWrapper.makeDose(new BigDecimal(4))))));		
		Assert.assertEquals(
				"DefaultLongTextConverterImpl",
				LongTextConverter.getConverterClassName(dosage));
		Assert.assertEquals(
				"Doseringsforløbet starter lørdag den 1. januar 2011 og ophører efter det angivne forløb:\n"+
				"   Doseringsforløb:\n"+
				"   Lørdag den 1. januar 2011: 4 stk 2 gange ved måltid\n"+
				"   Søndag den 2. januar 2011: 4 stk 2 gange ved måltid\n"+
				"   Mandag den 3. januar 2011: 4 stk 2 gange ved måltid\n"+
				"   Tirsdag den 4. januar 2011: 4 stk 2 gange ved måltid",
				LongTextConverter.convert(dosage));
		Assert.assertEquals(
				"LimitedNumberOfDaysConverterImpl", 
				ShortTextConverter.getConverterClassName(dosage));
		Assert.assertEquals(
				"4 stk 2 gange daglig i 4 dage ved måltid", 
				ShortTextConverter.convert(dosage));
		Assert.assertEquals(
				8.0, 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001); 				
		Assert.assertEquals(DosageType.Temporary, DosageTypeCalculator.calculate(dosage));
	}

	@Test
	public void test4Til6Stk2GangeDagligI3DageVedMaaltid() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			StructuresWrapper.makeStructures(
				UnitOrUnitsWrapper.makeUnit("stk"), 
				StructureWrapper.makeStructure(
					0, "ved måltid", DateOrDateTimeWrapper.makeDate("2011-01-01"), null,  
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
						PlainDoseWrapper.makeDose(new BigDecimal(4), new BigDecimal(6))))));		
		Assert.assertEquals(
				"Doseringsforløbet starter lørdag den 1. januar 2011 og ophører efter det angivne forløb:\n"+
				"   Doseringsforløb:\n"+
				"   Lørdag den 1. januar 2011: 4-6 stk 2 gange ved måltid\n"+
				"   Søndag den 2. januar 2011: 4-6 stk 2 gange ved måltid\n"+
				"   Mandag den 3. januar 2011: 4-6 stk 2 gange ved måltid",
				LongTextConverter.convert(dosage));
		Assert.assertEquals(
				"LimitedNumberOfDaysConverterImpl", 
				ShortTextConverter.getConverterClassName(dosage));		
		Assert.assertEquals(
				"4-6 stk 2 gange daglig i 3 dage ved måltid", 
				ShortTextConverter.convert(dosage));
		Assert.assertEquals(
				8.0, 
				DailyDosisCalculator.calculate(dosage).getInterval().getMinimum().doubleValue(), 
				0.000000001); 				
		Assert.assertEquals(
				12.0, 
				DailyDosisCalculator.calculate(dosage).getInterval().getMaximum().doubleValue(), 
				0.000000001); 				
		Assert.assertEquals(DosageType.Temporary, DosageTypeCalculator.calculate(dosage));
	}

	@Test
	public void testCQ611() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			StructuresWrapper.makeStructures(
				UnitOrUnitsWrapper.makeUnit("ml"), 
				StructureWrapper.makeStructure(
					0, "ved måltid", DateOrDateTimeWrapper.makeDate("2011-01-01"), null, 
					DayWrapper.makeDay(
						3, 
						TimedDoseWrapper.makeDose(new LocalTime(11,25), new BigDecimal(7), false)))));
		Assert.assertEquals(
				"Doseringsforløbet starter lørdag den 1. januar 2011 og ophører efter det angivne forløb:\n" +
				"   Doseringsforløb:\n" +
				"   Mandag den 3. januar 2011: 7 ml kl. 11:25 ved måltid",
				LongTextConverter.convert(dosage));
		Assert.assertNull(ShortTextConverter.getConverterClassName(dosage));
		Assert.assertEquals(
				7./3., 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001); 							
		Assert.assertEquals(DosageType.Temporary, DosageTypeCalculator.calculate(dosage));
	}
	
	// FMK-3017 Dosis-to-text oversætter NotIterated struktur med en enkelt dag forkert
	@Test
	public void test5ml4gange() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeDosage(
			StructuresWrapper.makeStructures(
				UnitOrUnitsWrapper.makeUnit("ml"), 
				StructureWrapper.makeStructure(
					0, null, 
					DateOrDateTimeWrapper.makeDate("2010-01-01"), DateOrDateTimeWrapper.makeDate("2110-01-01"), 
					DayWrapper.makeDay(
						1, 
						PlainDoseWrapper.makeDose(new BigDecimal(5), true), 
						PlainDoseWrapper.makeDose(new BigDecimal(5), true), 
						PlainDoseWrapper.makeDose(new BigDecimal(5), true),
						PlainDoseWrapper.makeDose(new BigDecimal(5), true)))));			
					
		Assert.assertEquals(
				"5 ml efter behov 4 gange", 
				ShortTextConverter.convert(dosage));
	}
	
	
}
