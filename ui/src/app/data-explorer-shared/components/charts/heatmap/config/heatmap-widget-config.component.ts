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
import { ChartConfigurationService } from '../../../../services/chart-configuration.service';
import {
    HeatmapVisConfig,
    HeatmapWidgetModel,
} from '../model/heatmap-widget.model';
import { DataExplorerFieldProviderService } from '../../../../services/data-explorer-field-provider-service';
import { DataExplorerField } from '@streampipes/platform-services';

@Component({
    selector: 'sp-data-explorer-heatmap-widget-config',
    templateUrl: './heatmap-widget-config.component.html',
})
export class HeatmapWidgetConfigComponent extends BaseWidgetConfig<
    HeatmapWidgetModel,
    HeatmapVisConfig
> {
    constructor(
        widgetConfigurationService: ChartConfigurationService,
        fieldService: DataExplorerFieldProviderService,
    ) {
        super(widgetConfigurationService, fieldService);
    }

    setShowLabelsProperty(field: DataExplorerField) {
        this.currentlyConfiguredWidget.visualizationConfig.showLabelsProperty =
            field['checked'];
        this.triggerDataRefresh();
    }

    setSelectedHeatProperty(field: DataExplorerField) {
        this.currentlyConfiguredWidget.visualizationConfig.selectedHeatProperty =
            field;
        this.triggerDataRefresh();
    }

    updateVisualMapMin(min: number) {
        this.currentlyConfiguredWidget.visualizationConfig.visualMapMin = min;
        this.triggerDataRefresh();
    }

    updateVisualMapMax(max: number) {
        this.currentlyConfiguredWidget.visualizationConfig.visualMapMax = max;
        this.triggerDataRefresh();
    }

    protected applyWidgetConfig(config: HeatmapVisConfig): void {
        config.selectedHeatProperty = this.fieldService.getSelectedField(
            config.selectedHeatProperty,
            this.fieldProvider.numericFields,
            () => this.fieldProvider.numericFields[0],
        );
        config.showLabelsProperty ??= false;

        if (config.visualMapMax === undefined) {
            config.visualMapMax = 200;
        }

        if (config.visualMapMin === undefined) {
            config.visualMapMin = 0;
        }
    }

    protected requiredFieldsForChartPresent(): boolean {
        return this.fieldProvider.numericFields.length > 0;
    }
}
