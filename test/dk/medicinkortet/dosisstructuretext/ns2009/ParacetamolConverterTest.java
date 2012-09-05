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

import org.junit.Assert;
import org.junit.Test;

import dk.medicinkortet.dosisstructuretext.DailyDosisCalculator;
import dk.medicinkortet.dosisstructuretext.DosageType;
import dk.medicinkortet.dosisstructuretext.DosageTypeCalculator;
import dk.medicinkortet.dosisstructuretext.LongTextConverter;
import dk.medicinkortet.dosisstructuretext.ShortTextConverter;
import dk.medicinkortet.dosisstructuretext.TestHelper;
import dk.medicinkortet.dosisstructuretext.shorttextconverterimpl.ParacetamolConverterImpl;
import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.PlainDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageStructureWrapper;

public class ParacetamolConverterTest {
	
	@Test
	public void test1Til2stk3Til4GangeDagligVedSmerter() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			DosageStructureWrapper.makeStructuredDosage(
				1, "stk", "ved smerter", TestHelper.toDate("2011-01-01"), TestHelper.toDate("2011-01-30"),
				DayWrapper.makeDay(
					1, 
					PlainDoseWrapper.makeDose(new BigDecimal(1), new BigDecimal(2)), 
					PlainDoseWrapper.makeDose(new BigDecimal(1), new BigDecimal(2)), 
					PlainDoseWrapper.makeDose(new BigDecimal(1), new BigDecimal(2)), 
					PlainDoseWrapper.makeDose(new BigDecimal(1), new BigDecimal(2), true))));		
		Assert.assertEquals(
			"Doseringsforløbet starter lørdag den 1. januar 2011 og gentages hver dag:\n"+
			"   Doseringsforløb:\n"+
			"   1-2 stk ved smerter + 1-2 stk ved smerter + 1-2 stk ved smerter + 1-2 stk efter behov ved smerter",
			LongTextConverter.convert(dosage));
		Assert.assertEquals(
			ParacetamolConverterImpl.class, 
			ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
			"1-2 stk 3-4 gange daglig ved smerter", 
			ShortTextConverter.convert(dosage));
		Assert.assertTrue(DailyDosisCalculator.calculate(dosage).isNone());
		Assert.assertEquals(DosageType.Combined, DosageTypeCalculator.calculate(dosage));
	}

}
