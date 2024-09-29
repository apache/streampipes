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
package org.apache.streampipes.extensions.management.connect.adapter;

import org.apache.streampipes.connect.shared.AdapterPipelineGeneratorBase;
import org.apache.streampipes.extensions.management.connect.adapter.model.pipeline.AdapterPipeline;
import org.apache.streampipes.extensions.management.connect.adapter.preprocessing.elements.SendToBrokerAdapterSink;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;

public class AdapterPipelineGenerator extends AdapterPipelineGeneratorBase {

  public AdapterPipeline generatePipeline(AdapterDescription adapterDescription) {

    var pipelineElements = makeAdapterPipelineElements(adapterDescription.getRules(), true);

    if (hasValidGrounding(adapterDescription)) {
      return new AdapterPipeline(pipelineElements, getAdapterSink(adapterDescription),
              adapterDescription.getEventSchema());
    } else {
      return new AdapterPipeline(pipelineElements, adapterDescription.getEventSchema());
    }
  }

  private SendToBrokerAdapterSink getAdapterSink(AdapterDescription adapterDescription) {
    return new SendToBrokerAdapterSink(adapterDescription);
  }

  private boolean hasValidGrounding(AdapterDescription adapterDescription) {
    return adapterDescription.getEventGrounding() != null
            && adapterDescription.getEventGrounding().getTransportProtocol() != null
            && adapterDescription.getEventGrounding().getTransportProtocol().getBrokerHostname() != null;
  }
}
