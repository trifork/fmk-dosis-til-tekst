package dk.medicinkortet.dosisstructuretext;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TextHelper {

	private static final Map<String, String> decimalsToFractions = new HashMap<String, String>();
	private static final Map<String, String> pluralToSingular = new HashMap<String, String>();
	
	static {
		decimalsToFractions.put("0,5", "1/2");
		decimalsToFractions.put("0,25", "1/4");
		decimalsToFractions.put("0,75", "3/4");
		decimalsToFractions.put("1,5", "1 1/2");
		pluralToSingular.put("behandlinger", "behandling");
		pluralToSingular.put("doser", "dosis");
		pluralToSingular.put("dråber", "dråbe");
	}
		
	public static String formatQuantity(BigDecimal quantity) {
		// We replace . with , below using string replace as we want to make
		// sure we always use , no matter what the locale settings are
		return trim(quantity.toPlainString().replace('.', ','));
	}
	
	public static String trim(String number) {
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
	
	
}
