package dk.medicinkortet.dosisstructuretext;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.Test;

import dk.medicinkortet.dosisstructuretext.vowrapper.DateOrDateTimeWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.PlainDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructureWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuresWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.UnitOrUnitsWrapper;

/* Test fix of old error in dosagetypecalculator, that returned combined as soon as more than one structure was present */
public class DosageTypeCalculator144Test {
	
	static final SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd");
	static final SimpleDateFormat dateTimeParser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Test
	public void testTemporaryBefore144NowFixed() {
		DosageWrapper dosage = 
				DosageWrapper.makeDosage(
					StructuresWrapper.makeStructures(
						UnitOrUnitsWrapper.makeUnit("stk"),
						StructureWrapper.makeStructure(
							1, "mod smerter", 
							DateOrDateTimeWrapper.makeDate("2017-01-01"), DateOrDateTimeWrapper.makeDate("2017-02-01"),
							DayWrapper.makeDay(1,
								PlainDoseWrapper.makeDose(new BigDecimal(4)), 
								PlainDoseWrapper.makeDose(new BigDecimal(4))))
					));
		
		Assert.assertEquals(DosageType.Fixed, DosageTypeCalculator144.calculate(dosage));
	}
	
	@Test
	public void testCombined() {
		DosageWrapper dosage = 
				DosageWrapper.makeDosage(
					StructuresWrapper.makeStructures(
						UnitOrUnitsWrapper.makeUnit("stk"),
						StructureWrapper.makeStructure(
							1, "mod smerter", 
							DateOrDateTimeWrapper.makeDate("2017-01-01"), DateOrDateTimeWrapper.makeDate("2017-02-01"),
							DayWrapper.makeDay(1,
								PlainDoseWrapper.makeDose(new BigDecimal(4)), 
								PlainDoseWrapper.makeDose(new BigDecimal(4), true)))
					));
		
		Assert.assertEquals(DosageType.Combined, DosageTypeCalculator144.calculate(dosage));
	}
	
	@Test
	public void testFixedOnlyReturnsFixed() {
		DosageWrapper dosage = 
				DosageWrapper.makeDosage(
					StructuresWrapper.makeStructures(
						UnitOrUnitsWrapper.makeUnit("stk"),
						StructureWrapper.makeStructure(
							1, "mod smerter", 
							DateOrDateTimeWrapper.makeDate("2017-01-01"), DateOrDateTimeWrapper.makeDate("2017-02-01"),
							DayWrapper.makeDay(1,
								PlainDoseWrapper.makeDose(new BigDecimal(4)), 
								PlainDoseWrapper.makeDose(new BigDecimal(4))))
					));
		
		Assert.assertEquals(DosageType.Fixed, DosageTypeCalculator144.calculate(dosage));
	}
	
	
	@Test
	public void testMultipleStructuresAllFixedReturnsFixed() {
		DosageWrapper dosage = 
				DosageWrapper.makeDosage(
					StructuresWrapper.makeStructures(
						UnitOrUnitsWrapper.makeUnit("stk"),
						StructureWrapper.makeStructure(
							1, "mod smerter", 
							DateOrDateTimeWrapper.makeDate("2017-01-01"), DateOrDateTimeWrapper.makeDate("2017-01-03"),
							DayWrapper.makeDay(1,
								PlainDoseWrapper.makeDose(new BigDecimal(4)), 
								PlainDoseWrapper.makeDose(new BigDecimal(4)))),
						StructureWrapper.makeStructure(
							1, "mod smerter", 
							DateOrDateTimeWrapper.makeDate("2017-01-04"), null,
							DayWrapper.makeDay(1,
								PlainDoseWrapper.makeDose(new BigDecimal(4)), 
								PlainDoseWrapper.makeDose(new BigDecimal(4))))
					));
		
		Assert.assertEquals(DosageType.Fixed, DosageTypeCalculator144.calculate(dosage));
		
	}
	
