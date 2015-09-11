package com.trifork;


import com.trifork.vo.DosageDrug;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import java.util.Collection;

@Configuration
@ComponentScan
@ImportResource({"jdbc.xml"})
public class Main {

    @Autowired
    private DrugDAO drugDAO;

    public void run() {
        Collection<DosageDrug> allDrugsNew = drugDAO.getAllDrugs("New");
        Collection<DosageDrug> allDrugsOld = drugDAO.getAllDrugs("Old");
        compareDrugs(allDrugsNew, allDrugsOld);
//        compareDrugs(allDrugsOld, allDrugsNew);
    }

    private void compareDrugs(Collection<DosageDrug> allDrugsA, Collection<DosageDrug> allDrugsB) {
        long cntFound = 0;
        long cntDiff = 0;
        long cntNotFound = allDrugsA.size();
        String longString = "{";
        for (DosageDrug ddNew : allDrugsA) {
            boolean found = false;
            for (DosageDrug ddOld : allDrugsB) {
                if (ddOld.getDrugId() == ddNew.getDrugId()) {
                    if (!ddNew.equals(ddOld)) {
                        String shortTranslation = drugDAO.getShortSuggestionText(ddNew.getDrugId(), "Old");
                        if (!("tyggetabletter".equals(ddOld.getUnit().getSingular()) && "tyggetablet".equals(ddNew.getUnit().getSingular()))) {
                            if (shortTranslation != null) {
//                            System.out.println("diff A=>" + ddOld);
//                            System.out.println("diff B=>" + ddNew);

                                System.out.println(ddNew.getDrugId() + "\t" + ddOld.getUnit().getSingular() + "\t=>\t" + ddNew.getUnit().getSingular() + "\t==\t" + shortTranslation);
//                            System.out.println("==========================================");
                                cntDiff++;
                                longString += ddNew.getDrugId() + "L,";
                            }
                        }
                    }
                    found = true;
                    cntFound++;
                    cntNotFound--;
                    break;
                }
            }
            if (!found) {
//                System.out.println("not found =>" + ddNew);
            }
//            System.out.println("==========================================");
        }
        System.out.println("Found: " + cntFound + " not found: " + cntNotFound);
        System.out.println("Diff: " + cntDiff);
        System.out.println("String: " + longString);
    }

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(Main.class);
        Main m = context.getBean(Main.class);
        m.run();
    }
}
