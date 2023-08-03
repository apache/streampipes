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

import { Directive } from '@angular/core';
import { UserPrivilege } from '../../_enums/user-privilege.enum';
import { ActivatedRoute } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { Pipeline, PipelineService } from '@streampipes/platform-services';
import { SpPipelineDetailsTabs } from '../pipeline-details-tabs';
import {
    CurrentUserService,
    SpBreadcrumbService,
} from '@streampipes/shared-ui';

@Directive()
export abstract class SpPipelineDetailsDirective {
    tabs = [];
    hasPipelineWritePrivileges = false;
    hasPipelineDeletePrivileges = false;

    currentPipeline: string;
    pipeline: Pipeline;
    pipelineAvailable = false;

    constructor(
        protected activatedRoute: ActivatedRoute,
        protected pipelineService: PipelineService,
        protected authService: AuthService,
        protected currentUserService: CurrentUserService,
        protected breadcrumbService: SpBreadcrumbService,
    ) {}

    onInit(): void {
        this.currentUserService.user$.subscribe(user => {
            this.hasPipelineWritePrivileges = this.authService.hasRole(
                UserPrivilege.PRIVILEGE_WRITE_PIPELINE,
            );
            this.hasPipelineDeletePrivileges = this.authService.hasRole(
                UserPrivilege.PRIVILEGE_DELETE_PIPELINE,
            );
            const pipelineId = this.activatedRoute.snapshot.params.pipelineId;
            if (pipelineId) {
                this.tabs = new SpPipelineDetailsTabs().getTabs(pipelineId);
                this.currentPipeline = pipelineId;
                this.loadPipeline();
            }
        });
    }

    loadPipeline(): void {
        this.pipelineService
            .getPipelineById(this.currentPipeline)
            .subscribe(pipeline => {
                this.pipeline = pipeline;
                this.pipelineAvailable = true;
                this.onPipelineAvailable();
            });
    }

    abstract onPipelineAvailable(): void;
}
