package dk.medicinkortet.dosisstructuretext.longtextconverterimpl;

import dk.medicinkortet.dosisstructuretext.TextHelper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;

public class EmptyStructureConverterImpl extends LongTextConverterImpl {

	@Override
	public boolean canConvert(DosageWrapper dosageStructure) {
		return dosageStructure.isStructured() && dosageStructure.getStructures().getStructures() != null
				&& dosageStructure.getStructures().getStructures().first().getDays().isEmpty();
	}

	@Override
	public String doConvert(DosageWrapper dosageStructure) {
		StringBuilder s = new StringBuilder();
		s.append("Doseringsforløbet starter " + datesToLongText(dosageStructure.getStructures().getStructures().first().getStartDateOrDateTime()));
		if(dosageStructure.getStructures().getStructures().first().getEndDateOrDateTime() != null) {
			s.append(" og ophører " + datesToLongText(dosageStructure.getStructures().getStructures().first().getEndDateOrDateTime()));
		}
		
		s.append(":\n" + TextHelper.INDENT + "Bemærk: skal ikke anvendes i denne periode!\n");
		
		
		return s.toString();
	}

}
