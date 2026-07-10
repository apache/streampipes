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
    Message,
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
import { PipelineAssemblyOptionsComponent } from './pipeline-assembly-options/pipeline-assembly-options.component';
import { JsplumbService } from '../../services/jsplumb.service';
import { TranslateService } from '@ngx-translate/core';
import { FlexDirective } from '@ngbracket/ngx-layout/flex';
import { PipelineOperationsService } from '../../../pipelines/services/pipeline-operations.service';

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
    submit(startPipelineAfterStorage = true) {
        if (this.originalPipeline) {
            void this.savePipelineChanges(startPipelineAfterStorage);
            return;
        }

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
        const resourceConfig: ObjectManageDialogResourceConfig<Pipeline> = {
            resourceLabel: 'Pipeline',
            nameLabel: 'Pipeline name',
            descriptionLabel: 'Description',
            nameProperty: 'name',
            assetLinkType: 'pipeline',
            assetLinkCheckboxLabel:
                'Add the current pipeline to an existing asset',
            saveResource: resource =>
                this.savePipelineResource(resource, startPipelineAfterStorage),
        };
        const dialogRef = this.dialogService.open(ObjectManageDialogComponent, {
            panelType: PanelType.SLIDE_IN_PANEL,
            title: this.translateService.instant('Save pipeline'),
            width: '50vw',
            data: {
                createMode: !this.originalPipeline,
                objectInstanceId: this.originalPipeline?._id,
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
        startPipelineAfterStorage: boolean,
    ): Promise<void> {
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
        pipeline._id = this.originalPipeline._id;
        pipeline.name = this.originalPipeline.name;
        pipeline.description = this.originalPipeline.description;
        pipeline.running = this.originalPipeline.running;
        pipeline.createdAt = this.originalPipeline.createdAt;
        pipeline.createdByUser = this.originalPipeline.createdByUser;

        await this.savePipelineResource(pipeline, startPipelineAfterStorage);
        await this.savePendingManagePipelineChanges();
        this.editorService.makePipelineAssemblyEmpty(true);
        this.editorService.removePipelineFromCache().subscribe();
        this.router.navigate(['pipelines']);
    }

    private async savePipelineResource(
        pipeline: Pipeline,
        startPipelineAfterStorage: boolean,
    ): Promise<void> {
        const pipelineId = await this.persistPipeline(pipeline);
        this.pipelineCanvasMetadata.pipelineId = pipelineId;
        await firstValueFrom(
            this.pipelineCanvasService.updatePipelineCanvasMetadata(
                pipelineId,
                this.pipelineCanvasMetadata,
            ),
        );

        if (startPipelineAfterStorage) {
            await this.startPipeline(pipelineId);
        }
    }

    private async persistPipeline(pipeline: Pipeline): Promise<string> {
        if (this.originalPipeline) {
            if (pipeline.running) {
                await this.stopPipeline(this.originalPipeline._id);
            }

            const result = await firstValueFrom(
                this.pipelineService.updatePipeline(pipeline),
            );
            this.assertSaveSuccess(result);
            return this.originalPipeline._id;
        }

        const result = await firstValueFrom(
            this.pipelineService.storePipeline(pipeline),
        );
        this.assertSaveSuccess(result);
        const pipelineId = result.notifications?.[1]?.description;

        if (!pipelineId) {
            throw new Error('The pipeline id was missing after saving.');
        }

        pipeline._id = pipelineId;
        return pipelineId;
    }

    private async stopPipeline(pipelineId: string): Promise<void> {
        const result = await firstValueFrom(
            this.pipelineService.stopPipeline(pipelineId),
        );

        if (!result.success) {
            throw new Error('Stopping the existing pipeline failed.');
        }
    }

    private async startPipeline(pipelineId: string): Promise<void> {
        const result = await firstValueFrom(
            this.pipelineService.startPipeline(pipelineId),
        );

        if (!result.success) {
            throw new Error('Starting the pipeline failed.');
        }
    }

    private assertSaveSuccess(
        result: Message | PipelineOperationStatus,
    ): asserts result is Message {
        if (!result.success) {
            throw new Error('Saving the pipeline failed.');
        }
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
