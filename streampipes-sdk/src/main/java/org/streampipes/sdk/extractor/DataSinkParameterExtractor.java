package org.streampipes.sdk.extractor;

import org.streampipes.model.impl.graph.SecInvocation;

/**
 * Created by riemer on 05.04.2017.
 */
public class DataSinkParameterExtractor extends AbstractParameterExtractor<SecInvocation> {

  public static DataSinkParameterExtractor from(SecInvocation sepaElement) {
    return new DataSinkParameterExtractor(sepaElement);
  }

  public DataSinkParameterExtractor(SecInvocation sepaElement) {
    super(sepaElement);
  }
}
