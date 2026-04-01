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

package org.apache.streampipes.client.api;

import org.apache.streampipes.client.model.StreamPipesClientConfig;
import org.apache.streampipes.client.util.StreamPipesApiPath;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.model.shared.annotation.ExposedToScripts;

import java.util.List;
import java.util.Optional;

public class DataLakeMeasureApi extends AbstractTypedClientApi<DataLakeMeasure>
    implements IDataLakeMeasureApi {

  public DataLakeMeasureApi(StreamPipesClientConfig clientConfig) {
    super(clientConfig, DataLakeMeasure.class);
  }

  @Override
  @ExposedToScripts
  public Optional<DataLakeMeasure> get(String id) {
    return getSingle(getBaseResourcePath().addToPath(id));
  }

  @Override
  @ExposedToScripts
  public Optional<DataLakeMeasure> getByDatasetName(String datasetName) {
    return getSingle(getBaseResourcePath().addToPath("byName").addToPath(datasetName));
  }

  @Override
  @ExposedToScripts
  public List<DataLakeMeasure> all() {
    throw new IllegalArgumentException("Not yet implemented");
  }

  @Override
  @ExposedToScripts
  public void create(DataLakeMeasure element) {
    post(getBaseResourcePath(), element);
  }

  @Override
  @ExposedToScripts
  public void delete(String elementId) {
    throw new IllegalArgumentException("Not yet implemented");
  }

  @Override
  @ExposedToScripts
  public void update(DataLakeMeasure measure) {
    put(getBaseResourcePath().addToPath(measure.getElementId()), measure);
  }

  @Override
  protected StreamPipesApiPath getBaseResourcePath() {
    return StreamPipesApiPath.fromStreamPipesBasePath()
        .addToPath("api")
        .addToPath("v4")
        .addToPath("datalake")
        .addToPath("measure");
  }
}
