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

import * as dagre from 'dagre';
import {JsplumbBridge} from "./jsplumb-bridge.service";
import {Injectable} from "@angular/core";
import {PipelineElementConfig} from "../model/editor.model";

@Injectable()
export class PipelineValidationService {

    errorMessages: any = [];
    pipelineValid: boolean = false;

    availableErrorMessages: any = [
        {title: "Did you add a data stream?", content: "Any pipeline needs at least one data stream."},
        {title: "Did you add a data sink?", content: "Any pipeline needs at least one data sink."},
        {title: "Did you connect all elements?", content: "No orphaned elements are allowed within a pipeline, make sure to connect all elements."},
        {title: "Separate pipelines", content: "It seems you've created more than one pipeline at once. Create only one pipeline at a time!"}
    ];

    constructor(private JsplumbBridge: JsplumbBridge) {
    }

    isValidPipeline(rawPipelineModel) {
        let streamInAssembly = this.isStreamInAssembly(rawPipelineModel);
        let sepaInAssembly = this.isSepaInAssembly(rawPipelineModel);
        let actionInAssembly = this.isActionInAssembly(rawPipelineModel);
        let allElementsConnected = true;
        let onlyOnePipelineCreated = true;

        if (streamInAssembly && (sepaInAssembly || actionInAssembly)) {
            allElementsConnected = this.allElementsConnected(rawPipelineModel);
        }

        if (streamInAssembly && actionInAssembly && allElementsConnected) {
            onlyOnePipelineCreated = this.onlyOnePipelineCreated(rawPipelineModel);
        }

        if (!this.isEmptyPipeline(rawPipelineModel)) {
            this.buildErrorMessages(streamInAssembly, actionInAssembly, allElementsConnected, onlyOnePipelineCreated);
        } else {
            this.errorMessages = [];
        }

        this.pipelineValid = streamInAssembly && actionInAssembly && allElementsConnected && onlyOnePipelineCreated;
        return this.pipelineValid;
    }

    isEmptyPipeline(rawPipelineModel) {
        return !this.isActionInAssembly(rawPipelineModel) && !this.isStreamInAssembly(rawPipelineModel) && !this.isInAssembly(rawPipelineModel, 'sepa');
    }

    buildErrorMessages(streamInAssembly, actionInAssembly, allElementsConnected, onlyOnePipelineCreated) {
        this.errorMessages = [];
        if (!streamInAssembly) {
            this.errorMessages.push(this.availableErrorMessages[0]);
        }
        if (!actionInAssembly) {
            this.errorMessages.push(this.availableErrorMessages[1]);
        }
        if (!allElementsConnected) {
            this.errorMessages.push(this.availableErrorMessages[2]);
        }
        if (!onlyOnePipelineCreated) {
            this.errorMessages.push(this.availableErrorMessages[3]);
        }
    }

    allElementsConnected(rawPipelineModel) {
        let g = this.makeGraph(rawPipelineModel);
        return g.nodes().every(node => this.isFullyConnected(g, node, rawPipelineModel));
    }

    isFullyConnected(g, node, rawPipelineModel) {
        var nodeProperty = g.node(node);
        return g.outEdges(node).length >= nodeProperty.endpointCount;
    }

    isStreamInAssembly(rawPipelineModel) {
        return this.isInAssembly(rawPipelineModel, "stream") || this.isInAssembly(rawPipelineModel, "set");
    }

    isActionInAssembly(rawPipelineModel) {
        return this.isInAssembly(rawPipelineModel, "action");
    }

    isSepaInAssembly(rawPipelineModel) {
        return this.isInAssembly(rawPipelineModel, "sepa");
    }

    onlyOnePipelineCreated(rawPipelineModel) {
        let g = this.makeGraph(rawPipelineModel);
        let tarjan = dagre.graphlib.alg.tarjan(g);

        return tarjan.length == 1;
    }

    isInAssembly(rawPipelineModel: PipelineElementConfig[], type) {
        var isElementInAssembly = false;
        rawPipelineModel.forEach(pe => {
            if (pe.type === type && !pe.settings.disabled) {
                isElementInAssembly = true;
            }
        });
        return isElementInAssembly;
    }

    makeGraph(rawPipelineModel: PipelineElementConfig[]) {
        var g = new dagre.graphlib.Graph();
        g.setGraph({rankdir: "LR"});
        g.setDefaultEdgeLabel(function () {
            return {};
        });
        var nodes = $("#assembly").find("span[id^='jsplumb']").get();

        for (var i = 0; i < nodes.length; i++) {
            var n = nodes[i];
            var elementOptions = this.getElementOptions(n.id, rawPipelineModel);
            if (!elementOptions.settings.disabled) {
                g.setNode(n.id, {
                    label: n.id,
                    type: elementOptions.type,
                    name: elementOptions.payload.name,
                    endpointCount: this.JsplumbBridge.getEndpointCount(n.id)
                });
            }
        }
        var edges = this.JsplumbBridge.getAllConnections();
        for (var i = 0; i < edges.length; i++) {
            var c = edges[i];
            g.setEdge(c.source.id, c.target.id);
            g.setEdge(c.target.id, c.source.id);
        }
        return g;
    }

    getElementOptions(id, rawPipelineModel: PipelineElementConfig[]) {
        var pipelineElement;
        rawPipelineModel.forEach(pe => {
           if (pe.payload.dom === id) {
               pipelineElement = pe;
           }
        });
        return pipelineElement;
    }
}

//PipelineValidationService.$inject=['ObjectProvider', 'JsplumbBridge'];