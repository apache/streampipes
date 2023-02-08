/*
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

import { Component, OnDestroy, OnInit } from '@angular/core';
import { BaseStreamPipesWidget } from '../base/base-widget';
import { StaticPropertyExtractor } from '../../../sdk/extractor/static-property-extractor';
import { ResizeService } from '../../../services/resize.service';
import { TrafficLightConfig } from './traffic-light-config';
import { DatalakeRestService } from '@streampipes/platform-services';

@Component({
    selector: 'sp-traffic-light-widget',
    templateUrl: './traffic-light-widget.component.html',
    styleUrls: ['./traffic-light-widget.component.css'],
})
export class TrafficLightWidgetComponent
    extends BaseStreamPipesWidget
    implements OnInit, OnDestroy
{
    items: string[];
    width: number;
    height: number;

    containerWidth: number;
    containerHeight: number;

    lightWidth: number;
    lightHeight: number;

    selectedWarningRange: number;
    selectedFieldToObserve: string;
    selectedLimitGreaterThan: boolean;
    selectedThreshold: number;

    activeClass = 'red';

    constructor(
        dataLakeService: DatalakeRestService,
        resizeService: ResizeService,
    ) {
        super(dataLakeService, resizeService, false);
    }

    ngOnInit(): void {
        super.ngOnInit();
        this.width = this.computeCurrentWidth(this.itemWidth);
        this.height = this.computeCurrentHeight(this.itemHeight);
        this.updateContainerSize();
        this.updateLightSize();
        this.items = [];
    }

    updateContainerSize() {
        this.containerHeight = this.height - 60;
        this.containerWidth = this.containerHeight / 3;
    }

    updateLightSize() {
        this.lightWidth = (this.containerHeight - 60) / 3;
        this.lightHeight = this.lightWidth;
    }

    ngOnDestroy(): void {
        super.ngOnDestroy();
    }

    extractConfig(extractor: StaticPropertyExtractor) {
        this.selectedThreshold = extractor.integerParameter(
            TrafficLightConfig.CRITICAL_VALUE_KEY,
        );
        this.selectedFieldToObserve = extractor.mappingPropertyValue(
            TrafficLightConfig.NUMBER_MAPPING_KEY,
        );
        this.selectedWarningRange = extractor.integerParameter(
            TrafficLightConfig.WARNING_RANGE_KEY,
        );
        this.selectedLimitGreaterThan =
            extractor.selectedSingleValue(
                TrafficLightConfig.CRITICAL_VALUE_LIMIT,
            ) === 'Upper Limit';
    }

    getFieldsToQuery(): string[] {
        return [this.selectedFieldToObserve];
    }

    protected onEvent(events: any[]) {
        const item = events[0][this.selectedFieldToObserve];
        if (this.isInOkRange(item)) {
            this.activeClass = 'green';
        } else if (this.isInWarningRange(item)) {
            this.activeClass = 'yellow';
        } else {
            this.activeClass = 'red';
        }
    }

    exceedsThreshold(value) {
        if (this.selectedLimitGreaterThan) {
            return value >= this.selectedThreshold;
        } else {
            return value <= this.selectedThreshold;
        }
    }

    isInWarningRange(value) {
        if (this.exceedsThreshold(value)) {
            return false;
        } else {
            if (this.selectedLimitGreaterThan) {
                return (
                    value >=
                    this.selectedThreshold -
                        this.selectedThreshold *
                            (this.selectedWarningRange / 100)
                );
            } else {
                return (
                    value <=
                    this.selectedThreshold +
                        this.selectedThreshold *
                            (this.selectedWarningRange / 100)
                );
            }
        }
    }
    isInOkRange(value) {
        return !this.exceedsThreshold(value) && !this.isInWarningRange(value);
    }

    protected onSizeChanged(width: number, height: number) {
        this.width = width;
        this.height = height;
        this.updateContainerSize();
        this.updateLightSize();
    }

    protected getQueryLimit(extractor: StaticPropertyExtractor): number {
        return 1;
    }
}
