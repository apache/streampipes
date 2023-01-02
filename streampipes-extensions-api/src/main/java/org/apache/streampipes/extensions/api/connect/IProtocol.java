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
package org.apache.streampipes.extensions.api.connect;

import org.apache.streampipes.extensions.api.connect.exception.AdapterException;
import org.apache.streampipes.extensions.api.connect.exception.ParseException;
import org.apache.streampipes.model.connect.grounding.ProtocolDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.schema.EventSchema;

import java.util.List;
import java.util.Map;

public interface IProtocol extends Connector {

  IProtocol getInstance(ProtocolDescription protocolDescription,
                        IParser parser,
                        IFormat format);

  ProtocolDescription declareModel();

  GuessSchema getGuessSchema() throws ParseException;

  List<Map<String, Object>> getNElements(int n) throws ParseException;

  void run(IAdapterPipeline adapterPipeline) throws AdapterException;

  /*
       Stops the running protocol. Mainly relevant for streaming protocols
     */
  void stop();

  String getId();

  //TODO remove
  void setEventSchema(EventSchema eventSchema);
}
