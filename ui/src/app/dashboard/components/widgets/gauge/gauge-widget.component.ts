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
import { BaseNgxChartsStreamPipesWidget } from '../base/base-ngx-charts-widget';
import { ResizeService } from '../../../services/resize.service';
import { StaticPropertyExtractor } from '../../../sdk/extractor/static-property-extractor';
import { GaugeConfig } from './gauge-config';
import { DatalakeRestService } from '@streampipes/platform-services';

@Component({
    selector: 'sp-gauge-widget',
    templateUrl: './gauge-widget.component.html',
    styleUrls: ['./gauge-widget.component.css'],
})
export class GaugeWidgetComponent
    extends BaseNgxChartsStreamPipesWidget
    implements OnInit, OnDestroy
{
    data: any = [];
    min: number;
    max: number;

    selectedProperty: string;

    constructor(
        dataLakeService: DatalakeRestService,
        resizeService: ResizeService,
    ) {
        super(dataLakeService, resizeService);
    }

    ngOnInit(): void {
        super.ngOnInit();
    }

    ngOnDestroy(): void {
        super.ngOnDestroy();
    }

    extractConfig(extractor: StaticPropertyExtractor) {
        this.min = extractor.integerParameter(GaugeConfig.MIN_KEY);
        this.max = extractor.integerParameter(GaugeConfig.MAX_KEY);
        this.selectedProperty = extractor.mappingPropertyValue(
            GaugeConfig.NUMBER_MAPPING_KEY,
        );
    }

    isNumber(item: any): boolean {
        return false;
    }

    protected onEvent(events: any[]) {
        this.data[0] = {
            name: 'value',
            value: events[0][this.selectedProperty],
        };
        this.data = [...this.data];
    }

    protected getQueryLimit(extractor: StaticPropertyExtractor): number {
        return 1;
    }

    getFieldsToQuery(): string[] {
        return [this.selectedProperty];
    }
}
