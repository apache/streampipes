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
    EventEmitter,
    Input,
    OnDestroy,
    ViewChild,
    inject,
} from '@angular/core';
import { JsplumbBridge } from '../../services/jsplumb-bridge.service';
import { PipelinePositioningService } from '../../services/pipeline-positioning.service';
import { PipelineValidationService } from '../../services/pipeline-validation.service';
import {
    InvocablePipelineElementUnion,
    PipelineElementConfig,
    PipelineElementUnion,
} from '../../model/editor.model';
import { ObjectProvider } from '../../services/object-provider.service';
import {
    AssetSaveService,
    DialogService,
    KeyboardShortcutService,
    ObjectManageDialogComponent,
    ObjectManageDialogResourceConfig,
    ObjectManageDialogResult,
    PanelType,
    ShortcutRegistration,
    SpBasicViewComponent,
} from '@streampipes/shared-ui';
import { EditorService } from '../../services/editor.service';
import {
    LinkageData,
    MeasurementUpdateInfo,
    Pipeline,
    PipelineCanvasMetadata,
    PipelineCanvasMetadataService,
    PipelineOperationStatus,
    PermissionsService,
    PipelineService,
} from '@streampipes/platform-services';
import { JsplumbFactoryService } from '../../services/jsplumb-factory.service';
import { firstValueFrom, forkJoin } from 'rxjs';
import { Router } from '@angular/router';
import { PipelineAssemblyDrawingAreaComponent } from './pipeline-assembly-drawing-area/pipeline-assembly-drawing-area.component';
import {
    PipelineAssemblyOptionsComponent,
    PipelineAssemblySaveOptions,
} from './pipeline-assembly-options/pipeline-assembly-options.component';
import { JsplumbService } from '../../services/jsplumb.service';
import { TranslateService } from '@ngx-translate/core';
import { FlexDirective } from '@ngbracket/ngx-layout/flex';
import { PipelineOperationsService } from '../../../pipelines/services/pipeline-operations.service';
import { SavePipelineUpdateMigrationComponent } from '../../dialog/save-pipeline/save-pipeline-update-migration/save-pipeline-update-migration.component';
import { SavePipelineStatusDialogComponent } from '../../dialog/save-pipeline/save-pipeline-status-dialog/save-pipeline-status-dialog.component';
import {
    Status,
    StatusIndicator,
} from '../../../core-ui/multi-step-status-indicator/multi-step-status-indicator.model';
import { PipelineAction } from '../../../pipelines/model/pipeline-model';
import { IdGeneratorService } from '../../../core-services/id-generator/id-generator.service';

interface PipelineSaveResult {
    saveSuccessful: boolean;
    statusIndicators: StatusIndicator[];
    finalPipelineOperationStatus?: PipelineOperationStatus;
    pipelineAction?: PipelineAction;
}

@Component({
    selector: 'sp-pipeline-assembly',
    templateUrl: './pipeline-assembly.component.html',
    styleUrls: ['./pipeline-assembly.component.scss'],
    imports: [
        SpBasicViewComponent,
        FlexDirective,
        PipelineAssemblyOptionsComponent,
        PipelineAssemblyDrawingAreaComponent,
    ],
})
export class PipelineAssemblyComponent implements AfterViewInit, OnDestroy {
    private jsPlumbFactoryService = inject(JsplumbFactoryService);
    private pipelinePositioningService = inject(PipelinePositioningService);
    private objectProvider = inject(ObjectProvider);
    editorService = inject(EditorService);
    pipelineValidationService = inject(PipelineValidationService);
    private dialogService = inject(DialogService);
    private router = inject(Router);
    private jsplumbService = inject(JsplumbService);
    private translateService = inject(TranslateService);
    private shortcutService = inject(KeyboardShortcutService);
    private pipelineService = inject(PipelineService);
    private pipelineCanvasService = inject(PipelineCanvasMetadataService);
    private permissionsService = inject(PermissionsService);
    private assetSaveService = inject(AssetSaveService);
    private pipelineOperationsService = inject(PipelineOperationsService);
    private idGeneratorService = inject(IdGeneratorService);

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
    private pendingManagePipelineResult?: ObjectManageDialogResult<Pipeline>;

