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

package org.apache.streampipes.processors.enricher.jvm.processor.sizemeasure;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.IStreamPipesDataProcessor;
import org.apache.streampipes.extensions.api.pe.config.IDataProcessorConfiguration;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.param.IDataProcessorParameters;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.extensions.ExtensionAssetType;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.builder.processor.DataProcessorConfiguration;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.helpers.Tuple2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class SizeMeasureProcessor implements IStreamPipesDataProcessor {

  static final String SIZE_UNIT = "sizeUnit";
  static final String BYTE_SIZE = "BYTE";
  static final String KILOBYTE_SIZE = "KILOBYTE";
  static final String MEGABYTE_SIZE = "MEGABYTE";

  static final String BYTES_OPTION = "Bytes";
  static final String KILO_BYTES_OPTION = "Kilobytes (1024 Bytes)";
  static final String MEGA_BYTES_OPTION = "Megabytes (1024 Kilobytes)";

  static final String EVENT_SIZE = "eventSize";

  private String sizeUnit;

  @Override
  public IDataProcessorConfiguration declareConfig() {
    return DataProcessorConfiguration.create(
        SizeMeasureProcessor::new,
        ProcessingElementBuilder
            .create("org.apache.streampipes.processors.enricher.jvm.sizemeasure", 0)
            .category(DataProcessorType.STRUCTURE_ANALYTICS)
            .withAssets(ExtensionAssetType.DOCUMENTATION, ExtensionAssetType.ICON)
            .withLocales(Locales.EN)
            .requiredStream(StreamRequirementsBuilder
                                .create()
                                .requiredProperty(EpRequirements.anyProperty())
                                .build())
            .requiredSingleValueSelection(
                Labels.withId(SIZE_UNIT),
                Options.from(
                    new Tuple2<>(BYTES_OPTION, BYTE_SIZE),
                    new Tuple2<>(KILO_BYTES_OPTION, KILOBYTE_SIZE),
                    new Tuple2<>(MEGA_BYTES_OPTION, MEGABYTE_SIZE)
                )
            )
            .outputStrategy(OutputStrategies.append(EpProperties.doubleEp(
                Labels.withId(EVENT_SIZE),
                EVENT_SIZE,
                "http://schema.org/contentSize"
            )))
            .build()
    );
  }

  @Override
  public void onPipelineStarted(
      IDataProcessorParameters params,
      SpOutputCollector collector,
      EventProcessorRuntimeContext runtimeContext
  ) {
    this.sizeUnit = params.extractor()
                          .selectedSingleValueInternalName(SIZE_UNIT, String.class);
  }

  @Override
  public void onEvent(Event event, SpOutputCollector collector) throws SpRuntimeException {
    try {
      double size = getSizeInBytes(event.getRaw());
      if (sizeUnit.equals(KILOBYTE_SIZE)) {
        size /= 1024;
      } else if (sizeUnit.equals(MEGABYTE_SIZE)) {
        size /= 1048576;
      }
      event.addField(EVENT_SIZE, size);
      collector.collect(event);
    } catch (IOException e) {
      throw new SpRuntimeException("Error calculating event size", e);
    }
  }

  @Override
  public void onPipelineStopped() {
  }

  private int getSizeInBytes(Object map) throws IOException {
    // Measuring the size by serializing it and then measuring the bytes
    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
    ObjectOutputStream out = new ObjectOutputStream(byteStream);

    out.writeObject(map);
    out.close();

    return byteStream.toByteArray().length;
  }
}
