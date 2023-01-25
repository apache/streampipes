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
    ElementRef,
    EventEmitter,
    Input,
    NgZone,
    OnInit,
    Output,
    ViewChild,
} from '@angular/core';
import { JsplumbBridge } from '../../services/jsplumb-bridge.service';
import { PipelinePositioningService } from '../../services/pipeline-positioning.service';
import { PipelineValidationService } from '../../services/pipeline-validation.service';
import { JsplumbService } from '../../services/jsplumb.service';
import { ShepherdService } from '../../../services/tour/shepherd.service';
import {
    PipelineElementConfig,
    PipelineElementUnion,
} from '../../model/editor.model';
import { ObjectProvider } from '../../services/object-provider.service';
import {
    ConfirmDialogComponent,
    DialogService,
    PanelType,
    SpBreadcrumbService,
} from '@streampipes/shared-ui';
import { SavePipelineComponent } from '../../dialog/save-pipeline/save-pipeline.component';
import { MatDialog } from '@angular/material/dialog';
import { EditorService } from '../../services/editor.service';
import {
    PipelineCanvasMetadata,
    PipelineCanvasMetadataService,
    PipelineService,
} from '@streampipes/platform-services';
import { JsplumbFactoryService } from '../../services/jsplumb-factory.service';
import Panzoom, { PanzoomObject } from '@panzoom/panzoom';
import { PipelineElementDraggedService } from '../../services/pipeline-element-dragged.service';
import { PipelineComponent } from '../pipeline/pipeline.component';
import { forkJoin } from 'rxjs';
import { PipelineElementDiscoveryComponent } from '../../dialog/pipeline-element-discovery/pipeline-element-discovery.component';
import { SpPipelineRoutes } from '../../../pipelines/pipelines.routes';
import { Router } from '@angular/router';

@Component({
    selector: 'sp-pipeline-assembly',
    templateUrl: './pipeline-assembly.component.html',
    styleUrls: ['./pipeline-assembly.component.scss'],
})
export class PipelineAssemblyComponent implements OnInit, AfterViewInit {
    @Input()
    rawPipelineModel: PipelineElementConfig[];

    @Input()
    currentModifiedPipelineId: any;

    @Input()
    allElements: PipelineElementUnion[];

    @Output()
    pipelineCanvasMaximizedEmitter: EventEmitter<boolean> =
        new EventEmitter<boolean>();

    JsplumbBridge: JsplumbBridge;

    pipelineCanvasMaximized = false;

    currentMouseOverElement: any;
    currentZoomLevel: any;
    preview: any;

    selectMode: any;
    currentPipelineName: any;
    currentPipelineDescription: any;

    pipelineValid = false;

    pipelineCacheRunning = false;
    pipelineCached = false;

    pipelineCanvasMetadata: PipelineCanvasMetadata =
        new PipelineCanvasMetadata();
    pipelineCanvasMetadataAvailable = false;

    config: any = {};
    @ViewChild('outerCanvas') pipelineCanvas: ElementRef;

    @ViewChild('pipelineComponent')
    pipelineComponent: PipelineComponent;

    panzoom: PanzoomObject;

    constructor(
        private jsPlumbFactoryService: JsplumbFactoryService,
        private pipelinePositioningService: PipelinePositioningService,
        private objectProvider: ObjectProvider,
        public editorService: EditorService,
        public pipelineValidationService: PipelineValidationService,
        private pipelineService: PipelineService,
        private jsplumbService: JsplumbService,
        private shepherdService: ShepherdService,
        private dialogService: DialogService,
        private dialog: MatDialog,
        private ngZone: NgZone,
        private pipelineElementDraggedService: PipelineElementDraggedService,
        private pipelineCanvasMetadataService: PipelineCanvasMetadataService,
        private breadcrumbService: SpBreadcrumbService,
        private router: Router,
    ) {
        this.selectMode = true;
        this.currentZoomLevel = 1;
    }

