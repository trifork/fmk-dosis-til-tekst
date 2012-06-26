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
import java.util.Collection;
import java.util.List;

import dk.medicinkortet.dosisstructuretext.Interval;
import dk.medicinkortet.web.shared.jaxb.dkma.medicinecard.DosageDayElementStructure;
import dk.medicinkortet.web.shared.jaxb.dkma.medicinecard.DosageTimeElementStructure;

public class DayWrapper {
		
	// Wrapped values
	private int dayNumber;
	private List<AccordingToNeedDoseWrapper> accordingToNeedDoses = new ArrayList<AccordingToNeedDoseWrapper>();
	private List<PlainDoseWrapper> plainDoses = new ArrayList<PlainDoseWrapper>();
	private List<TimedDoseWrapper> timedDoses = new ArrayList<TimedDoseWrapper>();
	private MorningDoseWrapper morningDose;
	private NoonDoseWrapper noonDose;
	private EveningDoseWrapper eveningDose;
	private NightDoseWrapper nightDose;
	
	// Helper / cached values
	private List<DoseWrapper> allDoses = new ArrayList<DoseWrapper>();	
	private Boolean areAllDosesTheSame;
	private Boolean areAllDosesHaveTheSameQuantity;
	
	public DayWrapper(DosageDayElementStructure dosageDayElementStructure) {
		this.dayNumber = dosageDayElementStructure.getDosageDayIdentifier();
		for(DosageTimeElementStructure dose: dosageDayElementStructure.getDosageTimeElementStructures()) {
			if(dose.getDosageTimeTime()!=null && dose.getDosageTimeTime().trim().length()>0) {
				TimedDoseWrapper d = new TimedDoseWrapper(dose);
				timedDoses.add(d);
				allDoses.add(d);
			}
			else {
				PlainDoseWrapper d = new PlainDoseWrapper(dose); // We keep doses in order, otherwise addAll would be nicer
				plainDoses.add(d);
				allDoses.add(d);
			}			
		}
		for(DosageTimeElementStructure dose: dosageDayElementStructure.getAccordingToNeedDosageTimeElementStructures()) {
			accordingToNeedDoses.add(new AccordingToNeedDoseWrapper(dose));
			allDoses.addAll(accordingToNeedDoses);
		}
		if(dosageDayElementStructure.getMorningDosageTimeElementStructure()!=null) {
			morningDose = new MorningDoseWrapper(dosageDayElementStructure.getMorningDosageTimeElementStructure());
			allDoses.add(morningDose);
		}
		if(dosageDayElementStructure.getNoonDosageTimeElementStructure()!=null) {
			noonDose = new NoonDoseWrapper(dosageDayElementStructure.getNoonDosageTimeElementStructure());
			allDoses.add(noonDose);
		}
		if(dosageDayElementStructure.getEveningDosageTimeElementStructure()!=null) {
			eveningDose = new EveningDoseWrapper(dosageDayElementStructure.getEveningDosageTimeElementStructure());
			allDoses.add(eveningDose);
		}
		if(dosageDayElementStructure.getNightDosageTimeElementStructure()!=null) {
			nightDose = new NightDoseWrapper(dosageDayElementStructure.getNightDosageTimeElementStructure());
			allDoses.add(nightDose);
		}			
	}

	public DayWrapper(dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageDayElementStructure dosageDayElementStructure) {
		this.dayNumber = dosageDayElementStructure.getDosageDayIdentifier();
		for(dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageTimeElementStructure dose: dosageDayElementStructure.getDosageTimeElementStructures()) {
			if(dose.getDosageTimeTime()==null) {
				PlainDoseWrapper d = new PlainDoseWrapper(dose); // We keep doses in order, otherwise addAll would be nicer
				plainDoses.add(d);
				allDoses.add(d);
			}
			else {
				TimedDoseWrapper d = new TimedDoseWrapper(dose);
				timedDoses.add(d);
				allDoses.add(d);
			}			
		}
		for(dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageTimeElementStructure dose: dosageDayElementStructure.getAccordingToNeedDosageTimeElementStructures()) {
			accordingToNeedDoses.add(new AccordingToNeedDoseWrapper(dose));
			allDoses.addAll(accordingToNeedDoses);
		}
		if(dosageDayElementStructure.getMorningDosageTimeElementStructure()!=null) {
			morningDose = new MorningDoseWrapper(dosageDayElementStructure.getMorningDosageTimeElementStructure());
			allDoses.add(morningDose);
		}
		if(dosageDayElementStructure.getNoonDosageTimeElementStructure()!=null) {
			noonDose = new NoonDoseWrapper(dosageDayElementStructure.getNoonDosageTimeElementStructure());
			allDoses.add(noonDose);
		}
		if(dosageDayElementStructure.getEveningDosageTimeElementStructure()!=null) {
			eveningDose = new EveningDoseWrapper(dosageDayElementStructure.getEveningDosageTimeElementStructure());
			allDoses.add(eveningDose);
		}
		if(dosageDayElementStructure.getNightDosageTimeElementStructure()!=null) {
			nightDose = new NightDoseWrapper(dosageDayElementStructure.getNightDosageTimeElementStructure());
			allDoses.add(nightDose);
		}			
	}

