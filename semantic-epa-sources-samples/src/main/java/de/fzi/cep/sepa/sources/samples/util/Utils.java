package de.fzi.cep.sepa.sources.samples.util;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.model.impl.Domain;
import de.fzi.cep.sepa.sources.samples.config.AkerVariables;
import de.fzi.cep.sepa.sources.samples.config.SourcesConfig;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.json.JSONArray;
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

        /*String[] vars = new String[sourceID.length];
        for (int i = 0; i < sourceID.length; i++) {
            vars[i] = String.valueOf(sourceID[i]);
        }*/
        JSONObject json = new JSONObject();
        json.put("startTime", startTime);
        json.put("endTime", endTime);
        //json.put("variables", vars);
        json.put("topic", topicName);
        json.put("partner", "aker");

       System.out.println("Sending request: \n" + json.toString());

        String testJson = "{\n" +
                "  \t\"startTime\": \"2013-11-19T11:00:00+0100\", \n" +
                "\"endTime\" : \"2013-11-19T14:15:00+0100\" , \t\t\t\n" +
                "\"variables\" : [\"1000692\"], \n" +
                "\"topic\":\"some_new_topic\", \n" +
                "\"partner\":\"aker\"\n" +
                "}";


        try {
            return  Request.Post(SourcesConfig.eventReplayURI + "/EventPlayer/api/playback/")
                    .bodyString(testJson, ContentType.APPLICATION_JSON)
                    .execute().returnResponse().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}
