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
package org.streampipes.connect.adapters.simulator;

import org.streampipes.connect.adapter.Adapter;
import org.streampipes.connect.adapter.exception.AdapterException;
import org.streampipes.connect.adapter.exception.ParseException;
import org.streampipes.connect.adapter.model.specific.SpecificDataStreamAdapter;
import org.streampipes.connect.adapter.sdk.ParameterExtractor;
import org.streampipes.model.AdapterType;
import org.streampipes.model.connect.adapter.SpecificAdapterStreamDescription;
import org.streampipes.model.connect.guess.GuessSchema;
import org.streampipes.sdk.builder.adapter.SpecificDataStreamAdapterBuilder;
import org.streampipes.sdk.helpers.Labels;

public class RandomDataStreamAdapter extends SpecificDataStreamAdapter {

  public static final String ID = "http://streampipes.org/adapter/specific/randomdatastream";

  private final static String WaitTimeMs = "wait-time-ms";

  private RandomDataSimulator randomDataSimulator;

  public RandomDataStreamAdapter() {
    super();
  }

  public RandomDataStreamAdapter(SpecificAdapterStreamDescription adapterStreamDescription) {
    super(adapterStreamDescription);
    ParameterExtractor extractor = new ParameterExtractor(adapterStreamDescription.getConfig());
    Integer waitTimeMs = extractor.singleValue(WaitTimeMs, Integer.class);
    this.randomDataSimulator = new RandomDataSimulator(adapterPipeline, waitTimeMs);
  }

  @Override
  public SpecificAdapterStreamDescription declareModel() {
    return SpecificDataStreamAdapterBuilder.create(ID, "Random Data Simulator (Stream)",
            "Publishes an endless stream of random events")
            .category(AdapterType.Debugging)
            .requiredIntegerParameter(Labels.from(WaitTimeMs, "Wait Time (MS)", "The time to " +
                    "wait between two events in milliseconds"))
            .build();
  }

  @Override
  public void startAdapter() throws AdapterException {
    Thread thread = new Thread(this.randomDataSimulator);
    thread.start();
  }

  @Override
  public void stopAdapter() throws AdapterException {
    this.randomDataSimulator.setRunning(false);
  }

  @Override
  public Adapter getInstance(SpecificAdapterStreamDescription adapterDescription) {
    return new RandomDataStreamAdapter(adapterDescription);
  }

  @Override
  public GuessSchema getSchema(SpecificAdapterStreamDescription adapterDescription) throws AdapterException, ParseException {
    return RandomDataSimulatorUtils.randomSchema();
  }

  @Override
  public String getId() {
    return ID;
  }
}
