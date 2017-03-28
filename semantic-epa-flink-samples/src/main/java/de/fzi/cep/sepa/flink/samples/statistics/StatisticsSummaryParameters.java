package de.fzi.cep.sepa.flink.samples.statistics;

import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.runtime.param.BindingParameters;

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
