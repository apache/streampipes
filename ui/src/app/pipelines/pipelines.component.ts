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

import { Component, OnDestroy, OnInit } from '@angular/core';
import {
    FunctionId,
    FunctionsService,
    Pipeline,
    PipelineService,
} from '@streampipes/platform-services';
import {
    CurrentUserService,
    DialogRef,
    DialogService,
    PanelType,
    SpBreadcrumbService,
} from '@streampipes/shared-ui';
import { StartAllPipelinesDialogComponent } from './dialog/start-all-pipelines/start-all-pipelines-dialog.component';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { UserPrivilege } from '../_enums/user-privilege.enum';
import { SpPipelineRoutes } from './pipelines.routes';
import { UserRole } from '../_enums/user-role.enum';
import { ShepherdService } from '../services/tour/shepherd.service';
import { Subscription } from 'rxjs';

@Component({
    selector: 'sp-pipelines',
    templateUrl: './pipelines.component.html',
    styleUrls: ['./pipelines.component.scss'],
})
export class PipelinesComponent implements OnInit, OnDestroy {
    pipeline: Pipeline;
    pipelines: Pipeline[] = [];
    filteredPipelines: Pipeline[] = [];
    starting: boolean;
    stopping: boolean;

    pipelinesReady = false;
    hasPipelineWritePrivileges = false;

    functions: FunctionId[] = [];
    functionsReady = false;
    isAdminRole = false;

    tutorialActive = false;
    tutorialActiveSubscription: Subscription;
    userSubscription: Subscription;

    constructor(
        private pipelineService: PipelineService,
        private dialogService: DialogService,
        private authService: AuthService,
        private currentUserService: CurrentUserService,
        private router: Router,
        private functionsService: FunctionsService,
        private breadcrumbService: SpBreadcrumbService,
        private shepherdService: ShepherdService,
    ) {
        this.starting = false;
        this.stopping = false;
    }

    ngOnInit() {
        this.breadcrumbService.updateBreadcrumb(
            this.breadcrumbService.getRootLink(SpPipelineRoutes.BASE),
        );
        this.userSubscription = this.currentUserService.user$.subscribe(
            user => {
                this.hasPipelineWritePrivileges = this.authService.hasRole(
                    UserPrivilege.PRIVILEGE_WRITE_PIPELINE,
                );
                this.isAdminRole = this.authService.hasRole(
                    UserRole.ROLE_ADMIN,
                );
            },
        );
        if (this.shepherdService.isTourActive()) {
            this.shepherdService.trigger('pipeline-started');
        }
        this.getPipelines();
        this.getFunctions();
        this.tutorialActiveSubscription =
            this.shepherdService.tutorialActive$.subscribe(tutorialActive => {
                this.tutorialActive = tutorialActive;
            });
    }

    getFunctions() {
        this.functionsService.getActiveFunctions().subscribe(functions => {
            this.functions = functions.map(f => f.functionId);
            this.functionsReady = true;
        });
    }

    getPipelines() {
        this.pipelines = [];
        this.pipelineService.getPipelines().subscribe(pipelines => {
            this.pipelines = pipelines;
            this.applyPipelineFilters(new Set<string>());
        });
    }

    applyPipelineFilters(elementIds: Set<string>) {
        if (elementIds.size == 0) {
            this.filteredPipelines = this.pipelines;
        } else {
            this.filteredPipelines = this.pipelines.filter(p =>
                elementIds.has(p.elementId),
            );
        }
        this.pipelinesReady = true;
    }

    checkCurrentSelectionStatus(status) {
        let active = true;
        this.filteredPipelines.forEach(pipeline => {
            if (pipeline.running === status) {
                active = false;
            }
        });
        return active;
    }

    startAllPipelines(action) {
        const dialogRef: DialogRef<StartAllPipelinesDialogComponent> =
            this.dialogService.open(StartAllPipelinesDialogComponent, {
                panelType: PanelType.STANDARD_PANEL,
                title: (action ? 'Start' : 'Stop') + ' all pipelines',
                width: '70vw',
                data: {
                    pipelines: this.filteredPipelines,
                    action: action,
                },
            });

        dialogRef.afterClosed().subscribe(data => {
            if (data) {
                this.getPipelines();
            }
        });
    }

    startPipelineTour(): void {
        this.shepherdService.startPipelineTour();
    }

    navigateToPipelineEditor() {
        this.router
            .navigate(['pipelines', 'create'])
            .then(() =>
                this.shepherdService.trigger('pipeline-new-button-clicked'),
            );
    }

    ngOnDestroy() {
        this.userSubscription?.unsubscribe();
        this.tutorialActiveSubscription?.unsubscribe();
    }
}
