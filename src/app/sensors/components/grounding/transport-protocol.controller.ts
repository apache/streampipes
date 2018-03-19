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