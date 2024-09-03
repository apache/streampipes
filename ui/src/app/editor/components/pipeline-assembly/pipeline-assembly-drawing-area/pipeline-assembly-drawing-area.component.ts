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
    ElementRef,
    EventEmitter,
    Input,
    NgZone,
    OnInit,
    Output,
    ViewChild,
} from '@angular/core';
import { JsplumbBridge } from '../../../services/jsplumb-bridge.service';
import { PipelineComponent } from '../../pipeline/pipeline.component';
import {
    PipelineElementConfig,
    PipelineElementUnion,
} from '../../../model/editor.model';
import { PipelineAssemblyDrawingAreaPanZoomComponent } from './pipeline-assembly-drawing-area-pan-zoom/pipeline-assembly-drawing-area-pan-zoom.component';
import { PipelineValidationService } from '../../../services/pipeline-validation.service';
import {
    PipelineCanvasMetadata,
    PipelinePreviewModel,
    SpMetricsEntry,
} from '@streampipes/platform-services';
import { EditorService } from '../../../services/editor.service';
import { PipelinePositioningService } from '../../../services/pipeline-positioning.service';
import { HttpDownloadProgressEvent } from '@angular/common/http';
import { LivePreviewService } from '../../../../services/live-preview.service';
import { ObjectProvider } from '../../../services/object-provider.service';
import { Subscription } from 'rxjs';

@Component({
    selector: 'sp-pipeline-assembly-drawing-area',
    templateUrl: './pipeline-assembly-drawing-area.component.html',
    styleUrls: ['./pipeline-assembly-drawing-area.component.scss'],
})
export class PipelineAssemblyDrawingAreaComponent implements OnInit {
    @Input()
    jsplumbBridge: JsplumbBridge;

    @Input()
    allElements: PipelineElementUnion[];

    @Input()
    rawPipelineModel: PipelineElementConfig[];

    @Input()
    pipelineCanvasMetadata: PipelineCanvasMetadata;

    @Input()
    pipelineCanvasMetadataAvailable: boolean;

    @Input()
    previewModeActive: boolean;

    @Input()
    readonly: boolean;

    @Input()
    metricsInfo: Record<string, SpMetricsEntry>;

    @Output()
    triggerPipelineCacheUpdateEmitter: EventEmitter<void> = new EventEmitter();

    pipelineValid = false;
    pipelinePreview: PipelinePreviewModel;
    pipelinePreviewSub: Subscription;

    @ViewChild('pipelineComponent')
    pipelineComponent: PipelineComponent;

    @ViewChild('zoomComponent')
    zoomComponent: PipelineAssemblyDrawingAreaPanZoomComponent;
    @ViewChild('outerCanvas') pipelineCanvas: ElementRef;

    constructor(
        public pipelineValidationService: PipelineValidationService,
        private ngZone: NgZone,
        private editorService: EditorService,
        private pipelinePositioningService: PipelinePositioningService,
        private livePreviewService: LivePreviewService,
        private objectProvider: ObjectProvider,
    ) {}

    ngOnInit(): void {
        if (this.rawPipelineModel.length > 0) {
            this.displayPipelineInEditor(
                !this.pipelineCanvasMetadataAvailable,
                this.pipelineCanvasMetadata,
            );
        }
    }

    isPipelineAssemblyEmpty() {
        return (
            this.rawPipelineModel.length === 0 ||
            this.rawPipelineModel.every(pe => pe.settings.disabled)
        );
    }

    displayPipelineInEditor(
        autoLayout: boolean,
        pipelineCanvasMetadata?: PipelineCanvasMetadata,
    ): void {
        setTimeout(() => {
            this.pipelinePositioningService.displayPipeline(
                this.rawPipelineModel,
                '#assembly',
                false,
                autoLayout,
                pipelineCanvasMetadata,
            );

            this.editorService.makePipelineAssemblyEmpty(false);
            this.ngZone.run(() => {
                this.pipelineValid =
                    this.pipelineValidationService.isValidPipeline(
                        this.rawPipelineModel.filter(
                            pe => !pe.settings.disabled,
                        ),
                        false,
                    );
            });
            if (!this.readonly) {
                this.pipelineComponent.triggerPipelineModification();
            }
        });
    }

    togglePipelineElementLivePreview() {
        if (!this.previewModeActive) {
            const pipeline = this.objectProvider.makePipeline(
                this.rawPipelineModel,
            );
            this.editorService
                .initiatePipelinePreview(pipeline)
                .subscribe(response => {
                    this.pipelinePreview = response;
                    this.previewModeActive = true;
                    this.pipelinePreviewSub = this.editorService
                        .getPipelinePreviewResult(response.previewId)
                        .subscribe(res => {
                            const data = this.livePreviewService.convert(
                                res as HttpDownloadProgressEvent,
                            );
                            if (data) {
                                this.livePreviewService.eventSub.next(data);
                            }
                        });
                });
        } else {
            this.deletePipelineElementPreview(false);
        }
    }

    deletePipelineElementPreview(resume: boolean) {
        if (this.previewModeActive) {
            this.pipelinePreviewSub?.unsubscribe();
            this.editorService
                .deletePipelinePreviewRequest(this.pipelinePreview.previewId)
                .subscribe(() => {
                    this.previewModeActive = false;
                    if (resume) {
                        this.togglePipelineElementLivePreview();
                    }
                });
        }
    }

    resetZoom(): void {
        this.zoomComponent.resetZoom();
    }
}
