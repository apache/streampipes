package org.streampipes.sdk.extractor;

import org.streampipes.model.graph.DataSinkInvocation;

/**
 * Created by riemer on 05.04.2017.
 */
public class DataSinkParameterExtractor extends AbstractParameterExtractor<DataSinkInvocation> {

  public static DataSinkParameterExtractor from(DataSinkInvocation sepaElement) {
    return new DataSinkParameterExtractor(sepaElement);
  }

  public DataSinkParameterExtractor(DataSinkInvocation sepaElement) {
    super(sepaElement);
  }
}
