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

import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructureWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.UnitOrUnitsWrapper;

public class MorningNoonEveningNightConverterImpl extends ShortTextConverterImpl {

	@Override
	public boolean canConvert(DosageWrapper dosage) {
		if(dosage.getStructures()==null)
			return false;
		if(dosage.getStructures().getStructures().size()!=1)
			return false;	
		StructureWrapper structure = dosage.getStructures().getStructures().first();
		if(structure.getIterationInterval()!=1)
			return false;
		if(structure.getDays().size()!=1)
			return false;
		DayWrapper day = structure.getDays().first();
		if(day.getDayNumber()!=1)
			return false;
		if(day.containsPlainDose() || day.containsTimedDose())
			return false;
		return true;
	}
	
	@Override
	public String doConvert(DosageWrapper dosage) {
		StructureWrapper structure = dosage.getStructures().getStructures().first();
		StringBuilder text = new StringBuilder();
		DayWrapper day = structure.getDays().first();
		appendMorning(day, text, dosage.getStructures().getUnitOrUnits());
		appendNoon(day, text, dosage.getStructures().getUnitOrUnits());
		appendEvening(day, text, dosage.getStructures().getUnitOrUnits());
		appendNight(day, text, dosage.getStructures().getUnitOrUnits());
		appendSupplText(structure.getSupplText(), text);
		return text.toString();
	}

	public static void appendMorning(DayWrapper day, StringBuilder text, UnitOrUnitsWrapper unitOrUnits) {
		if(day.getMorningDose()!=null) {
			text.append(toValue(day.getMorningDose(), unitOrUnits));
			if(day.getMorningDose().isAccordingToNeed())
				text.append(" efter behov");
		}		
	}
	
	public static void appendNoon(DayWrapper day, StringBuilder text, UnitOrUnitsWrapper unitOrUnits) {
		if(day.getNoonDose()!=null) {
			if(day.getMorningDose()!=null && (day.getEveningDose()!=null || day.getNightDose()!=null))
				text.append(", ");
			else if(day.getMorningDose()!=null)
				text.append(" og ");			
			if(!day.allDosesHaveTheSameQuantity())
				text.append(toValue(day.getNoonDose(), unitOrUnits));
			else if(day.getMorningDose()!=null)
				text.append(day.getNoonDose().getLabel());
			else 
				text.append(toValue(day.getNoonDose(), unitOrUnits));
			if(day.getNoonDose().isAccordingToNeed())
				text.append(" efter behov");
		}
	}
	
	public static void appendEvening(DayWrapper day, StringBuilder text, UnitOrUnitsWrapper unitOrUnits) {
		if(day.getEveningDose()!=null) {
			if((day.getMorningDose()!=null || day.getNoonDose()!=null) && day.getNightDose()!=null)
				text.append(", ");
			else if(day.getMorningDose()!=null || day.getNoonDose()!=null)
				text.append(" og ");			
			if(!day.allDosesHaveTheSameQuantity())
				text.append(toValue(day.getEveningDose(), unitOrUnits));
			else if(day.getMorningDose()!=null || day.getNoonDose()!=null)
				text.append(day.getEveningDose().getLabel());			
			else
				text.append(toValue(day.getEveningDose(), unitOrUnits));
			if(day.getEveningDose().isAccordingToNeed())
				text.append(" efter behov");			
		}		
	}
	
	public static void appendNight(DayWrapper day, StringBuilder text, UnitOrUnitsWrapper unitOrUnits) {
		if(day.getNightDose()!=null) {
			if(day.getMorningDose()!=null || day.getNoonDose()!=null || day.getEveningDose()!=null)
				text.append(" og ");			
			if(!day.allDosesHaveTheSameQuantity())
				text.append(toValue(day.getNightDose(), unitOrUnits));
			else if(day.getMorningDose()!=null || day.getNoonDose()!=null || day.getEveningDose()!=null)
				text.append(day.getNightDose().getLabel());
			else
				text.append(toValue(day.getNightDose(), unitOrUnits));
			if(day.getNightDose().isAccordingToNeed())
				text.append(" efter behov");			
		}		
	}

	public static void appendSupplText(String supplText, StringBuilder text) {
		if(supplText!=null)
			text.append(" ").append(supplText);		
	}

}
