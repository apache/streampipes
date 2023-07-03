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

import { PipelineValidationService } from '../../services/pipeline-validation.service';
import { JsplumbService } from '../../services/jsplumb.service';
import { PipelineEditorService } from '../../services/pipeline-editor.service';
import { JsplumbBridge } from '../../services/jsplumb-bridge.service';
import { ShepherdService } from '../../../services/tour/shepherd.service';
import {
    Component,
    EventEmitter,
    Input,
    NgZone,
    OnDestroy,
    OnInit,
    Output,
} from '@angular/core';
import {
    InvocablePipelineElementUnion,
    PipelineElementConfig,
    PipelineElementConfigurationStatus,
    PipelineElementUnion,
} from '../../model/editor.model';
import {
    CustomOutputStrategy,
    DataProcessorInvocation,
    DataSinkInvocation,
    Notification,
    Pipeline,
    PipelineCanvasMetadata,
    PipelineEdgeValidation,
    PipelineModificationMessage,
    PipelinePreviewModel,
    SpDataStream,
} from '@streampipes/platform-services';
import { ObjectProvider } from '../../services/object-provider.service';
import { CustomizeComponent } from '../../dialog/customize/customize.component';
import {
    ConfirmDialogComponent,
    DialogService,
    PanelType,
} from '@streampipes/shared-ui';
import { EditorService } from '../../services/editor.service';
import { MatchingErrorComponent } from '../../dialog/matching-error/matching-error.component';
import { MatDialog } from '@angular/material/dialog';
import { forkJoin } from 'rxjs';
import { JsplumbFactoryService } from '../../services/jsplumb-factory.service';
import { PipelinePositioningService } from '../../services/pipeline-positioning.service';
import {
    EVENT_CONNECTION,
    EVENT_CONNECTION_ABORT,
    EVENT_CONNECTION_DETACHED,
    EVENT_CONNECTION_DRAG,
    EVENT_CONNECTION_MOVED,
} from '@jsplumb/browser-ui';
import { PipelineStyleService } from '../../services/pipeline-style.service';

@Component({
    selector: 'sp-pipeline',
    templateUrl: './pipeline.component.html',
    styleUrls: ['./pipeline.component.css'],
})
export class PipelineComponent implements OnInit, OnDestroy {
    @Input()
    pipelineValid: boolean;

    @Input()
    canvasId: string;

    @Input()
    rawPipelineModel: PipelineElementConfig[];

    @Input()
    allElements: PipelineElementUnion[];

    @Input()
    preview: boolean;

    @Input()
    pipelineCached: boolean;

    @Output()
    pipelineCachedChanged: EventEmitter<boolean> = new EventEmitter<boolean>();

    @Input()
    pipelineCacheRunning: boolean;

    @Input()
    pipelineCanvasMetadata: PipelineCanvasMetadata;

    @Output()
    pipelineCacheRunningChanged: EventEmitter<boolean> =
        new EventEmitter<boolean>();

    currentMouseOverElement: string;
    currentPipelineModel: Pipeline;
    idCounter: any;
    currentZoomLevel: any;

    canvasWidth = '100%';
    canvasHeight = '100%';

    JsplumbBridge: JsplumbBridge;

    previewModeActive = false;
    pipelinePreview: PipelinePreviewModel;

    constructor(
        private jsplumbService: JsplumbService,
        private pipelineEditorService: PipelineEditorService,
        private pipelinePositioningService: PipelinePositioningService,
        private jsplumbFactoryService: JsplumbFactoryService,
        private objectProvider: ObjectProvider,
        private editorService: EditorService,
        private shepherdService: ShepherdService,
        private pipelineStyleService: PipelineStyleService,
        private pipelineValidationService: PipelineValidationService,
        private dialogService: DialogService,
        private dialog: MatDialog,
        private ngZone: NgZone,
    ) {
        this.currentMouseOverElement = '';
        this.currentPipelineModel = new Pipeline();
        this.idCounter = 0;

        this.currentZoomLevel = 1;
    }

    ngOnInit() {
        this.JsplumbBridge = this.jsplumbFactoryService.getJsplumbBridge(
            this.preview,
        );
        this.initAssembly();
        this.initPlumb();
    }

