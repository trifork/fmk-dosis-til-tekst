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
