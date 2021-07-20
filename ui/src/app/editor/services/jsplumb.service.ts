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
import {Injectable} from "@angular/core";
import {
    InvocablePipelineElementUnion,
    PipelineElementConfig,
    PipelineElementUnion
} from "../model/editor.model";
import {PipelineElementTypeUtils} from "../utils/editor.utils";
import {
    DataProcessorInvocation,
    DataSinkInvocation,
    Pipeline,
    SpDataSet,
    SpDataStream,
    SpDataStreamUnion
} from "../../core-model/gen/streampipes-model";
import {PipelineElementDraggedService} from "./pipeline-element-dragged.service";
import {JsplumbEndpointService} from "./jsplumb-endpoint.service";
import {JsplumbFactoryService} from "./jsplumb-factory.service";
import { EditorService } from './editor.service';

@Injectable()
export class JsplumbService {

    idCounter: number = 0;

    constructor(private JsplumbConfigService: JsplumbConfigService,
                private JsplumbFactory: JsplumbFactoryService,
                private jsplumbEndpointService: JsplumbEndpointService,
                private editorService: EditorService,
                private pipelineElementDraggedService: PipelineElementDraggedService) {
    }

    isFullyConnected(pipelineElementConfig: PipelineElementConfig,
                     previewConfig: boolean) {
        let jsplumbBridge = this.JsplumbFactory.getJsplumbBridge(previewConfig);
        let payload = pipelineElementConfig.payload as InvocablePipelineElementUnion;
        return payload.inputStreams == null ||
          jsplumbBridge.getConnections({target: $("#" +payload.dom)}).length == payload.inputStreams.length;
    }

    makeRawPipeline(pipelineModel: Pipeline,
                    isPreview: boolean) {
        return pipelineModel
          .streams
          .map(s => this.toConfig(s, "stream", isPreview))
          .concat(pipelineModel.sepas.map(s => this.toConfig(s, "sepa", isPreview)))
          .concat(pipelineModel.actions.map(s => this.toConfig(s, "action", isPreview)));
    }

    toConfig(pe: PipelineElementUnion,
             type: string,
             isPreview: boolean) {
        (pe as any).type = type;
        return this.createNewPipelineElementConfig(pe, {x: 100, y: 100}, isPreview, true);
    }

    createElementWithoutConnection(pipelineModel: PipelineElementConfig[],
                                   pipelineElement: PipelineElementUnion,
                                   x: number,
                                   y: number) {
        let pipelineElementConfig = this.createNewPipelineElementConfigAtPosition(x, y, pipelineElement, false);
        pipelineModel.push(pipelineElementConfig);

        if (pipelineElementConfig.payload instanceof SpDataSet) {
            this.editorService.updateDataSet(pipelineElement).subscribe(data => {
                (pipelineElementConfig.payload as SpDataSet).eventGrounding = data.eventGrounding;
                (pipelineElementConfig.payload as SpDataSet).datasetInvocationId = data.invocationId;

                setTimeout(() => {
                    this.elementDropped(pipelineElementConfig.payload.dom,
                      pipelineElementConfig.payload,
                      true,
                      false);
                }, 1);
            });
        } else {
            setTimeout(() => {
                this.elementDropped(pipelineElementConfig.payload.dom,
                  pipelineElementConfig.payload,
                  true,
                  false);
            }, 100);
        }

    }

    createElement(pipelineModel: PipelineElementConfig[],
                  pipelineElement: InvocablePipelineElementUnion,
                  pipelineElementDomId: string) {
        var pipelineElementDom = $("#" + pipelineElementDomId);

        let pipelineElementConfig = this.createNewPipelineElementConfigWithFixedCoordinates(pipelineElementDom, pipelineElement, false);
        pipelineModel.push(pipelineElementConfig);
        setTimeout(() => {
            this.createAssemblyElement(pipelineElementConfig.payload.dom,
              pipelineElementConfig.payload as InvocablePipelineElementUnion,
              pipelineElementDom,
              false);
        });
    }

    createAssemblyElement(pipelineElementDomId: string,
                          pipelineElement: InvocablePipelineElementUnion,
                          $parentElement,
                          previewConfig: boolean) {
        var $target;
        if (pipelineElement instanceof DataProcessorInvocation) {
            $target = this.dataProcessorDropped(pipelineElementDomId, pipelineElement as DataProcessorInvocation, true, false);
            this.connectNodes($parentElement, $target, previewConfig);
        } else {
            $target = this.dataSinkDropped(pipelineElementDomId, pipelineElement, true, false);
            this.connectNodes($parentElement, $target, previewConfig);
        }
    }

