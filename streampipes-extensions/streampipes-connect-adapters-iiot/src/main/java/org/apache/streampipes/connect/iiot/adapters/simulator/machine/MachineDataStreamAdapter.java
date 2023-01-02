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
package org.apache.streampipes.connect.iiot.adapters.simulator.machine;

import org.apache.streampipes.extensions.api.connect.exception.AdapterException;
import org.apache.streampipes.extensions.api.connect.exception.ParseException;
import org.apache.streampipes.extensions.management.connect.adapter.Adapter;
import org.apache.streampipes.extensions.management.connect.adapter.model.specific.SpecificDataStreamAdapter;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.adapter.SpecificAdapterStreamDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.sdk.builder.adapter.SpecificDataStreamAdapterBuilder;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.utils.Assets;

import java.util.ArrayList;

public class MachineDataStreamAdapter extends SpecificDataStreamAdapter {

  public static final String ID = "org.apache.streampipes.connect.iiot.adapters.simulator.machine";
  private static final String WAIT_TIME_MS = "wait-time-ms";
  private static final String SELECTED_SIMULATOR_OPTION = "selected-simulator-option";

  private String selectedSimulatorOption = "";

  private MachineDataSimulator machineDataSimulator;

  public MachineDataStreamAdapter() {
  }

  public MachineDataStreamAdapter(SpecificAdapterStreamDescription adapterStreamDescription) {
    super(adapterStreamDescription);
    StaticPropertyExtractor extractor =
        StaticPropertyExtractor.from(adapterStreamDescription.getConfig(), new ArrayList<>());
    Integer waitTimeMs = extractor.singleValueParameter(WAIT_TIME_MS, Integer.class);
    this.selectedSimulatorOption = extractor.selectedSingleValue(SELECTED_SIMULATOR_OPTION, String.class);
    this.machineDataSimulator = new MachineDataSimulator(adapterPipeline, waitTimeMs, selectedSimulatorOption);
  }

  @Override
  public SpecificAdapterStreamDescription declareModel() {
    return SpecificDataStreamAdapterBuilder.create(ID)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .category(AdapterType.Debugging)
        .requiredIntegerParameter(Labels.withId(WAIT_TIME_MS), 1000)
        .requiredSingleValueSelection(Labels.withId(SELECTED_SIMULATOR_OPTION), Options.from(
            "flowrate", "pressure", "waterlevel"))
        .build();
  }

  @Override
  public void startAdapter() throws AdapterException {
    Thread thread = new Thread(this.machineDataSimulator);
    thread.start();
  }

  @Override
  public void stopAdapter() throws AdapterException {
    this.machineDataSimulator.setRunning(false);
  }

  @Override
  public Adapter getInstance(SpecificAdapterStreamDescription adapterStreamDescription) {
    return new MachineDataStreamAdapter(adapterStreamDescription);
  }

  @Override
  public GuessSchema getSchema(SpecificAdapterStreamDescription adapterStreamDescription)
      throws AdapterException, ParseException {
    return MachineDataSimulatorUtils.getSchema(this.selectedSimulatorOption);
  }

  @Override
  public String getId() {
    return ID;
  }
}
