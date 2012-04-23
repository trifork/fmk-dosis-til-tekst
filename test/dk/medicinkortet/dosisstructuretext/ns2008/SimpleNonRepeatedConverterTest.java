package dk.medicinkortet.dosisstructuretext.ns2008;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import dk.medicinkortet.dosisstructuretext.LongTextConverter;
import dk.medicinkortet.dosisstructuretext.ShortTextConverter;
import dk.medicinkortet.dosisstructuretext.TestHelper;
import dk.medicinkortet.dosisstructuretext.converterimpl.SimpleNonRepeatedConverterImpl;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageDayElementStructure;
import dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageQuantityStructure;
import dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageQuantityUnitTextType;
import dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageStructure;
import dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageTimeElementStructure;
import dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageTimesStructure;

public class SimpleNonRepeatedConverterTest {

	private DosageStructure makeDosage() {
		DosageStructure dosage = new DosageStructure();
		DosageTimesStructure dosageTimes = new DosageTimesStructure();
		dosage.setDosageTimesStructure(dosageTimes);
		dosageTimes.setDosageTimesIterationIntervalQuantity(0);
		dosageTimes.setDosageTimesStartDate(TestHelper.toDate("2011-01-01"));
		dosageTimes.setDosageTimesEndDate(TestHelper.toDate("2011-01-01"));
		dosageTimes.setDosageQuantityUnitText(DosageQuantityUnitTextType.STK);
		List<DosageDayElementStructure> days = new ArrayList<DosageDayElementStructure>(); 
		dosageTimes.setDosageDayElementStructures(days);
		DosageDayElementStructure day1 = new DosageDayElementStructure();
		days.add(day1);		
		day1.setDosageDayIdentifier(0);
		DosageTimeElementStructure dose = new DosageTimeElementStructure();
		DosageQuantityStructure q = new DosageQuantityStructure();
		q.setDosageQuantityValue(1.0);
		q.setDosageQuantityFreeText("5 timer før virkning ønskes");
		dose.setDosageQuantityStructure(q);
		day1.getDosageTimeElementStructures().add(dose);
		return dosage;
	}
	
	@Test
	public void test1Plaster5TimerFoerVirkningOenskes() throws Exception {
		DosageStructure dosage = makeDosage();
		DosageWrapper w = new DosageWrapper(dosage);
		Assert.assertEquals(
				"Doseringen foretages kun lørdag den 1. januar 2011:\n"+
				"   Doseringsforløb:\n"+
				"   Dag ikke angivet: 1 stk 5 timer før virkning ønskes",
				LongTextConverter.convert(w));
		Assert.assertEquals(
				SimpleNonRepeatedConverterImpl.class, 
				ShortTextConverter.getConverterClass(w));
		Assert.assertEquals(
				"1 stk 5 timer før virkning ønskes", 
				ShortTextConverter.convert(w));
		// assertEquals(10.0, result.getAvgDailyDosis().doubleValue()); 				
	}
	
}
