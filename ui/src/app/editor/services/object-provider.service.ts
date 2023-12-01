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
import {
    InvocablePipelineElementUnion,
    PipelineElementConfig,
} from '../model/editor.model';
import { DataSinkInvocation, Pipeline } from '@streampipes/platform-services';
import { EditorService } from './editor.service';
import { JsplumbFactoryService } from './jsplumb-factory.service';

@Injectable({ providedIn: 'root' })
export class ObjectProvider {
    constructor(
        private editorService: EditorService,
        private jsplumbFactoryService: JsplumbFactoryService,
    ) {}

    prepareElement(pipelineElement: InvocablePipelineElementUnion) {
        pipelineElement.connectedTo = [];
        return pipelineElement;
    }

    preparePipeline(): Pipeline {
        const pipeline = new Pipeline();
        pipeline.name = '';
        pipeline.description = '';
        pipeline.streams = [];
        pipeline.sepas = [];
        pipeline.actions = [];

        return pipeline;
    }

    makeFinalPipeline(currentPipelineElements: PipelineElementConfig[]) {
        return this.makePipeline(currentPipelineElements);
    }

    makePipeline(currentPipelineElements: PipelineElementConfig[]): Pipeline {
        let pipeline = this.preparePipeline();
        pipeline = this.addElementNew(pipeline, currentPipelineElements);
        return pipeline;
    }

    hasConnectedPipelineElement(
        pipelineElementDomId: string,
        rawPipelineModel: PipelineElementConfig[],
    ) {
        const pipelineElement = this.findElement(
            pipelineElementDomId,
            rawPipelineModel,
        );
        if (pipelineElement.payload instanceof DataSinkInvocation) {
            return false;
        } else {
            return (
                rawPipelineModel
                    .filter(
                        pe => !pe.settings.disabled && pe.payload.connectedTo,
                    )
                    .find(
                        pe =>
                            pe.payload.connectedTo.indexOf(
                                pipelineElementDomId,
                            ) > -1,
                    ) !== undefined
            );
        }
    }

    findElement(
        elementId,
        rawPipelineModel: PipelineElementConfig[],
    ): PipelineElementConfig {
        return (
            rawPipelineModel.find(pe => pe.payload.dom === elementId) ||
            ({} as PipelineElementConfig)
        );
    }

    addElementNew(
        pipeline,
        currentPipelineElements: PipelineElementConfig[],
    ): Pipeline {
        const jsplumbBridge =
            this.jsplumbFactoryService.getJsplumbBridge(false);
        currentPipelineElements.forEach(pipelineElementConfig => {
            if (
                pipelineElementConfig.settings.disabled === undefined ||
                !pipelineElementConfig.settings.disabled
            ) {
                if (
                    pipelineElementConfig.type === 'sepa' ||
                    pipelineElementConfig.type === 'action'
                ) {
                    let payload = pipelineElementConfig.payload;
                    payload = this.prepareElement(
                        payload as InvocablePipelineElementUnion,
                    );
                    const connections = jsplumbBridge.getConnections({
                        target: document.getElementById(payload.dom),
                    });
                    for (let i = 0; i < connections.length; i++) {
                        payload.connectedTo.push(connections[i].sourceId);
                    }
                    if (payload.connectedTo && payload.connectedTo.length > 0) {
                        pipelineElementConfig.type === 'action'
                            ? pipeline.actions.push(payload)
                            : pipeline.sepas.push(payload);
                    }
                } else {
                    pipeline.streams.push(pipelineElementConfig.payload);
                }
            }
        });
        return pipeline;
    }

    updatePipeline(pipeline: Pipeline) {
        return this.editorService.updatePartialPipeline(pipeline);
    }
}
