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

package dk.medicinkortet.dosisstructuretext.longtextconverterimpl;

import dk.medicinkortet.dosisstructuretext.vowrapper.DateOrDateTimeWrapper;

public abstract class SimpleLongTextConverterImpl extends LongTextConverterImpl {
	
	public String doConvert(String text, DateOrDateTimeWrapper startDateOrDateTime, DateOrDateTimeWrapper endDateOrDateTime) {
		
		StringBuilder s = new StringBuilder();
		
		if(startDateOrDateTime!=null && endDateOrDateTime!=null && 
				startDateOrDateTime.equals(endDateOrDateTime)) { 
			// Same day dosage
			s.append("Doseringen foretages kun "+datesToLongText(endDateOrDateTime)+".\n"+"   Dosering:\n   ");
		}
		else if(startDateOrDateTime!=null) {
			appendDosageStart(s, startDateOrDateTime);
			if(endDateOrDateTime!=null) {
				s.append(" og ophører "+datesToLongText(endDateOrDateTime)+".\n"+"   Doseringsforløb:\n   ");
			}
			else {
				s.append(".\n   Doseringsforløb:\n   ");
			}
		}
		else if(startDateOrDateTime==null) {
			if(endDateOrDateTime!=null) {
				s.append("Doseringsforløbet ophører "+datesToLongText(endDateOrDateTime)+".\n"+"   Doseringsforløb:\n   ");
			}
		}
		
		s.append(text);
		
		return s.toString();
	}
	
}
