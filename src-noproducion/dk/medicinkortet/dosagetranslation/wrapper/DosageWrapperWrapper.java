package dk.medicinkortet.dosagetranslation.wrapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.SortedSet;
import java.util.TreeMap;

import dk.medicinkortet.dosagetranslation.DosageWrappers;
import dk.medicinkortet.dosagetranslation.RawDefinition;
import dk.medicinkortet.dosagetranslation.RawDefinitions;
import dk.medicinkortet.dosagetranslation.ValidationException;
import dk.medicinkortet.dosisstructuretext.vowrapper.DateOrDateTimeWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.EveningDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.MorningDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.NightDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.NoonDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.PlainDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructureWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuresWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.UnitOrUnitsWrapper;

public class DosageWrapperWrapper {

	private static final Date START_DATE = DateOrDateTimeWrapper.makeDate("2013-06-03").getDate();
	private static final Date END_DATE = DateOrDateTimeWrapper.makeDate("2015-06-01").getDate();
	
	public static DosageWrappers wrap(RawDefinitions rawDefinitions) {
		DosageWrappers dosageWrappers = new DosageWrappers();
		for(RawDefinition rawDefinition: rawDefinitions) {
			if(rawDefinition.getError()==null) {
//				System.out.println("Wrapping "+rawDefinition.getIterationInterval()+", "+rawDefinition.getType()+", "+rawDefinition.getMapping()+" from row "+(rawDefinition.getRowNumber()+1));
				dosageWrappers.add(rawDefinition.getRowNumber(), wrap(rawDefinition));
			}
			else {
//				System.out.println("Ignoring row "+(rawDefinition.getRowNumber()+1)+": "+rawDefinition.getError());
				dosageWrappers.add(rawDefinition.getRowNumber(), null);
			}
		}
		return dosageWrappers;
	}

	public static DosageWrapper wrap(RawDefinition rawDefinition) {		
		try {
			
			CurlyUnwrapper.unwrapCurlies(rawDefinition);
			
			// Wrap 
			DosageWrapper dosageWrapper = DosageWrapper.makeDosage(
				StructuresWrapper.makeStructures(
					wrapUnits(
						rawDefinition.getUnitSingular(), 
						rawDefinition.getUnitPlural()), 
					wrapStructures(
						rawDefinition.getIterationIntervals(), 
						rawDefinition.getTypes(), 
						rawDefinition.getMappings(), 
						rawDefinition.getSupplementaryText(), 
						rawDefinition.getRowNumber())));
			
			return dosageWrapper;
		}
		catch(ValidationException e) {
			System.err.println("Error wrapping row "+(rawDefinition.getRowNumber()+1)+": "+e.getMessage());
			rawDefinition.addError(e.getMessage());
			return null;
		}			
	}

	private static UnitOrUnitsWrapper wrapUnits(String unitSingular, String unitPlural) {
		return UnitOrUnitsWrapper.makeUnits(unitSingular, unitPlural);
	}

	private static StructureWrapper[] wrapStructures(ArrayList<Integer> iterationIntervals, ArrayList<String> types, ArrayList<String> mappings, String supplText, int rowNumberForDebug) throws ValidationException {
		ArrayList<StructureWrapper> structureWrappers = new ArrayList<StructureWrapper>();
		// Handle special cases:
		if( types.size()==2 && types.get(0).equals("M+M+A+N") && types.get(1).equals("PN") && 
			iterationIntervals.get(0).intValue()==1 && (iterationIntervals.get(1).intValue()==0 || iterationIntervals.get(1).intValue()==1)) {
			DoseWrapper[] d1 = wrapMorningNoonEveningNightDoses(mappings.get(0));
			DoseWrapper[] d2 = mapAccordingToNeedDoses(mappings.get(1));
			DoseWrapper[] ds = new DoseWrapper[d1.length+d2.length]; 
			for(int i=0; i<d1.length; i++)
				ds[i] = d1[i];
			for(int i=0; i<d2.length; i++)
				ds[i+d1.length] = d2[i];
			structureWrappers.add(
				StructureWrapper.makeStructure(
					1, supplText, 
					DateOrDateTimeWrapper.makeDate(START_DATE), 
					DateOrDateTimeWrapper.makeDate(END_DATE), 
					DayWrapper.makeDay(1, ds)));
		}
		else {
			// Handle standard cases
			ArrayList<Date[]> periodes = determinePeriodes(iterationIntervals, types, mappings, supplText, rowNumberForDebug);
			for(int i=0; i<iterationIntervals.size(); i++) {
				structureWrappers.add(
					wrapStructure(
						iterationIntervals.get(i), 
						types.get(i), 
						mappings.get(i), 
						supplText, 
						periodes.get(i)[0], periodes.get(i)[1]));
			}
		}
		return structureWrappers.toArray(new StructureWrapper[structureWrappers.size()]);
	}

