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

declare const jsPlumb: any;

export class JsplumbBridge {

    constructor() {
    }

    setZoom(scale) {
        jsPlumb.setZoom(scale);
    }

    repaintEverything() {
        jsPlumb.repaintEverything(true);
    }

    deleteEveryEndpoint() {
        jsPlumb.deleteEveryEndpoint();
    }

    setContainer(container) {
        jsPlumb.setContainer(container);
    }

    unbind(element) {
        jsPlumb.unbind(element);
    }

    bind(event, fn) {
        return jsPlumb.bind(event, fn);
    }

    // TODO: Overloading Functions?
    selectEndpoints(endpoint?) {
        if (endpoint === undefined) {
            return jsPlumb.selectEndpoints();
        }
        return jsPlumb.selectEndpoints(endpoint);
    }

    selectEndpointsById(id) {
        return jsPlumb.selectEndpoints({source: id});
    }

    getSourceEndpoint(id) {
        return jsPlumb.selectEndpoints({source: id});
    }

    getTargetEndpoint(id) {
        return jsPlumb.selectEndpoints({target: id});
    }

    getEndpointCount(id) {
        return jsPlumb.selectEndpoints({element: id}).length;
    }

    detach(connection) {
        jsPlumb.detach(connection);
    }

    getConnections(filter) {
        return jsPlumb.getConnections(filter);
    }

    addEndpoint(element, options) {
        return jsPlumb.addEndpoint(element, options);
    }

    connect(connection) {
        jsPlumb.connect(connection);
    }

    removeAllEndpoints(element) {
        jsPlumb.removeAllEndpoints(element);
    }

    registerEndpointTypes(typeInfo) {
        jsPlumb.registerEndpointTypes(typeInfo);
    }

    draggable(element, option) {
        jsPlumb.draggable(element, option);
    }

    // TODO: Overloading Functions?
    setSuspendDrawing(bool1, bool2?) {
        if (bool2 === undefined) {
            jsPlumb.setSuspendDrawing(bool1);
        } else {
            jsPlumb.setSuspendDrawing(bool1, bool2);
        }
    }

    getAllConnections() {
        return jsPlumb.getAllConnections();
    }

    reset() {
        jsPlumb.reset();
    }
}

//JsplumbBridge.$inject = [];
