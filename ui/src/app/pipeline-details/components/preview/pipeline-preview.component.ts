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
    Component,
    EventEmitter,
    Input,
    OnDestroy,
    OnInit,
    Output,
} from '@angular/core';
import { Pipeline } from '@streampipes/platform-services';
import {
    PipelineElementConfig,
    PipelineElementUnion,
} from '../../../editor/model/editor.model';
import { PipelinePositioningService } from '../../../editor/services/pipeline-positioning.service';
import { JsplumbService } from '../../../editor/services/jsplumb.service';
import { ObjectProvider } from '../../../editor/services/object-provider.service';
import { JsplumbFactoryService } from '../../../editor/services/jsplumb-factory.service';

@Component({
    selector: 'sp-pipeline-preview',
    templateUrl: './pipeline-preview.component.html',
    styleUrls: ['./pipeline-preview.component.scss'],
})
export class PipelinePreviewComponent implements OnInit {
    @Input()
    jspcanvas: string;

    rawPipelineModel: PipelineElementConfig[];

    @Input()
    pipeline: Pipeline;

    @Output()
    selectedElementEmitter: EventEmitter<PipelineElementUnion> =
        new EventEmitter<PipelineElementUnion>();

    constructor(
        private pipelinePositioningService: PipelinePositioningService,
        private jsplumbService: JsplumbService,
        private jsplumbFactoryService: JsplumbFactoryService,
        private objectProvider: ObjectProvider,
    ) {}

    ngOnInit() {
        setTimeout(() => {
            const elid = '#' + this.jspcanvas;
            this.rawPipelineModel = this.jsplumbService.makeRawPipeline(
                this.pipeline,
                true,
            );
            setTimeout(() => {
                this.pipelinePositioningService.displayPipeline(
                    this.rawPipelineModel,
                    elid,
                    true,
                    true,
                );
                const existingEndpointIds = [];
                setTimeout(() => {
                    this.jsplumbFactoryService
                        .getJsplumbBridge(true)
                        .selectEndpoints()
                        .each(endpoint => {
                            if (
                                existingEndpointIds.indexOf(
                                    endpoint.element.id,
                                ) === -1
                            ) {
                                $(endpoint.element).click(() => {
                                    const payload =
                                        this.objectProvider.findElement(
                                            endpoint.element.id,
                                            this.rawPipelineModel,
                                        ).payload;
                                    this.selectedElementEmitter.emit(payload);
                                });
                                existingEndpointIds.push(endpoint.element.id);
                            }
                        });
                });
            });
        });
    }
}
