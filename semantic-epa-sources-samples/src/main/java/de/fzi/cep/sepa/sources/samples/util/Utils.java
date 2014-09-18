package de.fzi.cep.sepa.sources.samples.util;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.model.impl.Domain;
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
    public static String performRequest(long sourceID, String topicName, String startTime, String endTime) {

        JSONObject json = new JSONObject();
        json.append("startTime", startTime);
        json.append("endTime", endTime);
        json.append("variables", sourceID);
        json.append("topic", topicName);
        json.append("partner", "aker");

        try {
            Content cont = Request.Post(SourcesConfig.eventReplayURI + "/EventPlayer/api/playback/")
                    .bodyString(json.toString(), ContentType.APPLICATION_JSON)
                    .execute().returnContent();
            return cont.asString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}
