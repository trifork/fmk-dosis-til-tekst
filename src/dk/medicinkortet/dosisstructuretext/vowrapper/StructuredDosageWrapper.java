package dk.medicinkortet.dosisstructuretext.vowrapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import dk.medicinkortet.dosisstructuretext.Interval;
import dk.medicinkortet.web.shared.jaxb.dkma.medicinecard.DosageDayElementStructure;
import dk.medicinkortet.web.shared.jaxb.dkma.medicinecard.DosageTimesStructure;
import dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageQuantityUnitTextType;

/**
 * Wrapper for a structured dosage element. The corresponding wrapped xml element is DosageTimesStructure.  
 */
public class StructuredDosageWrapper {

	// Mapped values
	private int iterationInterval;
	private String unit;
	private String supplText;
	private Date startDate;
	private Date startDateTime;
	private Date endDate;
	private Date endDateTime;
	private List<DayWrapper> days = new ArrayList<DayWrapper>();	
	
	// Helper / cached values
	private boolean allDosesHaveTheSameSupplText;
	protected String uniqueSupplText;
	protected Boolean areAllDosesTheSame;
	protected Boolean areAllDaysTheSame;
	
	public StructuredDosageWrapper(DosageTimesStructure dosageTimesStructure) {
		this(
			dosageTimesStructure.getDosageTimesIterationIntervalQuantity(),
			dosageTimesStructure.getDosageQuantityUnitText(), 
			dosageTimesStructure.getDosageSupplementaryText(), 
			dosageTimesStructure.getDosageTimesStartDate(),
			dosageTimesStructure.getDosageTimesEndDate(),
			dosageTimesStructure.getDosageTimesStartDateTime(),
			dosageTimesStructure.getDosageTimesEndDateTime(),
			wrap(dosageTimesStructure), 
			true, // All doses have the same suppl text in the 2009 namespace, as it is set on the structured dosage itesef 
			dosageTimesStructure.getDosageSupplementaryText()); // There is always a unique suppl. text in the 2009 namespace, as it is set on the structured dosage itesef
	}

	public StructuredDosageWrapper(dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageTimesStructure dosageTimesStructure) {
		this(
			dosageTimesStructure.getDosageTimesIterationIntervalQuantity(),
			dosageTimesStructure.getDosageQuantityUnitText().value(), 
			null, // There is no suppl text on this level in the 2008 namespace
			dosageTimesStructure.getDosageTimesStartDate(), 
			dosageTimesStructure.getDosageTimesEndDate(), 
			null, 
			null, 
			wrap(dosageTimesStructure), 
			allDosesHaveTheSameSupplText(dosageTimesStructure), 
			getUniqueSupplText(dosageTimesStructure)); 
	}
	
	/**
	 * Factory metod to create structured dosages
	 */
	public static StructuredDosageWrapper makeStructuredDosage(int iterationInterval, String unit, String supplText, Date startDate, Date endDate, DayWrapper... days) {
		return new StructuredDosageWrapper(
			iterationInterval, unit, supplText, 
			startDate, endDate, null, null, 
			wrap(days), 
			true, 
			supplText);
	}

	/**
	 * Factory metod to create structured dosages
	 */
	public static StructuredDosageWrapper makeStructuredDosage(int iterationInterval, String unit, String supplText, Date startDate, Date endDate, Date startDateTime, Date endDateTime, DayWrapper... days) {
		return new StructuredDosageWrapper(
			iterationInterval, unit, supplText, 
			startDate, endDate, startDateTime, endDateTime, 
			wrap(days), 
			true, 
			supplText);
	}

	/**
	 * Factory metod to create structured dosages in 2008 namespace
	 */
	public static StructuredDosageWrapper makeStructuredDosage(int iterationInterval, DosageQuantityUnitTextType unit, Date startDate, Date endDate, DayWrapper... days) {
		return new StructuredDosageWrapper(
			iterationInterval, unit.value(), null, 
			startDate, endDate, null, null, 
			wrap(days),
			allDosesHaveTheSameSupplText(days),
			getUniqueSupplText(days)); 
	}
	
