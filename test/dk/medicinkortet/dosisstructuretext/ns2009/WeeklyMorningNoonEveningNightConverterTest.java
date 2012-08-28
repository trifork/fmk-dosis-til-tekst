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
import dk.medicinkortet.dosisstructuretext.longtextconverterimpl.TwoDaysRepeatedConverterImpl;
import dk.medicinkortet.dosisstructuretext.longtextconverterimpl.WeeklyRepeatedConverterImpl;
import dk.medicinkortet.dosisstructuretext.shorttextconverterimpl.RepeatedConverterImpl;
import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.EveningDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.MorningDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.PlainDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuredDosageWrapper;

public class WeeklyMorningNoonEveningNightConverterTest {

	@Test /* TODO: This is a candidate for a short text converter */
	public void testWeeklyPlain() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			StructuredDosageWrapper.makeStructuredDosage(
				7, "stk", "ved måltid", TestHelper.toDate("2012-06-08"), TestHelper.toDate("2012-12-31"), 
				DayWrapper.makeDay(
					1, 
					PlainDoseWrapper.makeDose(new BigDecimal(1)), 
					PlainDoseWrapper.makeDose(new BigDecimal(1))),
				DayWrapper.makeDay(
					3, 
					PlainDoseWrapper.makeDose(new BigDecimal(1)), 
					PlainDoseWrapper.makeDose(new BigDecimal(1))),
				DayWrapper.makeDay(
					5, 
					PlainDoseWrapper.makeDose(new BigDecimal(1)), 
					PlainDoseWrapper.makeDose(new BigDecimal(1))),
				DayWrapper.makeDay(
					7, 
					PlainDoseWrapper.makeDose(new BigDecimal(1)), 
					PlainDoseWrapper.makeDose(new BigDecimal(1)))));
		Assert.assertEquals(
				WeeklyRepeatedConverterImpl.class, 
				LongTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
				"Doseringsforløbet starter fredag den 8. juni 2012, forløbet gentages hver uge.\n"+
				"Bemærk at doseringen har et komplekst forløb:\n"+
				"   Doseringsforløb:\n"+
				"   Tirsdag: 1 stk ved måltid 2 gange\n"+
				"   Torsdag: 1 stk ved måltid 2 gange\n"+
				"   Fredag: 1 stk ved måltid 2 gange\n"+
				"   Søndag: 1 stk ved måltid 2 gange",
				LongTextConverter.convert(dosage));
		Assert.assertNull(ShortTextConverter.convert(dosage));
		Assert.assertEquals(
				8/7., 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001); 							
		Assert.assertEquals(DosageType.Temporary, DosageTypeCalculator.calculate(dosage));						
	}
	
	@Test
	public void testWeeklyMorningAndEvening() throws Exception {
		DosageWrapper dosage = DosageWrapper.makeStructuredDosage(
			StructuredDosageWrapper.makeStructuredDosage(
				7, "stk", "ved måltid", TestHelper.toDate("2012-06-08"), TestHelper.toDate("2012-12-31"), 
				DayWrapper.makeDay(
					1, 
					MorningDoseWrapper.makeDose(new BigDecimal(1)), 
					EveningDoseWrapper.makeDose(new BigDecimal(1))),
				DayWrapper.makeDay(
					3, 
					MorningDoseWrapper.makeDose(new BigDecimal(1)), 
					EveningDoseWrapper.makeDose(new BigDecimal(1))),
				DayWrapper.makeDay(
					5, 
					MorningDoseWrapper.makeDose(new BigDecimal(1)), 
					EveningDoseWrapper.makeDose(new BigDecimal(1))),
				DayWrapper.makeDay(
					7, 
					MorningDoseWrapper.makeDose(new BigDecimal(1)), 
					EveningDoseWrapper.makeDose(new BigDecimal(1)))));
		Assert.assertEquals(
				WeeklyRepeatedConverterImpl.class, 
				LongTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
				"Doseringsforløbet starter fredag den 8. juni 2012, forløbet gentages hver uge.\n"+
				"Bemærk at doseringen har et komplekst forløb:\n"+
				 "   Doseringsforløb:\n"+
				 "   Tirsdag: 1 stk morgen ved måltid + 1 stk aften ved måltid\n"+
				 "   Torsdag: 1 stk morgen ved måltid + 1 stk aften ved måltid\n"+
				 "   Fredag: 1 stk morgen ved måltid + 1 stk aften ved måltid\n"+
				 "   Søndag: 1 stk morgen ved måltid + 1 stk aften ved måltid",
				LongTextConverter.convert(dosage));
		Assert.assertEquals(
				dk.medicinkortet.dosisstructuretext.shorttextconverterimpl.WeeklyMorningNoonEveningNightConverterImpl.class,
				ShortTextConverter.getConverterClass(dosage));
		Assert.assertEquals(
				"1 stk morgen og aften ved måltid tirsdag, torsdag, fredag og søndag hver uge",
				ShortTextConverter.convert(dosage));
		Assert.assertEquals(
				8/7., 
				DailyDosisCalculator.calculate(dosage).getValue().doubleValue(), 
				0.000000001); 							
		Assert.assertEquals(DosageType.Temporary, DosageTypeCalculator.calculate(dosage));						
	}
	
}