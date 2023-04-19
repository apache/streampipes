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

package org.apache.streampipes.extensions.management.connect.adapter.parser;

import org.apache.streampipes.extensions.management.connect.adapter.format.util.JsonEventProperty;
import org.apache.streampipes.model.connect.adapter.IEventCollector;
import org.apache.streampipes.model.connect.adapter.Parser;
import org.apache.streampipes.model.connect.grounding.ParserDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.sdk.builder.adapter.GuessSchemaBuilder;
import org.apache.streampipes.sdk.builder.adapter.ParserDescriptionBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class JsonParser implements Parser {

  private static final Logger LOG = LoggerFactory.getLogger(JsonParser.class);

  public static final String ID = "org.apache.streampipes.extensions.management.connect.adapter.parser";
  public static final String LABEL = "Json";

  private final ObjectMapper mapper;

  public JsonParser() {
    mapper = new ObjectMapper();
  }

  @Override
  public ParserDescription declareDescription() {
    return ParserDescriptionBuilder
        .create(ID, LABEL, "Each event is a single json object (e.g. {'value': 1})")
        .build();
  }

  @Override
  public GuessSchema getGuessSchema(InputStream inputStream) {
    var schemaBuilder = GuessSchemaBuilder.create();

    toMap(inputStream)
        .forEach((key, value) -> {
          schemaBuilder.sample(
              key,
              value);
          schemaBuilder
              .property(
                  JsonEventProperty.getEventProperty(
                      key,
                      value
                  ));
        });

    return schemaBuilder.build();
  }

  // TODO we need to introduce a parse exeption here
  @Override
  public void parse(InputStream inputStream, IEventCollector collector) {
    Map<String, Object> event = toMap(inputStream);
    collector.collect(event);
  }

  private Map<String, Object> toMap(InputStream inputStream) {
    try {
      return mapper.readValue(inputStream, HashMap.class);
    } catch (IOException e) {
      LOG.error("Could not parse event", e);
    }

    // TODO we need to introduce a parse exeption here
    return null;
  }

}
