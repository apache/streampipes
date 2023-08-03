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
import { SpAbstractAdapterDetailsDirective } from '../abstract-adapter-details.directive';
import { ActivatedRoute } from '@angular/router';
import {
    AdapterService,
    AdapterMonitoringService,
} from '@streampipes/platform-services';
import {
    CurrentUserService,
    SpBreadcrumbService,
} from '@streampipes/shared-ui';
import { SpConnectRoutes } from '../../../connect.routes';

@Component({
    selector: 'sp-adapter-details-overview',
    templateUrl: './adapter-details-overview.component.html',
    styleUrls: ['./adapter-details-overview.component.scss'],
})
export class SpAdapterDetailsOverviewComponent
    extends SpAbstractAdapterDetailsDirective
    implements OnInit
{
    constructor(
        currentUserService: CurrentUserService,
        activatedRoute: ActivatedRoute,
        adapterService: AdapterService,
        adapterMonitoringService: AdapterMonitoringService,
        breadcrumbService: SpBreadcrumbService,
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
        this.breadcrumbService.updateBreadcrumb([
            SpConnectRoutes.BASE,
            { label: this.adapter.name },
            { label: 'Overview' },
        ]);
    }
}
