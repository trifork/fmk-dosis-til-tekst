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
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import dk.medicinkortet.dosisstructuretext.vowrapper.DateOrDateTimeWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DayWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.DoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.EveningDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.MorningDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.NightDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.NoonDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.PlainDoseWrapper;
import dk.medicinkortet.dosisstructuretext.vowrapper.TimedDoseWrapper;

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
		
		b.registerTypeAdapter(DateOrDateTimeWrapper.class, new JsonSerializer<DateOrDateTimeWrapper>() {
			@Override
			public JsonElement serialize(DateOrDateTimeWrapper dateOrDateTime, Type type, JsonSerializationContext context) {
				JsonObject dateOrDateTimeObject = new JsonObject();
				
				if(dateOrDateTime.getDate() != null) {
					dateOrDateTimeObject.addProperty("date", new SimpleDateFormat("yyyy-MM-dd").format(dateOrDateTime.getDate())); // "startDateOrDateTime":{"date":"2014-02-07"}
				}
				else {
					dateOrDateTimeObject.addProperty("dateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dateOrDateTime.getDateTime())); // "startDateOrDateTime":{"dateTime":"2014-02-07"}
				}
				
				return dateOrDateTimeObject;
			}
		});

		// Since the default json serialization of Morning/Noon/Evening/NIght dose wrappers dosesn't include their type, we need to add it during serialization
		b.registerTypeAdapter(DoseWrapper.class, new JsonSerializer<DoseWrapper>() {
			@Override
			public JsonElement serialize(DoseWrapper dose, Type type, JsonSerializationContext context) {
				JsonObject json = (JsonObject)context.serialize(dose, dose.getClass());
				if(dose instanceof MorningDoseWrapper) {
					json.addProperty("type", "MorningDoseWrapper");
				}
				else if(dose instanceof NoonDoseWrapper) {
					json.addProperty("type", "NoonDoseWrapper");
				}
				else if(dose instanceof EveningDoseWrapper) {
					json.addProperty("type", "EveningDoseWrapper");
				}
				else if(dose instanceof NightDoseWrapper) {
					json.addProperty("type", "NightDoseWrapper");
				}
				else if(dose instanceof PlainDoseWrapper) {
					json.addProperty("type", "PlainDoseWrapper");
				}
				else if(dose instanceof TimedDoseWrapper) {
					json.addProperty("type", "TimedDoseWrapper");
				}
				return json;
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
