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

package dk.medicinkortet.dosisstructuretext.vowrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.SortedSet;
import java.util.TreeSet;

public class StructuresWrapper {
	
	private UnitOrUnitsWrapper unitOrUnits;
	private SortedSet<StructureWrapper> structures;

	private static final Comparator<StructureWrapper> STRUCTURE_COMPARATOR = new Comparator<StructureWrapper>() {
		@Override
		public int compare(StructureWrapper o1, StructureWrapper o2) {
			int i = o1.getStartDateOrDateTime().getDateOrDateTime().compareTo(o2.getStartDateOrDateTime().getDateOrDateTime());
			if(i!=0)
				return i;
			if(o1.containsAccordingToNeedDosesOnly())
				return 1;
			else
				return -1;
		}
	};
		
	public static StructuresWrapper makeStructures(UnitOrUnitsWrapper unitOrUnits, StructureWrapper... structures) {
		TreeSet<StructureWrapper> set = new TreeSet<StructureWrapper>(STRUCTURE_COMPARATOR);
		set.addAll(Arrays.asList(structures));
		return new StructuresWrapper(unitOrUnits, set);
	}

	public static StructuresWrapper makeStructures(UnitOrUnitsWrapper unitOrUnits, Collection<StructureWrapper> structures) {
		if(structures instanceof SortedSet<?>)
			return new StructuresWrapper(unitOrUnits, (SortedSet<StructureWrapper>)structures);
		else {
			TreeSet<StructureWrapper> set = new TreeSet<StructureWrapper>(STRUCTURE_COMPARATOR);
			set.addAll(structures);		
			return new StructuresWrapper(unitOrUnits, set); 
		}
	}

	private StructuresWrapper(UnitOrUnitsWrapper unitOrUnits, SortedSet<StructureWrapper> structures) {
		this.unitOrUnits = unitOrUnits;
		if(structures==null)
			throw new NullPointerException();
		if(structures.size()==0)
			throw new IllegalArgumentException();
		this.structures = structures;
	}
	
	public UnitOrUnitsWrapper getUnitOrUnits() {
		return unitOrUnits;
	}

	public SortedSet<StructureWrapper> getStructures() {
		return structures;
	}

	public boolean hasOverlappingPeriodes() {
		ArrayList<StructureWrapper> ss = new ArrayList<StructureWrapper>(getStructures());
		for(int i=0; i<ss.size(); i++) {
			for(int j=i+1; j<ss.size(); j++) {
				DateOrDateTimeWrapper dis = ss.get(i).getStartDateOrDateTime();
				DateOrDateTimeWrapper die = ss.get(i).getEndDateOrDateTime();
				DateOrDateTimeWrapper djs = ss.get(j).getStartDateOrDateTime();
				DateOrDateTimeWrapper dje = ss.get(j).getEndDateOrDateTime();
				if(overlaps(dis, die, djs, dje))
					return true;
			}
		}
		return false;
	}

	private boolean overlaps(DateOrDateTimeWrapper dis,	DateOrDateTimeWrapper die, DateOrDateTimeWrapper djs, DateOrDateTimeWrapper dje) {
		GregorianCalendar cis = makeStart(dis);
		GregorianCalendar cjs = makeStart(djs);
		if(cis.equals(cjs)) {
			return true;
		}
		GregorianCalendar cie = makeEnd(die);
		GregorianCalendar cje = makeEnd(dje);
		if(cis.before(cjs)) {
			return !cie.before(cje);
		}
		else {
			return !cje.before(cie);
		}
	}
	
	private GregorianCalendar makeStart(DateOrDateTimeWrapper ds) {
		GregorianCalendar c = new GregorianCalendar();
		if(ds!=null && ds.getDateTime()!=null) {
			c.setTime(ds.getDateTime());			
			c.set(GregorianCalendar.MILLISECOND, 0);
		}		
		else if(ds!=null && ds.getDate()!=null) {
			c.setTime(ds.getDate());
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(GregorianCalendar.MILLISECOND, 0);
		}
		else {
			c.set(Calendar.YEAR, 2000);
			c.set(Calendar.MONTH, Calendar.JANUARY);
			c.set(Calendar.DATE, 1);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(GregorianCalendar.MILLISECOND, 0);
		}
		return c;
	}
	
	private GregorianCalendar makeEnd(DateOrDateTimeWrapper de) {
		GregorianCalendar c = new GregorianCalendar();
		if(de!=null && de.getDateTime()!=null) {
			c.setTime(de.getDateTime());			
			c.set(GregorianCalendar.MILLISECOND, 0);
		}		
		else if(de!=null && de.getDate()!=null) {
			c.setTime(de.getDate());
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(GregorianCalendar.MILLISECOND, 0);
		}
		else {
			c.set(Calendar.YEAR, 2999);
			c.set(Calendar.MONTH, Calendar.DECEMBER);
			c.set(Calendar.DATE, 31);
			c.set(Calendar.HOUR_OF_DAY, 23);
			c.set(Calendar.MINUTE, 59);
			c.set(Calendar.SECOND, 59);
			c.set(GregorianCalendar.MILLISECOND, 0);
		}
		return c;
	}
	
}
