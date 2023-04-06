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

import org.apache.streampipes.connect.iiot.utils.FileProtocolUtils;
import org.apache.streampipes.extensions.api.connect.exception.AdapterException;
import org.apache.streampipes.extensions.management.connect.AdapterInterface;
import org.apache.streampipes.extensions.management.connect.IAdapterRuntimeContext;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.adapter.AdapterConfiguration;
import org.apache.streampipes.model.connect.adapter.IEventCollector;
import org.apache.streampipes.model.connect.guess.AdapterGuessInfo;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.builder.adapter.AdapterConfigurationBuilder;
import org.apache.streampipes.sdk.builder.adapter.CsvParser;
import org.apache.streampipes.sdk.builder.adapter.JsonParser;
import org.apache.streampipes.sdk.extractor.AdapterParameterExtractor;
import org.apache.streampipes.sdk.helpers.Alternatives;
import org.apache.streampipes.sdk.helpers.Filetypes;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.utils.Assets;

import java.io.IOException;
import java.io.InputStream;

public class FileReplayAdapter implements AdapterInterface {

  private static final String REPLACE_TIMESTAMP = "replaceTimestamp";
  private static final String SPEED = "speed";
  private static final String FILE_PATH = "filePath";
  private static final String REPLAY_ONCE = "replayOnce";

  private static final String SPEED_UP = "speedUp";
  private static final String KEEP_ORIGINAL_TIME = "keepOriginalTime";
  private static final String SPEED_UP_FACTOR = "speedUpFactor";
  private static final String FASTEST = "fastest";

  private static final String SPEED_UP_FACTOR_GROUP = "speed-up-factor-group";


  @Override
  public AdapterConfiguration declareConfig() {
    return AdapterConfigurationBuilder.create("org.apache.streampipes.connect.iiot.adapters.simulator.machine.v2")
        .withSupportedParsers(new JsonParser(), new CsvParser())
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .withCategory(AdapterType.Generic)
        .requiredFile(Labels.withId(FILE_PATH), Filetypes.CSV, Filetypes.JSON, Filetypes.XML)
        .requiredMultiValueSelection(Labels.withId(REPLACE_TIMESTAMP), Options.from(""))
        .requiredSingleValueSelection(Labels.withId(REPLAY_ONCE), Options.from("no", "yes"))
        .requiredAlternatives(Labels.withId(SPEED),
            Alternatives.from(Labels.withId(KEEP_ORIGINAL_TIME), true),
            Alternatives.from(Labels.withId(FASTEST)), Alternatives.from(Labels.withId(SPEED_UP_FACTOR),
                StaticProperties.group(Labels.withId(SPEED_UP_FACTOR_GROUP),
                    StaticProperties.doubleFreeTextProperty(Labels.withId(SPEED_UP)))))
        .build();
  }

  @Override
  public void onAdapterStarted(AdapterParameterExtractor extractor,
                               IEventCollector collector,
                               IAdapterRuntimeContext adapterRuntimeContext) throws AdapterException {
    var inputStream = getDataFromEndpoint(extractor.selectedFilename(FILE_PATH));
    extractor.selectedParser().parse(inputStream, collector);
  }

  @Override
  public void onAdapterStopped(AdapterParameterExtractor extractor, IAdapterRuntimeContext adapterRuntimeContext)
      throws AdapterException {

  }

  @Override
  public AdapterGuessInfo onSchemaRequested(AdapterParameterExtractor extractor,
                                            IAdapterRuntimeContext adapterRuntimeContext) throws AdapterException {
    var inputStream = getDataFromEndpoint(extractor.selectedFilename(FILE_PATH));
    var adapterGuessInfo = extractor.selectedParser().getAdapterGuessInfo(inputStream);

    return adapterGuessInfo;
  }

  private InputStream getDataFromEndpoint(String selectedFileName) throws AdapterException {
    try {
      return FileProtocolUtils.getFileInputStream(selectedFileName);
    } catch (IOException e) {
      throw new AdapterException("Could not find file: " + selectedFileName);
    }
  }
}
