package dk.medicinkortet.dosisstructuretext.ns2008;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import dk.medicinkortet.dosisstructuretext.LongTextConverter;
import dk.medicinkortet.dosisstructuretext.ShortTextConverter;
import dk.medicinkortet.dosisstructuretext.TestHelper;
import dk.medicinkortet.dosisstructuretext.converterimpl.MorningNoonEveningNightConverterImpl;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageDayElementStructure;
import dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageDefinedTimeElementStructure;
import dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageQuantityStructure;
import dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageQuantityUnitTextType;
import dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageStructure;
import dk.medicinkortet.web.shared.jaxb.dkma.medicinecard2008.DosageTimesStructure;

public class MorningNoonEveningNightConverterTest {

	private DosageStructure makeMyDay() {
		DosageStructure dosage = new DosageStructure();
		DosageTimesStructure dosageTimes = new DosageTimesStructure();
		dosage.setDosageTimesStructure(dosageTimes);
		dosageTimes.setDosageTimesIterationIntervalQuantity(1);
		dosageTimes.setDosageTimesStartDate(TestHelper.toDate("2011-01-01"));
		dosageTimes.setDosageTimesEndDate(TestHelper.toDate("2011-12-31"));
		dosageTimes.setDosageQuantityUnitText(DosageQuantityUnitTextType.STK);
//		dosageTimes.setDosageSupplementaryText("ved måltid");
		List<DosageDayElementStructure> days = new ArrayList<DosageDayElementStructure>(); 
		dosageTimes.setDosageDayElementStructures(days);
		DosageDayElementStructure day1 = new DosageDayElementStructure();
		days.add(day1);		
		day1.setDosageDayIdentifier(1);
		return dosage;
	}
	
	private DosageStructure makeDosages(Double mo, Double no, Double ev, Double ni) {
		DosageStructure dosage = makeMyDay();
		DosageDayElementStructure day1 = dosage.getDosageTimesStructure().getDosageDayElementStructures().get(0);
		if(mo!=null) {
			DosageDefinedTimeElementStructure morning = new DosageDefinedTimeElementStructure();
			day1.setMorningDosageTimeElementStructure(morning);
			DosageQuantityStructure q = new DosageQuantityStructure();
			q.setDosageQuantityValue(mo);
			q.setDosageQuantityFreeText("ved måltid");
			morning.setDosageQuantityStructure(q);
		}
		if(no!=null) {
			DosageDefinedTimeElementStructure noon = new DosageDefinedTimeElementStructure();
			day1.setNoonDosageTimeElementStructure(noon);
			DosageQuantityStructure q = new DosageQuantityStructure();
			q.setDosageQuantityValue(no);
			q.setDosageQuantityFreeText("ved måltid");
			noon.setDosageQuantityStructure(q);
		}
		if(ev!=null) {
			DosageDefinedTimeElementStructure evening = new DosageDefinedTimeElementStructure();
			day1.setEveningDosageTimeElementStructure(evening);
			DosageQuantityStructure q = new DosageQuantityStructure();
			q.setDosageQuantityValue(ev);
			q.setDosageQuantityFreeText("ved måltid");
			evening.setDosageQuantityStructure(q);
		}
		if(ni!=null) {
			DosageDefinedTimeElementStructure night = new DosageDefinedTimeElementStructure();
			day1.setNightDosageTimeElementStructure(night);
			DosageQuantityStructure q = new DosageQuantityStructure();
			q.setDosageQuantityValue(ni);
			q.setDosageQuantityFreeText("ved måltid");
			night.setDosageQuantityStructure(q);
		}
		return dosage;
	}
	