    jsplumbBridge: JsplumbBridge;
    private shortcutReg: ShortcutRegistration;

    @ViewChild('assemblyOptionsComponent')
    assemblyOptionsComponent: PipelineAssemblyOptionsComponent;
    @ViewChild('drawingAreaComponent')
    drawingAreaComponent: PipelineAssemblyDrawingAreaComponent;

    ngAfterViewInit() {
        this.shortcutReg = this.shortcutService.register('pipeline-assembly', [
            { key: 's', ctrl: true, action: () => this.onShortcutSave() },
        ]);
        this.jsplumbBridge = this.jsPlumbFactoryService.getJsplumbBridge(
            this.readonly,
        );
    }

    ngOnDestroy(): void {
        this.shortcutReg?.unregister();
    }

    private onShortcutSave(): void {
        if (!this.readonly && this.rawPipelineModel?.length) {
            this.submit();
        }
    }

    /**
     * clears the Assembly of all elements
     */
    clearAssembly() {
        this.editorService.makePipelineAssemblyEmpty(true);
        this.rawPipelineModel = [];
        this.jsplumbBridge.deleteEveryEndpoint();
        this.drawingAreaComponent.resetZoom();
        this.jsplumbBridge.repaintEverything();

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
    submit(
        saveOptions: PipelineAssemblySaveOptions = {
            startPipelineAfterStorage: true,
            createNewPipeline: false,
        },
    ) {
        const pipeline = this.makePipelineForSave();

        if (this.originalPipeline && saveOptions.createNewPipeline) {
            this.prepareClonedPipeline(pipeline);
            this.openCreatePipelineDialog(
                pipeline,
                saveOptions.startPipelineAfterStorage,
                this.makeClonedPipelineCanvasMetadata(),
            );
            return;
        }

        if (this.originalPipeline) {
            void this.savePipelineChanges(
                pipeline,
                saveOptions.startPipelineAfterStorage,
            );
            return;
        }

        this.openCreatePipelineDialog(
            pipeline,
            saveOptions.startPipelineAfterStorage,
            this.pipelineCanvasMetadata,
        );
    }

    private makePipelineForSave(): Pipeline {
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
        return pipeline;
    }

    private openCreatePipelineDialog(
        pipeline: Pipeline,
        startPipelineAfterStorage: boolean,
        pipelineCanvasMetadata: PipelineCanvasMetadata,
    ): void {
        const resourceConfig: ObjectManageDialogResourceConfig<Pipeline> = {
            resourceLabel: 'Pipeline',
            nameLabel: 'Pipeline name',
            descriptionLabel: 'Description',
            nameProperty: 'name',
            assetLinkType: 'pipeline',
            assetLinkCheckboxLabel:
                'Add the current pipeline to an existing asset',
            saveResource: async resource => {
                const saveResult = await this.savePipelineResource(
                    resource,
                    startPipelineAfterStorage,
                    false,
                    pipelineCanvasMetadata,
                );
                if (!saveResult.saveSuccessful) {
                    throw new Error('Saving the pipeline failed.');
                }
            },
        };
        const dialogRef = this.dialogService.open(ObjectManageDialogComponent, {
            panelType: PanelType.SLIDE_IN_PANEL,
            title: this.translateService.instant('Save pipeline'),
            width: '50vw',
            data: {
                createMode: true,
                resource: JSON.parse(JSON.stringify(pipeline)),
                saveMode: 'immediate',
                resourceConfig,
                headerTitle: this.translateService.instant('Save pipeline'),
            },
        });
        dialogRef.afterClosed().subscribe(refresh => {
            if (refresh) {
                this.editorService.makePipelineAssemblyEmpty(true);
                this.editorService.removePipelineFromCache().subscribe();
                this.router.navigate(['pipelines']);
            }
        });
    }

    managePipeline(): void {
        if (!this.originalPipeline) {
            return;
        }

        const resource: Pipeline = { ...this.originalPipeline };
        const resourceConfig: ObjectManageDialogResourceConfig<Pipeline> = {
            resourceLabel: 'Pipeline',
            nameLabel: 'Pipeline name',
            descriptionLabel: 'Description',
            nameProperty: 'name',
            assetLinkType: 'pipeline',
            assetLinkCheckboxLabel:
                'Add the current pipeline to an existing asset',
        };

        const dialogRef = this.dialogService.open(ObjectManageDialogComponent, {
            panelType: PanelType.SLIDE_IN_PANEL,
            title: this.translateService.instant('Manage'),
            width: '50vw',
            data: {
                objectInstanceId: resource._id,
                resource,
                saveMode: 'deferred',
                resourceConfig,
                headerTitle:
                    this.translateService.instant('Manage Pipeline ') +
                    resource.name,
            },
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result && typeof result !== 'boolean') {
                this.pendingManagePipelineResult = result;
                Object.assign(this.originalPipeline, result.resource);
            }
        });
    }

