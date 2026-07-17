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
    Pipeline,
    PipelineCanvasMetadata,
    PermissionsService,
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
import { IdGeneratorService } from '../../../core-services/id-generator/id-generator.service';
import {
    SavePipelineComponent,
    SavePipelineDialogResult,
} from '../../dialog/save-pipeline/save-pipeline.component';

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
                const saveSuccessful = await this.savePipelineResource(
                    resource,
                    startPipelineAfterStorage,
                    false,
                    pipelineCanvasMetadata,
                );
                if (!saveSuccessful) {
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

        const saveSuccessful = await this.savePipelineResource(
            pipeline,
            startPipelineAfterStorage,
            true,
            this.pipelineCanvasMetadata,
        );
        if (!saveSuccessful) {
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
    ): Promise<boolean> {
        const dialogRef = this.dialogService.open(SavePipelineComponent, {
            panelType: PanelType.STANDARD_PANEL,
            title: this.translateService.instant('Save pipeline'),
            width: '70vw',
            disableClose: true,
            data: {
                pipeline,
                originalPipeline: updateExisting
                    ? this.originalPipeline
                    : undefined,
                pipelineCanvasMetadata,
                startPipelineAfterStorage,
                updateExisting,
            },
        });

        const result = (await firstValueFrom(
            dialogRef.afterClosed(),
        )) as SavePipelineDialogResult;
        if (result?.pipelineId) {
            pipeline._id = result.pipelineId;
        }
        return !!result?.success;
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