    connectNodes($parentElement,
                 $target,
                 previewConfig: boolean) {
        var options;
        let jsplumbBridge = this.getBridge(previewConfig);
        if ($parentElement.hasClass("stream")) {
            // TODO: getJsplumbConfig depends on isPreview. Not implemented yet
            options = this.jsplumbEndpointService.getJsplumbConfig(true).streamEndpointOptions;
        } else {
            // TODO: getJsplumbConfig depends on isPreview. Not implemented yet
            options = this.jsplumbEndpointService.getJsplumbConfig(true).sepaEndpointOptions;
        }
        var sourceEndPoint;
        if (jsplumbBridge.selectEndpoints({source: $parentElement}).length > 0) {
            if (!(jsplumbBridge.selectEndpoints({source: $parentElement}).get(0).isFull())) {
                sourceEndPoint = jsplumbBridge.selectEndpoints({source: $parentElement}).get(0)
            } else {
                sourceEndPoint = jsplumbBridge.addEndpoint($parentElement, options);
            }
        } else {
            sourceEndPoint = jsplumbBridge.addEndpoint($parentElement, options);
        }

        var targetEndPoint = jsplumbBridge.selectEndpoints({target: $target}).get(0);

        jsplumbBridge.connect({source: sourceEndPoint, target: targetEndPoint, detachable: true});
        jsplumbBridge.repaintEverything();
    }

    createNewPipelineElementConfigWithFixedCoordinates($parentElement, json, isPreview): PipelineElementConfig {
        var x = $parentElement.position().left;
        var y = $parentElement.position().top;
        return this.createNewPipelineElementConfigAtPosition(x, y, json, isPreview);
    }

    createNewPipelineElementConfigAtPosition(x: number,
                                             y: number,
                                             json: any,
                                             isPreview: boolean): PipelineElementConfig {
        let coord = {'x': x + 200, 'y': y};
        return this.createNewPipelineElementConfig(json, coord, isPreview, false);
    }

