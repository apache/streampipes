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
package org.streampipes.processors.filters.jvm.processor.pallettransportdetection;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class PalletTransportDetectionParameters extends EventProcessorBindingParams {

  private String startTs;
  private String endTs;
  private String palletField1;
  private String palletField2;

  public PalletTransportDetectionParameters(DataProcessorInvocation graph,
                                            String startTs,
                                            String endTs,
                                            String palletField1, String palletField2) {
    super(graph);
    this.startTs = startTs;
    this.endTs = endTs;
    this.palletField1 = palletField1;
    this.palletField2 = palletField2;

  }

  public String getStartTs() {
    return startTs;
  }

  public String getEndTs() {
    return endTs;
  }

  public String getPalletField1() {
    return palletField1;
  }

  public String getPalletField2() {
    return palletField2;
  }
}
