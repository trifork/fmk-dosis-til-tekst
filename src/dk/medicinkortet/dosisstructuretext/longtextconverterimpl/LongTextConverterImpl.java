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

import java.util.Date;

import dk.medicinkortet.dosisstructuretext.TextHelper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DateOrDateTimeWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructureWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.UnitOrUnitsWrapper;

public abstract class LongTextConverterImpl {
	
	abstract public boolean canConvert(DosageWrapper dosageStructure);

	abstract public String doConvert(DosageWrapper dosageStructure);

	protected void appendDosageStart(StringBuilder s, DateOrDateTimeWrapper startDateOrDateTime) {
		s.append("Doseringsforløbet starter "+datesToLongText(startDateOrDateTime));
	}
	
	protected String datesToLongText(DateOrDateTimeWrapper startDateOrDateTime) {
		if(startDateOrDateTime==null)
			throw new IllegalArgumentException();
		if(startDateOrDateTime.getDate()!=null) {
			return TextHelper.formatLongDate(startDateOrDateTime.getDate());
		} 
		else {
            Date dateTime = startDateOrDateTime.getDateTime();
            // We do not want to show seconds precision if seconds are not specified or 0
            if (haveSeconds(dateTime)) {
                return TextHelper.formatLongDateTime(dateTime);
            } 
            else {
                return TextHelper.formatLongDateNoSecs(dateTime);
            }
		}
	}

    private boolean haveSeconds(Date dateTime) {
        long secs = dateTime.getTime() / 1000;
        if (secs % 60 != 0) {
            return true;
        }
        return false;
    }

    protected int appendDays(StringBuilder s, UnitOrUnitsWrapper unitOrUnits, StructureWrapper structure) {
		int appendedLines = 0;
		for(DayWrapper day: structure.getDays()) {
			appendedLines++;
			if(appendedLines>1)
				s.append("\n");
			s.append(TextHelper.INDENT+makeDaysLabel(structure, day));
			s.append(makeDaysDosage(unitOrUnits, structure, day));
		}		
		return appendedLines;
	}
	
	protected String makeDaysLabel(StructureWrapper structure, DayWrapper day) {
		if(day.getDayNumber()==0) {
			if(day.containsAccordingToNeedDosesOnly())
				return "Efter behov: ";
			else 
				return "Dag ikke angivet: ";
		}
		else {
			return TextHelper.makeDayString(structure.getStartDateOrDateTime(), day.getDayNumber())+": ";
		}		
	}

	protected String makeDaysDosage(UnitOrUnitsWrapper unitOrUnits, StructureWrapper structure, DayWrapper day) {
		StringBuilder s = new StringBuilder();
		
		if(day.getNumberOfDoses()==1) {
			s.append(makeOneDose(day.getDose(0), unitOrUnits, structure.getSupplText()));
			if(day.containsAccordingToNeedDosesOnly() && day.getDayNumber()>0)
				s.append(" højst 1 gang daglig");
		}
		else if(day.getNumberOfDoses()>1 && day.allDosesAreTheSame()) {
			s.append(makeOneDose(day.getDose(0), unitOrUnits, structure.getSupplText()));
			if(day.containsAccordingToNeedDosesOnly() && day.getDayNumber()>0)
				s.append(" højst "+day.getNumberOfDoses()+" gange daglig");
			else
				s.append(" "+day.getNumberOfDoses()+" gange");
		}
		else {
			for(int d=0; d<day.getNumberOfDoses(); d++) {
				s.append(makeOneDose(day.getDose(d), unitOrUnits, structure.getSupplText()));
				if(d<day.getNumberOfDoses()-1)
					s.append(" + ");
			}
		}
		return s.toString();
	}


	
	protected StringBuilder makeOneDose(DoseWrapper dose, UnitOrUnitsWrapper unitOrUnits, String supplText) {
		StringBuilder s = new StringBuilder();
		s.append(dose.getAnyDoseQuantityString());
		s.append(" ").append(TextHelper.getUnit(dose, unitOrUnits));
		if(dose.getLabel().length()>0)
			s.append(" ").append(dose.getLabel());
		if(dose.isAccordingToNeed())
			s.append(" efter behov");
		if(supplText!=null)
			s.append(" ").append(supplText);
		return s;
	}

	protected void appendNoteText(StringBuilder s, StructureWrapper structure) {
		if(isVarying(structure) && isComplex(structure))
			s.append(".\nBemærk at doseringen varierer og har et komplekst forløb:\n");
		else if(isVarying(structure))
			s.append(".\nBemærk at doseringen varierer:\n");
		else if(isComplex(structure))
			s.append(".\nBemærk at doseringen har et komplekst forløb:\n");
		else
			s.append(":\n");
	}
	
	protected boolean isComplex(StructureWrapper structure) {
		if(structure.getDays().size()==1)
			return false;
		return !structure.daysAreInUninteruptedSequenceFromOne();
	}
	
	protected boolean isVarying(StructureWrapper structure) {
		return !structure.allDaysAreTheSame();
	}	
	
}