    ngOnInit(): void {
        if (this.currentModifiedPipelineId) {
            this.displayPipelineById();
        } else {
            this.checkAndDisplayCachedPipeline();
        }
        this.pipelineElementDraggedService.pipelineElementMovedSubject.subscribe(
            position => {
                const offsetHeight =
                    this.pipelineCanvas.nativeElement.offsetHeight;
                const offsetWidth =
                    this.pipelineCanvas.nativeElement.offsetWidth;
                const currentPan = this.panzoom.getPan();
                let xOffset = 0;
                let yOffset = 0;
                if (position.y + currentPan.y > offsetHeight - 100) {
                    yOffset = -10;
                }
                if (position.x + currentPan.x > offsetWidth - 100) {
                    xOffset = -10;
                }
                if (xOffset < 0 || yOffset < 0) {
                    this.pan(xOffset, yOffset);
                }
            },
        );
    }

    ngAfterViewInit() {
        this.JsplumbBridge = this.jsPlumbFactoryService.getJsplumbBridge(
            this.preview,
        );
        const elem = document.getElementById('assembly');
        this.panzoom = Panzoom(elem, {
            maxScale: 5,
            excludeClass: 'sp-no-pan',
            canvas: true,
            contain: 'outside',
        });
    }

    autoLayout() {
        this.pipelinePositioningService.layoutGraph(
            '#assembly',
            "div[id^='jsplumb']",
            110,
            false,
        );
        this.JsplumbBridge.repaintEverything();
    }

    zoomOut() {
        this.doZoom(true);
    }

    zoomIn() {
        this.doZoom(false);
    }

    doZoom(zoomOut) {
        zoomOut ? this.panzoom.zoomOut() : this.panzoom.zoomIn();
        this.currentZoomLevel = this.panzoom.getScale();
        this.JsplumbBridge.setZoom(this.currentZoomLevel);
        this.JsplumbBridge.repaintEverything();
    }

    showClearAssemblyConfirmDialog(event: any) {
        const dialogRef = this.dialog.open(ConfirmDialogComponent, {
            width: '500px',
            data: {
                title: 'Do you really want to delete the current pipeline?',
                subtitle: 'This cannot be undone.',
                cancelTitle: 'No',
                okTitle: 'Yes',
                confirmAndCancel: true,
            },
        });
        dialogRef.afterClosed().subscribe(ev => {
            if (ev) {
                if (this.currentModifiedPipelineId) {
                    this.currentModifiedPipelineId = undefined;
                }
                this.clearAssembly();
                this.editorService.makePipelineAssemblyEmpty(true);
            }
        });
    }

    /**
     * clears the Assembly of all elements
     */
    clearAssembly() {
        // $('#assembly').children().not('#clear, #submit').remove();
        this.JsplumbBridge.deleteEveryEndpoint();
        this.rawPipelineModel = [];
        this.currentZoomLevel = 1;
        this.JsplumbBridge.setZoom(this.currentZoomLevel);
        this.JsplumbBridge.repaintEverything();

        const removePipelineFromCache =
            this.editorService.removePipelineFromCache();
        const removeCanvasMetadataFromCache =
            this.editorService.removeCanvasMetadataFromCache();
        forkJoin([
            removePipelineFromCache,
            removeCanvasMetadataFromCache,
        ]).subscribe(msg => {
            this.pipelineCached = false;
            this.pipelineCacheRunning = false;
        });
    }

    /**
     * Sends the pipeline to the server
     */
    submit() {
        const pipelineModel = this.pipelineComponent.rawPipelineModel;
        const pipeline = this.objectProvider.makeFinalPipeline(pipelineModel);
        this.pipelinePositioningService.collectPipelineElementPositions(
            this.pipelineCanvasMetadata,
            pipelineModel,
        );
        pipeline.name = this.currentPipelineName;
        pipeline.description = this.currentPipelineDescription;
        if (this.currentModifiedPipelineId) {
            pipeline._id = this.currentModifiedPipelineId;
        }

        this.dialogService.open(SavePipelineComponent, {
            panelType: PanelType.SLIDE_IN_PANEL,
            title: 'Save pipeline',
            data: {
                pipeline: pipeline,
                currentModifiedPipelineId: this.currentModifiedPipelineId,
                pipelineCanvasMetadata: this.pipelineCanvasMetadata,
            },
        });
    }