	private DayWrapper() {
		
	}
	
	public static DayWrapper makeDay(int dayNumber, DoseWrapper... doses) {
		DayWrapper day = new DayWrapper();
		day.dayNumber = dayNumber;
		for(DoseWrapper dose: doses) {
			if(dose instanceof AccordingToNeedDoseWrapper)
				day.accordingToNeedDoses.add((AccordingToNeedDoseWrapper)dose);
			else if(dose instanceof PlainDoseWrapper)
				day.plainDoses.add((PlainDoseWrapper)dose);
			else if(dose instanceof TimedDoseWrapper)
				day.timedDoses.add((TimedDoseWrapper)dose);
			else if(dose instanceof MorningDoseWrapper)
				day.morningDose = (MorningDoseWrapper)dose;
			else if(dose instanceof NoonDoseWrapper)
				day.noonDose = (NoonDoseWrapper)dose;
			else if(dose instanceof EveningDoseWrapper)
				day.eveningDose = (EveningDoseWrapper)dose;
			else if(dose instanceof NightDoseWrapper)
				day.nightDose = (NightDoseWrapper)dose;
			else 
				throw new RuntimeException();
			day.allDoses.add(dose);
		}
		
		return day;
	}

	public void removeDoses(Collection<DoseWrapper> doses) {
		for(DoseWrapper dose :doses)
			removeDose(dose);
	}

	public void removeDose(DoseWrapper dose) {
		if(dose instanceof AccordingToNeedDoseWrapper) {
			if(!accordingToNeedDoses.contains(dose))
				throw new IllegalArgumentException();
			accordingToNeedDoses.remove(dose);
		}
		else if(dose instanceof PlainDoseWrapper) {
			if(!plainDoses.contains(dose))
				throw new IllegalArgumentException();
			plainDoses.remove(dose);
		}
		else if(dose instanceof TimedDoseWrapper) {
			if(!timedDoses.contains(dose))
				throw new IllegalArgumentException();
			timedDoses.remove(dose);
		}
		else if(dose instanceof MorningDoseWrapper) {
			if(!morningDose.equals(dose))
				throw new IllegalArgumentException();
			morningDose = null;
		}
		else if(dose instanceof NoonDoseWrapper) {
			if(!noonDose.equals(dose))
				throw new IllegalArgumentException();
			noonDose = null;
		}
		else if(dose instanceof EveningDoseWrapper) {
			if(!eveningDose.equals(dose))
				throw new IllegalArgumentException();
			eveningDose = null;
		}
		else if(dose instanceof NightDoseWrapper) {
			if(!nightDose.equals(dose))
				throw new IllegalArgumentException();
			nightDose = null;
		}
		else {
			throw new RuntimeException();
		}
		
		if(!allDoses.contains(dose))
			throw new IllegalArgumentException();
		allDoses.remove(dose);		
	}
	
	
	public int getDayNumber() {
		return dayNumber;
	}

	public int getNumberOfDoses() {
		return allDoses.size();
	}

	public DoseWrapper getDose(int index) {
		return allDoses.get(index);		
	}
	
	public List<AccordingToNeedDoseWrapper> getAccordingToNeedDoses() {
		 return accordingToNeedDoses;
	}
	
	public int getNumberOfAccordingToNeedDoses() {
		return accordingToNeedDoses.size();
	}
	
	public List<PlainDoseWrapper> getPlainDoses() {
		return plainDoses;
	}
	
