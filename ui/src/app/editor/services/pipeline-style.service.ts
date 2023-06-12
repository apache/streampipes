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

import { Injectable } from '@angular/core';
import { JsplumbConfigService } from './jsplumb-config.service';
import {
    EdgeValidationStatus,
    PipelineEdgeValidation,
} from '@streampipes/platform-services';
import { Endpoint } from '@jsplumb/browser-ui';
import { JsplumbFactoryService } from './jsplumb-factory.service';
import {
    PipelineElementConfig,
    PipelineElementConfigurationStatus,
} from '../model/editor.model';

@Injectable({ providedIn: 'root' })
export class PipelineStyleService {
    constructor(
        private jsPlumbConfigService: JsplumbConfigService,
        private jsplumbFactoryService: JsplumbFactoryService,
    ) {}

    updateAllConnectorStyles(edgeValidations: PipelineEdgeValidation[]) {
        edgeValidations.forEach(edgeValidation =>
            this.updateConnectorStyle(edgeValidation),
        );
    }

    updateAllEndpointStyles(edgeValidations: PipelineEdgeValidation[]) {
        const jsplumbBridge =
            this.jsplumbFactoryService.getJsplumbBridge(false);
        edgeValidations.forEach(value => {
            const endpoints = jsplumbBridge.getTargetEndpoint(value.targetId);
            endpoints.each(endpoint => {
                if (endpoint.connections.length > 0) {
                    endpoint.setType('token');
                }
            });
        });
    }

    updateConnectorStyle(validation: PipelineEdgeValidation) {
        const jsplumbBridge =
            this.jsplumbFactoryService.getJsplumbBridge(false);
        const connections = jsplumbBridge.getConnections({
            source: this.byId(validation.sourceId),
            target: this.byId(validation.targetId),
        });
        const connectorStyle = this.getConnectorStyleConfig(validation.status);

        if (Array.isArray(connections)) {
            connections.forEach(connection => {
                connection.setPaintStyle(connectorStyle);
            });
        }
    }

    getConnectorStyleConfig(status: EdgeValidationStatus) {
        if (status.validationStatusType === 'COMPLETE') {
            return this.jsPlumbConfigService.getConnectorStyleSuccess();
        } else if (status.validationStatusType === 'INCOMPLETE') {
            return this.jsPlumbConfigService.getConnectorStyleWarning();
        } else {
            return this.jsPlumbConfigService.getConnectorStyleError();
        }
    }

    updateEndpointStyle(endpoint: Endpoint, endpointType: string) {
        endpoint.setType(endpointType);
    }

    updatePeConfigurationStatus(
        pe: PipelineElementConfig,
        status: PipelineElementConfigurationStatus,
    ) {
        pe.settings.completed = status;
    }

    byId(id: string) {
        return document.getElementById(id);
    }
}
