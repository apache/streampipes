package de.fzi.cep.sepa.flink.samples.elasticsearch;

import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.streaming.connectors.elasticsearch2.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch2.RequestIndexer;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;

import java.util.Map;

public class ElasticSearchIndexRequestBuilder implements ElasticsearchSinkFunction<Map<String, Object>> {

	private String indexName;
	private String typeName;
	
	public ElasticSearchIndexRequestBuilder(String indexName, String typeName) {
		this.indexName = indexName;
		this.typeName = typeName;
	}
	
	private static final long serialVersionUID = 1L;

    public IndexRequest createIndexRequest(Map<String, Object> element) {

        return Requests.indexRequest()
                .index(indexName)
                .type(typeName)
                .source(element);
    }

	@Override
	public void process(Map<String, Object> stringObjectMap, RuntimeContext runtimeContext, RequestIndexer requestIndexer) {
		requestIndexer.add(createIndexRequest(stringObjectMap));
	}
}
