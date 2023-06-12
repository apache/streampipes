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
import { JsplumbBridge } from './jsplumb-bridge.service';
import { JsplumbConfigService } from './jsplumb-config.service';
import { JsplumbService } from './jsplumb.service';
import { Injectable } from '@angular/core';
import { PipelineElementConfig } from '../model/editor.model';
import {
    DataProcessorInvocation,
    DataSinkInvocation,
    PipelineCanvasMetadata,
    PipelineElementMetadata,
    SpDataStream,
} from '@streampipes/platform-services';
import { JsplumbFactoryService } from './jsplumb-factory.service';
import { ObjectProvider } from './object-provider.service';
import { Connection } from '@jsplumb/browser-ui';

@Injectable({ providedIn: 'root' })
export class PipelinePositioningService {
    constructor(
        private jsplumbService: JsplumbService,
        private jsplumbConfigService: JsplumbConfigService,
        private jsplumbFactoryService: JsplumbFactoryService,
        private objectProvider: ObjectProvider,
    ) {}

    collectPipelineElementPositions(
        pipelineCanvasMetadata: PipelineCanvasMetadata,
        rawPipelineModel: PipelineElementConfig[],
    ): PipelineCanvasMetadata {
        rawPipelineModel.forEach(pe => {
            this.collectPipelineElementPosition(
                pe.payload.dom,
                pipelineCanvasMetadata,
            );
        });
        return pipelineCanvasMetadata;
    }

    collectPipelineElementPosition(
        domId: string,
        pipelineCanvasMetadata: PipelineCanvasMetadata,
    ) {
        const elementRef = $(`#${domId}`);
        if (elementRef && elementRef.position()) {
            const leftPos = elementRef.position().left;
            const topPos = elementRef.position().top;
            if (!pipelineCanvasMetadata.pipelineElementMetadata) {
                pipelineCanvasMetadata.pipelineElementMetadata = {};
            }
            if (!pipelineCanvasMetadata.pipelineElementMetadata[domId]) {
                pipelineCanvasMetadata.pipelineElementMetadata[domId] =
                    new PipelineElementMetadata();
            }
            pipelineCanvasMetadata.pipelineElementMetadata[domId].position = {
                x: leftPos,
                y: topPos,
            };
        }
    }

    displayPipeline(
        rawPipelineModel: PipelineElementConfig[],
        targetCanvas,
        previewConfig: boolean,
        autoLayout: boolean,
        pipelineCanvasMetadata?: PipelineCanvasMetadata,
    ) {
        const jsPlumbBridge =
            this.jsplumbFactoryService.getJsplumbBridge(previewConfig);

        const jsplumbConfig = previewConfig
            ? this.jsplumbConfigService.getPreviewConfig()
            : this.jsplumbConfigService.getEditorConfig();

        rawPipelineModel.forEach(currentPe => {
            if (!currentPe.settings.disabled) {
                if (currentPe.type === 'stream' || currentPe.type === 'set') {
                    this.jsplumbService.dataStreamDropped(
                        currentPe.payload.dom,
                        currentPe.payload as SpDataStream,
                        true,
                        previewConfig,
                    );
                }
                if (currentPe.type === 'sepa') {
                    this.jsplumbService.dataProcessorDropped(
                        currentPe.payload.dom,
                        currentPe.payload as DataProcessorInvocation,
                        true,
                        previewConfig,
                    );
                }
                if (currentPe.type === 'action') {
                    this.jsplumbService.dataSinkDropped(
                        currentPe.payload.dom,
                        currentPe.payload as DataSinkInvocation,
                        true,
                        previewConfig,
                    );
                }
            }
        });

        this.connectPipelineElements(
            rawPipelineModel,
            previewConfig,
            jsplumbConfig,
            jsPlumbBridge,
        );
        if (autoLayout) {
            this.layoutGraph(
                targetCanvas,
                "div[id^='jsplumb']",
                previewConfig ? 75 : 110,
                previewConfig,
            );
        } else if (pipelineCanvasMetadata) {
            this.layoutGraphFromCanvasMetadata(pipelineCanvasMetadata);
        }
        jsPlumbBridge.repaintEverything();
    }

    layoutGraph(
        canvasId: string,
        nodeIdentifier: string,
        dimension: number,
        previewConfig: boolean,
    ) {
        const jsPlumbBridge =
            this.jsplumbFactoryService.getJsplumbBridge(previewConfig);
        const g = new dagre.graphlib.Graph();
        g.setGraph({ rankdir: 'LR', ranksep: previewConfig ? '50' : '100' });
        g.setDefaultEdgeLabel(() => {
            return {};
        });
        const nodes = $(canvasId).find(nodeIdentifier).get();
        nodes.forEach(n => {
            g.setNode(n.id, {
                label: n.id,
                width: dimension,
                height: dimension,
            });
        });

        const edges = jsPlumbBridge.getAllConnections() as Connection[];
        edges.forEach(edge => {
            g.setEdge(edge.source.id, edge.target.id);
        });

        dagre.layout(g);
        g.nodes().forEach(v => {
            const elementRef = $(`#${v}`);
            elementRef.css('left', g.node(v).x + 'px');
            elementRef.css('top', g.node(v).y + 'px');
        });
    }

    layoutGraphFromCanvasMetadata(
        pipelineCanvasMetadata: PipelineCanvasMetadata,
    ) {
        Object.entries(pipelineCanvasMetadata.pipelineElementMetadata).forEach(
            ([key, value]) => {
                const elementRef = $(`#${key}`);
                if (elementRef) {
                    elementRef.css('left', value.position.x + 'px');
                    elementRef.css('top', value.position.y + 'px');
                }
            },
        );
    }

    connectPipelineElements(
        rawPipelineModel: PipelineElementConfig[],
        previewConfig: boolean,
        jsplumbConfig: any,
        jsPlumbBridge: JsplumbBridge,
    ) {
        let source;
        let target;
        jsPlumbBridge.setSuspendDrawing(true);
        rawPipelineModel.forEach(pe => {
            if (pe.type === 'sepa' || pe.type === 'action') {
                if (!pe.settings.disabled && pe.payload.connectedTo) {
                    pe.payload.connectedTo.forEach((connection, index) => {
                        source = connection;
                        target = pe.payload.dom;
                        const sourceEndpointId = 'out-' + connection;
                        const inTargetEndpointId =
                            'in-' + index + '-' + pe.payload.dom;
                        jsPlumbBridge.connect({
                            uuids: [sourceEndpointId, inTargetEndpointId],
                            detachable: !previewConfig,
                        });
                        jsPlumbBridge.activateEndpointWithType(
                            sourceEndpointId,
                            true,
                            'token',
                        );
                        jsPlumbBridge.activateEndpointWithType(
                            inTargetEndpointId,
                            true,
                            'token',
                        );

                        if (
                            !(pe.payload instanceof DataSinkInvocation) &&
                            !this.objectProvider.hasConnectedPipelineElement(
                                pe.payload.dom,
                                rawPipelineModel,
                            )
                        ) {
                            const outTargetEndpointId = 'out-' + pe.payload.dom;
                            jsPlumbBridge.activateEndpointWithType(
                                outTargetEndpointId,
                                true,
                                'token',
                            );
                        }
                    });
                }
            }
        });
        jsPlumbBridge.setSuspendDrawing(false, true);
    }
}
