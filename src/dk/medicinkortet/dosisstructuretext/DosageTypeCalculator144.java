/**
* The contents of this file are subject to the Mozilla Public
* License Version 1.1 (the "License"); you may not use this file
* except in compliance with the License. You may obtain a copy of
* the License at http://www.mozilla.org/MPL/
*
* Software distributed under the License is distributed on an "AS
* IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
* implied. See the License for the specific language governing
* rights and limitations under the License.
*
* Contributor(s): Contributors are attributed in the source code
* where applicable.
*
* The Original Code is "Dosis-til-tekst".
*
* The Initial Developer of the Original Code is Trifork Public A/S.
*
* Portions created for the FMK Project are Copyright 2011,
* National Board of e-Health (NSI). All Rights Reserved.
*/

package dk.medicinkortet.dosisstructuretext;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructureWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.StructuresWrapper;

/*** 
 * From FMK 1.4.4 and above, only 3 dosage types are available: Fixed, AccordingToNeed and Combined (besides Unspec.).
 * Use this DosageTypeCalculator144 for all services using FMK 1.4.4 and higher, and use DosageTypeCalculator for FMK 1.4.2 and below
 * 
 * @author chj
 *
 */
public class DosageTypeCalculator144 {
	
	public static DosageType calculate(DosageWrapper dosage) {
		if(dosage.isAdministrationAccordingToSchema())
			return DosageType.Unspecified;
		else if(dosage.isFreeText())
			return DosageType.Unspecified;
		else 
			return calculateFromStructures(dosage.getStructures());
	}
	
	private  static DosageType calculateFromStructures(StructuresWrapper structures) {
		if(hasAtLeastOneCombinedStructure(structures) || hasMixedNotEmptyStructures(structures)) {
			return DosageType.Combined;
		}
		else {
			/* Invariant at this point: all structures are a) fixed or empty .... or b) PN or empty
			 * We now have to calculate which empty periods are from the fixed and which are from the PN part
			 * This is done by looking at gaps/overlaps in the fixed/PN dosage part
			 * 
			 */
			LinkedList<StructureWrapper> fixedStructures = new LinkedList<>();
			LinkedList<StructureWrapper> pnStructures = new LinkedList<>();
			
			splitInFixedAndPN(structures, fixedStructures, pnStructures);

			if(fixedStructures.isEmpty()) {
				return DosageType.AccordingToNeed;
			}
			else {
				return pnStructures.isEmpty() ? DosageType.Fixed : DosageType.Combined;
						
			}
		}
	}

	public static int structureSorter(StructureWrapper s1, StructureWrapper s2) {
		return s1.getStartDateOrDateTime().getDateOrDateTime().compareTo(s2.getStartDateOrDateTime().getDateOrDateTime());
	}

	
	/*
	 * Precondition: all structures contains only fixed or pn doses
	 * In case some contained mixed, this method would never have been called, since we then know that the DosageType would be combined
	 */
	protected static void splitInFixedAndPN(StructuresWrapper structures, LinkedList<StructureWrapper> fixedStructures, LinkedList<StructureWrapper> pnStructures) {
		
		 LinkedList<StructureWrapper> emptyStructures = new LinkedList<>();
		 
		structures.getStructures().forEach(s -> {
			if(s.isEmpty()) {
				emptyStructures.add(s);
			}
			else {
				if(s.containsAccordingToNeedDosesOnly()) {
					pnStructures.add(s);
				}
				else {
					fixedStructures.add(s);
				}
			}
		});
		
		fixedStructures.sort(DosageTypeCalculator144::structureSorter);
		pnStructures.sort(DosageTypeCalculator144::structureSorter);
		emptyStructures.sort(DosageTypeCalculator144::structureSorter);

		/* Find all gaps in the fixed and pn structures, and insert fitting emptystructures in the gaps
		 * We know that some should fit, since it is validated in the DosageStructureValidator, that no gaps are present.  
		 */
		fillGapsWithEmptyPeriods(fixedStructures, emptyStructures);
		fillGapsWithEmptyPeriods(pnStructures, emptyStructures);
		
		/* in case any emptystructures are left, they should be placed either at the beginning or end of either the fixed or the pn structures */
		
		LinkedList<StructureWrapper> unhandledEmptyStructures = new LinkedList<>();
		
		for(int i = 0; i < emptyStructures.size(); i++) {
			StructureWrapper es = emptyStructures.get(i);
			boolean handled = false;
			
			if(!fixedStructures.isEmpty()) {
				if(abuts(es, fixedStructures.getFirst())) {
					fixedStructures.addFirst(es);
					handled = true;
				}
				else if(abuts(fixedStructures.getLast(), es)) {
					fixedStructures.addLast(es);
					handled = true;
				}
			}
			
			if(!handled && !pnStructures.isEmpty()) {
				if(abuts(es, pnStructures.getFirst())) {
					pnStructures.addFirst(es);
					handled = true;
				}
				else if(abuts(pnStructures.getLast(), es)) {
					pnStructures.addLast(es);
					handled = true;
				}
			}
			
			if(!handled) {
				unhandledEmptyStructures.add(es);
			}
		}
		
		/* In case there are still unhandled empy structures, and either fixed or pn-structures are completely empty, they should go there */
		boolean noFixedStructures = fixedStructures.isEmpty();
		boolean noPNStructures = pnStructures.isEmpty();
		
		unhandledEmptyStructures.forEach(es -> {
			if(noFixedStructures) {
				fixedStructures.add(es);
			}
			else if(noPNStructures) {
				pnStructures.add(es);
			}
		});
	}

