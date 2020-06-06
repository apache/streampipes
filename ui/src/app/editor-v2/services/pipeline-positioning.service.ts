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

//import * from 'lodash';
import * as dagre from "dagre";
import {JsplumbBridge} from "./jsplumb-bridge.service";
import {JsplumbConfigService} from "./jsplumb-config.service";
import {JsplumbService} from "./jsplumb.service";
import {Inject, Injectable} from "@angular/core";
import {PipelineElementConfig} from "../model/editor.model";

declare const jsPlumb: any;

@Injectable()
export class PipelinePositioningService {


    constructor(private JsplumbService: JsplumbService,
                private JsplumbConfigService: JsplumbConfigService,
                private JsplumbBridge: JsplumbBridge) {
    }

    displayPipeline(rawPipelineModel: PipelineElementConfig[], targetCanvas, isPreview, autoLayout) {
        var jsplumbConfig = isPreview ? this.JsplumbConfigService.getPreviewConfig() : this.JsplumbConfigService.getEditorConfig();

        for (var i = 0; i < rawPipelineModel.length; i++) {
            var currentPe = rawPipelineModel[i];
            if (!currentPe.settings.disabled) {
                if (currentPe.type === "stream") {
                    this.JsplumbService.streamDropped(currentPe.payload.dom, currentPe.payload, true, isPreview);
                }
                if (currentPe.type === "sepa") {
                    this.JsplumbService.sepaDropped(currentPe.payload.dom, currentPe.payload, true, isPreview);
                }
                if (currentPe.type === "action") {
                    this.JsplumbService.actionDropped(currentPe.payload.dom, currentPe.payload, true, isPreview);
                }
            }
        }

        this.connectPipelineElements(rawPipelineModel, !isPreview, jsplumbConfig);
        if (autoLayout) {
            this.layoutGraph(targetCanvas, "span[id^='jsplumb']", isPreview ? 75 : 110, isPreview);
        }
        this.JsplumbBridge.repaintEverything();
    }

    layoutGraph(canvas, nodeIdentifier, dimension, isPreview) {
        var g = new dagre.graphlib.Graph();
        g.setGraph({rankdir: "LR", ranksep: isPreview ? "50" : "100"});
        g.setDefaultEdgeLabel(function () {
            return {};
        });
        var nodes = $(canvas).find(nodeIdentifier).get();

        for (var i = 0; i < nodes.length; i++) {
            var n = nodes[i];
            g.setNode(n.id, {label: n.id, width: dimension, height: dimension});
        }
        var edges = this.JsplumbBridge.getAllConnections();
        for (var i = 0; i < edges.length; i++) {
            var c = edges[i];
            g.setEdge(c.source.id, c.target.id);
        }
        dagre.layout(g);
        g.nodes().forEach(v => {
            $("#" + v).css("left", g.node(v).x + "px");
            $("#" + v).css("top", g.node(v).y + "px");
        });
    }

    connectPipelineElements(rawPipelineModel: PipelineElementConfig[], detachable, jsplumbConfig) {
        var source, target;

        this.JsplumbBridge.setSuspendDrawing(true);
        for (var i = 0; i < rawPipelineModel.length; i++) {
            var pe = rawPipelineModel[i];

            if (pe.type == "sepa") {
                if (pe.payload.connectedTo) {
                    for (var j = 0, connection; connection = pe.payload.connectedTo[j]; j++) {
                        source = connection;
                        target = pe.payload.dom;

                        var options;
                        var id = "#" + source;
                        if ($(id).hasClass("sepa")) {
                            options = jsplumbConfig.sepaEndpointOptions;
                        } else {
                            options = jsplumbConfig.streamEndpointOptions;
                        }

                        let sourceEndpointId = "out-" + connection;
                        let targetEndpointId = "in-" + j + "-" + pe.payload.dom;
                        this.JsplumbBridge.connect(
                            {uuids: [sourceEndpointId, targetEndpointId], detachable: detachable}
                        );
                    }
                }
            } else if (pe.type == "action") {
                target = pe.payload.dom;

                if (pe.payload.connectedTo) {
                    for (var j = 0, connection; connection = pe.payload.connectedTo[j]; j++) {
                        source = connection;
                        let sourceEndpointId = "out-" + connection;
                        let targetEndpointId = "in-" + j + "-" + target;
                        this.JsplumbBridge.connect(
                            {uuids: [sourceEndpointId, targetEndpointId], detachable: detachable}
                        );
                    }
                }
            }
        }
        this.JsplumbBridge.setSuspendDrawing(false, true);
    }

}

//PipelinePositioningService.$inject = ['JsplumbService', 'JsplumbConfigService', 'JsplumbBridge'];