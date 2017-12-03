package org.streampipes.pe.mixed.flink.samples.elasticsearch;

import org.streampipes.container.util.StandardTransportFormat;
import org.streampipes.wrapper.flink.FlinkDataSinkDeclarer;
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkDataSinkRuntime;
import org.streampipes.pe.mixed.flink.samples.FlinkConfig;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.model.ApplicationLink;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.graph.DataSinkDescription;
import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.streampipes.model.staticproperty.MappingPropertyUnary;
import org.streampipes.model.staticproperty.StaticProperty;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ElasticSearchController extends FlinkDataSinkDeclarer {

	@Override
	public DataSinkDescription declareModel() {
		
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		EventProperty e1 = EpRequirements.domainPropertyReq("http://schema.org/DateTime");
		eventProperties.add(e1);
		EventSchema schema1 = new EventSchema();
		schema1.setEventProperties(eventProperties);
		
		SpDataStream stream1 = new SpDataStream();
		stream1.setEventSchema(schema1);
		
		DataSinkDescription desc = new DataSinkDescription("elasticsearch", "Elasticsearch", "Stores data in an elasticsearch cluster");
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
	protected FlinkDataSinkRuntime getRuntime(DataSinkInvocation graph) {
		return new ElasticSearchProgram(graph, new FlinkDeploymentConfig(FlinkConfig.JAR_FILE,
				FlinkConfig.INSTANCE.getFlinkHost(), FlinkConfig.INSTANCE.getFlinkPort()));
//		return new ElasticSearchProgram(graph);
	}

	public ApplicationLink getKibanaLink() {
		ApplicationLink kibanaLink = new ApplicationLink();
		kibanaLink.setApplicationName("Kibana");
		kibanaLink.setApplicationDescription("Kibana lets you visualize and analyze historical data collected by StreamPipes.");
		kibanaLink.setApplicationIconUrl(FlinkConfig.iconBaseUrl + "/elasticsearch_icon.png");
		kibanaLink.setApplicationLinkType("application");
		kibanaLink.setApplicationUrl("http://" + FlinkConfig.INSTANCE.getElasticsearchHost() +":5601");
		return kibanaLink;
	}

	public ApplicationLink getFlinkLink() {
		ApplicationLink flinkLink = new ApplicationLink();
		flinkLink.setApplicationName("Flink Dashboard");
		flinkLink.setApplicationDescription("The Apache Flink Dashboard lets you see and analyze currently running StreamPipes jobs.");
		flinkLink.setApplicationIconUrl(FlinkConfig.iconBaseUrl + "/flink_icon.png");
		flinkLink.setApplicationLinkType("system");
		flinkLink.setApplicationUrl("http://" + FlinkConfig.INSTANCE.getFlinkHost() +":48081");

		return flinkLink;
	}
}
