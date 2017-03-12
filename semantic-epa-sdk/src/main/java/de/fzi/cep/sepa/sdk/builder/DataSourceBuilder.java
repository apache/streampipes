package de.fzi.cep.sepa.sdk.builder;

import de.fzi.cep.sepa.model.impl.graph.SepDescription;

/**
 * Created by riemer on 04.12.2016.
 */
public class DataSourceBuilder extends AbstractPipelineElementBuilder<DataSourceBuilder, SepDescription> {

  public static DataSourceBuilder create(String id, String label, String description) {
    return new DataSourceBuilder(id, label, description);
  }

  protected DataSourceBuilder(String id, String label, String description) {
    super(id, label, description, new SepDescription());
  }

  @Override
  protected DataSourceBuilder me() {
    return this;
  }

  @Override
  protected void prepareBuild() {

  }
}
