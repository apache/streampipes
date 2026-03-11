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

package org.apache.streampipes.sdk.builder.sink;

import org.apache.streampipes.extensions.api.assets.AssetResolver;
import org.apache.streampipes.extensions.api.pe.IStreamPipesDataSink;
import org.apache.streampipes.extensions.api.pe.config.IDataSinkConfiguration;
import org.apache.streampipes.model.graph.DataSinkDescription;

import java.util.function.Supplier;

public class DataSinkConfiguration implements IDataSinkConfiguration {

  private final Supplier<IStreamPipesDataSink> supplier;
  private final DataSinkDescription dataSinkDescription;
  private final AssetResolver assetResolver;

  public static DataSinkConfiguration create(Supplier<IStreamPipesDataSink> supplier,
                                             DataSinkDescription dataSinkDescription) {
    return new DataSinkConfiguration(supplier, dataSinkDescription, null);
  }

  public static DataSinkConfiguration create(Supplier<IStreamPipesDataSink> supplier,
                                             DataSinkDescription dataSinkDescription,
                                             AssetResolver assetResolver) {
    return new DataSinkConfiguration(supplier, dataSinkDescription, assetResolver);
  }

  private DataSinkConfiguration(Supplier<IStreamPipesDataSink> supplier,
                                DataSinkDescription dataSinkDescription,
                                AssetResolver assetResolver) {
    this.supplier = supplier;
    this.dataSinkDescription = dataSinkDescription;
    this.assetResolver = assetResolver;
  }

  @Override
  public DataSinkDescription getDescription() {
    return dataSinkDescription;
  }

  @Override
  public Supplier<IStreamPipesDataSink> getSupplier() {
    return supplier;
  }

  @Override
  public AssetResolver getAssetResolver() {
    return assetResolver == null ? IDataSinkConfiguration.super.getAssetResolver() : assetResolver;
  }
}
