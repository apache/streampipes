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
import { BaseWidgetConfig } from '../../base/base-widget-config';
import {
    DistributionChartVisConfig,
    DistributionChartWidgetModel,
} from '../model/distribution-chart-widget.model';
import { DataExplorerField } from '@streampipes/platform-services';
import { WidgetType } from '../../../../registry/data-explorer-widgets';

@Component({
    selector: 'sp-data-explorer-distribution-chart-widget-config',
    templateUrl: './distribution-chart-widget-config.component.html',
})
export class DistributionWidgetConfigComponent
    extends BaseWidgetConfig<
        DistributionChartWidgetModel,
        DistributionChartVisConfig
    >
    implements OnInit
{
    ngOnInit(): void {
        super.onInit();
    }

    setSelectedProperty(field: DataExplorerField) {
        this.currentlyConfiguredWidget.visualizationConfig.selectedProperty =
            field;
        this.triggerViewRefresh();
    }

    updateDisplayType(selectedType: string) {
        this.currentlyConfiguredWidget.visualizationConfig.displayType =
            selectedType;
        if (
            this.fieldProvider.numericFields.find(
                field =>
                    field ===
                    this.currentlyConfiguredWidget.visualizationConfig
                        .selectedProperty,
            ) === undefined
        ) {
            this.currentlyConfiguredWidget.visualizationConfig.selectedProperty =
                this.fieldProvider.numericFields[0];
        }
        this.triggerViewRefresh();
    }

    updateRoundingValue(selectedType: number) {
        this.currentlyConfiguredWidget.visualizationConfig.roundingValue =
            selectedType;
        this.triggerViewRefresh();
    }

    onResolutionChange(resolution: number): void {
        this.currentlyConfiguredWidget.visualizationConfig.resolution =
            resolution;
        this.triggerViewRefresh();
    }

    onNumBinChange(numberOfBins: number): void {
        this.currentlyConfiguredWidget.visualizationConfig.numberOfBins =
            numberOfBins;
        this.triggerViewRefresh();
    }

    onDomainMinChange(value: number) {
        this.currentlyConfiguredWidget.visualizationConfig.domainMin = value;
        this.triggerViewRefresh();
    }

    onDomainMaxChange(value: number) {
        this.currentlyConfiguredWidget.visualizationConfig.domainMax = value;
        this.triggerViewRefresh();
    }

    protected getWidgetType(): WidgetType {
        return WidgetType.DistributionChart;
    }

    protected initWidgetConfig(): DistributionChartVisConfig {
        return {
            forType: this.getWidgetType(),
            selectedProperty: this.fieldProvider.nonNumericFields[0],
            displayType: 'histogram',
            roundingValue: 0.1,
            resolution: 1,
            autoBin: true,
            numberOfBins: 10,
            domainMin: 0,
            domainMax: 100,
            autoDomain: true,
        };
    }
}