	private DosageStructure makeDosagesWithMinMax(Double moi, Double mox, Double noi, Double nox, Double evi, Double evx, Double nii, Double nix) {
		DosageStructure dosage = makeMyDay();
		DosageDayElementStructure day1 = dosage.getDosageTimesStructure().getDosageDayElementStructures().get(0);
		if(moi!=null && mox!=null) {
			DosageDefinedTimeElementStructure morning = new DosageDefinedTimeElementStructure();
			day1.setMorningDosageTimeElementStructure(morning);
			DosageQuantityStructure qi = new DosageQuantityStructure();
			qi.setDosageQuantityValue(moi);
			qi.setDosageQuantityFreeText("ved måltid");
			DosageQuantityStructure qx = new DosageQuantityStructure();
			qx.setDosageQuantityValue(mox);
			qx.setDosageQuantityFreeText("ved måltid");
			morning.setMinimalDosageQuantityStructure(qi);
			morning.setMaximalDosageQuantityStructure(qx);
		}
		if(noi!=null && nox!=null) {
			DosageDefinedTimeElementStructure noon = new DosageDefinedTimeElementStructure();
			day1.setNoonDosageTimeElementStructure(noon);
			DosageQuantityStructure qi = new DosageQuantityStructure();
			qi.setDosageQuantityValue(noi);
			qi.setDosageQuantityFreeText("ved måltid");
			DosageQuantityStructure qx = new DosageQuantityStructure();
			qx.setDosageQuantityValue(nox);
			qx.setDosageQuantityFreeText("ved måltid");
			noon.setMinimalDosageQuantityStructure(qi);
			noon.setMaximalDosageQuantityStructure(qx);
		}
		if(evi!=null && evx!=null) {
			DosageDefinedTimeElementStructure evening = new DosageDefinedTimeElementStructure();
			day1.setEveningDosageTimeElementStructure(evening);
			DosageQuantityStructure qi = new DosageQuantityStructure();
			qi.setDosageQuantityValue(evi);
			qi.setDosageQuantityFreeText("ved måltid");
			DosageQuantityStructure qx = new DosageQuantityStructure();
			qx.setDosageQuantityValue(evx);
			qx.setDosageQuantityFreeText("ved måltid");
			evening.setMinimalDosageQuantityStructure(qi);
			evening.setMaximalDosageQuantityStructure(qx);
		}
		if(nii!=null && nix!=null) {
			DosageDefinedTimeElementStructure night = new DosageDefinedTimeElementStructure();
			day1.setNightDosageTimeElementStructure(night);
			DosageQuantityStructure qi = new DosageQuantityStructure();
			qi.setDosageQuantityValue(nii);
			qi.setDosageQuantityFreeText("ved måltid");
			DosageQuantityStructure qx = new DosageQuantityStructure();
			qx.setDosageQuantityValue(nix);
			qx.setDosageQuantityFreeText("ved måltid");
			night.setMinimalDosageQuantityStructure(qi);
			night.setMaximalDosageQuantityStructure(qx);
		}
		return dosage;
	}
	
	@Test
	public void testMorningNoonEveningAndNight() throws Exception {
		DosageStructure dosage = makeDosages(1.0, 2.0, 3.0, 4.0);
		DosageWrapper w = new DosageWrapper(dosage);
		Assert.assertEquals(
				"Doseringsforløbet starter lørdag den 1. januar 2011 og gentages dagligt:\n"+
				"   Doseringsforløb:\n"+
				"   Lørdag den 1. januar 2011: 1 stk morgen ved måltid + 2 stk middag ved måltid + 3 stk aften ved måltid + 4 stk nat ved måltid",
				LongTextConverter.convert(w));
		Assert.assertEquals(
				MorningNoonEveningNightConverterImpl.class, 
				ShortTextConverter.getConverterClass(w));
		Assert.assertEquals(
				"1 stk morgen, 2 stk middag, 3 stk aften og 4 stk nat ved måltid", 
				ShortTextConverter.convert(w));
		// assertEquals(10.0, result.getAvgDailyDosis().doubleValue()); 				
	}

