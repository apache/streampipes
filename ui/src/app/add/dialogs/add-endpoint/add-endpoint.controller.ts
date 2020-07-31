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

import {RestApi} from "../../../services/rest-api.service";

export class AddEndpointController {

    $mdDialog: any;
    RestApi: RestApi;
    rdfEndpoints: any;
    addSelected: any;
    newEndpoint: any;
    getEndpointItems: any;

    constructor($mdDialog, RestApi, getEndpointItems) {
        this.$mdDialog = $mdDialog;
        this.RestApi = RestApi;
        this.rdfEndpoints = [];
        this.addSelected = false;
        this.newEndpoint = {};
        this.getEndpointItems = getEndpointItems;
    }

    $onInit() {
        this.loadRdfEndpoints();
    }

    showAddInput() {
        this.addSelected = true;
    }

    loadRdfEndpoints() {
        this.RestApi.getRdfEndpoints()
            .then(rdfEndpoints => {
                this.rdfEndpoints = rdfEndpoints.data;
            });
    }

    addRdfEndpoint(rdfEndpoint) {
        this.RestApi.addRdfEndpoint(rdfEndpoint)
            .then(message => {
                this.loadRdfEndpoints();
                this.getEndpointItems();
            });
    }

    removeRdfEndpoint(rdfEndpointId) {
        this.RestApi.removeRdfEndpoint(rdfEndpointId)
            .then(message => {
                this.loadRdfEndpoints();
                this.getEndpointItems();
            });
    }

    hide() {
        this.$mdDialog.hide();
    }

    cancel() {
        this.$mdDialog.cancel();
    };

}

AddEndpointController.$inject = ['$mdDialog', 'RestApi', 'getEndpointItems'];