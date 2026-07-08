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

import { Component, inject, Input, OnInit } from '@angular/core';
import { AssetSaveService, DialogRef } from '@streampipes/shared-ui';
import {
    DatalakeRestService,
    DataSinkInvocation,
    LinkageData,
    Message,
    Pipeline,
    PipelineCanvasMetadata,
    PipelineCanvasMetadataService,
    PipelineOperationStatus,
    MeasurementUpdateInfo,
    PipelineService,
    SpAssetTreeNode,
} from '@streampipes/platform-services';
import { EditorService } from '../../services/editor.service';
import { ShepherdService } from '../../../services/tour/shepherd.service';
import { UntypedFormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import {
    InvocablePipelineElementUnion,
    PipelineStorageOptions,
} from '../../model/editor.model';
import { IdGeneratorService } from '../../../core-services/id-generator/id-generator.service';
import { firstValueFrom, lastValueFrom, Observable, of, tap } from 'rxjs';
import { filter, switchMap } from 'rxjs/operators';
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
import { SavePipelineSettingsComponent } from './save-pipeline-settings/save-pipeline-settings.component';
import { MultiStepStatusIndicatorComponent } from '../../../core-ui/multi-step-status-indicator/multi-step-status-indicator.component';
import { MatDivider } from '@angular/material/divider';
import { PipelineStartedStatusComponent } from '../../../core-ui/pipeline/pipeline-started-status/pipeline-started-status.component';
import { MatButton } from '@angular/material/button';
import { SavePipelineUpdateMigrationComponent } from './save-pipeline-update-migration/save-pipeline-update-migration.component';

@Component({
    selector: 'sp-save-pipeline',
    templateUrl: './save-pipeline.component.html',
    styleUrls: ['./save-pipeline.component.scss'],
    imports: [
        FlexDirective,
        LayoutDirective,
        SavePipelineSettingsComponent,
        MultiStepStatusIndicatorComponent,
        MatDivider,
        PipelineStartedStatusComponent,
        LayoutGapDirective,
        MatButton,
        TranslatePipe,
        SavePipelineUpdateMigrationComponent,
    ],
})
export class SavePipelineComponent implements OnInit {
    private editorService = inject(EditorService);
    private dialogRef = inject(DialogRef<SavePipelineComponent>);
    private idGeneratorService = inject(IdGeneratorService);
    private pipelineService = inject(PipelineService);
    private router = inject(Router);
    private shepherdService = inject(ShepherdService);
    private pipelineCanvasService = inject(PipelineCanvasMetadataService);
    private assetSaveService = inject(AssetSaveService);
    private dataLakeService = inject(DatalakeRestService);
    private translateService = inject(TranslateService);

    @Input()
    pipeline: Pipeline;

    @Input()
    originalPipeline: Pipeline;

    selectedAssets: SpAssetTreeNode[];
    deselectedAssets: SpAssetTreeNode[];
    originalAssets: SpAssetTreeNode[];

    @Input()
    pipelineCanvasMetadata: PipelineCanvasMetadata;

    operationProgress = false;
    operationCompleted = false;
    operationSuccess = false;

    errorMessage = '';
    pipelineId: string;

    storageOptions: PipelineStorageOptions = {
        updateMode: 'update',
        startPipelineAfterStorage: true,
        navigateToPipelineOverview: true,
        updateModeActive: false,
    };

    submitPipelineForm: UntypedFormGroup = new UntypedFormGroup({});
    statusIndicators: StatusIndicator[] = [];
    finalPipelineOperationStatus: PipelineOperationStatus;
    pipelineAction: PipelineAction;
    pipelineUpdatePreflight = false;
    measurementUpdateInfos: MeasurementUpdateInfo[] = [];

    ngOnInit() {
        this.storageOptions.updateModeActive =
            this.originalPipeline !== undefined;
        if (this.storageOptions.updateModeActive) {
            this.pipeline._id = this.originalPipeline._id;
            this.pipeline.name = this.originalPipeline.name;
            this.pipeline.description = this.originalPipeline.description;
            this.pipeline.running = this.originalPipeline.running;
            this.pipeline.createdAt = this.originalPipeline.createdAt;
            this.pipeline.createdByUser = this.originalPipeline.createdByUser;
        }

        if (this.shepherdService.isTourActive()) {
            this.shepherdService.trigger('enter-pipeline-name');
        }
    }

    performStorageOperations(
        stopPipeline$: Observable<null | PipelineOperationStatus>,
        savePipeline$: Observable<Message>,
    ) {
        // if pipeline is running and update mode: stop pipeline
        // if update mode: update pipeline, if not update mode or update mode clone: save pipeline
        // if update mode and not clone: update canvas, else store new canvas
        // if should start: start pipeline
        stopPipeline$
            .pipe(
                tap(() =>
                    this.addStatusIndicator(
                        this.translateService.instant('Saving pipeline'),
                        Status.PROGRESS,
                    ),
                ),
                switchMap(() => savePipeline$),
                tap(message => {
                    this.operationSuccess = message.success;
                    if (!message.success) {
                        this.handleStorageError();
                    }
                    this.modifyStatusIndicator(Status.SUCCESS);
                    this.pipelineId = message.notifications[1].description;
                }),
                // only continue if pipeline was saved
                filter(message => message.success),
                tap(() =>
                    this.addStatusIndicator(
                        this.translateService.instant('Saving metadata'),
                        Status.PROGRESS,
                    ),
                ),
                switchMap(() =>
                    this.getPipelineCanvasMetadata$(this.pipelineId),
                ),
                tap(() => this.modifyStatusIndicator(Status.SUCCESS)),
                switchMap(() => this.getStartPipeline$()),
            )
            .subscribe({
                next: message => {
                    this.onSuccess(message);
                    // Add Asset as soon as pipelineId is known
                    this.addToAsset();
                },
                error: msg => {
                    this.onFailure(msg);
                },
            });
    }

    clonePipeline(): void {
        this.pipeline._id = undefined;
        this.pipeline._rev = undefined;
        this.pipeline.running = false;
        this.pipeline.actions.forEach(element => this.updateId(element));
        this.pipeline.sepas.forEach(element => this.updateId(element));
        this.pipelineCanvasMetadata._id = undefined;
        this.pipelineCanvasMetadata._rev = undefined;
    }

    savePipeline(skipPreflight = false) {
        if (this.shouldPerformUpdatePreflight(skipPreflight)) {
            this.performUpdatePreflight();
            return;
        }

        this.pipelineUpdatePreflight = false;
        let stopPipeline$: Observable<null | PipelineOperationStatus> =
            of(null);
        let savePipeline$: Observable<Message> =
            this.pipelineService.storePipeline(this.pipeline);
        this.operationProgress = true;
        if (this.storageOptions.updateModeActive) {
            if (this.storageOptions.updateMode === 'clone') {
                this.clonePipeline();
            } else {
                if (this.pipeline.running) {
                    stopPipeline$ = this.getStopPipeline$();
                }
                savePipeline$ = this.pipelineService.updatePipeline(
                    this.pipeline,
                );
            }
        }

        this.performStorageOperations(stopPipeline$, savePipeline$);
    }

    shouldPerformUpdatePreflight(skipPreflight: boolean): boolean {
        return (
            !skipPreflight &&
            this.storageOptions.updateModeActive &&
            this.storageOptions.updateMode !== 'clone' &&
            this.hasDataLakeSink()
        );
    }

    hasDataLakeSink(): boolean {
        return this.pipeline.actions.some(
            action =>
                action.appId ===
                'org.apache.streampipes.sinks.internal.jvm.datalake',
        );
    }

    performUpdatePreflight(): void {
        this.operationProgress = true;
        this.addStatusIndicator(
            this.translateService.instant('Checking pipeline update'),
            Status.PROGRESS,
        );
        this.pipelineService
            .performPipelineMigrationPreflight(this.pipeline)
            .subscribe({
                next: updateInfos => {
                    if (updateInfos.length === 0) {
                        this.modifyStatusIndicator(Status.SUCCESS);
                        this.savePipeline(true);
                    } else {
                        this.measurementUpdateInfos = updateInfos;
                        this.pipelineUpdatePreflight = true;
                        this.operationProgress = false;
                        this.statusIndicators = [];
                    }
                },
                error: msg => {
                    this.onFailure(msg);
                },
            });
    }

    updateId(entity: InvocablePipelineElementUnion) {
        const lastIdIndex = entity.elementId.lastIndexOf(':');
        entity.elementId =
            entity.elementId.substring(0, lastIdIndex + 1) +
            this.idGeneratorService.generate(5);
    }

    getStopPipeline$(): Observable<PipelineOperationStatus> {
        return of(null).pipe(
            tap(() =>
                this.addStatusIndicator(
                    this.translateService.instant('Stopping pipeline'),
                    Status.PROGRESS,
                ),
            ),
            switchMap(() =>
                this.pipelineService.stopPipeline(this.originalPipeline._id),
            ),
            tap(msg => {
                this.operationSuccess = msg.success;
                if (!msg.success) {
                    this.handlePipelineOperationError(msg, PipelineAction.Stop);
                } else {
                    this.modifyStatusIndicator(Status.SUCCESS);
                }
            }),
            filter(status => status.success),
        );
    }

    getStartPipeline$(): Observable<null | PipelineOperationStatus> {
        if (this.storageOptions.startPipelineAfterStorage) {
            return of(null).pipe(
                tap(() =>
                    this.addStatusIndicator(
                        this.translateService.instant('Starting pipeline'),
                        Status.PROGRESS,
                    ),
                ),
                switchMap(() =>
                    this.pipelineService.startPipeline(this.pipelineId),
                ),
                tap(msg => {
                    if (!msg.success) {
                        this.handlePipelineOperationError(
                            msg,
                            PipelineAction.Start,
                        );
                    } else {
                        this.modifyStatusIndicator(
                            msg.success ? Status.SUCCESS : Status.FAILURE,
                        );
                    }
                }),
            );
        } else {
            return of(null);
        }
    }

    getPipelineCanvasMetadata$(pipelineId: string): Observable<object> {
        this.pipelineCanvasMetadata.pipelineId = pipelineId;
        return this.pipelineCanvasService.updatePipelineCanvasMetadata(
            pipelineId,
            this.pipelineCanvasMetadata,
        );
    }

    addStatusIndicator(message: string, status: Status) {
        this.statusIndicators.push({ message, status });
    }

    modifyStatusIndicator(status: Status) {
        // modify status of the last indicator
        this.statusIndicators[this.statusIndicators.length - 1].status = status;
    }

    handleStorageError(): void {
        this.onFailure();
    }

    handlePipelineOperationError(
        status: PipelineOperationStatus,
        pipelineAction: PipelineAction,
    ) {
        this.onFailure();
        this.showPipelineOperationStatus(status, pipelineAction);
    }

    onFailure(_msg?: any) {
        this.operationCompleted = true;
        this.operationSuccess = false;
        if (this.statusIndicators.length > 0) {
            this.modifyStatusIndicator(Status.FAILURE);
        }
    }

    showPipelineOperationStatus(
        status: PipelineOperationStatus,
        pipelineAction: PipelineAction,
    ) {
        this.finalPipelineOperationStatus = status;
        this.pipelineAction = pipelineAction;
    }

    onSuccess(status?: PipelineOperationStatus) {
        this.operationProgress = false;
        this.operationCompleted = true;
        if (status) {
            this.showPipelineOperationStatus(status, PipelineAction.Start);
        }
        this.editorService.makePipelineAssemblyEmpty(true);
        this.editorService.removePipelineFromCache().subscribe();
        if (this.shepherdService.isTourActive()) {
            this.shepherdService.hideCurrentStep();
        }
        if (this.storageOptions.navigateToPipelineOverview && status?.success) {
            this.navigateToPipelineOverview();
        }
    }

    navigateToPipelineOverview(): void {
        this.hide(true);
        this.router.navigate(['pipelines']);
    }

    hide(skipReload: boolean) {
        let reloadConfig = undefined;
        if (!skipReload) {
            reloadConfig = this.operationSuccess
                ? { reload: true, pipelineId: this.pipelineId }
                : undefined;
        }
        this.dialogRef.close(reloadConfig);
    }

    async addToAsset(): Promise<void> {
        let linkageData: LinkageData[] = [];
        linkageData = await this.addPipelineLinkageData(linkageData);

        await this.saveAssets(linkageData);
    }
    private async addPipelineLinkageData(
        linkageData: LinkageData[],
    ): Promise<LinkageData[]> {
        const pipeline = await firstValueFrom(
            this.pipelineService.getPipelineById(this.pipelineId),
        );

        linkageData.push({
            type: 'pipeline',
            id: this.pipelineId,
            name: pipeline.name,
        });

        const serviceList: DataSinkInvocation[] =
            pipeline.actions as DataSinkInvocation[];
        const dataSinkServices: DataSinkInvocation[] = serviceList.filter(
            action => action.serviceTagPrefix === 'DATA_SINK',
        );

        for (const service of dataSinkServices) {
            const staticProperty = service.staticProperties.find(
                prop => prop.internalName === 'db_measurement',
            );

            const measureFromPipeline = (staticProperty as { value: string })
                .value;

            const measure = await lastValueFrom(
                this.dataLakeService.getMeasurementByName(measureFromPipeline),
            );

            linkageData.push({
                type: 'measurement',
                id: measure.elementId,
                name: measureFromPipeline,
            });
        }
        return linkageData;
    }

    private async saveAssets(linkageData: LinkageData[]): Promise<void> {
        await this.assetSaveService.saveSelectedAssets(
            this.selectedAssets,
            linkageData,
            this.deselectedAssets,
            this.originalAssets,
        );
    }
}
