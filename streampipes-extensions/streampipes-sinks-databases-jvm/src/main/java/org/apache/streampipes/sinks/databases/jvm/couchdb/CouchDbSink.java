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

package org.apache.streampipes.sinks.databases.jvm.couchdb;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.couchdb.client.CouchServer;
import org.apache.streampipes.couchdb.client.DatabaseOptions;
import org.apache.streampipes.couchdb.client.TypedCollection;
import org.apache.streampipes.couchdb.client.codec.GsonCodec;
import org.apache.streampipes.extensions.api.pe.IStreamPipesDataSink;
import org.apache.streampipes.extensions.api.pe.config.IDataSinkConfiguration;
import org.apache.streampipes.extensions.api.pe.context.EventSinkRuntimeContext;
import org.apache.streampipes.extensions.api.pe.param.IDataSinkParameters;
import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.extensions.ExtensionAssetType;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.EventConverter;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.builder.sink.DataSinkConfiguration;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;

import com.google.gson.Gson;

import java.time.Duration;

public class CouchDbSink implements IStreamPipesDataSink {

  private static final String DATABASE_HOST_KEY = "db_host";
  private static final String DATABASE_PORT_KEY = "db_port";
  private static final String DATABASE_NAME_KEY = "db_name";
  private static final String DATABASE_USER_KEY = "db_user";
  private static final String DATABASE_PASSORD_KEY = "db_password";

  // The database is external and may be further away than the internal CouchDB.
  private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(10);

  private CouchServer couchServer;
  private TypedCollection<Object> documents;

  @Override
  public IDataSinkConfiguration declareConfig() {
    return DataSinkConfiguration.create(
        CouchDbSink::new,
        DataSinkBuilder.create("org.apache.streampipes.sinks.databases.jvm.couchdb", 0)
            .withLocales(Locales.EN)
            .withAssets(ExtensionAssetType.DOCUMENTATION, ExtensionAssetType.ICON)
            .category(DataSinkType.DATABASE)
            .requiredStream(StreamRequirementsBuilder
                .create()
                .requiredProperty(EpRequirements.anyProperty())
                .build())
            .requiredTextParameter(Labels.withId(DATABASE_HOST_KEY))
            .requiredIntegerParameter(Labels.withId(DATABASE_PORT_KEY))
            .requiredTextParameter(Labels.withId(DATABASE_NAME_KEY))
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

    //TODO: Use this after optional parameters implementation
    //String user = extractor.singleValueParameter(DATABASE_USER_KEY, String.class);
    //String password = extractor.singleValueParameter(DATABASE_PASSORD_KEY, String.class);

    String user = null;
    String password = null;

    this.couchServer = CouchServer.builder()
        .url("http", hostname, port)
        .credentials(user, password)
        .connectTimeout(CONNECT_TIMEOUT)
        .build();
    var database = couchServer.database(dbName, DatabaseOptions.createIfMissing());
    database.create();
    // Gson keeps the stored format: fields without a value are left out.
    this.documents = database.typed(Object.class, GsonCodec.of(new Gson()));
  }

  @Override
  public void onEvent(Event event) throws SpRuntimeException {
    documents.create(new EventConverter(event).toInputEventMap());
  }

  @Override
  public void onPipelineStopped() {
    this.couchServer.close();
  }
}
