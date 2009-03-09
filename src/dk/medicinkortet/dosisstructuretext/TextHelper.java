package dk.medicinkortet.dosisstructuretext;

import java.util.TimeZone;

/**
 * General helper class for creating a correct text. Contains mappings to singular or plural unit names 
 * and a mapping of a limited number of decimals to fractions 
 */
public class TextHelper {
	
//	private static final DateFormat DATE_FORMAT_Z = new SimpleDateFormat("yyyy-MM-dd'Z'");
//	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
//	private static final SimpleDateFormat DATE_FORMAT_3 = new SimpleDateFormat("yyyy-MM-dd");
//
//	private static final DateFormat TIME_FORMAT_000Z = new SimpleDateFormat("HH:mm:ss'.'SSS'Z'");
//	private static final DateFormat TIME_FORMAT_Z = new SimpleDateFormat("HH:mm:ss'Z'");
//	private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
//	private static final DateFormat TIME_FORMAT_000 = new SimpleDateFormat("HH:mm:ss.SSS");
//	private static final SimpleDateFormat TIME_FORMAT_2 = new SimpleDateFormat("HH:mm");
//	private static final SimpleDateFormat TIME_FORMAT_3 = new SimpleDateFormat("HH:mm:ss");

	
//	static {
//		TIME_FORMAT_Z.setTimeZone(TimeZone.getTimeZone("UTC"));
//		TIME_FORMAT_000Z.setTimeZone(TimeZone.getTimeZone("UTC"));
//	}
	
	/**
	 * This array contains a mapping of the units defined in the schema file DKMA_DosageQuantityUnitText.xsd to 
	 * singular form. Any unit not defined in this mapping is used as-is.
	 */
	private static String[][] MAP_UNIT_TO_SINGULAR = {
	{"anti-Xa internationale enheder", "anti-Xa internationale enhed"},
	{"behandlinger", "behandling"}, 
	{"doser", "dosis"},
	{"dråber", "dråbe"},
	{"egg infection doses", "egg infection dosis"},
	{"enheder", "enhed"},
	{"ELISA units", "ELISA unit"},
	{"gram", "gram"},
	{"hæmagglutinationsenheder", "hæmagglutinationsenhed"},
	{"histaminækvivalenter, priktest", "histaminækvivalent, priktest"},
	{"internationale enheder", "internationale enheder"},
	{"kallikrein inactivation units", "kallikrein inactivation unit"},
	{"milliliter", "ml"},
	{"milligram", "mg"},
	{"plaque forming units", "plaque forming unit"},
	{"standardized quantity units", "standardized quantity unit"},
	{"tissue culture infection doses 50", "tissue culture infection dosis 50"},
	{"udvortes behandlinger", "udvortes behandling"}};
	
	
	/**
	 * This array contains a mapping of the units defined in the schema file DKMA_DosageQuantityUnitText.xsd to 
	 * plural form. Any unit not defined in this mapping is used as-is. 
	 */
	private static String[][] MAP_UNIT_TO_PLURAL = {
		{"milliliter", "ml"},
		{"milligram", "mg"}};
	
	/**
	 * In some cases it is preferred to have the dosage quantity in fractions. This map defines the conversions 
	 * from decimal to fractions to use. Any value not defined in this mapping is used as-is. 
	 */
	private static String[][] MAP_DECIMAL_TO_FRACTIONS = {
		{"0.5", "1/2"},
		{"0.25", "1/4"},
		{"0.75", "3/4"}, 
		{"1.5", "1 1/2"}};
	
	/**
	 * Maps this unit from its form defined in DKMA_DosageQuantityUnitText.xsd to singular. The mappings are
	 * defined in a private array within this class. 
	 * @param unit unit to map to singular
	 * @return unit mapped to singular, or unmodified if no mapping is defined. 
	 */
	public static String unitToSingular(String unit) {
		return map(MAP_UNIT_TO_SINGULAR, unit);
	}
	
	/**
	 * Maps this unit from its form defined in DKMA_DosageQuantityUnitText.xsd to plural. The mappings are
	 * defined in a private array within this class. 
	 * @param unit unit to map to plural
	 * @return unit mapped to plural, or unmodified if no mapping is defined. 
	 */
	public static String unitToPlural(String unit) {
		return map(MAP_UNIT_TO_PLURAL, unit);
	}
	
	/**
	 * Maps the passed value from decimal to a fraction, if a mapping for the passed value is defined. If no
	 * mapping is done the decimal value returned in string form, where the decimal dot is replaced with a 
	 * comma.
	 * @param decimal to map, e.g. 0.5
	 * @return fraction as string, e.g. "1/2" or "0,5" if no mapping is defined. 
	 */
	public static String decimalToFraction(Double decimal) {
		if(decimal==null)
			return null;
		String mapped = map(MAP_DECIMAL_TO_FRACTIONS, decimal.toString());
		mapped = mapped.replace('.', ',');
		mapped = trimDecimals(mapped);
		return mapped;
	}

	private static String trimDecimals(String mapped) {
		if (!mapped.contains(",")) return mapped;
		
		int length = mapped.length();
		return mapped.endsWith("0") || mapped.endsWith(",") ? trimDecimals(mapped.substring(0, length-1)) : mapped;
	}

