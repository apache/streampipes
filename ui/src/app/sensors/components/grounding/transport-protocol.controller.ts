/*
 * Copyright 2019 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

export class TransportProtocolController {

    availableTransportProtocols: any;
    selectedTransportProtocol: any;

    constructor() {
        this.availableTransportProtocols = [{
            "id": "kafka",
            "name": "Apache Kafka",
            "type": "org.streampipes.model.grounding.KafkaTransportProtocol"
        },
            {"id": "jms", "name": "JMS", "type": "org.streampipes.model.grounding.JmsTransportProtocol"},
            {"id": "mqtt", "name": "MQTT", "type": "org.streampipes.model.grounding.MqttTransportProtocol"}];
        this.selectedTransportProtocol = "";
    }
    
    addTransportProtocol(transportProtocols) {
        if (this.selectedTransportProtocol == this.availableTransportProtocols[0].id) this.addKafkaProtocol(transportProtocols);
        else this.addJmsProtocol(transportProtocols);
    }

    addKafkaProtocol(transportProtocols) {
        transportProtocols.push({
            "type": this.availableTransportProtocols[0].type,
            "properties": {
                "zookeeperHost": "",
                "zookeeperPort": 2181,
                "brokerHostname": "",
                "kafkaPort": 9092,
                "topicName": ""
            }
        });
    }

    addJmsProtocol(transportProtocols) {
        transportProtocols.push({
            "type": this.availableTransportProtocols[1].type,
            "properties": {"brokerHostname": "", "port": 61616, "topicName": ""}
        });
    }

    removeTransportProtocol(transportProtocols) {
        transportProtocols.splice(0, 1);
    }

    findProtocol(transportProtocol) {
        if (transportProtocol == undefined) return "";
        else {
            if (transportProtocol.type == this.availableTransportProtocols[0].type) return this.availableTransportProtocols[0].name;
            else return this.availableTransportProtocols[1].name;
        }
    }
}