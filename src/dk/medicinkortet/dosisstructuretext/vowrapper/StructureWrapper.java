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

package dk.medicinkortet.dosisstructuretext.vowrapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import dk.medicinkortet.dosisstructuretext.Interval;

public class StructureWrapper {
	
	// Mapped values
	private int iterationInterval;
	private String supplText;	
	private DateOrDateTimeWrapper startDateOrDateTime;
	private DateOrDateTimeWrapper endDateOrDateTime;
	private SortedSet<DayWrapper> days;
	
	// Cached values
	private Boolean areAllDaysTheSame;
	private Boolean areAllDosesTheSame;
	private ArrayList<DayWrapper> daysAsList;

	private static final Comparator<DayWrapper> DAY_COMPARATOR = new Comparator<DayWrapper>() {
		@Override
		public int compare(DayWrapper o1, DayWrapper o2) {
			return o1.getDayNumber() - o2.getDayNumber();
		}
	};
	
	/**
	 * Factory metod to create structured dosages
	 */
	public static StructureWrapper makeStructure(int iterationInterval, String supplText, DateOrDateTimeWrapper startDateOrDateTime, DateOrDateTimeWrapper endDateOrDateTime, DayWrapper... days) {
		TreeSet<DayWrapper> set = new TreeSet<DayWrapper>(DAY_COMPARATOR);
		set.addAll(Arrays.asList(days));
		return new StructureWrapper(iterationInterval, supplText, startDateOrDateTime, endDateOrDateTime, set);
	}
	
	/**
	 * Factory metod to create structured dosages
	 */
	public static StructureWrapper makeStructure(int iterationInterval, String supplText, DateOrDateTimeWrapper startDateOrDateTime, DateOrDateTimeWrapper endDateOrDateTime, Collection<DayWrapper> days) {
		if(days instanceof SortedSet<?>)
			return new StructureWrapper(iterationInterval, supplText, startDateOrDateTime, endDateOrDateTime, (SortedSet<DayWrapper>)days);
		else {
			TreeSet<DayWrapper> set = new TreeSet<DayWrapper>(DAY_COMPARATOR);
			set.addAll(days);
			return new StructureWrapper(iterationInterval, supplText, startDateOrDateTime, endDateOrDateTime, set);
		}
	}
	
	private StructureWrapper(int iterationInterval, String supplText, 
			DateOrDateTimeWrapper startDateOrDateTime, DateOrDateTimeWrapper endDateOrDateTime,
			SortedSet<DayWrapper> days) {
		this.iterationInterval = iterationInterval;
		this.supplText = supplText;
		this.startDateOrDateTime = startDateOrDateTime;
		this.endDateOrDateTime = endDateOrDateTime;
		if(days==null)
			throw new NullPointerException();
		if(days.size()==0)
			throw new IllegalArgumentException();
		this.days = days;
	}

	public int getIterationInterval() {
		return iterationInterval;
	}

	public String getSupplText() {
		return supplText;
	}

	public DateOrDateTimeWrapper getStartDateOrDateTime() {
		return startDateOrDateTime;
	}

	public DateOrDateTimeWrapper getEndDateOrDateTime() {
		return endDateOrDateTime;
	}
	
	public boolean startsAndEndsSameDay() {
		if(getStartDateOrDateTime()==null || getEndDateOrDateTime()==null)
			return false;
		GregorianCalendar cal1 = new GregorianCalendar();
		cal1.setTime(getStartDateOrDateTime().getDateOrDateTime());
		GregorianCalendar cal2 = new GregorianCalendar();
		cal2.setTime(getEndDateOrDateTime().getDateOrDateTime());
		return cal1.get(GregorianCalendar.YEAR) == cal2.get(GregorianCalendar.YEAR) &&
				cal1.get(GregorianCalendar.DAY_OF_YEAR) == cal2.get(GregorianCalendar.DAY_OF_YEAR);
}	

	public SortedSet<DayWrapper> getDays() {
		return days;
	}	
	
	public List<DayWrapper> getDaysAsList() {
		if(daysAsList==null)
			daysAsList = new ArrayList<DayWrapper>(getDays());
		return daysAsList;
	}
	
	public DayWrapper getDay(int dayNumber) {
		for(DayWrapper day: days) 
			if(day.getDayNumber()==dayNumber)
				return day;
		return null;
	}
	
	public boolean allDaysAreTheSame() {
		if(areAllDaysTheSame==null) {
			areAllDaysTheSame = true;
			DayWrapper day0 = null;
			for(DayWrapper day: days) {
				if(day0==null) {
					day0 = day;
				}
				else {
					if(day0.getNumberOfDoses()!=day.getNumberOfDoses()) {
						areAllDaysTheSame = false;
						break;						
					}
					else {
						for(int d=0; d<day0.getNumberOfDoses(); d++) {
							if(!day0.getAllDoses().get(d).theSameAs(day.getAllDoses().get(d))) {
								areAllDaysTheSame = false;
								break;						
							}
						}
					}
				}
			}
		}
		return areAllDaysTheSame;
	}	

	public boolean daysAreInUninteruptedSequenceFromOne() {
		int dayNumber = 1;
		for(DayWrapper day: getDays()) {
			if(day.getDayNumber()!=dayNumber)
				return false;
			dayNumber++;
		}
		return true;
	}
	
	/**
	 * Compares dosage quantities and the dosages label (the type of the dosage)
	 * @return true if all dosages are of the same type and has the same quantity
	 */
	public boolean allDosesAreTheSame() {
		if(areAllDosesTheSame==null) {
			areAllDosesTheSame = true;
			DoseWrapper dose0 = null;
			for(DayWrapper day: days) {
				for(DoseWrapper dose: day.getAllDoses()) {
					if(dose0==null) {
						dose0 = dose;
					}
					else if(!dose0.theSameAs(dose)) {
						areAllDosesTheSame = false;
						break;
					}	
				}
			}
		}
		return areAllDosesTheSame;
	}
	
	
	public boolean containsMorningNoonEveningNightDoses() {
		for(DayWrapper day: days) 
			if(day.containsMorningNoonEveningNightDoses())
				return true;
		return false;
	}	
	
	public boolean containsPlainDose() {		
		for(DayWrapper day: days) 
			if(day.containsPlainDose())
				return true;
		return false;
	}	

	public boolean containsTimedDose() {		
		for(DayWrapper day: days) 
			if(day.containsTimedDose())
				return true;
		return false;
	}	
	
	public boolean containsAccordingToNeedDosesOnly() {		
		for(DayWrapper day: days) 
			if(!day.containsAccordingToNeedDosesOnly())
				return false;
		return true;
	}

	public boolean containsAccordingToNeedDose() {		
		for(DayWrapper day: days) 
			if(day.containsAccordingToNeedDose())
				return true;
		return false;
	}

	public Interval<BigDecimal> getSumOfDoses() {
		Interval<BigDecimal> allSum = null;
		for(DayWrapper day: days) {
			Interval<BigDecimal> daySum = day.getSumOfDoses(); 
			if(allSum==null) {
				allSum = daySum;
			}
			else {
				allSum = new Interval<BigDecimal> (
					allSum.getMinimum().add(daySum.getMinimum()), 
					allSum.getMaximum().add(daySum.getMaximum()));
			}
		}
		return allSum;
	}

}
