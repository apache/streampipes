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

import {
    BrowserJsPlumbInstance,
    ContainmentType,
    JsPlumbInstance,
    newInstance,
} from '@jsplumb/browser-ui';
import { Injectable } from '@angular/core';
import { JsplumbBridge } from './jsplumb-bridge.service';
import { PipelineElementDraggedService } from './pipeline-element-dragged.service';
import { JsplumbConfigService } from './jsplumb-config.service';

@Injectable({ providedIn: 'root' })
export class JsplumbFactoryService {
    pipelineEditorInstance: BrowserJsPlumbInstance;
    pipelineEditorBridge: JsplumbBridge;

    constructor(
        private pipelineElementDraggedService: PipelineElementDraggedService,
        private jsplumbConfigService: JsplumbConfigService,
    ) {}

    getJsplumbBridge(previewConfig: boolean): JsplumbBridge {
        if (!this.pipelineEditorBridge) {
            this.pipelineEditorInstance = previewConfig
                ? this.makePipelinePreviewInstance()
                : this.makePipelineEditorInstance();
            this.prepareJsplumb(this.pipelineEditorInstance);
            this.pipelineEditorBridge = new JsplumbBridge(
                this.pipelineEditorInstance,
            );
        }
        return this.pipelineEditorBridge;
    }

    makePipelineEditorInstance(): BrowserJsPlumbInstance {
        return newInstance({
            container: document.getElementById('assembly'),
            dragOptions: {
                containment: ContainmentType.parent,
                cursor: 'pointer',
                zIndex: 2000,
                drag: params => {
                    this.pipelineElementDraggedService.notify({
                        x: params.pos.x,
                        y: params.pos.y,
                    });
                },
            },
        });
    }

    makePipelinePreviewInstance(): BrowserJsPlumbInstance {
        return newInstance({
            container: document.getElementById('assembly'),
            elementsDraggable: false,
        });
    }

    prepareJsplumb(jsplumbInstance: JsPlumbInstance) {
        jsplumbInstance.registerEndpointTypes(
            this.jsplumbConfigService.getEndpointTypeConfig(),
        );
    }

    destroy() {
        this.pipelineEditorInstance?.destroy();
        this.pipelineEditorBridge = undefined;
    }
}
