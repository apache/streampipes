package org.streampipes.sdk.extractor;

import org.streampipes.model.graph.DataSinkInvocation;

public class DataSinkParameterExtractor extends AbstractParameterExtractor<DataSinkInvocation> {

  public static DataSinkParameterExtractor from(DataSinkInvocation sepaElement) {
    return new DataSinkParameterExtractor(sepaElement);
  }

  public DataSinkParameterExtractor(DataSinkInvocation sepaElement) {
    super(sepaElement);
  }
}
