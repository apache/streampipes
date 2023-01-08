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
import org.apache.streampipes.extensions.management.connect.adapter.model.specific.SpecificDataStreamAdapter;
import org.apache.streampipes.extensions.management.connect.adapter.sdk.ParameterExtractor;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.adapter.SpecificAdapterStreamDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.sdk.builder.adapter.SpecificDataStreamAdapterBuilder;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;

public class RandomDataStreamAdapter extends SpecificDataStreamAdapter {

  public static final String ID = "org.apache.streampipes.connect.adapters.simulator.randomdatastream";

  private static final String WAIT_TIME_MS = "wait-time-ms";

  private RandomDataSimulator randomDataSimulator;

  public RandomDataStreamAdapter() {
    super();
  }

  public RandomDataStreamAdapter(SpecificAdapterStreamDescription adapterStreamDescription) {
    super(adapterStreamDescription);
    ParameterExtractor extractor = new ParameterExtractor(adapterStreamDescription.getConfig());
    Integer waitTimeMs = extractor.singleValue(WAIT_TIME_MS, Integer.class);
    this.randomDataSimulator = new RandomDataSimulator(adapterPipeline, waitTimeMs);
  }

  @Override
  public SpecificAdapterStreamDescription declareModel() {
    return SpecificDataStreamAdapterBuilder.create(ID)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .category(AdapterType.Debugging)
        .requiredIntegerParameter(Labels.withId(WAIT_TIME_MS))
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
  public GuessSchema getSchema(SpecificAdapterStreamDescription adapterDescription)
      throws AdapterException, ParseException {
    return RandomDataSimulatorUtils.randomSchema();
  }

  @Override
  public String getId() {
    return ID;
  }
}
