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

import { JsplumbConfigService } from './jsplumb-config.service';
import { JsplumbBridge } from './jsplumb-bridge.service';
import { Injectable } from '@angular/core';
import {
    InvocablePipelineElementUnion,
    PipelineElementConfig,
    PipelineElementConfigurationStatus,
    PipelineElementUnion,
} from '../model/editor.model';
import { PipelineElementTypeUtils } from '../utils/editor.utils';
import {
    DataProcessorInvocation,
    DataSinkInvocation,
    Pipeline,
    SpDataStream,
} from '@streampipes/platform-services';
import { JsplumbEndpointService } from './jsplumb-endpoint.service';
import { JsplumbFactoryService } from './jsplumb-factory.service';
import { EditorService } from './editor.service';

@Injectable({ providedIn: 'root' })
export class JsplumbService {
    idCounter = 0;

    constructor(
        private jsplumbConfigService: JsplumbConfigService,
        private jsplumbFactory: JsplumbFactoryService,
        private jsplumbEndpointService: JsplumbEndpointService,
        private editorService: EditorService,
    ) {}

    isFullyConnected(
        pipelineElementConfig: PipelineElementConfig,
        previewConfig: boolean,
    ) {
        const jsplumbBridge =
            this.jsplumbFactory.getJsplumbBridge(previewConfig);
        const payload =
            pipelineElementConfig.payload as InvocablePipelineElementUnion;
        return (
            payload.inputStreams == null ||
            jsplumbBridge.getConnections({
                target: document.getElementById(payload.dom),
            }).length == payload.inputStreams.length
        );
    }

    makeRawPipeline(pipelineModel: Pipeline, isPreview: boolean) {
        return pipelineModel.streams
            .map(s => this.toConfig(s, 'stream', isPreview))
            .concat(
                pipelineModel.sepas.map(s =>
                    this.toConfig(s, 'sepa', isPreview),
                ),
            )
            .concat(
                pipelineModel.actions.map(s =>
                    this.toConfig(s, 'action', isPreview),
                ),
            );
    }

    toConfig(pe: PipelineElementUnion, type: string, isPreview: boolean) {
        (pe as any).type = type;
        return this.createNewPipelineElementConfig(
            pe,
            { x: 100, y: 100 },
            isPreview,
            true,
        );
    }

    createElementWithoutConnection(
        pipelineModel: PipelineElementConfig[],
        pipelineElement: PipelineElementUnion,
        x: number,
        y: number,
    ) {
        const pipelineElementConfig =
            this.createNewPipelineElementConfigAtPosition(
                x,
                y,
                pipelineElement,
                false,
            );
        pipelineModel.push(pipelineElementConfig);

        setTimeout(() => {
            this.elementDropped(
                pipelineElementConfig.payload.dom,
                pipelineElementConfig.payload,
                true,
                false,
            );
            this.getBridge(false).repaintEverything();
        }, 10);
    }

    createElement(
        pipelineModel: PipelineElementConfig[],
        pipelineElement: InvocablePipelineElementUnion,
        sourceElementDomId: string,
    ) {
        const sourceElement = $('#' + sourceElementDomId);

        const pipelineElementConfig =
            this.createNewPipelineElementConfigWithFixedCoordinates(
                sourceElement,
                pipelineElement,
                false,
            );
        pipelineModel.push(pipelineElementConfig);
        setTimeout(() => {
            this.createAssemblyElement(
                pipelineElementConfig.payload.dom,
                pipelineElementConfig.payload as InvocablePipelineElementUnion,
                sourceElement,
                false,
            );
        });
    }

    createAssemblyElement(
        pipelineElementDomId: string,
        pipelineElement: InvocablePipelineElementUnion,
        sourceElement,
        previewConfig: boolean,
    ) {
        let targetElementId;
        if (pipelineElement instanceof DataProcessorInvocation) {
            targetElementId = this.dataProcessorDropped(
                pipelineElementDomId,
                pipelineElement as DataProcessorInvocation,
                true,
                false,
            );
            this.connectNodes(sourceElement, targetElementId, previewConfig);
        } else {
            targetElementId = this.dataSinkDropped(
                pipelineElementDomId,
                pipelineElement,
                true,
                false,
            );
            this.connectNodes(sourceElement, targetElementId, previewConfig);
        }
    }

