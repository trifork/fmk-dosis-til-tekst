package com.trifork.vo;

public class DosageDrug {

    private long drugId;

    private long dosageUnitCode;

    private String drugName;

    private DosageUnit unit;

    public long getDrugId() {
        return drugId;
    }

    public void setDrugId(long drugId) {
        this.drugId = drugId;
    }

    public long getDosageUnitCode() {
        return dosageUnitCode;
    }

    public void setDosageUnitCode(long dosageUnitCode) {
        this.dosageUnitCode = dosageUnitCode;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public DosageUnit getUnit() {
        return unit;
    }

    public void setUnit(DosageUnit unit) {
        this.unit = unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DosageDrug that = (DosageDrug) o;

        if (drugId != that.drugId) return false;
//        if (!drugName.equals(that.drugName)) return false;
        return unit.equals(that.unit);

    }

    @Override
    public int hashCode() {
        int result = (int) (drugId ^ (drugId >>> 32));
//        result = 31 * result + drugName.hashCode();
        result = 31 * result + unit.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "DosageDrug{" +
                "drugId=" + drugId +
                ", drugName='" + drugName + '\'' +
                ", unit=" + unit +
                '}';
    }
}
