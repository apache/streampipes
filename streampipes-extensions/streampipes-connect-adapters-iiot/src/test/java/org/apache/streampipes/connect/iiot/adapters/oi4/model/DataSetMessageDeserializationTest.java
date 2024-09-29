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
package org.apache.streampipes.connect.iiot.adapters.oi4.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DataSetMessageDeserializationTest {

  private String datasetMessageObjectPayload = """
          {
            "DataSetWriterId": 1,
            "SequenceNumber": 123,
            "Timestamp": "2024-05-10T07:28:01.711Z",
            "Filter": "processDataInput",
            "Source": "ABC",
            "Payload": {
              "Device status": 0,
              "Level": 163,
              "OUT1": false,
              "OUT2": false
            }
          }
          """;

  private String datasetMessageArrayPayload = """
          {
            "DataSetWriterId": 1,
            "SequenceNumber": 123,
            "Timestamp": "2024-05-10T07:28:01.711Z",
            "Filter": "processDataInput",
            "Source": "ABC",
            "Payload": [
              {
                "Device status": 0,
                "Level": 163,
                "OUT1": false,
                "OUT2": false
              }
            ]
          }
          """;

  @Test
  public void testObjectDeserialization() throws JsonProcessingException {
    var mapper = new ObjectMapper();
    var deserialized = mapper.readValue(datasetMessageObjectPayload, DataSetMessage.class);
    Assertions.assertEquals(4, deserialized.payload().size());
  }

  @Test
  public void testArrayDeserialization() throws JsonProcessingException {
    var mapper = new ObjectMapper();
    var deserialized = mapper.readValue(datasetMessageArrayPayload, DataSetMessage.class);
    Assertions.assertEquals(4, deserialized.payload().size());
  }
}
