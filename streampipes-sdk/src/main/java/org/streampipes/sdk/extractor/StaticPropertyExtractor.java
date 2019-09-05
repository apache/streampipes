/*
Copyright 2019 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.sdk.extractor;

import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.model.staticproperty.StaticProperty;

import java.util.List;

public class StaticPropertyExtractor extends AbstractParameterExtractor<DataSinkInvocation> {

  public StaticPropertyExtractor(DataSinkInvocation sepaElement) {
    super(sepaElement);
  }

  public static StaticPropertyExtractor from(List<StaticProperty> staticProperties,
                                                List<SpDataStream> inputStreams) {
    DataSinkInvocation dataSinkInvocation = new DataSinkInvocation();
    dataSinkInvocation.setStaticProperties(staticProperties);
    dataSinkInvocation.setInputStreams(inputStreams);
    return new StaticPropertyExtractor(dataSinkInvocation);
  }
}
