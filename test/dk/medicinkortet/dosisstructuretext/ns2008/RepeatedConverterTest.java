package dk.medicinkortet.dosisstructuretext.ns2008;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import dk.medicinkortet.dosisstructuretext.LongTextConverter;
import dk.medicinkortet.dosisstructuretext.Interval;
import dk.medicinkortet.dosisstructuretext.ShortTextConverter;
import dk.medicinkortet.dosisstructuretext.TestHelper;
import dk.medicinkortet.dosisstructuretext.converterimpl.RepeatedConverterImpl;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageDayElementStructure;
import dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageQuantityStructure;
import dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageQuantityUnitTextType;
import dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageStructure;
import dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageTimeElementStructure;
import dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageTimesStructure;

public class RepeatedConverterTest {

	private DosageStructure makeMyDay(int iterationInterval) {
		DosageStructure dosage = new DosageStructure();
		DosageTimesStructure dosageTimes = new DosageTimesStructure();
		dosage.setDosageTimesStructure(dosageTimes);
		dosageTimes.setDosageTimesIterationIntervalQuantity(iterationInterval);
		dosageTimes.setDosageTimesStartDate(TestHelper.toDate("2011-01-01"));
		dosageTimes.setDosageTimesEndDate(TestHelper.toDate("2011-12-31"));
		dosageTimes.setDosageQuantityUnitText(DosageQuantityUnitTextType.STK);
		List<DosageDayElementStructure> days = new ArrayList<DosageDayElementStructure>(); 
		dosageTimes.setDosageDayElementStructures(days);
		DosageDayElementStructure day1 = new DosageDayElementStructure();
		days.add(day1);		
		day1.setDosageDayIdentifier(1);
		return dosage;
	}
	
	private DosageStructure makeDosages(int iterationInterval, String supplementaryText, Double... dosageQuantity) {
		DosageStructure dosage = makeMyDay(iterationInterval);
		DosageDayElementStructure day1 = dosage.getDosageTimesStructure().getDosageDayElementStructures().get(0);
		for(int i=0; i<dosageQuantity.length; i++) {
			DosageTimeElementStructure dose = new DosageTimeElementStructure();
			DosageQuantityStructure q = new DosageQuantityStructure();
			q.setDosageQuantityValue(dosageQuantity[i]);
			q.setDosageQuantityFreeText(supplementaryText);
			dose.setDosageQuantityStructure(q);
			day1.getDosageTimeElementStructures().add(dose);
		}
		return dosage;
	}

	private DosageStructure makeDosages(int iterationInterval, String supplementaryText, Interval<Double>... dosageQuantity) {
		DosageStructure dosage = makeMyDay(iterationInterval);
		DosageDayElementStructure day1 = dosage.getDosageTimesStructure().getDosageDayElementStructures().get(0);
		for(int i=0; i<dosageQuantity.length; i++) {
			DosageTimeElementStructure dose = new DosageTimeElementStructure();
			DosageQuantityStructure qi = new DosageQuantityStructure();
			qi.setDosageQuantityValue(dosageQuantity[i].getMinimum());
			qi.setDosageQuantityFreeText(supplementaryText);
			dose.setMinimalDosageQuantityStructure(qi);
			DosageQuantityStructure qx = new DosageQuantityStructure();
			qx.setDosageQuantityValue(dosageQuantity[i].getMaximum());
			qx.setDosageQuantityFreeText(supplementaryText);
			dose.setMaximalDosageQuantityStructure(qx);
			day1.getDosageTimeElementStructures().add(dose);
		}
		return dosage;
	}
	
	@Test
	public void test3stk2gangeDaglig() throws Exception {
		DosageStructure dosage = makeDosages(1, null, 3.0, 3.0);
		DosageWrapper w = new DosageWrapper(dosage);
		Assert.assertEquals(
				"Doseringsforløbet starter lørdag den 1. januar 2011 og gentages dagligt:\n"+
				"   Doseringsforløb:\n"+
				"   Lørdag den 1. januar 2011: 3 stk 2 gange",
				LongTextConverter.convert(w));
		Assert.assertEquals(
				RepeatedConverterImpl.class, 
				ShortTextConverter.getConverterClass(w));
		Assert.assertEquals(
				"3 stk 2 gange daglig", 
				ShortTextConverter.convert(w));
		// assertEquals(10.0, result.getAvgDailyDosis().doubleValue()); 				
	}
	
	@Test
	public void test1stk3gangeDaglig() throws Exception {
		DosageStructure dosage = makeDosages(1, "ved måltid", 3.0, 3.0);
		DosageWrapper w = new DosageWrapper(dosage);
		Assert.assertEquals(
				"Doseringsforløbet starter lørdag den 1. januar 2011 og gentages dagligt:\n"+
				"   Doseringsforløb:\n"+
				"   Lørdag den 1. januar 2011: 3 stk ved måltid 2 gange",
				LongTextConverter.convert(w));
		Assert.assertEquals(
				RepeatedConverterImpl.class, 
				ShortTextConverter.getConverterClass(w));
		Assert.assertEquals(
				"3 stk 2 gange daglig ved måltid", 
				ShortTextConverter.convert(w));
		// assertEquals(10.0, result.getAvgDailyDosis().doubleValue()); 				
	}
	
