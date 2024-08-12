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
    HostListener,
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
    PipelineEdgeValidation,
    PipelineModificationMessage,
    PipelinePreviewModel,
    SpDataStream,
    SpMetricsEntry,
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
import { JsplumbFactoryService } from '../../services/jsplumb-factory.service';
import {
    EVENT_CONNECTION,
    EVENT_CONNECTION_ABORT,
    EVENT_CONNECTION_DETACHED,
    EVENT_CONNECTION_DRAG,
    EVENT_CONNECTION_MOVED,
} from '@jsplumb/browser-ui';
import { PipelineStyleService } from '../../services/pipeline-style.service';
import { IdGeneratorService } from '../../../core-services/id-generator/id-generator.service';

@Component({
    selector: 'sp-pipeline',
    templateUrl: './pipeline.component.html',
})
export class PipelineComponent implements OnInit, OnDestroy {
    @Input()
    pipelineValid: boolean;

    @Input()
    rawPipelineModel: PipelineElementConfig[];

    @Input()
    allElements: PipelineElementUnion[];

    @Input()
    readonly: boolean;

    @Input()
    metricsInfo: Record<string, SpMetricsEntry>;

    @Output()
    deletePreviewEmitter: EventEmitter<boolean> = new EventEmitter<boolean>();

    @Output()
    triggerPipelineCacheUpdateEmitter: EventEmitter<void> = new EventEmitter();

    currentMouseOverElement = '';
    currentPipelineModel: Pipeline;
    idCounter: any;
    currentZoomLevel: any;

    JsplumbBridge: JsplumbBridge;

    @Input()
    previewModeActive = false;

    @Input()
    pipelinePreview: PipelinePreviewModel;

    shouldOpenCustomizeSettings = false;

    constructor(
        private jsplumbService: JsplumbService,
        private pipelineEditorService: PipelineEditorService,
        private jsplumbFactoryService: JsplumbFactoryService,
        private objectProvider: ObjectProvider,
        private editorService: EditorService,
        private idGeneratorService: IdGeneratorService,
        private shepherdService: ShepherdService,
        private pipelineStyleService: PipelineStyleService,
        private pipelineValidationService: PipelineValidationService,
        private dialogService: DialogService,
        private dialog: MatDialog,
        private ngZone: NgZone,
    ) {
        this.currentPipelineModel = new Pipeline();
        this.idCounter = 0;

        this.currentZoomLevel = 1;
    }

    ngOnInit() {
        this.JsplumbBridge = this.jsplumbFactoryService.getJsplumbBridge(
            this.readonly,
        );
        if (!this.readonly) {
            this.initAssembly();
            this.initPlumb();
        }
    }

    getCssStyle(
        pipelineElementConfig: PipelineElementConfig,
    ): Record<string, string> {
        return {
            position: 'absolute',
            width: '90px',
            height: '90px',
            left: pipelineElementConfig.settings.position.x + 'px',
            top: pipelineElementConfig.settings.position.y + 'px',
        };
    }

    validatePipeline(pm?: PipelineModificationMessage) {
        setTimeout(() => {
            this.ngZone.run(() => {
                this.pipelineValid =
                    this.pipelineValidationService.isValidPipeline(
                        this.rawPipelineModel.filter(
                            pe => !pe.settings.disabled,
                        ),
                        this.readonly,
                        pm,
                    );
            });
        });
    }

    ngOnDestroy() {
        this.deletePreviewEmitter.emit(false);
        this.jsplumbFactoryService.destroy();
    }

    @HostListener('window:beforeunload')
    onWindowClose() {
        this.ngOnDestroy();
    }

    updateMouseover(elementId: string) {
        this.currentMouseOverElement = elementId;
    }

    updateOptionsClick(elementId: string) {
        this.currentMouseOverElement =
            this.currentMouseOverElement === elementId ? '' : elementId;
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
                        this.idGeneratorService.generate(5);
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
                this.JsplumbBridge.repaintEverything();
                this.validatePipeline();
                this.triggerPipelineCacheUpdateEmitter.emit();
            },
        });
    }

    checkTopicModel(pipelineElementConfig: PipelineElementConfig) {
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
        const index = this.rawPipelineModel.findIndex(
            pe => pe.payload.dom === pipelineElement.payload.dom,
        );
        this.rawPipelineModel.splice(index, 1);
        if (this.rawPipelineModel.every(pe => pe.settings.disabled)) {
            this.editorService.makePipelineAssemblyEmpty(true);
        }
        this.JsplumbBridge.repaintEverything();
        this.validatePipeline();
        this.triggerPipelineCacheUpdateEmitter.emit();
    }

    initPlumb() {
        this.JsplumbBridge.unbind(EVENT_CONNECTION);

        this.JsplumbBridge.bind(EVENT_CONNECTION_DRAG, () => {
            this.shouldOpenCustomizeSettings = true;
        });

        this.JsplumbBridge.bind(EVENT_CONNECTION_ABORT, () => {
            this.shouldOpenCustomizeSettings = false;
        });

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

            if (
                this.shouldOpenCustomizeSettings ||
                info.connection.data.openCustomize
            ) {
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
                                this.currentConnectionValid(edgeValidations);
                            if (currentConnectionValid) {
                                this.validatePipeline(
                                    pipelineModificationMessage,
                                );
                                this.modifyPipeline(
                                    pipelineModificationMessage,
                                );
                                if (
                                    this.jsplumbService.isFullyConnected(
                                        pe,
                                        this.readonly,
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
                                        this.triggerPipelineCacheUpdateEmitter.emit();
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

    currentConnectionValid(targetEdges: PipelineEdgeValidation[]) {
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
                    if (
                        modification.inputStreams &&
                        modification.inputStreams.length > 0
                    ) {
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

    isCustomOutput(pe: PipelineElementConfig) {
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

    showErrorDialog(title: string, description: string) {
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
                        this.triggerPipelineCacheUpdateEmitter.emit();
                        this.announceConfiguredElement(pipelineElementConfig);
                        if (this.previewModeActive) {
                            this.deletePreviewEmitter.emit(true);
                        }
                        this.validatePipeline(pm);
                    });
            } else {
                this.validatePipeline();
            }
        });
    }

    announceConfiguredElement(pe: PipelineElementConfig) {
        this.editorService.announceConfiguredElement(pe.payload.dom);
    }

    triggerPipelineModification() {
        this.currentPipelineModel = this.objectProvider.makePipeline(
            this.rawPipelineModel,
        );
        this.objectProvider
            .updatePipeline(this.currentPipelineModel)
            .subscribe(pm => {
                this.modifyPipeline(pm);
                this.pipelineValid =
                    this.pipelineValidationService.isValidPipeline(
                        this.rawPipelineModel,
                        this.readonly,
                        pm,
                    );
            });
    }
}
