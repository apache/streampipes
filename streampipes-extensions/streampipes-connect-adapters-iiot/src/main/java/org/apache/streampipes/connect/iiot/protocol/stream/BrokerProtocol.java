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
package org.apache.streampipes.connect.iiot.protocol.stream;

import org.apache.streampipes.extensions.api.connect.IFormat;
import org.apache.streampipes.extensions.api.connect.IParser;
import org.apache.streampipes.extensions.api.connect.exception.ParseException;
import org.apache.streampipes.extensions.management.connect.adapter.guess.SchemaGuesser;
import org.apache.streampipes.extensions.management.connect.adapter.model.generic.Protocol;
import org.apache.streampipes.model.connect.guess.GuessSchema;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class BrokerProtocol extends Protocol {

  protected String brokerUrl;
  protected String topic;

  public BrokerProtocol() {

  }

  public BrokerProtocol(IParser parser, IFormat format, String brokerUrl, String topic) {
    super(parser, format);
    this.brokerUrl = brokerUrl;
    this.topic = topic;
  }

  @Override
  public GuessSchema getGuessSchema() throws ParseException {

    List<byte[]> eventByte = getNByteElements(1);

    if (parser.supportsPreview()) {
      return SchemaGuesser.guessSchema(parser.getSchemaAndSample(eventByte));
    } else {
      return SchemaGuesser.guessSchema(parser.getEventSchema(eventByte));
    }
  }

  @Override
  public List<Map<String, Object>> getNElements(int n) throws ParseException {
    List<byte[]> resultEventsByte = getNByteElements(n);
    List<Map<String, Object>> result = new ArrayList<>();
    for (byte[] event : resultEventsByte) {
      result.add(format.parse(event));
    }

    return result;
  }

  protected abstract List<byte[]> getNByteElements(int n) throws ParseException;

}
