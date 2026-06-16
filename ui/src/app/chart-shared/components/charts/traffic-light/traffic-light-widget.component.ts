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
import { BaseDataExplorerWidgetDirective } from '../base/base-data-explorer-widget.directive';
import { TrafficLightWidgetModel } from './model/traffic-light-widget.model';
import {
    DataExplorerField,
    SpQueryResult,
} from '@streampipes/platform-services';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { NgClass, NgStyle } from '@angular/common';
import { ClassDirective, StyleDirective } from '@ngbracket/ngx-layout/extended';
import { NoDataInDateRangeComponent } from '../base/no-data/no-data-in-date-range.component';

@Component({
    selector: 'sp-data-explorer-traffic-light-widget',
    templateUrl: './traffic-light-widget.component.html',
    styleUrls: ['./traffic-light-widget.component.scss'],
    imports: [
        FlexDirective,
        LayoutAlignDirective,
        LayoutDirective,
        NgStyle,
        StyleDirective,
        NoDataInDateRangeComponent,
        NgClass,
        ClassDirective,
    ],
})
export class TrafficLightWidgetComponent
    extends BaseDataExplorerWidgetDirective<TrafficLightWidgetModel>
    implements OnInit
{
    row: any[][];
    header: string[];
    fieldIndex: number;

    selectedWarningRange: number;
    selectedFieldToObserve: DataExplorerField;
    selectedUpperLimit: boolean;
    selectedThreshold: number;
    selectedToShowValue: boolean;

    activeClass = 'red';
    displayed_value: string;
    currentValue?: number;
    widgetWidth = 0;
    widgetHeight = 0;
    trafficLightShellWidth = 88;
    trafficLightValueFontSize = 28;

    ngOnInit(): void {
        super.ngOnInit();
        this.widgetWidth = this.initialSize.width;
        this.widgetHeight = this.initialSize.height;
        this.updateSettings();
        this.updateTrafficLightMetrics();
    }

    updateSettings(): void {
        this.selectedFieldToObserve =
            this.dataExplorerWidget.visualizationConfig.selectedFieldToObserve;
        this.selectedWarningRange =
            this.dataExplorerWidget.visualizationConfig.selectedWarningRange;
        this.selectedUpperLimit =
            this.dataExplorerWidget.visualizationConfig.selectedUpperLimit;
        this.selectedThreshold =
            this.dataExplorerWidget.visualizationConfig.selectedThreshold;
        this.selectedToShowValue =
            this.dataExplorerWidget.visualizationConfig.selectedToShowValue;
    }

    getTrafficLightColor(): void {
        const value = this.row[0][this.fieldIndex];
        this.currentValue = value;

        if (this.isInOkRange(value)) {
            this.activeClass = 'green';
        } else if (this.isInWarningRange(value)) {
            this.activeClass = 'yellow';
        } else {
            this.activeClass = 'red';
        }

        this.updateTrafficLightMetrics();
    }

    exceedsThreshold(value) {
        if (this.selectedUpperLimit) {
            return value >= this.selectedThreshold;
        } else {
            return value <= this.selectedThreshold;
        }
    }

    isInWarningRange(value) {
        if (this.exceedsThreshold(value)) {
            return false;
        } else {
            if (this.selectedUpperLimit) {
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

    refreshView(): void {
        this.updateSettings();
        this.fieldToObserve();
        this.getTrafficLightColor();
    }

    beforeDataFetched(): void {
        this.setShownComponents(false, false, true, false);
    }

    fieldToObserve(): void {
        this.fieldIndex = this.header.indexOf(
            this.selectedFieldToObserve.runtimeName,
        );
    }

    onDataReceived(spQueryResult: SpQueryResult[]): void {
        if (
            spQueryResult.length > 0 &&
            spQueryResult[0].allDataSeries.length > 0
        ) {
            this.header = spQueryResult[0].allDataSeries[0].headers;
            this.row = spQueryResult[0].allDataSeries[0].rows;
            this.fieldToObserve();
            this.getTrafficLightColor();
            this.setShownComponents(false, true, false, false);
        } else {
            this.setShownComponents(true, false, false, false);
        }
    }

    onResize(width: number, height: number) {
        this.widgetWidth = width;
        this.widgetHeight = height;
        this.updateTrafficLightMetrics();
    }

    handleUpdatedFields(
        addedFields: DataExplorerField[],
        removedFields: DataExplorerField[],
    ) {
        const updatedFields = this.fieldUpdateService.updateFieldSelection(
            [
                this.dataExplorerWidget.visualizationConfig
                    .selectedFieldToObserve,
            ],
            {
                addedFields,
                removedFields,
                fieldProvider: this.fieldProvider,
            },
            () => true,
        );

        this.selectedFieldToObserve = updatedFields[0];
        this.fieldToObserve();
        this.refreshView();
    }

    private updateTrafficLightMetrics(): void {
        const availableWidth = this.widgetWidth > 0 ? this.widgetWidth : 280;
        const availableHeight = this.widgetHeight > 0 ? this.widgetHeight : 320;
        const reservedValueHeight = this.selectedToShowValue
            ? Math.max(36, availableHeight * 0.12)
            : 0;
        const usableHeight = Math.max(
            availableHeight - reservedValueHeight,
            120,
        );
        const maxWidthByHeight = usableHeight / 3.25;
        const preferredWidth = availableWidth * 0.4;
        const maxShellWidth = Math.min(
            Math.max(availableWidth * 0.55, 140),
            220,
        );
        let shellWidth = this.clamp(
            Math.min(preferredWidth, maxWidthByHeight),
            72,
            maxShellWidth,
        );

        const valueText = this.formatDisplayedValue(this.currentValue);
        const desiredFontSize = this.clamp(
            Math.round(shellWidth * 0.34),
            18,
            42,
        );
        const estimatedRequiredWidth =
            valueText.length * desiredFontSize * 0.58 + 2 * 12;

        shellWidth = Math.max(
            shellWidth,
            Math.min(estimatedRequiredWidth, maxShellWidth),
        );
        shellWidth = Math.min(
            shellWidth,
            availableWidth * 0.82,
            maxWidthByHeight,
            maxShellWidth,
        );

        this.trafficLightShellWidth = Math.round(shellWidth);
        this.displayed_value = this.formatDisplayedValue(this.currentValue);
        this.trafficLightValueFontSize = this.computeValueFontSize(
            this.trafficLightShellWidth,
            this.displayed_value,
        );
    }

    private computeValueFontSize(width: number, value: string): number {
        if (!value) {
            return 28;
        }

        return this.clamp(
            Math.floor((width - 24) / Math.max(value.length * 0.58, 1)),
            12,
            42,
        );
    }

    private formatDisplayedValue(value: number | undefined): string {
        if (value === undefined || value === null || Number.isNaN(value)) {
            return '';
        }

        return value.toFixed(2);
    }

    private clamp(value: number, min: number, max: number): number {
        return Math.max(min, Math.min(max, value));
    }
}
