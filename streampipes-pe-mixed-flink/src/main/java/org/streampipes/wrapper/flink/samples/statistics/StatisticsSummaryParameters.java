package org.streampipes.wrapper.flink.samples.statistics;

import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.wrapper.params.BindingParameters;

/**
 * Created by riemer on 29.01.2017.
 */
public class StatisticsSummaryParameters extends BindingParameters {

  private String listPropertyName;

  public StatisticsSummaryParameters(SepaInvocation graph, String listPropertyName) {
    super(graph);
    this.listPropertyName = listPropertyName;
  }

  public String getListPropertyName() {
    return listPropertyName;
  }
}
