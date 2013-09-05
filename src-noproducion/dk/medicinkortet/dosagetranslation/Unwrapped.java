package dk.medicinkortet.dosagetranslation;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

import javax.management.RuntimeErrorException;

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

public class Unwrapped {
	
	private String _unitSingular;
	private String _unitPlural;
	private String _type;
	private String _iterationInterval; 
	private String _mapping;
	private String _supplementaryText;
	
	private DateOrDateTimeWrapper START_DATE = DateOrDateTimeWrapper.makeDate("2013-06-03");
	private DateOrDateTimeWrapper END_DATE = DateOrDateTimeWrapper.makeDate("2015-06-01");
	
	public Unwrapped(String unitSingular, String unitPlural, String type, String iterationInterval, String mapping, String supplementaryText) {
		this._unitSingular = unitSingular;
		this._unitPlural = unitPlural;
		this._type = type;
		this._iterationInterval = iterationInterval;
		this._mapping = mapping;
		this._supplementaryText = supplementaryText;
	}
	
	public DosageWrapper wrap() {
		
		// Validate everyting is filled in
		if(this._mapping==null)
			throw new RuntimeException("Missing mapping");
		if(this._unitSingular==null)
			throw new RuntimeException("Missing unit singular");
		if(this._unitPlural==null)
			throw new RuntimeException("Missing unit plural");
		if(this._type==null)
			throw new RuntimeException("Missing type");

		// Split {...}-sections
		ArrayList<Integer> iterationIntervals;
		ArrayList<String> types;
		ArrayList<String> mappings;
		if(this._iterationInterval.indexOf("{")>=0 || this._type.indexOf("{")>=0 || this._mapping.indexOf("{")>=0) {
			try {
				iterationIntervals = splitToIntegers(this._iterationInterval);
				types = splitToStrings(this._type);
				mappings = splitToStrings(this._mapping);
				if(iterationIntervals.size()!=types.size() || iterationIntervals.size()!=mappings.size())
					throw new RuntimeException("Error splitting {...}-sections, number of sections must match");
			}
			catch(Exception e) {
				throw new RuntimeException("Error splitting {...}-sections", e);
			}
		}
		else {
			iterationIntervals = new ArrayList<Integer>();
			iterationIntervals.add(new Double(this._iterationInterval).intValue());
			types = new ArrayList<String>();
			types.add(this._type);
			mappings = new ArrayList<String>();
			mappings.add(this._mapping);
		}
		
		// Wrap 
		DosageWrapper dosageWrapper = DosageWrapper.makeDosage(
			StructuresWrapper.makeStructures(
				wrapUnits(this._unitSingular, this._unitPlural), 
				wrapStructures(iterationIntervals, types, mappings, this._supplementaryText)));
		
		return dosageWrapper;
//		
//		if(type.equals("M+M+A+N")) 
//			return wrapMorningNoonEveningNight();
//		else if(type.equals("N daglig"))
//			return wrapNDaily();
//		else if(type.equals("PN"))
//			return wrapPN();
//		else 
//			throw new RuntimeException("Unexpected type \""+type+"\"");
	}

	private UnitOrUnitsWrapper wrapUnits(String unitSingular, String unitPlural) {
		return UnitOrUnitsWrapper.makeUnits(unitSingular, unitPlural);
	}

	private StructureWrapper[] wrapStructures(ArrayList<Integer> iterationIntervals, ArrayList<String> types, ArrayList<String> mappings, String supplText) {
		ArrayList<StructureWrapper> structureWrappers = new ArrayList<StructureWrapper>();
		for(int i=0; i<iterationIntervals.size(); i++) {
			structureWrappers.add(wrapStructure(iterationIntervals.get(i), types.get(i), mappings.get(i), supplText));
		}
		return structureWrappers.toArray(new StructureWrapper[structureWrappers.size()]);
	}

	private StructureWrapper wrapStructure(Integer iterationInterval, String type, String mapping, String supplText) {
		// Handle dag 1: ... dag 2: ... etc
		TreeMap<Integer, String> days = splitDays(mapping);
		// Wrap
		return StructureWrapper.makeStructure(
			iterationInterval, 
			supplText, 
			START_DATE, 
			END_DATE, 
			wrapDays(type, days));
	}
	
	private DayWrapper[] wrapDays(String type, TreeMap<Integer, String> days) {
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
	
	private DoseWrapper[] wrapDoses(String type, String mapping) {
		if(type.equals("M+M+A+N")) 
			return wrapMorningNoonEveningNightDoses(mapping);
		else if(type.equals("N daglig")) 
			return wrapNDailyDoses(mapping);
		else if(type.equals("PN"))
			return mapAccordingToNeedDoses(mapping);
		else 
			throw new RuntimeException("Unexpected type \""+type+"\"");		
	}

	private DoseWrapper[] wrapMorningNoonEveningNightDoses(String mapping) {
		String[] doses = mapping.split("\\+");
		if(doses.length<3||doses.length>4)
			throw new RuntimeException("Error parsing morning+noon+evening+night dose, found "+doses.length+" doses in \""+mapping+"\", expected 3 or 4");
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
	
	private DoseWrapper[] wrapNDailyDoses(String mapping) {
		String[] doses = mapping.split(";");
		DoseWrapper[] doseWrappers = new DoseWrapper[doses.length];
		for(int i=0; i<doses.length; i++) {
			doseWrappers[i] = PlainDoseWrapper.makeDose(toBigDecimal(doses[i]));
		}
		return doseWrappers;
	}
	
	private TreeMap<Integer, String> splitDays(String mapping) {
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

	private DoseWrapper[] mapAccordingToNeedDoses(String mapping) {
		String[] doses = mapping.split(";");
		DoseWrapper[] doseWrappers = new DoseWrapper[doses.length];
		for(int i=0; i<doses.length; i++) {
			doseWrappers[i] = PlainDoseWrapper.makeDose(toBigDecimal(doses[i]), true);	
		}
		return doseWrappers;
	}
	
	private ArrayList<Integer> splitToIntegers(String s) {
		ArrayList<Integer> is = new ArrayList<Integer>();
		for(String t: splitToStrings(s)) 
			is.add(Integer.parseInt(t.trim()));
		return is;
	}

	private ArrayList<String> splitToStrings(String s) {
		ArrayList<String> ss = new ArrayList<String>(Arrays.asList(s.split("\\{")));
		if(ss.get(0).length()==0)
			ss.remove(0);
		for(int i=0; i<ss.size(); i++) {
			String ts = ss.get(i);
			ts = ts.substring(0, ts.length()-1);
			ss.set(i, ts);
		}
		return ss;
	}
	
	public static BigDecimal toBigDecimal(String value) {
		if(value==null)
			return null;
		try {
			DecimalFormatSymbols sym = DecimalFormatSymbols.getInstance();
			sym.setDecimalSeparator('.');
			new DecimalFormat("#########.#########", sym).parse(value);
			BigDecimal v = new BigDecimal(value);
			v = v.setScale(9, BigDecimal.ROUND_HALF_UP);
			return v;
		}
		catch(ParseException e) {
			throw new RuntimeException(e);
		}
		catch(NumberFormatException e) {
			throw new RuntimeException("Error parsing value \""+value+"\" as BigDecimal", e);
		}
	}
	
	public static Integer toInteger(String value) {
		if(value==null)
			return null;
		return Integer.parseInt(value.trim());
	}

}