    deletePipeline(): void {
        if (!this.originalPipeline) {
            return;
        }

        this.pipelineOperationsService.showDeleteDialog(
            this.originalPipeline._id,
            this.originalPipeline.name,
            this.originalPipeline.running,
            new EventEmitter<boolean>(),
            () => this.router.navigate(['pipelines']),
        );
    }

    private async savePipelineChanges(
        pipeline: Pipeline,
        startPipelineAfterStorage: boolean,
    ): Promise<void> {
        pipeline._id = this.originalPipeline._id;
        pipeline.name = this.originalPipeline.name;
        pipeline.description = this.originalPipeline.description;
        pipeline.running = this.originalPipeline.running;
        pipeline.createdAt = this.originalPipeline.createdAt;
        pipeline.createdByUser = this.originalPipeline.createdByUser;

        if (!(await this.confirmPipelineUpdateIfRequired(pipeline))) {
            return;
        }

        const saveResult = await this.savePipelineResource(
            pipeline,
            startPipelineAfterStorage,
            true,
            this.pipelineCanvasMetadata,
        );
        if (!saveResult.saveSuccessful) {
            return;
        }

        await this.savePendingManagePipelineChanges();
        this.editorService.makePipelineAssemblyEmpty(true);
        this.editorService.removePipelineFromCache().subscribe();
        this.router.navigate(['pipelines']);
    }

    private async savePipelineResource(
        pipeline: Pipeline,
        startPipelineAfterStorage: boolean,
        updateExisting: boolean,
        pipelineCanvasMetadata: PipelineCanvasMetadata,
    ): Promise<PipelineSaveResult> {
        const saveResult = await this.executePipelineSave(
            pipeline,
            startPipelineAfterStorage,
            updateExisting,
            pipelineCanvasMetadata,
        );
        await this.openPipelineSaveStatusDialog(saveResult);
        return saveResult;
    }

    private async savePendingManagePipelineChanges(): Promise<void> {
        const result = this.pendingManagePipelineResult;
        if (!result) {
            return;
        }

        if (result.permission) {
            await firstValueFrom(
                this.permissionsService.updatePermission(result.permission),
            );
        }

        if (this.shouldSaveManagePipelineAssets(result)) {
            await this.assetSaveService.saveSelectedAssets(
                result.selectedAssets,
                this.createPipelineLinkageData(result.resource),
                result.deselectedAssets,
                result.originalAssets,
            );
        }

        this.pendingManagePipelineResult = undefined;
    }

    private shouldSaveManagePipelineAssets(
        result: ObjectManageDialogResult<Pipeline>,
    ): boolean {
        return (
            result.addToAssets &&
            (result.selectedAssets.length > 0 ||
                result.deselectedAssets.length > 0 ||
                result.originalAssets.length > 0)
        );
    }

    private createPipelineLinkageData(pipeline: Pipeline): LinkageData[] {
        return [
            {
                type: 'pipeline',
                id: pipeline._id ?? '',
                name: pipeline.name ?? '',
            },
        ];
    }

