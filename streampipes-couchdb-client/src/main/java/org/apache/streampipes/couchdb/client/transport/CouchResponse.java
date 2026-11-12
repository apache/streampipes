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

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Locale;
import java.util.Map;

/**
 * An HTTP response from CouchDB. The body is a stream that must be closed, which {@link #close()} does.
 *
 * <p>{@link #close()} first reads what is left of the body, up to {@value #MAX_DRAIN_BYTES} bytes. HTTP clients
 * only return a connection to their pool when the body was read to the end, and CouchDB follows every JSON body
 * with a newline that a JSON parser never reads. Larger remainders, such as an attachment stream closed early, are
 * cheaper to drop with the connection.</p>
 */
public final class CouchResponse implements AutoCloseable {

  static final int MAX_DRAIN_BYTES = 64 * 1024;

  private final int status;
  private final Map<String, String> headers;
  private final InputStream body;

  /**
   * Creates a response.
   *
   * @param status  HTTP status code
   * @param headers first value of each header, names in lower case
   * @param body    response body, never {@code null}
   */
  public CouchResponse(int status, Map<String, String> headers, InputStream body) {
    this.status = status;
    this.headers = headers;
    this.body = body;
  }

  public int status() {
    return status;
  }

  public String header(String name) {
    return headers.get(name.toLowerCase(Locale.ROOT));
  }

  public String contentType() {
    return header("content-type");
  }

  public InputStream body() {
    return body;
  }

  public byte[] bodyBytes() {
    try {
      return body.readAllBytes();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public void close() {
    try (body) {
      drain();
    } catch (IOException e) {
      // Nothing left to release.
    }
  }

  private void drain() throws IOException {
    var buffer = new byte[4096];
    int drained = 0;
    int read;
    while (drained < MAX_DRAIN_BYTES && (read = body.read(buffer)) != -1) {
      drained += read;
    }
  }
}
