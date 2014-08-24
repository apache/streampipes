package de.fzi.cep.sepa.rest.http;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpJsonParser {

	public static String getContentFromUrl(String uri) throws URISyntaxException, ClientProtocolException, IOException
	{
		HttpGet request = new HttpGet(new URI(uri));
		
		HttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(request);
		
		String pageContent = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
		return pageContent;
		
	}
}
