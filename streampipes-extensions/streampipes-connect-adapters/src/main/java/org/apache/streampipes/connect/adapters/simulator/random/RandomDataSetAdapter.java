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
package org.apache.streampipes.connect.adapters.simulator.random;

import org.apache.streampipes.extensions.api.connect.exception.AdapterException;
import org.apache.streampipes.extensions.api.connect.exception.ParseException;
import org.apache.streampipes.extensions.management.connect.adapter.Adapter;
import org.apache.streampipes.extensions.management.connect.adapter.model.specific.SpecificDataSetAdapter;
import org.apache.streampipes.extensions.management.connect.adapter.sdk.ParameterExtractor;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.adapter.SpecificAdapterSetDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.sdk.builder.adapter.SpecificDataSetAdapterBuilder;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;

public class RandomDataSetAdapter extends SpecificDataSetAdapter {

  public static final String ID = "org.apache.streampipes.connect.adapters.simulator.randomdataset";

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
    return SpecificDataSetAdapterBuilder.create(ID)
        .withAssets(Assets.DOCUMENTATION)
        .withLocales(Locales.EN)
        .category(AdapterType.Debugging)
        .requiredIntegerParameter(Labels.withId(WaitTimeMs))
        .requiredIntegerParameter(Labels.withId(NumberOfEvents))
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
  public GuessSchema getSchema(SpecificAdapterSetDescription adapterDescription)
      throws AdapterException, ParseException {
    return RandomDataSimulatorUtils.randomSchema();
  }

  @Override
  public String getId() {
    return ID;
  }
}
