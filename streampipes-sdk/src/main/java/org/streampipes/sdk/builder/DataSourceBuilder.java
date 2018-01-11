package org.streampipes.sdk.builder;

import org.streampipes.model.graph.DataSourceDescription;

public class DataSourceBuilder extends AbstractPipelineElementBuilder<DataSourceBuilder, DataSourceDescription> {

  public static DataSourceBuilder create(String id, String label, String description) {
    return new DataSourceBuilder(id, label, description);
  }

  /**
   * Creates a new data source using the builder pattern.
   * @param id A unique identifier of the new element, e.g., com.mycompany.source.mynewdatasource
   * @param label A human-readable name of the element. Will later be shown as the element name in the StreamPipes UI.
   * @param description A human-readable description of the element.
   */
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
