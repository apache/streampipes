package de.fzi.cep.sepa.actions.samples.proasense.kpi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.fzi.cep.sepa.actions.config.ActionConfig;
import de.fzi.cep.sepa.actions.messaging.kafka.KafkaConsumerGroup;
import de.fzi.cep.sepa.actions.samples.proasense.ProaSenseEventNotifier;
import de.fzi.cep.sepa.actions.samples.proasense.ProaSenseTopologyViewer;
import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.commons.config.BrokerConfig;
import de.fzi.cep.sepa.commons.config.Configuration;
import de.fzi.cep.sepa.desc.declarer.SemanticEventConsumerDeclarer;
import de.fzi.cep.sepa.model.impl.Domain;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.staticproperty.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.impl.KafkaTransportProtocol;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;
import de.fzi.cep.sepa.model.impl.TransportFormat;
import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;
import de.fzi.cep.sepa.model.vocabulary.MessageFormat;

public class ProaSenseKpiController implements SemanticEventConsumerDeclarer {

	private ProaSenseEventNotifier notifier;
	
	@Override
	public SecDescription declareModel() {
		
		List<String> domains = new ArrayList<String>();
		domains.add(Domain.DOMAIN_PERSONAL_ASSISTANT.toString());
		domains.add(Domain.DOMAIN_PROASENSE.toString());
		
		EventStream stream1 = new EventStream();
		EventSchema schema1 = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		schema1.setEventProperties(eventProperties);
		stream1.setEventSchema(schema1);
		
		SecDescription desc = new SecDescription("proasensekpi", "ProaSense KPI", "Store as ProaSense KPI", "");
		
		stream1.setUri(ActionConfig.serverUrl +"/" +Utils.getRandomString());
		desc.addEventStream(stream1);
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		
		staticProperties.add(new FreeTextStaticProperty("kpi", "KPI name"));
		desc.setStaticProperties(staticProperties);
		
		EventGrounding grounding = new EventGrounding();
		BrokerConfig config = Configuration.getBrokerConfig();
		grounding.setTransportProtocol(new KafkaTransportProtocol(config.getKafkaHost(), config.getKafkaPort(), "", config.getZookeeperHost(), config.getZookeeperPort()));
		grounding.setTransportFormats(Arrays.asList(new TransportFormat(MessageFormat.Json)));
		desc.setSupportedGrounding(grounding);
		
		
		return desc;
	}

	@Override
	public Response invokeRuntime(SecInvocation sec) {
		String consumerTopic = sec.getInputStreams().get(0).getEventGrounding().getTransportProtocol().getTopicName();
		this.notifier = new ProaSenseEventNotifier(consumerTopic);
		KafkaConsumerGroup kafkaConsumerGroup = new KafkaConsumerGroup(Configuration.getBrokerConfig().getZookeeperUrl(), consumerTopic,
				new String[] {consumerTopic}, new ProaSenseKpiPublisher(sec, notifier));
		kafkaConsumerGroup.run(1);
		
		return null;
	}

	@Override
	public Response detachRuntime() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isVisualizable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getHtml(SecInvocation graph) {
		return new ProaSenseTopologyViewer(null, notifier).generateHtml();
	}
}
