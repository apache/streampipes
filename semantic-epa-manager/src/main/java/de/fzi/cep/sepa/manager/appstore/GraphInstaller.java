package de.fzi.cep.sepa.manager.appstore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;

import de.fzi.cep.sepa.commons.exceptions.SepaParseException;
import de.fzi.cep.sepa.manager.operations.Operations;
import de.fzi.cep.sepa.messages.Message;
import de.fzi.cep.sepa.messages.Notifications;

public class GraphInstaller {

	private List<String> elementUris;
	private String username;
	
	public GraphInstaller(List<String> elementUris, String username) {;
		this.elementUris = elementUris;
		this.username = username;
	}
	
	public List<Message> install() {
		List<Message> result = new ArrayList<>();
		elementUris.forEach(e -> result.add(installElement(e)));
		return result;
	}
	
	private Message installElement(String elementUri) {
		
		String jsonLd;
		try {
			jsonLd = getGraphData(elementUri);
			return Operations.verifyAndAddElement(jsonLd, username, true);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return Notifications.error(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			return Notifications.error(e.getMessage());
		} catch (SepaParseException e) {
			e.printStackTrace();
			return Notifications.error(e.getMessage());
		}
		
	}
	
	private String getGraphData(String elementUri) throws ClientProtocolException, IOException {
		return Request
				.Get(elementUri)
				.addHeader("Accept", "application/json")
				.execute()
				.returnContent()
				.asString();
	}
}
