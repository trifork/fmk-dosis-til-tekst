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

public class DailyDosis {
	
	private BigDecimal value;
	private Interval<BigDecimal> interval;
	private String unit;
	
	public DailyDosis() {
	}

	public DailyDosis(BigDecimal value, String unit) {
		this.value = value;
		this.unit = unit;
	}
	
	public DailyDosis(Interval<BigDecimal> interval, String unit) {
		this.interval = interval;
		this.unit = unit;
	}
	
	public BigDecimal getValue() {
		return value;
	}
	
	public Interval<BigDecimal> getInterval() {
		return interval;
	}
	
	public String getUnit() {
		return unit;
	}
	
	public boolean isValue() {
		return value!=null;
	}
	
	public boolean isInterval() {
		return interval!=null;
	}
	
	public boolean isNone() {
		return value==null && interval==null;
	}
	
}
