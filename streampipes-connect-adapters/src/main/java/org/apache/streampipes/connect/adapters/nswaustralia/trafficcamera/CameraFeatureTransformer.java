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
package org.apache.streampipes.connect.adapters.nswaustralia.trafficcamera;

import org.apache.streampipes.connect.adapters.nswaustralia.trafficcamera.model.Feature;
import org.apache.streampipes.connect.adapters.sensemap.SensorNames;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class CameraFeatureTransformer {

  private Feature cameraInfo;

  public CameraFeatureTransformer(Feature cameraInfo) {
    this.cameraInfo = cameraInfo;
  }

  public Map<String, Object> toMap() {
    Map<String, Object> map = new HashMap<>();

    map.put(SensorNames.KEY_TIMESTAMP, System.currentTimeMillis());
    map.put(TrafficCameraSensorNames.KEY_REGION, cameraInfo.getProperties().getRegion());
    map.put(TrafficCameraSensorNames.KEY_DIRECTION, cameraInfo.getProperties().getDirection());
    map.put(TrafficCameraSensorNames.KEY_VIEW, cameraInfo.getProperties().getView());
    map.put(TrafficCameraSensorNames.KEY_TITLE, cameraInfo.getProperties().getTitle());
    map.put(TrafficCameraSensorNames.KEY_LATITUDE, cameraInfo.getGeometry().getCoordinates().get
            (0));
    map.put(TrafficCameraSensorNames.KEY_LONGITUDE, cameraInfo.getGeometry().getCoordinates().get
            (1));
    map.put(TrafficCameraSensorNames.KEY_IMAGE, getImage(cameraInfo.getProperties().getHref()));

    return map;
  }

  private String getImage(String href) {
    try {
      byte[] imageBytes = new CameraDataHttpExecutor(href).getImageData();
      return toBase64(imageBytes);
    } catch (IOException e) {
      e.printStackTrace();
      return "No image available";
    }
  }

  private String toBase64(byte[] imageData) {
    return Base64.getEncoder().encodeToString(imageData);
  }
}
