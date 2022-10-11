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

import org.apache.streampipes.client.StreamPipesClient;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.dataexplorer.commons.image.ImageStore;
import org.apache.streampipes.dataexplorer.commons.influx.InfluxStore;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.svcdiscovery.api.SpConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class TimeSeriesStore {

    private static final Logger LOG = LoggerFactory.getLogger(TimeSeriesStore.class);

    private ImageStore imageStore;
    private final InfluxStore influxStore;


    public TimeSeriesStore(SpConfig config,
                           StreamPipesClient client,
                           DataLakeMeasure measure,
                           boolean enableImageStore) {

        measure = DataExplorerUtils.sanitizeAndRegisterAtDataLake(client, measure);

        if (enableImageStore) {
            // TODO check if event properties are replaces correctly
            this.imageStore = new ImageStore(measure, config);
        }

        this.influxStore = new InfluxStore(measure, config);

    }

    public boolean onEvent(Event event) throws SpRuntimeException {
        // Store all images in image store and replace image with internal id
        if (imageStore != null) {
            this.imageStore.onEvent(event);
        }

        // Store event in time series database
        this.influxStore.onEvent(event);

        return true;
    }


    public boolean alterRetentionTime(DataLakeMeasure dataLakeMeasure) {
        return true;
    }

    public void close() throws SpRuntimeException  {
        if (imageStore != null) {
            try {
                this.imageStore.close();
            } catch (IOException e) {
                LOG.error("Could not close couchDB connection");
                throw new SpRuntimeException(e);
            }
        }

        this.influxStore.close();
    }
}