    connectNodes(
        sourceElementSelector,
        targetElementId,
        previewConfig: boolean,
    ) {
        let options;
        const sourceElement = sourceElementSelector.get()[0];
        const jsplumbBridge = this.getBridge(previewConfig);
        const jsplumbConfig =
            this.jsplumbEndpointService.getJsplumbConfig(true);
        options = sourceElementSelector.hasClass('stream')
            ? jsplumbConfig.streamEndpointOptions
            : jsplumbConfig.sepaEndpointOptions;
        let sourceEndPoint;
        const selectedEndpoints = jsplumbBridge.selectEndpoints({
            source: sourceElement,
        });
        sourceEndPoint =
            selectedEndpoints.length > 0
                ? !selectedEndpoints.get(0).isFull()
                    ? jsplumbBridge
                          .selectEndpoints({ source: sourceElement })
                          .get(0)
                    : jsplumbBridge.addEndpoint(sourceElement, options)
                : jsplumbBridge.addEndpoint(sourceElement, options);

        const targetElement = document.getElementById(targetElementId);
        const targetEndPoint = jsplumbBridge
            .selectEndpoints({ target: targetElement })
            .get(0);

        jsplumbBridge.connect({
            source: sourceEndPoint,
            target: targetEndPoint,
            detachable: true,
        });
        jsplumbBridge.repaintEverything();
    }

    createNewPipelineElementConfigWithFixedCoordinates(
        sourceElement,
        pipelineElement: InvocablePipelineElementUnion,
        isPreview,
    ): PipelineElementConfig {
        const x = sourceElement.position().left;
        const y = sourceElement.position().top;
        return this.createNewPipelineElementConfigAtPosition(
            x,
            y,
            pipelineElement,
            isPreview,
        );
    }

    createNewPipelineElementConfigAtPosition(
        x: number,
        y: number,
        json: any,
        isPreview: boolean,
    ): PipelineElementConfig {
        const coord = { x: x + 200, y: y };
        return this.createNewPipelineElementConfig(
            json,
            coord,
            isPreview,
            false,
        );
    }

    createNewPipelineElementConfig(
        pipelineElement: PipelineElementUnion,
        coordinates,
        isPreview: boolean,
        isCompleted: boolean,
        newElementId?: string,
    ): PipelineElementConfig {
        const displaySettings = isPreview
            ? 'connectable-preview'
            : 'connectable-editor';
        const connectable = 'connectable';
        const pipelineElementConfig = {} as PipelineElementConfig;
        pipelineElementConfig.type = PipelineElementTypeUtils.toCssShortHand(
            PipelineElementTypeUtils.fromType(pipelineElement),
        );
        pipelineElementConfig.payload = this.clone(
            pipelineElement,
            newElementId,
        );
        pipelineElementConfig.settings = {
            connectable,
            openCustomize: !(pipelineElement as any).configured,
            preview: isPreview,
            completed:
                pipelineElement instanceof SpDataStream ||
                isPreview ||
                isCompleted
                    ? PipelineElementConfigurationStatus.OK
                    : PipelineElementConfigurationStatus.INCOMPLETE,
            disabled: false,
            loadingStatus: false,
            displaySettings,
            position: {
                x: coordinates.x,
                y: coordinates.y,
            },
        };
        if (!pipelineElementConfig.payload.dom) {
            pipelineElementConfig.payload.dom =
                'jsplumb_' + this.idCounter + '_' + this.makeId(4);
            this.idCounter++;
        }

        return pipelineElementConfig;
    }

