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

package org.apache.streampipes.couchdb.client.transport;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CouchResponseTest {

  static class TrackingStream extends ByteArrayInputStream {
    boolean closed;

    TrackingStream(byte[] data) {
      super(data);
    }

    @Override
    public void close() {
      closed = true;
    }
  }

  @Test
  public void drainsTheRestOfTheBodyBeforeClosing() {
    var body = new TrackingStream("{\"ok\":true}\n".getBytes());
    var response = new CouchResponse(200, Map.of(), body);
    body.skip(11);
    response.close();
    assertEquals(0, body.available(), "trailing newline read so the connection can be reused");
    assertTrue(body.closed);
  }

  @Test
  public void stopsDrainingLargeRemainders() {
    var body = new TrackingStream(new byte[CouchResponse.MAX_DRAIN_BYTES * 3]);
    new CouchResponse(200, Map.of(), body).close();
    assertTrue(body.available() > 0);
    assertTrue(body.closed);
  }
}
