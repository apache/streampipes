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

package org.apache.streampipes.sinks.databases.jvm.postgresql;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.IStreamPipesDataSink;
import org.apache.streampipes.extensions.api.pe.config.IDataSinkConfiguration;
import org.apache.streampipes.extensions.api.pe.context.EventSinkRuntimeContext;
import org.apache.streampipes.extensions.api.pe.param.IDataSinkParameters;
import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.extensions.ExtensionAssetType;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.builder.sink.DataSinkConfiguration;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.helpers.Tuple2;

public class PostgreSqlSink implements IStreamPipesDataSink {

  public static final String ID = "org.apache.streampipes.sinks.databases.jvm.postgresql";
  public static final String APPEND_TO_EXISTING_KEY = "append_to_existing";
  public static final String BATCH_SIZE_KEY = "batch_size";

  private static final String DATABASE_HOST_KEY = "db_host";
  private static final String DATABASE_PORT_KEY = "db_port";
  private static final String DATABASE_NAME_KEY = "db_name";
  private static final String DATABASE_TABLE_KEY = "db_table";
  private static final String DATABASE_USER_KEY = "db_user";
  private static final String DATABASE_PASSWORD_KEY = "db_password";
  private static final String SSL_MODE = "ssl_mode";
  private static final String SSL_ENABLED = "ssl_enabled";
  private static final String SSL_DISABLED = "ssl_disabled";

  private PostgreSql postgreSql;

  @Override
  public IDataSinkConfiguration declareConfig() {
    return DataSinkConfiguration.create(
        PostgreSqlSink::new,
        DataSinkBuilder
            .create(ID, 1)
            .withLocales(Locales.EN)
            .withAssets(ExtensionAssetType.DOCUMENTATION, ExtensionAssetType.ICON)
            .category(DataSinkType.DATABASE)
            .requiredStream(StreamRequirementsBuilder.create()
                .requiredProperty(EpRequirements.anyProperty())
                .build())
            .requiredTextParameter(Labels.withId(DATABASE_HOST_KEY))
            .requiredIntegerParameter(Labels.withId(DATABASE_PORT_KEY), 5432)
            .requiredTextParameter(Labels.withId(DATABASE_NAME_KEY))
            .requiredTextParameter(Labels.withId(DATABASE_TABLE_KEY))
            .requiredSlideToggle(Labels.withId(APPEND_TO_EXISTING_KEY), false)
            .requiredTextParameter(Labels.withId(DATABASE_USER_KEY))
            .requiredSecret(Labels.withId(DATABASE_PASSWORD_KEY))
            .requiredSingleValueSelection(Labels.withId(SSL_MODE),
                Options.from(
                    new Tuple2<>("Yes", SSL_ENABLED),
                    new Tuple2<>("No", SSL_DISABLED)))
            .requiredIntegerParameter(Labels.withId(BATCH_SIZE_KEY), 1)
            .build()
    );
  }

  @Override
  public void onPipelineStarted(
      IDataSinkParameters parameters,
      EventSinkRuntimeContext runtimeContext) {
    var extractor = parameters.extractor();
    String hostname = extractor.singleValueParameter(DATABASE_HOST_KEY, String.class);
    Integer port = extractor.singleValueParameter(DATABASE_PORT_KEY, Integer.class);
    String dbName = extractor.singleValueParameter(DATABASE_NAME_KEY, String.class);
    String tableName = extractor.singleValueParameter(DATABASE_TABLE_KEY, String.class);
    String user = extractor.singleValueParameter(DATABASE_USER_KEY, String.class);
    String password = extractor.secretValue(DATABASE_PASSWORD_KEY);
    String sslSelection = extractor.selectedSingleValueInternalName(SSL_MODE, String.class);
    boolean appendToExisting = extractor.slideToggleValue(APPEND_TO_EXISTING_KEY);
    Integer batchSize = extractor.singleValueParameter(BATCH_SIZE_KEY, Integer.class);
    if (batchSize == null || batchSize < 1) {
      throw new SpRuntimeException("Batch size must be at least 1, but was '" + batchSize
          + "'. Use 1 to write each event immediately, or a higher value to write events in batches.");
    }

    PostgreSqlParameters params = new PostgreSqlParameters(
        parameters.getModel(),
        hostname,
        port,
        dbName,
        tableName,
        user,
        password,
        sslSelection.equals(SSL_ENABLED),
        appendToExisting,
        batchSize);

    this.postgreSql = new PostgreSql();
    postgreSql.onInvocation(params);
  }

  @Override
  public void onEvent(Event event) throws SpRuntimeException {
    postgreSql.onEvent(event);
  }

  @Override
  public void onPipelineStopped() {
    postgreSql.onDetach();
  }
}
