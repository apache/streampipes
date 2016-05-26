package de.fzi.cep.sepa.desc;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.fzi.cep.sepa.client.declarer.*;
import org.apache.commons.httpclient.protocol.Protocol;
import org.restlet.Restlet;

import com.clarkparsia.empire.SupportsRdfId;

import de.fzi.cep.sepa.commons.config.ClientConfiguration;
import de.fzi.cep.sepa.endpoint.RestletConfig;
import de.fzi.cep.sepa.endpoint.SecRestlet;
import de.fzi.cep.sepa.endpoint.SepRestlet;
import de.fzi.cep.sepa.endpoint.SepaRestlet;
import de.fzi.cep.sepa.html.WelcomePage;
import de.fzi.cep.sepa.html.model.Description;
import de.fzi.cep.sepa.html.page.EventConsumerWelcomePage;
import de.fzi.cep.sepa.html.page.EventProcessingAgentWelcomePage;
import de.fzi.cep.sepa.html.page.EventProducerWelcomePage;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;

public class RestletGenerator {

	private List<RestletConfig> restletConfigurations;
	private List<Description> welcomePageDescriptions;
	private int port;
	private String contextPath;
	private boolean standalone;
	
	public final static String REGEX = "[a-zA-Z]{4}://[a-zA-Z0-9\\-\\.]+:\\d+/";
	
	public RestletGenerator(int port, String contextPath, boolean standalone) {
		this.restletConfigurations = new ArrayList<>();
		this.welcomePageDescriptions = new ArrayList<>();
		this.port = port;
		this.contextPath = contextPath;
		this.standalone = standalone;
	}
	
	public RestletGenerator(int port) {
		this.restletConfigurations = new ArrayList<>();
		this.welcomePageDescriptions = new ArrayList<>();
		this.port = port;
		this.contextPath = "";
		this.standalone = true;
	}
	
	public RestletGenerator addRestlets(List<InvocableDeclarer<?, ?>> declarers) {
		addSepaRestlets(declarers
			.stream()
			.filter(d -> d instanceof SemanticEventProcessingAgentDeclarer)
			.map(d -> ((SemanticEventProcessingAgentDeclarer) d)).collect(Collectors.toList()));
		
		addSecRestlets(declarers
				.stream()
				.filter(d -> d instanceof SemanticEventConsumerDeclarer)
				.map(d -> ((SemanticEventConsumerDeclarer) d)).collect(Collectors.toList()));
		
		addSepRestlets(declarers
				.stream()
				.filter(d -> d instanceof SemanticEventProducerDeclarer)
				.map(d -> ((SemanticEventProducerDeclarer) d)).collect(Collectors.toList()));
		
		return this;
	}
	
	public RestletGenerator addSepaRestlets(List<SemanticEventProcessingAgentDeclarer> declarers) {
		String baseUri = getUrl() +port +contextPath;
		welcomePageDescriptions.addAll(new EventProcessingAgentWelcomePage(baseUri +"/", declarers).buildUris());

		for (SemanticEventProcessingAgentDeclarer declarer : declarers) {
			SepaDescription sepa = new SepaDescription(declarer.declareModel());
			if (sepa.getPathName() == null) sepa.setPathName(sepa.getUri().replaceFirst(REGEX, ""));
			//sepa.setIconUrl(baseUri +"/" +sepa.getIconUrl().replaceFirst("[a-zA-Z]{4}://[a-zA-Z\\.]+:\\d+/", ""));
			if (standalone) 
				{
					sepa.setPathName("/" +sepa.getPathName());
					sepa.setUri(baseUri +sepa.getPathName());		
					sepa.setRdfId(new SupportsRdfId.URIKey(URI.create(sepa.getUri())));
				}
			else 
				{
					sepa.setUri(baseUri +"/" +sepa.getPathName());
					sepa.setRdfId(new SupportsRdfId.URIKey(URI.create(sepa.getUri())));
				}
			addConfig(sepa.getPathName(), new SepaRestlet(sepa, declarer));
		}
		return this;
	}
		
	public RestletGenerator addSepRestlets(List<SemanticEventProducerDeclarer> declarers) {
		String baseUri = getBaseUri(port) +contextPath;
		welcomePageDescriptions.addAll(new EventProducerWelcomePage(baseUri +"/", declarers).buildUris());

		for (SemanticEventProducerDeclarer producer : declarers) {
			SepDescription sep = producer.declareModel();
			String currentPath; 
			
			if (standalone) 
			{
				currentPath = "/" +sep.getUri();
			}
			else
			{
				currentPath = sep.getUri();
			}
			sep.setUri(baseUri +"/" +currentPath);
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
	
	public RestletGenerator addSecRestlets(List<SemanticEventConsumerDeclarer> declarers) {	
		String baseUri = getUrl() +port +contextPath;
		welcomePageDescriptions.addAll(new EventConsumerWelcomePage(baseUri +"/", declarers).buildUris());

		for (SemanticEventConsumerDeclarer declarer : declarers) {

			SecDescription sec = new SecDescription(declarer.declareModel());
			if (sec.getUri().startsWith("http")) sec.setUri(sec.getUri().replaceFirst(REGEX, ""));
			
			String pathName = sec.getUri();
			if (standalone) 
			{
				pathName = "/" +sec.getUri();
				sec.setUri(baseUri +pathName);
				sec.setRdfId(new SupportsRdfId.URIKey(URI.create(sec.getUri())));
			}
			else
			{
				pathName = sec.getUri();
				sec.setUri(baseUri +"/" +pathName);
				sec.setRdfId(new SupportsRdfId.URIKey(URI.create(sec.getUri())));
			}
			
			addConfig(pathName, new SecRestlet(sec, declarer));
		}

		return this;
	}
	
	public List<RestletConfig> getRestletConfigurations(boolean prependPath)
	{
		String pathPrefix = prependPath ? "/" : "";
		addConfig(pathPrefix, new WelcomePage(welcomePageDescriptions));
		return restletConfigurations;
	}
	
	private String getUrl() {
		InetAddress addr;
		try {
			addr = InetAddress.getLocalHost();
			if (!addr.getCanonicalHostName().equals("localhost")) return Protocol.getProtocol("http").getScheme()  + "://" +addr.getCanonicalHostName() +":";
			else return Protocol.getProtocol("http").getScheme()  + "://" +ClientConfiguration.INSTANCE.getWebappHost() +":";
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
	
	public static void main(String[] args)
	{
		String url = "http://ipe-koi04.fzi.de:8090/sepa/numericalfilter";
		String replaced = url.replaceFirst("[a-zA-Z]{4}://[a-zA-Z0-9\\-\\.]+:\\d+/", "");
		System.out.println(replaced);
	}
}
