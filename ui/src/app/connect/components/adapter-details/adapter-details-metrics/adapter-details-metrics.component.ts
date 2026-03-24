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

import { Component, OnInit, inject } from '@angular/core';
import { SpAbstractAdapterDetailsDirective } from '../abstract-adapter-details.directive';
import { ActivatedRoute } from '@angular/router';
import {
    AdapterMonitoringService,
    AdapterService,
    SpMetricsEntry,
} from '@streampipes/platform-services';
import {
    CurrentUserService,
    SpBasicNavTabsComponent,
    SpBreadcrumbService,
} from '@streampipes/shared-ui';
import { SpConnectRoutes } from '../../../connect.breadcrumb';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatIconButton } from '@angular/material/button';
import { MatTooltip } from '@angular/material/tooltip';
import { SpSimpleMetricsComponent } from '../../../../core-ui/monitoring/simple-metrics/simple-metrics.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-adapter-details-metrics',
    templateUrl: './adapter-details-metrics.component.html',
    styleUrls: [],
    imports: [
        SpBasicNavTabsComponent,
        LayoutDirective,
        LayoutAlignDirective,
        MatIconButton,
        MatTooltip,
        FlexDirective,
        SpSimpleMetricsComponent,
        TranslatePipe,
    ],
})
export class SpAdapterDetailsMetricsComponent
    extends SpAbstractAdapterDetailsDirective
    implements OnInit
{
    adapterMetrics: SpMetricsEntry;

    constructor() {
        const currentUserService = inject(CurrentUserService);
        const activatedRoute = inject(ActivatedRoute);
        const adapterService = inject(AdapterService);
        const adapterMonitoringService = inject(AdapterMonitoringService);
        const breadcrumbService = inject(SpBreadcrumbService);

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

    loadMetrics(): void {
        this.adapterMonitoringService
            .getMetricsInfoForAdapter(this.currentAdapterId)
            .subscribe(res => {
                this.adapterMetrics = res;
            });
    }

    onAdapterLoaded(): void {
        this.breadcrumbService.updateBreadcrumb([
            SpConnectRoutes.BASE,
            { label: this.adapter.name },
            { label: 'Metrics' },
        ]);
        this.loadMetrics();
    }
}
