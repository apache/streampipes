package org.streampipes.pe.mixed.flink.samples.elasticsearch;

import org.streampipes.pe.mixed.flink.samples.FlinkConfig;
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkDataSinkRuntime;
import org.streampipes.pe.mixed.flink.samples.elasticsearch.elastic5.Elasticsearch5Sink;
import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.model.util.SepaUtils;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.util.Collector;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElasticSearchProgram extends FlinkDataSinkRuntime implements Serializable {


    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public ElasticSearchProgram(DataSinkInvocation graph) {
        super(graph);
    }

    public ElasticSearchProgram(DataSinkInvocation graph, FlinkDeploymentConfig config) {
        super(graph, config);
    }

    @Override
    public void getSink(
            DataStream<Map<String, Object>>... convertedStream) {

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
            transports.add(new InetSocketAddress(InetAddress.getByName(FlinkConfig.INSTANCE.getElasticsearchHost()), FlinkConfig.INSTANCE.getElasticsearchPort()));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

//        transports.add(new InetSocketAddress("ipe-koi05.fzi.de", 9300));

        convertedStream[0].flatMap(new FlatMapFunction<Map<String, Object>, Map<String, Object>>() {

            @Override
            public void flatMap(Map<String, Object> arg0, Collector<Map<String, Object>> arg1) throws Exception {
                arg0.put("timestamp", new Date((long) arg0.get(timeName)));
                arg1.collect(arg0);
            }

        }).addSink(new Elasticsearch5Sink<>(config, transports, new
                ElasticSearchIndexRequestBuilder(indexName, typeName)));
    }
}