	/* Check if second wrapper comes just after the first, without gaps or overlaps */
	protected static boolean abuts(StructureWrapper first, StructureWrapper second) {
		if(first.getEndDateOrDateTime() != null) { 
			if(first.getEndDateOrDateTime().getDate() != null 
				&& second.getStartDateOrDateTime().getDate() != null 
				&& dateAbuts(first.getEndDateOrDateTime().getDate(), second.getStartDateOrDateTime().getDate())) {
				return true;
			}
			else if(first.getEndDateOrDateTime().getDateTime() != null
					&& second.getStartDateOrDateTime().getDateTime() != null
					&& dateTimeAbuts(first.getEndDateOrDateTime().getDateTime(), second.getStartDateOrDateTime().getDateTime())) {
				return true;
			}
			// If an interval ends with a date and the next ends with a datetime we cannot determine if they abut 
		}
		
		// No end date, definitely not abut
		return false;
	}
	
	protected static boolean dateTimeAbuts(Date dateTime1, Date dateTime2) {
		long secondsBetween = ChronoUnit.SECONDS.between(Instant.ofEpochMilli(dateTime1.getTime()), Instant.ofEpochMilli(dateTime2.getTime()));
		return (secondsBetween  >= 0 && secondsBetween <= 1);
	}

	protected static boolean dateAbuts(Date d1, Date d2) {
		return Period.between(d1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), d2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()).getDays() == 1;
	}
	
	private static void fillGapsWithEmptyPeriods(LinkedList<StructureWrapper> structures, LinkedList<StructureWrapper> emptyStructures) {
		int structuresSize = structures.size();
		for(int i = 0; i < structuresSize - 1; i++) {
			if(hasGap(structures.get(i), structures.get(i+1))) {
				StructureWrapper emptyStructureFittingGap = findEmptyStructuresThatFitsGap(emptyStructures, structures.get(i), structures.get(i+1));
				if(emptyStructureFittingGap != null) {
					structures.add(i+1, emptyStructureFittingGap);
					structuresSize++;
					emptyStructures.remove(emptyStructureFittingGap);
				}
			}
		}
	}

	private static StructureWrapper findEmptyStructuresThatFitsGap(LinkedList<StructureWrapper> emptyStructures, StructureWrapper structureWrapper1, StructureWrapper structureWrapper2) {
		return emptyStructures.stream().filter(es -> abuts(structureWrapper1, es) && abuts(es, structureWrapper2)).findFirst().orElse(null);
	}

	protected static boolean hasGap(StructureWrapper structureWrapper1, StructureWrapper structureWrapper2) {
		return !abuts(structureWrapper1, structureWrapper2);
	}


	/* Check if at least one not-empty Structure containing both fixed and PN Dose's exists */
	private static boolean hasAtLeastOneCombinedStructure(StructuresWrapper structures) {
		return structures.getStructures().stream().anyMatch(s -> s.containsAccordingToNeedDose() && !s.containsAccordingToNeedDosesOnly());
	}

	/* Check if at least one structure with fixed dose's only AND at least one structure with PN only doses exists 
	 * Precondition: since this method is called after hasAtLeastOneCombinedStructure(), 
	 * then we suggest that all Structures contains either fixed or PN doses, not combined inside one Structure
	 */
	private static boolean hasMixedNotEmptyStructures(StructuresWrapper structures) {
		if(structures != null && structures.getStructures() != null) {
			Iterator<StructureWrapper> structureIterator = structures.getStructures().iterator();
			
			StructureWrapper firstNotEmptyStructure = null;
			
			// Find first none-empty structure
			while(firstNotEmptyStructure == null && structureIterator.hasNext()) {
				StructureWrapper firstNotEmptyStructureCandidate = structureIterator.next();
				if(!firstNotEmptyStructureCandidate.getDays().isEmpty()) {
					firstNotEmptyStructure = firstNotEmptyStructureCandidate;
				}
			}
			
			if(firstNotEmptyStructure != null) {
				DosageType firstType = calculateFromStructure(firstNotEmptyStructure);
				
				while(structureIterator.hasNext()) {
					StructureWrapper structure = structureIterator.next();
					if(!structure.getDays().isEmpty() && !firstType.equals(calculateFromStructure(structure))) {
						return true;
					}
				}
			}
		}
		
		return false;
 	}
	
	private static DosageType calculateFromStructure(StructureWrapper structure) {
		if(DosageTypeCalculator.isAccordingToNeed(structure)) {
			return DosageType.AccordingToNeed;
		}
		else if(structure.containsAccordingToNeedDose()) {
			return DosageType.Combined;
		}
		else {
			return DosageType.Fixed;
		}
	}
}
 