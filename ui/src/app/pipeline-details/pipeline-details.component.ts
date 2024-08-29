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

import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
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
    SpBreadcrumbService,
} from '@streampipes/shared-ui';
import { SpPipelineRoutes } from '../pipelines/pipelines.routes';
import { UserPrivilege } from '../_enums/user-privilege.enum';
import { forkJoin, interval, Observable, of, Subscription } from 'rxjs';
import { catchError, filter, switchMap } from 'rxjs/operators';
import { PipelinePreviewComponent } from './components/preview/pipeline-preview.component';
import { HttpContext } from '@angular/common/http';
import { NGX_LOADING_BAR_IGNORED } from '@ngx-loading-bar/http-client';

@Component({
    selector: 'sp-pipeline-details-overview-component',
    templateUrl: './pipeline-details.component.html',
    styleUrls: ['./pipeline-details.component.scss'],
})
export class SpPipelineDetailsComponent implements OnInit, OnDestroy {
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

    currentUserSub: Subscription;
    autoRefreshSub: Subscription;

    @ViewChild('pipelinePreviewComponent')
    pipelinePreviewComponent: PipelinePreviewComponent;

    constructor(
        private activatedRoute: ActivatedRoute,
        private pipelineService: PipelineService,
        private pipelineCanvasService: PipelineCanvasMetadataService,
        private authService: AuthService,
        private currentUserService: CurrentUserService,
        private breadcrumbService: SpBreadcrumbService,
        private pipelineMonitoringService: PipelineMonitoringService,
    ) {}

    ngOnInit(): void {
        this.currentUserSub = this.currentUserService.user$.subscribe(user => {
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
            this.pipelineService.getPipelineById(this.currentPipelineId),
            this.pipelineCanvasService
                .getPipelineCanvasMetadata(this.currentPipelineId)
                .pipe(
                    catchError(() => {
                        return of(new PipelineCanvasMetadata());
                    }),
                ),
        ]).subscribe(res => {
            this.pipeline = res[0];
            this.pipelineCanvasMetadata = res[1];
            this.pipelineAvailable = true;
            this.onPipelineAvailable();
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
    }

    setupAutoRefresh(): void {
        this.autoRefreshSub = interval(5000)
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

    ngOnDestroy() {
        this.currentUserSub?.unsubscribe();
        this.autoRefreshSub?.unsubscribe();
    }
}
