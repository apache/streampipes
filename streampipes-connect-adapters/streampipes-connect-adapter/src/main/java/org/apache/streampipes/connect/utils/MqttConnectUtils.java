/*
Copyright 2020 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.apache.streampipes.connect.utils;

import org.apache.streampipes.connect.protocol.stream.MqttConfig;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternative;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.apache.streampipes.sdk.helpers.Alternatives;
import org.apache.streampipes.sdk.helpers.Label;
import org.apache.streampipes.sdk.helpers.Labels;

public class MqttConnectUtils {

    /**
     * Keys of user configuration parameters
     */
    public static final String ACCESS_MODE = "access-mode";
    public static final String ANONYMOUS_ACCESS = "anonymous-alternative";
    public static final String USERNAME_ACCESS = "username-alternative";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    public static Label getAccessModeLabel() {
        return Labels.from(ACCESS_MODE, "Access Mode", "");
    }

    public static Label getBrokerUrlLabel() {
        return Labels.from("broker_url", "Broker URL",
                "Example: tcp://test-server.com:1883 (Protocol required. Port required)");
    }

    public static Label getTopicLabel() {
        return Labels.from("topic", "Topic","Example: test/topic");
    }

    public static StaticPropertyAlternative getAlternativesOne() {
        return Alternatives.from(Labels.from(ANONYMOUS_ACCESS, "Unauthenticated", ""));

    }

    public static StaticPropertyAlternative getAlternativesTwo() {
        return Alternatives.from(Labels.from(USERNAME_ACCESS, "Username/Password", ""),
                StaticProperties.group(Labels.from("username-group", "User Group", ""),
                        StaticProperties.stringFreeTextProperty(Labels.from(USERNAME,
                                "Username", "")),
                        StaticProperties.secretValue(Labels.from(PASSWORD,
                                "Password", ""))));

    }


//    public static StaticPropertyAlternative[] getAlternatives() {
//        StaticPropertyAlternative[] result = {
//                Alternatives.from(Labels.from(ANONYMOUS_ACCESS, "Unauthenticated", "")),
//                Alternatives.from(Labels.from(USERNAME_ACCESS, "Username/Password", ""),
//                        StaticProperties.group(Labels.from("username-group", "User Group", ""),
//                                StaticProperties.stringFreeTextProperty(Labels.from(USERNAME,
//                                        "Username", "")),
//                                StaticProperties.secretValue(Labels.from(PASSWORD,
//                                        "Password", ""))))
//        };
//        return result;
//
//    }

    public static MqttConfig getMqttConfig(StaticPropertyExtractor extractor) {
        return getMqttConfig(extractor, null);
    }

    public static MqttConfig getMqttConfig(StaticPropertyExtractor extractor, String topicInput) {
        MqttConfig mqttConfig;
        String brokerUrl = extractor.singleValueParameter("broker_url", String.class);

        String topic;
        if (topicInput == null) {
            topic = extractor.singleValueParameter("topic", String.class);
        } else {
            topic = topicInput;
        }

        String selectedAlternative = extractor.selectedAlternativeInternalId(ACCESS_MODE);

        if (selectedAlternative.equals(ANONYMOUS_ACCESS)) {
            mqttConfig = new MqttConfig(brokerUrl, topic);
        } else {
            String username = extractor.singleValueParameter(USERNAME, String.class);
            String password = extractor.secretValue(PASSWORD);
            mqttConfig = new MqttConfig(brokerUrl, topic, username, password);
        }

        return mqttConfig;
    }

}
