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

package org.apache.streampipes.connect.adapters.image.stream;

import org.apache.streampipes.connect.adapters.image.ImageZipAdapter;
import org.apache.streampipes.connect.adapters.image.ImageZipUtils;
import org.apache.streampipes.extensions.api.connect.exception.AdapterException;
import org.apache.streampipes.extensions.api.connect.exception.ParseException;
import org.apache.streampipes.extensions.management.connect.adapter.Adapter;
import org.apache.streampipes.extensions.management.connect.adapter.model.specific.SpecificDataStreamAdapter;
import org.apache.streampipes.model.connect.adapter.SpecificAdapterStreamDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.sdk.builder.adapter.GuessSchemaBuilder;
import org.apache.streampipes.sdk.builder.adapter.SpecificDataStreamAdapterBuilder;
import org.apache.streampipes.sdk.helpers.Filetypes;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.streampipes.sdk.helpers.EpProperties.imageProperty;
import static org.apache.streampipes.sdk.helpers.EpProperties.timestampProperty;

public class ImageStreamAdapter extends SpecificDataStreamAdapter {

  private static final Logger LOG = LoggerFactory.getLogger(ImageStreamAdapter.class);

  public static final String ID = "org.apache.streampipes.connect.adapters.image.stream";

  private ImageZipAdapter imageZipAdapter;

  public ImageStreamAdapter() {

  }

  public ImageStreamAdapter(SpecificAdapterStreamDescription adapterDescription) {
    super(adapterDescription);
  }

  @Override
  public SpecificAdapterStreamDescription declareModel() {
    SpecificAdapterStreamDescription description = SpecificDataStreamAdapterBuilder.create(ID)
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .requiredIntegerParameter(Labels.withId(ImageZipUtils.INTERVAL_KEY))
        .requiredFile(Labels.withId(ImageZipUtils.ZIP_FILE_KEY), Filetypes.ZIP)
        .build();
    description.setAppId(ID);

    return description;
  }

  @Override
  public void startAdapter() throws AdapterException {
    imageZipAdapter = new ImageZipAdapter(adapterDescription);
    imageZipAdapter.start(adapterPipeline, true);
  }

  @Override
  public void stopAdapter() throws AdapterException {
    imageZipAdapter.stop();
  }

  @Override
  public GuessSchema getSchema(SpecificAdapterStreamDescription adapterDescription)
      throws AdapterException, ParseException {
    return GuessSchemaBuilder.create()
        .property(timestampProperty(ImageZipUtils.TIMESTAMP))
        .property(imageProperty(ImageZipUtils.IMAGE))
        .build();
  }

  @Override
  public Adapter getInstance(SpecificAdapterStreamDescription adapterDescription) {
    return new ImageStreamAdapter(adapterDescription);
  }

  @Override
  public String getId() {
    return ID;
  }
}
