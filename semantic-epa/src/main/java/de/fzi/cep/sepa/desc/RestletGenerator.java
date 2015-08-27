package de.fzi.cep.sepa.desc;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.protocol.Protocol;
import org.restlet.Restlet;

import de.fzi.cep.sepa.desc.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.desc.declarer.SemanticEventConsumerDeclarer;
import de.fzi.cep.sepa.desc.declarer.SemanticEventProcessingAgentDeclarer;
import de.fzi.cep.sepa.desc.declarer.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.endpoint.RestletConfig;
import de.fzi.cep.sepa.endpoint.SecRestlet;
import de.fzi.cep.sepa.endpoint.SepRestlet;
import de.fzi.cep.sepa.endpoint.SepaRestlet;
import de.fzi.cep.sepa.html.EventConsumerWelcomePage;
import de.fzi.cep.sepa.html.EventProcessingAgentWelcomePage;
import de.fzi.cep.sepa.html.EventProducerWelcomePage;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;

public class RestletGenerator {

	private List<RestletConfig> restletConfigurations;
	private int port;
	
	public RestletGenerator(int port) {
		this.restletConfigurations = new ArrayList<>();
		this.port = port;
	}
	
	public RestletGenerator addSepaRestlets(List<SemanticEventProcessingAgentDeclarer> declarers, String contextPath, boolean embedded) {
		String baseUri = getUrl() +port +contextPath;
		addConfig("", new EventProcessingAgentWelcomePage(baseUri +"/", declarers));

		for (SemanticEventProcessingAgentDeclarer declarer : declarers) {
			SepaDescription sepa = declarer.declareModel();
			if (!embedded) 
				{
					sepa.setPathName("/" +sepa.getPathName());
					sepa.setUri(baseUri +sepa.getPathName());
				}
			else sepa.setUri(baseUri +"/" +sepa.getPathName());
			addConfig(sepa.getPathName(), new SepaRestlet(sepa, declarer));
		}
		return this;
	}
	
	public RestletGenerator addSepaRestlets(List<SemanticEventProcessingAgentDeclarer> declarers, boolean embedded) {
		return addSepaRestlets(declarers, "", embedded);
	}
	
	public RestletGenerator addSepRestlets(List<SemanticEventProducerDeclarer> declarers, String contextPath, boolean embedded) {
		String baseUri = getBaseUri(port) +contextPath;
		addConfig("", new EventProducerWelcomePage(baseUri +"/", declarers));

		for (SemanticEventProducerDeclarer producer : declarers) {
			SepDescription sep = producer.declareModel();
			String currentPath;
			
			if (!embedded) 
			{
				currentPath = "/" +sep.getUri();
			}
			else
			{
				currentPath = sep.getUri();
			}
			sep.setUri(baseUri +currentPath);
			for (EventStreamDeclarer declarer : producer.getEventStreams()) {
				EventStream stream = declarer.declareModel(sep);
				sep.addEventStream(stream);
				if (declarer.isExecutable())
					declarer.executeStream();
			}
			addConfig(currentPath, new SepRestlet(sep));
		}
		
		return this;
	}
	
	public RestletGenerator addSepRestlets(List<SemanticEventProducerDeclarer> declarers, boolean embedded) {
		return addSepRestlets(declarers, "", embedded);		
	}
	
	public RestletGenerator addSecRestlets(List<SemanticEventConsumerDeclarer> declarers, String contextPath) {	
		String baseUri = getBaseUri(port);
		addConfig("", new EventConsumerWelcomePage(baseUri, declarers));

		for (SemanticEventConsumerDeclarer declarer : declarers) {

			SecDescription sec = declarer.declareModel();
			String pathName = sec.getUri();
			sec.setUri(baseUri + sec.getUri());
			addConfig(pathName, new SecRestlet(sec, declarer));
		}

		return this;
	}
	
	
	public RestletGenerator addSecRestlets(List<SemanticEventConsumerDeclarer> declarers) {	
		return addSecRestlets(declarers, "");
	}
	
	public List<RestletConfig> getRestletConfigurations()
	{
		return restletConfigurations;
	}
	
	private String getUrl() {
		InetAddress addr;
		try {
			addr = InetAddress.getLocalHost();
			return Protocol.getProtocol("http").getScheme()  + "://" +addr.getCanonicalHostName() +":";
		} catch (UnknownHostException e) {
			return "http://localhost:";
		}	
	}
	
	private String getBaseUri(int port)
	{
		return getUrl() +port;
	}
	
	private void addConfig(String baseUri, Restlet restlet)
	{
		restletConfigurations.add(new RestletConfig(baseUri, restlet));
	}
}
