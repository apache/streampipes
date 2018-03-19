import * as angular from 'angular';

export class SupportedGroundingController {

    transportFormatTypesThrift: any;
    transportFormatTypesJson: any;
    thriftFormat: any;
    jsonFormat: any;
    kafkaClass: any;
    jmsClass: any;
    grounding: any;

    constructor() {
        this.transportFormatTypesThrift = ["http://sepa.event-processing.org/sepa#TransportFormat", "http://www.w3.org/2000/01/rdf-schema#Resource", "http://sepa.event-processing.org/sepa#thrift"];
        this.transportFormatTypesJson = ["http://sepa.event-processing.org/sepa#TransportFormat", "http://www.w3.org/2000/01/rdf-schema#Resource", "http://sepa.event-processing.org/sepa#json"];

        this.thriftFormat = "http://sepa.event-processing.org/sepa#thrift";
        this.jsonFormat = "http://sepa.event-processing.org/sepa#json";

        this.kafkaClass = "org.streampipes.model.grounding.KafkaTransportProtocol";
        this.jmsClass = "org.streampipes.model.grounding.JmsTransportProtocol";
    }


    kafkaSelected(transportProtocols) {
        return this.brokerSelected(transportProtocols, this.kafkaClass);
    }

    jmsSelected(transportProtocols) {
        return this.brokerSelected(transportProtocols, this.jmsClass);
    }

    jsonSelected(transportFormats) {
        return this.formatSelected(transportFormats, this.jsonFormat);
    }

    thriftSelected(transportFormats) {
        return this.formatSelected(transportFormats, this.thriftFormat);
    }

    brokerSelected(transportProtocols, protocolClass) {
        var selected = false;
        angular.forEach(transportProtocols, protocol => {
            if (protocol.type == protocolClass) selected = true;
        });
        return selected;
    }

    formatSelected(transportFormats, formatClass) {
        var selected = false;
        angular.forEach(transportFormats, format => {
            angular.forEach(format.rdfType, function (type) {
                if (type == formatClass) selected = true;
            })
        });
        return selected;
    }

    toggleKafka(transportProtocols) {
        if (this.kafkaSelected(transportProtocols)) {
            this.removeBroker(transportProtocols, this.kafkaClass);
        }
        else {
            this.checkGrounding();
            if (transportProtocols == undefined) this.grounding.transportProtocols = [];
            this.grounding.transportProtocols.push({
                "type": this.kafkaClass,
                "properties": {"kafkaPort": 0, "zookeeperPort": 0}
            })
        }
    }

    toggleJms(transportProtocols) {
        if (this.jmsSelected(transportProtocols)) {
            this.removeBroker(transportProtocols, this.jmsClass);
        }
        else {
            this.checkGrounding();
            if (transportProtocols == undefined) this.grounding.transportProtocols = [];
            this.grounding.transportProtocols.push({"type": this.jmsClass, "properties": {"port": 0}})
        }
    }

    removeBroker(transportProtocols, protocolClass) {
        angular.forEach(transportProtocols, protocol => {
            if (protocol.type == protocolClass) transportProtocols.splice(transportProtocols.indexOf(protocol), 1);
        });
    }

    toggleJson(transportFormats) {
        if (this.jsonSelected(transportFormats)) {
            this.removeFormat(transportFormats, this.jsonFormat);
        }
        else {
            this.checkGrounding();
            if (transportFormats == undefined) this.grounding.transportFormats = [];
            this.grounding.transportFormats.push({"rdfType": this.transportFormatTypesJson});
        }
    }

    toggleThrift(transportFormats) {
        if (this.thriftSelected(transportFormats)) {
            this.removeFormat(transportFormats, this.thriftFormat);
        }
        else {
            this.checkGrounding();
            if (transportFormats == undefined) this.grounding.transportFormats = [];
            this.grounding.transportFormats.push({"rdfType": this.transportFormatTypesThrift});
        }
    }

    checkGrounding() {
        if (this.grounding == undefined) this.grounding = {};
    }


    removeFormat(transportFormats, formatClass) {
        angular.forEach(transportFormats, format => {
            angular.forEach(format.rdfType, type => {
                if (type == formatClass) transportFormats.splice(transportFormats.indexOf(format), 1);
            })
        });
    }
}