    validatePipeline() {
        setTimeout(() => {
            this.ngZone.run(() => {
                this.pipelineValid =
                    this.pipelineValidationService.isValidPipeline(
                        this.rawPipelineModel.filter(
                            pe => !pe.settings.disabled,
                        ),
                        this.preview,
                    );
            });
        });
    }

    ngOnDestroy() {
        this.deletePipelineElementPreview(false);
        this.jsplumbFactoryService.destroy(this.preview);
    }

    updateMouseover(elementId) {
        this.currentMouseOverElement = elementId;
    }

    updateOptionsClick(elementId) {
        this.currentMouseOverElement =
            this.currentMouseOverElement === elementId ? '' : elementId;
    }

    getElementCss(currentPipelineElementSettings) {
        return (
            'position:absolute;' +
            (this.preview ? 'width:75px;' : 'width:90px;') +
            (this.preview ? 'height:75px;' : 'height:90px;') +
            'left: ' +
            currentPipelineElementSettings.position.x +
            'px; ' +
            'top: ' +
            currentPipelineElementSettings.position.y +
            'px; '
        );
    }

    getElementCssClasses(currentPipelineElement) {
        return (
            currentPipelineElement.type +
            ' ' +
            (currentPipelineElement.settings.openCustomize ? '' : '') +
            currentPipelineElement.settings.connectable +
            ' ' +
            currentPipelineElement.settings.displaySettings
        );
    }

    isStreamInPipeline() {
        return this.isInPipeline('stream');
    }

    isSetInPipeline() {
        return this.isInPipeline('set');
    }

    isInPipeline(type) {
        return this.rawPipelineModel.some(
            x => x.type === type && !x.settings.disabled,
        );
    }

    showMixedStreamAlert() {
        this.dialog.open(ConfirmDialogComponent, {
            width: '500px',
            data: {
                title: 'Currently, it is not possible to mix data streams and data sets in a single pipeline.',
                confirmAndCancel: false,
                okTitle: 'Ok',
            },
        });
    }

    findPipelineElementByElementId(elementId: string) {
        return this.allElements.find(a => a.elementId === elementId);
    }

    initAssembly() {
        ($('#assembly') as any).droppable({
            tolerance: 'fit',
            drop: (element, ui) => {
                const pipelineElementId = ui.draggable.data('pe');
                const pipelineElement: PipelineElementUnion =
                    this.findPipelineElementByElementId(pipelineElementId);
                if (ui.draggable.hasClass('draggable-pipeline-element')) {
                    this.editorService.makePipelineAssemblyEmpty(false);
                    const newElementId =
                        pipelineElement.elementId +
                        ':' +
                        this.jsplumbService.makeId(5);
                    const pipelineElementConfig =
                        this.jsplumbService.createNewPipelineElementConfig(
                            pipelineElement,
                            this.pipelineEditorService.getCoordinates(
                                ui,
                                this.currentZoomLevel,
                            ),
                            false,
                            false,
                            newElementId,
                        );
                    if (
                        (this.isStreamInPipeline() &&
                            pipelineElementConfig.type === 'set') ||
                        (this.isSetInPipeline() &&
                            pipelineElementConfig.type === 'stream')
                    ) {
                        this.showMixedStreamAlert();
                    } else {
                        this.rawPipelineModel.push(pipelineElementConfig);

                        if (pipelineElementConfig.type === 'stream') {
                            this.checkTopicModel(pipelineElementConfig);
                        } else if (pipelineElementConfig.type === 'sepa') {
                            setTimeout(() => {
                                this.jsplumbService.dataProcessorDropped(
                                    pipelineElementConfig.payload.dom,
                                    pipelineElementConfig.payload as DataProcessorInvocation,
                                    true,
                                    false,
                                );
                            }, 10);
                        } else if (pipelineElementConfig.type === 'action') {
                            setTimeout(() => {
                                this.jsplumbService.dataSinkDropped(
                                    pipelineElementConfig.payload.dom,
                                    pipelineElementConfig.payload as DataSinkInvocation,
                                    true,
                                    false,
                                );
                            }, 10);
                        }
                        if (this.shepherdService.isTourActive()) {
                            this.shepherdService.trigger(
                                'drop-' + pipelineElementConfig.type,
                            );
                        }
                    }
                }
                this.JsplumbBridge.repaintEverything();
                this.validatePipeline();
                this.triggerPipelineCacheUpdate();
            },
        });
    }

