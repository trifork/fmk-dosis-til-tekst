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

package dk.medicinkortet.dosisstructuretext.longtextconverterimpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageStructureWrapper;

public class WeeklyRepeatedConverterImpl extends LongTextConverterImpl {

	@Override
	public boolean canConvert(DosageWrapper dosage) {
		if(dosage.getDosageStructure()==null)
			return false;
		DosageStructureWrapper dosageStructure = dosage.getDosageStructure();
		if(dosageStructure.getIterationInterval()!=7)
			return false;
		if(dosageStructure.getStartDateOrDateTime().equals(dosageStructure.getEndDateOrDateTime()))
			return false; 
		if(dosageStructure.getDays().size()>7)
			return false;
		if(dosageStructure.getFirstDay().getDayNumber()==0)
			return false;
		if(dosageStructure.getMaxDay().getDayNumber()>7)
			return false;
		if(!dosageStructure.allDosesHaveTheSameSupplText()) // Special case needed for 2008 NS as it may contain multiple texts 
			return false;
		return true;
	}

	@Override
	public String doConvert(DosageWrapper dosage) {
		return convert(dosage.getDosageStructure());
	}

	public String convert(DosageStructureWrapper dosageStructure) {
		StringBuilder s = new StringBuilder();		
		appendDosageStart(s, dosageStructure);
		s.append(", forløbet gentages hver uge");
		appendNoteText(s, dosageStructure);
		s.append(INDENT+"Doseringsforløb:\n");
		appendDays(s, dosageStructure);
		return s.toString();	
	}

	@Override
	protected int appendDays(StringBuilder s, DosageStructureWrapper dosageStructure) {
		// Make a sorted list of weekdays
		ArrayList<DayOfWeek> daysOfWeek = sortDaysOfWeek(dosageStructure);		
		// Make text
		int appendedLines = 0;
		for(DayOfWeek e: daysOfWeek) {
			if(appendedLines>0)
				s.append("\n");
			appendedLines++;
			s.append(INDENT+e.name+": ");
			s.append(makeDaysDosage(dosageStructure, e.day));
		}		
		return appendedLines;
	}

	public static ArrayList<DayOfWeek> sortDaysOfWeek(DosageStructureWrapper dosageStructure) {
		// First convert all days (up to 7) to day of week and DK name ((1, Mandag) etc).
		ArrayList<DayOfWeek> daysOfWeek = new ArrayList<DayOfWeek>();
		for(DayWrapper day: dosageStructure.getDays()) {
			daysOfWeek.add(makeDayOfWeekAndName(dosageStructure.getStartDateOrDateTime(), day));
		}
		// Sort according to day of week (Monday always first)
		Collections.sort(daysOfWeek, new Comparator<DayOfWeek>() {
			@Override
			public int compare(DayOfWeek e1, DayOfWeek e2) {
				return e1.dayOfWeek.compareTo(e2.dayOfWeek);
			}
		});		
		return daysOfWeek;
	}
	
	public static class DayOfWeek {
		public Integer dayOfWeek;
		public String name;
		public DayWrapper day;
	}
	
	private static DayOfWeek makeDayOfWeekAndName(Date startDateOrDateTime, DayWrapper day) {
		DayOfWeek d = new DayOfWeek();
		d.day = day;
		GregorianCalendar c = makeFromDateOnly(startDateOrDateTime);
		c.add(GregorianCalendar.DATE, day.getDayNumber()-1);
		SimpleDateFormat f = new SimpleDateFormat(DAY_FORMAT, new Locale("da", "DK"));
		d.dayOfWeek = usToDkDayOfWeek(c.get(GregorianCalendar.DAY_OF_WEEK));
		String dateString = f.format(c.getTime());
		d.name = Character.toUpperCase(dateString.charAt(0)) + dateString.substring(1);
		return d;
	}
	
	private static int usToDkDayOfWeek(int us) {
		switch(us) {
			case GregorianCalendar.MONDAY: return 1;
			case GregorianCalendar.TUESDAY: return 2;
			case GregorianCalendar.WEDNESDAY: return 3;
			case GregorianCalendar.THURSDAY: return 4;
			case GregorianCalendar.FRIDAY: return 5;
			case GregorianCalendar.SATURDAY: return 6;
			case GregorianCalendar.SUNDAY: return 7;
			default: throw new RuntimeException(""+us);
		}
	}
	
	
}
