package de.fzi.cep.sepa.flink.samples.elasticsearch;

import de.fzi.cep.sepa.commons.config.ClientConfiguration;
import de.fzi.cep.sepa.flink.FlinkDeploymentConfig;
import de.fzi.cep.sepa.flink.FlinkSecRuntime;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;
import de.fzi.cep.sepa.model.util.SepaUtils;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.connectors.elasticsearch2.ElasticsearchSink;
import org.apache.flink.util.Collector;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.*;

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
    }

    @Override
    public DataStreamSink<Map<String, Object>> getSink(
            DataStream<Map<String, Object>> convertedStream) {

        Map<String, String> config = new HashMap<>();
        config.put("bulk.flush.max.actions", "100");
        config.put("cluster.name", "streampipes-cluster");

        String indexName = SepaUtils.getFreeTextStaticPropertyValue(graph, "index-name");
        String timeName = SepaUtils.getMappingPropertyName(graph, "timestamp");

        // TODO We removed the typename for the demo
        // String typeName = SepaUtils.getFreeTextStaticPropertyValue(graph, "type-name");
        String typeName = indexName;



        List<InetSocketAddress> transports = new ArrayList<>();

        try {
            transports.add(new InetSocketAddress(InetAddress.getByName(ClientConfiguration.INSTANCE.getElasticsearchHost()), ClientConfiguration.INSTANCE.getElasticsearchPort()));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

//        transports.add(new InetSocketAddress("ipe-koi05.fzi.de", 9300));

        return convertedStream.flatMap(new FlatMapFunction<Map<String, Object>, Map<String, Object>>() {

            @Override
            public void flatMap(Map<String, Object> arg0, Collector<Map<String, Object>> arg1) throws Exception {
                arg0.put("timestamp", new Date((long) arg0.get(timeName)));
                arg1.collect(arg0);
            }

        }).addSink(new ElasticsearchSink<>(config, transports, new ElasticSearchIndexRequestBuilder(indexName, typeName)));
    }
}
