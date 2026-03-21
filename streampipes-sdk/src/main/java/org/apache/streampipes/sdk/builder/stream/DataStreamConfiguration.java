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

package org.apache.streampipes.sdk.builder.stream;

import org.apache.streampipes.extensions.api.assets.AssetResolver;
import org.apache.streampipes.extensions.api.pe.IStreamPipesDataStream;
import org.apache.streampipes.extensions.api.pe.config.IDataStreamConfiguration;
import org.apache.streampipes.model.SpDataStream;

import java.util.function.Supplier;

public class DataStreamConfiguration implements IDataStreamConfiguration {

  private final Supplier<IStreamPipesDataStream> supplier;
  private final SpDataStream dataStream;
  private final AssetResolver assetResolver;

  public static DataStreamConfiguration create(Supplier<IStreamPipesDataStream> supplier,
                                               SpDataStream dataStream) {
    return new DataStreamConfiguration(supplier, dataStream, null);
  }

  public static DataStreamConfiguration create(Supplier<IStreamPipesDataStream> supplier,
                                               SpDataStream dataStream,
                                               AssetResolver assetResolver) {
    return new DataStreamConfiguration(supplier, dataStream, assetResolver);
  }

  public DataStreamConfiguration(Supplier<IStreamPipesDataStream> supplier,
                                 SpDataStream dataStream,
                                 AssetResolver assetResolver) {
    this.supplier = supplier;
    this.dataStream = dataStream;
    this.assetResolver = assetResolver;
  }

  @Override
  public SpDataStream getDescription() {
    return dataStream;
  }

  @Override
  public Supplier<IStreamPipesDataStream> getSupplier() {
    return supplier;
  }

  @Override
  public AssetResolver getAssetResolver() {
    return assetResolver == null ? IDataStreamConfiguration.super.getAssetResolver() : assetResolver;
  }
}
