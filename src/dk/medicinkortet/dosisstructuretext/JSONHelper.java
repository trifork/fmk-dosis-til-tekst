package dk.medicinkortet.dosisstructuretext;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class JSONHelper {
	
	public static void write(Object o, File file) throws IOException {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			getGson().toJson(o, new IndentingWriter(writer));
			System.out.println("JsonHelper.write: Created file "+file.getAbsolutePath());
		}
		finally {
			try {
				writer.close();
			}
			catch(Exception e) {
				// ignored 
			}
		}
	}	
		
	public static <T> T read(File file, Class<T> klass) throws IOException {
		BufferedReader r = null;
		if(!file.exists())
			throw new IOException("No file "+file.getAbsolutePath());
		try {
			r = new BufferedReader(new FileReader(file));
			return getGson().fromJson(r, klass);
		}
		finally {
			try {
				r.close();
			}
			catch(Exception e) {
				// ignore //
			}
		}
	}
	
	public static Gson getGson() {
		GsonBuilder b = new GsonBuilder();
		b.disableHtmlEscaping();
		b.registerTypeAdapter(Date.class, new JsonSerializer<Date>() {
			@Override
			public JsonElement serialize(Date date, Type type, JsonSerializationContext context) {
				return new JsonPrimitive(new SimpleDateFormat("yyyy-MM-dd").format(date));
			}
		});
		b.registerTypeAdapter(java.sql.Date.class, new JsonSerializer<java.sql.Date>() {
			@Override
			public JsonElement serialize(java.sql.Date date, Type type, JsonSerializationContext context) {
				return new JsonPrimitive(new SimpleDateFormat("yyyy-MM-dd").format(date));
			}
		});		
		return b.create();
	}
	
	public static String toJsonString(Object object) {		
		return getGson().toJson(object);	
	}
	
	public static void toJsonFile(Object object, File file) throws IOException {
		BufferedWriter w = null;		
		try {
			w = new BufferedWriter(new FileWriter(file));		
			getGson().toJson(object, w);
		}
		finally {
			try {
				w.close();
			}
			catch(IOException e) {
				/* ignored */
			}
		}
	}

}
