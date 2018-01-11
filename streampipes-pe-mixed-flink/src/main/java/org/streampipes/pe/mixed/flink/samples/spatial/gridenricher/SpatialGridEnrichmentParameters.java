package org.streampipes.pe.mixed.flink.samples.spatial.gridenricher;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class SpatialGridEnrichmentParameters extends EventProcessorBindingParams {

  private EnrichmentSettings enrichmentSettings;

  public SpatialGridEnrichmentParameters(DataProcessorInvocation graph, EnrichmentSettings enrichmentSettings) {
    super(graph);
    this.enrichmentSettings = enrichmentSettings;
  }

  public EnrichmentSettings getEnrichmentSettings() {
    return enrichmentSettings;
  }
}