    private async confirmPipelineUpdateIfRequired(
        pipeline: Pipeline,
    ): Promise<boolean> {
        if (!this.originalPipeline || !this.hasDataLakeSink(pipeline)) {
            return true;
        }

        const measurementUpdateInfos = await firstValueFrom(
            this.pipelineService.performPipelineMigrationPreflight(pipeline),
        );

        if (measurementUpdateInfos.length === 0) {
            return true;
        }

        return this.openPipelineUpdateMigrationDialog(measurementUpdateInfos);
    }

    private hasDataLakeSink(pipeline: Pipeline): boolean {
        return pipeline.actions.some(
            action =>
                action.appId ===
                'org.apache.streampipes.sinks.internal.jvm.datalake',
        );
    }

    private async openPipelineUpdateMigrationDialog(
        measurementUpdateInfos: MeasurementUpdateInfo[],
    ): Promise<boolean> {
        const dialogRef = this.dialogService.open(
            SavePipelineUpdateMigrationComponent,
            {
                panelType: PanelType.SLIDE_IN_PANEL,
                title: this.translateService.instant('Pipeline update review'),
                width: '50vw',
                data: {
                    measurementUpdateInfos,
                },
            },
        );

        const startUpdateSubscription =
            dialogRef.componentInstance.instance.startUpdateEmitter.subscribe(
                () => {
                    dialogRef.close(true);
                },
            );

        const shouldContinue = await firstValueFrom(dialogRef.afterClosed());
        startUpdateSubscription.unsubscribe();
        return !!shouldContinue;
    }

    private prepareClonedPipeline(pipeline: Pipeline): void {
        pipeline._id = undefined;
        pipeline._rev = undefined;
        pipeline.name = this.originalPipeline.name;
        pipeline.description = this.originalPipeline.description;
        pipeline.running = false;
        pipeline.actions.forEach(element =>
            this.updateInvocablePipelineElementId(element),
        );
        pipeline.sepas.forEach(element =>
            this.updateInvocablePipelineElementId(element),
        );
    }

    private updateInvocablePipelineElementId(
        entity: InvocablePipelineElementUnion,
    ): void {
        const lastIdIndex = entity.elementId.lastIndexOf(':');
        entity.elementId =
            entity.elementId.substring(0, lastIdIndex + 1) +
            this.idGeneratorService.generate(5);
    }

    private makeClonedPipelineCanvasMetadata(): PipelineCanvasMetadata {
        const metadata = PipelineCanvasMetadata.fromData(
            this.pipelineCanvasMetadata,
            new PipelineCanvasMetadata(),
        );
        metadata._id = undefined;
        metadata._rev = undefined;
        metadata.pipelineId = undefined;
        return metadata;
    }

