package dk.medicinkortet.dosagetranslation.dumper;

import java.io.IOException;
import java.io.Writer;

public class IndentingWriter implements Appendable {

	private Writer w;

	private char p = ' ';
	private int indents = 0;
	
	public IndentingWriter(Writer w) {
		this.w = w;
	}
	
	public Appendable append(CharSequence c) throws IOException {
		return w.append(c);
	}

	public Appendable append(CharSequence c, int start, int end) throws IOException {
		return w.append(c, start, end);
	}

	public Appendable append(char c) throws IOException {
		if(c=='['||c=='{') {
			Appendable a = w.append(c).append("\n").append(indent(++indents));
			p = c;			
			return a;
		}
		else if(c==']'||c=='}') {
			Appendable a = append("\n").append(indent(--indents)).append(c);
			p = c;
			return a;
		}
		else if(c==','&&p!='\\') {
			Appendable a = w.append(c).append("\n").append(indent(indents));
			p = c;
			return a;
		}
		else {
			p = c;
			return w.append(c);
		}
	}
	
	public void close() throws IOException {
		w.close();
	}
	
	public CharSequence indent(int indents) {
		StringBuilder b = new StringBuilder();
		for(int i=0; i<indents*3; i++) {
			b.append(' ');
		}
		return b;
	}
		
}
