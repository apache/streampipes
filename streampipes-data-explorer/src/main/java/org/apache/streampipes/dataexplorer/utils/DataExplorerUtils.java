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
package org.apache.streampipes.dataexplorer.utils;

import org.apache.streampipes.config.backend.BackendConfig;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.storage.management.StorageDispatcher;

import okhttp3.OkHttpClient;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class DataExplorerUtils {

  public static List<DataLakeMeasure> getInfos() {
    return StorageDispatcher.INSTANCE
        .getNoSqlStore()
        .getDataLakeStorage()
        .getAllDataLakeMeasures();
  }

  public static InfluxDB getInfluxDBClient() {
    OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient().newBuilder()
        .connectTimeout(120, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS);

    return InfluxDBFactory.connect(BackendConfig.INSTANCE.getInfluxUrl(), okHttpClientBuilder);
  }
}