	@Test
	public void testMultipleStructuresAllPNReturnsPN() {
		DosageWrapper dosage = 
				DosageWrapper.makeDosage(
					StructuresWrapper.makeStructures(
						UnitOrUnitsWrapper.makeUnit("stk"),
						StructureWrapper.makeStructure(
							1, "mod smerter", 
							DateOrDateTimeWrapper.makeDate("2017-01-01"), DateOrDateTimeWrapper.makeDate("2017-01-03"),
							DayWrapper.makeDay(1,
								PlainDoseWrapper.makeDose(new BigDecimal(4), true), 
								PlainDoseWrapper.makeDose(new BigDecimal(4), true))),
						StructureWrapper.makeStructure(
							1, "mod smerter", 
							DateOrDateTimeWrapper.makeDate("2017-01-04"), null,
							DayWrapper.makeDay(1,
								PlainDoseWrapper.makeDose(new BigDecimal(4), true), 
								PlainDoseWrapper.makeDose(new BigDecimal(4), true)))
					));
		
		Assert.assertEquals(DosageType.AccordingToNeed, DosageTypeCalculator144.calculate(dosage));
		
	}
	
	@Test
	public void testMultipleStructuresInternallyCombinedReturnsCombined() {
		DosageWrapper dosage = 
			DosageWrapper.makeDosage(
				StructuresWrapper.makeStructures(
					UnitOrUnitsWrapper.makeUnit("stk"),
					StructureWrapper.makeStructure(
						1, "mod smerter", 
						DateOrDateTimeWrapper.makeDate("2017-01-01"), null,
						DayWrapper.makeDay(1,
							PlainDoseWrapper.makeDose(new BigDecimal(4)), 
							PlainDoseWrapper.makeDose(new BigDecimal(4), true))),
					StructureWrapper.makeStructure(
						1, "mod smerter", 
						DateOrDateTimeWrapper.makeDate("2017-01-01"), null,
						DayWrapper.makeDay(1,
							PlainDoseWrapper.makeDose(new BigDecimal(4), true), 
							PlainDoseWrapper.makeDose(new BigDecimal(4))))
				));
		
		Assert.assertEquals(DosageType.Combined, DosageTypeCalculator144.calculate(dosage));
		
	}
	
	@Test
	public void testNotOverlappingFixedAndEmptyReturnsFixed() {
		DosageWrapper dosage = 
			DosageWrapper.makeDosage(
				StructuresWrapper.makeStructures(
					UnitOrUnitsWrapper.makeUnit("stk"),
					StructureWrapper.makeStructure(
						1, "mod smerter", 
						DateOrDateTimeWrapper.makeDate("2017-02-01"), DateOrDateTimeWrapper.makeDate("2017-02-02"),
						DayWrapper.makeDay(1,
							PlainDoseWrapper.makeDose(new BigDecimal(4)), 
							PlainDoseWrapper.makeDose(new BigDecimal(4), false))),
					StructureWrapper.makeStructure(
						1, "mod smerter", 
						DateOrDateTimeWrapper.makeDate("2017-02-03"), DateOrDateTimeWrapper.makeDate("2017-02-04"))
				));

		Assert.assertEquals(DosageType.Fixed, DosageTypeCalculator144.calculate(dosage));
	}


	@Test
	public void testOverlappingFixedAndEmptyReturnsCombined() {
		DosageWrapper dosage = 
			DosageWrapper.makeDosage(
				StructuresWrapper.makeStructures(
					UnitOrUnitsWrapper.makeUnit("stk"),
					StructureWrapper.makeStructure(
						1, "mod smerter", 
						DateOrDateTimeWrapper.makeDate("2017-02-01"), DateOrDateTimeWrapper.makeDate("2017-02-02"),
						DayWrapper.makeDay(1,
							PlainDoseWrapper.makeDose(new BigDecimal(4)), 
							PlainDoseWrapper.makeDose(new BigDecimal(4), false))),
					StructureWrapper.makeStructure(
						1, "mod smerter", 
						DateOrDateTimeWrapper.makeDate("2017-02-01"), DateOrDateTimeWrapper.makeDate("2017-02-02"))
				));
		
		Assert.assertEquals(DosageType.Combined, DosageTypeCalculator144.calculate(dosage));
	}

