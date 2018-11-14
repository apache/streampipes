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
package org.streampipes.test.generator;

import org.streampipes.model.SpDataStream;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.test.generator.grounding.EventGroundingGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EventStreamGenerator {

  public static SpDataStream makeEmptyStream() {
    SpDataStream stream = new SpDataStream();
    stream.setEventSchema(new EventSchema());
    stream.setEventGrounding(EventGroundingGenerator.makeDummyGrounding());

    return stream;
  }

  public static SpDataStream makeStreamWithProperties(List<String> runtimeNames) {
    SpDataStream stream = makeEmptyStream();
    stream
            .getEventSchema()
            .setEventProperties(runtimeNames
                    .stream()
                    .map(r -> new EventPropertyPrimitive("", r, "", new ArrayList<>()))
                    .collect(Collectors.toList()));

    return stream;
  }
}
