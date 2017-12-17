package org.streampipes.pe.mixed.flink.samples.elasticsearch;

import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.wrapper.params.binding.EventSinkBindingParams;

public class ElasticSearchParameters extends EventSinkBindingParams {

  private String timestampField;
  private String indexName;

  public ElasticSearchParameters(DataSinkInvocation graph, String timestampField, String indexName) {
    super(graph);
    this.timestampField = timestampField;
    this.indexName = indexName;
  }

  public String getTimestampField() {
    return timestampField;
  }

  public String getIndexName() {
    return indexName;
  }
}
