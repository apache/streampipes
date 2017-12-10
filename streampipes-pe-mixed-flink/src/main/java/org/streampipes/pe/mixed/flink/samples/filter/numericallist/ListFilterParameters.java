package org.streampipes.pe.mixed.flink.samples.filter.numericallist;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

import java.util.List;

public class ListFilterParameters extends EventProcessorBindingParams {

  private String propertyName;
  private List<Double> filterKeywords;
  private FilterOperation filterOperation;
  private FilterSettings filterSettings;

  public ListFilterParameters(DataProcessorInvocation graph, String propertyName, List<Double> filterKeywords,
                              FilterOperation filterOperation, FilterSettings filterSettings) {
    super(graph);
    this.propertyName = propertyName;
    this.filterKeywords = filterKeywords;
    this.filterOperation = filterOperation;
    this.filterSettings = filterSettings;
  }

  public String getPropertyName() {
    return propertyName;
  }

  public List<Double> getFilterKeywords() {
    return filterKeywords;
  }

  public FilterOperation getFilterOperation() {
    return filterOperation;
  }

  public FilterSettings getFilterSettings() {
    return filterSettings;
  }

}