	private static ArrayList<Date[]> determinePeriodes(ArrayList<Integer> iterationIntervals, ArrayList<String> types, ArrayList<String> mappings, String supplText, int rowNumberForDebug) throws ValidationException {
		ArrayList<Date[]> periodes = new ArrayList<Date[]>();
		if(iterationIntervals.size()==1) {
			periodes.add(new Date[]{START_DATE, END_DATE});
		}
		else if(iterationIntervals.size()==2) {
			if(	types.get(0).equals("N daglig") && types.get(1).equals("N daglig") && 
				iterationIntervals.get(0).intValue()==0 && iterationIntervals.get(1).intValue()>=1) {
				// First periode
				TreeMap<Integer, String> daysMap = splitDays(mappings.get(0));
				int lastDayNumber = ((SortedSet<Integer>)daysMap.keySet()).last();
				GregorianCalendar c = new GregorianCalendar();
				c.setTime(START_DATE);
				c.add(GregorianCalendar.DATE, lastDayNumber-1);
				periodes.add(new Date[]{START_DATE, c.getTime()});
				// Second periode
				c.add(GregorianCalendar.DATE, 1);
				periodes.add(new Date[]{c.getTime(), END_DATE});
			}
			else if( types.get(0).equals("N daglig") && types.get(1).equals("N daglig") && 
				 iterationIntervals.get(0).intValue()==0 && iterationIntervals.get(1).intValue()==0) {
				// First periode
				TreeMap<Integer, String> daysMap = splitDays(mappings.get(0));
				int lastDayNumber = ((SortedSet<Integer>)daysMap.keySet()).last();
				GregorianCalendar c = new GregorianCalendar();
				c.setTime(START_DATE);
				c.add(GregorianCalendar.DATE, lastDayNumber-1);
				periodes.add(new Date[]{START_DATE, c.getTime()});
				// Second periode
				c.add(GregorianCalendar.DATE, 1);
				Date c0 = c.getTime();
				daysMap = splitDays(mappings.get(1));
				lastDayNumber = ((SortedSet<Integer>)daysMap.keySet()).last();
				c.add(GregorianCalendar.DATE, lastDayNumber-1);
				periodes.add(new Date[]{c0, c.getTime()});
			}
			else {
				throw new ValidationException("Don't know how to handle this combination of type and iteration interval");
			}
		}
		else {
			throw new ValidationException("Don't know how to handle "+iterationIntervals.size()+" {...}-periodes");
		}	
		return periodes;
	}

	private static StructureWrapper wrapStructure(Integer iterationInterval, String type, 
			String mapping, String supplText, Date startDate, Date endDate) throws ValidationException {
		// Handle dag 1: ... dag 2: ... etc
		TreeMap<Integer, String> days = splitDays(mapping);
		// Wrap
		return StructureWrapper.makeStructure(
			iterationInterval, 
			supplText, 
			DateOrDateTimeWrapper.makeDate(startDate), 
			DateOrDateTimeWrapper.makeDate(endDate), 
			wrapDays(type, days));
	}
	
	private static DayWrapper[] wrapDays(String type, TreeMap<Integer, String> days) throws ValidationException {
		ArrayList<DayWrapper> dayWrappers = new ArrayList<DayWrapper>();
		for(Integer dayNumber: days.keySet()) {
			String mapping = days.get(dayNumber);
			dayWrappers.add(
				DayWrapper.makeDay(
					dayNumber, 
					wrapDoses(type, mapping)));
		}
		return dayWrappers.toArray(new DayWrapper[dayWrappers.size()]);
	}
	
