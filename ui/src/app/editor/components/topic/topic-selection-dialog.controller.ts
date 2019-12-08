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

export class TopicSelectionDialogController {

    topicMappings: any;
    eventProperties: any;
    streamDescription: any;

    constructor() { }

    $onInit() {
        angular.forEach(this.topicMappings, topicMapping => {
            if (!topicMapping.selectedMapping) {
                topicMapping.selectedMapping = "*";
            }
        })

        this.eventProperties = this.getEventProperties();
    }

    getEventProperties() {
        return this.streamDescription.eventSchema.eventProperties;
    }

    getEventProperty(runtimeName) {
        var eventProperty = {};
        angular.forEach(this.getEventProperties(), function (ep) {
            if (ep.properties.runtimeName == runtimeName) {
                eventProperty = ep;
            }
        })
        return eventProperty;
    }

    getTopicDefinition() {
        return this.streamDescription.eventGrounding
            .transportProtocols[0]
            .properties.topicDefinition
            .properties;
    }

    getWildcardTopic() {
        return this.getTopicDefinition()
            .wildcardTopicName;
    }

    getLabel(runtimeName) {
        var ep = this.getEventProperty(runtimeName);
        return ep['properties'].label + " (" + ep['properties'].description + ")";
    }

    getPossibleValues(runtimeName) {
        var ep = this.getEventProperty(runtimeName);
        return ep['properties'].valueSpecification.properties.runtimeValues;
    }


}