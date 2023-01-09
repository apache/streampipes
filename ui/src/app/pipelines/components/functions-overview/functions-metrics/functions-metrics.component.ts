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

@Component({
    selector: 'sp-functions-metrics',
    templateUrl: './functions-metrics.component.html',
    styleUrls: [],
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
