package com.trifork.vo;

public class DosageUnit {

    private Long code;

    private String singular;

    private String plural;

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public String getSingular() {
        return singular;
    }

    public void setSingular(String singular) {
        this.singular = singular;
    }

    public String getPlural() {
        return plural;
    }

    public void setPlural(String plural) {
        this.plural = plural;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DosageUnit that = (DosageUnit) o;

        if (singular != null ? !singular.equals(that.singular) : that.singular != null) return false;
        return !(plural != null ? !plural.equals(that.plural) : that.plural != null);

    }

    @Override
    public int hashCode() {
        int result = singular != null ? singular.hashCode() : 0;
        result = 31 * result + (plural != null ? plural.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DosageUnit{" +
                "singular='" + singular + '\'' +
                ", plural='" + plural + '\'' +
                '}';
    }
}