	@Test
	public void testOverlappingFixedWithoutEnddateAndEmptyWithoutEnddateReturnsCombined() {
		DosageWrapper dosage = 
			DosageWrapper.makeDosage(
				StructuresWrapper.makeStructures(
					UnitOrUnitsWrapper.makeUnit("stk"),
					StructureWrapper.makeStructure(	
						1, "mod smerter", 
						DateOrDateTimeWrapper.makeDate("2017-01-01"), null,
						DayWrapper.makeDay(1,
							PlainDoseWrapper.makeDose(new BigDecimal(4)), 
							PlainDoseWrapper.makeDose(new BigDecimal(4), false))),
					StructureWrapper.makeStructure(
						1, "mod smerter", 
						DateOrDateTimeWrapper.makeDate("2017-01-01"), null)
				));
		
		Assert.assertEquals(DosageType.Combined, DosageTypeCalculator144.calculate(dosage));
	}

	
	
	@Test
	public void testNotOverlappingPNAndEmptyReturnsPN() {
		DosageWrapper dosage = 
			DosageWrapper.makeDosage(
				StructuresWrapper.makeStructures(
					UnitOrUnitsWrapper.makeUnit("stk"),
					StructureWrapper.makeStructure(
						1, "mod smerter", 
						DateOrDateTimeWrapper.makeDate("2017-02-01"), DateOrDateTimeWrapper.makeDate("2017-02-02"),
						DayWrapper.makeDay(1,
							PlainDoseWrapper.makeDose(new BigDecimal(4), true))),
					StructureWrapper.makeStructure(
						1, "mod smerter", 
						DateOrDateTimeWrapper.makeDate("2017-02-03"), DateOrDateTimeWrapper.makeDate("2017-02-04"))
				));
	
		Assert.assertEquals(DosageType.AccordingToNeed, DosageTypeCalculator144.calculate(dosage));
	}

	@Test
	public void testOverlappingPNAndEmptyReturnsCombined() {
		DosageWrapper dosage = 
			DosageWrapper.makeDosage(
				StructuresWrapper.makeStructures(
					UnitOrUnitsWrapper.makeUnit("stk"),
					StructureWrapper.makeStructure(
						1, "mod smerter", 
						DateOrDateTimeWrapper.makeDate("2017-01-01"), null,
						DayWrapper.makeDay(1,
							PlainDoseWrapper.makeDose(new BigDecimal(4), true))),
					StructureWrapper.makeStructure(
						1, "mod smerter", 
						DateOrDateTimeWrapper.makeDate("2017-01-01"), null)
				));
		
		Assert.assertEquals(DosageType.Combined, DosageTypeCalculator144.calculate(dosage));
	}
	
	// FMK-3247 Forkerte doseringstyper ved tomme doseringsperioder
	@Test
	public void testWithSeveralEmptyPeriodsReturnsCombined() {
		DosageWrapper dosage = 
			DosageWrapper.makeDosage(
				StructuresWrapper.makeStructures(
					UnitOrUnitsWrapper.makeUnit("stk"),
					StructureWrapper.makeStructure(1, "mod smerter", 
						DateOrDateTimeWrapper.makeDate("2017-01-01"), DateOrDateTimeWrapper.makeDate("2017-01-03"),
						DayWrapper.makeDay(1,PlainDoseWrapper.makeDose(new BigDecimal(4)))),
					StructureWrapper.makeStructure(1, "mod smerter", 
						DateOrDateTimeWrapper.makeDate("2017-01-04"), DateOrDateTimeWrapper.makeDate("2017-01-12")),
					StructureWrapper.makeStructure(1, "mod smerter", 
						DateOrDateTimeWrapper.makeDate("2017-01-13"), DateOrDateTimeWrapper.makeDate("2017-01-15"),
						DayWrapper.makeDay(1,PlainDoseWrapper.makeDose(new BigDecimal(4)))),
					StructureWrapper.makeStructure(1, "mod smerter", 
							DateOrDateTimeWrapper.makeDate("2017-01-01"), DateOrDateTimeWrapper.makeDate("2017-01-15"))
				));
		
		Assert.assertEquals(DosageType.Combined, DosageTypeCalculator144.calculate(dosage));
	}
	
