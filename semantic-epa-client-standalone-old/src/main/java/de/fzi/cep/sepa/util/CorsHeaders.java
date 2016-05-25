package de.fzi.cep.sepa.util;

import java.util.concurrent.ConcurrentMap;

import org.restlet.Message;
import org.restlet.Response;
import org.restlet.data.Header;
import org.restlet.util.Series;

public class CorsHeaders {

	private static final String HEADERS_KEY = "org.restlet.http.headers";
	
	public void make(Response response)
	{
		getMessageHeaders(response).add("Access-Control-Allow-Origin", "*"); 
		getMessageHeaders(response).add("Access-Control-Allow-Methods", "POST,OPTIONS,GET");
		getMessageHeaders(response).add("Access-Control-Allow-Headers", "Content-Type"); 
		getMessageHeaders(response).add("Access-Control-Allow-Credentials", "false"); 
		getMessageHeaders(response).add("Access-Control-Max-Age", "60"); 
	}
	
	@SuppressWarnings("unchecked")
	private Series<Header> getMessageHeaders(Message message) {

		ConcurrentMap<String, Object> attrs = message.getAttributes();

		Series<Header> headers = (Series<Header>) attrs.get(HEADERS_KEY);
		if (headers == null) {
			headers = new Series<Header>(Header.class);
			Series<Header> prev = (Series<Header>) attrs.putIfAbsent(
					HEADERS_KEY, headers);
			if (prev != null) {
				headers = prev;
			}
		}
		return headers;
	}
}
