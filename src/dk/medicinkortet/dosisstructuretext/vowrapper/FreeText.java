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

import java.util.Date;

public class FreeText {
	
	private Date startDate;
	private Date startDateTime;
	private boolean dosageStartedPreviously;

	private Date endDate;
	private Date endDateTime;
	private boolean dosageEndingUndetermined;
	
	private String text;
	
	public FreeText(Date startDate, Date startDateTime, boolean dosageStartedPreviously, Date endDate, Date endDateTime, boolean dosageEndingUndetermined, String text) {
		this.startDate = startDate;
		this.startDateTime = startDateTime;
		this.dosageStartedPreviously = dosageStartedPreviously;
		this.endDate = endDate;
		this.endDateTime = endDateTime;
		this.dosageEndingUndetermined = dosageEndingUndetermined;
		this.text = text;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getStartDateTime() {
		return startDateTime;
	}

	public boolean isDosageStartedPreviously() {
		return dosageStartedPreviously;
	}

	public Date getEndDate() {
		return endDate;
	}

	public Date getEndDateTime() {
		return endDateTime;
	}

	public boolean isDosageEndingUndetermined() {
		return dosageEndingUndetermined;
	}

	public String getText() {
		return text;
	}
	
}
