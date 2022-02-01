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

import { RxStompService } from '@stomp/ng2-stompjs';
import { ResizeService } from '../../../services/resize.service';
import { BaseNgxChartsStreamPipesWidget } from './base-ngx-charts-widget';
import { StaticPropertyExtractor } from '../../../sdk/extractor/static-property-extractor';
import { LineConfig } from '../line/line-config';
import { DashboardService } from '../../../services/dashboard.service';
import { Directive } from '@angular/core';

@Directive()
export abstract class BaseNgxLineChartsStreamPipesWidget extends BaseNgxChartsStreamPipesWidget {

    multi: any = [];

    selectedNumberProperty: string;
    selectedTimestampProperty: string;
    minYAxisRange: number;
    maxYAxisRange: number;

    constructor(rxStompService: RxStompService, dashboardService: DashboardService, resizeService: ResizeService) {
        super(rxStompService, dashboardService, resizeService);
    }

    ngOnInit(): void {
        super.ngOnInit();
        this.multi = [
            {
                'name': this.selectedNumberProperty,
                'series': [
                ]
            }];
    }

    protected extractConfig(extractor: StaticPropertyExtractor) {
        this.selectedNumberProperty = extractor.mappingPropertyValue(LineConfig.NUMBER_MAPPING_KEY);
        this.selectedTimestampProperty = extractor.mappingPropertyValue(LineConfig.TIMESTAMP_MAPPING_KEY);
        this.minYAxisRange = extractor.integerParameter(LineConfig.MIN_Y_AXIS_KEY);
        this.maxYAxisRange = extractor.integerParameter(LineConfig.MAX_Y_AXIS_KEY);
    }

    protected onEvent(event: any) {
        const time = event[this.selectedTimestampProperty];
        const value = event[this.selectedNumberProperty];
        this.makeEvent(time, value);
    }

    makeEvent(time: any, value: any): void {
        this.multi[0].series.push({'name': time, 'value': value});
        if (this.multi[0].series.length > 10) {
            this.multi[0].series.shift();
        }
        this.multi = [...this.multi];
    }

    timestampTickFormatting(timestamp: any): string {
        const date = new Date(timestamp);
        const timeString = date.getHours() + ':' + date.getMinutes().toString().substr(-2) + ':' + date.getSeconds().toString().substr(-2);
        return timeString;
    }
}
