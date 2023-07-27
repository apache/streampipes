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

import * as FileSaver from 'file-saver';
import { Component, OnInit } from '@angular/core';
import {
    FunctionId,
    FunctionsService,
    Pipeline,
    PipelineCategory,
    PipelineService,
} from '@streampipes/platform-services';
import {
    CurrentUserService,
    DialogRef,
    DialogService,
    PanelType,
    SpBreadcrumbService,
} from '@streampipes/shared-ui';
import { ImportPipelineDialogComponent } from './dialog/import-pipeline/import-pipeline-dialog.component';
import { StartAllPipelinesDialogComponent } from './dialog/start-all-pipelines/start-all-pipelines-dialog.component';
import { PipelineCategoriesDialogComponent } from './dialog/pipeline-categories/pipeline-categories-dialog.component';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { UserPrivilege } from '../_enums/user-privilege.enum';
import { SpPipelineRoutes } from './pipelines.routes';
import { UserRole } from '../_enums/user-role.enum';

@Component({
    selector: 'sp-pipelines',
    templateUrl: './pipelines.component.html',
    styleUrls: ['./pipelines.component.scss'],
})
export class PipelinesComponent implements OnInit {
    pipeline: Pipeline;
    pipelines: Pipeline[] = [];
    systemPipelines: Pipeline[] = [];
    starting: boolean;
    stopping: boolean;
    pipelineCategories: PipelineCategory[];
    activeCategoryId: string;

    pipelineIdToStart: string;

    pipelineToStart: Pipeline;
    systemPipelineToStart: Pipeline;

    pipelinesReady = false;

    selectedCategoryIndex = 0;
    hasPipelineWritePrivileges = false;

    functions: FunctionId[] = [];
    functionsReady = false;
    isAdminRole = false;

    constructor(
        private pipelineService: PipelineService,
        private dialogService: DialogService,
        private activatedRoute: ActivatedRoute,
        private authService: AuthService,
        private currentUserService: CurrentUserService,
        private router: Router,
        private functionsService: FunctionsService,
        private breadcrumbService: SpBreadcrumbService,
    ) {
        this.pipelineCategories = [];
        this.starting = false;
        this.stopping = false;
    }

    ngOnInit() {
        this.breadcrumbService.updateBreadcrumb(
            this.breadcrumbService.getRootLink(SpPipelineRoutes.BASE),
        );
        this.currentUserService.user$.subscribe(user => {
            this.hasPipelineWritePrivileges = this.authService.hasRole(
                UserPrivilege.PRIVILEGE_WRITE_PIPELINE,
            );
            this.isAdminRole = this.authService.hasRole(UserRole.ROLE_ADMIN);
        });
        this.activatedRoute.queryParams.subscribe(params => {
            if (params['pipeline']) {
                this.pipelineIdToStart = params['pipeline'];
            }
            this.getPipelineCategories();
            this.getPipelines();
            this.getFunctions();
        });
    }

    setSelectedTab(index) {
        this.activeCategoryId =
            index === 0 ? undefined : this.pipelineCategories[index - 1]._id;
    }

    exportPipelines() {
        const blob = new Blob([JSON.stringify(this.pipelines)], {
            type: 'application/json',
        });
        FileSaver.saveAs(blob, 'pipelines.json');
    }

    getFunctions() {
        this.functionsService.getActiveFunctions().subscribe(functions => {
            this.functions = functions.map(f => f.functionId);
            console.log(this.functions);
            this.functionsReady = true;
        });
    }

    getPipelines() {
        this.pipelines = [];
        this.pipelineService.getPipelines().subscribe(pipelines => {
            this.pipelines = pipelines;
            this.checkForImmediateStart(pipelines);
            this.pipelinesReady = true;
        });
    }

    checkForImmediateStart(pipelines: Pipeline[]) {
        this.pipelineToStart = undefined;
        pipelines.forEach(pipeline => {
            if (pipeline._id === this.pipelineIdToStart) {
                this.pipelineToStart = pipeline;
            }
        });
        this.pipelineIdToStart = undefined;
    }

    getPipelineCategories() {
        this.pipelineService
            .getPipelineCategories()
            .subscribe(pipelineCategories => {
                this.pipelineCategories = pipelineCategories;
            });
    }

    activeClass(pipeline) {
        return 'active-pipeline';
    }

    checkCurrentSelectionStatus(status) {
        let active = true;
        this.pipelines.forEach(pipeline => {
            if (
                !this.activeCategoryId ||
                pipeline.pipelineCategories.some(
                    pc => pc === this.activeCategoryId,
                )
            ) {
                if (pipeline.running === status) {
                    active = false;
                }
            }
        });
        return active;
    }

    openImportPipelinesDialog() {
        const dialogRef: DialogRef<ImportPipelineDialogComponent> =
            this.dialogService.open(ImportPipelineDialogComponent, {
                panelType: PanelType.STANDARD_PANEL,
                title: 'Import Pipeline',
                width: '70vw',
                data: {
                    pipelines: this.pipelines,
                },
            });
        dialogRef.afterClosed().subscribe(data => {
            if (data) {
                this.refreshPipelines();
            }
        });
    }

    startAllPipelines(action) {
        const dialogRef: DialogRef<StartAllPipelinesDialogComponent> =
            this.dialogService.open(StartAllPipelinesDialogComponent, {
                panelType: PanelType.STANDARD_PANEL,
                title: (action ? 'Start' : 'Stop') + ' all pipelines',
                width: '70vw',
                data: {
                    pipelines: this.pipelines,
                    action: action,
                    activeCategoryId: this.activeCategoryId,
                },
            });

        dialogRef.afterClosed().subscribe(data => {
            if (data) {
                this.refreshPipelines();
            }
        });
    }

    showPipelineCategoriesDialog() {
        const dialogRef: DialogRef<PipelineCategoriesDialogComponent> =
            this.dialogService.open(PipelineCategoriesDialogComponent, {
                panelType: PanelType.STANDARD_PANEL,
                title: 'Pipeline Categories',
                width: '70vw',
                data: {
                    pipelines: this.pipelines,
                    systemPipelines: this.systemPipelines,
                },
            });

        dialogRef.afterClosed().subscribe(data => {
            this.getPipelineCategories();
            this.refreshPipelines();
        });
    }

    refreshPipelines() {
        this.getPipelines();
    }

    showPipeline(pipeline) {
        pipeline.display = !pipeline.display;
    }

    navigateToPipelineEditor() {
        this.router.navigate(['pipelines', 'create']);
    }
}
