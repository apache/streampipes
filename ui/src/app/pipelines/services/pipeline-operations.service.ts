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

import { ShepherdService } from '../../services/tour/shepherd.service';
import { EventEmitter, Injectable } from '@angular/core';
import { Pipeline, PipelineService } from '@streampipes/platform-services';
import { PanelType, DialogService, DialogRef } from '@streampipes/shared-ui';
import { PipelineStatusDialogComponent } from '../dialog/pipeline-status/pipeline-status-dialog.component';
import { DeletePipelineDialogComponent } from '../dialog/delete-pipeline/delete-pipeline-dialog.component';
import { Router } from '@angular/router';
import { PipelineAction } from '../model/pipeline-model';
import { PipelineNotificationsComponent } from '../dialog/pipeline-notifications/pipeline-notifications.component';
import { ObjectPermissionDialogComponent } from '../../core-ui/object-permission-dialog/object-permission-dialog.component';

@Injectable()
export class PipelineOperationsService {
    starting: any;
    stopping: any;

    constructor(
        private shepherdService: ShepherdService,
        private pipelineService: PipelineService,
        private dialogService: DialogService,
        private router: Router,
    ) {}

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
        dialogRef.afterClosed().subscribe(msg => {
            refreshPipelinesEmitter.emit(true);
            if (toggleRunningOperation) {
                toggleRunningOperation(toggleAction);
            }
        });
    }

    showDeleteDialog(
        pipeline: Pipeline,
        refreshPipelinesEmitter: EventEmitter<boolean>,
        switchToPipelineView?: any,
    ) {
        const dialogRef: DialogRef<DeletePipelineDialogComponent> =
            this.dialogService.open(DeletePipelineDialogComponent, {
                panelType: PanelType.STANDARD_PANEL,
                title: 'Delete Pipeline',
                width: '70vw',
                data: {
                    pipeline: pipeline,
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
        pipeline: Pipeline,
        refreshPipelinesEmitter: EventEmitter<boolean>,
    ) {
        const dialogRef: DialogRef<PipelineNotificationsComponent> =
            this.dialogService.open(PipelineNotificationsComponent, {
                panelType: PanelType.STANDARD_PANEL,
                title: 'Pipeline Notifications',
                width: '70vw',
                data: {
                    pipeline: pipeline,
                },
            });

        dialogRef.afterClosed().subscribe(close => {
            refreshPipelinesEmitter.emit(true);
        });
    }

    showPermissionsDialog(
        pipeline: Pipeline,
        refreshPipelinesEmitter: EventEmitter<boolean>,
    ) {
        const dialogRef = this.dialogService.open(
            ObjectPermissionDialogComponent,
            {
                panelType: PanelType.SLIDE_IN_PANEL,
                title: 'Manage permissions',
                width: '70vw',
                data: {
                    objectInstanceId: pipeline._id,
                    headerTitle:
                        'Manage permissions for pipeline ' + pipeline.name,
                },
            },
        );

        dialogRef.afterClosed().subscribe(refresh => {
            refreshPipelinesEmitter.emit(refresh);
        });
    }

    showPipelineInEditor(id: string) {
        this.router.navigate(['pipelines', 'modify', id]);
    }

    showPipelineDetails(id: string) {
        this.router.navigate(['pipelines', 'details', id]);
    }

    modifyPipeline(pipeline) {
        this.showPipelineInEditor(pipeline);
    }

    showLogs(id) {
        // this.$state.go("streampipes.pipelinelogs", {pipeline: id});
    }
}
