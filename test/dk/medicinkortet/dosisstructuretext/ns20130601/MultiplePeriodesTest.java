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

package dk.medicinkortet.dosisstructuretext.ns20130601;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

import dk.medicinkortet.dosisstructuretext.DailyDosisCalculator;
import dk.medicinkortet.dosisstructuretext.DosageType;
import dk.medicinkortet.dosisstructuretext.DosageTypeCalculator;
import dk.medicinkortet.dosisstructuretext.LongTextConverter;
import dk.medicinkortet.dosisstructuretext.ShortTextConverter;
import dk.medicinkortet.dosisstructuretext.vowrapper.DateOrDateTimeWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.EveningDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.MorningDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.NoonDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.PlainDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructureWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuresWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.UnitOrUnitsWrapper;

public class MultiplePeriodesTest {
	
	@Test
	public void testTwoFollwingPeriodes() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			StructuresWrapper.makeStructures(
				UnitOrUnitsWrapper.makeUnits("tablet", "tabletter"), 
				StructureWrapper.makeStructure(
					0, null, DateOrDateTimeWrapper.makeDate("2013-06-01"), DateOrDateTimeWrapper.makeDate("2013-06-3"),
					DayWrapper.makeDay(
						1, 
						MorningDoseWrapper.makeDose(new BigDecimal(2)), 
						NoonDoseWrapper.makeDose(new BigDecimal(2)), 
						EveningDoseWrapper.makeDose(new BigDecimal(2))),
					DayWrapper.makeDay(
						2, 
						MorningDoseWrapper.makeDose(new BigDecimal(2)), 
						EveningDoseWrapper.makeDose(new BigDecimal(2))),
					DayWrapper.makeDay(
						3, 
						MorningDoseWrapper.makeDose(new BigDecimal(2)))), 
						
						
				StructureWrapper.makeStructure(
					1, null, DateOrDateTimeWrapper.makeDate("2013-06-04"), null, 
					DayWrapper.makeDay(
						1, 
						MorningDoseWrapper.makeDose(new BigDecimal(1)))
					)
				)				
			); 
		Assert.assertEquals(
			"Doseringen indeholder flere perioder:\n" +
			"\n" +
			"Doseringsforløbet starter lørdag den 1. juni 2013 og ophører efter det angivne forløb.\n" +
			"Bemærk at doseringen varierer:\n" +
			"   Doseringsforløb:\n" +
			"   Lørdag den 1. juni 2013: 2 tabletter morgen + 2 tabletter middag + 2 tabletter aften\n" +
			"   Søndag den 2. juni 2013: 2 tabletter morgen + 2 tabletter aften\n" +
			"   Mandag den 3. juni 2013: 2 tabletter morgen\n" +
			"\n" +
			"Doseringsforløbet starter tirsdag den 4. juni 2013 og gentages hver dag:\n" +
			"   Doseringsforløb:\n" +
			"   1 tablet morgen",
			LongTextConverter.convert(dosage));
		Assert.assertNull(ShortTextConverter.getConverterClass(dosage));
		Assert.assertNull(DailyDosisCalculator.calculate(dosage).getValue()); 		
		Assert.assertEquals(DosageType.Combined, DosageTypeCalculator.calculate(dosage));		
	}
	
	@Test
	public void testTwoFollwingPeriodesWithOverlappingPN() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			StructuresWrapper.makeStructures(
				UnitOrUnitsWrapper.makeUnits("tablet", "tabletter"), 
				StructureWrapper.makeStructure(
					0, null, DateOrDateTimeWrapper.makeDate("2013-06-01"), DateOrDateTimeWrapper.makeDate("2013-06-3"),
					DayWrapper.makeDay(
						1, 
						MorningDoseWrapper.makeDose(new BigDecimal(2)), 
						NoonDoseWrapper.makeDose(new BigDecimal(2)), 
						EveningDoseWrapper.makeDose(new BigDecimal(2))),
					DayWrapper.makeDay(
						2, 
						MorningDoseWrapper.makeDose(new BigDecimal(2)), 
						EveningDoseWrapper.makeDose(new BigDecimal(2))),
					DayWrapper.makeDay(
						3, 
						MorningDoseWrapper.makeDose(new BigDecimal(2)))), 
						
						
				StructureWrapper.makeStructure(
					1, null, DateOrDateTimeWrapper.makeDate("2013-06-04"), null, 
					DayWrapper.makeDay(
						1, 
						MorningDoseWrapper.makeDose(new BigDecimal(1)))),
				
				
				StructureWrapper.makeStructure(
					0, "ved smerter", DateOrDateTimeWrapper.makeDate("2013-06-01"), null, 
					DayWrapper.makeDay(
						0, 
						PlainDoseWrapper.makeDose(new BigDecimal(2), true))))
						
			); 
				
		Assert.assertEquals(
			"Doseringen indeholder flere perioder, bemærk at der er ovelappende perioder:\n" +
			"\n" +
			"Doseringsforløbet starter lørdag den 1. juni 2013 og ophører efter det angivne forløb.\n" +
			"Bemærk at doseringen varierer:\n" +
			"   Doseringsforløb:\n" +
			"   Lørdag den 1. juni 2013: 2 tabletter morgen + 2 tabletter middag + 2 tabletter aften\n" +
			"   Søndag den 2. juni 2013: 2 tabletter morgen + 2 tabletter aften\n" +
			"   Mandag den 3. juni 2013: 2 tabletter morgen\n" +
			"\n" +
			"Doseringsforløbet starter lørdag den 1. juni 2013:\n" + 
			"   Doseringsforløb:\n" +
			"   Efter behov: 2 tabletter efter behov ved smerter\n" +
			"\n" + 
			"Doseringsforløbet starter tirsdag den 4. juni 2013 og gentages hver dag:\n" +
			"   Doseringsforløb:\n" +
			"   1 tablet morgen",
			LongTextConverter.convert(dosage));
		Assert.assertNull(ShortTextConverter.getConverterClass(dosage));
		Assert.assertNull(DailyDosisCalculator.calculate(dosage).getValue()); 		
		Assert.assertEquals(DosageType.Combined, DosageTypeCalculator.calculate(dosage));		
	}

}
