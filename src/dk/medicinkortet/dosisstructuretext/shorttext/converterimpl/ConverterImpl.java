package dk.medicinkortet.dosisstructuretext.shorttext.converterimpl;

import dk.medicinkortet.dosisstructuretext.DosisStructureText;
import dk.medicinkortet.dosisstructuretext.simpelxml.Node;
import dk.medicinkortet.dosisstructuretext.simpelxml.Nodes;
import dk.medicinkortet.dosisstructuretext.simpelxml.parser.XPathException;

/**
 * Basic converter functions. The converter implementations must extend this class. 
 */
public abstract class ConverterImpl {
	
	private StringBuffer shortText = null;
		
	/**
	 * Try to perform conversion on the passed DosageTimesStructure node
	 * @param dosageTimesStructure
	 * @param result updated with the short text and the name of this filter if this converter has handled the conversion
	 * @return true if this converter has handled the conversion
	 * @throws XPathException
	 */
	public boolean convert(Node dosageTimesStructure, DosisStructureText result) throws XPathException {
		if(!doTest(dosageTimesStructure))
			return false;
		shortText = new StringBuffer();
		doConvert(dosageTimesStructure);
		result.setShortText(getShortText());
		result.setShortTextFilter(getClass().getSimpleName());
		return true;
	}
	
	/**
	 * Override this method to test if the converter can do the conversion of the passed node
	 * @param dosageTimesStructure
	 * @throws XPathException
	 */
	protected abstract boolean doTest(Node dosageTimesStructure) throws XPathException;

	/**
	 * Override this method to do the conversion, the test is always run first
	 * @param dosageTimesStructure
	 * @throws XPathException
	 */
	protected abstract void doConvert(Node dosageTimesStructure) throws XPathException;

	/**
	 * Append to the short text
	 * @param s
	 */
	protected void append(String s) {
		shortText.append(s);
	}

	/**
	 * Append to the short text
	 * @param i
	 */
	protected void append(int i) {
		shortText.append(i);
	}
	
	/**
	 * Returns the short text for this conversion
	 */
	protected String getShortText() {
		return shortText.toString();
	}

	/**
	 * Helper method for converters: Determines if the passed object is a sequence of integers.
	 * @param o
	 * @return true if the passed object is a sequence of integers
	 */
	protected boolean isSequence(Object o) {
		if(o==null)
			return false;
		else if(o instanceof Integer)
			return true;
		else if(o instanceof Integer[]) {
			Integer[] sequence = (Integer[])o;
			int prev = sequence[0].intValue();
			for(int i=1; i<sequence.length; i++) {
				int ths = sequence[i].intValue();
				if(ths!=prev+1)
					return false;
				prev = ths;
			}
			return true;
		}
		else
			throw new RuntimeException("Cannot evaluate isSequence for "+o.getClass().getName());		
	}
	
	/**
	 * Helper method for converters: Determines if all the passed nodes in the object are equal. 
	 * If the object is a Nodes object the content is evaluated, if the object is a Node object   
	 * the result is always true.
	 * @param o
	 */
	protected boolean allEquals(Object o) {
		Nodes n = new Nodes();
		add(o, n);
		return n.allEquals();
	}

	/**
	 * Helper method for converters: Determines if all the passed nodes in the objects are equal. 
	 * The Node or Nodes in the arguments are added to one Nodes list and evaluated for equals.   
	 * @param o0
	 * @param o1
	 */
	protected boolean allEquals(Object o0, Object o1) {
		Nodes n = new Nodes();
		add(o0, n);
		add(o1, n);
		return n.allEquals();
	}

	/**
	 * Helper method for converters: Determines if all the passed nodes in the objects are equal. 
	 * The Node or Nodes in the arguments are added to one Nodes list and evaluated for equals.   
	 * @param o0
	 * @param o1
	 * @param o2
	 * @param o3
	 */
	protected boolean allEquals(Object o0, Object o1, Object o2, Object o3) {
		Nodes n = new Nodes();
		add(o0, n);
		add(o1, n);
		add(o2, n);
		add(o3, n);
		return n.allEquals();
	}
	
	/**
	 * If o is a Node it is added to the Nodes object n.
	 * If o is a Nodes it's content is added to the Nodes object n.
	 * Otherwise a RuntimeException is thrown
	 * @param result
	 * @param n
	 */
	private void add(Object o, Nodes n) {
		if(o==null)
			return;
		if(o instanceof Node)
			n.addNode((Node)o);
		else if(o instanceof Nodes)
			n.addNodes((Nodes)o);
		else 
			throw new RuntimeException("Cannot evaluate result of class "+o.getClass().getName());		
	}
	
}
