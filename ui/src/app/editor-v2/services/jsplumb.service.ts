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

import {JsplumbConfigService} from "./jsplumb-config.service";
import {JsplumbBridge} from "./jsplumb-bridge.service";
import {Inject, Injectable} from "@angular/core";
import {PipelineElementConfig, PipelineElementUnion} from "../model/editor.model";
import {PipelineElementTypeUtils} from "../utils/editor.utils";
import * as angular from "angular";

@Injectable()
export class JsplumbService {

    apiConstants: any;
    idCounter: any;
    $timeout: any;
    RestApi: any;

    constructor(private JsplumbConfigService: JsplumbConfigService,
                private JsplumbBridge: JsplumbBridge) {
        this.idCounter = 0;
    }

    prepareJsplumb() {
        this.JsplumbBridge.registerEndpointTypes({
            "empty": {
                paintStyle: {
                    fillStyle: "white",
                    strokeStyle: "#9E9E9E",
                    lineWidth: 2
                }
            },
            "token": {
                paintStyle: {
                    fillStyle: "#BDBDBD",
                    strokeStyle: "#9E9E9E",
                    lineWidth: 2
                },
                hoverPaintStyle: {
                    fillStyle: "#BDBDBD",
                    strokeStyle: "#4CAF50",
                    lineWidth: 4
                }
            },
            "highlight": {
                paintStyle: {
                    fillStyle: "white",
                    strokeStyle: "#4CAF50",
                    lineWidth: 4
                }
            }
        });
    }

    activateEndpoint(endpointId, endpointEnabled) {
        this.JsplumbBridge.selectEndpointsById(endpointId).setEnabled(endpointEnabled);
    }

    makeRawPipeline(pipelineModel, isPreview) {
        return pipelineModel
            .streams
            .map(s => this.toConfig(s, "stream", isPreview))
            .concat(pipelineModel.sepas.map(s => this.toConfig(s, "sepa", isPreview)))
            .concat(pipelineModel.actions.map(s => this.toConfig(s, "action", isPreview)));
    }

    toConfig(pe, type, isPreview) {
        pe.type = type;
        pe.configured = true;
        return this.createNewPipelineElementConfig(pe, {x: 100, y: 100}, isPreview);
    }


    createElement(pipelineModel, pipelineElement, pipelineElementDomId) {
        var pipelineElementDom = $("#" + pipelineElementDomId);
        var pipelineElementConfig = this.createNewPipelineElementConfigWithFixedCoordinates(pipelineElementDom, pipelineElement, false);
        pipelineModel.push(pipelineElementConfig);
        this.$timeout(() => {
            this.createAssemblyElement(pipelineElementConfig.payload.dom, pipelineElementConfig.payload, pipelineElementDom);
        });
    }

    createAssemblyElement($newElementId, json, $parentElement) {
        var $target;
        if (json.belongsTo.indexOf("sepa") > 0) { //Sepa Element
            $target = this.sepaDropped($newElementId, json, true, false);
            this.connectNodes($parentElement, $target);
        } else {
            $target = this.actionDropped($newElementId, json, true, false);
            this.connectNodes($parentElement, $target);
        }
    }

    connectNodes($parentElement, $target) {
        var options;
        if ($parentElement.hasClass("stream")) {
            // TODO: getJsplumbConfig depends on isPreview. Not implemented yet
            options = this.getJsplumbConfig(true).streamEndpointOptions;
        } else {
            // TODO: getJsplumbConfig depends on isPreview. Not implemented yet
            options = this.getJsplumbConfig(true).sepaEndpointOptions;
        }
        var sourceEndPoint;
        if (this.JsplumbBridge.selectEndpoints({source: $parentElement}).length > 0) {
            if (!(this.JsplumbBridge.selectEndpoints({source: $parentElement}).get(0).isFull())) {
                sourceEndPoint = this.JsplumbBridge.selectEndpoints({source: $parentElement}).get(0)
            } else {
                sourceEndPoint = this.JsplumbBridge.addEndpoint($parentElement, options);
            }
        } else {
            sourceEndPoint = this.JsplumbBridge.addEndpoint($parentElement, options);
        }

        var targetEndPoint = this.JsplumbBridge.selectEndpoints({target: $target}).get(0);

        this.JsplumbBridge.connect({source: sourceEndPoint, target: targetEndPoint, detachable: true});
        this.JsplumbBridge.repaintEverything();
    }

    createNewPipelineElementConfigWithFixedCoordinates($parentElement, json, isPreview) {
        var x = $parentElement.position().left;
        var y = $parentElement.position().top;
        var coord = {'x': x + 200, 'y': y};
        return this.createNewPipelineElementConfig(json, coord, isPreview);
    }

