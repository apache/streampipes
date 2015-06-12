package de.fzi.cep.sepa.sources.samples.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.fzi.cep.sepa.model.impl.Domain;
import de.fzi.cep.sepa.sources.samples.config.SourcesConfig;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.json.JSONObject;

public class Utils {
	
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

        System.out.println("Subscription: " +json.toString());
        
        String testJson = "{\n" +
                "  \t\"startTime\": \"2013-11-19T11:00:00+0100\", \n" +
                "\"endTime\" : \"2013-11-19T14:15:00+0100\" , \t\t\t\n" +
                "\"variables\" : [\"1000692\"], \n" +
                "\"topic\":\"some_new_topic\", \n" +
                "\"partner\":\"aker\"\n" +
                "}";

        try {
           // HttpResponse response = Request.Post(SourcesConfig.eventReplayURI + "/EventPlayer/api/playback/")
           //         .bodyString(json.toString(), ContentType.APPLICATION_JSON)
           //         .execute().returnResponse();
            //return response.toString();
        	
        } catch (Exception e) {
            e.printStackTrace();
        }
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
}