	@Test
	public void test1stkDaglig() throws Exception {
		DosageStructure dosage = makeDosages(1, "ved måltid", 1.0);
		DosageWrapper w = new DosageWrapper(dosage);
		Assert.assertEquals(
				"Doseringsforløbet starter lørdag den 1. januar 2011 og gentages dagligt:\n"+
				"   Doseringsforløb:\n"+
				"   Lørdag den 1. januar 2011: 1 stk ved måltid",
				LongTextConverter.convert(w));
		Assert.assertEquals(
				RepeatedConverterImpl.class, 
				ShortTextConverter.getConverterClass(w));
		Assert.assertEquals(
				"1 stk daglig ved måltid", 
				ShortTextConverter.convert(w));
		// assertEquals(10.0, result.getAvgDailyDosis().doubleValue()); 				
	}

	@Test
	public void test1Til2stk2GangeDaglig() throws Exception {
		DosageStructure dosage = makeDosages(1, null, new Interval(1.0, 2.0), new Interval(1.0, 2.0));
		DosageWrapper w = new DosageWrapper(dosage);
		Assert.assertEquals(
				"Doseringsforløbet starter lørdag den 1. januar 2011 og gentages dagligt:\n"+
				"   Doseringsforløb:\n"+
				"   Lørdag den 1. januar 2011: 1-2 stk 2 gange",
				LongTextConverter.convert(w));
		Assert.assertEquals(
				RepeatedConverterImpl.class, 
				ShortTextConverter.getConverterClass(w));
		Assert.assertEquals(
				"1-2 stk 2 gange daglig", 
				ShortTextConverter.convert(w));
		// assertEquals(10.0, result.getAvgDailyDosis().doubleValue()); 				
	}

	@Test
	public void test1stkHver2Dag() throws Exception {
		DosageStructure dosage = makeDosages(2, "ved måltid", 1.0);
		DosageWrapper w = new DosageWrapper(dosage);
		Assert.assertEquals(
				"Doseringsforløbet starter lørdag den 1. januar 2011, forløbet gentages efter 2 dage:\n"+
				"   Doseringsforløb:\n"+
				"   Lørdag den 1. januar 2011: 1 stk ved måltid",
				LongTextConverter.convert(w));
		Assert.assertEquals(
				RepeatedConverterImpl.class, 
				ShortTextConverter.getConverterClass(w));
		Assert.assertEquals(
				"1 stk hver 2. dag ved måltid", 
				ShortTextConverter.convert(w));
		// assertEquals(10.0, result.getAvgDailyDosis().doubleValue()); 				
	}
	
	
	@Test
	public void test1stkOmUgen() throws Exception {
		DosageStructure dosage = makeDosages(7, "ved måltid", 1.0);
		DosageWrapper w = new DosageWrapper(dosage);
		Assert.assertEquals(
				"Doseringsforløbet starter lørdag den 1. januar 2011, forløbet gentages efter 7 dage:\n"+
				"   Doseringsforløb:\n"+
				"   Lørdag den 1. januar 2011: 1 stk ved måltid",
				LongTextConverter.convert(w));
		Assert.assertEquals(
				RepeatedConverterImpl.class, 
				ShortTextConverter.getConverterClass(w));
		Assert.assertEquals(
				"1 stk 1 gang om ugen ved måltid", 
				ShortTextConverter.convert(w));
		// assertEquals(10.0, result.getAvgDailyDosis().doubleValue()); 				
	}

	@Test
	public void test1stkOmMaaneden() throws Exception {
		DosageStructure dosage = makeDosages(30, "ved måltid", 1.0);
		DosageWrapper w = new DosageWrapper(dosage);
		Assert.assertEquals(
				"Doseringsforløbet starter lørdag den 1. januar 2011, forløbet gentages efter 30 dage:\n"+
				"   Doseringsforløb:\n"+
				"   Lørdag den 1. januar 2011: 1 stk ved måltid",
				LongTextConverter.convert(w));
		Assert.assertEquals(
				RepeatedConverterImpl.class, 
				ShortTextConverter.getConverterClass(w));
		Assert.assertEquals(
				"1 stk 1 gang om måneden ved måltid", 
				ShortTextConverter.convert(w));
		// assertEquals(10.0, result.getAvgDailyDosis().doubleValue()); 				
	}
	
