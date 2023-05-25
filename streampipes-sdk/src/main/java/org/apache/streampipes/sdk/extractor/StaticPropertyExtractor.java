/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.apache.streampipes.sdk.extractor;

import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.staticproperty.StaticProperty;

import java.util.ArrayList;
import java.util.List;

public class StaticPropertyExtractor extends AbstractParameterExtractor<DataSinkInvocation>
    implements IStaticPropertyExtractor {

  public StaticPropertyExtractor(DataSinkInvocation sepaElement) {
    super(sepaElement);
  }

  public static StaticPropertyExtractor from(List<StaticProperty> staticProperties,
                                             List<SpDataStream> inputStreams, String appId) {
    DataSinkInvocation dataSinkInvocation = makeGraph(staticProperties, inputStreams);
    dataSinkInvocation.setAppId(appId);
    return new StaticPropertyExtractor(dataSinkInvocation);
  }

  public static StaticPropertyExtractor from(List<StaticProperty> staticProperties,
                                             List<SpDataStream> inputStreams) {
    DataSinkInvocation dataSinkInvocation = makeGraph(staticProperties, inputStreams);
    return new StaticPropertyExtractor(dataSinkInvocation);
  }

  public static StaticPropertyExtractor from(List<StaticProperty> staticProperties) {
    return from(staticProperties, new ArrayList<>());
  }

  private static DataSinkInvocation makeGraph(List<StaticProperty> staticProperties,
                                              List<SpDataStream> inputStreams) {
    DataSinkInvocation dataSinkInvocation = new DataSinkInvocation();
    dataSinkInvocation.setStaticProperties(staticProperties);
    dataSinkInvocation.setInputStreams(inputStreams);
    return dataSinkInvocation;
  }
}
