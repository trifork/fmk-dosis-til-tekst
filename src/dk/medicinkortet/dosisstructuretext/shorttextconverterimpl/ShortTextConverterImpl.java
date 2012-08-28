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

package dk.medicinkortet.dosisstructuretext.shorttextconverterimpl;

import dk.medicinkortet.dosisstructuretext.TextHelper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DoseWrapper;

public abstract class ShortTextConverterImpl {

	abstract public boolean canConvert(DosageWrapper dosageTimes);

	abstract public String doConvert(DosageWrapper dosageTimes);
	
	protected static String toValue(DoseWrapper dose) {
		if(dose.getDoseQuantity()!=null) {
			return TextHelper.quantityToString(dose.getDoseQuantity());
		}
		else if(dose.getMinimalDoseQuantity()!=null && dose.getMaximalDoseQuantity()!=null) {
			return TextHelper.quantityToString(dose.getMinimalDoseQuantity()) + 
				"-" + TextHelper.quantityToString(dose.getMaximalDoseQuantity());	
		}
		else {
			return null;
		}		
	}
	
	protected static String toValue(DoseWrapper dose, String unit) {
		String s = toValue(dose);
		if(s==null)
			return null;
		unit = TextHelper.correctUnit(dose, unit);
		if(dose.getLabel().length()==0)
			return s + " " + unit;
		else 
			return s + " " + unit + " " + dose.getLabel();
	}
	
}