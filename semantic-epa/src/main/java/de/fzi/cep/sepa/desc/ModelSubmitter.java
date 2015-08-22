package de.fzi.cep.sepa.desc;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.protocol.Protocol;
import org.restlet.Restlet;

import de.fzi.cep.sepa.commons.config.Configuration;
import de.fzi.cep.sepa.desc.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.desc.declarer.SemanticEventConsumerDeclarer;
import de.fzi.cep.sepa.desc.declarer.SemanticEventProcessingAgentDeclarer;
import de.fzi.cep.sepa.desc.declarer.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.endpoint.RestletConfig;
import de.fzi.cep.sepa.endpoint.SecRestlet;
import de.fzi.cep.sepa.endpoint.SepRestlet;
import de.fzi.cep.sepa.endpoint.SepaRestlet;
import de.fzi.cep.sepa.endpoint.Server;
import de.fzi.cep.sepa.html.EventConsumerWelcomePage;
import de.fzi.cep.sepa.html.EventProcessingAgentWelcomePage;
import de.fzi.cep.sepa.html.EventProducerWelcomePage;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;

public class ModelSubmitter {
	
	private static List<RestletConfig> config = config();
	
	public static boolean submitProducer(
			List<SemanticEventProducerDeclarer> producers) throws Exception {
		
		String baseUri = getBaseUri(Configuration.getInstance().SOURCES_PORT);
		addConfig("", new EventProducerWelcomePage(baseUri, producers));

		for (SemanticEventProducerDeclarer producer : producers) {
			SepDescription sep = producer.declareModel();
			String currentPath = sep.getUri();
			sep.setUri(baseUri + currentPath);

			for (EventStreamDeclarer declarer : producer.getEventStreams()) {
				EventStream stream = declarer.declareModel(sep);
				sep.addEventStream(stream);
				if (declarer.isExecutable())
					declarer.executeStream();
			}
			addConfig(currentPath, new SepRestlet(sep));
		}

		return start(Configuration.getInstance().SOURCES_PORT);
	}

	public static boolean submitAgent(
			List<SemanticEventProcessingAgentDeclarer> declarers) throws Exception {
		String baseUri = getUrl() +Configuration.getInstance().ESPER_PORT;
		addConfig("", new EventProcessingAgentWelcomePage(baseUri, declarers));

		for (SemanticEventProcessingAgentDeclarer declarer : declarers) {
			SepaDescription sepa = declarer.declareModel();
			sepa.setUri(baseUri + sepa.getPathName());
			addConfig(sepa.getPathName(), new SepaRestlet(sepa, declarer));
		}

		return start(Configuration.getInstance().ESPER_PORT);
	}

	public static boolean submitConsumer(
			List<SemanticEventConsumerDeclarer> declarers) throws Exception {
		String baseUri = getBaseUri(Configuration.getInstance().ACTION_PORT);
		addConfig("", new EventConsumerWelcomePage(baseUri, declarers));

		for (SemanticEventConsumerDeclarer declarer : declarers) {

			SecDescription sec = declarer.declareModel();
			String pathName = sec.getUri();
			sec.setUri(baseUri + sec.getUri());
			addConfig(pathName, new SecRestlet(sec, declarer));
		}

		return start(Configuration.getInstance().ACTION_PORT);
	}
	
	private static String getUrl() {
		InetAddress addr;
		try {
			addr = InetAddress.getLocalHost();
			return Protocol.getProtocol("http").getScheme()  + "://" +addr.getCanonicalHostName() +":";
		} catch (UnknownHostException e) {
			return "http://localhost:";
		}	
	}
	
	private static String getBaseUri(int port)
	{
		return getUrl() +port;
	}
	
	private static List<RestletConfig> config()
	{
		return new ArrayList<>();
	}
	
	private static void addConfig(String baseUri, Restlet restlet)
	{
		config.add(new RestletConfig(baseUri, restlet));
	}
	
	private static boolean start(int port)
	{
		return Server.INSTANCE.create(port, config);
	}

}
