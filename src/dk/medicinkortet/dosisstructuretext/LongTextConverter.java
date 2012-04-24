package dk.medicinkortet.dosisstructuretext;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuredDosageWrapper;

public class LongTextConverter {

	private static final String LONG_DATE_FORMAT = "EEEEEEE 'den' d'.' MMMMMMM yyyy";
	private static final String LONG_DATE_TIME_FORMAT = "EEEEEEE 'den' d'.' MMMMMMM yyyy 'kl.' HH:mm:ss";
	private static final String INDENT = "   ";
	private static final String DAY_0_LABEL_PN_ONLY = "Efter behov";
	private static final String DAY_0_LABEL_MIXED = "Dag ikke angivet";
	
	
	public static String convert(DosageWrapper dosage) {
		if(dosage.isAdministrationAccordingToSchema())
			return "Dosering efter skema i lokalt system";
		else if(dosage.isFreeText())
			return dosage.getFreeText();
		else
			return convert(dosage.getDosageTimes());
	}

	private static String convert(StructuredDosageWrapper dosageTimes) {
		StringBuilder s = new StringBuilder();
		
		if(dosageTimes.getStartDateOrDateTime().equals(dosageTimes.getEndDateOrDateTime())) { 
			// Same day dosage
			s.append("Doseringen foretages kun "+datesToLongText(dosageTimes.getStartDate(), dosageTimes.getStartDateTime())+":\n");
		}
		else if(dosageTimes.getIterationInterval()==0 /*&& dosageTimes.getDays().size()==1*/) {
			// Not repeated dosage
			appendDosageStart(s, dosageTimes);			
			// If there is just one day with according to need dosages we don't want say when to stop
			if(dosageTimes.getDays().size()==1 && dosageTimes.containsAccordingToNeedDosesOnly()) {
				s.append(":\n");
			}
			else {
				s.append(" og ophører efter det angivne forløb");
				appendNoteText(s, dosageTimes);				
			}
		}
		else if(dosageTimes.getIterationInterval()==1) {
			// Daily dosage
			appendDosageStart(s, dosageTimes);
			s.append(" og gentages dagligt:\n");
		}
		else if(dosageTimes.getIterationInterval()>1 && dosageTimes.getDays().size()==1) {
			appendDosageStart(s, dosageTimes);
			appendRepetition(s, dosageTimes);
			appendNoteText(s, dosageTimes);
		}
		else if(dosageTimes.getIterationInterval()>1 && dosageTimes.getDays().size()>1) {
			appendDosageStart(s, dosageTimes);
			appendRepetition(s, dosageTimes);
			appendNoteText(s, dosageTimes);
		}

		s.append(INDENT+"Doseringsforløb:\n");

		appendDays(s, dosageTimes);

		return s.toString();	
	}
	
	private static int appendDays(StringBuilder s, StructuredDosageWrapper dosageTimes) {
		int appendedLines = 0;
		for(int dayIndex=0; dayIndex<dosageTimes.getDays().size(); dayIndex++) {
			appendedLines++;
			if(dayIndex>0)
				s.append("\n");
			s.append(INDENT+makeDaysLabel(
					dosageTimes,
					dosageTimes.getDays().get(dayIndex)));
			s.append(makeDaysDosage(dosageTimes, dosageTimes.getDays().get(dayIndex)));
		}		
		return appendedLines;
	}
	
	private static String makeDaysLabel(StructuredDosageWrapper dosageTimes, DayWrapper day) {
		if(day.getDayNumber()==0) {
			if(day.containsAccordingToNeedDosesOnly())
				return DAY_0_LABEL_PN_ONLY+": ";
			else 
				return DAY_0_LABEL_MIXED+": ";
		}
		else {
			return makeDayString(dosageTimes.getStartDateOrDateTime(), day.getDayNumber())+": ";
		}		
	}

	private static String makeDaysDosage(StructuredDosageWrapper dosageTimes, DayWrapper day) {
		StringBuilder s = new StringBuilder();
		
		if(day.getNumberOfDoses()>1 && day.allDosesAreTheSame()) {
			s.append(makeOneDose(day.getDose(0), dosageTimes.getUnit(), dosageTimes.getUniqueSupplText()));
			if(day.containsAccordingToNeedDosesOnly())
				s.append(" højst");
			s.append(" "+day.getNumberOfDoses()+" gange");
		}
		else {
			for(int d=0; d<day.getNumberOfDoses(); d++) {
				s.append(makeOneDose(day.getDose(d), dosageTimes.getUnit(), dosageTimes.getSupplText()));
				if(d<day.getNumberOfDoses()-1)
					s.append(" + ");
			}
		}
		return s.toString();
	}

	private static String makeDayString(Date startDateOrDateTime, int dayNumber) {
		GregorianCalendar c = makeFromDateOnly(startDateOrDateTime);
		c.add(GregorianCalendar.DATE, dayNumber-1);
		SimpleDateFormat f = new SimpleDateFormat(LONG_DATE_FORMAT, new Locale("da", "DK"));
		String dateString = f.format(c.getTime());
		dateString = Character.toUpperCase(dateString.charAt(0)) + dateString.substring(1);
		return dateString;
	}
	
	private static GregorianCalendar makeFromDateOnly(Date date) {
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(date);
		c.set(GregorianCalendar.HOUR, 0);
		c.set(GregorianCalendar.MINUTE, 0);
		c.set(GregorianCalendar.SECOND, 0);
		c.set(GregorianCalendar.MILLISECOND, 0);
		return c;
	}
	
	private static StringBuilder makeOneDose(DoseWrapper dose, String unit, String supplText) {
		StringBuilder s = new StringBuilder();
		s.append(dose.getAnyDoseQuantityString());
		if(s.toString().equals("1"))
			unit = TextHelper.unitToSingular(unit);		
		s.append(" ").append(unit);
		if(dose.getLabel().length()>0)
			s.append(" ").append(dose.getLabel());
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

	private static void appendDosageStart(StringBuilder s, StructuredDosageWrapper dosageTimes) {
		s.append("Doseringsforløbet starter "+datesToLongText(dosageTimes.getStartDate(), dosageTimes.getStartDateTime()));
	}
	
	private static String datesToLongText(Date startDate, Date startDateTime) {
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
	
	private static void appendRepetition(StringBuilder s, StructuredDosageWrapper dosageTimes) {
		s.append(", forløbet gentages efter "+dosageTimes.getIterationInterval()+" dage");
	}
	
	private static void appendNoteText(StringBuilder s, StructuredDosageWrapper dosageTimes) {
		if(isVarying(dosageTimes) && isComplex(dosageTimes))
			s.append(".\nBemærk at doseringen varierer og har et komplekst forløb:\n");
		else if(isVarying(dosageTimes))
			s.append(".\nBemærk at doseringen varierer:\n");
		else if(isComplex(dosageTimes))
			s.append(".\nBemærk at doseringen har et komplekst forløb:\n");
		else
			s.append(":\n");
	}
	
	private static boolean isComplex(StructuredDosageWrapper dosageTimes) {
		if(dosageTimes.getDays().size()==1)
			return false;
		for(int d=1; d<dosageTimes.getDays().size(); d++) {
			if(dosageTimes.getDays().get(d-1).getDayNumber()!=d)
				return true;
		}
		return false;
	}
	
	private static boolean isVarying(StructuredDosageWrapper dosageTimes) {
		return !dosageTimes.allDaysAreTheSame();
	}
		
}
