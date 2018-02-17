export class TransportFormatController {

    constructor() {
        this.availableTransportFormats = [{
            "id": "thrift",
            "name": "Thrift Simple Event Format",
            "rdf": ["http://www.w3.org/2000/01/rdf-schema#Resource", "http://sepa.event-processing.org/sepa#TransportFormat", "http://sepa.event-processing.org/sepa#thrift"]
        },
            {
                "id": "json",
                "name": "Flat JSON Format",
                "rdf": ["http://www.w3.org/2000/01/rdf-schema#Resource", "http://sepa.event-processing.org/sepa#TransportFormat", "http://sepa.event-processing.org/sepa#json"]
            },
            {
                "id": "xml",
                "name": "XML",
                "rdf": ["http://www.w3.org/2000/01/rdf-schema#Resource", "http://sepa.event-processing.org/sepa#TransportFormat", "http://sepa.event-processing.org/sepa#xml"]
            }];
        this.selectedTransportFormat = "";

    }


    getFormat() {
        if (this.selectedTransportFormat == 'thrift') return this.availableTransportFormats[0].rdf;
        else return this.availableTransportFormats[1].rdf;
    }

    addTransportFormat(transportFormats) {
        transportFormats.push({"rdfType": this.getFormat()});
    }

    removeTransportFormat(transportFormats) {
        transportFormats.splice(0, 1);
    }

    findFormat(transportFormat) {
        if (transportFormat == undefined) return "";
        else {
            if (transportFormat.rdfType.indexOf(this.availableTransportFormats[0].rdf[2]) != -1) return this.availableTransportFormats[0].name;
            else return this.availableTransportFormats[1].name;
        }
    }
}