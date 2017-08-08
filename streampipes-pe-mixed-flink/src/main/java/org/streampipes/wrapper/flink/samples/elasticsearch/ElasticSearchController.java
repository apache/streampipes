package org.streampipes.wrapper.flink.samples.elasticsearch;

import org.streampipes.container.util.StandardTransportFormat;
import org.streampipes.commons.config.old.ClientConfiguration;
import org.streampipes.wrapper.flink.AbstractFlinkConsumerDeclarer;
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkSecRuntime;
import org.streampipes.wrapper.flink.samples.FlinkConfig;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.model.impl.ApplicationLink;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.graph.SecDescription;
import org.streampipes.model.impl.graph.SecInvocation;
import org.streampipes.model.impl.staticproperty.FreeTextStaticProperty;
import org.streampipes.model.impl.staticproperty.MappingPropertyUnary;
import org.streampipes.model.impl.staticproperty.StaticProperty;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ElasticSearchController extends AbstractFlinkConsumerDeclarer {

	@Override
	public SecDescription declareModel() {
		
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		EventProperty e1 = EpRequirements.domainPropertyReq("http://schema.org/DateTime");
		eventProperties.add(e1);
		EventSchema schema1 = new EventSchema();
		schema1.setEventProperties(eventProperties);
		
		EventStream stream1 = new EventStream();
		stream1.setEventSchema(schema1);
		
		SecDescription desc = new SecDescription("elasticsearch", "Elasticsearch", "Stores data in an elasticsearch cluster");
		desc.setIconUrl(FlinkConfig.iconBaseUrl + "/elasticsearch_icon.png");
		
		desc.addEventStream(stream1);
	
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		
		staticProperties.add(new FreeTextStaticProperty("index-name", "Index Name", "Elasticsearch index name property"));
		//TODO We removed type for the demo
		// staticProperties.add(new FreeTextStaticProperty("type-name", "Type Name", "Elasticsearch type name property"));
		staticProperties.add(new MappingPropertyUnary(URI.create(e1.getElementId()), "timestamp", "Timestamp Property", "Timestamp Mapping"));
		
		desc.setStaticProperties(staticProperties);
		desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());
		desc.setApplicationLinks(Arrays.asList(getKibanaLink(), getFlinkLink()));
		
		return desc;
	}


	@Override
	public boolean isVisualizable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getHtml(SecInvocation graph) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected FlinkSecRuntime getRuntime(SecInvocation graph) {
		return new ElasticSearchProgram(graph, new FlinkDeploymentConfig(FlinkConfig.JAR_FILE, FlinkConfig.FLINK_HOST, FlinkConfig.FLINK_PORT));
//		return new ElasticSearchProgram(graph);
	}

	public ApplicationLink getKibanaLink() {
		ApplicationLink kibanaLink = new ApplicationLink();
		kibanaLink.setApplicationName("Kibana");
		kibanaLink.setApplicationDescription("Kibana lets you visualize and analyze historical data collected by StreamPipes.");
		kibanaLink.setApplicationIconUrl(FlinkConfig.iconBaseUrl + "/elasticsearch_icon.png");
		kibanaLink.setApplicationLinkType("application");
		if (!ClientConfiguration.INSTANCE.isNissatechRunning()) {
			kibanaLink.setApplicationUrl("http://" +ClientConfiguration.INSTANCE.getElasticsearchHost() +":5601");
		} else {
			kibanaLink.setApplicationUrl("http://proasense-ui.nissatech.com/kibana");
		}
		return kibanaLink;
	}

	public ApplicationLink getFlinkLink() {
		ApplicationLink flinkLink = new ApplicationLink();
		flinkLink.setApplicationName("Flink Dashboard");
		flinkLink.setApplicationDescription("The Apache Flink Dashboard lets you see and analyze currently running StreamPipes jobs.");
		flinkLink.setApplicationIconUrl(FlinkConfig.iconBaseUrl + "/flink_icon.png");
		flinkLink.setApplicationLinkType("system");
		flinkLink.setApplicationUrl("http://" +ClientConfiguration.INSTANCE.getFlinkHost() +":48081");

		return flinkLink;
	}
}