    private async executePipelineSave(
        pipeline: Pipeline,
        startPipelineAfterStorage: boolean,
        updateExisting: boolean,
        pipelineCanvasMetadata: PipelineCanvasMetadata,
    ): Promise<PipelineSaveResult> {
        const saveResult: PipelineSaveResult = {
            saveSuccessful: false,
            statusIndicators: [],
        };

        try {
            if (updateExisting && pipeline.running) {
                this.addStatusIndicator(
                    saveResult,
                    this.translateService.instant('Stopping pipeline'),
                    Status.PROGRESS,
                );
                const stopResult = await firstValueFrom(
                    this.pipelineService.stopPipeline(
                        this.originalPipeline._id,
                    ),
                );
                saveResult.finalPipelineOperationStatus = stopResult;
                saveResult.pipelineAction = PipelineAction.Stop;
                this.modifyLastStatusIndicator(
                    saveResult,
                    stopResult.success ? Status.SUCCESS : Status.FAILURE,
                );
                if (!stopResult.success) {
                    return saveResult;
                }
            }

            this.addStatusIndicator(
                saveResult,
                this.translateService.instant('Saving pipeline'),
                Status.PROGRESS,
            );
            const saveMessage = updateExisting
                ? await firstValueFrom(
                      this.pipelineService.updatePipeline(pipeline),
                  )
                : await firstValueFrom(
                      this.pipelineService.storePipeline(pipeline),
                  );

            if (!saveMessage.success) {
                this.modifyLastStatusIndicator(saveResult, Status.FAILURE);
                return saveResult;
            }

            this.modifyLastStatusIndicator(saveResult, Status.SUCCESS);

            const pipelineId =
                (updateExisting ? this.originalPipeline?._id : undefined) ??
                saveMessage.notifications?.[1]?.description;
            if (!pipelineId) {
                this.addStatusIndicator(
                    saveResult,
                    this.translateService.instant(
                        'The pipeline id was missing after saving.',
                    ),
                    Status.FAILURE,
                );
                return saveResult;
            }

            pipeline._id = pipelineId;
            this.addStatusIndicator(
                saveResult,
                this.translateService.instant('Saving metadata'),
                Status.PROGRESS,
            );
            pipelineCanvasMetadata.pipelineId = pipelineId;
            await firstValueFrom(
                this.pipelineCanvasService.updatePipelineCanvasMetadata(
                    pipelineId,
                    pipelineCanvasMetadata,
                ),
            );
            this.modifyLastStatusIndicator(saveResult, Status.SUCCESS);
            saveResult.saveSuccessful = true;

            if (startPipelineAfterStorage) {
                this.addStatusIndicator(
                    saveResult,
                    this.translateService.instant('Starting pipeline'),
                    Status.PROGRESS,
                );
                const startResult = await firstValueFrom(
                    this.pipelineService.startPipeline(pipelineId),
                );
                saveResult.finalPipelineOperationStatus = startResult;
                saveResult.pipelineAction = PipelineAction.Start;
                this.modifyLastStatusIndicator(
                    saveResult,
                    startResult.success ? Status.SUCCESS : Status.FAILURE,
                );
            }
        } catch {
            if (saveResult.statusIndicators.length === 0) {
                this.addStatusIndicator(
                    saveResult,
                    this.translateService.instant('Saving pipeline'),
                    Status.FAILURE,
                );
            } else {
                this.modifyLastStatusIndicator(saveResult, Status.FAILURE);
            }
        }

        return saveResult;
    }

    private addStatusIndicator(
        saveResult: PipelineSaveResult,
        message: string,
        status: Status,
    ): void {
        saveResult.statusIndicators.push({ message, status });
    }

    private modifyLastStatusIndicator(
        saveResult: PipelineSaveResult,
        status: Status,
    ): void {
        saveResult.statusIndicators[
            saveResult.statusIndicators.length - 1
        ].status = status;
    }

    private async openPipelineSaveStatusDialog(
        saveResult: PipelineSaveResult,
    ): Promise<void> {
        const dialogRef = this.dialogService.open(
            SavePipelineStatusDialogComponent,
            {
                panelType: PanelType.STANDARD_PANEL,
                title: this.translateService.instant('Pipeline status'),
                width: '50vw',
                data: {
                    statusIndicators: saveResult.statusIndicators,
                    finalPipelineOperationStatus:
                        saveResult.finalPipelineOperationStatus,
                    pipelineAction: saveResult.pipelineAction,
                },
            },
        );

        await firstValueFrom(dialogRef.afterClosed());
    }

    togglePreview(): void {
        this.previewModeActive = !this.previewModeActive;
        this.drawingAreaComponent.togglePipelineElementLivePreview();
    }

    triggerCacheUpdate(): void {
        this.assemblyOptionsComponent.triggerCacheUpdate();
    }

    displayPipelineTemplate(pipeline: Pipeline) {
        // Clears old pipeline before new elements are added
        this.clearAssembly();
        this.jsplumbBridge.reset();
        this.pipelineCanvasMetadata = new PipelineCanvasMetadata();
        this.pipelineCanvasMetadataAvailable = false;

        this.originalPipeline = pipeline;
        this.rawPipelineModel = [];
        this.rawPipelineModel = this.jsplumbService.makeRawPipeline(
            pipeline,
            false,
        );
        setTimeout(() => {
            this.drawingAreaComponent.displayPipelineInEditor(
                true,
                this.pipelineCanvasMetadata,
            );

            this.triggerCacheUpdate();
        });
    }
}
