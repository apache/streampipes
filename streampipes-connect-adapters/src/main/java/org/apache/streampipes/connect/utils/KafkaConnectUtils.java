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

package org.apache.streampipes.connect.utils;

import org.apache.streampipes.connect.protocol.stream.KafkaConfig;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternative;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.apache.streampipes.sdk.helpers.Alternatives;
import org.apache.streampipes.sdk.helpers.Label;
import org.apache.streampipes.sdk.helpers.Labels;

public class KafkaConnectUtils {

    private static final String TOPIC_KEY = "topic";
    private static final String HOST_KEY = "host";
    private static final String PORT_KEY = "port";
    private static final String ACCESS_MODE = "access-mode";
    private static final String ANONYMOUS_ACCESS = "anonymous-alternative";
    private static final String USERNAME_ACCESS = "username-alternative";
    private static final String USERNAME_GROUP = "username-group";
    private static final String USERNAME_KEY = "username";

    public static String getUsernameKey() {
        return USERNAME_KEY;
    }

    public static String getPasswordKey() {
        return PASSWORD_KEY;
    }

    private static final String PASSWORD_KEY = "password";

    public static String getAccessModeKey() {
        return ACCESS_MODE;
    }

    public static Label getTopicLabel() {
        return Labels.withId(TOPIC_KEY);
    }

    public static Label getHostLabel() {
        return Labels.withId(HOST_KEY);
    }

    public static String getHostKey() {
        return HOST_KEY;
    }

    public static String getPortKey() {
        return PORT_KEY;
    }

    public static Label getPortLabel() {
        return Labels.withId(PORT_KEY);
    }

    public static Label getAccessModeLabel() {
        return Labels.withId(ACCESS_MODE);
    }

    public static String getSaslAccessKey() {
        return USERNAME_ACCESS;
    }

    public static String getAnonymousAccessKey() {
        return ANONYMOUS_ACCESS;
    }

    public static StaticPropertyAlternative getAlternativesOne() {
        return Alternatives.from(Labels.withId(ANONYMOUS_ACCESS));
    }

    public static StaticPropertyAlternative getAlternativesTwo() {
        return Alternatives.from(Labels.withId(USERNAME_ACCESS),
                StaticProperties.group(Labels.withId(USERNAME_GROUP),
                        StaticProperties.stringFreeTextProperty(Labels.withId(USERNAME_KEY)),
                        StaticProperties.secretValue(Labels.withId(PASSWORD_KEY))));
    }

    public static KafkaConfig getConfig(StaticPropertyExtractor extractor) {
        String brokerUrl = extractor.singleValueParameter(HOST_KEY, String.class);
        String topic = extractor.selectedSingleValue(TOPIC_KEY, String.class);
        Integer port = extractor.singleValueParameter(PORT_KEY, Integer.class);
        String authentication = extractor.selectedAlternativeInternalId(ACCESS_MODE);
        if (authentication.equals(USERNAME_ACCESS)) {
            String password = extractor.secretValue(PASSWORD_KEY);
            String username = extractor.singleValueParameter(USERNAME_KEY, String.class);
            return new KafkaConfig(brokerUrl, port, topic, authentication, username, password);
        }
        else {
            return new KafkaConfig(brokerUrl, port, topic, authentication, null, null);
        }
    }
}
