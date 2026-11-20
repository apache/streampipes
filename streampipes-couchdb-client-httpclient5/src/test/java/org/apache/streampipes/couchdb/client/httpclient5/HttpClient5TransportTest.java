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

package org.apache.streampipes.couchdb.client.httpclient5;

import org.apache.streampipes.couchdb.client.exception.CouchDbTransportException;
import org.apache.streampipes.couchdb.client.transport.CouchRequest;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HttpClient5TransportTest {

  @Test
  public void releasesTheConnectionWhenTheBodyCannotBeOpened() throws IOException {
    var entity = mock(HttpEntity.class);
    when(entity.getContent()).thenThrow(new IOException("connection reset"));
    var response = mock(ClassicHttpResponse.class);
    when(response.getHeaders()).thenReturn(new Header[0]);
    when(response.getEntity()).thenReturn(entity);
    var client = mock(CloseableHttpClient.class);
    when(client.executeOpen(any(), any(), any())).thenReturn(response);
    var transport = new HttpClient5Transport("http://localhost:5984", client);

    assertThrows(CouchDbTransportException.class, () -> transport.send(CouchRequest.get("/db/a"), Map.of()));

    verify(response).close();
  }
}