    createNewPipelineElementConfig(pipelineElement: PipelineElementUnion,
                                   coordinates,
                                   isPreview: boolean): PipelineElementConfig {
        let displaySettings = isPreview ? 'connectable-preview' : 'connectable-editor';
        let connectable = "connectable";
        let pipelineElementConfig = {} as PipelineElementConfig;
        pipelineElementConfig.type = PipelineElementTypeUtils
            .toCssShortHand(PipelineElementTypeUtils.fromType(pipelineElement))
        pipelineElementConfig.payload = angular.copy(pipelineElement)
        pipelineElementConfig.settings = {connectable: connectable,
            openCustomize: !(pipelineElement as any).configured,
            preview: isPreview,
            disabled: false,
            loadingStatus: false,
            displaySettings: displaySettings,
            position: {
                x: coordinates.x,
                y: coordinates.y
            }};
        if (!pipelineElementConfig.payload.dom) {
            pipelineElementConfig.payload.dom = "jsplumb_" + this.idCounter + "_" + this.makeId(4);
            this.idCounter++;
        }

        return pipelineElementConfig;
    }

    makeId(count) {
        var text = "";
        var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        for (var i = 0; i < count; i++)
            text += possible.charAt(Math.floor(Math.random() * possible.length));

        return text;
    }

    streamDropped(pipelineElementDomId, json, endpoints, preview) {
        if (endpoints) {
            if (!preview) {
                this.JsplumbBridge.draggable(pipelineElementDomId, {containment: 'parent'});
            }

            this.JsplumbBridge.addEndpoint(pipelineElementDomId, this.getStreamEndpoint(preview, pipelineElementDomId));
        }
        return pipelineElementDomId;
    };

    setDropped($newElement, json, endpoints, preview) {
        this.RestApi.updateDataSet(json).then(msg => {
            let data = msg.data;
            json.eventGrounding = data.eventGrounding;
            json.datasetInvocationId = data.invocationId;
            this.streamDropped($newElement, json, endpoints, preview);
        });
    }

    sepaDropped(pipelineElementDomId, json, endpoints, preview) {
        this.actionDropped(pipelineElementDomId, json, endpoints, preview);
        if (endpoints) {
            this.JsplumbBridge.addEndpoint(pipelineElementDomId, this.getOutputEndpoint(preview, pipelineElementDomId));
        }
        return pipelineElementDomId;
    };

    actionDropped(pipelineElementDomId, json, endpoints, preview) {
        if (!preview) {
            this.JsplumbBridge.draggable(pipelineElementDomId, {containment: 'parent'});
        }

        if (endpoints) {
            if (json.inputStreams.length < 2) { //1 InputNode
                this.JsplumbBridge.addEndpoint(pipelineElementDomId, this.getInputEndpoint(preview, pipelineElementDomId, 0));
            } else {
                this.JsplumbBridge.addEndpoint(pipelineElementDomId, this.getNewTargetPoint(preview, 0, 0.25, pipelineElementDomId, 0));
                this.JsplumbBridge.addEndpoint(pipelineElementDomId, this.getNewTargetPoint(preview, 0, 0.75, pipelineElementDomId, 1));
            }
        }
        return pipelineElementDomId;
    };

    getJsplumbConfig(preview): any {
        return preview ? this.JsplumbConfigService.getPreviewConfig() : this.JsplumbConfigService.getEditorConfig();
    }

    getStreamEndpoint(preview, pipelineElementDomId) {
        var jsplumbConfig = this.getJsplumbConfig(preview);
        let config = jsplumbConfig.streamEndpointOptions;
        config.uuid = "out-" + pipelineElementDomId;
        return config;
    }

    getInputEndpoint(preview, pipelineElementDomId, index): any {
        var jsplumbConfig = this.getJsplumbConfig(preview);
        let inConfig = jsplumbConfig.leftTargetPointOptions;
        inConfig.uuid = "in-" + index + "-" + pipelineElementDomId;
        return inConfig;
    }

    getOutputEndpoint(preview, pipelineElementDomId): any {
        var jsplumbConfig = this.getJsplumbConfig(preview);
        let outConfig = jsplumbConfig.sepaEndpointOptions;
        outConfig.uuid = "out-" + pipelineElementDomId;
        return outConfig;
    }

    getNewTargetPoint(preview, x, y, pipelineElementDomId, index): any {
        let inConfig = this.getInputEndpoint(preview, pipelineElementDomId, index);
        inConfig.type = "empty";
        inConfig.anchor = [x, y, -1, 0];
        inConfig.isTarget = true;

        return inConfig;
    }
}

//JsplumbService.$inject = ['JsplumbConfigService', 'JsplumbBridge', '$timeout', 'RestApi'];
