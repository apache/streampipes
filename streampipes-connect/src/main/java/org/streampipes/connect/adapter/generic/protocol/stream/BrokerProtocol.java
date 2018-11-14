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
package org.streampipes.connect.adapter.generic.protocol.stream;

import org.streampipes.connect.adapter.generic.format.Format;
import org.streampipes.connect.adapter.generic.format.Parser;
import org.streampipes.connect.adapter.generic.guess.SchemaGuesser;
import org.streampipes.connect.adapter.generic.protocol.Protocol;
import org.streampipes.model.connect.guess.GuessSchema;
import org.streampipes.model.schema.EventSchema;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class BrokerProtocol extends Protocol {

  protected String brokerUrl;
  protected String topic;

  public BrokerProtocol() {

  }

  public BrokerProtocol(Parser parser, Format format, String brokerUrl, String topic) {
    super(parser, format);
    this.brokerUrl = brokerUrl;
    this.topic = topic;
  }

  @Override
  public GuessSchema getGuessSchema() {

    List<byte[]> eventByte = getNByteElements(1);
    EventSchema eventSchema = parser.getEventSchema(eventByte);

    return SchemaGuesser.guessSchma(eventSchema, getNElements(1));
  }

  @Override
  public List<Map<String, Object>> getNElements(int n) {
    List<byte[]> resultEventsByte = getNByteElements(n);
    List<Map<String, Object>> result = new ArrayList<>();
    for (byte[] event : resultEventsByte) {
      result.add(format.parse(event));
    }

    return result;
  }

  protected abstract List<byte[]> getNByteElements(int n);

}
