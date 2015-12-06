package de.fzi.cep.sepa.flink.samples.elasticsearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSink;
import org.apache.flink.util.Collector;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;

import de.fzi.cep.sepa.flink.FlinkDeploymentConfig;
import de.fzi.cep.sepa.flink.FlinkSecRuntime;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;
import de.fzi.cep.sepa.model.util.SepaUtils;

public class ElasticSearchProgram extends FlinkSecRuntime implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ElasticSearchProgram(SecInvocation graph) {
		super(graph);
	}
	
	public ElasticSearchProgram(SecInvocation graph, FlinkDeploymentConfig config) {
		super(graph, config);
		// TODO Auto-generated constructor stub
	}

	@Override
	public DataStreamSink<Map<String, Object>> getSink(
			DataStream<Map<String, Object>> convertedStream) {
				
		Map<String, String> config = new HashMap<>();
		config.put("bulk.flush.max.actions", "100");
		config.put("cluster.name", "streampipes-cluster");

		String indexName = SepaUtils.getFreeTextStaticPropertyValue(graph, "index-name");
		String typeName = SepaUtils.getFreeTextStaticPropertyValue(graph, "type-name");
		String timeName = SepaUtils.getMappingPropertyName(graph, "timestamp");
	
		
		List<TransportAddress> transports = new ArrayList<>();
		transports.add(new InetSocketTransportAddress("ipe-koi05.fzi.de", 9300));

//		data.map(new MapFunction<String, Integer>() {
//			  public Integer map(String value) { return Integer.parseInt(value); }
//			});

		return convertedStream.flatMap(new FlatMapFunction<Map<String, Object>, Map<String, Object>>() {

			@Override
			public void flatMap(Map<String, Object> arg0, Collector<Map<String, Object>> arg1) throws Exception {
				arg0.put("timestamp", new Date((long) arg0.get(timeName)));
				arg1.collect(arg0);
			}

			
			
		}).addSink(new ElasticsearchSink<Map<String, Object>>(config, transports, new ElasticSearchIndexRequestBuilder(indexName, typeName)));

//		return convertedStream.map(new MapFunction<Map<String, Object>, Map<String, Object>>() {
//		    @Override
//		    public Map<String, Object> map(Map<String, Object> element) throws Exception {
////		    	Date d = new Date((long) element.get("appendedTime"));
//		    	Date d = new Date();
//		        return element.put(timestamp", d);
//		    }
//		})		
	}
}
