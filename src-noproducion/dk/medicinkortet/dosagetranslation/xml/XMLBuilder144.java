package dk.medicinkortet.dosagetranslation.xml;

import dk.medicinkortet.dosagetranslation.RawDefinition;
import dk.medicinkortet.dosisstructuretext.vowrapper.*;

public class XMLBuilder144 extends XMLBuilderBase {

    public static String build(RawDefinition d) {
        if(d.getIterationIntervals().size()!=1)
            return null;
        if(d.getDosageWrapper().getStructures()!=null && d.getDosageWrapper().getStructures().getStructures().size()!=1)
            return null;
        StringBuilder s = new StringBuilder();
        s.append("<Dosage>");
        s.append("<Version>1.4.4</Version>");
        s.append("<m15:Dosage " +
                "xsi:schemaLocation=\"http://www.dkma.dk/medicinecard/xml.schema/2015/01/01 ../../../2015/01/01/DosageForRequest.xsd\" " +
                "xmlns:m15=\"http://www.dkma.dk/medicinecard/xml.schema/2015/01/01\" " +
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
        if(d.getDosageWrapper().getAdministrationAccordingToSchema()!=null) {
            s.append("<m15:AdministrationAccordingToSchemaInLocalSystem>");
            s.append("<m15:StartDate>2010-01-01</m15:StartDate>");
            s.append("<m15:EndDate>2110-01-01</m15:EndDate>");
            s.append("</m15:AdministrationAccordingToSchemaInLocalSystem>");
        }
        else if(d.getDosageWrapper().getFreeText()!=null) {
            s.append("<m15:FreeText>");
            s.append("<m15:StartDate></m15:StartDate>");
            s.append("<m15:EndDate></m15:EndDate>");
            s.append("<m15:Text>"+escape(d.getDosageWrapper().getFreeText().getText())+"</m15:Text>");
            s.append("</m15:FreeText>");
        }
        else {
            s.append(buildStructures(d));
        }
        s.append("</m15:Dosage>");
        s.append("</Dosage>");
        return s.toString();
    }

    private static String buildStructures(RawDefinition d) {
        StringBuilder s = new StringBuilder();
        s.append("<m15:Structures>");
        s.append("<m15:UnitTexts source=\"Doseringsforslag\">");
        s.append("<m15:Singular>"+escape(d.getUnitSingular())+"</m15:Singular>");
        s.append("<m15:Plural>"+escape(d.getUnitPlural())+"</m15:Plural>");
        s.append("</m15:UnitTexts>");
        for(StructureWrapper structure: d.getDosageWrapper().getStructures().getStructures()) {
            s.append(buildStructure(d.getSupplementaryText(), structure));
        }
        s.append("</m15:Structures>");
        return s.toString();
    }

    private static String buildStructure(String supplementaryText, StructureWrapper structure) { // Note that suppl. text realy should be defined for each structure instead
        StringBuilder s = new StringBuilder();
        s.append("<m15:Structure>");
        if(structure.getIterationInterval()==0)
            s.append("<m15:NotIterated/>");
        else
            s.append("<m15:IterationInterval>"+structure.getIterationInterval()+"</m15:IterationInterval>");
        s.append("<m15:StartDate>2010-01-01</m15:StartDate>");
        s.append("<m15:EndDate>2110-01-01</m15:EndDate>");
        if(supplementaryText!=null)
            s.append("<m15:SupplementaryText>"+escape(supplementaryText)+"</m15:SupplementaryText>");
        for(DayWrapper day: structure.getDays()) {
            s.append(buildDay(day));
        }
        s.append("</m15:Structure>");
        return s.toString();
    }


    private static String buildDay(DayWrapper day) {
        StringBuilder s = new StringBuilder();
        if(day.getDayNumber()==0) {
            s.append("<m15:AnyDay>");
        }
        else {
            s.append("<m15:Day>");
            s.append("<m15:Number>"+day.getDayNumber()+"</m15:Number>");
        }
        for(DoseWrapper dose: day.getAllDoses()) {
            s.append("<m15:Dose>");
            if(dose instanceof MorningDoseWrapper)
                s.append("<m15:Time>morning</m15:Time>");
            else if(dose instanceof NoonDoseWrapper)
                s.append("<m15:Time>noon</m15:Time>");
            else if(dose instanceof EveningDoseWrapper)
                s.append("<m15:Time>evening</m15:Time>");
            else if(dose instanceof NightDoseWrapper)
                s.append("<m15:Time>night</m15:Time>");
            else if(dose instanceof TimedDoseWrapper)
                s.append("<m15:Time>"+((TimedDoseWrapper)dose).getTime()+"</m15:Time>");
            s.append(buildDose(dose));
            if(dose.isAccordingToNeed())
                s.append("<m15:IsAccordingToNeed/>");
            s.append("</m15:Dose>");
        }
        if(day.getDayNumber()==0)
            s.append("</m15:AnyDay>");
        else
            s.append("</m15:Day>");

        return s.toString();
    }

    private static String buildDose(DoseWrapper dose) {
        StringBuilder s = new StringBuilder();
        if(dose.getMinimalDoseQuantityString()!=null)
            s.append("<m15:MinimalQuantity>"+dose.getMinimalDoseQuantityString().replace(',', '.')+"</m15:MinimalQuantity>");
        if(dose.getMaximalDoseQuantityString()!=null)
            s.append("<m15:MaximalQuantity>"+dose.getMaximalDoseQuantityString().replace(',', '.')+"</m15:MaximalQuantity>");
        if(dose.getDoseQuantityString()!=null)
            s.append("<m15:Quantity>"+dose.getDoseQuantityString().replace(',', '.')+"</m15:Quantity>");
        return s.toString();
    }

}