	private static DoseWrapper[] wrapDoses(String type, String mapping) throws ValidationException {
		if(type.equals("M+M+A+N")) 
			return wrapMorningNoonEveningNightDoses(mapping);
		else if(type.equals("N daglig")) 
			return wrapNDailyDoses(mapping);
		else if(type.equals("PN"))
			return mapAccordingToNeedDoses(mapping);
		else 
			throw new ValidationException("Unexpected type \""+type+"\"");		
	}

	private static DoseWrapper[] wrapMorningNoonEveningNightDoses(String mapping) throws ValidationException {
		String[] doses = mapping.split("\\+");
		if(doses.length<3||doses.length>4)
			throw new ValidationException("Error parsing morning+noon+evening+night dose, found "+doses.length+" doses in \""+mapping+"\", expected 3 or 4");
		DoseWrapper[] doseWrappers = new DoseWrapper[doses.length];
		for(int i=0; i<doses.length; i++) {
			if(i==0)
				doseWrappers[i] = MorningDoseWrapper.makeDose(toBigDecimal(doses[i]));
			else if(i==1)
				doseWrappers[i] = NoonDoseWrapper.makeDose(toBigDecimal(doses[i]));
			else if(i==2)
				doseWrappers[i] = EveningDoseWrapper.makeDose(toBigDecimal(doses[i]));
			else if(i==3)
				doseWrappers[i] = NightDoseWrapper.makeDose(toBigDecimal(doses[i]));
		}
		return doseWrappers;
	}
	
	private static DoseWrapper[] wrapNDailyDoses(String mapping) throws ValidationException {
		String[] doses = mapping.split(";");
		DoseWrapper[] doseWrappers = new DoseWrapper[doses.length];
		for(int i=0; i<doses.length; i++) {
			doseWrappers[i] = PlainDoseWrapper.makeDose(toBigDecimal(doses[i].trim()));
		}
		return doseWrappers;
	}
	
	private static TreeMap<Integer, String> splitDays(String mapping) {
		TreeMap<Integer, String> map = new TreeMap<Integer, String>();
		ArrayList<String> days = new ArrayList<String>(Arrays.asList(mapping.split("\\s*(?i)dag\\s*")));		
		if(days.get(0).length()==0)
			days.remove(0);		
		for(int i=0; i<days.size(); i++) {
			String day = days.get(i);
			int dayNumber = 1;
			String daysMapping = day;
			if(day.indexOf(':')>=0) {
				dayNumber = Integer.parseInt(day.substring(0, day.indexOf(':')).trim());
				daysMapping = day.substring(day.indexOf(':')+1).trim();
			}
			map.put(dayNumber, daysMapping);
		}
		return map;
	}

	private static DoseWrapper[] mapAccordingToNeedDoses(String mapping) throws ValidationException {
		String[] doses = mapping.split(";");
		DoseWrapper[] doseWrappers = new DoseWrapper[doses.length];
		for(int i=0; i<doses.length; i++) {
			doseWrappers[i] = PlainDoseWrapper.makeDose(toBigDecimal(doses[i]), true);	
		}
		return doseWrappers;
	}
	
	public static BigDecimal toBigDecimal(String value) throws ValidationException {
		if(value==null)
			return null;
		try {
			BigDecimal v = new BigDecimal(value.trim());
			v = v.setScale(9, BigDecimal.ROUND_HALF_UP);
			return v;
		}
		catch(NumberFormatException e) {
			throw new ValidationException("Error parsing value \""+value+"\" as BigDecimal: "+e.getMessage());
		}
	}
	
	public static Integer toInteger(String value) {
		if(value==null)
			return null;
		return Integer.parseInt(value.trim());
	}

	private static int getDaysToDose(StructureWrapper structure, int part, int parts, int rowNumberForDebug) throws ValidationException {
		if(part<parts) {
			// First N parts
			if(structure.getIterationInterval()!=0)
				throw new ValidationException("If {...} is used only last dose is allowed to have iteration interval > 0, part "+(part+1)+" of "+(parts+1));
			return structure.getDays().last().getDayNumber();
		}
		else {
			// last part
			return 365;
		}
	}
}