	// Helper method tests
	
	@Test
	public void testDateAbuts() throws ParseException {
		Assert.assertTrue(DosageTypeCalculator144.dateAbuts(dateParser.parse("2017-02-01"), dateParser.parse("2017-02-02")));
		Assert.assertFalse(DosageTypeCalculator144.dateAbuts(dateParser.parse("2017-02-01"), dateParser.parse("2017-02-03")));
	}
	
	@Test
	public void testDateTimeAbuts() throws ParseException {
		Assert.assertTrue(DosageTypeCalculator144.dateTimeAbuts(dateTimeParser.parse("2017-02-01 10:00:00"), dateTimeParser.parse("2017-02-01 10:00:01")));
		Assert.assertFalse(DosageTypeCalculator144.dateTimeAbuts(dateTimeParser.parse("2017-02-01 10:00:00"), dateTimeParser.parse("2017-02-01 11:00:00")));
	}
	
	@Test
	public void testStructuresWithDatesAbuts() {

		// First structure with end date - abuts
		StructureWrapper s1 = StructureWrapper.makeStructure(1, "mod smerter", DateOrDateTimeWrapper.makeDate("2017-01-01"), DateOrDateTimeWrapper.makeDate("2017-01-02"));
		StructureWrapper s2 = StructureWrapper.makeStructure(1, "mod smerter", DateOrDateTimeWrapper.makeDate("2017-01-03"), DateOrDateTimeWrapper.makeDate("2017-01-03"));
		Assert.assertTrue(DosageTypeCalculator144.abuts(s1,  s2));
		
		// First structure with end date - overlaps
		s1 = StructureWrapper.makeStructure(1, "mod smerter", DateOrDateTimeWrapper.makeDate("2017-01-01"), DateOrDateTimeWrapper.makeDate("2017-01-03"));
		s2 = StructureWrapper.makeStructure(1, "mod smerter", DateOrDateTimeWrapper.makeDate("2017-01-03"), DateOrDateTimeWrapper.makeDate("2017-01-05"));
		Assert.assertFalse(DosageTypeCalculator144.abuts(s1,  s2));
		
		// First structure with end date - gap
		s1 = StructureWrapper.makeStructure(1, "mod smerter", DateOrDateTimeWrapper.makeDate("2017-01-01"), DateOrDateTimeWrapper.makeDate("2017-01-03"));
		s2 = StructureWrapper.makeStructure(1, "mod smerter", DateOrDateTimeWrapper.makeDate("2017-01-05"), DateOrDateTimeWrapper.makeDate("2017-01-06"));
		Assert.assertFalse(DosageTypeCalculator144.abuts(s1,  s2));
		
				
		// First structure without end date
		s1 = StructureWrapper.makeStructure(1, "mod smerter", DateOrDateTimeWrapper.makeDate("2017-01-01"), null);
		s2 = StructureWrapper.makeStructure(1, "mod smerter", DateOrDateTimeWrapper.makeDate("2017-01-02"), DateOrDateTimeWrapper.makeDate("2017-01-03"));
		Assert.assertFalse(DosageTypeCalculator144.abuts(s1,  s2));
	}
	
