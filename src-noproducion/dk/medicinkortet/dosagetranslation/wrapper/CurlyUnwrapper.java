package dk.medicinkortet.dosagetranslation.wrapper;

import java.util.ArrayList;
import java.util.Arrays;

import dk.medicinkortet.dosagetranslation.RawDefinition;
import dk.medicinkortet.dosagetranslation.ValidationException;

public class CurlyUnwrapper {

	public static void unwrapCurlies(RawDefinition rawDefinition) throws ValidationException {

		if( rawDefinition.getIterationInterval().indexOf("{")>=0 || 
			rawDefinition.getType().indexOf("{")>=0 || 
			rawDefinition.getMapping().indexOf("{")>=0) {
			try {
				rawDefinition.setIterationIntervals(splitToIntegers(rawDefinition.getIterationInterval()));
				rawDefinition.setTypes(splitToStrings(rawDefinition.getType()));
				rawDefinition.setMappings(splitToStrings(rawDefinition.getMapping()));
			}
			catch(Exception e) {
				throw new ValidationException("Error unwrapping {...}-sections: "+e.getClass()+": "+e.getMessage());
			}
			if( rawDefinition.getIterationIntervals().size()!=rawDefinition.getTypes().size() || 
					rawDefinition.getIterationIntervals().size()!=rawDefinition.getMappings().size())
					throw new ValidationException("Error unwrapping {...}-sections, number of sections must match");
		}
		else {
			ArrayList<Integer> iterationIntervals = new ArrayList<Integer>();
			iterationIntervals.add(new Double(rawDefinition.getIterationInterval()).intValue());
			rawDefinition.setIterationIntervals(iterationIntervals);
			ArrayList<String> types = new ArrayList<String>();
			types.add(rawDefinition.getType());
			rawDefinition.setTypes(types);
			ArrayList<String> mappings = new ArrayList<String>();
			mappings.add(rawDefinition.getMapping());
			rawDefinition.setMappings(mappings);
		}
	}
	
	private static ArrayList<Integer> splitToIntegers(String s) {
		ArrayList<Integer> is = new ArrayList<Integer>();
		for(String t: splitToStrings(s)) 
			is.add(Integer.parseInt(t.trim()));
		return is;
	}

	private static ArrayList<String> splitToStrings(String s) {
		ArrayList<String> ss = new ArrayList<String>(Arrays.asList(s.split("\\{")));
		if(ss.get(0).length()==0)
			ss.remove(0);
		for(int i=0; i<ss.size(); i++) {
			String ts = ss.get(i);
			ts = ts.substring(0, ts.length()-1);
			ss.set(i, ts);
		}
		return ss;
	}

}
