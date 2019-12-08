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

export class TransportFormatController {

    availableTransportFormats: any;
    selectedTransportFormat: any;

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