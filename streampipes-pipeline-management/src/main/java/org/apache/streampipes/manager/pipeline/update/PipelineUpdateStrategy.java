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

package org.apache.streampipes.manager.pipeline.update;

import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.schema.EventSchema;

public interface PipelineUpdateStrategy<T> {

  String affectedElementId(T updateElement);

  String updatedStreamName(T updateElement);

  EventSchema updatedEventSchema(T updateElement);

  default Pipeline apply(Pipeline pipeline, T updateElement) {
    var updatedStreams = pipeline
        .getStreams()
        .stream()
        .peek(stream -> {
          if (stream.getElementId().equals(affectedElementId(updateElement))) {
            stream.setEventSchema(updatedEventSchema(updateElement));
            stream.setName(updatedStreamName(updateElement));
          }
        })
        .toList();

    pipeline.setStreams(updatedStreams);
    return pipeline;
  }

  String notificationType();
}
