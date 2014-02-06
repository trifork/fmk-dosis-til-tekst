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

package dk.medicinkortet.dosisstructuretext;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang.time.FastDateFormat;

import dk.medicinkortet.dosisstructuretext.longtextconverterimpl.WeeklyRepeatedConverterImpl.DayOfWeek;
import dk.medicinkortet.dosisstructuretext.vowrapper.DateOrDateTimeWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.UnitOrUnitsWrapper;

public class TextHelper {

	public static final String VERSION = "2013-08-09";

	public static final String LONG_DATE_FORMAT = "EEEEEEE 'den' d'.' MMMMMMM yyyy";
	public static final String LONG_DATE_TIME_FORMAT = "EEEEEEE 'den' d'.' MMMMMMM yyyy 'kl.' HH:mm:ss";
	public static final String LONG_DATE_TIME_FORMAT_NO_SECS = "EEEEEEE 'den' d'.' MMMMMMM yyyy 'kl.' HH:mm";
	public static final String DAY_FORMAT = "EEEEEEE";
	public static final String INDENT = "   ";
	
    private static final FastDateFormat longDateTimeFormatter = FastDateFormat.getInstance(LONG_DATE_TIME_FORMAT, new Locale("da", "DK"));
    private static final FastDateFormat longDateTimeFormatterNoSecs = FastDateFormat.getInstance(LONG_DATE_TIME_FORMAT_NO_SECS, new Locale("da", "DK"));
    private static final FastDateFormat longDateFormatter = FastDateFormat.getInstance(LONG_DATE_FORMAT, new Locale("da", "DK"));
	
	private static final Map<String, String> decimalsToFractions = new HashMap<String, String>();
	private static final Map<String, String> singularToPlural = new HashMap<String, String>();
	private static final Map<String, String> pluralToSingular = new HashMap<String, String>();
	
	private static final String[][] UNITS = new String[][] {
		{"ampul", "ampuller"},	
		{"applikatordosis", "applikatordoser"},
		{"beholder", "beholdere"},
		{"behandling", "behandlinger"}, 
		{"brev", "breve"},
		{"brusetablet", "brusetabletter"},
		{"bukkalfilm", "bukkalfilm"},
		{"doseringssprøjte", "doseringssprøjter"},
		{"dosis", "doser"},
		{"dråbe", "dråber"},
		{"engangspen", "engangspenne"},
		{"engangssprøjte", "engangssprøjter"},
		{"enkeltdosisbeholder", "enkeltdosisbeholdere"},
		{"hætteglas", "hætteglas"},
		{"hætteglas+brev", "hætteglas+breve"},
		{"IE", "IE"},
		{"implantat", "implantater"},
		{"indgnidning", "indgnidninger"},
		{"indsprøjtning", "indsprøjtninger"},
		{"inhalationskapsel", "inhalationskapsler"},
		{"injektionssprøjte", "injektionssprøjter"},
		{"injektor", "injektorer"},
		{"kapsel", "kapsler"},
		{"kapsel med inhalationspulver", "kapsler med inhalationspulver"},
		{"L", "L"},
		{"liter", "liter"},
		{"mg", "mg"},
		{"ml", "ml"},
		{"måleske", "måleskeer"},
		{"oral sprøjte", "orale sprøjter"},
		{"pensling", "penslinger"},
		{"plaster", "plastre"},
		{"plastflaske", "plastflasker"},
		{"pudring", "pudringer"},
		{"pulver+solvens", "pulver+solvens"},
		{"pump", "pump"},
		{"pust", "pust"},
		{"påsmøring", "påsmøringer"},
		{"rektal stikpille", "rektale stikpiller"},
		{"rektalsprøjte", "rektalsprøjte"},
		{"rektaltube", "rektaltuber"},
		{"resoriblet", "resoribletter"},
		{"skylning", "skylninger"},
		{"spiral", "spiraler"},
		{"streg", "streger"},
		{"stribe", "striber"},
		{"strømpe", "strømper"},
		{"sug", "sug"},
		{"sugetablet", "sugetabletter"},
		{"sæt breve A+B", "sæt breve A+B"},
		{"tablet", "tabletter"},
		{"tablet+opløsningsvæske", "tabletter+opløsningsvæsker"},
		{"tablet til rektal anvendelse", "tabletter til rektal anvendelse"},
		{"tryk", "tryk"},
		{"tube", "tuber"},
		{"tubule", "tubuler"},
		{"tyggegummi", "tyggegummier"},
		{"tyggetabletter", "tyggetabletter"},
		{"tykt lag", "tykke lag"},
		{"tyndt lag", "tynde lag"},
		{"vaginalindlæg", "vaginalindlæg"},
		{"vaginalkapsel", "vaginalkapsler"},
		{"vaginaltablet", "vaginaltabletter"},
		{"vagitorie", "vagitorier"},
		{"vask", "vaske"},
		{"øjenlamel", "øjenlamel"},
		{"øjenskylning", "øjenskylninger"},
	};

	static {
		decimalsToFractions.put("0,5", "1/2");
		decimalsToFractions.put("0,25", "1/4");
		decimalsToFractions.put("0,75", "3/4");
		decimalsToFractions.put("1,5", "1 1/2");

		for(String[] u: UNITS) {
			singularToPlural.put(u[0], u[1]);
			pluralToSingular.put(u[1], u[0]);
		}
		
	}
		
