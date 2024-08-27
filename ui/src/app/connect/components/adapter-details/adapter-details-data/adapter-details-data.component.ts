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
import { SpAbstractAdapterDetailsDirective } from '../abstract-adapter-details.directive';
import {
    CurrentUserService,
    SpBreadcrumbService,
} from '@streampipes/shared-ui';
import { ActivatedRoute } from '@angular/router';
import {
    AdapterMonitoringService,
    AdapterService,
    PipelineElementService,
    SpDataStream,
} from '@streampipes/platform-services';

@Component({
    selector: 'sp-adapter-details-data',
    templateUrl: './adapter-details-data.component.html',
    styleUrl: './adapter-details-data.component.scss',
})
export class AdapterDetailsDataComponent
    extends SpAbstractAdapterDetailsDirective
    implements OnInit
{
    stream: SpDataStream;

    constructor(
        currentUserService: CurrentUserService,
        activatedRoute: ActivatedRoute,
        adapterService: AdapterService,
        adapterMonitoringService: AdapterMonitoringService,
        breadcrumbService: SpBreadcrumbService,
        private pipelineElementService: PipelineElementService,
    ) {
        super(
            currentUserService,
            activatedRoute,
            adapterService,
            adapterMonitoringService,
            breadcrumbService,
        );
    }

    ngOnInit(): void {
        super.onInit();
    }

    onAdapterLoaded(): void {
        const streamId = this.adapter.correspondingDataStreamElementId;

        this.pipelineElementService
            .getDataStreamByElementId(streamId)
            .subscribe(stream => (this.stream = stream));
    }
}
