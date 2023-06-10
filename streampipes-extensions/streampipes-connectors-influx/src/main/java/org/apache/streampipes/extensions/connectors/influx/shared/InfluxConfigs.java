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

package org.apache.streampipes.extensions.connectors.influx.shared;

import org.apache.streampipes.dataexplorer.commons.influx.InfluxConnectionSettings;
import org.apache.streampipes.extensions.api.extractor.IParameterExtractor;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.builder.AbstractConfigurablePipelineElementBuilder;
import org.apache.streampipes.sdk.helpers.Alternatives;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.helpers.Tuple2;

import static org.apache.streampipes.extensions.connectors.influx.shared.InfluxKeys.DATABASE_AUTHENTICATION;
import static org.apache.streampipes.extensions.connectors.influx.shared.InfluxKeys.DATABASE_HOST_KEY;
import static org.apache.streampipes.extensions.connectors.influx.shared.InfluxKeys.DATABASE_MEASUREMENT_KEY;
import static org.apache.streampipes.extensions.connectors.influx.shared.InfluxKeys.DATABASE_NAME_KEY;
import static org.apache.streampipes.extensions.connectors.influx.shared.InfluxKeys.DATABASE_PASSWORD_KEY;
import static org.apache.streampipes.extensions.connectors.influx.shared.InfluxKeys.DATABASE_PORT_KEY;
import static org.apache.streampipes.extensions.connectors.influx.shared.InfluxKeys.DATABASE_PROTOCOL;
import static org.apache.streampipes.extensions.connectors.influx.shared.InfluxKeys.DATABASE_TOKEN_ALT;
import static org.apache.streampipes.extensions.connectors.influx.shared.InfluxKeys.DATABASE_TOKEN_KEY;
import static org.apache.streampipes.extensions.connectors.influx.shared.InfluxKeys.DATABASE_USER_KEY;
import static org.apache.streampipes.extensions.connectors.influx.shared.InfluxKeys.USERNAME_GROUP_KEY;
import static org.apache.streampipes.extensions.connectors.influx.shared.InfluxKeys.USERNAME_PASSWORD_ALT;

public class InfluxConfigs {

  public static void appendSharedInfluxConfig(AbstractConfigurablePipelineElementBuilder<?, ?> builder) {
    builder
        .requiredSingleValueSelection(Labels.withId(DATABASE_PROTOCOL),
            Options.from(new Tuple2<>("HTTP", "http"), new Tuple2<>("HTTPS", "https")))
        .requiredTextParameter(Labels.withId(DATABASE_HOST_KEY))
        .requiredIntegerParameter(Labels.withId(DATABASE_PORT_KEY), 8086)
        .requiredTextParameter(Labels.withId(DATABASE_NAME_KEY))
        .requiredTextParameter(Labels.withId(DATABASE_MEASUREMENT_KEY))
        .requiredAlternatives(Labels.withId(DATABASE_AUTHENTICATION),
            Alternatives.from(Labels.withId(DATABASE_TOKEN_ALT),
                StaticProperties.secretValue(Labels.withId(DATABASE_TOKEN_KEY))
            ),
            Alternatives.from(Labels.withId(USERNAME_PASSWORD_ALT),
                StaticProperties.group(
                    Labels.withId(USERNAME_GROUP_KEY),
                    StaticProperties.stringFreeTextProperty(
                        Labels.withId(DATABASE_USER_KEY)),
                    StaticProperties.secretValue(Labels.withId(DATABASE_PASSWORD_KEY))
                ))
        );
  }

  public static InfluxConnectionSettings fromExtractor(IParameterExtractor<?> extractor) {
    String protocol = extractor.selectedSingleValueInternalName(DATABASE_PROTOCOL, String.class);
    String hostname = extractor.singleValueParameter(DATABASE_HOST_KEY, String.class);
    Integer port = extractor.singleValueParameter(DATABASE_PORT_KEY, Integer.class);
    String dbName = extractor.singleValueParameter(DATABASE_NAME_KEY, String.class);

    String authAlternative = extractor.selectedAlternativeInternalId(DATABASE_AUTHENTICATION);

    if (authAlternative.equals(USERNAME_PASSWORD_ALT)) {
      String user = extractor.singleValueParameter(DATABASE_USER_KEY, String.class);
      String password = extractor.secretValue(DATABASE_PASSWORD_KEY);
      return InfluxConnectionSettings.from(protocol, hostname, port, dbName, user, password);
    } else {
      String token = extractor.secretValue(DATABASE_TOKEN_KEY);
      return InfluxConnectionSettings.from(protocol, hostname, port, dbName, token);
    }
  }
}