	public static String formatQuantity(BigDecimal quantity) {
		// We replace . with , below using string replace as we want to make
		// sure we always use , no matter what the locale settings are
		return trim(quantity.toPlainString().replace('.', ','));
	}
	
	public static String gange(Integer number) {
		if(number==null || number.intValue()>1)
			return "gange";
		else
			return "gang";
	}
	
	public static String maybeAddSpace(String supplText) {
		if(supplText==null)
			return " ";
		else if(supplText.startsWith(",") || supplText.startsWith("."))
			return "";
		else
			return " ";
	}
	
	public static String trim(String number) {
		if(number.indexOf('.')<0 && number.indexOf(',')<0)
			return number;
		if(number.length()==1 || number.charAt(number.length()-1)>'0')
			return number;
		else 
			return trim(number.substring(0, number.length()-1));
	}
	
	public static String formatDate(Date date) {
		return FastDateFormat.getInstance("yyyy-MM-dd").format(date);
	}
		
	public static String quantityToString(BigDecimal quantity) {
		String s = formatQuantity(quantity);
		if(decimalsToFractions.containsKey(s))
			return decimalsToFractions.get(s);
		else
			return s;
	}
	
	public static String unitToSingular(String s) {
		if(pluralToSingular.containsKey(s))
			return pluralToSingular.get(s);
		else
			return s;
	}

	public static String unitToPlural(String s) {
		if(singularToPlural.containsKey(s))
			return singularToPlural.get(s);
		else
			return s;
	}
	
	public static String getUnit(DoseWrapper dose, UnitOrUnitsWrapper unitOrUnits) {
		if(unitOrUnits.getUnit()!=null)
			return correctUnit(dose, unitOrUnits.getUnit());
		else if(unitOrUnits.getUnitSingular()!=null && unitOrUnits.getUnitPlural()!=null)
			return chooseUnit(dose, unitOrUnits.getUnitSingular(), unitOrUnits.getUnitPlural());
		else 
			return null;
	}

	private static String correctUnit(DoseWrapper dose, String unit) {
		if(hasPluralUnit(dose))
			return unitToPlural(unit);
		else 
			return unitToSingular(unit);			
	}

	private static String chooseUnit(DoseWrapper dose, String unitSingular, String unitPlural) {
		if(hasPluralUnit(dose))
			return unitPlural;
		else
			return unitSingular;
	}
	
	private static boolean hasPluralUnit(DoseWrapper dose) {
		if(dose.getDoseQuantity()!=null) {
			return dose.getDoseQuantity().doubleValue()>1.0 || dose.getDoseQuantity().doubleValue()<0.000000001d;
		}
		else if(dose.getMaximalDoseQuantity()!=null) {
			return dose.getMaximalDoseQuantity().doubleValue()>1.0 || dose.getMaximalDoseQuantity().doubleValue()<0.000000001d;
		}
		else {
			return false;
		}
	}
	
	public static String makeDayString(DateOrDateTimeWrapper startDateOrDateTime, int dayNumber) {
		GregorianCalendar c = makeFromDateOnly(startDateOrDateTime.getDateOrDateTime());
		c.add(GregorianCalendar.DATE, dayNumber-1);
		String dateString = longDateFormatter.format(c.getTime());
		dateString = Character.toUpperCase(dateString.charAt(0)) + dateString.substring(1);
		return dateString;
	}
	
	public static GregorianCalendar makeFromDateOnly(Date date) {
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(date);
		c.set(GregorianCalendar.HOUR, 0);
		c.set(GregorianCalendar.MINUTE, 0);
		c.set(GregorianCalendar.SECOND, 0);
		c.set(GregorianCalendar.MILLISECOND, 0);
		return c;
	}

	public static String formatLongDate(Date date) {
		return longDateFormatter.format(date);
	}

	public static String formatLongDateTime(Date dateTime) {
		return longDateTimeFormatter.format(dateTime);
	}

	public static String formatLongDateNoSecs(Date dateTime) {
		return longDateTimeFormatterNoSecs.format(dateTime);
	}
	
	public static DayOfWeek makeDayOfWeekAndName(DateOrDateTimeWrapper startDateOrDateTime, DayWrapper day, boolean initialUpperCase) {
		GregorianCalendar c = makeFromDateOnly(startDateOrDateTime.getDateOrDateTime());
		c.add(GregorianCalendar.DATE, day.getDayNumber()-1);
		FastDateFormat f = FastDateFormat.getInstance(DAY_FORMAT, new Locale("da", "DK"));
		int dayOfWeek = usToDkDayOfWeek(c.get(GregorianCalendar.DAY_OF_WEEK));
		String dateString = f.format(c.getTime());
		String name;
		if(initialUpperCase)
			name = Character.toUpperCase(dateString.charAt(0)) + dateString.substring(1);
		else 
			name = dateString;
		return new DayOfWeek(dayOfWeek, name, day);

	}
	
	private static int usToDkDayOfWeek(int us) {
		switch(us) {
			case GregorianCalendar.MONDAY: return 1;
			case GregorianCalendar.TUESDAY: return 2;
			case GregorianCalendar.WEDNESDAY: return 3;
			case GregorianCalendar.THURSDAY: return 4;
			case GregorianCalendar.FRIDAY: return 5;
			case GregorianCalendar.SATURDAY: return 6;
			case GregorianCalendar.SUNDAY: return 7;
			default: throw new RuntimeException(""+us);
		}
	}
	
}