	@Test
	public void testMorningNoonEveningAndNightWithEqualDoses() throws Exception {
		DosageStructure dosage = makeDosages(2.0, 2.0, 2.0, 2.0);
		DosageWrapper w = new DosageWrapper(dosage);
		Assert.assertEquals(
				"Doseringsforløbet starter lørdag den 1. januar 2011 og gentages dagligt:\n"+
				"   Doseringsforløb:\n"+
				"   Lørdag den 1. januar 2011: 2 stk morgen ved måltid + 2 stk middag ved måltid + 2 stk aften ved måltid + 2 stk nat ved måltid",
				LongTextConverter.convert(w));
		Assert.assertEquals(
				MorningNoonEveningNightConverterImpl.class, 
				ShortTextConverter.getConverterClass(w));
		Assert.assertEquals(
				"2 stk morgen, middag, aften og nat ved måltid", 
				ShortTextConverter.convert(w));
		// assertEquals(10.0, result.getAvgDailyDosis().doubleValue()); 				
	}
	
	@Test
	public void testNoonEveningAndNight() throws Exception {
		DosageStructure dosage = makeDosages(null, 2.0, 3.0, 4.0);
		DosageWrapper w = new DosageWrapper(dosage);
		Assert.assertEquals(
			"Doseringsforløbet starter lørdag den 1. januar 2011 og gentages dagligt:\n"+
			"   Doseringsforløb:\n"+
			"   Lørdag den 1. januar 2011: 2 stk middag ved måltid + 3 stk aften ved måltid + 4 stk nat ved måltid",
			LongTextConverter.convert(w));
		Assert.assertEquals(
				MorningNoonEveningNightConverterImpl.class, 
				ShortTextConverter.getConverterClass(w));
		Assert.assertEquals(
				"2 stk middag, 3 stk aften og 4 stk nat ved måltid", 
				ShortTextConverter.convert(w));
		// assertEquals(9.0, result.getAvgDailyDosis().doubleValue()); 				
	}
	
	@Test
	public void testMorningNoonAndEvening() throws Exception {
		DosageStructure dosage = makeDosages(1.0, 2.0, 3.0, null);
		DosageWrapper w = new DosageWrapper(dosage);
		Assert.assertEquals(
				"Doseringsforløbet starter lørdag den 1. januar 2011 og gentages dagligt:\n"+
				"   Doseringsforløb:\n"+
				"   Lørdag den 1. januar 2011: 1 stk morgen ved måltid + 2 stk middag ved måltid + 3 stk aften ved måltid",
				LongTextConverter.convert(w));
		Assert.assertEquals(
				MorningNoonEveningNightConverterImpl.class, 
				ShortTextConverter.getConverterClass(w));
		Assert.assertEquals(
				"1 stk morgen, 2 stk middag og 3 stk aften ved måltid", 
				ShortTextConverter.convert(w));
		// assertEquals(6.0, result.getAvgDailyDosis().doubleValue()); 				
	}

	@Test
	public void testMorningAndNoonWithZeroIntervals() throws Exception {
		DosageStructure dosage = makeDosagesWithMinMax(0.0, 1.0, 2.0, 3.0, null, null, null, null);
		DosageWrapper w = new DosageWrapper(dosage);
		Assert.assertEquals(
				"Doseringsforløbet starter lørdag den 1. januar 2011 og gentages dagligt:\n"+
				"   Doseringsforløb:\n"+
				"   Lørdag den 1. januar 2011: 0-1 stk morgen ved måltid + 2-3 stk middag ved måltid",
				LongTextConverter.convert(w));
		Assert.assertEquals(
				MorningNoonEveningNightConverterImpl.class, 
				ShortTextConverter.getConverterClass(w));
		Assert.assertEquals(
				"0-1 stk morgen og 2-3 stk middag ved måltid", 
				ShortTextConverter.convert(w));
		//		assertEquals(1.0, result.getMinAvgDailyDosis().doubleValue()); 				
		//		assertEquals(3.0, result.getMaxAvgDailyDosis().doubleValue()); 				
	}
	
	public void testNight() throws Exception {
		DosageStructure dosage = makeDosages(null, null, null, 2.0);
		DosageWrapper w = new DosageWrapper(dosage);
		Assert.assertEquals(
				"Daglig 2 stk nat ved måltid", 
				LongTextConverter.convert(w));
		Assert.assertEquals(
				MorningNoonEveningNightConverterImpl.class, 
				ShortTextConverter.getConverterClass(w));
		Assert.assertEquals(
				"2 stk nat ved måltid", 
				ShortTextConverter.convert(w));
	}
	
}
