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
import { StatusWidgetModel } from './model/status-widget.model';
import {
    DataExplorerField,
    SpQueryResult,
} from '@streampipes/platform-services';

@Component({
    selector: 'sp-data-explorer-status-widget',
    templateUrl: './status-widget.component.html',
    styleUrls: ['./status-widget.component.scss'],
})
export class StatusWidgetComponent
    extends BaseDataExplorerWidgetDirective<StatusWidgetModel>
    implements OnInit
{
    width: number;
    height: number;

    row: any[][];
    header: string[];
    fieldIndex = -1;

    containerWidth: number;
    containerHeight: number;

    lightWidth: number;
    lightHeight: number;

    selectedDataType: string;
    selectedInterval: number;
    showLastSeen: boolean;
    selectedBooleanFieldToObserve: DataExplorerField;
    selectedMappingGreenTrue: boolean;

    lastTimestamp = 0;
    active: boolean;

    ngOnInit(): void {
        super.ngOnInit();
        this.onResize(
            this.gridsterItemComponent.width - this.widthOffset,
            this.gridsterItemComponent.height - this.heightOffset,
        );
        this.updateSettings();
    }

    updateSettings(): void {
        this.selectedDataType =
            this.dataExplorerWidget.visualizationConfig.selectedDataType;
        this.selectedInterval =
            this.dataExplorerWidget.visualizationConfig.selectedInterval;
        this.showLastSeen =
            this.dataExplorerWidget.visualizationConfig.showLastSeen;
        this.selectedBooleanFieldToObserve =
            this.dataExplorerWidget.visualizationConfig.selectedBooleanFieldToObserve;
        this.selectedMappingGreenTrue =
            this.dataExplorerWidget.visualizationConfig.selectedMappingGreenTrue;
    }

    getNumericalStatus(): void {
        const timestamp = new Date().getTime();
        this.active =
            this.lastTimestamp >= timestamp - this.selectedInterval * 1000;
    }

    getBooleanStatus(): void {
        if (this.selectedMappingGreenTrue) {
            this.active = this.row[0][this.fieldIndex];
        } else {
            this.active = !this.row[0][this.fieldIndex];
        }
    }

    booleanFieldToObserve(): void {
        this.fieldIndex = this.header.indexOf(
            this.selectedBooleanFieldToObserve.runtimeName,
        );
    }

    refreshView(): void {
        this.updateSettings();
        if (this.row !== undefined && this.row.length > 0) {
            if (this.selectedDataType == 'boolean') {
                this.getBooleanStatus();
            } else {
                this.getNumericalStatus();
            }
        }
    }

    beforeDataFetched(): void {
        this.setShownComponents(false, false, true, false);
    }

    onDataReceived(spQueryResult: SpQueryResult[]): void {
        if (
            spQueryResult.length > 0 &&
            spQueryResult[0].allDataSeries.length > 0
        ) {
            this.header = spQueryResult[0].allDataSeries[0].headers;
            this.row = spQueryResult[0].allDataSeries[0].rows;
            this.lastTimestamp = spQueryResult[0].allDataSeries[0].rows[0][0];

            if (this.selectedDataType == 'number') {
                this.getNumericalStatus();
            } else if (this.selectedDataType == 'boolean') {
                this.booleanFieldToObserve();
                this.getBooleanStatus();
            }
            this.setShownComponents(false, true, false, false);
        } else {
            this.setShownComponents(true, false, false, false);
        }
    }

    onResize(width: number, heigth: number): void {
        this.containerHeight = heigth * 0.3;
        this.containerWidth = this.containerHeight;
        this.lightWidth = this.containerHeight;
        this.lightHeight = this.lightWidth;
    }

    handleUpdatedFields(
        addedFields: DataExplorerField[],
        removedFields: DataExplorerField[],
    ) {
        const updatedFields = this.fieldUpdateService.updateFieldSelection(
            [
                this.dataExplorerWidget.visualizationConfig
                    .selectedBooleanFieldToObserve,
            ],
            {
                addedFields,
                removedFields,
                fieldProvider: this.fieldProvider,
            },
            () => true,
        );

        this.selectedBooleanFieldToObserve = updatedFields[0];
        this.booleanFieldToObserve();
        this.refreshView();
    }
}
