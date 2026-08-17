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

import { Component, Input, OnInit, inject } from '@angular/core';
import { DialogRef } from '@streampipes/shared-ui';
import {
    MeasurementUpdateInfo,
    Message,
    Pipeline,
    PipelineCanvasMetadata,
    PipelineCanvasMetadataService,
    PipelineOperationStatus,
    PipelineService,
} from '@streampipes/platform-services';
import { firstValueFrom } from 'rxjs';
import {
    Status,
    StatusIndicator,
} from '../../../core-ui/multi-step-status-indicator/multi-step-status-indicator.model';
import { PipelineAction } from '../../../pipelines/model/pipeline-model';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import {
    FlexDirective,
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { MultiStepStatusIndicatorComponent } from '../../../core-ui/multi-step-status-indicator/multi-step-status-indicator.component';
import { MatDivider } from '@angular/material/divider';
import { PipelineStartedStatusComponent } from '../../../core-ui/pipeline/pipeline-started-status/pipeline-started-status.component';
import { MatButton } from '@angular/material/button';
import { SavePipelineUpdateMigrationComponent } from './save-pipeline-update-migration/save-pipeline-update-migration.component';

export interface SavePipelineDialogResult {
    success: boolean;
    pipelineId?: string;
}

@Component({
    selector: 'sp-save-pipeline',
    templateUrl: './save-pipeline.component.html',
    styleUrls: ['./save-pipeline.component.scss'],
    imports: [
        FlexDirective,
        LayoutDirective,
        LayoutGapDirective,
        MultiStepStatusIndicatorComponent,
        MatDivider,
        PipelineStartedStatusComponent,
        MatButton,
        TranslatePipe,
        SavePipelineUpdateMigrationComponent,
    ],
})
export class SavePipelineComponent implements OnInit {
    private dialogRef = inject(DialogRef<SavePipelineComponent>);
    private pipelineService = inject(PipelineService);
    private pipelineCanvasService = inject(PipelineCanvasMetadataService);
    private translateService = inject(TranslateService);

    @Input()
    pipeline: Pipeline;

    @Input()
    originalPipeline?: Pipeline;

    @Input()
    pipelineCanvasMetadata: PipelineCanvasMetadata;

    @Input()
    startPipelineAfterStorage = true;

    @Input()
    updateExisting = false;

    operationProgress = false;
    operationCompleted = false;
    operationSuccess = false;

    pipelineId: string;
    statusIndicators: StatusIndicator[] = [];
    finalPipelineOperationStatus?: PipelineOperationStatus;
    pipelineAction?: PipelineAction;
    pipelineUpdatePreflight = false;
    measurementUpdateInfos: MeasurementUpdateInfo[] = [];

    ngOnInit(): void {
        void this.savePipeline();
    }

    async savePipeline(skipPreflight = false): Promise<void> {
        if (await this.shouldPerformUpdatePreflight(skipPreflight)) {
            await this.performUpdatePreflight();
            return;
        }

        this.pipelineUpdatePreflight = false;
        this.operationProgress = true;

        try {
            if (this.updateExisting && this.pipeline.running) {
                const stopResult = await this.stopPipeline();
                if (!stopResult.success) {
                    return;
                }
            }

            const saveMessage = await this.storeOrUpdatePipeline();
            if (!saveMessage.success) {
                this.handleStorageError();
                return;
            }

            this.pipelineId = this.getPipelineId(saveMessage);
            if (!this.pipelineId) {
                this.handleStorageError();
                return;
            }

            this.pipeline._id = this.pipelineId;
            await this.savePipelineCanvasMetadata();
            if (!(await this.startPipelineIfRequested())) {
                return;
            }
            this.onSuccess();
        } catch {
            this.onFailure();
        }
    }

    private async shouldPerformUpdatePreflight(
        skipPreflight: boolean,
    ): Promise<boolean> {
        return !skipPreflight && this.updateExisting && this.hasDatasetSink();
    }

    private hasDatasetSink(): boolean {
        return this.pipeline.actions.some(
            action =>
                action.appId ===
                'org.apache.streampipes.sinks.internal.jvm.datalake',
        );
    }

    private async performUpdatePreflight(): Promise<void> {
        this.operationProgress = true;
        this.addStatusIndicator(
            this.translateService.instant('Checking pipeline update'),
            Status.PROGRESS,
        );

        try {
            const updateInfos = await firstValueFrom(
                this.pipelineService.performPipelineMigrationPreflight(
                    this.pipeline,
                ),
            );

            if (updateInfos.length === 0) {
                this.modifyStatusIndicator(Status.SUCCESS);
                await this.savePipeline(true);
            } else {
                this.measurementUpdateInfos = updateInfos;
                this.pipelineUpdatePreflight = true;
                this.operationProgress = false;
                this.statusIndicators = [];
            }
        } catch {
            this.onFailure();
        }
    }

    private async stopPipeline(): Promise<PipelineOperationStatus> {
        this.addStatusIndicator(
            this.translateService.instant('Stopping pipeline'),
            Status.PROGRESS,
        );
        const stopResult = await firstValueFrom(
            this.pipelineService.stopPipeline(this.originalPipeline._id),
        );
        this.operationSuccess = stopResult.success;
        if (!stopResult.success) {
            this.handlePipelineOperationError(stopResult, PipelineAction.Stop);
        } else {
            this.modifyStatusIndicator(Status.SUCCESS);
        }
        return stopResult;
    }

    private async storeOrUpdatePipeline(): Promise<Message> {
        this.addStatusIndicator(
            this.translateService.instant('Saving pipeline'),
            Status.PROGRESS,
        );
        const saveMessage = this.updateExisting
            ? await firstValueFrom(
                  this.pipelineService.updatePipeline(this.pipeline),
              )
            : await firstValueFrom(
                  this.pipelineService.storePipeline(this.pipeline),
              );
        this.operationSuccess = saveMessage.success;
        this.modifyStatusIndicator(
            saveMessage.success ? Status.SUCCESS : Status.FAILURE,
        );
        return saveMessage;
    }

    private getPipelineId(saveMessage: Message): string {
        return (
            (this.updateExisting ? this.originalPipeline?._id : undefined) ??
            saveMessage.notifications?.[1]?.description
        );
    }

    private async savePipelineCanvasMetadata(): Promise<void> {
        this.addStatusIndicator(
            this.translateService.instant('Saving metadata'),
            Status.PROGRESS,
        );
        this.pipelineCanvasMetadata.pipelineId = this.pipelineId;
        await firstValueFrom(
            this.pipelineCanvasService.updatePipelineCanvasMetadata(
                this.pipelineId,
                this.pipelineCanvasMetadata,
            ),
        );
        this.modifyStatusIndicator(Status.SUCCESS);
    }

    private async startPipelineIfRequested(): Promise<boolean> {
        if (!this.startPipelineAfterStorage) {
            return true;
        }

        this.addStatusIndicator(
            this.translateService.instant('Starting pipeline'),
            Status.PROGRESS,
        );
        const startResult = await firstValueFrom(
            this.pipelineService.startPipeline(this.pipelineId),
        );
        if (!startResult.success) {
            this.handlePipelineOperationError(
                startResult,
                PipelineAction.Start,
            );
            return false;
        } else {
            this.modifyStatusIndicator(Status.SUCCESS);
            this.showPipelineOperationStatus(startResult, PipelineAction.Start);
        }
        return true;
    }

    private handleStorageError(): void {
        this.onFailure();
    }

    private handlePipelineOperationError(
        status: PipelineOperationStatus,
        pipelineAction: PipelineAction,
    ): void {
        this.onFailure();
        this.showPipelineOperationStatus(status, pipelineAction);
    }

    private onFailure(): void {
        this.operationProgress = false;
        this.operationCompleted = true;
        this.operationSuccess = false;
        if (this.statusIndicators.length > 0) {
            this.modifyStatusIndicator(Status.FAILURE);
        }
    }

    private showPipelineOperationStatus(
        status: PipelineOperationStatus,
        pipelineAction: PipelineAction,
    ): void {
        this.finalPipelineOperationStatus = status;
        this.pipelineAction = pipelineAction;
    }

    private onSuccess(): void {
        this.operationProgress = false;
        this.operationCompleted = true;
        this.operationSuccess = true;
    }

    addStatusIndicator(message: string, status: Status): void {
        this.statusIndicators.push({ message, status });
    }

    modifyStatusIndicator(status: Status): void {
        this.statusIndicators[this.statusIndicators.length - 1].status = status;
    }

    close(): void {
        this.dialogRef.close({
            success: this.operationSuccess,
            pipelineId: this.pipelineId,
        } satisfies SavePipelineDialogResult);
    }
}
