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

import org.apache.streampipes.dataexplorer.api.query.DatasetQueryBackend;
import org.apache.streampipes.dataexplorer.api.query.DatasetQueryCursor;
import org.apache.streampipes.dataexplorer.api.query.QueryCapabilities;
import org.apache.streampipes.dataexplorer.api.query.QueryExecutionException;
import org.apache.streampipes.dataexplorer.api.query.QueryExecutionOptions;
import org.apache.streampipes.dataexplorer.api.query.QuerySpec;
import org.apache.streampipes.dataexplorer.api.query.QueryTimestampFormat;
import org.apache.streampipes.dataexplorer.influx.client.InfluxConnectionSettings;
import org.apache.streampipes.dataexplorer.query.DatasetQueryFields;
import org.apache.streampipes.model.dataset.DatasetMetadata;

import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

public final class InfluxQueryBackend implements DatasetQueryBackend {
  private final InfluxQueryCompiler compiler;
  private final String database;
  private final Supplier<InfluxQueryConnection> clients;
  private final InfluxQueryTransport transport;
  private final Function<QueryTimestampFormat, InfluxQueryConnection> formattedClients;

  public InfluxQueryBackend(String database, Supplier<InfluxQueryConnection> clients) {
    this.database = database;
    this.compiler = new InfluxQueryCompiler(database);
    this.clients = clients;
    this.transport = null;
    this.formattedClients = null;
  }

  public InfluxQueryBackend(String database, Function<QueryTimestampFormat, InfluxQueryConnection> clients) {
    this.database = database;
    this.compiler = new InfluxQueryCompiler(database);
    this.clients = () -> clients.apply(QueryTimestampFormat.EPOCH_MILLIS);
    this.formattedClients = clients;
    this.transport = null;
  }

  public InfluxQueryBackend(InfluxConnectionSettings settings) {
    this.database = settings.getDatabaseName();
    this.compiler = new InfluxQueryCompiler(database);
    this.clients = null;
    this.formattedClients = null;
    this.transport = new InfluxQueryTransport(settings);
  }

  @Override
  public void close() {
    if (transport != null) {
      transport.close();
    }
  }

  @Override
  public QueryCapabilities capabilities() {
    return compiler.capabilities();
  }

  @Override
  public Map<String, Long> latestTimestamps(List<DatasetMetadata> datasets) {
    var fields = new LinkedHashMap<String, String>();
    for (var dataset : datasets) {
      DatasetQueryFields.firstCountableProperty(dataset)
          .ifPresent(field -> fields.put(dataset.getMeasureName(), field));
    }
    if (fields.isEmpty()) {
      return Map.of();
    }
    var nativeQueries = new InfluxLatestTimestampQuery();
    var statement = nativeQueries.compile(fields, database);
    var query = new Query(statement.getCommand(), database);
    var result = execute(query);
    if (result.hasError() || result.getResults() != null
        && result.getResults().stream().anyMatch(QueryResult.Result::hasError)) {
      throw new QueryExecutionException("Latest timestamp query failed");
    }
    var byName = nativeQueries.parse(result);
    var byId = new LinkedHashMap<String, Long>();
    datasets.forEach(dataset -> {
      if (byName.containsKey(dataset.getMeasureName())) {
        byId.put(dataset.getElementId(), byName.get(dataset.getMeasureName()));
      }
    });
    return byId;
  }

  private QueryResult execute(Query query) {
    if (transport != null) {
      return transport.execute(query);
    }
    try (var connection = clients.get()) {
      return connection.client().query(query, TimeUnit.MILLISECONDS);
    }
  }

  @Override
  public DatasetQueryCursor open(DatasetMetadata dataset, QuerySpec query) {
    return open(dataset, query, QueryExecutionOptions.defaults());
  }

  @Override
  public DatasetQueryCursor open(DatasetMetadata dataset, QuerySpec query, QueryExecutionOptions options) {
    var statement = compiler.compile(query, dataset.getMeasureName());
    if (transport != null) {
      return transport.open(statement, options.timestampFormat());
    }
    if (formattedClients != null) {
      var format = InfluxTimestampEncoding.nativeClient(options.timestampFormat());
      var connection = formattedClients.apply(format);
      return new InfluxQueryCursor(connection.client(), statement, format, connection::close);
    }
    var connection = clients.get();
    return new InfluxQueryCursor(connection.client(), statement, connection::close);
  }
}
