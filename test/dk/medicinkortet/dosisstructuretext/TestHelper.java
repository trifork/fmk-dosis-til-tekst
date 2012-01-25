package dk.medicinkortet.dosisstructuretext;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestHelper {

	public static Date toDate(String s) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd").parse(s);
		} 
		catch(ParseException e) {
			throw new RuntimeException(e);
		}
	}
	
}
