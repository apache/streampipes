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

import { Component, OnDestroy, OnInit, ViewChild, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import {
    Pipeline,
    PipelineCanvasMetadata,
    PipelineCanvasMetadataService,
    PipelineMonitoringService,
    PipelineService,
    SpLogEntry,
    SpMetricsEntry,
} from '@streampipes/platform-services';
import { PipelineElementUnion } from '../editor/model/editor.model';
import {
    CurrentUserService,
    DialogService,
    KeyboardShortcutService,
    PanelType,
    ShortcutRegistration,
    SpBreadcrumbService,
} from '@streampipes/shared-ui';
import { SpPipelineRoutes } from '../pipelines/pipelines.breadcrumb';
import { UserPrivilege } from '../core/auth/user-privilege.enum';
import { forkJoin, interval, Observable, of, Subscription } from 'rxjs';
import { catchError, filter, map, switchMap } from 'rxjs/operators';
import { PipelinePreviewComponent } from './components/preview/pipeline-preview.component';
import { HttpContext } from '@angular/common/http';
import { NGX_LOADING_BAR_IGNORED } from '@ngx-loading-bar/http-client';
import { PipelineCodeDialogComponent } from './dialogs/pipeline-code/pipeline-code-dialog.component';
import { SpBasicViewComponent } from '@streampipes/shared-ui';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { PipelineDetailsToolbarComponent } from './components/pipeline-details-toolbar/pipeline-details-toolbar.component';
import { PipelineDetailsExpansionPanelComponent } from './components/pipeline-details-expansion-panel/pipeline-details-expansion-panel.component';
import { TranslatePipe } from '@ngx-translate/core';
import { PipelineOperationsService } from '../pipelines/services/pipeline-operations.service';
import { MeasurementUpdateDialogComponent } from '../pipelines/dialog/measurement-update/measurement-update-dialog.component';
import { MeasurementUpdateAction } from '../pipelines/model/pipeline-model';

@Component({
    selector: 'sp-pipeline-details-overview-component',
    templateUrl: './pipeline-details.component.html',
    styleUrls: ['./pipeline-details.component.scss'],
    imports: [
        SpBasicViewComponent,
        FlexDirective,
        LayoutAlignDirective,
        PipelineDetailsToolbarComponent,
        LayoutDirective,
        PipelinePreviewComponent,
        PipelineDetailsExpansionPanelComponent,
        TranslatePipe,
    ],
})
export class SpPipelineDetailsComponent implements OnInit, OnDestroy {
    private activatedRoute = inject(ActivatedRoute);
    private pipelineService = inject(PipelineService);
    private pipelineCanvasService = inject(PipelineCanvasMetadataService);
    private authService = inject(AuthService);
    private currentUserService = inject(CurrentUserService);
    private breadcrumbService = inject(SpBreadcrumbService);
    private pipelineMonitoringService = inject(PipelineMonitoringService);
    private dialogService = inject(DialogService);
    private router = inject(Router);
    private pipelineOperationsService = inject(PipelineOperationsService);
    private shortcutService = inject(KeyboardShortcutService);

    hasPipelineWritePrivileges = false;

    currentPipelineId: string;

    pipeline: Pipeline;
    pipelineCanvasMetadata: PipelineCanvasMetadata;

    pipelineAvailable = false;
    selectedElement: PipelineElementUnion;
    autoRefresh = false;
    metricsInfo: Record<string, SpMetricsEntry> = {};
    logInfo: Record<string, SpLogEntry[]> = {};
    previewModeActive = false;
    pipelineNotFound = false;

    currentUser$: Subscription;
    autoRefresh$: Subscription;
    private shortcutReg: ShortcutRegistration;
    private measurementUpdateDialogOpened = false;

    @ViewChild('pipelinePreviewComponent')
    pipelinePreviewComponent: PipelinePreviewComponent;

    ngOnInit(): void {
        this.shortcutReg = this.shortcutService.register('pipeline-details', [
            { key: 'e', action: () => this.onShortcutEdit() },
        ]);

        this.currentUser$ = this.currentUserService.user$.subscribe(user => {
            this.hasPipelineWritePrivileges = this.authService.hasRole(
                UserPrivilege.PRIVILEGE_WRITE_PIPELINE,
            );
            const pipelineId = this.activatedRoute.snapshot.params.pipelineId;
            if (pipelineId) {
                this.currentPipelineId = pipelineId;
                this.loadPipeline();
            }
        });
    }

    loadPipeline(): void {
        forkJoin([
            this.pipelineService.getPipelineById(this.currentPipelineId).pipe(
                catchError(error => {
                    if (error.status === 404) {
                        this.pipelineNotFound = true;
                    }
                    return of(null);
                }),
            ),
            this.pipelineCanvasService
                .getPipelineCanvasMetadata(this.currentPipelineId)
                .pipe(
                    map(response => {
                        if (response === null) {
                            this.pipelineAvailable = false;
                            return new PipelineCanvasMetadata();
                        }
                        return response;
                    }),
                    catchError(error => {
                        this.pipelineAvailable = false;
                        return of(new PipelineCanvasMetadata());
                    }),
                ),
        ]).subscribe(([pipeline, metadata]) => {
            this.pipeline = pipeline;
            this.pipelineCanvasMetadata = metadata;

            if (pipeline && !this.pipelineNotFound) {
                this.pipelineAvailable = true;
                this.onPipelineAvailable();
            }
        });
    }

    selectElement(element: PipelineElementUnion) {
        this.selectedElement = element;
    }

    onPipelineAvailable(): void {
        this.triggerReload();
        this.setupAutoRefresh();
        this.breadcrumbService.updateBreadcrumb([
            SpPipelineRoutes.BASE,
            { label: this.pipeline.name },
            { label: 'Overview' },
        ]);
        this.openMeasurementUpdateDialogIfRequired();
    }

    openMeasurementUpdateDialogIfRequired(): void {
        if (
            this.measurementUpdateDialogOpened ||
            this.pipeline.healthStatus !== 'HANDLE_MEASUREMENT_UPDATE'
        ) {
            return;
        }

        this.measurementUpdateDialogOpened = true;
        const dialogRef = this.dialogService.open(
            MeasurementUpdateDialogComponent,
            {
                panelType: PanelType.STANDARD_PANEL,
                title: 'Measurement update required',
                width: '50vw',
                data: {
                    pipeline: this.pipeline,
                },
            },
        );

        dialogRef.afterClosed().subscribe(action => {
            this.handleMeasurementUpdateAction(action);
        });
    }

    handleMeasurementUpdateAction(action?: MeasurementUpdateAction): void {
        if (action === 'edit-pipeline') {
            this.pipelineOperationsService.showPipelineInEditor(
                this.pipeline._id,
            );
        } else if (action === 'manage-datasets') {
            this.router.navigate(['datasets']);
        }
    }

    setupAutoRefresh(): void {
        this.autoRefresh$ = interval(5000)
            .pipe(
                filter(() => this.autoRefresh),
                switchMap(() => this.getMonitoringObservables(true)),
            )
            .subscribe(res => this.onMonitoringResultAvailable(res));
    }

    getMonitoringObservables(forceUpdate: boolean): Observable<any> {
        return forkJoin([
            this.getMetricsObservable(forceUpdate),
            this.getLogsObservable(),
        ]);
    }

    triggerReload(): void {
        forkJoin([
            this.getMetricsObservable(),
            this.getLogsObservable(),
        ]).subscribe(res => {
            this.onMonitoringResultAvailable(res);
        });
    }

    onMonitoringResultAvailable(
        res: [Record<string, SpMetricsEntry>, Record<string, SpLogEntry[]>],
    ): void {
        this.metricsInfo = res[0];
        this.logInfo = res[1];
    }

    getMetricsObservable(
        forceUpdate = false,
    ): Observable<Record<string, SpMetricsEntry>> {
        return this.pipelineMonitoringService.getMetricsInfoForPipeline(
            this.currentPipelineId,
            forceUpdate,
            new HttpContext().set(NGX_LOADING_BAR_IGNORED, true),
        );
    }

    getLogsObservable(): Observable<Record<string, SpLogEntry[]>> {
        return this.pipelineMonitoringService.getLogInfoForPipeline(
            this.currentPipelineId,
            new HttpContext().set(NGX_LOADING_BAR_IGNORED, true),
        );
    }

    toggleLivePreview(): void {
        this.previewModeActive = !this.previewModeActive;
        this.pipelinePreviewComponent?.toggleLivePreview();
    }

    openPipelineAsCodeDialog(): void {
        this.dialogService.open(PipelineCodeDialogComponent, {
            panelType: PanelType.SLIDE_IN_PANEL,
            width: '50vw',
            title: 'Pipeline code',
            data: {
                pipeline: this.pipeline,
            },
        });
    }

    editPipeline(): void {
        this.pipelineOperationsService.showPipelineInEditor(this.pipeline._id);
    }

    deletePipeline(): void {
        this.pipelineOperationsService.showDeleteDialog(
            this.pipeline,
            null,
            () => this.router.navigate(['pipelines']),
        );
    }

    private onShortcutEdit(): void {
        if (this.hasPipelineWritePrivileges) {
            this.editPipeline();
        }
    }

    ngOnDestroy() {
        this.shortcutReg?.unregister();
        this.currentUser$?.unsubscribe();
        this.autoRefresh$?.unsubscribe();
    }
}
