package org.streampipes.wrapper.flink.samples.spatial.gridenricher;

import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.wrapper.params.BindingParameters;

/**
 * Created by riemer on 08.04.2017.
 */
public class SpatialGridEnrichmentParameters extends BindingParameters {

  private EnrichmentSettings enrichmentSettings;

  public SpatialGridEnrichmentParameters(SepaInvocation graph, EnrichmentSettings enrichmentSettings) {
    super(graph);
    this.enrichmentSettings = enrichmentSettings;
  }

  public EnrichmentSettings getEnrichmentSettings() {
    return enrichmentSettings;
  }
}
