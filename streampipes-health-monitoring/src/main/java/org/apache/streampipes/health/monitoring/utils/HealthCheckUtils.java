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

package org.apache.streampipes.health.monitoring.utils;

import org.apache.streampipes.commons.constants.InstanceIdExtractor;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HealthCheckUtils {

  public static void addSuccessfulRestoreNotification(List<String> pipelineNotifications,
                                                      InvocableStreamPipesEntity pipelineElement) {
    pipelineNotifications.add(getCurrentDatetime() + "Pipeline element '" + pipelineElement.getName()
        + "' was not available and was successfully restored.");
  }

  public static void addFailedAttemptNotification(List<String> pipelineNotifications,
                                                  InvocableStreamPipesEntity pipelineElement) {
    pipelineNotifications.add(getCurrentDatetime() + "Pipeline element '" + pipelineElement.getName()
        + "' was not available and could not be restored.");
  }

  private static String getCurrentDatetime() {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");
    LocalDateTime now = LocalDateTime.now();
    return "[" + dtf.format(now) + "] ";
  }

  public static String extractInstanceId(InvocableStreamPipesEntity pipelineElement) {
    return InstanceIdExtractor.extractId(pipelineElement.getElementId());
  }


}
