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

package org.apache.streampipes.sinks.notifications.jvm.slack;

import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.wrapper.params.binding.EventSinkBindingParams;

public class SlackNotificationParameters extends EventSinkBindingParams {
  private String authToken;
  private String userChannel;
  private String channelType;
  private String message;

  public SlackNotificationParameters(DataSinkInvocation graph,
                                     String authToken,
                                     String channelType,
                                     String userChannel,
                                     String message) {
    super(graph);
    this.authToken = authToken;
    this.userChannel = userChannel;
    this.message = message;
    this.channelType = channelType;
  }

  public String getAuthToken() {
    return authToken;
  }

  public String getUserChannel() {
    return userChannel;
  }

  public String getMessage() {
    return message;
  }

  public String getChannelType() {
    return channelType;
  }
}