	@Test
	public void testStructuresWithDateTimesAbuts() {

		// First structure with end date - abuts
		StructureWrapper s1 = StructureWrapper.makeStructure(1, "mod smerter", DateOrDateTimeWrapper.makeDateTime("2017-01-01 10:00:00"), DateOrDateTimeWrapper.makeDateTime("2017-01-02 11:00:00"));
		StructureWrapper s2 = StructureWrapper.makeStructure(1, "mod smerter", DateOrDateTimeWrapper.makeDateTime("2017-01-02 11:00:01"), DateOrDateTimeWrapper.makeDateTime("2017-01-03 08:00:00"));
		Assert.assertTrue(DosageTypeCalculator144.abuts(s1,  s2));
		
		// First structure with end date - equals seconds structures start (only possible due to old data in production, validation rejects new dosages of that kind)
		s1 = StructureWrapper.makeStructure(1, "mod smerter", DateOrDateTimeWrapper.makeDateTime("2017-01-01 10:00:00"), DateOrDateTimeWrapper.makeDateTime("2017-01-02 11:00:00"));
		s2 = StructureWrapper.makeStructure(1, "mod smerter", DateOrDateTimeWrapper.makeDateTime("2017-01-02 11:00:00"), DateOrDateTimeWrapper.makeDateTime("2017-01-03 08:00:00"));
		Assert.assertTrue(DosageTypeCalculator144.abuts(s1,  s2));
	
		// First structure with end date - overlaps
		s1 = StructureWrapper.makeStructure(1, "mod smerter", DateOrDateTimeWrapper.makeDateTime("2017-01-01 10:00:00"), DateOrDateTimeWrapper.makeDateTime("2017-01-03 09:00:00"));
		s2 = StructureWrapper.makeStructure(1, "mod smerter", DateOrDateTimeWrapper.makeDateTime("2017-01-03 08:00:00"), DateOrDateTimeWrapper.makeDateTime("2017-01-05 11:00:00"));
		Assert.assertFalse(DosageTypeCalculator144.abuts(s1,  s2));
		
		// First structure with end date - gap
		s1 = StructureWrapper.makeStructure(1, "mod smerter", DateOrDateTimeWrapper.makeDateTime("2017-01-01 10:00:00"), DateOrDateTimeWrapper.makeDateTime("2017-01-03 11:00:00"));
		s2 = StructureWrapper.makeStructure(1, "mod smerter", DateOrDateTimeWrapper.makeDateTime("2017-01-05 08:00:00"), DateOrDateTimeWrapper.makeDateTime("2017-01-06 12:00:00"));
		Assert.assertFalse(DosageTypeCalculator144.abuts(s1,  s2));
		
				
		// First structure without end date
		s1 = StructureWrapper.makeStructure(1, "mod smerter", DateOrDateTimeWrapper.makeDateTime("2017-01-01 08:00:00"), null);
		s2 = StructureWrapper.makeStructure(1, "mod smerter", DateOrDateTimeWrapper.makeDateTime("2017-01-02 08:00:00"), DateOrDateTimeWrapper.makeDateTime("2017-01-03 12:00:00"));
		Assert.assertFalse(DosageTypeCalculator144.abuts(s1,  s2));
	}

