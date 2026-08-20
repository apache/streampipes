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

import { EventEmitter, inject, Injectable } from '@angular/core';
import {
    Message,
    Pipeline,
    PipelineService,
    PipelineSummaryDto,
} from '@streampipes/platform-services';
import {
    DialogRef,
    DialogService,
    ObjectManageDialogComponent,
    ObjectManageDialogResourceConfig,
    PanelType,
} from '@streampipes/shared-ui';
import { PipelineStatusDialogComponent } from '../dialog/pipeline-status/pipeline-status-dialog.component';
import { DeletePipelineDialogComponent } from '../dialog/delete-pipeline/delete-pipeline-dialog.component';
import { Router } from '@angular/router';
import { PipelineAction } from '../model/pipeline-model';
import { PipelineNotificationsComponent } from '../dialog/pipeline-notifications/pipeline-notifications.component';
import { PipelineCodeDialogComponent } from '../../pipeline-details/dialogs/pipeline-code/pipeline-code-dialog.component';
import { firstValueFrom } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class PipelineOperationsService {
    private dialogService = inject(DialogService);
    private router = inject(Router);
    private pipelineService = inject(PipelineService);

    starting: any;
    stopping: any;

    startPipeline(
        pipelineId: string,
        refreshPipelinesEmitter: EventEmitter<boolean>,
        toggleRunningOperation?,
    ) {
        if (toggleRunningOperation) {
            toggleRunningOperation('starting');
        }
        const dialogRef = this.showPipelineOperationsDialog(
            pipelineId,
            PipelineAction.Start,
        );
        this.afterPipelineOperationsDialogClosed(
            dialogRef,
            refreshPipelinesEmitter,
            'starting',
            toggleRunningOperation,
        );
    }

    stopPipeline(
        pipelineId: string,
        refreshPipelinesEmitter: EventEmitter<boolean>,
        toggleRunningOperation?,
    ) {
        if (toggleRunningOperation) {
            toggleRunningOperation('stopping');
        }
        const dialogRef = this.showPipelineOperationsDialog(
            pipelineId,
            PipelineAction.Stop,
        );
        this.afterPipelineOperationsDialogClosed(
            dialogRef,
            refreshPipelinesEmitter,
            'stopping',
            toggleRunningOperation,
        );
    }

    afterPipelineOperationsDialogClosed(
        dialogRef: DialogRef<PipelineStatusDialogComponent>,
        refreshPipelinesEmitter: EventEmitter<boolean>,
        toggleAction: string,
        toggleRunningOperation?,
    ) {
        dialogRef.afterClosed().subscribe(_msg => {
            refreshPipelinesEmitter.emit(true);
            if (toggleRunningOperation) {
                toggleRunningOperation(toggleAction);
            }
        });
    }

    showDeleteDialog(
        elementId: string,
        name: string,
        running: boolean,
        refreshPipelinesEmitter: EventEmitter<boolean>,
        switchToPipelineView?: any,
    ) {
        const dialogRef: DialogRef<DeletePipelineDialogComponent> =
            this.dialogService.open(DeletePipelineDialogComponent, {
                panelType: PanelType.STANDARD_PANEL,
                title: 'Delete Pipeline',
                width: '70vw',
                data: {
                    elementId,
                    name,
                    running,
                },
            });

        dialogRef.afterClosed().subscribe(data => {
            if (data) {
                if (!switchToPipelineView) {
                    refreshPipelinesEmitter.emit(true);
                } else {
                    switchToPipelineView();
                }
            }
        });
    }

    showPipelineOperationsDialog(
        pipelineId: string,
        action: PipelineAction,
    ): DialogRef<PipelineStatusDialogComponent> {
        return this.dialogService.open(PipelineStatusDialogComponent, {
            panelType: PanelType.STANDARD_PANEL,
            title: 'Pipeline Status',
            width: '70vw',
            data: {
                pipelineId: pipelineId,
                action: action,
            },
        });
    }

    showPipelineNotificationsDialog(
        pipelineSummary: PipelineSummaryDto,
        refreshPipelinesEmitter: EventEmitter<boolean>,
    ) {
        const dialogRef: DialogRef<PipelineNotificationsComponent> =
            this.dialogService.open(PipelineNotificationsComponent, {
                panelType: PanelType.STANDARD_PANEL,
                title: 'Pipeline Notifications',
                width: '70vw',
                data: {
                    pipelineSummary: pipelineSummary,
                },
            });

        dialogRef.afterClosed().subscribe(_close => {
            refreshPipelinesEmitter.emit(true);
        });
    }

    showManageDialog(
        pipelineSummary: PipelineSummaryDto,
        refreshPipelinesEmitter: EventEmitter<boolean>,
    ) {
        this.pipelineService
            .getPipelineById(pipelineSummary.elementId)
            .subscribe(pipeline => {
                const resourceConfig: ObjectManageDialogResourceConfig<Pipeline> =
                    {
                        resourceLabel: 'Pipeline',
                        nameLabel: 'Pipeline name',
                        descriptionLabel: 'Description',
                        nameProperty: 'name',
                        assetLinkType: 'pipeline',
                        assetLinkCheckboxLabel:
                            'Add the current pipeline to an existing asset',
                        saveResource: async resource => {
                            const shouldRestart = resource.running;

                            if (shouldRestart) {
                                const stopResult = await firstValueFrom(
                                    this.pipelineService.stopPipeline(
                                        resource._id,
                                    ),
                                );
                                this.assertPipelineOperationSucceeded(
                                    stopResult.success,
                                    'Stopping the pipeline failed.',
                                );
                            }

                            const result = await firstValueFrom(
                                this.pipelineService.updatePipeline(resource),
                            );
                            this.assertPipelineSaveSucceeded(result);

                            if (shouldRestart) {
                                const startResult = await firstValueFrom(
                                    this.pipelineService.startPipeline(
                                        resource._id,
                                    ),
                                );
                                this.assertPipelineOperationSucceeded(
                                    startResult.success,
                                    'Starting the pipeline failed.',
                                );
                            }
                        },
                    };
                const dialogRef = this.dialogService.open(
                    ObjectManageDialogComponent,
                    {
                        panelType: PanelType.SLIDE_IN_PANEL,
                        title: 'Manage',
                        width: '50vw',
                        data: {
                            objectInstanceId: pipeline._id,
                            resource: { ...pipeline },
                            saveMode: 'immediate',
                            resourceConfig,
                            headerTitle: 'Manage Pipeline ' + pipeline.name,
                        },
                    },
                );

                dialogRef.afterClosed().subscribe(refresh => {
                    refreshPipelinesEmitter.emit(!!refresh);
                });
            });
    }

    showPermissionsDialog(
        pipelineSummary: PipelineSummaryDto,
        refreshPipelinesEmitter: EventEmitter<boolean>,
    ) {
        const dialogRef = this.dialogService.open(ObjectManageDialogComponent, {
            panelType: PanelType.SLIDE_IN_PANEL,
            title: 'Manage',
            width: '50vw',
            data: {
                objectInstanceId: pipelineSummary.elementId,
                resource: {
                    _id: pipelineSummary.elementId,
                    name: pipelineSummary.name,
                },
                saveMode: 'immediate',
                resourceConfig: {
                    resourceLabel: 'Pipeline',
                    nameLabel: 'Pipeline name',
                    nameProperty: 'name',
                    showResourceFields: false,
                    showAssetLinking: false,
                } as ObjectManageDialogResourceConfig<Pipeline>,
                headerTitle: 'Manage Pipeline ' + pipelineSummary.name,
            },
        });

        dialogRef.afterClosed().subscribe(refresh => {
            refreshPipelinesEmitter.emit(refresh);
        });
    }

    showCodeDialog(pipelineSummary: PipelineSummaryDto): void {
        this.pipelineService
            .getPipelineById(pipelineSummary.elementId)
            .subscribe(pipeline => {
                this.dialogService.open(PipelineCodeDialogComponent, {
                    panelType: PanelType.SLIDE_IN_PANEL,
                    width: '50vw',
                    title: 'Pipeline code',
                    data: {
                        pipeline,
                    },
                });
            });
    }

    showPipelineInEditor(id: string) {
        this.router.navigate(['pipelines', 'modify', id]);
    }

    showPipelineCloneInEditor(id: string) {
        this.router.navigate(['pipelines', 'modify', id], {
            queryParams: { clone: true },
        });
    }

    showPipelineDetails(id: string) {
        this.router.navigate(['pipelines', 'details', id]);
    }

    modifyPipeline(pipelineId: string) {
        this.showPipelineInEditor(pipelineId);
    }

    clonePipeline(pipelineId: string) {
        this.showPipelineCloneInEditor(pipelineId);
    }

    private assertPipelineSaveSucceeded(result: Message): void {
        if (!result.success) {
            throw new Error('Saving the pipeline failed.');
        }
    }

    private assertPipelineOperationSucceeded(
        success: boolean,
        errorMessage: string,
    ): void {
        if (!success) {
            throw new Error(errorMessage);
        }
    }
}
