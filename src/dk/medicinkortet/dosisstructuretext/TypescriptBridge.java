package dk.medicinkortet.dosisstructuretext;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import dk.medicinkortet.dosisstructuretext.vowrapper.DosageWrapper;

public class TypescriptBridge {

	public class ConsoleObject {
		public void log(String level, String msg) {
			System.out.println(msg);
		}
	}
	
	private static ScriptEngine engine = null;

	private static ScriptEngine getEngineInstance() {
		if (engine == null) {

			engine = new ScriptEngineManager().getEngineByName("nashorn");

			InputStream d2sResource = LongTextConverter.class.getClassLoader().getResourceAsStream("dosistiltekst.js");
			if (d2sResource == null) {
				// Development-environment, read from target, or directly from project

				FileReader reader = null;
				try {
					reader = new FileReader("../fmk-dosis-til-tekst-ts/target/dosistiltekst.js");
				} catch (FileNotFoundException e) {
				}

				if (reader == null) {
					try {
						reader = new FileReader("node_modules/fmk-dosis-til-tekst-ts/target/dosistiltekst.js");
					} catch (FileNotFoundException e) {

					}
				}

				if (reader == null) {
					throw new RuntimeException("dosistiltekst.js not found");
				}

				Bindings bindings = new SimpleBindings();
				bindings.put("console", new TypescriptBridge().new ConsoleObject());

				try {
					engine.setBindings(bindings, ScriptContext.GLOBAL_SCOPE);
					engine.eval(reader);

				} catch (ScriptException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}

		}

		return engine;
	}
	
	public static String convertLongText(DosageWrapper dosage) {
		String json = JSONHelper.toJsonString(dosage);
		
		Object res;
		try {
			res = getEngineInstance().eval("dosistiltekst.Factory.getLongTextConverter().convert(" + json + ")");
		} catch (ScriptException e) {
			e.printStackTrace();
			throw new RuntimeException("ScriptException in LongTextConverter.convert()", e);
		}
		
		return (String)res;
	}
	
	public static String convertShortText(DosageWrapper dosage) {
		String json = JSONHelper.toJsonString(dosage);
		
		Object res;
		try {
			res = getEngineInstance().eval("dosistiltekst.Factory.getShortTextConverter().convert(" + json + ")");
		} catch (ScriptException e) {
			e.printStackTrace();
			throw new RuntimeException("ScriptException in ShortTextConverter.convert()", e);
		}
		
		return (String)res;
	}

	public static String getShortTextConverterClassName(DosageWrapper dosage) {
		String json = JSONHelper.toJsonString(dosage);
		
		Object res;
		try {
			res = getEngineInstance().eval("dosistiltekst.Factory.getShortTextConverter().getConverterClassName(" + json + ")");
		} catch (ScriptException e) {
			e.printStackTrace();
			throw new RuntimeException("ScriptException in ShortTextConverter.getConverterClassName()", e);
		}
		
		return (String)res;
	}
}
