package de.fzi.cep.sepa.flink.samples.elasticsearch;

import java.util.Map;

import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.streaming.connectors.elasticsearch.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;

public class ElasticSearchIndexRequestBuilder implements IndexRequestBuilder<Map<String, Object>>{

	private String indexName;
	private String typeName;
	
	public ElasticSearchIndexRequestBuilder(String indexName, String typeName) {
		this.indexName = indexName;
		this.typeName = typeName;
	}
	
	private static final long serialVersionUID = 1L;

	@Override
    public IndexRequest createIndexRequest(Map<String, Object> element, RuntimeContext ctx) {
      
        return Requests.indexRequest()
                .index(indexName)
                .type(typeName)
                .source(element);
    }

}
