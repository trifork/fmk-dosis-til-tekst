package dk.medicinkortet.dosisstructuretext.converterimpl;

import dk.medicinkortet.dosisstructuretext.TextHelper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DoseWrapper;

public abstract class ShortTextConverterImpl {

	abstract public boolean canConvert(DosageWrapper dosageTimes);

	abstract public String doConvert(DosageWrapper dosageTimes);
	
	protected String toValue(DoseWrapper dose) {
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

	protected String toValue(DoseWrapper dose, String unit) {
		String s = toValue(dose);
		if(s==null)
			return null;
		else if(dose.getLabel().length()==0)
			return s + " " + unit;
		else 
			return s + " " + unit + " " + dose.getLabel();
	}
	
}
