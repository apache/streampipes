package org.streampipes.pe.mixed.flink.samples.statistics;

import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

/**
 * Created by riemer on 29.01.2017.
 */
public class StatisticsSummaryParameters extends EventProcessorBindingParams {

  private String listPropertyName;

  public StatisticsSummaryParameters(SepaInvocation graph, String listPropertyName) {
    super(graph);
    this.listPropertyName = listPropertyName;
  }

  public String getListPropertyName() {
    return listPropertyName;
  }
}
