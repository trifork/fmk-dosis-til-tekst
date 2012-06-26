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
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuredDosageWrapper;

public class RepeatedConverterImpl extends ShortTextConverterImpl {

	@Override
	public boolean canConvert(DosageWrapper dosage) {
		if(dosage.getDosageTimes()==null)
			return false;
		StructuredDosageWrapper dosageTimes = dosage.getDosageTimes();
		if(dosageTimes.getIterationInterval()==0)
			return false;
		if(dosageTimes.getDays().size()!=1)
			return false;
		DayWrapper day = dosageTimes.getDays().get(0);
		if(day.containsAccordingToNeedDose())
			return false;
		if(day.getMorningDose()!=null || day.getNoonDose()!=null 
				|| day.getEveningDose()!=null || day.getNightDose()!=null)
			return false;
		if(!day.allDosesAreTheSame())
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
		
		text.append(toValue(day.getAllDoses().get(0), dosageTimes.getUnit()));
		
//		if(day.getAllDoses().get(0) instanceof TimedDoseWrapper)
//			text.append(" "+((TimedDoseWrapper)day.getAllDoses().get(0)).getTime());
		
		if(dosageTimes.getIterationInterval()==1 && day.getNumberOfDoses()==1)
			text.append(" daglig");
		else if(dosageTimes.getIterationInterval()==1 && day.getNumberOfDoses()>1)
			text.append(" "+day.getNumberOfDoses()+" gange daglig");
		else if(numberOfWholeWeeks(dosageTimes.getIterationInterval())==1 && day.getNumberOfDoses()==1)
			text.append(" 1 gang om ugen");
		else if(numberOfWholeWeeks(dosageTimes.getIterationInterval())==1 && day.getNumberOfDoses()>1)
			text.append(" "+day.getNumberOfDoses()+" gange samme dag 1 gang om ugen");
		else if(numberOfWholeMonths(dosageTimes.getIterationInterval())==1 && day.getNumberOfDoses()==1)
			text.append(" 1 gang om måneden");
		else if(numberOfWholeMonths(dosageTimes.getIterationInterval())==1 && day.getNumberOfDoses()>=1)
			text.append(" "+day.getNumberOfDoses()+" gange samme dag 1 gang om måneden");
		else if(dosageTimes.getIterationInterval()>1 && day.getNumberOfDoses()==1)
			text.append(" hver "+dosageTimes.getIterationInterval()+". dag");
		else if(dosageTimes.getIterationInterval()>1 && day.getNumberOfDoses()>=1)
			text.append(" "+day.getNumberOfDoses()+" gange samme dag hver "+dosageTimes.getIterationInterval()+". dag");
		else
			return null; // Something unexpected happened!
		
		if(dosageTimes.getUniqueSupplText()!=null)
			text.append(" ").append(dosageTimes.getUniqueSupplText());
		
		return text.toString();
	}

	private int numberOfWholeWeeks(int iterationInterval) {
		int numberOfWholeWeeks =  iterationInterval/7;
		if(numberOfWholeWeeks*7!=iterationInterval)
			numberOfWholeWeeks = -1;
		return numberOfWholeWeeks;
	}

	private int numberOfWholeMonths(int iterationInterval) {
		int numberOfWholeMonths = iterationInterval/30;
		if(numberOfWholeMonths*30!=iterationInterval)
			numberOfWholeMonths = -1;
		return numberOfWholeMonths;
	}
	
	
	
	
	
}
