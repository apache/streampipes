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

import {
    BrowserJsPlumbInstance,
    EndpointSelection,
    SelectOptions,
} from '@jsplumb/browser-ui';

export class JsplumbBridge {
    constructor(private jsPlumbInstance: BrowserJsPlumbInstance) {}

    activateEndpoint(endpointId: string, endpointEnabled: boolean) {
        const endpoint = this.getEndpointById(endpointId);
        endpoint.enabled = endpointEnabled;
    }

    activateEndpointWithType(
        endpointId: string,
        endpointEnabled: boolean,
        endpointType: string,
    ) {
        this.activateEndpoint(endpointId, endpointEnabled);
        this.setEndpointType(endpointId, endpointType);
    }

    setEndpointType(endpointId: string, endpointType: string) {
        const endpoint = this.getEndpointById(endpointId);
        // @ts-ignore
        endpoint.setType(endpointType);
    }

    getEndpointById(endpointId: string) {
        return this.jsPlumbInstance.getEndpoint(endpointId);
    }

    setZoom(scale) {
        this.jsPlumbInstance.setZoom(scale);
    }

    repaintEverything() {
        this.jsPlumbInstance.repaintEverything();
    }

    deleteEveryEndpoint() {
        this.jsPlumbInstance.selectEndpoints().deleteAll();
        this.jsPlumbInstance.deleteEveryConnection();
    }

    setContainer(container) {
        this.jsPlumbInstance.setContainer(document.getElementById(container));
    }

    unbind(element) {
        this.jsPlumbInstance.unbind(element);
    }

    bind(event, fn) {
        return this.jsPlumbInstance.bind(event, fn);
    }

    selectEndpoints(endpoint?): EndpointSelection {
        if (endpoint === undefined) {
            return this.jsPlumbInstance.selectEndpoints();
        } else {
            return this.jsPlumbInstance.selectEndpoints(endpoint);
        }
    }

    getTargetEndpoint(id: string): EndpointSelection {
        return this.jsPlumbInstance.selectEndpoints({
            target: document.getElementById(id),
        });
    }

    getEndpointCount(id: string): number {
        return this.jsPlumbInstance.selectEndpoints({
            element: document.getElementById(id),
        }).length;
    }

    detach(connection) {
        this.jsPlumbInstance.deleteConnection(connection);
    }

    getConnections(filter: SelectOptions<Element>) {
        return this.jsPlumbInstance.getConnections(filter);
    }

    addEndpoint(pipelineElementDomId: string, options: any) {
        options.cssClass = 'sp-no-pan';
        return this.jsPlumbInstance.addEndpoint(
            document.getElementById(pipelineElementDomId),
            options,
        );
    }

    connect(connection) {
        this.jsPlumbInstance.connect(connection);
    }

    removeAllEndpoints(element) {
        this.jsPlumbInstance.removeAllEndpoints(
            document.getElementById(element),
        );
    }

    registerEndpointTypes(typeInfo) {
        this.jsPlumbInstance.registerEndpointTypes(typeInfo);
    }

    // TODO: Overloading Functions?
    setSuspendDrawing(bool1, bool2?) {
        if (bool2 === undefined) {
            this.jsPlumbInstance.setSuspendDrawing(bool1);
        } else {
            this.jsPlumbInstance.setSuspendDrawing(bool1, bool2);
        }
    }

    getAllConnections() {
        return this.jsPlumbInstance.getConnections();
    }

    reset() {
        this.jsPlumbInstance.reset();
    }
}
