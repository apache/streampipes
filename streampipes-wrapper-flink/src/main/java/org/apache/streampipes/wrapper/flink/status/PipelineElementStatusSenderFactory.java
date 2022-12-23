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

package org.apache.streampipes.wrapper.flink.status;

import org.apache.streampipes.messaging.kafka.SpKafkaProducer;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.monitoring.ElementStatusInfoSettings;

public class PipelineElementStatusSenderFactory {

  public static <T extends InvocableStreamPipesEntity> PipelineElementStatusSender getStatusSender(T graph) {

    SpKafkaProducer kafkaProducer = new SpKafkaProducer();
    // TODO refactor

    return new PipelineElementStatusSender(kafkaProducer,
        graph.getStatusInfoSettings().getErrorTopic(),
        graph.getStatusInfoSettings().getStatsTopic());
  }

  private static <T extends InvocableStreamPipesEntity> String buildKafkaUrl(T graph) {

    ElementStatusInfoSettings settings = graph.getStatusInfoSettings();
    return settings.getKafkaHost() + ":" + settings.getKafkaPort();
  }
}
