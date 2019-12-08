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

import * as angular from 'angular';

export class TopicSelectionDialog {

    $mdDialog: any;
    RestApi: any;
    streamDescription: any;
    finished: any;
    platformIdMappings: any;
    locationIdMappings: any;
    sensorIdMappings: any;
    availableMappings: any;

    constructor($mdDialog, RestApi, streamDescription) {
        this.$mdDialog = $mdDialog;
        this.RestApi = RestApi;
        this.streamDescription = streamDescription;
    }

    $onInit() {
        this.finished = false;
        this.platformIdMappings = this.getMappingsByType("PLATFORM_IDENTIFIER");
        this.locationIdMappings = this.getMappingsByType("LOCATION_IDENTIFIER");
        this.sensorIdMappings = this.getMappingsByType("SENSOR_IDENTIFIER");

        this.availableMappings = this.getMappings();
        this.finished = true;
    }

    hide() {
        this.$mdDialog.hide();
    }

    cancel() {
        this.$mdDialog.cancel();
    }

    getMappings() {
        return this.getTopicDefinition()
            .wildcardTopicMappings;
    }

    getMappingsByType(topicParameterType) {
        var result = [];
        angular.forEach(this.getMappings(), topicMapping => {
            if (topicMapping.topicParameterType == topicParameterType) {
                result.push(topicMapping);
            }
        });
        return result;
    }

    getTopicDefinition() {
        return this.streamDescription.eventGrounding
            .transportProtocols[0]
            .properties.topicDefinition
            .properties;
    }

    save() {
        this.RestApi
            .updateStream(this.streamDescription)
            .then(msg => {
                let stream = msg.data;
                this.streamDescription = stream;
                this.hide();
            });
    }
}

TopicSelectionDialog.$inject = ['$mdDialog', 'RestApi', 'streamDescription'];