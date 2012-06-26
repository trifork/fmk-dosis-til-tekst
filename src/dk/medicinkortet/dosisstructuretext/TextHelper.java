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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dk.medicinkortet.dosisstructuretext.vowrapper.DoseWrapper;

public class TextHelper {

	public static final String VERSION = "2012-04-20"; 
	
	private static final Map<String, String> decimalsToFractions = new HashMap<String, String>();
	private static final Map<String, String> singularToPlural = new HashMap<String, String>();
	private static final Map<String, String> pluralToSingular = new HashMap<String, String>();
	
	static {
		decimalsToFractions.put("0,5", "1/2");
		decimalsToFractions.put("0,25", "1/4");
		decimalsToFractions.put("0,75", "3/4");
		decimalsToFractions.put("1,5", "1 1/2");

		pluralToSingular.put("tabletter", "tablet");
		pluralToSingular.put("kapsler", "kapsel");
		pluralToSingular.put("dråber", "dråbe");
		pluralToSingular.put("behandlinger", "behandling");
		pluralToSingular.put("doser", "dosis");
		pluralToSingular.put("tykke lag", "tykt lag");
		pluralToSingular.put("tynde lag", "tyndt lag");

		singularToPlural.put("tablet", "tabletter");
		singularToPlural.put("kapsel", "kapsler");
		singularToPlural.put("dråbe", "dråber");
		singularToPlural.put("behandling", "behandlinger");
		singularToPlural.put("dosis", "doser");
		singularToPlural.put("tykt lag", "tykke lag");
		singularToPlural.put("tyndt lag", "tynde lag");
	}
		
	public static String formatQuantity(BigDecimal quantity) {
		// We replace . with , below using string replace as we want to make
		// sure we always use , no matter what the locale settings are
		return trim(quantity.toPlainString().replace('.', ','));
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
		return new SimpleDateFormat("yyyy-MM-dd").format(date);
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

	public static String correctUnit(DoseWrapper dose, String unit) {
		if(dose.getDoseQuantity()!=null) {
			if(dose.getDoseQuantity().doubleValue()>1.0)
				return unitToPlural(unit);
			else 
				return unitToSingular(unit);			
		}
		else if(dose.getMaximalDoseQuantity()!=null) {
			if(dose.getMaximalDoseQuantity().doubleValue()>1.0)
				return unitToPlural(unit);
			else 
				return unitToSingular(unit);
		}
		else {
			return unit;
		}
	}
	
	
}
