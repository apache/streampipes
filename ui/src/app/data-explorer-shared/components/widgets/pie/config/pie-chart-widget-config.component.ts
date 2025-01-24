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
    PieChartVisConfig,
    PieChartWidgetModel,
} from '../model/pie-chart-widget.model';
import { DataExplorerField } from '@streampipes/platform-services';
import { ColorMappingService } from '../../../../services/color-mapping.service';
import { WidgetConfigurationService } from '../../../../services/widget-configuration.service';
import { DataExplorerFieldProviderService } from '../../../../services/data-explorer-field-provider-service';

@Component({
    selector: 'sp-pie-chart-widget-config',
    templateUrl: './pie-chart-widget-config.component.html',
})
export class SpPieChartWidgetConfigComponent extends BaseWidgetConfig<
    PieChartWidgetModel,
    PieChartVisConfig
> {
    constructor(
        private colorMappingService: ColorMappingService,
        widgetConfigurationService: WidgetConfigurationService,
        fieldService: DataExplorerFieldProviderService,
    ) {
        super(widgetConfigurationService, fieldService);
    }

    setSelectedProperty(field: DataExplorerField) {
        this.currentlyConfiguredWidget.visualizationConfig.selectedProperty =
            field;
        this.triggerViewRefresh();
    }

    protected applyWidgetConfig(config: PieChartVisConfig): void {
        config.selectedProperty = this.fieldService.getSelectedField(
            config.selectedProperty,
            this.fieldProvider.allFields,
            () => this.fieldProvider.allFields[0],
        );
        config.roundingValue ??= 0.1;
        config.selectedRadius ??= 0;
        config.showCustomColorMapping ??= false;
        config.colorMappings ??= [];
    }

    updateRoundingValue(selectedType: number) {
        this.currentlyConfiguredWidget.visualizationConfig.roundingValue =
            selectedType;
        this.triggerViewRefresh();
    }

    updateInnerRadius(selectedRadius: number) {
        this.currentlyConfiguredWidget.visualizationConfig.selectedRadius =
            selectedRadius;
        this.triggerViewRefresh();
    }

    showCustomColorMapping(showCustomColorMapping: boolean) {
        this.currentlyConfiguredWidget.visualizationConfig.showCustomColorMapping =
            showCustomColorMapping;

        if (!showCustomColorMapping) {
            this.resetColorMappings();
        }

        this.triggerViewRefresh();
    }

    resetColorMappings(): void {
        this.currentlyConfiguredWidget.visualizationConfig.colorMappings = [];
        this.triggerViewRefresh();
    }

    addMapping() {
        this.colorMappingService.addMapping(
            this.currentlyConfiguredWidget.visualizationConfig.colorMappings,
        );
        this.triggerViewRefresh();
    }

    removeMapping(index: number) {
        this.currentlyConfiguredWidget.visualizationConfig.colorMappings =
            this.colorMappingService.removeMapping(
                this.currentlyConfiguredWidget.visualizationConfig
                    .colorMappings,
                index,
            );
        this.triggerViewRefresh();
    }

    updateColor(index: number, newColor: string) {
        this.colorMappingService.updateColor(
            this.currentlyConfiguredWidget.visualizationConfig.colorMappings,
            index,
            newColor,
        );
        this.triggerViewRefresh();
    }

    updateMapping() {
        this.triggerViewRefresh();
    }

    protected requiredFieldsForChartPresent(): boolean {
        return this.fieldProvider.allFields.length > 0;
    }
}
