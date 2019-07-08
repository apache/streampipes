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
import org.streampipes.connect.adapter.model.specific.SpecificDataSetAdapter;
import org.streampipes.connect.adapter.sdk.ParameterExtractor;
import org.streampipes.model.AdapterType;
import org.streampipes.model.connect.adapter.SpecificAdapterSetDescription;
import org.streampipes.model.connect.guess.GuessSchema;
import org.streampipes.sdk.builder.adapter.SpecificDataSetAdapterBuilder;
import org.streampipes.sdk.helpers.Labels;

public class RandomDataSetAdapter extends SpecificDataSetAdapter {

  public static final String ID = "http://streampipes.org/adapter/specific/randomdataset";

  private static final String WaitTimeMs = "wait-time-ms";
  private static final String NumberOfEvents = "number-of-events";

  private RandomDataSimulator randomDataSimulator;

  public RandomDataSetAdapter() {
    super();
  }

  public RandomDataSetAdapter(SpecificAdapterSetDescription adapterSetDescription) {
    super(adapterSetDescription);
    ParameterExtractor extractor = new ParameterExtractor(adapterSetDescription.getConfig());
    Integer waitTimeMs = extractor.singleValue(WaitTimeMs, Integer.class);
    Integer numberOfEvents = extractor.singleValue(NumberOfEvents, Integer.class);
    this.randomDataSimulator = new RandomDataSimulator(adapterPipeline, waitTimeMs, numberOfEvents);
  }

  @Override
  public SpecificAdapterSetDescription declareModel() {
    return SpecificDataSetAdapterBuilder.create(ID, "Random Data Simulator (Set)",
            "Publishes a bounded stream of random events")
            .category(AdapterType.Debugging)
            .requiredIntegerParameter(Labels.from(WaitTimeMs, "Wait Time (MS)", "The time to " +
                    "wait between two events in milliseconds"))
            .requiredIntegerParameter(Labels.from(NumberOfEvents, "Number of Events", "The number" +
                    " of events to send."))
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
  public Adapter getInstance(SpecificAdapterSetDescription adapterDescription) {
    return new RandomDataSetAdapter(adapterDescription);
  }

  @Override
  public GuessSchema getSchema(SpecificAdapterSetDescription adapterDescription) throws AdapterException, ParseException {
    return RandomDataSimulatorUtils.randomSchema();
  }

  @Override
  public String getId() {
    return ID;
  }
}
