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

package org.apache.streampipes.sdk.builder.processor;

import org.apache.streampipes.extensions.api.pe.IStreamPipesDataProcessor;
import org.apache.streampipes.extensions.api.pe.config.IDataProcessorConfiguration;
import org.apache.streampipes.model.graph.DataProcessorDescription;

import java.util.function.Supplier;

public class DataProcessorConfiguration implements IDataProcessorConfiguration {

  private final Supplier<IStreamPipesDataProcessor> supplier;
  private final DataProcessorDescription dataProcessorDescription;

  public static DataProcessorConfiguration create(Supplier<IStreamPipesDataProcessor> supplier,
                                                  DataProcessorDescription dataProcessorDescription) {
    return new DataProcessorConfiguration(supplier, dataProcessorDescription);
  }

  private DataProcessorConfiguration(Supplier<IStreamPipesDataProcessor> supplier,
                                    DataProcessorDescription dataProcessorDescription) {
    this.supplier = supplier;
    this.dataProcessorDescription = dataProcessorDescription;
  }

  @Override
  public DataProcessorDescription getDescription() {
    return dataProcessorDescription;
  }

  @Override
  public Supplier<IStreamPipesDataProcessor> getSupplier() {
    return supplier;
  }
}
