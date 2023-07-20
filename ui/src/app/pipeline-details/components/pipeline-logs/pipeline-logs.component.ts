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

import { Component, OnInit } from '@angular/core';
import { SpPipelineDetailsDirective } from '../sp-pipeline-details.directive';
import { SpPipelineRoutes } from '../../../pipelines/pipelines.routes';
import { ActivatedRoute } from '@angular/router';
import {
    PipelineMonitoringService,
    PipelineService,
    SpLogEntry,
} from '@streampipes/platform-services';
import { AuthService } from '../../../services/auth.service';
import {
    CurrentUserService,
    SpBreadcrumbService,
} from '@streampipes/shared-ui';
import { PipelineElementUnion } from '../../../editor/model/editor.model';

@Component({
    selector: 'sp-pipeline-logs',
    templateUrl: './pipeline-logs.component.html',
    styleUrls: ['./pipeline-logs.component.scss'],
})
export class PipelineLogsComponent
    extends SpPipelineDetailsDirective
    implements OnInit
{
    logInfos: Record<string, SpLogEntry[]> = {};
    selectedElementId: string;

    constructor(
        activatedRoute: ActivatedRoute,
        pipelineService: PipelineService,
        authService: AuthService,
        currentUserService: CurrentUserService,
        breadcrumbService: SpBreadcrumbService,
        private pipelineMonitoringService: PipelineMonitoringService,
    ) {
        super(
            activatedRoute,
            pipelineService,
            authService,
            currentUserService,
            breadcrumbService,
        );
    }

    ngOnInit(): void {
        super.onInit();
    }

    onPipelineAvailable(): void {
        this.breadcrumbService.updateBreadcrumb([
            SpPipelineRoutes.BASE,
            { label: this.pipeline.name },
            { label: 'Logs' },
        ]);
        this.receiveLogInfos();
    }

    receiveLogInfos(): void {
        this.pipelineMonitoringService
            .getLogInfoForPipeline(this.pipeline._id)
            .subscribe(response => {
                this.logInfos = response;
            });
    }

    selectElement(pipelineElement: PipelineElementUnion): void {
        this.selectedElementId = pipelineElement.elementId;
    }

    triggerLogUpdate(): void {
        this.pipelineMonitoringService
            .triggerMonitoringUpdate()
            .subscribe(res => {
                this.receiveLogInfos();
            });
    }
}
