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

package dk.medicinkortet.dosisstructuretext;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

import dk.medicinkortet.dosisstructuretext.vowrapper.DateOrDateTimeWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.EveningDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.MorningDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.NightDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.NoonDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.PlainDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructureWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuresWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.UnitOrUnitsWrapper;

public class DosageWrapperTest {
	
	@Test
	public void testDaglig4StkModSmerter2Gange() throws Exception {
		DosageWrapper dosage = 
			DosageWrapper.makeDosage(
				StructuresWrapper.makeStructures(
					UnitOrUnitsWrapper.makeUnit("stk"),
					StructureWrapper.makeStructure(
						1, "mod smerter", 
						DateOrDateTimeWrapper.makeDate("2011-01-01"), null,
						DayWrapper.makeDay(1,
							PlainDoseWrapper.makeDose(new BigDecimal(4)), 
							PlainDoseWrapper.makeDose(new BigDecimal(4))))));
		Assert.assertEquals(
			"Doseringsforløbet starter lørdag den 1. januar 2011 og gentages hver dag:\n"+
			"   Doseringsforløb:\n"+
			"   4 stk 2 gange daglig mod smerter", 
			LongTextConverter.convert(dosage));
	}

	@Test
	public void testMaxQuantityFullCiphers() throws Exception {
		  
		BigDecimal minimalQuantity = new BigDecimal("0.000000001");
		BigDecimal maximalQuantity = new BigDecimal("999999999.9999999");
		DosageWrapper dosage = 
			DosageWrapper.makeDosage(
				StructuresWrapper.makeStructures(
					UnitOrUnitsWrapper.makeUnit("stk"), 
					StructureWrapper.makeStructure(
					1, "mod smerter",
					DateOrDateTimeWrapper.makeDate("2011-01-01"),DateOrDateTimeWrapper.makeDate("2011-01-14"), 
					DayWrapper.makeDay(1,
						MorningDoseWrapper.makeDose(minimalQuantity, maximalQuantity)))));
		
		DailyDosis dd = DailyDosisCalculator.calculate(dosage);
		Assert.assertEquals(0, minimalQuantity.compareTo(dd.getInterval().getMinimum()));
		Assert.assertEquals(0, maximalQuantity.compareTo(dd.getInterval().getMaximum()));
	}

	
	@Test
	public void testDaglig4StkModSmerterPlus4StkEfterBehovModSmerter() throws Exception {
		DosageWrapper dosage = 
			DosageWrapper.makeDosage(
				StructuresWrapper.makeStructures(
					UnitOrUnitsWrapper.makeUnit("stk"),
					StructureWrapper.makeStructure(
						1, "mod smerter", 
						DateOrDateTimeWrapper.makeDate("2011-01-01"), null,
						DayWrapper.makeDay(1,
								PlainDoseWrapper.makeDose(new BigDecimal(4)), 
								PlainDoseWrapper.makeDose(new BigDecimal(4), true)))));
		Assert.assertEquals(
			"Doseringsforløbet starter lørdag den 1. januar 2011 og gentages hver dag:\n"+
			"   Doseringsforløb:\n"+
			"   4 stk mod smerter + 4 stk efter behov mod smerter",
			LongTextConverter.convert(dosage));
	}

	@Test
	public void testHverAndenDagEtc() throws Exception {
		DosageWrapper dosage = 
			DosageWrapper.makeDosage(
				StructuresWrapper.makeStructures(
					UnitOrUnitsWrapper.makeUnit("stk"),
					StructureWrapper.makeStructure(
						2, "mod smerter", 
						DateOrDateTimeWrapper.makeDate("2011-01-01"), DateOrDateTimeWrapper.makeDate("2011-01-14"), 
						DayWrapper.makeDay(1,
							PlainDoseWrapper.makeDose(new BigDecimal(1))), 
						DayWrapper.makeDay(2, 
							PlainDoseWrapper.makeDose(new BigDecimal(2))))));
		Assert.assertEquals(
			"Doseringsforløbet starter lørdag den 1. januar 2011, forløbet gentages hver 2. dag, og ophører fredag den 14. januar 2011.\n"+
			"Bemærk at doseringen varierer:\n"+
			"   Doseringsforløb:\n"+
			"   Dag 1: 1 stk mod smerter\n"+
			"   Dag 2: 2 stk mod smerter",
			LongTextConverter.convert(dosage));
	}

	@Test
	public void testMorgenMiddagAftenNat() throws Exception {
		DosageWrapper dosage = 
			DosageWrapper.makeDosage(
				StructuresWrapper.makeStructures(
					UnitOrUnitsWrapper.makeUnit("stk"), 
					StructureWrapper.makeStructure(
					1, "mod smerter",
					DateOrDateTimeWrapper.makeDate("2011-01-01"),DateOrDateTimeWrapper.makeDate("2011-01-14"), 
					DayWrapper.makeDay(1,
						MorningDoseWrapper.makeDose(new BigDecimal(1)), 
						NoonDoseWrapper.makeDose(new BigDecimal(1)), 
						EveningDoseWrapper.makeDose(new BigDecimal(1)), 
						NightDoseWrapper.makeDose(new BigDecimal(1)))))); 
		Assert.assertEquals(
			"Doseringsforløbet starter lørdag den 1. januar 2011, gentages hver dag, og ophører fredag den 14. januar 2011:\n"+
			"   Doseringsforløb:\n"+
			"   1 stk morgen mod smerter + 1 stk middag mod smerter + 1 stk aften mod smerter + 1 stk nat mod smerter",				
			LongTextConverter.convert(dosage));
	}
	
	
}