	/**
	 * Maps the passed value from decimal to a fraction, if a mapping for the passed value is defined. If no
	 * mapping is done the decimal value returned in string form, where the decimal dot is replaced with a 
	 * comma.
	 * @param decimal to map, e.g. "0.5"
	 * @return fraction as string, e.g. "1/2" or "0,5" if no mapping is defined. 
	 */
	public static String decimalToFraction(String decimal) {
		if(decimal==null)
			return null;
		String mapped = map(MAP_DECIMAL_TO_FRACTIONS, decimal);
		mapped = mapped.replace('.', ',');
		mapped = trimDecimals(mapped);
		return mapped;
	}

	/**
	 * Helper method to lookup in a map
	 * @param map map to use
	 * @param toMap value to map
	 * @return mapped value, or the original value if nothing found
	 */
	private static String map(String[][] map, String toMap) {
		for(int i=0; i<map.length; i++) {
			if(map[i][0].equals(toMap))
				return map[i][1];
		}
		return toMap;
	}
	
	public static String formatDateString(String dateString) throws DosageValidationException {
		//TODO Tom skal have kigget på det her
		return dateString;
		//		if(dateString==null)
//			return null;		
//		if(dateString.endsWith("Z")) {
//			try {
//				GregorianCalendar now = new GregorianCalendar();		
//				if(now.getTimeZone().useDaylightTime()) {
//					now.setTime(DATE_FORMAT_Z.parse(dateString));				
//					now.roll(GregorianCalendar.HOUR_OF_DAY, now.getTimeZone().getRawOffset()/(1000*60*60));
//				}
//				else {
//					now.setTime(DATE_FORMAT_Z.parse(dateString));									
//				}
//				return DATE_FORMAT_3.format(now.getTime());
//			}
//			catch(ParseException e) {
//				throw new DosageValidationException("Error in date format for \""+dateString+"\"");
//			}
//		}
//		else {				
//			try {
//				return DATE_FORMAT_3.format(DATE_FORMAT.parse(dateString));
//			}
//			catch(ParseException e) {
//				throw new DosageValidationException("Error in date format for \""+dateString+"\"");
//			}
//		}		
	}	

	public static String formatTimeString(String timeString) throws DosageValidationException {
		//TODO tom skal have et kig på det her
		return timeString;
		//		if(timeString==null)
//			return null;		
//		boolean hasTimezone = timeString.endsWith("Z");
//		boolean hasMilliseconds = timeString.indexOf(".")>0;
//		if(hasTimezone&&hasMilliseconds) {
//			try {
//				GregorianCalendar now = new GregorianCalendar();		
//				if(now.getTimeZone().useDaylightTime()) {
//					now.setTime(TIME_FORMAT_000Z.parse(timeString));				
//					now.roll(GregorianCalendar.HOUR_OF_DAY, now.getTimeZone().getRawOffset()/(1000*60*60));
//				}
//				else {
//					now.setTime(TIME_FORMAT_000Z.parse(timeString));									
//				}
//				if(now.get(GregorianCalendar.SECOND)==0)
//					return TIME_FORMAT_2.format(now.getTime());
//				else
//					return TIME_FORMAT_3.format(now.getTime());
//			}
//			catch(ParseException e) {
//				throw new DosageValidationException("Error in time format for \""+timeString+"\"");
//			}			
//		}
//		else if(hasTimezone&&!hasMilliseconds) {
//			try {
//				GregorianCalendar now = new GregorianCalendar();		
//				if(now.getTimeZone().useDaylightTime()) {
//					now.setTime(TIME_FORMAT_Z.parse(timeString));				
//					now.roll(GregorianCalendar.HOUR_OF_DAY, now.getTimeZone().getRawOffset()/(1000*60*60));
//				}
//				else {
//					now.setTime(TIME_FORMAT_Z.parse(timeString));									
//				}
//				if(now.get(GregorianCalendar.SECOND)==0)
//					return TIME_FORMAT_2.format(now.getTime());
//				else
//					return TIME_FORMAT_3.format(now.getTime());
//			}
//			catch(ParseException e) {
//				throw new DosageValidationException("Error in time format for \""+timeString+"\"");
//			}
//		}
//		else if(!hasTimezone&&hasMilliseconds) {
//			try {
//				GregorianCalendar now = new GregorianCalendar();		
//				now.setTime(TIME_FORMAT_000.parse(timeString));				
//				if(now.get(GregorianCalendar.SECOND)==0)
//					return TIME_FORMAT_2.format(now.getTime());
//				else
//					return TIME_FORMAT_3.format(now.getTime());
//			}
//			catch(ParseException e) {
//				throw new DosageValidationException("Error in time format for \""+timeString+"\"");
//			}
//			
//		}
//		else { // if(!hasTimezone&&!hasMilliseconds){				
//			try {
//				GregorianCalendar now = new GregorianCalendar();		
//				now.setTime(TIME_FORMAT.parse(timeString));				
//				if(now.get(GregorianCalendar.SECOND)==0)
//					return TIME_FORMAT_2.format(now.getTime());
//				else
//					return TIME_FORMAT_3.format(now.getTime());
//			}
//			catch(ParseException e) {
//				throw new DosageValidationException("Error in time format for \""+timeString+"\"");
//			}
//		}		
	}	
	
}

