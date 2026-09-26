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

package org.apache.streampipes.dataexplorer.influx;

import org.apache.streampipes.dataexplorer.influx.auth.InfluxAuthMode;
import org.apache.streampipes.dataexplorer.influx.client.InfluxClientUtils;
import org.apache.streampipes.dataexplorer.influx.client.InfluxConnectionSettings;

import okhttp3.Call;
import okhttp3.Credentials;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.IOException;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

/** Synchronous line-protocol transport, without database provisioning, batching or implicit retries. */
public final class InfluxWriteTransport implements AutoCloseable {
  private final OkHttpClient client;
  private final HttpUrl endpoint;
  private final Set<Call> calls = new HashSet<>();
  private boolean closed;

  public InfluxWriteTransport(InfluxConnectionSettings settings, Duration timeout) {
    var builder = settings.getAuthMode() == InfluxAuthMode.TOKEN
        ? InfluxClientUtils.getHttpClientBuilder(settings.getToken()) : InfluxClientUtils.getHttpClientBuilder();
    if (settings.getAuthMode() != InfluxAuthMode.TOKEN) {
      String authorization = Credentials.basic(settings.getUsername(), settings.getPassword());
      builder.addInterceptor(chain -> chain.proceed(chain.request().newBuilder()
          .header("Authorization", authorization).build()));
    }
    client = builder.connectTimeout(timeout).readTimeout(timeout).writeTimeout(timeout).callTimeout(timeout)
        .retryOnConnectionFailure(false).followRedirects(false).followSslRedirects(false).build();
    endpoint = HttpUrl.get(settings.getConnectionUrl()).newBuilder().addPathSegment("write")
        .addQueryParameter("db", settings.getDatabaseName()).addQueryParameter("precision", "ns").build();
  }

  /** Returns the HTTP status; callers own acknowledgement and retry policy. Response bodies are never logged. */
  public int write(String lineProtocol) throws IOException {
    var request = new Request.Builder().url(endpoint)
        .post(RequestBody.create(MediaType.get("text/plain; charset=utf-8"), lineProtocol)).build();
    Call call;
    synchronized (this) {
      if (closed) {
        throw new IllegalStateException("Influx writer closed");
      }
      call = client.newCall(request);
      calls.add(call);
    }
    try (var response = call.execute()) {
      return response.code();
    } finally {
      synchronized (this) {
        calls.remove(call);
      }
    }
  }

  @Override
  public synchronized void close() {
    closed = true;
    calls.forEach(Call::cancel);
    calls.clear();
    client.dispatcher().cancelAll();
    client.dispatcher().executorService().shutdown();
    // InfluxClientUtils owns the shared connection pool; do not evict other clients' connections.
  }
}
