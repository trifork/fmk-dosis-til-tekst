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
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import dk.medicinkortet.dosisstructuretext.TextHelper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageStructureWrapper;

public abstract class LongTextConverterImpl {
	
	protected static final String LONG_DATE_FORMAT = "EEEEEEE 'den' d'.' MMMMMMM yyyy";
	protected static final String LONG_DATE_TIME_FORMAT = "EEEEEEE 'den' d'.' MMMMMMM yyyy 'kl.' HH:mm:ss";
	protected static final String DAY_FORMAT = "EEEEEEE";
	protected static final String INDENT = "   ";
	
	abstract public boolean canConvert(DosageWrapper dosageStructure);

	abstract public String doConvert(DosageWrapper dosageStructure);

	protected void appendDosageStart(StringBuilder s, DosageStructureWrapper dosageStructure) {
		s.append("Doseringsforløbet starter "+datesToLongText(dosageStructure.getStartDate(), dosageStructure.getStartDateTime()));
	}
	
	protected String datesToLongText(Date startDate, Date startDateTime) {
		if(startDate==null && startDateTime==null)
			throw new IllegalArgumentException();
		if(startDate!=null) {
			SimpleDateFormat f = new SimpleDateFormat(LONG_DATE_FORMAT, new Locale("da", "DK"));
			return f.format(startDate);
		}
		else { 
			SimpleDateFormat f = new SimpleDateFormat(LONG_DATE_TIME_FORMAT, new Locale("da", "DK"));
			return f.format(startDateTime);
		}
	}
	
	protected int appendDays(StringBuilder s, DosageStructureWrapper dosageStructure) {
		int appendedLines = 0;
		for(int dayIndex=0; dayIndex<dosageStructure.getDays().size(); dayIndex++) {
			appendedLines++;
			if(dayIndex>0)
				s.append("\n");
			s.append(INDENT+makeDaysLabel(
					dosageStructure,
					dosageStructure.getDays().get(dayIndex)));
			s.append(makeDaysDosage(dosageStructure, dosageStructure.getDays().get(dayIndex)));
		}		
		return appendedLines;
	}
	
	protected String makeDaysLabel(DosageStructureWrapper dosageStructure, DayWrapper day) {
		if(day.getDayNumber()==0) {
			if(day.containsAccordingToNeedDosesOnly())
				return "Efter behov: ";
			else 
				return "Dag ikke angivet: ";
		}
		else {
			return makeDayString(dosageStructure.getStartDateOrDateTime(), day.getDayNumber())+": ";
		}		
	}

	protected String makeDaysDosage(DosageStructureWrapper dosageStructure, DayWrapper day) {
		StringBuilder s = new StringBuilder();
		
		if(day.getNumberOfDoses()==1) {
			s.append(makeOneDose(day.getDose(0), dosageStructure.getUnit(), dosageStructure.getUnitSingular(), dosageStructure.getUnitPlural(), dosageStructure.getSupplText()));
			if(day.containsAccordingToNeedDosesOnly() && day.getDayNumber()>0)
				s.append(" højst 1 gang daglig");
		}
		else if(day.getNumberOfDoses()>1 && day.allDosesAreTheSame()) {
			s.append(makeOneDose(day.getDose(0), dosageStructure.getUnit(), dosageStructure.getUnitSingular(), dosageStructure.getUnitPlural(), dosageStructure.getSupplText()));
			if(day.containsAccordingToNeedDosesOnly() && day.getDayNumber()>0)
				s.append(" højst "+day.getNumberOfDoses()+" gange daglig");
			else
				s.append(" "+day.getNumberOfDoses()+" gange");
		}
		else {
			for(int d=0; d<day.getNumberOfDoses(); d++) {
				s.append(makeOneDose(day.getDose(d), dosageStructure.getUnit(), dosageStructure.getUnitSingular(), dosageStructure.getUnitPlural(), dosageStructure.getSupplText()));
				if(d<day.getNumberOfDoses()-1)
					s.append(" + ");
			}
		}
		return s.toString();
	}

	protected String makeDayString(Date startDateOrDateTime, int dayNumber) {
		GregorianCalendar c = makeFromDateOnly(startDateOrDateTime);
		c.add(GregorianCalendar.DATE, dayNumber-1);
		SimpleDateFormat f = new SimpleDateFormat(LONG_DATE_FORMAT, new Locale("da", "DK"));
		String dateString = f.format(c.getTime());
		dateString = Character.toUpperCase(dateString.charAt(0)) + dateString.substring(1);
		return dateString;
	}
	
	protected static GregorianCalendar makeFromDateOnly(Date date) {
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(date);
		c.set(GregorianCalendar.HOUR, 0);
		c.set(GregorianCalendar.MINUTE, 0);
		c.set(GregorianCalendar.SECOND, 0);
		c.set(GregorianCalendar.MILLISECOND, 0);
		return c;
	}
	
	protected StringBuilder makeOneDose(DoseWrapper dose, String unit, String unitSingular, String unitPlural, String supplText) {
		StringBuilder s = new StringBuilder();
		s.append(dose.getAnyDoseQuantityString());
		s.append(" ").append(TextHelper.getUnit(dose, unit, unitSingular, unitPlural));
		if(dose.getLabel().length()>0)
			s.append(" ").append(dose.getLabel());
		if(dose.isAccordingToNeed())
			s.append(" efter behov");
		if(supplText!=null)
			s.append(" ").append(supplText);
		// Handle suppl. text in 2008 namespace
		if(dose.getSupplText()!=null)
			s.append(" ").append(dose.getSupplText());
		else if(dose.getMinimalSupplText()!=null && dose.getMaximalSupplText()!=null && dose.getMinimalSupplText().equals(dose.getMaximalSupplText())) 
			s.append(" ").append(dose.getMinimalSupplText());			
		else if(dose.getMinimalSupplText()!=null && dose.getMaximalSupplText()!=null && !dose.getMinimalSupplText().equals(dose.getMaximalSupplText())) 
			s.append(" ").append(dose.getMinimalSupplText()).append(" / ").append(dose.getMaximalSupplText());			
		else if(dose.getMinimalSupplText()!=null)
			s.append(" ").append(dose.getMinimalSupplText());
		else if(dose.getMaximalSupplText()!=null)
			s.append(" ").append(dose.getMaximalSupplText());
		return s;
	}

	protected void appendNoteText(StringBuilder s, DosageStructureWrapper dosageStructure) {
		if(isVarying(dosageStructure) && isComplex(dosageStructure))
			s.append(".\nBemærk at doseringen varierer og har et komplekst forløb:\n");
		else if(isVarying(dosageStructure))
			s.append(".\nBemærk at doseringen varierer:\n");
		else if(isComplex(dosageStructure))
			s.append(".\nBemærk at doseringen har et komplekst forløb:\n");
		else
			s.append(":\n");
	}
	
	protected boolean isComplex(DosageStructureWrapper dosageStructure) {
		if(dosageStructure.getDays().size()==1)
			return false;
		for(int d=1; d<dosageStructure.getDays().size(); d++) {
			if(dosageStructure.getDays().get(d-1).getDayNumber()!=d)
				return true;
		}
		return false;
	}
	
	protected boolean isVarying(DosageStructureWrapper dosageStructure) {
		return !dosageStructure.allDaysAreTheSame();
	}	
	
}
