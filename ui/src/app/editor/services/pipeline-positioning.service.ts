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

import * as dagre from "dagre";
import {JsplumbBridge} from "./jsplumb-bridge.service";
import {JsplumbConfigService} from "./jsplumb-config.service";
import {JsplumbService} from "./jsplumb.service";
import {Injectable} from "@angular/core";
import {PipelineElementConfig} from "../model/editor.model";
import {
    DataProcessorInvocation,
    DataSinkInvocation,
    SpDataStream
} from "../../core-model/gen/streampipes-model";
import {JsplumbFactoryService} from "./jsplumb-factory.service";
import {ObjectProvider} from "./object-provider.service";

@Injectable()
export class PipelinePositioningService {

    constructor(private JsplumbService: JsplumbService,
                private JsplumbConfigService: JsplumbConfigService,
                private JsplumbFactoryService: JsplumbFactoryService,
                private ObjectProvider: ObjectProvider) {
    }

    displayPipeline(rawPipelineModel: PipelineElementConfig[],
                    targetCanvas,
                    previewConfig: boolean,
                    autoLayout: boolean) {
        let jsPlumbBridge = this.JsplumbFactoryService.getJsplumbBridge(previewConfig);
        let jsplumbConfig = previewConfig ? this.JsplumbConfigService.getPreviewConfig() : this.JsplumbConfigService.getEditorConfig();
        rawPipelineModel.forEach(currentPe => {
            if (!currentPe.settings.disabled) {
                if (currentPe.type === "stream" || currentPe.type === "set") {
                    this.JsplumbService.dataStreamDropped(currentPe.payload.dom,
                        currentPe.payload as SpDataStream,
                        true,
                        previewConfig);
                }
                if (currentPe.type === "sepa") {
                    this.JsplumbService.dataProcessorDropped(currentPe.payload.dom, currentPe.payload as DataProcessorInvocation, true, previewConfig);
                }
                if (currentPe.type === "action") {
                    this.JsplumbService.dataSinkDropped(currentPe.payload.dom, currentPe.payload as DataSinkInvocation, true, previewConfig);
                }
            }
        });

        this.connectPipelineElements(rawPipelineModel, previewConfig, jsplumbConfig, jsPlumbBridge);
        if (autoLayout) {
            this.layoutGraph(targetCanvas, "span[id^='jsplumb']", previewConfig ? 75 : 110, previewConfig);
        }
        jsPlumbBridge.repaintEverything();
    }

    layoutGraph(canvas, nodeIdentifier, dimension, isPreview) {
        let jsPlumbBridge = this.JsplumbFactoryService.getJsplumbBridge(isPreview);
        var g = new dagre.graphlib.Graph();
        g.setGraph({rankdir: "LR", ranksep: isPreview ? "50" : "100"});
        g.setDefaultEdgeLabel(function () {
            return {};
        });

        var nodes = $(canvas).find(nodeIdentifier).get();
        nodes.forEach((n, index) => {
            g.setNode(n.id, {label: n.id, width: dimension, height: dimension});
        });

        var edges = jsPlumbBridge.getAllConnections();
        edges.forEach(edge => {
            g.setEdge(edge.source.id, edge.target.id);
        });

        dagre.layout(g);
        g.nodes().forEach(v => {
            $(`#${v}`).css("left", g.node(v).x + "px");
            $(`#${v}`).css("top", g.node(v).y + "px");
        });
    }

    connectPipelineElements(rawPipelineModel: PipelineElementConfig[],
                            previewConfig: boolean,
                            jsplumbConfig: any,
                            jsPlumbBridge: JsplumbBridge) {
        var source, target;
        jsPlumbBridge.setSuspendDrawing(true);
        for (var i = 0; i < rawPipelineModel.length; i++) {
            var pe = rawPipelineModel[i];

            if (pe.type == "sepa" || pe.type == "action") {
                if (!(pe.settings.disabled) && pe.payload.connectedTo) {
                    pe.payload.connectedTo.forEach((connection, index) => {
                        source = connection;
                        target = pe.payload.dom;

                        let sourceEndpointId = "out-" + connection;
                        let inTargetEndpointId = "in-" + index + "-" + pe.payload.dom;
                        jsPlumbBridge.connect(
                            {
                                uuids: [sourceEndpointId, inTargetEndpointId],
                                detachable: !previewConfig
                            }
                        );
                        jsPlumbBridge.activateEndpointWithType(sourceEndpointId, true, "token");
                        jsPlumbBridge.activateEndpointWithType(inTargetEndpointId, true, "token");

                        if (!(pe.payload instanceof DataSinkInvocation) && !(this.ObjectProvider.hasConnectedPipelineElement(pe.payload.dom, rawPipelineModel))) {
                            let outTargetEndpointId = "out-" + pe.payload.dom;
                            jsPlumbBridge.activateEndpointWithType(outTargetEndpointId, true, "token");
                        }
                    });
                }
            }
        }
        jsPlumbBridge.setSuspendDrawing(false, true);
    }
}