    createNewPipelineElementConfig(pipelineElement: PipelineElementUnion,
                                   coordinates,
                                   isPreview: boolean,
                                   isCompleted: boolean,
                                   newElementId?: string): PipelineElementConfig {
        let displaySettings = isPreview ? 'connectable-preview' : 'connectable-editor';
        let connectable = "connectable";
        let pipelineElementConfig = {} as PipelineElementConfig;
        pipelineElementConfig.type = PipelineElementTypeUtils
          .toCssShortHand(PipelineElementTypeUtils.fromType(pipelineElement))
        pipelineElementConfig.payload = this.clone(pipelineElement, newElementId);
        pipelineElementConfig.settings = {connectable: connectable,
            openCustomize: !(pipelineElement as any).configured,
            preview: isPreview,
            completed: (pipelineElement instanceof SpDataStream || pipelineElement instanceof SpDataSet || isPreview || isCompleted),
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

    clone(pipelineElement: PipelineElementUnion, newElementId?: string) {
        if (pipelineElement instanceof SpDataSet) {
            return SpDataSet.fromData(pipelineElement, new SpDataSet());
        } else if (pipelineElement instanceof SpDataStream) {
            return SpDataStream.fromData(pipelineElement, new SpDataStream());
        } else if (pipelineElement instanceof DataProcessorInvocation) {
            let clonedPe = DataProcessorInvocation.fromData(pipelineElement, new DataProcessorInvocation());
            if (newElementId) {
                this.updateElementIds(clonedPe, newElementId)
            }
            return clonedPe;
        } else {
            let clonedPe = DataSinkInvocation.fromData(pipelineElement, new DataSinkInvocation());
            if (newElementId) {
                this.updateElementIds(clonedPe, newElementId);
            }
            return clonedPe;
        }
    }

    updateElementIds(pipelineElement: PipelineElementUnion, newElementId: string) {
        pipelineElement.elementId = newElementId;
        pipelineElement.uri = newElementId;
    }

    makeId(count: number) {
        var text = "";
        var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        for (var i = 0; i < count; i++)
            text += possible.charAt(Math.floor(Math.random() * possible.length));

        return text;
    }

    elementDropped(pipelineElementDomId: string,
                   pipelineElement: PipelineElementUnion,
                   endpoints: boolean,
                   preview: boolean): string {
        if (pipelineElement instanceof SpDataStream) {
            return this.dataStreamDropped(pipelineElementDomId, pipelineElement as SpDataStream, endpoints, preview);
        } else if (pipelineElement instanceof SpDataSet) {
            return this.dataSetDropped(pipelineElementDomId, pipelineElement as SpDataSet, endpoints, preview);
        } else if (pipelineElement instanceof DataProcessorInvocation) {
            return this.dataProcessorDropped(pipelineElementDomId, pipelineElement, endpoints, preview);
        } else if (pipelineElement instanceof DataSinkInvocation) {
            return this.dataSinkDropped(pipelineElementDomId, pipelineElement, endpoints, preview);
        };
    }

    dataSetDropped(pipelineElementDomId: string,
                   pipelineElement: SpDataSet,
                   endpoints: boolean,
                   preview: boolean) {



        let jsplumbBridge = this.getBridge(preview);

        if (endpoints) {
            this.makeDraggableIfNotPreview(pipelineElementDomId, jsplumbBridge, preview);
            let endpointOptions = this.jsplumbEndpointService.getStreamEndpoint(preview, pipelineElementDomId);
            jsplumbBridge.addEndpoint(pipelineElementDomId, endpointOptions);
        }
        return pipelineElementDomId;
    };


    dataStreamDropped(pipelineElementDomId: string,
                      pipelineElement: SpDataStreamUnion,
                      endpoints: boolean,
                      preview: boolean) {
        let jsplumbBridge = this.getBridge(preview);
        if (endpoints) {
            this.makeDraggableIfNotPreview(pipelineElementDomId, jsplumbBridge, preview);
            let endpointOptions = this.jsplumbEndpointService.getStreamEndpoint(preview, pipelineElementDomId);
            jsplumbBridge.addEndpoint(pipelineElementDomId, endpointOptions);
        }
        return pipelineElementDomId;
    };

    dataProcessorDropped(pipelineElementDomId: string,
                         pipelineElement: DataProcessorInvocation,
                         endpoints: boolean,
                         preview: boolean): string {
        let jsplumbBridge = this.getBridge(preview);
        this.dataSinkDropped(pipelineElementDomId, pipelineElement, endpoints, preview);
        if (endpoints) {
            jsplumbBridge.addEndpoint(pipelineElementDomId,
              this.jsplumbEndpointService.getOutputEndpoint(preview, pipelineElementDomId));
        }
        return pipelineElementDomId;
    };

    dataSinkDropped(pipelineElementDomId: string,
                    pipelineElement: InvocablePipelineElementUnion,
                    endpoints: boolean,
                    preview: boolean): string {
        let jsplumbBridge = this.getBridge(preview);
        this.makeDraggableIfNotPreview(pipelineElementDomId, jsplumbBridge, preview);

        if (endpoints) {
            if (pipelineElement.inputStreams.length < 2) { //1 InputNode
                jsplumbBridge.addEndpoint(pipelineElementDomId,
                  this.jsplumbEndpointService.getInputEndpoint(preview, pipelineElementDomId, 0));
            } else {
                jsplumbBridge.addEndpoint(pipelineElementDomId,
                  this.jsplumbEndpointService.getNewTargetPoint(preview, 0, 0.25, pipelineElementDomId, 0));
                jsplumbBridge.addEndpoint(pipelineElementDomId,
                  this.jsplumbEndpointService.getNewTargetPoint(preview, 0, 0.75, pipelineElementDomId, 1));
            }
        }
        return pipelineElementDomId;
    };

    getBridge(previewConfig: boolean): JsplumbBridge {
        return this.JsplumbFactory.getJsplumbBridge(previewConfig);
    }

    makeDraggableIfNotPreview(pipelineElementDomId: string,
                              jsplumbBridge: JsplumbBridge,
                              previewConfig: boolean) {
        if (!previewConfig) {
            jsplumbBridge.draggable(pipelineElementDomId, {containment: 'parent',
                drag: (e => {
                    this.pipelineElementDraggedService.notify({x: e.pos[0], y: e.pos[1]});
                })
            });
        }
    }
}
