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
import { AbstractFunctionDetailsDirective } from '../abstract-function-details.directive';
import { SpMetricsEntry } from '@streampipes/platform-services';
import { SpBasicNavTabsComponent } from '@streampipes/shared-ui';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatIconButton } from '@angular/material/button';
import { MatTooltip } from '@angular/material/tooltip';
import { KeyValuePipe } from '@angular/common';
import { SpSimpleMetricsComponent } from '../../../../core-ui/monitoring/simple-metrics/simple-metrics.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-functions-metrics',
    templateUrl: './functions-metrics.component.html',
    styleUrls: [],
    imports: [
        SpBasicNavTabsComponent,
        FlexDirective,
        LayoutDirective,
        LayoutAlignDirective,
        MatIconButton,
        MatTooltip,
        KeyValuePipe,
        SpSimpleMetricsComponent,
        TranslatePipe,
    ],
})
export class SpFunctionsMetricsComponent
    extends AbstractFunctionDetailsDirective
    implements OnInit
{
    metrics: SpMetricsEntry;

    ngOnInit(): void {
        super.onInit();
    }

    afterFunctionLoaded(): void {
        this.loadMetrics();
    }

    loadMetrics() {
        this.functionsService
            .getFunctionMetrics(this.activeFunction.functionId.id)
            .subscribe(metrics => {
                this.metrics = metrics;
                this.contentReady = true;
            });
    }

    getBreadcrumbLabel(): string {
        return 'Metrics';
    }
}
