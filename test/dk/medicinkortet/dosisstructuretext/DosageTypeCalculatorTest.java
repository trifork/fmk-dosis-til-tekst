package dk.medicinkortet.dosisstructuretext;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

import dk.medicinkortet.dosisstructuretext.vowrapper.DateOrDateTimeWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.PlainDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructureWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuresWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.UnitOrUnitsWrapper;

/* Test fix of error (FMK-2421) in dosagetypecalculator, that returned combined as soon as more than one structure was present */
public class DosageTypeCalculatorTest {

	@Test
	public void testTemporary() {
		DosageWrapper dosage = 
				DosageWrapper.makeDosage(
					StructuresWrapper.makeStructures(
						UnitOrUnitsWrapper.makeUnit("stk"),
						StructureWrapper.makeStructure(
							1, "mod smerter", 
							DateOrDateTimeWrapper.makeDate("2011-01-01"), DateOrDateTimeWrapper.makeDate("2011-02-01"),
							DayWrapper.makeDay(1,
								PlainDoseWrapper.makeDose(new BigDecimal(4)), 
								PlainDoseWrapper.makeDose(new BigDecimal(4))))
					));
		
		Assert.assertEquals(DosageType.Temporary, DosageTypeCalculator.calculate(dosage));
		
	}
	
	@Test
	public void testMultipleStructuresAllFixed() {
		DosageWrapper dosage = 
				DosageWrapper.makeDosage(
					StructuresWrapper.makeStructures(
						UnitOrUnitsWrapper.makeUnit("stk"),
						StructureWrapper.makeStructure(
							1, "mod smerter", 
							DateOrDateTimeWrapper.makeDate("2011-01-01"), null,
							DayWrapper.makeDay(1,
								PlainDoseWrapper.makeDose(new BigDecimal(4)), 
								PlainDoseWrapper.makeDose(new BigDecimal(4)))),
						StructureWrapper.makeStructure(
							1, "mod smerter", 
							DateOrDateTimeWrapper.makeDate("2011-01-01"), null,
							DayWrapper.makeDay(1,
								PlainDoseWrapper.makeDose(new BigDecimal(4)), 
								PlainDoseWrapper.makeDose(new BigDecimal(4))))
					));
		
		Assert.assertEquals(DosageType.Fixed, DosageTypeCalculator.calculate(dosage));
		
	}
	
	@Test
	public void testMultipleStructuresAllPN() {
		DosageWrapper dosage = 
				DosageWrapper.makeDosage(
					StructuresWrapper.makeStructures(
						UnitOrUnitsWrapper.makeUnit("stk"),
						StructureWrapper.makeStructure(
							1, "mod smerter", 
							DateOrDateTimeWrapper.makeDate("2011-01-01"), null,
							DayWrapper.makeDay(1,
								PlainDoseWrapper.makeDose(new BigDecimal(4), true), 
								PlainDoseWrapper.makeDose(new BigDecimal(4), true))),
						StructureWrapper.makeStructure(
							1, "mod smerter", 
							DateOrDateTimeWrapper.makeDate("2011-01-01"), null,
							DayWrapper.makeDay(1,
								PlainDoseWrapper.makeDose(new BigDecimal(4), true), 
								PlainDoseWrapper.makeDose(new BigDecimal(4), true)))
					));
		
		Assert.assertEquals(DosageType.AccordingToNeed, DosageTypeCalculator.calculate(dosage));
		
	}
	
	@Test
	public void testMultipleStructuresCombined() {
		DosageWrapper dosage = 
				DosageWrapper.makeDosage(
					StructuresWrapper.makeStructures(
						UnitOrUnitsWrapper.makeUnit("stk"),
						StructureWrapper.makeStructure(
							1, "mod smerter", 
							DateOrDateTimeWrapper.makeDate("2011-01-01"), null,
							DayWrapper.makeDay(1,
								PlainDoseWrapper.makeDose(new BigDecimal(4)), 
								PlainDoseWrapper.makeDose(new BigDecimal(4), true))),
						StructureWrapper.makeStructure(
							1, "mod smerter", 
							DateOrDateTimeWrapper.makeDate("2011-01-01"), null,
							DayWrapper.makeDay(1,
								PlainDoseWrapper.makeDose(new BigDecimal(4), true), 
								PlainDoseWrapper.makeDose(new BigDecimal(4))))
					));
		
		Assert.assertEquals(DosageType.Combined, DosageTypeCalculator.calculate(dosage));
		
	}
}
