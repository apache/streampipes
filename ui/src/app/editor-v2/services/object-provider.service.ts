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
import {InvocablePipelineElementUnion, PipelineElementConfig} from "../model/editor.model";
import {InvocableStreamPipesEntity, Pipeline} from "../../core-model/gen/streampipes-model";

@Injectable()
export class ObjectProvider {

    constructor(private RestApi: RestApi,
                private JsplumbBridge: JsplumbBridge) {
    }

    prepareElement(pipelineElement: InvocablePipelineElementUnion) {
        pipelineElement.connectedTo = [];
        return pipelineElement;
    }

    preparePipeline(): Pipeline {
        var pipeline = new Pipeline();
        pipeline.name = "";
        pipeline.description = "";
        pipeline.streams = [];
        pipeline.sepas = [];
        pipeline.actions = [];

        return pipeline;
    }

    makeFinalPipeline(currentPipelineElements) {
        return this.makePipeline(currentPipelineElements);
    }

    makePipeline(currentPipelineElements): Pipeline {
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

    addElementNew(pipeline, currentPipelineElements: PipelineElementConfig[]): Pipeline {
        currentPipelineElements.forEach(pe => {
            if (pe.settings.disabled == undefined || !pe.settings.disabled) {
                if (pe.type === 'sepa' || pe.type === 'action') {
                    let payload = pe.payload;
                    payload = this.prepareElement(payload as InvocablePipelineElementUnion);
                    let connections = this.JsplumbBridge.getConnections({
                        target: $("#" + payload.dom)
                    });
                    for (let i = 0; i < connections.length; i++) {
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

    updatePipeline(pipeline: Pipeline) {
        return this.RestApi.updatePartialPipeline(pipeline);
    };

    storePipeline(pipeline) {
        return this.RestApi.storePipeline(pipeline);

    }
}
