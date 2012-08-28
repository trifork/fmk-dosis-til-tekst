package dk.medicinkortet.web.shared.jaxb.dkma.medicinecard20120601;

import java.util.ArrayList;
import java.util.List;

public class DosageDay {

    protected int dayNumber;
    protected List<Dose> doses;
    
    public DosageDay(int dayNumber, List<Dose> doses) {
		this.dayNumber = dayNumber;
		this.doses = doses;
	}

	public int getDayNumber() {
        return dayNumber;
    }

    public List<Dose> getDoses() {
        if (doses == null) {
        	doses = new ArrayList<Dose>();
        }
        return this.doses;
    }

}
