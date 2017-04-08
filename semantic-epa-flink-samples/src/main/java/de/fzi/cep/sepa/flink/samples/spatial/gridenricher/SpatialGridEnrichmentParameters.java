package de.fzi.cep.sepa.flink.samples.spatial.gridenricher;

import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.runtime.param.BindingParameters;

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
