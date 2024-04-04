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

import { Component } from '@angular/core';
import { BaseWidgetConfig } from '../../base/base-widget-config';
import {
    HistogramChartVisConfig,
    HistogramChartWidgetModel,
} from '../model/histogram-chart-widget.model';
import { DataExplorerField } from '@streampipes/platform-services';
import { config } from 'rxjs';

@Component({
    selector: 'sp-histogram-widget-config',
    templateUrl: './histogram-chart-widget-config.component.html',
})
export class SpHistogramChartWidgetConfigComponent extends BaseWidgetConfig<
    HistogramChartWidgetModel,
    HistogramChartVisConfig
> {
    setSelectedProperty(field: DataExplorerField) {
        this.currentlyConfiguredWidget.visualizationConfig.selectedProperty =
            field;
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

    protected applyWidgetConfig(config: HistogramChartVisConfig): void {
        config.selectedProperty = this.fieldService.getSelectedField(
            config.selectedProperty,
            this.fieldProvider.allFields,
            () => this.fieldProvider.allFields[0],
        );
        config.autoBin ??= true;
        config.numberOfBins ??= 10;
        config.domainMin ??= 0;
        config.domainMax ??= 100;
        config.autoDomain ??= true;
    }

    protected requiredFieldsForChartPresent(): boolean {
        return this.fieldProvider.allFields.length > 0;
    }
}
