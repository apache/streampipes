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
import { PipelineElementService } from '@streampipes/platform-services';
import {
    PipelineElementConfig,
    PipelineElementUnion,
} from './model/editor.model';
import { SpBreadcrumbService } from '@streampipes/shared-ui';
import { ActivatedRoute } from '@angular/router';
import { zip } from 'rxjs';
import { SpPipelineRoutes } from '../pipelines/pipelines.routes';

@Component({
    selector: 'sp-editor',
    templateUrl: './editor.component.html',
    styleUrls: ['./editor.component.scss'],
})
export class EditorComponent implements OnInit {
    allElements: PipelineElementUnion[] = [];

    rawPipelineModel: PipelineElementConfig[] = [];
    currentModifiedPipelineId: string;

    allElementsLoaded = false;

    constructor(
        private pipelineElementService: PipelineElementService,
        private activatedRoute: ActivatedRoute,
        private breadcrumbService: SpBreadcrumbService,
    ) {}

    ngOnInit() {
        this.activatedRoute.params.subscribe(params => {
            if (params.pipelineId) {
                this.currentModifiedPipelineId = params.pipelineId;
            } else {
                this.breadcrumbService.updateBreadcrumb([
                    SpPipelineRoutes.BASE,
                    { label: 'New Pipeline' },
                ]);
            }
        });
        zip(
            this.pipelineElementService.getDataStreams(),
            this.pipelineElementService.getDataProcessors(),
            this.pipelineElementService.getDataSinks(),
        ).subscribe(response => {
            this.allElements = this.allElements
                .concat(response[0])
                .concat(response[1])
                .concat(response[2])
                .sort((a, b) => {
                    return a.name.localeCompare(b.name);
                });
            this.allElementsLoaded = true;
        });
    }
}
