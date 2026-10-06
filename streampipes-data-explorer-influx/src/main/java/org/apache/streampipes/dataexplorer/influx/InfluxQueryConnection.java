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

import org.apache.streampipes.dataexplorer.api.query.QueryTimestampFormat;
import org.apache.streampipes.dataexplorer.influx.auth.InfluxAuthMode;
import org.apache.streampipes.dataexplorer.influx.client.InfluxClientUtils;
import org.apache.streampipes.dataexplorer.influx.client.InfluxConnectionSettings;

import okhttp3.Dispatcher;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;

/** Owns one query client's HTTP calls, including cancellation before the first result callback. */
public record InfluxQueryConnection(InfluxDB client, Runnable cancelCalls) implements AutoCloseable {
  public static InfluxQueryConnection open(InfluxConnectionSettings settings) {
    return open(settings, QueryTimestampFormat.EPOCH_MILLIS);
  }

  public static InfluxQueryConnection open(InfluxConnectionSettings settings, QueryTimestampFormat format) {
    var epoch = InfluxTimestampEncoding.epoch(format);
    var dispatcher = new Dispatcher();
    var builder = settings.getAuthMode() == InfluxAuthMode.TOKEN
        ? InfluxClientUtils.getHttpClientBuilder(settings.getToken()) : InfluxClientUtils.getHttpClientBuilder();
    builder.dispatcher(dispatcher);
    // influxdb-java's chunked overload omits epoch. Set it at the query transport boundary.
    builder.addInterceptor(chain -> {
      var request = chain.request();
      if ("true".equals(request.url().queryParameter("chunked"))) {
        var url = request.url().newBuilder().removeAllQueryParameters("epoch");
        if (epoch != null) {
          url.setQueryParameter("epoch", epoch);
        }
        request = request.newBuilder().url(url.build()).build();
      }
      return chain.proceed(request);
    });
    var client = settings.getAuthMode() == InfluxAuthMode.TOKEN
        ? InfluxDBFactory.connect(settings.getConnectionUrl(), builder)
        : InfluxDBFactory.connect(settings.getConnectionUrl(), settings.getUsername(), settings.getPassword(), builder);
    return new InfluxQueryConnection(client, dispatcher::cancelAll);
  }

  @Override
  public void close() {
    try {
      cancelCalls.run();
    } finally {
      client.close();
    }
  }
}
