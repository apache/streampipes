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
import { ActivatedRoute } from '@angular/router';
import { SpPipelineDetailsDirective } from '../sp-pipeline-details.directive';
import { AuthService } from '../../../services/auth.service';
import { PipelineService } from '@streampipes/platform-services';
import { PipelineElementUnion } from '../../../editor/model/editor.model';
import {
    CurrentUserService,
    SpBreadcrumbService,
} from '@streampipes/shared-ui';
import { SpPipelineRoutes } from '../../../pipelines/pipelines.routes';

@Component({
    selector: 'sp-pipeline-details-overview-component',
    templateUrl: './pipeline-details-overview.component.html',
    styleUrls: ['./pipeline-details-overview.component.scss'],
})
export class SpPipelineDetailsOverviewComponent
    extends SpPipelineDetailsDirective
    implements OnInit
{
    tabs = [];
    selectedElement: PipelineElementUnion;

    constructor(
        activatedRoute: ActivatedRoute,
        pipelineService: PipelineService,
        authService: AuthService,
        currentUserService: CurrentUserService,
        breadcrumbService: SpBreadcrumbService,
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

    selectElement(element: PipelineElementUnion) {
        this.selectedElement = element;
    }

    onPipelineAvailable(): void {
        this.breadcrumbService.updateBreadcrumb([
            SpPipelineRoutes.BASE,
            { label: this.pipeline.name },
            { label: 'Overview' },
        ]);
    }
}