    clone(pipelineElement: PipelineElementUnion, newElementId?: string) {
        if (pipelineElement instanceof SpDataStream) {
            const cloned = SpDataStream.fromData(
                pipelineElement,
                new SpDataStream(),
            );
            return cloned;
        } else if (pipelineElement instanceof DataProcessorInvocation) {
            const clonedPe = DataProcessorInvocation.fromData(
                pipelineElement,
                new DataProcessorInvocation(),
            );
            if (newElementId) {
                this.updateElementIds(clonedPe, newElementId);
            }
            return clonedPe;
        } else if (pipelineElement instanceof DataSinkInvocation) {
            const clonedPe = DataSinkInvocation.fromData(
                pipelineElement as DataSinkInvocation,
                new DataSinkInvocation(),
            );
            if (newElementId) {
                this.updateElementIds(clonedPe, newElementId);
            }
            return clonedPe;
        }
    }

    updateElementIds(
        pipelineElement: PipelineElementUnion,
        newElementId: string,
    ) {
        pipelineElement.elementId = newElementId;
        pipelineElement.uri = newElementId;
    }

    makeId(count: number) {
        let text = '';
        const possible =
            'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';

        for (let i = 0; i < count; i++) {
            text += possible.charAt(
                Math.floor(Math.random() * possible.length),
            );
        }

        return text;
    }

    elementDropped(
        pipelineElementDomId: string,
        pipelineElement: PipelineElementUnion,
        endpoints: boolean,
        preview: boolean,
    ): string {
        if (pipelineElement instanceof SpDataStream) {
            return this.dataStreamDropped(
                pipelineElementDomId,
                pipelineElement as SpDataStream,
                endpoints,
                preview,
            );
        } else if (pipelineElement instanceof DataProcessorInvocation) {
            return this.dataProcessorDropped(
                pipelineElementDomId,
                pipelineElement,
                endpoints,
                preview,
            );
        } else if (pipelineElement instanceof DataSinkInvocation) {
            return this.dataSinkDropped(
                pipelineElementDomId,
                pipelineElement,
                endpoints,
                preview,
            );
        }
    }

    dataStreamDropped(
        pipelineElementDomId: string,
        pipelineElement: SpDataStream,
        endpoints: boolean,
        preview: boolean,
    ) {
        const jsplumbBridge = this.getBridge(preview);
        if (endpoints) {
            const endpointOptions =
                this.jsplumbEndpointService.getStreamEndpoint(
                    preview,
                    pipelineElementDomId,
                );
            jsplumbBridge.addEndpoint(pipelineElementDomId, endpointOptions);
        }
        return pipelineElementDomId;
    }

    dataProcessorDropped(
        pipelineElementDomId: string,
        pipelineElement: DataProcessorInvocation,
        endpoints: boolean,
        preview: boolean,
    ): string {
        const jsplumbBridge = this.getBridge(preview);
        this.dataSinkDropped(
            pipelineElementDomId,
            pipelineElement,
            endpoints,
            preview,
        );
        if (endpoints) {
            jsplumbBridge.addEndpoint(
                pipelineElementDomId,
                this.jsplumbEndpointService.getOutputEndpoint(
                    preview,
                    pipelineElementDomId,
                ),
            );
        }
        return pipelineElementDomId;
    }

    dataSinkDropped(
        pipelineElementDomId: string,
        pipelineElement: InvocablePipelineElementUnion,
        endpoints: boolean,
        preview: boolean,
    ): string {
        const jsplumbBridge = this.getBridge(preview);
        if (endpoints) {
            if (pipelineElement.inputStreams.length < 2) {
                // 1 InputNode
                jsplumbBridge.addEndpoint(
                    pipelineElementDomId,
                    this.jsplumbEndpointService.getInputEndpoint(
                        preview,
                        pipelineElementDomId,
                        0,
                    ),
                );
            } else {
                jsplumbBridge.addEndpoint(
                    pipelineElementDomId,
                    this.jsplumbEndpointService.getNewTargetPoint(
                        preview,
                        0,
                        0.3,
                        pipelineElementDomId,
                        0,
                    ),
                );
                jsplumbBridge.addEndpoint(
                    pipelineElementDomId,
                    this.jsplumbEndpointService.getNewTargetPoint(
                        preview,
                        0,
                        0.7,
                        pipelineElementDomId,
                        1,
                    ),
                );
            }
        }
        return pipelineElementDomId;
    }

    getBridge(previewConfig: boolean): JsplumbBridge {
        return this.jsplumbFactory.getJsplumbBridge(previewConfig);
    }
}
