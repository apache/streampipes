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
import { WidgetConfigurationService } from '../../../../services/widget-configuration.service';
import {
    TrafficLightVisConfig,
    TrafficLightWidgetModel,
} from '../model/traffic-light-widget.model';
import { DataExplorerFieldProviderService } from '../../../../services/data-explorer-field-provider-service';
import { DataExplorerField } from '@streampipes/platform-services';

@Component({
    selector: 'sp-data-explorer-traffic-light-widget-config',
    templateUrl: './traffic-light-widget-config.component.html',
    styleUrls: ['./traffic-light-widget-config.component.scss'],
})
export class TrafficLightWidgetConfigComponent extends BaseWidgetConfig<
    TrafficLightWidgetModel,
    TrafficLightVisConfig
> {
    constructor(
        widgetConfigurationService: WidgetConfigurationService,
        fieldService: DataExplorerFieldProviderService,
    ) {
        super(widgetConfigurationService, fieldService);
    }
    warningRangeInterval: string;

    selectWarningRange(selectedWarningRange: string): void {
        const formattedValue = selectedWarningRange?.replace(',', '.');
        const numericValue = parseFloat(formattedValue);
        if (!isNaN(numericValue)) {
            this.currentlyConfiguredWidget.visualizationConfig.selectedWarningRange =
                numericValue;
        } else {
            this.currentlyConfiguredWidget.visualizationConfig.selectedWarningRange =
                null;
        }
        this.updateWarningRangeInterval();
        this.triggerViewRefresh();
    }

    selectFieldToObserve(selectedFieldToObserve: DataExplorerField): void {
        this.currentlyConfiguredWidget.visualizationConfig.selectedFieldToObserve =
            selectedFieldToObserve;
        this.triggerViewRefresh();
    }

    selectUpperLimit(selectedUpperLimit: boolean): void {
        this.currentlyConfiguredWidget.visualizationConfig.selectedUpperLimit =
            selectedUpperLimit;
        this.triggerViewRefresh();
    }

    selectToShowValue(selectedToShowValue: boolean): void {
        this.currentlyConfiguredWidget.visualizationConfig.selectedToShowValue =
            selectedToShowValue;
        this.triggerViewRefresh();
    }

    selectThreshold(selectedThreshold: string): void {
        const formattedValue = selectedThreshold?.replace(',', '.');
        const numericValue = parseFloat(formattedValue);
        if (!isNaN(numericValue)) {
            this.currentlyConfiguredWidget.visualizationConfig.selectedThreshold =
                numericValue;
        } else {
            this.currentlyConfiguredWidget.visualizationConfig.selectedThreshold =
                null;
        }
        this.updateWarningRangeInterval();
        this.triggerViewRefresh();
    }

    protected applyWidgetConfig(config: TrafficLightVisConfig): void {
        config.selectedFieldToObserve = this.fieldService.getSelectedField(
            config.selectedFieldToObserve,
            this.fieldProvider.allFields,
            () => this.fieldProvider.allFields[0],
        );
        this.currentlyConfiguredWidget.visualizationConfig.selectedUpperLimit ??=
            true;
        this.updateWarningRangeInterval();
    }
    protected requiredFieldsForChartPresent(): boolean {
        return true;
    }

    restrictInput(event: KeyboardEvent): void {
        const inputValue = (event.target as HTMLInputElement).value;
        const allowedKeys = [
            '0',
            '1',
            '2',
            '3',
            '4',
            '5',
            '6',
            '7',
            '8',
            '9',
            'Backspace',
            'ArrowLeft',
            'ArrowRight',
            'Delete',
        ];

        const pointOrComma = ['.', ','];
        if (pointOrComma.includes(event.key)) {
            if (inputValue.includes('.') || inputValue.includes(',')) {
                event.preventDefault();
                return;
            }
        }

        if (
            !allowedKeys.includes(event.key) &&
            !pointOrComma.includes(event.key) &&
            !(event.ctrlKey || event.metaKey)
        ) {
            event.preventDefault();
        }
    }

    updateWarningRangeInterval(): void {
        if (
            this.currentlyConfiguredWidget.visualizationConfig
                .selectedThreshold != null &&
            this.currentlyConfiguredWidget.visualizationConfig
                .selectedWarningRange != null
        ) {
            const rangeValue =
                this.currentlyConfiguredWidget.visualizationConfig
                    .selectedThreshold *
                (this.currentlyConfiguredWidget.visualizationConfig
                    .selectedWarningRange /
                    100);

            if (
                this.currentlyConfiguredWidget.visualizationConfig
                    .selectedWarningRange === 0
            ) {
                this.warningRangeInterval = 'No Warning Range defined';
            } else if (
                this.currentlyConfiguredWidget.visualizationConfig
                    .selectedUpperLimit
            ) {
                const lowerBound =
                    this.currentlyConfiguredWidget.visualizationConfig
                        .selectedThreshold - rangeValue;
                this.warningRangeInterval =
                    'Current Warning Range: ' +
                    `${lowerBound} to ${this.currentlyConfiguredWidget.visualizationConfig.selectedThreshold}`;
            } else {
                const upperBound =
                    this.currentlyConfiguredWidget.visualizationConfig
                        .selectedThreshold + rangeValue;
                this.warningRangeInterval =
                    'Current Warning Range: ' +
                    `${this.currentlyConfiguredWidget.visualizationConfig.selectedThreshold} to ${upperBound}`;
            }
        } else {
            this.warningRangeInterval = '';
        }
    }
}
