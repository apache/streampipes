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

package org.apache.streampipes.sinks.databases.jvm.mysql;

import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.DataSinkParameterExtractor;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventSink;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventSinkDeclarer;

public class MysqlController extends StandaloneEventSinkDeclarer<MysqlParameters> {

    private static final String HOST_KEY = "host";
    private static final String USER_KEY = "user";
    private static final String PASSWORD_KEY = "password";
    private static final String DB_KEY = "db";
    private static final String TABLE_KEY = "table";
    private static final String PORT_KEY = "port";

    @Override
    public DataSinkDescription declareModel() {
        return DataSinkBuilder.create("org.apache.streampipes.sinks.databases.jvm.mysql")
                .withLocales(Locales.EN)
                .withAssets(Assets.DOCUMENTATION, Assets.ICON)
                .requiredStream(StreamRequirementsBuilder
                        .create()
                        .requiredProperty(EpRequirements.anyProperty())
                        .build())
                .requiredTextParameter(Labels.withId(HOST_KEY), false, false)
                .requiredIntegerParameter(Labels.withId(PORT_KEY), 3306)
                .requiredTextParameter(Labels.withId(USER_KEY), false, false)
                .requiredSecret(Labels.withId(PASSWORD_KEY))
                .requiredTextParameter(Labels.withId(DB_KEY), false, false)
                .requiredTextParameter(Labels.withId(TABLE_KEY), false, false)
                .build();
    }

    @Override
    public ConfiguredEventSink<MysqlParameters> onInvocation(DataSinkInvocation graph,
                                                             DataSinkParameterExtractor extractor) {

        String host = extractor.singleValueParameter(HOST_KEY, String.class);
        String user = extractor.singleValueParameter(USER_KEY, String.class);
        String password = extractor.secretValue(PASSWORD_KEY);
        String db = extractor.singleValueParameter(DB_KEY, String.class);
        String table = extractor.singleValueParameter(TABLE_KEY, String.class);
        Integer port = extractor.singleValueParameter(PORT_KEY, Integer.class);

        // SSL connection is not yet implemented for MySQL client
        MysqlParameters params = new MysqlParameters(graph, host, user, password, db, table, port, false);
        return new ConfiguredEventSink<>(params, Mysql::new);
    }

}
