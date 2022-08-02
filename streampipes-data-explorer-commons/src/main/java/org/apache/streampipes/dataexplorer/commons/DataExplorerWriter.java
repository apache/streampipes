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

package org.apache.streampipes.dataexplorer.commons;

import org.apache.streampipes.dataexplorer.commons.configs.DataExplorerConfigurations;
import org.apache.streampipes.dataexplorer.commons.influx.InfluxConnectionSettings;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Deprecated
public class DataExplorerWriter {
    private InfluxDB influxDB;

    // TODO return a connection here
    public void connect(InfluxConnectionSettings dataExplorerConnectionSettings) {
        this.influxDB = InfluxDBFactory.connect(dataExplorerConnectionSettings.getInfluxDbHost() + ":" + dataExplorerConnectionSettings.getInfluxDbPort(),
                dataExplorerConnectionSettings.getUser(), dataExplorerConnectionSettings.getPassword());
        this.influxDB.setDatabase(DataExplorerConfigurations.DATA_LAKE_DATABASE_NAME);
    }

    public void close() {
        this.influxDB.close();
    }

    public void write(Map<String, Object> data,
                      String measurement) {
        Point.Builder builder = Point.measurement(measurement)
                .time((Long) data.get("timestamp"), TimeUnit.MILLISECONDS);

        data.remove("timestamp");

        for (String key : data.keySet()) {
            if (data.get(key) instanceof Double || data.get(key) == null) {
                builder.addField(key, (Double) data.get(key));
            } else if (data.get(key) instanceof Integer) {
                builder.addField(key, (Integer) data.get(key));
            } else {
                builder.tag(key, (String) data.get(key));
            }
        }

        this.influxDB.write(builder.build());
    }

}