	@Test
	public void testSplitInFixedAndPN() {
	
		LinkedList<StructureWrapper> fixedStructures = new LinkedList<>();
		LinkedList<StructureWrapper> pnStructures = new LinkedList<>();
		
		// pn only
		StructuresWrapper sw =
				StructuresWrapper.makeStructures(UnitOrUnitsWrapper.makeUnit("stk"),
				StructureWrapper.makeStructure(1, "mod smerter", DateOrDateTimeWrapper.makeDate("2017-01-01"), DateOrDateTimeWrapper.makeDate("2017-01-02"), 
				DayWrapper.makeDay(1, PlainDoseWrapper.makeDose(new BigDecimal(4), true))));
		
		DosageTypeCalculator144.splitInFixedAndPN(sw, fixedStructures, pnStructures);
		Assert.assertEquals(0, fixedStructures.size());
		Assert.assertEquals(1, pnStructures.size());
		fixedStructures.clear();
		pnStructures.clear();
		
		// fixed only
		sw = StructuresWrapper.makeStructures(UnitOrUnitsWrapper.makeUnit("stk"),
				StructureWrapper.makeStructure(1, "mod smerter", DateOrDateTimeWrapper.makeDate("2017-01-01"), DateOrDateTimeWrapper.makeDate("2017-01-02"), 
				DayWrapper.makeDay(1, PlainDoseWrapper.makeDose(new BigDecimal(4), false))));
		
		DosageTypeCalculator144.splitInFixedAndPN(sw, fixedStructures, pnStructures);
		Assert.assertEquals(1, fixedStructures.size());
		Assert.assertEquals(0, pnStructures.size());
		fixedStructures.clear();
		pnStructures.clear();
		
		
		// fixed: empty + nonempety, pn: none
		sw = StructuresWrapper.makeStructures(UnitOrUnitsWrapper.makeUnit("stk"),
				StructureWrapper.makeStructure(1, "mod smerter", DateOrDateTimeWrapper.makeDate("2017-01-01"), DateOrDateTimeWrapper.makeDate("2017-01-02")),
				StructureWrapper.makeStructure(1, "mod smerter", DateOrDateTimeWrapper.makeDate("2017-01-03"), DateOrDateTimeWrapper.makeDate("2017-01-10"), 
						DayWrapper.makeDay(1, PlainDoseWrapper.makeDose(new BigDecimal(4), false))
						));
		
		DosageTypeCalculator144.splitInFixedAndPN(sw, fixedStructures, pnStructures);
		Assert.assertEquals(2, fixedStructures.size());
		Assert.assertEquals(0, pnStructures.size());
		fixedStructures.clear();
		pnStructures.clear();
		
		// fixed: notempty + empty, pn: none
		sw = StructuresWrapper.makeStructures(UnitOrUnitsWrapper.makeUnit("stk"),
			StructureWrapper.makeStructure(1, "mod smerter", DateOrDateTimeWrapper.makeDate("2017-01-01"), DateOrDateTimeWrapper.makeDate("2017-01-02"), DayWrapper.makeDay(1, PlainDoseWrapper.makeDose(new BigDecimal(4), false))),
			StructureWrapper.makeStructure(1, "mod smerter", DateOrDateTimeWrapper.makeDate("2017-01-03"), DateOrDateTimeWrapper.makeDate("2017-01-10")
		));
		
		DosageTypeCalculator144.splitInFixedAndPN(sw, fixedStructures, pnStructures);
		Assert.assertEquals(2, fixedStructures.size());
		Assert.assertEquals(0, pnStructures.size());
		fixedStructures.clear();
		pnStructures.clear();
		
		// fixed: none, pn: empty + nonempety
		sw = StructuresWrapper.makeStructures(UnitOrUnitsWrapper.makeUnit("stk"),
				StructureWrapper.makeStructure(1, "mod smerter", DateOrDateTimeWrapper.makeDate("2017-01-01"), DateOrDateTimeWrapper.makeDate("2017-01-02")),
				StructureWrapper.makeStructure(1, "mod smerter", DateOrDateTimeWrapper.makeDate("2017-01-03"), DateOrDateTimeWrapper.makeDate("2017-01-10"), 
						DayWrapper.makeDay(1, PlainDoseWrapper.makeDose(new BigDecimal(4), true))
						));
			
		DosageTypeCalculator144.splitInFixedAndPN(sw, fixedStructures, pnStructures);
		Assert.assertEquals(0, fixedStructures.size());
		Assert.assertEquals(2, pnStructures.size());
		fixedStructures.clear();
		pnStructures.clear();
		
		// fixed: none, pn: nonempety + empty
		sw = StructuresWrapper.makeStructures(UnitOrUnitsWrapper.makeUnit("stk"),
				StructureWrapper.makeStructure(1, "mod smerter", DateOrDateTimeWrapper.makeDate("2017-01-01"), DateOrDateTimeWrapper.makeDate("2017-01-02"), DayWrapper.makeDay(1, PlainDoseWrapper.makeDose(new BigDecimal(4), true))),
				StructureWrapper.makeStructure(1, "mod smerter", DateOrDateTimeWrapper.makeDate("2017-01-03"), DateOrDateTimeWrapper.makeDate("2017-01-10") 
		));
			
		DosageTypeCalculator144.splitInFixedAndPN(sw, fixedStructures, pnStructures);
		Assert.assertEquals(0, fixedStructures.size());
		Assert.assertEquals(2, pnStructures.size());
		fixedStructures.clear();
		pnStructures.clear();
		
		// fixed: none, pn: empety + notempty + empty 
		sw = StructuresWrapper.makeStructures(UnitOrUnitsWrapper.makeUnit("stk"),
				StructureWrapper.makeStructure(1, "mod smerter", DateOrDateTimeWrapper.makeDate("2016-12-29"), DateOrDateTimeWrapper.makeDate("2016-12-31")),
				StructureWrapper.makeStructure(1, "mod smerter", DateOrDateTimeWrapper.makeDate("2017-01-01"), DateOrDateTimeWrapper.makeDate("2017-01-02"), DayWrapper.makeDay(1, PlainDoseWrapper.makeDose(new BigDecimal(4), true))),
				StructureWrapper.makeStructure(1, "mod smerter", DateOrDateTimeWrapper.makeDate("2017-01-03"), DateOrDateTimeWrapper.makeDate("2017-01-10"))
		);
			
		DosageTypeCalculator144.splitInFixedAndPN(sw, fixedStructures, pnStructures);
		Assert.assertEquals(0, fixedStructures.size());
		Assert.assertEquals(3, pnStructures.size());
		fixedStructures.clear();
		pnStructures.clear();
		
		// fixed: none, pn: empety + notempty + empty 
		sw = StructuresWrapper.makeStructures(UnitOrUnitsWrapper.makeUnit("stk"),
				StructureWrapper.makeStructure(1, "mod smerter", DateOrDateTimeWrapper.makeDate("2016-12-29"), DateOrDateTimeWrapper.makeDate("2016-12-31")),
				StructureWrapper.makeStructure(1, "mod smerter", DateOrDateTimeWrapper.makeDate("2017-01-01"), DateOrDateTimeWrapper.makeDate("2017-01-02"), DayWrapper.makeDay(1, PlainDoseWrapper.makeDose(new BigDecimal(4), false))),
				StructureWrapper.makeStructure(1, "mod smerter", DateOrDateTimeWrapper.makeDate("2017-01-03"), DateOrDateTimeWrapper.makeDate("2017-01-10"))
		);
			
		DosageTypeCalculator144.splitInFixedAndPN(sw, fixedStructures, pnStructures);
		Assert.assertEquals(3, fixedStructures.size());
		Assert.assertEquals(0, pnStructures.size());
		fixedStructures.clear();
		pnStructures.clear();

		// fixed: notempty + empty + notempty, pn: overlapping empty 
		sw = StructuresWrapper.makeStructures(UnitOrUnitsWrapper.makeUnit("stk"),
				StructureWrapper.makeStructure(1, "mod smerter", DateOrDateTimeWrapper.makeDate("2016-12-29"), DateOrDateTimeWrapper.makeDate("2016-12-31"), DayWrapper.makeDay(1, PlainDoseWrapper.makeDose(new BigDecimal(4), false))),
				StructureWrapper.makeStructure(1, "mod smerter", DateOrDateTimeWrapper.makeDate("2017-01-01"), DateOrDateTimeWrapper.makeDate("2017-01-02")),
				StructureWrapper.makeStructure(1, "mod smerter", DateOrDateTimeWrapper.makeDate("2017-01-01"), DateOrDateTimeWrapper.makeDate("2017-01-02")),
				StructureWrapper.makeStructure(1, "mod smerter", DateOrDateTimeWrapper.makeDate("2017-01-03"), DateOrDateTimeWrapper.makeDate("2017-01-10"), DayWrapper.makeDay(1, PlainDoseWrapper.makeDose(new BigDecimal(4), false)))
		);
			
		DosageTypeCalculator144.splitInFixedAndPN(sw, fixedStructures, pnStructures);
		Assert.assertEquals(3, fixedStructures.size());
		Assert.assertEquals(1, pnStructures.size());
		fixedStructures.clear();
		pnStructures.clear();
	}
}