    checkAndDisplayCachedPipeline() {
        const cachedPipeline = this.editorService.getCachedPipeline();
        const cachedCanvasMetadata =
            this.editorService.getCachedPipelineCanvasMetadata();
        forkJoin([cachedPipeline, cachedCanvasMetadata]).subscribe(results => {
            if (results[0] && results[0].length > 0) {
                this.rawPipelineModel = results[0] as PipelineElementConfig[];
                this.handleCanvasMetadataResponse(results[1]);
            }
        });
    }

    displayPipelineById() {
        const pipelineRequest = this.pipelineService.getPipelineById(
            this.currentModifiedPipelineId,
        );
        const canvasRequest =
            this.pipelineCanvasMetadataService.getPipelineCanvasMetadata(
                this.currentModifiedPipelineId,
            );
        pipelineRequest.subscribe(pipelineResp => {
            const pipeline = pipelineResp;
            this.currentPipelineName = pipeline.name;
            this.breadcrumbService.updateBreadcrumb([
                SpPipelineRoutes.BASE,
                { label: pipeline.name },
                { label: 'Modify' },
            ]);
            this.currentPipelineDescription = pipeline.description;
            this.rawPipelineModel = this.jsplumbService.makeRawPipeline(
                pipeline,
                false,
            );
            canvasRequest.subscribe(
                canvasResp => {
                    this.handleCanvasMetadataResponse(canvasResp);
                },
                error => {
                    this.handleCanvasMetadataResponse(undefined);
                },
            );
        });
    }

    handleCanvasMetadataResponse(canvasMetadata: PipelineCanvasMetadata) {
        if (canvasMetadata) {
            this.pipelineCanvasMetadata = canvasMetadata;
            this.pipelineCanvasMetadataAvailable = true;
        } else {
            this.pipelineCanvasMetadataAvailable = false;
            this.pipelineCanvasMetadata = new PipelineCanvasMetadata();
        }
        this.displayPipelineInEditor(
            !this.pipelineCanvasMetadataAvailable,
            this.pipelineCanvasMetadata,
        );
    }

    displayPipelineInEditor(
        autoLayout,
        pipelineCanvasMetadata?: PipelineCanvasMetadata,
    ) {
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
            this.pipelineComponent.triggerPipelineModification();
        });
    }

    isPipelineAssemblyEmpty() {
        return (
            this.rawPipelineModel.length === 0 ||
            this.rawPipelineModel.every(pe => pe.settings.disabled)
        );
    }

    panLeft() {
        this.pan(100, 0);
    }

    panRight() {
        this.pan(-100, 0);
    }

    panUp() {
        this.pan(0, 100);
    }

    panDown() {
        this.pan(0, -100);
    }

    panHome() {
        this.panAbsolute(0, 0);
    }

    pan(xOffset: number, yOffset: number) {
        const currentPan = this.panzoom.getPan();
        const panX = Math.min(0, currentPan.x + xOffset);
        const panY = Math.min(0, currentPan.y + yOffset);
        this.panzoom.pan(panX, panY);
    }

    panAbsolute(x: number, y: number) {
        this.panzoom.pan(x, y);
    }

    triggerPipelinePreview() {
        this.pipelineComponent.initiatePipelineElementPreview();
    }

    openDiscoverDialog() {
        this.dialogService.open(PipelineElementDiscoveryComponent, {
            panelType: PanelType.SLIDE_IN_PANEL,
            title: 'Discover pipeline elements',
            width: '50vw',
            data: {
                currentElements: this.allElements,
                rawPipelineModel: this.rawPipelineModel,
            },
        });
    }
}
