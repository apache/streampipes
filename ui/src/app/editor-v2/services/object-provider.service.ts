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
import {Injectable} from "@angular/core";
import {RestApi} from "../../services/rest-api.service";
import {JsplumbBridge} from "./jsplumb-bridge.service";
import {PipelineElementConfig} from "../model/editor.model";

declare const jsPlumb: any;

@Injectable()
export class ObjectProvider {

    ImageChecker: any;
    adjustingPipelineState: any;
    plumbReady: any;
    sources: any;
    sepas: any;
    actions: any;
    currentElement: any;
    //this.currentPipeline = new this.Pipeline();
    adjustingPipeline: any;

    constructor(private RestApi: RestApi,
                private JsplumbBridge: JsplumbBridge) {
    }

    prepareElement(json) {
        json.connectedTo = [];
        return json;
    }

    preparePipeline() {
        var pipeline = {};
        pipeline['name'] = "";
        pipeline['description'] = "";
        pipeline['streams'] = [];
        pipeline['sepas'] = [];
        pipeline['actions'] = [];

        return pipeline;
    }

    makeFinalPipeline(currentPipelineElements) {
        return this.makePipeline(currentPipelineElements);
    }

    makePipeline(currentPipelineElements) {
        var pipeline = this.preparePipeline();
        pipeline = this.addElementNew(pipeline, currentPipelineElements);
        return pipeline;
    }

    findElement(elementId, rawPipelineModel: PipelineElementConfig[]): PipelineElementConfig {
        let result = {} as PipelineElementConfig;
        angular.forEach(rawPipelineModel, pe => {
            if (pe.payload.dom === elementId) {
                result = pe;
            }
        });
        return result;
    }

    addElementNew(pipeline, currentPipelineElements: PipelineElementConfig[]) {
        currentPipelineElements.forEach(pe => {
            if (pe.settings.disabled == undefined || !pe.settings.disabled) {
                if (pe.type === 'sepa' || pe.type === 'action') {
                    var payload = pe.payload;
                    payload = this.prepareElement(payload);
                    var connections = this.JsplumbBridge.getConnections({
                        target: $("#" + payload.dom)
                    });
                    for (var i = 0; i < connections.length; i++) {
                        payload.connectedTo.push(connections[i].sourceId);
                    }
                    if (payload.connectedTo && payload.connectedTo.length > 0) {
                        pe.type === 'action' ? pipeline.actions.push(payload) : pipeline.sepas.push(payload);
                    }
                }
                else {
                    pipeline.streams.push(pe.payload);
                }
            }
        });
        return pipeline;
    }

    updatePipeline(pipeline) {
        return this.RestApi.updatePartialPipeline(pipeline);
    };

    storePipeline(pipeline) {
        return this.RestApi.storePipeline(pipeline);

    }

    State() {
        this.adjustingPipelineState = false;
        this.plumbReady = false;
        this.sources = {};
        this.sepas = {};
        this.actions = {};
        this.currentElement = {};
        //this.currentPipeline = new this.Pipeline();
        this.adjustingPipeline = {};
    };

}

//ObjectProvider.$inject = ['RestApi', 'JsplumbBridge'];