	@Test
	public void test2_5stk1GangOmUgenVedMaaltid() throws Exception {
		DosageStructure dosage = makeDosages(7, "ved måltid", 2.5);
		DosageWrapper w = new DosageWrapper(dosage);
		Assert.assertEquals(
				"Doseringsforløbet starter lørdag den 1. januar 2011, forløbet gentages efter 7 dage:\n"+
				"   Doseringsforløb:\n"+
				"   Lørdag den 1. januar 2011: 2,5 stk ved måltid",
				LongTextConverter.convert(w));
		Assert.assertEquals(
				RepeatedConverterImpl.class, 
				ShortTextConverter.getConverterClass(w));
		Assert.assertEquals(
				"2,5 stk 1 gang om ugen ved måltid", 
				ShortTextConverter.convert(w));
		// assertEquals(10.0, result.getAvgDailyDosis().doubleValue()); 				
	}
	
	@Test
	public void test2_5Stk2GangeSammeDag1GangOmUgenVedMaaltid() throws Exception {
		DosageStructure dosage = makeDosages(7, "ved måltid", 2.5, 2.5);
		DosageWrapper w = new DosageWrapper(dosage);
		Assert.assertEquals(
				"Doseringsforløbet starter lørdag den 1. januar 2011, forløbet gentages efter 7 dage:\n"+
				"   Doseringsforløb:\n"+
				"   Lørdag den 1. januar 2011: 2,5 stk ved måltid 2 gange",
				LongTextConverter.convert(w));
		Assert.assertEquals(
				RepeatedConverterImpl.class, 
				ShortTextConverter.getConverterClass(w));
		Assert.assertEquals(
				"2,5 stk 2 gange samme dag 1 gang om ugen ved måltid", 
				ShortTextConverter.convert(w));
	}

	@Test
	public void test2_5stk1GangOmMaanedenVedMaaltid() throws Exception {
		DosageStructure dosage = makeDosages(30, "ved måltid", 2.5);
		DosageWrapper w = new DosageWrapper(dosage);
		Assert.assertEquals(
				"Doseringsforløbet starter lørdag den 1. januar 2011, forløbet gentages efter 30 dage:\n"+
				"   Doseringsforløb:\n"+
				"   Lørdag den 1. januar 2011: 2,5 stk ved måltid",
				LongTextConverter.convert(w));
		Assert.assertEquals(
				RepeatedConverterImpl.class, 
				ShortTextConverter.getConverterClass(w));
		Assert.assertEquals(
				"2,5 stk 1 gang om måneden ved måltid", 
				ShortTextConverter.convert(w));
		// assertEquals(10.0, result.getAvgDailyDosis().doubleValue()); 				
	}
	
	@Test
	public void test2_5Stk2GangeSammeDag1GangOmMaanedenVedMaaltid() throws Exception {
		DosageStructure dosage = makeDosages(30, "ved måltid", 2.5, 2.5);
		DosageWrapper w = new DosageWrapper(dosage);
		Assert.assertEquals(
				"Doseringsforløbet starter lørdag den 1. januar 2011, forløbet gentages efter 30 dage:\n"+
				"   Doseringsforløb:\n"+
				"   Lørdag den 1. januar 2011: 2,5 stk ved måltid 2 gange",
				LongTextConverter.convert(w));
		Assert.assertEquals(
				RepeatedConverterImpl.class, 
				ShortTextConverter.getConverterClass(w));
		Assert.assertEquals(
				"2,5 stk 2 gange samme dag 1 gang om måneden ved måltid", 
				ShortTextConverter.convert(w));
	}

	@Test
	public void test2_5stkHver5DagVedMaaltid() throws Exception {
		DosageStructure dosage = makeDosages(5, "ved måltid", 2.5);
		DosageWrapper w = new DosageWrapper(dosage);
		Assert.assertEquals(
				"Doseringsforløbet starter lørdag den 1. januar 2011, forløbet gentages efter 5 dage:\n"+
				"   Doseringsforløb:\n"+
				"   Lørdag den 1. januar 2011: 2,5 stk ved måltid",
				LongTextConverter.convert(w));
		Assert.assertEquals(
				RepeatedConverterImpl.class, 
				ShortTextConverter.getConverterClass(w));
		Assert.assertEquals(
				"2,5 stk hver 5. dag ved måltid", 
				ShortTextConverter.convert(w));
		// assertEquals(10.0, result.getAvgDailyDosis().doubleValue()); 				
	}
	
	@Test
	public void test2_5stk2GangeSammeDagHver5DagVedMaaltid() throws Exception {
		DosageStructure dosage = makeDosages(5, "ved måltid", 2.5, 2.5);
		DosageWrapper w = new DosageWrapper(dosage);
		Assert.assertEquals(
				"Doseringsforløbet starter lørdag den 1. januar 2011, forløbet gentages efter 5 dage:\n"+
				"   Doseringsforløb:\n"+
				"   Lørdag den 1. januar 2011: 2,5 stk ved måltid 2 gange",
				LongTextConverter.convert(w));
		Assert.assertEquals(
				RepeatedConverterImpl.class, 
				ShortTextConverter.getConverterClass(w));
		Assert.assertEquals(
				"2,5 stk 2 gange samme dag hver 5. dag ved måltid", 
				ShortTextConverter.convert(w));
		// assertEquals(10.0, result.getAvgDailyDosis().doubleValue()); 				
	}
}
