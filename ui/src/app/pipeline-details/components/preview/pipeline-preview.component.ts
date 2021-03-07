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

import {Component, EventEmitter, Input, OnInit, Output} from "@angular/core";
import {Pipeline} from "../../../core-model/gen/streampipes-model";
import {PipelineElementConfig, PipelineElementUnion} from "../../../editor/model/editor.model";
import {PipelinePositioningService} from "../../../editor/services/pipeline-positioning.service";
import {JsplumbService} from "../../../editor/services/jsplumb.service";
import {ObjectProvider} from "../../../editor/services/object-provider.service";
import {JsplumbFactoryService} from "../../../editor/services/jsplumb-factory.service";

@Component({
    selector: 'pipeline-preview',
    templateUrl: './pipeline-preview.component.html',
    styleUrls: ['./pipeline-preview.component.scss']
})
export class PipelinePreviewComponent implements OnInit {

    @Input()
    jspcanvas: string;

    rawPipelineModel: PipelineElementConfig[];

    @Input()
    pipeline: Pipeline;

    @Output()
    selectedElementEmitter: EventEmitter<PipelineElementUnion> = new EventEmitter<PipelineElementUnion>();

    constructor(private PipelinePositioningService: PipelinePositioningService,
                private JsplumbService: JsplumbService,
                private JsplumbFactoryService: JsplumbFactoryService,
                private ObjectProvider: ObjectProvider) {
        this.PipelinePositioningService = PipelinePositioningService;
        this.ObjectProvider = ObjectProvider;
    }

    ngOnInit() {
        setTimeout(() => {
            var elid = "#" + this.jspcanvas;
            this.rawPipelineModel = this.JsplumbService.makeRawPipeline(this.pipeline, true);
            setTimeout(() => {
                this.PipelinePositioningService.displayPipeline(this.rawPipelineModel, elid, true, true);
                var existingEndpointIds = [];
                setTimeout(() => {
                    this.JsplumbFactoryService.getJsplumbBridge(true).selectEndpoints().each(endpoint => {
                        if (existingEndpointIds.indexOf(endpoint.element.id) === -1) {
                            $(endpoint.element).click(() => {
                                let payload = this.ObjectProvider.findElement(endpoint.element.id, this.rawPipelineModel).payload;
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