	public int getNumberOfPlainDoses() {
		return plainDoses.size();
	}	
	
	public MorningDoseWrapper getMorningDose() {
		return morningDose;
	}

	public NoonDoseWrapper getNoonDose() {
		return noonDose;
	}

	public EveningDoseWrapper getEveningDose() {
		return eveningDose;
	}

	public NightDoseWrapper getNightDose() {
		return nightDose;
	}

	public List<DoseWrapper> getAllDoses() {
		return allDoses;		
	}
	
	/**
	 * Compares dosage quantities and the dosages label (the type of the dosage)
	 * @return true if all dosages are of the same type and has the same quantity
	 */
	public boolean allDosesAreTheSame() {
		if(areAllDosesTheSame==null) {
			areAllDosesTheSame = true;
			DoseWrapper dose0 = null;
			for(DoseWrapper dose: getAllDoses()) {
				if(dose0==null) {
					dose0 = dose;
				}
				else if(!dose0.theSameAs(dose)) {
					areAllDosesTheSame = false;
					break;
				}	
			}
		}
		return areAllDosesTheSame;
	}

	/**
	 * Compares dosage quantities (but not the dosages label)
	 * @return true if all dosages has the same quantity
	 */
	public boolean allDosesHaveTheSameQuantity() {
		if(areAllDosesHaveTheSameQuantity==null) {
			areAllDosesHaveTheSameQuantity = true;
			if(allDoses.size()>1) {
				DoseWrapper dose0 = allDoses.get(0);
				for(int i=1; i<allDoses.size(); i++) {
					if(!dose0.getAnyDoseQuantityString().equals(allDoses.get(i).getAnyDoseQuantityString())) {
						areAllDosesHaveTheSameQuantity = false;
						break;
					}
				}
			}
		}
		return areAllDosesHaveTheSameQuantity;
	}
	
	public boolean containsAccordingToNeedDose() {
		for(DoseWrapper dose: getAllDoses()) {
			if(dose instanceof AccordingToNeedDoseWrapper) {
				return true;
			}
		}
		return false;
	}
	
	public boolean containsTimedDose() {
		for(DoseWrapper dose: getAllDoses()) {
			if(dose instanceof TimedDoseWrapper) {
				return true;
			}
		}
		return false;
	}
	
	public boolean containsPlainDose() {
		for(DoseWrapper dose: getAllDoses()) {
			if(dose instanceof PlainDoseWrapper) {
				return true;
			}
		}
		return false;
	}

	public boolean containsMorningNoonEveningNightDoses() {
		for(DoseWrapper dose: getAllDoses()) {
			if(dose instanceof MorningDoseWrapper || dose instanceof NoonDoseWrapper 
					|| dose instanceof EveningDoseWrapper || dose instanceof NightDoseWrapper) {
				return true;
			}
		}
		return false;
	}

	public boolean containsAccordingToNeedDosesOnly() {
		for(DoseWrapper dose: getAllDoses()) {
			if(!(dose instanceof AccordingToNeedDoseWrapper)) {
				return false;
			}
		}
		return true;
	}
	
	public Interval<BigDecimal> getSumOfDoses() {
		BigDecimal minValue = newDosage();
		BigDecimal maxValue = newDosage();
		for(DoseWrapper dose: getAllDoses()) {
			if(dose.getDoseQuantity()!=null) {
				minValue = addDosage(minValue, dose.getDoseQuantity());
				maxValue = addDosage(maxValue, dose.getDoseQuantity());
			}
			else if(dose.getMinimalDoseQuantity()!=null && dose.getMaximalDoseQuantity()!=null) {
				minValue = addDosage(minValue, dose.getMinimalDoseQuantity());
				maxValue = addDosage(maxValue, dose.getMaximalDoseQuantity());
			}
			else {
				throw new RuntimeException();
			}
		}		
		return new Interval<BigDecimal>(minValue, maxValue);		
	}
	
	private static BigDecimal newDosage() {
		BigDecimal v = new BigDecimal(0.0);
		v = v.setScale(9, BigDecimal.ROUND_HALF_UP);
		return v;
	}
	
	private static BigDecimal addDosage(BigDecimal bd, BigDecimal d) {
		if(d==null)
			throw new NullPointerException();
		return bd.add(d);
	}
	
}