	private StructuredDosageWrapper(
			int iterationInterval, String unit, String supplText, 
			Date startDate, Date endDate, Date startDateTime, Date endDateTime,
			List<DayWrapper> days, 
 			boolean allDosesHaveTheSameSupplText, 
			String uniqueSupplText) {
		this.iterationInterval = iterationInterval;
		this.unit = unit;
		this.supplText = supplText;
		this.startDate = startDate;
		this.endDate = endDate;
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
		this.days = days;
		this.allDosesHaveTheSameSupplText = allDosesHaveTheSameSupplText;
		this.uniqueSupplText = uniqueSupplText;
	}	
	
	public boolean allDosesHaveTheSameSupplText() {
		return allDosesHaveTheSameSupplText;
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
	
	/**
	 * Compares dosage quantities and the dosages label (the type of the dosage)
	 * @return true if all days contains the same dosages
	 */
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
	
	public boolean containsMorningNoonEveningNightToNeedDoses() {
		for(DayWrapper day: days) {
			if(day.containsMorningNoonEveningNightToNeedDoses())
				return true;
		}
		return false;
	}	
	
	public boolean containsAccordingToNeedDosesOnly() {		
		for(DayWrapper day: days) {
			if(!day.containsAccordingToNeedDosesOnly())
				return false;
		}
		return true;
	}	
	
	public boolean containsAccordingToNeedDose() {		
		for(DayWrapper day: days) {
			if(day.containsAccordingToNeedDose())
				return true;
		}
		return false;
	}	

	private static final boolean allDosesHaveTheSameSupplText(dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageTimesStructure dosageTimesStructure) {
		return getSupplTextFromNs2008(dosageTimesStructure).size()==1;		
	}	
	
	private static final String getUniqueSupplText(dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageTimesStructure dosageTimesStructure) {
		HashSet<String> supplTexts = getSupplTextFromNs2008(dosageTimesStructure);
		if(supplTexts.size()==1)
			return supplTexts.iterator().next();
		else
			return null;
	}
	
	private static final HashSet<String> getSupplTextFromNs2008(dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageTimesStructure dosageTimesStructure) {
		HashSet<String> collectedSupplTexts = new HashSet<String>();
		for(dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageDayElementStructure day: dosageTimesStructure.getDosageDayElementStructures()) {
			if(day.getMorningDosageTimeElementStructure()!=null)
				collectedSupplTexts.addAll(
					getSupplTextFromNs2008Quantity(day.getMorningDosageTimeElementStructure()));
			if(day.getNoonDosageTimeElementStructure()!=null)
				collectedSupplTexts.addAll(
					getSupplTextFromNs2008Quantity(day.getNoonDosageTimeElementStructure()));
			if(day.getEveningDosageTimeElementStructure()!=null)
				collectedSupplTexts.addAll(
					getSupplTextFromNs2008Quantity(day.getEveningDosageTimeElementStructure()));
			if(day.getNightDosageTimeElementStructure()!=null)
				collectedSupplTexts.addAll(
					getSupplTextFromNs2008Quantity(day.getNightDosageTimeElementStructure()));
			for(dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageTimeElementStructure dosageTime: day.getAccordingToNeedDosageTimeElementStructures())
				collectedSupplTexts.addAll(
					getSupplTextFromNs2008Quantity(dosageTime));
			for(dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageTimeElementStructure dosageTime: day.getDosageTimeElementStructures())
				collectedSupplTexts.addAll(
					getSupplTextFromNs2008Quantity(dosageTime));
		}		
		return collectedSupplTexts;
	}

	private static final String getUniqueSupplText(DayWrapper[] days) {
		HashSet<String> supplTexts = getSupplTextFromDayWrappers(days);
		if(supplTexts.size()==1)
			return supplTexts.iterator().next();
		else
			return null;	}	
	
	private static final boolean allDosesHaveTheSameSupplText(DayWrapper[] days) {
		return getSupplTextFromDayWrappers(days).size()==1;		
	}
	
	private static final HashSet<String> getSupplTextFromDayWrappers(DayWrapper[] days) {
		HashSet<String> collectedSupplTexts = new HashSet<String>();
		for(DayWrapper day: days) {
			for(DoseWrapper dose: day.getAllDoses()) {
				if(dose.getSupplText()!=null)
					collectedSupplTexts.add(dose.getSupplText());
				if(dose.getMinimalSupplText()!=null)
					collectedSupplTexts.add(dose.getMinimalSupplText());
				if(dose.getMaximalSupplText()!=null)
					collectedSupplTexts.add(dose.getMaximalSupplText());
			}
		}
		return collectedSupplTexts;
	}

	private static final HashSet<String> getSupplTextFromNs2008Quantity(dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageDefinedTimeElementStructure dosageTime) {
		return getSupplTextFromNs2008Quantities(
				dosageTime.getDosageQuantityStructure(), 
				dosageTime.getMinimalDosageQuantityStructure(), 
				dosageTime.getMaximalDosageQuantityStructure());
	}

	private static final HashSet<String> getSupplTextFromNs2008Quantity(dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageTimeElementStructure dosageTime) {
		return getSupplTextFromNs2008Quantities(
				dosageTime.getDosageQuantityStructure(), 
				dosageTime.getMinimalDosageQuantityStructure(), 
				dosageTime.getMaximalDosageQuantityStructure());
	}

	private static final HashSet<String> getSupplTextFromNs2008Quantities(dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageQuantityStructure quantity, dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageQuantityStructure minQuantity, dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageQuantityStructure maxQuantity) {
		HashSet<String> collectedSupplTexts = new HashSet<String>();
		if(quantity!=null)
			collectedSupplTexts.add(quantity.getDosageQuantityFreeText());
		if(minQuantity!=null)
			collectedSupplTexts.add(minQuantity.getDosageQuantityFreeText());
		if(maxQuantity!=null)
			collectedSupplTexts.add(maxQuantity.getDosageQuantityFreeText());
		return collectedSupplTexts;
	}	
	
	private static final ArrayList<DayWrapper> wrap(DosageTimesStructure dosageTimesStructure) {
		ArrayList<DayWrapper> days = new ArrayList<DayWrapper>();  
		for(DosageDayElementStructure d: dosageTimesStructure.getDosageDayElementStructures()) 
			days.add(new DayWrapper(d));
		return days;
	}

	private static final ArrayList<DayWrapper> wrap (dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageTimesStructure dosageTimesStructure) {
		ArrayList<DayWrapper> days = new ArrayList<DayWrapper>();  
		for(dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageDayElementStructure d: dosageTimesStructure.getDosageDayElementStructures()) 
			days.add(new DayWrapper(d));
		return days;
	}
	
	private static final ArrayList<DayWrapper> wrap(DayWrapper... days) {
		ArrayList<DayWrapper> wdays = new ArrayList<DayWrapper>();
		for(DayWrapper day: days)
			wdays.add(day);
		return wdays;			
	}	

	public Date getStartDateOrDateTime() {
		if(startDate!=null)
			return startDate;
		else 
			return startDateTime;
	}

	public Date getStartDate() {
		return startDate;
	}	

	public Date getStartDateTime() {
		return startDateTime;
	}	
	
	public Date getEndDateOrDateTime() {
		if(endDate!=null)
			return endDate;
		else
			return endDateTime;
	}

	public int getIterationInterval() {
		return iterationInterval;
	}
	
	public String getUnit() {
		return unit;
	}
	
	public String getSupplText() {
		return supplText;
	}
	
	/**
	 * In the 2008 namespace it is possible to have more than one supplementary text. If the
	 * source is a dosage in the 2008 namespace and all supplementary texts are equal (or all 
	 * are null) this method returns the supplementary text. If 2009 namespace or newer is used
	 * the return value is the single supplementary text. 
	 * Remember to check (e.g. in the canConvert) that all dosages have the same supplementary
	 * text by using the allDosesHaveTheSameSupplText method.
	 */
	public String getUniqueSupplText() {
		return uniqueSupplText;
	}	
	
	public List<DayWrapper> getDays() {
		return days;
	}
	
	public DayWrapper getFirstDay() {
		return days.get(0);
	}
	
	public DayWrapper getLastDay() {
		return days.get(days.size()-1);
	}
	
	public DayWrapper getDay(int dayNumber) {
		for(DayWrapper day: days) {
			if(day.getDayNumber()==dayNumber)
				return day;
		}
		return null;
	}
	
	public DayWrapper getMaxDay() {
		DayWrapper max = getFirstDay();
		for(DayWrapper day: days) {
			if(day.getDayNumber()>max.getDayNumber())
				max = day;
		}
		return max;
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
