package de.fzi.cep.sepa.manager.monitoring.job;

import java.io.IOException;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;

public class MonitoringUtils {

	public static String randomKey()
	{
		return RandomStringUtils.randomAlphabetic(5);
	}
	
	public static HttpResponse getHttpResponse(String url) throws ClientProtocolException, IOException
	{
		return Request.Get(url).execute().returnResponse();
	}
}
