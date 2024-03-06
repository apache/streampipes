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

package org.apache.streampipes.connect.iiot.adapters.migrations.config;

import org.apache.streampipes.connect.iiot.adapters.ros.RosBridgeAdapter;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.sdk.builder.adapter.AdapterConfigurationBuilder;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;

import java.util.Arrays;

import static org.apache.streampipes.connect.iiot.adapters.ros.RosBridgeAdapter.ID;
import static org.apache.streampipes.connect.iiot.adapters.ros.RosBridgeAdapter.ROS_HOST_KEY;
import static org.apache.streampipes.connect.iiot.adapters.ros.RosBridgeAdapter.ROS_PORT_KEY;
import static org.apache.streampipes.connect.iiot.adapters.ros.RosBridgeAdapter.TOPIC_KEY;

public class RosBridgeAdapterVersionedConfig {

  public static AdapterDescription getRosBridgeAdapterDescriptionV0(){
    return AdapterConfigurationBuilder.create(ID, 0, RosBridgeAdapter::new)
      .withLocales(Locales.EN)
      .withAssets(Assets.DOCUMENTATION, Assets.ICON)
      .withCategory(AdapterType.Manufacturing)
      .requiredTextParameter(Labels.withId(ROS_HOST_KEY))
      .requiredTextParameter(Labels.withId(ROS_PORT_KEY))
      .requiredSingleValueSelectionFromContainer(
        Labels.withId(TOPIC_KEY), Arrays.asList(ROS_HOST_KEY, ROS_PORT_KEY))
      .buildConfiguration().getAdapterDescription();
  }

  public static AdapterDescription getRosBridgeAdapterDescriptionV1(){
    return AdapterConfigurationBuilder.create(ID, 1, RosBridgeAdapter::new)
      .withLocales(Locales.EN)
      .withAssets(Assets.DOCUMENTATION, Assets.ICON)
      .withCategory(AdapterType.Manufacturing)
      .requiredTextParameter(Labels.withId(ROS_HOST_KEY))
      .requiredIntegerParameter(Labels.withId(ROS_PORT_KEY))
      .requiredSingleValueSelectionFromContainer(
        Labels.withId(TOPIC_KEY), Arrays.asList(ROS_HOST_KEY, ROS_PORT_KEY))
      .buildConfiguration().getAdapterDescription();
  }
}
