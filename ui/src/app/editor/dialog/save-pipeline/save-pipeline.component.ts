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

import { Component, Input, OnInit } from '@angular/core';
import { DialogRef } from '@streampipes/shared-ui';
import {
    Message,
    Pipeline,
    PipelineCanvasMetadata,
    PipelineCanvasMetadataService,
    PipelineOperationStatus,
    PipelineService,
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
import { Observable, of, tap } from 'rxjs';
import { filter, switchMap } from 'rxjs/operators';
import {
    Status,
    StatusIndicator,
} from '../../../core-ui/multi-step-status-indicator/multi-step-status-indicator.model';
import { PipelineAction } from '../../../pipelines/model/pipeline-model';

@Component({
    selector: 'sp-save-pipeline',
    templateUrl: './save-pipeline.component.html',
    styleUrls: ['./save-pipeline.component.scss'],
})
export class SavePipelineComponent implements OnInit {
    @Input()
    pipeline: Pipeline;

    @Input()
    originalPipeline: Pipeline;

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

    constructor(
        private editorService: EditorService,
        private dialogRef: DialogRef<SavePipelineComponent>,
        private idGeneratorService: IdGeneratorService,
        private pipelineService: PipelineService,
        private router: Router,
        private shepherdService: ShepherdService,
        private pipelineCanvasService: PipelineCanvasMetadataService,
    ) {}

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
                    this.addStatusIndicator('Saving pipeline', Status.PROGRESS),
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
                    this.addStatusIndicator('Saving metadata', Status.PROGRESS),
                ),
                switchMap(() =>
                    this.getPipelineCanvasMetadata$(this.pipelineId),
                ),
                tap(() => this.modifyStatusIndicator(Status.SUCCESS)),
                switchMap(() => this.getStartPipeline$()),
            )
            .subscribe({
                next: message => this.onSuccess(message),
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

    savePipeline() {
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

    updateId(entity: InvocablePipelineElementUnion) {
        const lastIdIndex = entity.elementId.lastIndexOf(':');
        entity.elementId =
            entity.elementId.substring(0, lastIdIndex + 1) +
            this.idGeneratorService.generate(5);
    }

    getStopPipeline$(): Observable<PipelineOperationStatus> {
        return of(null).pipe(
            tap(() =>
                this.addStatusIndicator('Stopping pipeline', Status.PROGRESS),
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
                        'Starting pipeline',
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

    getPipelineCanvasMetadata$(pipelineId: string): Observable<Object> {
        let request;
        this.pipelineCanvasMetadata.pipelineId = pipelineId;
        if (this.storageOptions.updateModeActive) {
            request = this.pipelineCanvasService.updatePipelineCanvasMetadata(
                this.pipelineCanvasMetadata,
            );
        } else {
            this.pipelineCanvasMetadata._id = undefined;
            this.pipelineCanvasMetadata._rev = undefined;
            request = this.pipelineCanvasService.addPipelineCanvasMetadata(
                this.pipelineCanvasMetadata,
            );
        }
        return request;
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

    onFailure(msg?: any) {
        this.operationCompleted = true;
        this.operationSuccess = false;
        this.modifyStatusIndicator(Status.FAILURE);
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
}
