/*
 * Copyright 2019 FZI Forschungszentrum Informatik
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
 *
 */

package org.streampipes.connect;

import org.streampipes.connect.adapters.opcua.OpcUaAdapter;
import org.streampipes.connect.init.AdapterDeclarerSingleton;
import org.streampipes.connect.init.AdapterWorkerContainer;
import org.streampipes.connect.protocol.stream.KafkaProtocol;
import org.streampipes.connect.protocol.stream.MqttProtocol;

public class Init extends AdapterWorkerContainer {

    public static void main(String[] args) {
        AdapterDeclarerSingleton
                .getInstance()
                .add(new KafkaProtocol())
                .add(new MqttProtocol())
                .add(new OpcUaAdapter());

        new Init().init("http://localhost:8098", "http://localhost:8099");

    }
}
