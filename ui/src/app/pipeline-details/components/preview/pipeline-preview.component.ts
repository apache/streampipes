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
    AfterViewInit,
    Component,
    EventEmitter,
    Input,
    OnInit,
    Output,
    ViewChild,
} from '@angular/core';
import {
    Pipeline,
    PipelineCanvasMetadata,
    SpMetricsEntry,
} from '@streampipes/platform-services';
import {
    PipelineElementConfig,
    PipelineElementUnion,
} from '../../../editor/model/editor.model';
import { JsplumbService } from '../../../editor/services/jsplumb.service';
import { JsplumbBridge } from '../../../editor/services/jsplumb-bridge.service';
import { PipelineAssemblyDrawingAreaComponent } from '../../../editor/components/pipeline-assembly/pipeline-assembly-drawing-area/pipeline-assembly-drawing-area.component';

@Component({
    selector: 'sp-pipeline-preview',
    templateUrl: './pipeline-preview.component.html',
    styleUrls: ['./pipeline-preview.component.scss'],
})
export class PipelinePreviewComponent implements OnInit, AfterViewInit {
    @Input()
    metricsInfo: Record<string, SpMetricsEntry>;

    rawPipelineModel: PipelineElementConfig[];

    @Input()
    pipeline: Pipeline;

    @Input()
    pipelineCanvasMetadata: PipelineCanvasMetadata;

    @Output()
    selectedElementEmitter: EventEmitter<PipelineElementUnion> =
        new EventEmitter<PipelineElementUnion>();

    @ViewChild('pipelineDrawingAreaComponent')
    pipelineDrawingAreaComponent: PipelineAssemblyDrawingAreaComponent;

    jsPlumbBridge: JsplumbBridge;

    constructor(private jsplumbService: JsplumbService) {}

    ngAfterViewInit() {
        this.jsPlumbBridge = this.jsplumbService.getBridge(true);
    }

    ngOnInit() {
        this.rawPipelineModel = this.jsplumbService.makeRawPipeline(
            this.pipeline,
            true,
        );
    }

    toggleLivePreview(): void {
        this.pipelineDrawingAreaComponent?.togglePipelineElementLivePreview();
    }
}
