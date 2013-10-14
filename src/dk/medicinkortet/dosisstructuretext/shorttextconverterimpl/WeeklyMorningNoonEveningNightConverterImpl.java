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

package dk.medicinkortet.dosisstructuretext.shorttextconverterimpl;

import java.util.SortedSet;

import dk.medicinkortet.dosisstructuretext.longtextconverterimpl.WeeklyRepeatedConverterImpl.DayOfWeek;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructureWrapper;

public class WeeklyMorningNoonEveningNightConverterImpl extends ShortTextConverterImpl {

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
		if(structure.containsAccordingToNeedDose() || structure.containsPlainDose() || structure.containsTimedDose())
			return false;
		if(!structure.allDaysAreTheSame()) // Otherwise the text is too long, and cannot fit into a short text
			return false;
		return true;		
	}

	@Override
	public String doConvert(DosageWrapper dosage) {
		
		StructureWrapper structure = dosage.getStructures().getStructures().first();
		
		SortedSet<DayOfWeek> daysOfWeek = 
				dk.medicinkortet.dosisstructuretext.longtextconverterimpl.WeeklyRepeatedConverterImpl.sortDaysOfWeek(structure);
		StringBuilder text = new StringBuilder();		
		
		DayOfWeek firstDay = daysOfWeek.first();
		MorningNoonEveningNightConverterImpl.appendMorning(firstDay.getDay(), text, dosage.getStructures().getUnitOrUnits());
		MorningNoonEveningNightConverterImpl.appendNoon(firstDay.getDay(), text, dosage.getStructures().getUnitOrUnits());
		MorningNoonEveningNightConverterImpl.appendEvening(firstDay.getDay(), text, dosage.getStructures().getUnitOrUnits());
		MorningNoonEveningNightConverterImpl.appendNight(firstDay.getDay(), text, dosage.getStructures().getUnitOrUnits());
		int i = 0;
		for(DayOfWeek d: daysOfWeek) {
			if(i==daysOfWeek.size()-1 && daysOfWeek.size()>1)
				text.append(" og ").append(d.getName().toLowerCase());
			else if(i==0) 
				text.append(" ").append(d.getName().toLowerCase());
			else if(i>0)
				text.append(", ").append(d.getName().toLowerCase());
			i++;
		}			
		text.append(" hver uge");
		
		MorningNoonEveningNightConverterImpl.appendSupplText(structure.getSupplText(), text);
		return text.toString();
	}

	
	
	
}
