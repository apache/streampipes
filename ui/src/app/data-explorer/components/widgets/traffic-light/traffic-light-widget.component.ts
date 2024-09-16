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

@Component({
    selector: 'sp-data-explorer-traffic-light-widget',
    templateUrl: './traffic-light-widget.component.html',
    styleUrls: ['./traffic-light-widget.component.scss'],
})
export class TrafficLightWidgetComponent
    extends BaseDataExplorerWidgetDirective<TrafficLightWidgetModel>
    implements OnInit
{
    row: any[][];
    header: string[];
    fieldIndex: number;

    width: number;
    height: number;

    containerWidth: number;
    containerHeight: number;

    lightWidth: number;
    lightHeight: number;

    selectedWarningRange: number;
    selectedFieldToObserve: DataExplorerField;
    selectedUpperLimit: boolean;
    selectedThreshold: number;
    selectedToShowValue: boolean;

    activeClass = 'red';
    displayed_value: string;

    ngOnInit(): void {
        super.ngOnInit();
        this.onResize(
            this.gridsterItemComponent.width - this.widthOffset,
            this.gridsterItemComponent.height - this.heightOffset,
        );
        this.updateSettings();
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
        this.displayed_value = value.toFixed(2);

        if (this.isInOkRange(value)) {
            this.activeClass = 'green';
        } else if (this.isInWarningRange(value)) {
            this.activeClass = 'yellow';
        } else {
            this.activeClass = 'red';
        }
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

    onResize(width: number, heigth: number) {
        this.containerHeight = heigth * 0.8;
        this.containerWidth = this.containerHeight / 3;
        this.lightWidth = (this.containerHeight * 0.9) / 3;
        this.lightHeight = this.lightWidth;
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
}
