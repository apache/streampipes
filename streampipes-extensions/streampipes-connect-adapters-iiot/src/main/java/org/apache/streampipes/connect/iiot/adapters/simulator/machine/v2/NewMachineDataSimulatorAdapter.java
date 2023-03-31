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

package org.apache.streampipes.connect.iiot.adapters.simulator.machine.v2;

import org.apache.streampipes.connect.iiot.adapters.simulator.machine.MachineDataSimulator;
import org.apache.streampipes.connect.iiot.adapters.simulator.machine.MachineDataSimulatorUtils;
import org.apache.streampipes.extensions.management.connect.AdapterInterface;
import org.apache.streampipes.extensions.api.connect.exception.AdapterException;
import org.apache.streampipes.extensions.management.connect.IAdapterRuntimeContext;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.adapter.AdapterConfiguration;
import org.apache.streampipes.model.connect.adapter.IEventCollector;
import org.apache.streampipes.model.connect.guess.AdapterGuessInfo;
import org.apache.streampipes.sdk.builder.adapter.AdapterConfigurationBuilder;
import org.apache.streampipes.sdk.builder.adapter.SpecificDataStreamAdapterBuilder;
import org.apache.streampipes.sdk.extractor.AdapterParameterExtractor;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.utils.Assets;

public class NewMachineDataSimulatorAdapter implements AdapterInterface {

  private static final String WAIT_TIME_MS = "wait-time-ms";
  private static final String SELECTED_SIMULATOR_OPTION = "selected-simulator-option";

  private MachineDataSimulator machineDataSimulator;

  @Override
  public AdapterConfiguration declareConfig() {
    return AdapterConfigurationBuilder.create()
        .withAdapterDescription(
            SpecificDataStreamAdapterBuilder.create("org.apache.streampipes.connect.iiot.adapters.simulator.machine.v2")
                .withAssets(Assets.DOCUMENTATION, Assets.ICON)
                .withLocales(Locales.EN)
                .category(AdapterType.Debugging)
                .requiredIntegerParameter(Labels.withId(WAIT_TIME_MS), 1000)
                .requiredSingleValueSelection(Labels.withId(SELECTED_SIMULATOR_OPTION), Options.from(
                    "flowrate", "pressure", "waterlevel"))
                .build())
        .build();
  }

  @Override
  public void onAdapterStarted(AdapterParameterExtractor extractor,
                               IEventCollector collector,
                               IAdapterRuntimeContext adapterRuntimeContext)
      throws AdapterException {

    var waitTimeMs = extractor.singleValueParameter(WAIT_TIME_MS, Integer.class);
    var selectedSimulatorOption = extractor.selectedSingleValue(SELECTED_SIMULATOR_OPTION, String.class);
    this.machineDataSimulator = new MachineDataSimulator(collector, waitTimeMs, selectedSimulatorOption);
    Thread thread = new Thread(this.machineDataSimulator);
    thread.start();
  }

  @Override
  public void onAdapterStopped(AdapterParameterExtractor extractor,
                               IAdapterRuntimeContext adapterRuntimeContext) throws AdapterException {
    this.machineDataSimulator.setRunning(false);
  }


  @Override
  public AdapterGuessInfo onSchemaRequested(AdapterParameterExtractor extractor,
                                            IAdapterRuntimeContext adapterRuntimeContext) throws AdapterException {
    var selectedSimulatorOption = extractor.selectedSingleValue(SELECTED_SIMULATOR_OPTION, String.class);
    return MachineDataSimulatorUtils.getAdapterGuessInfo(selectedSimulatorOption);
  }
}
