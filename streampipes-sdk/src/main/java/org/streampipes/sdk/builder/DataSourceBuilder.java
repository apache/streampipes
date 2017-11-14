package org.streampipes.sdk.builder;

import org.streampipes.model.graph.DataSourceDescription;

/**
 * Created by riemer on 04.12.2016.
 */
public class DataSourceBuilder extends AbstractPipelineElementBuilder<DataSourceBuilder, DataSourceDescription> {

  public static DataSourceBuilder create(String id, String label, String description) {
    return new DataSourceBuilder(id, label, description);
  }

  protected DataSourceBuilder(String id, String label, String description) {
    super(id, label, description, new DataSourceDescription());
  }

  @Override
  protected DataSourceBuilder me() {
    return this;
  }

  @Override
  protected void prepareBuild() {

  }
}