    checkTopicModel(pipelineElementConfig: PipelineElementConfig) {
        console.log(pipelineElementConfig);
        setTimeout(() => {
            this.jsplumbService.dataStreamDropped(
                pipelineElementConfig.payload.dom,
                pipelineElementConfig.payload as SpDataStream,
                true,
                false,
            );
        }, 10);

        const streamDescription = pipelineElementConfig.payload as SpDataStream;
        if (
            streamDescription.eventGrounding.transportProtocols[0]
                .topicDefinition['@class'] ===
            'org.apache.streampipes.model.grounding.WildcardTopicDefinition'
        ) {
            // this.EditorDialogManager.showCustomizeStreamDialog(streamDescription);
        }
    }

    handleDeleteOption(pipelineElement: PipelineElementConfig) {
        this.JsplumbBridge.removeAllEndpoints(pipelineElement.payload.dom);
        this.rawPipelineModel = this.rawPipelineModel.filter(
            pe => !(pe.payload.dom === pipelineElement.payload.dom),
        );
        if (this.rawPipelineModel.every(pe => pe.settings.disabled)) {
            this.editorService.makePipelineAssemblyEmpty(true);
        }
        this.JsplumbBridge.repaintEverything();
        this.validatePipeline();
        this.triggerPipelineCacheUpdate();
    }

    initPlumb() {
        this.JsplumbBridge.unbind(EVENT_CONNECTION);

        this.JsplumbBridge.bind(EVENT_CONNECTION_MOVED, info => {
            const pe = this.objectProvider.findElement(
                info.newTargetEndpoint.elementId,
                this.rawPipelineModel,
            );
            const oldPe = this.objectProvider.findElement(
                info.originalTargetEndpoint.elementId,
                this.rawPipelineModel,
            );
            (oldPe.payload as InvocablePipelineElementUnion).configured = false;
            (pe.payload as InvocablePipelineElementUnion).configured = false;
        });

        this.JsplumbBridge.bind(EVENT_CONNECTION_DETACHED, info => {
            const pe = this.objectProvider.findElement(
                info.targetEndpoint.elementId,
                this.rawPipelineModel,
            );
            (pe.payload as InvocablePipelineElementUnion).configured = false;
            pe.settings.openCustomize = true;
            info.targetEndpoint.setType('empty');
            this.JsplumbBridge.repaintEverything();
            this.validatePipeline();
        });

        this.JsplumbBridge.bind(EVENT_CONNECTION_DRAG, () => {
            this.JsplumbBridge.selectEndpoints().each(endpoint => {
                if (endpoint.isTarget && endpoint.connections.length === 0) {
                    endpoint.setType('highlight');
                }
            });
            this.JsplumbBridge.repaintEverything();
        });
        this.JsplumbBridge.bind(EVENT_CONNECTION_ABORT, () => {
            this.JsplumbBridge.selectEndpoints().each(endpoint => {
                if (endpoint.isTarget && endpoint.connections.length === 0) {
                    endpoint.setType('empty');
                }
            });
            this.JsplumbBridge.repaintEverything();
        });

        this.JsplumbBridge.bind(EVENT_CONNECTION, info => {
            const pe = this.objectProvider.findElement(
                info.target.id,
                this.rawPipelineModel,
            );
            if (pe.settings.openCustomize) {
                this.currentPipelineModel = this.objectProvider.makePipeline(
                    this.rawPipelineModel,
                );
                pe.settings.loadingStatus = true;
                this.objectProvider
                    .updatePipeline(this.currentPipelineModel)
                    .subscribe(
                        pipelineModificationMessage => {
                            pe.settings.loadingStatus = false;
                            const edgeValidations =
                                this.getTargetEdgeValidations(
                                    pipelineModificationMessage,
                                    info.target.id,
                                );
                            const currentConnectionValid =
                                this.currentConnectionValid(
                                    pe,
                                    edgeValidations,
                                );
                            if (currentConnectionValid) {
                                this.validatePipeline();
                                this.modifyPipeline(
                                    pipelineModificationMessage,
                                );
                                if (
                                    this.jsplumbService.isFullyConnected(
                                        pe,
                                        this.preview,
                                    )
                                ) {
                                    const payload =
                                        pe.payload as InvocablePipelineElementUnion;
                                    if (
                                        (payload.staticProperties &&
                                            payload.staticProperties.length >
                                                0) ||
                                        this.isCustomOutput(pe)
                                    ) {
                                        this.showCustomizeDialog(pe);
                                    } else {
                                        (
                                            pe.payload as InvocablePipelineElementUnion
                                        ).configured = true;
                                        this.pipelineStyleService.updatePeConfigurationStatus(
                                            pe,
                                            PipelineElementConfigurationStatus.OK,
                                        );
                                        this.announceConfiguredElement(pe);
                                        this.triggerPipelineCacheUpdate();
                                    }
                                }
                            } else {
                                this.JsplumbBridge.detach(info.connection);
                                const invalidEdgeValidation =
                                    edgeValidations.find(
                                        e => e.sourceId === info.source.id,
                                    );
                                if (invalidEdgeValidation) {
                                    this.showMatchingErrorDialog(
                                        invalidEdgeValidation.status
                                            .notifications,
                                    );
                                }
                            }
                        },
                        status => {
                            pe.settings.loadingStatus = false;
                            this.showErrorDialog(
                                status.error.title,
                                status.error.description,
                            );
                        },
                    );
            }
        });

        window.onresize = () => {
            this.JsplumbBridge.repaintEverything();
        };
    }

