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
    TimeSeriesChartVisConfig,
    TimeSeriesChartWidgetModel,
} from '../model/time-series-chart-widget.model';
import { WidgetConfigurationService } from '../../../../services/widget-configuration.service';
import { DataExplorerField } from '@streampipes/platform-services';
import { DataExplorerFieldProviderService } from '../../../../services/data-explorer-field-provider-service';

@Component({
    selector: 'sp-data-explorer-time-series-chart-widget-config',
    templateUrl: './time-series-chart-widget-config.component.html',
})
export class TimeSeriesChartWidgetConfigComponent extends BaseWidgetConfig<
    TimeSeriesChartWidgetModel,
    TimeSeriesChartVisConfig
> {
    constructor(
        widgetConfigurationService: WidgetConfigurationService,
        fieldService: DataExplorerFieldProviderService,
    ) {
        super(widgetConfigurationService, fieldService);
    }

    presetColors: string[] = [
        '#39B54A',
        '#1B1464',
        '#f44336',
        '#FFEB3B',
        '#000000',
        '#433BFF',
        '#FF00E4',
        '#FD8B00',
        '#FD8B00',
        '#00FFD5',
        '#581845',
        '#767676',
        '#4300BF',
        '#6699D4',
        '#D466A1',
    ];

    setSelectedProperties(selectedColumns: DataExplorerField[]) {
        this.currentlyConfiguredWidget.visualizationConfig.selectedTimeSeriesChartProperties =
            selectedColumns;

        const numericPlusBooleanFields =
            this.fieldProvider.numericFields.concat(
                this.fieldProvider.booleanFields,
            );

        const currentColors =
            this.currentlyConfiguredWidget.visualizationConfig.chosenColor;
        const currentNames =
            this.currentlyConfiguredWidget.visualizationConfig.displayName;
        const currentTypes =
            this.currentlyConfiguredWidget.visualizationConfig.displayType;
        const currentAxis =
            this.currentlyConfiguredWidget.visualizationConfig.chosenAxis;

        const lenBefore = Object.keys(currentAxis).length;

        numericPlusBooleanFields.map((field, index) => {
            const name = field.fullDbName + field.sourceIndex;
            if (!(name in currentColors)) {
                currentColors[name] = this.presetColors[lenBefore + index];
                currentNames[name] = field.fullDbName;
                currentTypes[name] = 'lines';
                currentAxis[name] = 'left';
            }
        });

        this.currentlyConfiguredWidget.visualizationConfig.chosenColor =
            currentColors;
        this.currentlyConfiguredWidget.visualizationConfig.displayName =
            currentNames;
        this.currentlyConfiguredWidget.visualizationConfig.displayType =
            currentTypes;
        this.currentlyConfiguredWidget.visualizationConfig.chosenAxis =
            currentAxis;

        this.triggerViewRefresh();
    }

    setShowSpikeProperty(field: DataExplorerField) {
        this.currentlyConfiguredWidget.visualizationConfig.showSpike =
            field['checked'];
        this.triggerViewRefresh();
    }

    protected applyWidgetConfig(config: TimeSeriesChartVisConfig): void {
        const numericPlusBooleanFields =
            this.fieldProvider.numericFields.concat(
                this.fieldProvider.booleanFields,
            );

        config.chosenColor = this.getConfigOrDefault(
            config.chosenColor,
            numericPlusBooleanFields,
            (field, index) => this.presetColors[index],
        );
        config.displayName = this.getConfigOrDefault(
            config.displayName,
            numericPlusBooleanFields,
            field => field.fullDbName,
        );
        config.displayType = this.getConfigOrDefault(
            config.displayType,
            numericPlusBooleanFields,
            () => 'lines',
        );
        config.chosenAxis = this.getConfigOrDefault(
            config.chosenAxis,
            numericPlusBooleanFields,
            () => 'left',
        );

        config.yKeys = [];
        config.selectedTimeSeriesChartProperties =
            this.fieldService.getSelectedFields(
                config.selectedTimeSeriesChartProperties,
                numericPlusBooleanFields,
                () => {
                    return numericPlusBooleanFields.length > 6
                        ? numericPlusBooleanFields.slice(0, 5)
                        : numericPlusBooleanFields;
                },
            );
        config.showSpike ??= true;
    }

    private getConfigOrDefault(
        config: Record<string, any>,
        availableFields: DataExplorerField[],
        getDefaultValue: (field: DataExplorerField, index: number) => string,
    ) {
        const fieldKeys = availableFields.map(
            f => f.fullDbName + f.sourceIndex,
        );
        if (!config) {
            config = {};
        } else {
            Object.keys(config).forEach(key => {
                if (!fieldKeys.includes(key)) {
                    delete config[key];
                }
            });
        }
        availableFields.forEach((field, index) => {
            const fieldName = field.fullDbName + field.sourceIndex;
            if (!config[fieldName]) {
                config[fieldName] = getDefaultValue(field, index);
            }
        });
        return config;
    }

    protected requiredFieldsForChartPresent(): boolean {
        return this.fieldProvider.numericFields.length > 0;
    }
}
