package dk.medicinkortet.web.shared.jaxb.dkma.medicinecard20120601;

public class Dose {

	protected DoseTime time;
    protected Double quantity;
    protected Double minimalQuantity;
    protected Double maximalQuantity;
    protected boolean isAccordingToNeed = false;
	
    public Dose(DoseTime time, Double quantity, boolean isAccordingToNeed) {
		this(time, quantity, null, null, isAccordingToNeed);
	}

    public Dose(DoseTime time, Double minimalQuantity, Double maximalQuantity, boolean isAccordingToNeed) {
		this(time, null, minimalQuantity, maximalQuantity, isAccordingToNeed);
	}

	private Dose(DoseTime time, Double quantity, Double minimalQuantity,	Double maximalQuantity, boolean isAccordingToNeed) {
		this.time = time;
		this.quantity = quantity;
		this.minimalQuantity = minimalQuantity;
		this.maximalQuantity = maximalQuantity;
		this.isAccordingToNeed = isAccordingToNeed;
	}

	public DoseTime getTime() {
		return time;
	}

	public Double getQuantity() {
		return quantity;
	}

	public Double getMinimalQuantity() {
		return minimalQuantity;
	}

	public Double getMaximalQuantity() {
		return maximalQuantity;
	}

	public boolean isAccordingToNeed() {
		return isAccordingToNeed;
	}
    
}