    currentConnectionValid(
        pe: PipelineElementConfig,
        targetEdges: PipelineEdgeValidation[],
    ) {
        const entity = pe.payload as InvocablePipelineElementUnion;
        return targetEdges.every(
            e => e.status.validationStatusType === 'COMPLETE',
        );
    }

    getTargetEdgeValidations(
        pipelineModificationMessage: PipelineModificationMessage,
        targetDomId: string,
    ): PipelineEdgeValidation[] {
        const edgeValidations = pipelineModificationMessage.edgeValidations;
        return edgeValidations.filter(ev => ev.targetId === targetDomId);
    }

    modifyPipeline(pm: PipelineModificationMessage) {
        if (pm.pipelineModifications) {
            pm.pipelineModifications.forEach(modification => {
                const id = modification.domId;
                if (id !== 'undefined') {
                    const pe = this.objectProvider.findElement(
                        id,
                        this.rawPipelineModel,
                    );
                    if (modification.staticProperties) {
                        (
                            pe.payload as InvocablePipelineElementUnion
                        ).staticProperties = modification.staticProperties;
                    }
                    if (pe.payload instanceof DataProcessorInvocation) {
                        if (modification.outputStrategies) {
                            (
                                pe.payload as DataProcessorInvocation
                            ).outputStrategies = modification.outputStrategies;
                        }
                        if (modification.outputStream) {
                            (
                                pe.payload as DataProcessorInvocation
                            ).outputStream = modification.outputStream;
                        }
                    }
                    if (modification.inputStreams) {
                        (
                            pe.payload as InvocablePipelineElementUnion
                        ).inputStreams = modification.inputStreams;
                    }
                    if (modification.validationInfos.length > 0) {
                        this.pipelineStyleService.updatePeConfigurationStatus(
                            pe,
                            PipelineElementConfigurationStatus.MODIFIED,
                        );
                    }
                }
            });
        }
        if (pm.edgeValidations) {
            this.pipelineStyleService.updateAllConnectorStyles(
                pm.edgeValidations,
            );
            this.pipelineStyleService.updateAllEndpointStyles(
                pm.edgeValidations,
            );
            this.JsplumbBridge.repaintEverything();
        }
    }

    isCustomOutput(pe) {
        let custom = false;
        if (pe.payload instanceof DataProcessorInvocation) {
            pe.payload.outputStrategies.forEach(strategy => {
                if (strategy instanceof CustomOutputStrategy) {
                    custom = true;
                }
            });
        }
        return custom;
    }

