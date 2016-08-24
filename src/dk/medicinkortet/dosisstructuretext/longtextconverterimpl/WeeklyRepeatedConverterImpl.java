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

import java.util.SortedSet;
import java.util.TreeSet;

import dk.medicinkortet.dosisstructuretext.TextHelper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructureWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.UnitOrUnitsWrapper;

public class WeeklyRepeatedConverterImpl extends LongTextConverterImpl {

	@Override
	public boolean canConvert(DosageWrapper dosage) {
		if(dosage.getStructures()==null)
			return false;
		if(dosage.getStructures().getStructures().size()!=1)
			return false;	
		StructureWrapper structure = dosage.getStructures().getStructures().first();
		if(structure.getIterationInterval()!=7)
			return false;
		if(structure.getStartDateOrDateTime().equals(structure.getEndDateOrDateTime()))
			return false; 
		if(structure.getDays().size()>7)
			return false;
		if(structure.getDays().first().getDayNumber()==0)
			return false;
		if(structure.getDays().last().getDayNumber()>7)
			return false;
		return true;
	}

	@Override
	public String doConvert(DosageWrapper dosage) {
		return convert(dosage.getStructures().getUnitOrUnits(), dosage.getStructures().getStructures().first());
	}

	public String convert(UnitOrUnitsWrapper unitOrUnits, StructureWrapper structure) {
		StringBuilder s = new StringBuilder();		
		appendDosageStart(s, structure.getStartDateOrDateTime());
		s.append(", forløbet gentages hver uge");
		if(structure.getEndDateOrDateTime() != null) {
			appendDosageEnd(s, structure.getEndDateOrDateTime());
		}
		appendNoteText(s, structure);
		s.append(TextHelper.INDENT+"Doseringsforløb:\n");
		appendDays(s, unitOrUnits, structure);
		return s.toString();	
	}

	@Override
	protected int appendDays(StringBuilder s, UnitOrUnitsWrapper unitOrUnits, StructureWrapper structure) {
		// Make a sorted list of weekdays
		SortedSet<DayOfWeek> daysOfWeek = sortDaysOfWeek(structure);
		int appendedLines = 0;
		for(DayOfWeek e: daysOfWeek) {
			if(appendedLines>0)
				s.append("\n");
			appendedLines++;
			s.append(TextHelper.INDENT+e.name+": ");
			s.append(makeDaysDosage(unitOrUnits, structure, e.day, true));
		}		
		return appendedLines;
	}

	public static SortedSet<DayOfWeek> sortDaysOfWeek(StructureWrapper structure) {
		// Convert all days (up to 7) to day of week and DK name ((1, Mandag) etc).
		// Sort according to day of week (Monday always first) using DayOfWeek's compareTo in SortedSet
		TreeSet<DayOfWeek> daysOfWeekSet = new TreeSet<DayOfWeek>();
		for(DayWrapper day: structure.getDays()) {
			daysOfWeekSet.add(TextHelper.makeDayOfWeekAndName(structure.getStartDateOrDateTime(), day, true));			
		}
		return daysOfWeekSet;
	}
	
	public static class DayOfWeek implements Comparable<DayOfWeek> {
		
		private Integer dayOfWeek;
		private String name;
		private DayWrapper day;
		
		public DayOfWeek(int dayOfWeek, String name, DayWrapper day) {
			super();
			this.dayOfWeek = dayOfWeek;
			this.name = name;
			this.day = day;
		}

		public int getDayOfWeek() {
			return dayOfWeek;
		}
		
		public String getName() {
			return name;
		}
		
		public DayWrapper getDay() {
			return day;
		}

		@Override
		public int compareTo(DayOfWeek other) {
			return this.dayOfWeek.compareTo(other.dayOfWeek);
		}
		
	}	
	
}
