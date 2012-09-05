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
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageStructureWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;

public class MorningNoonEveningNightConverterImpl extends ShortTextConverterImpl {

	@Override
	public boolean canConvert(DosageWrapper dosage) {
		if(dosage.getDosageStructure()==null)
			return false;
		DosageStructureWrapper dosageStructure = dosage.getDosageStructure();
		if(dosageStructure.getIterationInterval()!=1)
			return false;
		if(dosageStructure.getDays().size()!=1)
			return false;
		DayWrapper day = dosageStructure.getDays().get(0);
		if(day.getDayNumber()!=1)
			return false;
		if(day.containsPlainDose() || day.containsTimedDose())
			return false;
		if(!dosageStructure.allDosesHaveTheSameSupplText()) // Special case needed for 2008 NS as it may contain multiple texts 
			return false;
		return true;
	}
	
	@Override
	public String doConvert(DosageWrapper dosage) {
		DosageStructureWrapper dosageStructure = dosage.getDosageStructure();
		StringBuilder text = new StringBuilder();
		DayWrapper day = dosageStructure.getDays().get(0);
		appendMorning(day, text, dosageStructure);
		appendNoon(day, text, dosageStructure);
		appendEvening(day, text, dosageStructure);
		appendNight(day, text, dosageStructure);
		appendSupplText(dosageStructure.getUniqueSupplText(), text);
		return text.toString();
	}

	public static void appendMorning(DayWrapper day, StringBuilder text, DosageStructureWrapper dosageStructure) {
		if(day.getMorningDose()!=null) {
			text.append(toValue(day.getMorningDose(), dosageStructure));
			if(day.getMorningDose().isAccordingToNeed())
				text.append(" efter behov");
		}		
	}
	
	public static void appendNoon(DayWrapper day, StringBuilder text, DosageStructureWrapper dosageStructure) {
		if(day.getNoonDose()!=null) {
			if(day.getMorningDose()!=null && (day.getEveningDose()!=null || day.getNightDose()!=null))
				text.append(", ");
			else if(day.getMorningDose()!=null)
				text.append(" og ");			
			if(!day.allDosesHaveTheSameQuantity())
				text.append(toValue(day.getNoonDose(), dosageStructure));
			else if(day.getMorningDose()!=null)
				text.append(day.getNoonDose().getLabel());
			else 
				text.append(toValue(day.getNoonDose(), dosageStructure));
			if(day.getNoonDose().isAccordingToNeed())
				text.append(" efter behov");
		}
	}
	
	public static void appendEvening(DayWrapper day, StringBuilder text, DosageStructureWrapper dosageStructure) {
		if(day.getEveningDose()!=null) {
			if((day.getMorningDose()!=null || day.getNoonDose()!=null) && day.getNightDose()!=null)
				text.append(", ");
			else if(day.getMorningDose()!=null || day.getNoonDose()!=null)
				text.append(" og ");			
			if(!day.allDosesHaveTheSameQuantity())
				text.append(toValue(day.getEveningDose(), dosageStructure));
			else if(day.getMorningDose()!=null || day.getNoonDose()!=null)
				text.append(day.getEveningDose().getLabel());			
			else
				text.append(toValue(day.getEveningDose(), dosageStructure));
			if(day.getEveningDose().isAccordingToNeed())
				text.append(" efter behov");			
		}		
	}
	
	public static void appendNight(DayWrapper day, StringBuilder text, DosageStructureWrapper dosageStructure) {
		if(day.getNightDose()!=null) {
			if(day.getMorningDose()!=null || day.getNoonDose()!=null || day.getEveningDose()!=null)
				text.append(" og ");			
			if(!day.allDosesHaveTheSameQuantity())
				text.append(toValue(day.getNightDose(), dosageStructure));
			else if(day.getMorningDose()!=null || day.getNoonDose()!=null || day.getEveningDose()!=null)
				text.append(day.getNightDose().getLabel());
			else
				text.append(toValue(day.getNightDose(), dosageStructure));
			if(day.getNightDose().isAccordingToNeed())
				text.append(" efter behov");			
		}		
	}

	public static void appendSupplText(String supplText, StringBuilder text) {
		if(supplText!=null)
			text.append(" ").append(supplText);		
	}

}
