package dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008;

public enum DosageQuantityUnitTextType {

    BEHANDLINGER("behandlinger"),
    DOSER("doser"),
    DRAABER("dr\u00e5ber"),
    GRAM("gram"),
    INTERNATIONALE_ENHEDER("internationale enheder"),
    MIKROGRAM("mikrogram"),
    MILLIGRAM("milligram"),
    MILLILITER("milliliter"),
    MILLIMOL("millimol"),
    MILLION_ENHEDER("million enheder"),
    PUST("pust"),
    STK("stk"),
    SUG("sug"),
    TYKT_LAG("tykt lag"),
    TYNDT_LAG("tyndt lag"),
    XA_I_ENHEDER("XaI enheder");

    private final String value;

    DosageQuantityUnitTextType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DosageQuantityUnitTextType fromValue(String v) {
        for (DosageQuantityUnitTextType c: DosageQuantityUnitTextType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
