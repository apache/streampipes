package de.fzi.cep.sepa.sources.samples.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.json.JSONObject;

import de.fzi.cep.sepa.model.impl.Domain;

public class Utils {
	
	public static final String QUOTATIONMARK = "\"";
	public static final String COMMA = ",";
	public static final String COLON = ":";
	
	public static List<String> createDomain(Domain...domains)
	{
		ArrayList<String> domainList = new ArrayList<String>();
		for(Domain d : domains)
			domainList.add(d.toString());
			
		return domainList;
	}

    /**
     * Performs a request to
     * @param sourceID tagNumber
     * @param topicName
     * @param startTime Start time in ISO-8601
     * @param endTime End time in ISO-8601
     */
    public static String performRequest(long[] sourceID, String topicName, String startTime, String endTime) {

    	
        String[] vars = new String[sourceID.length];
        for (int i = 0; i < sourceID.length; i++) {
            vars[i] = String.valueOf(sourceID[i]);
        }
        JSONObject json = new JSONObject();
        //json.put("startTime", parseDate(startTime));
        //json.put("endTime", parseDate(endTime));
        json.put("startTime", "2013-11-19T11:00:00+0100");
        json.put("endTime", "2013-11-19T14:15:00+0100");
        json.put("variables", vars);
        json.put("topic", topicName);
        json.put("partner", "aker");

//        try {
//	            HttpResponse response = Request.Post(SourcesConfig.eventReplayURI + "/EventPlayer/api/playback/")
//	                    .bodyString(json.toString(), ContentType.APPLICATION_JSON)
//	                    .execute().returnResponse();
//	            return response.toString();        	
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return null;

    }
    
    public static String fetchJson(String url) throws ClientProtocolException, IOException
    {
    	Executor executor = Executor.newInstance()
    	        .auth(new HttpHost("kalmar29.fzi.de"), "testManager", "1234");

    	String content = executor.execute(Request.Get(url))
    	        .returnContent().asString();
		return content;
    }
    
    
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
