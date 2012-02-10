package dk.medicinkortet.dosisstructuretext.converterimpl;

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
		
		if(day.getMorningDose()!=null) {
			text.append(toValue(day.getMorningDose(), dosageTimes.getUnit()));
		}
		
		if(day.getNoonDose()!=null) {
			if(day.getMorningDose()!=null && (day.getEveningDose()!=null || day.getNightDose()!=null))
				text.append(", ");
			else if(day.getMorningDose()!=null)
				text.append(" og ");			
			if(!day.allDosesHaveTheSameQuantity())
				text.append(toValue(day.getNoonDose(), dosageTimes.getUnit()));
			else if(day.getMorningDose()!=null)
				text.append(day.getNoonDose().getLabel());
			else 
				text.append(toValue(day.getNoonDose(), dosageTimes.getUnit()));
		}
		
		if(day.getEveningDose()!=null) {
			if((day.getMorningDose()!=null || day.getNoonDose()!=null) && day.getNightDose()!=null)
				text.append(", ");
			else if(day.getMorningDose()!=null || day.getNoonDose()!=null)
				text.append(" og ");			
			if(!day.allDosesHaveTheSameQuantity())
				text.append(toValue(day.getEveningDose(), dosageTimes.getUnit()));
			else if(day.getMorningDose()!=null || day.getNoonDose()!=null)
				text.append(day.getEveningDose().getLabel());			
			else
				text.append(toValue(day.getEveningDose(), dosageTimes.getUnit()));
		}
		
		if(day.getNightDose()!=null) {
			if(day.getMorningDose()!=null || day.getNoonDose()!=null || day.getEveningDose()!=null)
				text.append(" og ");			
			if(!day.allDosesHaveTheSameQuantity())
				text.append(toValue(day.getNightDose(), dosageTimes.getUnit()));
			else if(day.getMorningDose()!=null || day.getNoonDose()!=null || day.getEveningDose()!=null)
				text.append(day.getNightDose().getLabel());
			else
				text.append(toValue(day.getNightDose(), dosageTimes.getUnit()));
		}
	
		if(dosageTimes.getUniqueSupplText()!=null)
			text.append(" ").append(dosageTimes.getUniqueSupplText());
		
		return text.toString();
	}


}
