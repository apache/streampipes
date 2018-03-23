/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.wrapper.flink.status;

import org.streampipes.messaging.kafka.SpKafkaProducer;
import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.monitoring.ElementStatusInfoSettings;

public class PipelineElementStatusSenderFactory {

  public static <I extends InvocableStreamPipesEntity> PipelineElementStatusSender getStatusSender(I graph) {

    SpKafkaProducer kafkaProducer = new SpKafkaProducer();
    // TODO refactor

    return new PipelineElementStatusSender(kafkaProducer,
            graph.getStatusInfoSettings().getErrorTopic(),
            graph.getStatusInfoSettings().getStatsTopic());
  }

  private static <I extends InvocableStreamPipesEntity> String buildKafkaUrl(I graph) {

    ElementStatusInfoSettings settings = graph.getStatusInfoSettings();
    return settings.getKafkaHost() +":" +settings.getKafkaPort();
  }
}
