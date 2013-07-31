package dk.medicinkortet.dosisstructuretext.vowrapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateOrDateTimeWrapper {

	private Date date;
	private Date dateTime;
	
	public static DateOrDateTimeWrapper makeDate(Date date) {
		return new DateOrDateTimeWrapper(date, null);
	}
	
	public static DateOrDateTimeWrapper makeDate(String date) {
		try {
			return new DateOrDateTimeWrapper(new SimpleDateFormat("yyyy-MM-dd").parse(date), null);
		} 
		catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public static DateOrDateTimeWrapper makeDateTime(Date dateTime) {
		return new DateOrDateTimeWrapper(null, dateTime);
	}
	
	public static DateOrDateTimeWrapper makeDateTime(String dateTime) {
		try {
			return new DateOrDateTimeWrapper(null, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTime));
		} 
		catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	private DateOrDateTimeWrapper(Date date, Date dateTime) {
		this.date = date;
		this.dateTime = dateTime;
	}

	public Date getDate() {
		return date;
	}

	public Date getDateTime() {
		return dateTime;
	}
	
	public Date getDateOrDateTime() {
		if(date!=null)
			return date;
		else 
			return dateTime;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o==null || !(o instanceof DateOrDateTimeWrapper))
			return false;
		DateOrDateTimeWrapper dt = (DateOrDateTimeWrapper)o;
		if(date!=null) 
			return date.equals(dt.getDate());
		else if(dateTime!=null) 
			return dateTime.equals(dt.getDateTime());
		else 
			return dt.getDate()==null && dt.getDateTime()==null;
	}
	
	
	
}
