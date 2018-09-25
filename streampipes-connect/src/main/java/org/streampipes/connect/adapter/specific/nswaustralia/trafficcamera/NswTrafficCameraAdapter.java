/*
Copyright 2018 FZI Forschungszentrum Informatik

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
package org.streampipes.connect.adapter.specific.nswaustralia.trafficcamera;

import org.streampipes.connect.adapter.Adapter;
import org.streampipes.connect.adapter.specific.PullAdapter;
import org.streampipes.connect.adapter.specific.nswaustralia.trafficcamera.model.Feature;
import org.streampipes.connect.adapter.specific.nswaustralia.trafficcamera.model.FeatureCollection;
import org.streampipes.connect.adapter.specific.sensemap.SensorNames;
import org.streampipes.connect.adapter.util.PollingSettings;
import org.streampipes.model.connect.adapter.AdapterDescription;
import org.streampipes.model.connect.adapter.SpecificAdapterStreamDescription;
import org.streampipes.model.connect.guess.GuessSchema;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.streampipes.sdk.builder.adapter.SpecificDataStreamAdapterBuilder;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.utils.Datatypes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class NswTrafficCameraAdapter extends PullAdapter {

  public static final String ID = "http://streampipes.org/adapter/specific/nswtrafficcamera";
  private static final String API_KEY = "";

  public NswTrafficCameraAdapter() {
    super();
  }

  public NswTrafficCameraAdapter(SpecificAdapterStreamDescription adapterDescription) {
    super(adapterDescription);
  }

  @Override
  protected void pullData() {
    List<Map<String, Object>> events = getEvents();

    for (Map<String, Object> event : events) {
      adapterPipeline.process(event);
    }
  }

  @Override
  protected PollingSettings getPollingIntervalInSeconds() {
    return PollingSettings.from(TimeUnit.MINUTES, 5);
  }

  private List<Map<String, Object>> getEvents() {
    List<Map<String, Object>> events = new ArrayList<>();

    try {
      FeatureCollection cameras = new CameraInfoHttpExecutor(API_KEY).getCameraData();

      for (Feature cameraInfo : cameras.getFeatures()) {
        events.add(new CameraFeatureTransformer(cameraInfo).toMap());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return events;
  }

  @Override
  public AdapterDescription declareModel() {
    return SpecificDataStreamAdapterBuilder.create(ID, "NSW Traffic Cameras", "Traffic camera " +
            "images produced by NSW Australia")
            .requiredTextParameter(Labels.from("api-key", "API Key", "The TfNSW " +
                    "API key"))
            .iconUrl("nsw.png")
            .build();
  }

  @Override
  public Adapter getInstance(AdapterDescription adapterDescription) {
    return new NswTrafficCameraAdapter((SpecificAdapterStreamDescription) adapterDescription);
  }

  @Override
  public GuessSchema getSchema(AdapterDescription adapterDescription) {

    EventSchema schema = new EventSchema();

    List<EventProperty> allProperties = new ArrayList<>();

    allProperties.add(EpProperties.timestampProperty(SensorNames.KEY_TIMESTAMP));

    allProperties.add(PrimitivePropertyBuilder
            .create(Datatypes.String, TrafficCameraSensorNames.KEY_REGION)
            .label(TrafficCameraSensorNames.LABEL_REGION)
            .description("The region")
            .build());

    allProperties.add(PrimitivePropertyBuilder
            .create(Datatypes.String, TrafficCameraSensorNames.KEY_VIEW)
            .label(TrafficCameraSensorNames.LABEL_VIEW)
            .description("The view")
            .build());

    allProperties.add(PrimitivePropertyBuilder
            .create(Datatypes.String, TrafficCameraSensorNames.KEY_DIRECTION)
            .label(TrafficCameraSensorNames.LABEL_DIRECTION)
            .description("The region")
            .build());

    allProperties.add(PrimitivePropertyBuilder
            .create(Datatypes.String, TrafficCameraSensorNames.KEY_IMAGE)
            .label(TrafficCameraSensorNames.LABEL_IMAGE)
            .description("The image")
            .build());

    schema.setEventProperties(allProperties);

    GuessSchema guessSchema = new GuessSchema();
    guessSchema.setEventSchema(schema);
    guessSchema.setPropertyProbabilityList(Collections.emptyList());

    return guessSchema;
  }

  @Override
  public String getId() {
    return ID;
  }
}
