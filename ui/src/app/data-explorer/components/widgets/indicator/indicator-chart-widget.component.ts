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
import { IndicatorChartWidgetModel } from './model/indicator-chart-widget.model';
import {
    DataExplorerField,
    SpQueryResult,
} from '@streampipes/platform-services';

@Component({
    selector: 'sp-data-explorer-indicator-chart-widget',
    templateUrl: './indicator-chart-widget.component.html',
})
export class IndicatorChartWidgetComponent
    extends BaseDataExplorerWidgetDirective<IndicatorChartWidgetModel>
    implements OnInit
{
    data = [
        {
            type: 'indicator',
            mode: 'number+delta',
            value: 400,
            number: { prefix: '' },
            delta: { position: 'top', reference: 320 },
            domain: { x: [0, 1], y: [0, 1] },
        },
    ];

    graph = {
        layout: {
            font: {
                color: '#FFF',
                family: 'Roboto',
            },
            autosize: true,
            plot_bgcolor: '#fff',
            paper_bgcolor: '#fff',
            margin: { t: 0, b: 0, l: 0, r: 0 },
            grid: { rows: 2, columns: 2, pattern: 'independent' },
            template: {
                data: {
                    indicator: [
                        {
                            mode: 'number+delta',
                            delta: { reference: 90 },
                        },
                    ],
                },
            },
        },

        config: {
            modeBarButtonsToRemove: [
                'lasso2d',
                'select2d',
                'toggleSpikelines',
                'toImage',
            ],
            displaylogo: false,
            displayModeBar: false,
            responsive: true,
        },
    };

    refreshView() {
        this.updateAppearance();
    }

    prepareData(numberResult: SpQueryResult[], deltaResult?: SpQueryResult) {
        const valueIndex = this.getColumnIndex(
            this.dataExplorerWidget.visualizationConfig.valueField,
            numberResult[0],
        );
        this.data[0].value =
            numberResult[0].total > 0
                ? numberResult[0].allDataSeries[0].rows[0][valueIndex]
                : '-';
        if (deltaResult) {
            const deltaIndex = this.getColumnIndex(
                this.dataExplorerWidget.visualizationConfig.deltaField,
                numberResult[0],
            );
            this.data[0].delta.reference =
                numberResult[0].total > 0
                    ? deltaResult.allDataSeries[0].rows[0][deltaIndex]
                    : '-';
        }
    }

    updateAppearance() {
        this.graph.layout.paper_bgcolor =
            this.dataExplorerWidget.baseAppearanceConfig.backgroundColor;
        this.graph.layout.plot_bgcolor =
            this.dataExplorerWidget.baseAppearanceConfig.backgroundColor;
        this.graph.layout.font.color =
            this.dataExplorerWidget.baseAppearanceConfig.textColor;
    }

    onResize(width: number, height: number) {
        this.graph.layout.autosize = false;
        (this.graph.layout as any).width = width;
        (this.graph.layout as any).height = height;
    }

    beforeDataFetched() {
        this.data[0].mode = this.dataExplorerWidget.visualizationConfig
            .showDelta
            ? 'number+delta'
            : 'number';
    }

    onDataReceived(spQueryResult: SpQueryResult[]) {
        this.prepareData(spQueryResult);
        this.setShownComponents(false, true, false, false);
    }

    handleUpdatedFields(
        addedFields: DataExplorerField[],
        removedFields: DataExplorerField[],
    ) {
        this.dataExplorerWidget.visualizationConfig.valueField =
            this.triggerFieldUpdate(
                this.dataExplorerWidget.visualizationConfig.valueField,
                addedFields,
                removedFields,
            );

        this.dataExplorerWidget.visualizationConfig.deltaField =
            this.triggerFieldUpdate(
                this.dataExplorerWidget.visualizationConfig.deltaField,
                addedFields,
                removedFields,
            );
    }

    triggerFieldUpdate(
        selected: DataExplorerField,
        addedFields: DataExplorerField[],
        removedFields: DataExplorerField[],
    ): DataExplorerField {
        return this.updateSingleField(
            selected,
            this.fieldProvider.numericFields,
            addedFields,
            removedFields,
            field => field.fieldCharacteristics.numeric,
        );
    }
}
