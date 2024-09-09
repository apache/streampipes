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

package org.apache.streampipes.connectors.ros.sink;

import org.apache.streampipes.dataformat.SpDataFormatDefinition;
import org.apache.streampipes.dataformat.JsonDataFormatDefinition;
import org.apache.streampipes.extensions.api.pe.IStreamPipesDataSink;
import org.apache.streampipes.extensions.api.pe.config.IDataSinkConfiguration;
import org.apache.streampipes.extensions.api.pe.context.EventSinkRuntimeContext;
import org.apache.streampipes.extensions.api.pe.param.IDataSinkParameters;
import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.extensions.ExtensionAssetType;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.builder.sink.DataSinkConfiguration;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;

import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.Topic;
import edu.wpi.rail.jrosbridge.messages.Message;

import static org.apache.streampipes.connectors.ros.config.RosConfig.ROS_HOST_KEY;
import static org.apache.streampipes.connectors.ros.config.RosConfig.ROS_PORT_KEY;
import static org.apache.streampipes.connectors.ros.config.RosConfig.TOPIC_KEY;

public class RosBridgeSink implements IStreamPipesDataSink {

  public static final String ID = "org.apache.streampipes.connectors.ros.sink";

  private final SpDataFormatDefinition spDataFormatDefinition = new JsonDataFormatDefinition();

  private Ros ros;
  private Topic topic;

  @Override
  public IDataSinkConfiguration declareConfig() {
    return DataSinkConfiguration.create(
        RosBridgeSink::new,
        DataSinkBuilder.create(ID, 0)
            .category(DataSinkType.MESSAGING)
            .withLocales(Locales.EN)
            .withAssets(ExtensionAssetType.DOCUMENTATION, ExtensionAssetType.ICON)
            .requiredStream(
                StreamRequirementsBuilder
                    .create()
                    .requiredProperty(EpRequirements.anyProperty())
                    .build()
            )
            .requiredTextParameter(Labels.withId(ROS_HOST_KEY))
            .requiredIntegerParameter(Labels.withId(ROS_PORT_KEY))
            .requiredTextParameter(Labels.withId(TOPIC_KEY))
            .build()
    );
  }

  @Override
  public void onPipelineStarted(IDataSinkParameters params,
                                EventSinkRuntimeContext runtimeContext) {
    var extractor = params.extractor();
    String rosHost = extractor.singleValueParameter(ROS_HOST_KEY, String.class);
    int rosPort = extractor.singleValueParameter(ROS_PORT_KEY, Integer.class);
    String rosTopic = extractor.singleValueParameter(TOPIC_KEY, String.class);

    ros = new Ros(rosHost, rosPort);
    ros.connect();
    topic = new Topic(ros, rosTopic, "std_msgs/String");
  }

  @Override
  public void onEvent(Event event) {
    if (ros.isConnected()) {
      var jsonEvent = spDataFormatDefinition.fromMap(event.getRaw());
      topic.publish(new Message(new String(jsonEvent)));
    }
  }

  @Override
  public void onPipelineStopped() {
    ros.disconnect();
  }
}
