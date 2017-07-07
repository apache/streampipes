package de.fzi.cep.sepa.sources.samples.adapter;


import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

public class Utils {
	
	public static final String QUOTATIONMARK = "\"";
	public static final String COMMA = ",";
	public static final String COLON = ":";

    
    public static String parseDate(String timestamp)
    {
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    	return sdf.format(new Date(Long.parseLong(timestamp)));
    }
    
    public static Optional<BufferedReader> getReader(File file) {
		try {
			return Optional.of(new BufferedReader(new FileReader(file)));
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
			return Optional.empty();
		}
	}
    
    public static String toJsonNumber(String key, Object value) {
		return new StringBuilder().append(QUOTATIONMARK).append(key).append(QUOTATIONMARK).append(COLON).append(value).append(COMMA).toString();
	}
    
    public static String toJsonString(String key, Object value) {
		return new StringBuilder().append(QUOTATIONMARK).append(key).append(QUOTATIONMARK).append(COLON).append(QUOTATIONMARK).append(value).append(QUOTATIONMARK).append(COMMA).toString();
	}
    
    public static String toJsonBoolean(String key, Object value) {
		return new StringBuilder().append(QUOTATIONMARK).append(key).append(QUOTATIONMARK).append(COLON).append(value).append(COMMA).toString();
	}
	
	public static String toJsonstr(String key, Object value, boolean last) {
		return new StringBuilder().append(QUOTATIONMARK).append(key).append(QUOTATIONMARK).append(COLON).append(value).toString();
	}
}
