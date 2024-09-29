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
package org.apache.streampipes.connectors.ros.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.Service;
import edu.wpi.rail.jrosbridge.services.ServiceRequest;
import edu.wpi.rail.jrosbridge.services.ServiceResponse;

public class RosConfig {

  public static final String ROS_HOST_KEY = "ROS_HOST_KEY";
  public static final String ROS_PORT_KEY = "ROS_PORT_KEY";
  public static final String TOPIC_KEY = "TOPIC_KEY";

  public static String getMethodType(Ros ros, String topic) {
    Service addTwoInts = new Service(ros, "/rosapi/topic_type", "rosapi/TopicType");
    ServiceRequest request = new ServiceRequest("{\"topic\": \"" + topic + "\"}");
    ServiceResponse response = addTwoInts.callServiceAndWait(request);

    JsonObject ob = new JsonParser().parse(response.toString()).getAsJsonObject();
    return ob.get("type").getAsString();
  }
}
