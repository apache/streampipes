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

package org.apache.streampipes.service.core;

import org.apache.streampipes.dataexplorer.management.DataExplorerDispatcher;
import org.apache.streampipes.dataexplorer.management.DatasetServices;
import org.apache.streampipes.manager.pipeline.update.ChartSchemaUpdateCoordinator;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.storage.api.explorer.IChartStorage;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Assembles database-independent application services outside REST resources and provider modules. */
@Configuration
public class DatasetQueryConfiguration {
  @Bean(destroyMethod = "close")
  public DatasetServices datasetServices(IChartStorage charts, SpResourceManager resources) {
    var dispatcher = new DataExplorerDispatcher();
    var catalog = dispatcher.getSchemaManagement(new ChartSchemaUpdateCoordinator(charts),
        resources.managePermissions().getDb(), resources.manageDataLakeMeasures().getDb());
    return dispatcher.getDatasetServices(catalog);
  }
}