    triggerPipelineCacheUpdate() {
        setTimeout(() => {
            this.pipelineCacheRunning = true;
            this.pipelineCacheRunningChanged.emit(this.pipelineCacheRunning);
            this.pipelinePositioningService.collectPipelineElementPositions(
                this.pipelineCanvasMetadata,
                this.rawPipelineModel,
            );
            const updateCachedPipeline =
                this.editorService.updateCachedPipeline(this.rawPipelineModel);
            const updateCachedCanvasMetadata =
                this.editorService.updateCachedCanvasMetadata(
                    this.pipelineCanvasMetadata,
                );
            forkJoin([
                updateCachedPipeline,
                updateCachedCanvasMetadata,
            ]).subscribe(() => {
                this.pipelineCacheRunning = false;
                this.pipelineCacheRunningChanged.emit(
                    this.pipelineCacheRunning,
                );
                this.pipelineCached = true;
                this.pipelineCachedChanged.emit(this.pipelineCached);
            });
        });
    }

    showErrorDialog(title, description) {
        this.dialog.open(ConfirmDialogComponent, {
            width: '500px',
            data: {
                title: title,
                subtitle: description,
                okTitle: 'Ok',
                confirmAndCancel: false,
            },
        });
    }

    showMatchingErrorDialog(notifications: Notification[]) {
        this.dialogService.open(MatchingErrorComponent, {
            panelType: PanelType.STANDARD_PANEL,
            title: 'Invalid Connection',
            data: {
                notifications: notifications,
            },
        });
    }

    showCustomizeDialog(pipelineElementConfig: PipelineElementConfig) {
        const dialogRef = this.dialogService.open(CustomizeComponent, {
            panelType: PanelType.SLIDE_IN_PANEL,
            title: 'Customize ' + pipelineElementConfig.payload.name,
            width: '50vw',
            data: {
                pipelineElement: pipelineElementConfig,
            },
        });

        dialogRef.afterClosed().subscribe(c => {
            if (c) {
                pipelineElementConfig.settings.openCustomize = false;
                (
                    pipelineElementConfig.payload as InvocablePipelineElementUnion
                ).configured = true;
                this.currentPipelineModel = this.objectProvider.makePipeline(
                    this.rawPipelineModel,
                );
                this.objectProvider
                    .updatePipeline(this.currentPipelineModel)
                    .subscribe(pm => {
                        this.modifyPipeline(pm);
                        // if (!(pipelineElementConfig.payload instanceof DataSinkInvocation)) {
                        //   this.JsplumbBridge.activateEndpoint('out-' + pipelineElementConfig.payload.dom, pipelineElementConfig.settings.completed);
                        // }
                        this.triggerPipelineCacheUpdate();
                        this.announceConfiguredElement(pipelineElementConfig);
                        if (this.previewModeActive) {
                            this.deletePipelineElementPreview(true);
                        }
                        this.validatePipeline();
                    });
            } else {
                this.validatePipeline();
            }
        });
    }

    announceConfiguredElement(pe: PipelineElementConfig) {
        this.editorService.announceConfiguredElement(pe.payload.dom);
    }

    initiatePipelineElementPreview() {
        if (!this.previewModeActive) {
            const pipeline = this.objectProvider.makePipeline(
                this.rawPipelineModel,
            );
            this.editorService
                .initiatePipelinePreview(pipeline)
                .subscribe(response => {
                    this.pipelinePreview = response;
                    this.previewModeActive = true;
                });
        } else {
            this.deletePipelineElementPreview(false);
        }
    }

    deletePipelineElementPreview(resume: boolean) {
        if (this.previewModeActive) {
            this.editorService
                .deletePipelinePreviewRequest(this.pipelinePreview.previewId)
                .subscribe(() => {
                    this.previewModeActive = false;
                    if (resume) {
                        this.initiatePipelineElementPreview();
                    }
                });
        }
    }

    triggerPipelineModification() {
        this.currentPipelineModel = this.objectProvider.makePipeline(
            this.rawPipelineModel,
        );
        this.objectProvider
            .updatePipeline(this.currentPipelineModel)
            .subscribe(pm => this.modifyPipeline(pm));
    }
}
