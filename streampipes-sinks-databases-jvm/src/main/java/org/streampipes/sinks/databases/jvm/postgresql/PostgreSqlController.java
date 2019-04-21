/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.streampipes.sinks.databases.jvm.postgresql;

import org.streampipes.model.DataSinkType;
import org.streampipes.model.graph.DataSinkDescription;
import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.sdk.builder.DataSinkBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.DataSinkParameterExtractor;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.Locales;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.sdk.utils.Assets;
import org.streampipes.wrapper.standalone.ConfiguredEventSink;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventSinkDeclarer;

public class PostgreSqlController extends StandaloneEventSinkDeclarer<PostgreSqlParameters> {

  private static final String DATABASE_HOST_KEY = "db_host";
  private static final String DATABASE_PORT_KEY = "db_port";
  private static final String DATABASE_NAME_KEY = "db_name";
  private static final String DATABASE_TABLE_KEY = "db_table";
  private static final String DATABASE_USER_KEY = "db_user";
  private static final String DATABASE_PASSWORD_KEY = "db_password";

  @Override
  public DataSinkDescription declareModel() {
    //TODO: Replace Icon, insert defaults (for the port)
    return DataSinkBuilder.create("org.streampipes.sinks.databases.jvm.postgresql")
            .withLocales(Locales.EN)
            .withAssets(Assets.DOCUMENTATION, Assets.ICON)
            .category(DataSinkType.STORAGE)
            .requiredStream(StreamRequirementsBuilder.create()
                    .requiredProperty(EpRequirements.anyProperty())
                    .build())
            .supportedFormats(SupportedFormats.jsonFormat())
            .supportedProtocols(SupportedProtocols.kafka(), SupportedProtocols.jms())
            .requiredTextParameter(Labels.withId(DATABASE_HOST_KEY))
            .requiredIntegerParameter(Labels.withId(DATABASE_PORT_KEY), 5432)
            .requiredTextParameter(Labels.withId(DATABASE_NAME_KEY))
            .requiredTextParameter(Labels.withId(DATABASE_TABLE_KEY))
            .requiredTextParameter(Labels.withId(DATABASE_USER_KEY))
            .requiredTextParameter(Labels.withId(DATABASE_PASSWORD_KEY))
            .build();
  }

  @Override
  public ConfiguredEventSink<PostgreSqlParameters> onInvocation(DataSinkInvocation graph,
                                                                DataSinkParameterExtractor extractor) {

    String hostname = extractor.singleValueParameter(DATABASE_HOST_KEY, String.class);
    Integer port = extractor.singleValueParameter(DATABASE_PORT_KEY, Integer.class);
    String dbName = extractor.singleValueParameter(DATABASE_NAME_KEY, String.class);
    String tableName = extractor.singleValueParameter(DATABASE_TABLE_KEY, String.class);
    String user = extractor.singleValueParameter(DATABASE_USER_KEY, String.class);
    String password = extractor.singleValueParameter(DATABASE_PASSWORD_KEY, String.class);

    PostgreSqlParameters params = new PostgreSqlParameters(graph,
            hostname,
            port,
            dbName,
            tableName,
            user,
            password);

    return new ConfiguredEventSink<>(params, PostgreSql::new);
  }
}
