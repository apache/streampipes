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

import { ResizeService } from '../../../services/resize.service';
import { BaseNgxChartsStreamPipesWidget } from './base-ngx-charts-widget';
import { StaticPropertyExtractor } from '../../../sdk/extractor/static-property-extractor';
import { LineConfig } from '../line/line-config';
import { Directive, OnInit } from '@angular/core';
import { DatalakeRestService } from '@streampipes/platform-services';
import { WidgetConfigBuilder } from '../../../registry/widget-config-builder';
import { BaseStreamPipesWidget } from './base-widget';

@Directive()
export abstract class BaseNgxLineChartsStreamPipesWidget
    extends BaseNgxChartsStreamPipesWidget
    implements OnInit
{
    multi: any = [];

    selectedNumberProperty: string;
    selectedTimestampProperty: string;
    minYAxisRange: number;
    maxYAxisRange: number;

    constructor(
        dataLakeService: DatalakeRestService,
        resizeService: ResizeService,
    ) {
        super(dataLakeService, resizeService);
    }

    ngOnInit(): void {
        super.ngOnInit();
        this.multi = [
            {
                name: this.selectedNumberProperty,
                series: [],
            },
        ];
    }

    protected extractConfig(extractor: StaticPropertyExtractor) {
        this.selectedNumberProperty = extractor.mappingPropertyValue(
            LineConfig.NUMBER_MAPPING_KEY,
        );
        this.minYAxisRange = extractor.integerParameter(
            LineConfig.MIN_Y_AXIS_KEY,
        );
        this.maxYAxisRange = extractor.integerParameter(
            LineConfig.MAX_Y_AXIS_KEY,
        );
    }

    protected onEvent(events: any[]) {
        this.multi[0].series = events.map(ev => {
            return {
                name: ev[BaseStreamPipesWidget.TIMESTAMP_KEY],
                value: ev[this.selectedNumberProperty],
            };
        });
        this.multi = [...this.multi];
    }

    timestampTickFormatting(timestamp: any): string {
        const padL = (nr, len = 2, chr = `0`) => `${nr}`.padStart(2, chr);
        const date = new Date(timestamp);
        return (
            date.getHours() +
            ':' +
            `${padL(date.getMinutes())}` +
            ':' +
            `${padL(date.getSeconds())}`
        );
    }

    protected getQueryLimit(extractor: StaticPropertyExtractor): number {
        return extractor.integerParameter(WidgetConfigBuilder.QUERY_LIMIT_KEY);
    }

    getFieldsToQuery(): string[] {
        return [this.selectedNumberProperty];
    }
}
