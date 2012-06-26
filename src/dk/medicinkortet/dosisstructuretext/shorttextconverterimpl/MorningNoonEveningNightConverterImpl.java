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
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuredDosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;

public class MorningNoonEveningNightConverterImpl extends ShortTextConverterImpl {

	@Override
	public boolean canConvert(DosageWrapper dosage) {
		if(dosage.getDosageTimes()==null)
			return false;
		StructuredDosageWrapper dosageTimes = dosage.getDosageTimes();
		if(dosageTimes.getIterationInterval()!=1)
			return false;
		if(dosageTimes.getDays().size()!=1)
			return false;
		DayWrapper day = dosageTimes.getDays().get(0);
		if(day.getDayNumber()!=1)
			return false;
		if(day.containsAccordingToNeedDose() || day.containsPlainDose() || day.containsTimedDose())
			return false;
		if(!dosageTimes.allDosesHaveTheSameSupplText()) // Special case needed for 2008 NS as it may contain multiple texts 
			return false;
		return true;
	}
	
	@Override
	public String doConvert(DosageWrapper dosage) {
		StructuredDosageWrapper dosageTimes = dosage.getDosageTimes();
		StringBuilder text = new StringBuilder();
		DayWrapper day = dosageTimes.getDays().get(0);
		appendMorning(day, text, dosageTimes.getUnit());
		appendNoon(day, text, dosageTimes.getUnit());
		appendEvening(day, text, dosageTimes.getUnit());
		appendNight(day, text, dosageTimes.getUnit());
		appendSupplText(dosageTimes.getUniqueSupplText(), text);
		return text.toString();
	}

	public static void appendMorning(DayWrapper day, StringBuilder text, String unit) {
		if(day.getMorningDose()!=null) {
			text.append(toValue(day.getMorningDose(), unit));
		}		
	}
	
	public static void appendNoon(DayWrapper day, StringBuilder text, String unit) {
		if(day.getNoonDose()!=null) {
			if(day.getMorningDose()!=null && (day.getEveningDose()!=null || day.getNightDose()!=null))
				text.append(", ");
			else if(day.getMorningDose()!=null)
				text.append(" og ");			
			if(!day.allDosesHaveTheSameQuantity())
				text.append(toValue(day.getNoonDose(), unit));
			else if(day.getMorningDose()!=null)
				text.append(day.getNoonDose().getLabel());
			else 
				text.append(toValue(day.getNoonDose(), unit));
		}
	}
	
	public static void appendEvening(DayWrapper day, StringBuilder text, String unit) {
		if(day.getEveningDose()!=null) {
			if((day.getMorningDose()!=null || day.getNoonDose()!=null) && day.getNightDose()!=null)
				text.append(", ");
			else if(day.getMorningDose()!=null || day.getNoonDose()!=null)
				text.append(" og ");			
			if(!day.allDosesHaveTheSameQuantity())
				text.append(toValue(day.getEveningDose(), unit));
			else if(day.getMorningDose()!=null || day.getNoonDose()!=null)
				text.append(day.getEveningDose().getLabel());			
			else
				text.append(toValue(day.getEveningDose(), unit));
		}		
	}
	
	public static void appendNight(DayWrapper day, StringBuilder text, String unit) {
		if(day.getNightDose()!=null) {
			if(day.getMorningDose()!=null || day.getNoonDose()!=null || day.getEveningDose()!=null)
				text.append(" og ");			
			if(!day.allDosesHaveTheSameQuantity())
				text.append(toValue(day.getNightDose(), unit));
			else if(day.getMorningDose()!=null || day.getNoonDose()!=null || day.getEveningDose()!=null)
				text.append(day.getNightDose().getLabel());
			else
				text.append(toValue(day.getNightDose(), unit));
		}		
	}

	public static void appendSupplText(String supplText, StringBuilder text) {
		if(supplText!=null)
			text.append(" ").append(supplText);		
	}

}
