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

import { AfterViewInit, Component, Input, ViewChild } from '@angular/core';
import { JsplumbBridge } from '../../services/jsplumb-bridge.service';
import { PipelinePositioningService } from '../../services/pipeline-positioning.service';
import { PipelineValidationService } from '../../services/pipeline-validation.service';
import {
    PipelineElementConfig,
    PipelineElementUnion,
} from '../../model/editor.model';
import { ObjectProvider } from '../../services/object-provider.service';
import { DialogService, PanelType } from '@streampipes/shared-ui';
import { SavePipelineComponent } from '../../dialog/save-pipeline/save-pipeline.component';
import { EditorService } from '../../services/editor.service';
import {
    Pipeline,
    PipelineCanvasMetadata,
} from '@streampipes/platform-services';
import { JsplumbFactoryService } from '../../services/jsplumb-factory.service';
import { forkJoin } from 'rxjs';
import { Router } from '@angular/router';
import { PipelineAssemblyDrawingAreaComponent } from './pipeline-assembly-drawing-area/pipeline-assembly-drawing-area.component';
import { PipelineAssemblyOptionsComponent } from './pipeline-assembly-options/pipeline-assembly-options.component';

@Component({
    selector: 'sp-pipeline-assembly',
    templateUrl: './pipeline-assembly.component.html',
    styleUrls: ['./pipeline-assembly.component.scss'],
})
export class PipelineAssemblyComponent implements AfterViewInit {
    @Input()
    rawPipelineModel: PipelineElementConfig[];

    @Input()
    originalPipeline: Pipeline;

    @Input()
    pipelineCanvasMetadata: PipelineCanvasMetadata;

    @Input()
    pipelineCanvasMetadataAvailable = false;

    @Input()
    allElements: PipelineElementUnion[];

    previewModeActive = false;
    readonly: boolean;

    JsplumbBridge: JsplumbBridge;

    @ViewChild('assemblyOptionsComponent')
    assemblyOptionsComponent: PipelineAssemblyOptionsComponent;
    @ViewChild('drawingAreaComponent')
    drawingAreaComponent: PipelineAssemblyDrawingAreaComponent;

    constructor(
        private jsPlumbFactoryService: JsplumbFactoryService,
        private pipelinePositioningService: PipelinePositioningService,
        private objectProvider: ObjectProvider,
        public editorService: EditorService,
        public pipelineValidationService: PipelineValidationService,
        private dialogService: DialogService,
        private router: Router,
    ) {}

    ngAfterViewInit() {
        this.JsplumbBridge = this.jsPlumbFactoryService.getJsplumbBridge(
            this.readonly,
        );
    }

    /**
     * clears the Assembly of all elements
     */
    clearAssembly() {
        this.editorService.makePipelineAssemblyEmpty(true);
        this.JsplumbBridge.deleteEveryEndpoint();
        this.rawPipelineModel = [];
        this.drawingAreaComponent.resetZoom();
        this.JsplumbBridge.repaintEverything();

        forkJoin([
            this.editorService.removePipelineFromCache(),
            this.editorService.removeCanvasMetadataFromCache(),
        ]).subscribe(() => {
            this.pipelineCanvasMetadata = new PipelineCanvasMetadata();
            if (this.originalPipeline) {
                this.router.navigate(['pipelines', 'create']);
            }
        });
    }

    /**
     * Sends the pipeline to the server
     */
    submit() {
        //const pipelineModel = this.pipelineComponent.rawPipelineModel;
        const pipelineModel = this.rawPipelineModel;
        const pipeline = this.objectProvider.makePipeline(pipelineModel);
        this.pipelinePositioningService.collectPipelineElementPositions(
            this.pipelineCanvasMetadata,
            pipelineModel,
        );
        pipeline.valid = this.pipelineValidationService.isValidPipeline(
            pipelineModel,
            this.readonly,
        );
        const dialogRef = this.dialogService.open(SavePipelineComponent, {
            panelType: PanelType.SLIDE_IN_PANEL,
            disableClose: true,
            title: 'Save pipeline',
            width: '40vw',
            data: {
                pipeline: pipeline,
                originalPipeline: this.originalPipeline,
                pipelineCanvasMetadata: this.pipelineCanvasMetadata,
            },
        });
        dialogRef
            .afterClosed()
            .subscribe((config: { reload: boolean; pipelineId: string }) => {
                if (config?.reload) {
                    this.clearAssembly();
                    this.rawPipelineModel = [];
                    setTimeout(() => {
                        this.router.navigate(['pipelines', 'create']);
                    });
                }
            });
    }

    togglePreview(): void {
        this.previewModeActive = !this.previewModeActive;
        this.drawingAreaComponent.togglePipelineElementLivePreview();
    }

    triggerCacheUpdate(): void {
        this.assemblyOptionsComponent.triggerCacheUpdate();
    }
}
