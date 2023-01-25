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
import { DistributionChartWidgetModel } from './model/distribution-chart-widget.model';
import {
    DataExplorerField,
    SpQueryResult,
} from '@streampipes/platform-services';

@Component({
    selector: 'sp-data-explorer-distribution-chart-widget',
    templateUrl: './distribution-chart-widget.component.html',
    styleUrls: ['./distribution-chart-widget.component.scss'],
})
export class DistributionChartWidgetComponent
    extends BaseDataExplorerWidgetDirective<DistributionChartWidgetModel>
    implements OnInit
{
    data = [];
    latestData: SpQueryResult[];

    rowNo = 2;
    colNo = 2;
    fixedColNo = 2;

    currentWidth: number;
    currentHeight: number;

    graph = {
        layout: {
            grid: {
                rows: this.rowNo,
                columns: this.colNo,
            },
            font: {
                color: '#FFF',
            },
            autosize: true,
            plot_bgcolor: '#fff',
            paper_bgcolor: '#fff',
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

    transform(rows, index: number): any[] {
        return rows.map(row => row[index]);
    }

    prepareData(spQueryResult: SpQueryResult[]) {
        this.data = [];

        const len = spQueryResult[0].allDataSeries.length;

        const even = len % this.colNo === 0;

        this.rowNo = even ? len / this.fixedColNo : (len + 1) / this.fixedColNo;

        this.colNo = len === 1 ? 1 : this.fixedColNo;

        let rowCount = 0;
        let colCount = 0;

        spQueryResult[0].allDataSeries.map((group, gindex) => {
            const series = group;
            const finalLabels: string[] = [];
            const finalValues: number[] = [];
            const values: Map<string, number> = new Map();
            const field =
                this.dataExplorerWidget.visualizationConfig.selectedProperty;
            const index = this.getColumnIndex(field, spQueryResult[0]);
            const histoValues: number[] = [];

            let groupName;

            if (group['tags'] != null) {
                Object.entries(group['tags']).forEach(([key, val]) => {
                    groupName = val;
                });
            }

            groupName = groupName === undefined ? field.fullDbName : groupName;

            if (series.total > 0) {
                const colValues = this.transform(series.rows, index);
                colValues.forEach(value => {
                    histoValues.push(value);

                    if (field.fieldCharacteristics.numeric) {
                        const roundingValue =
                            this.dataExplorerWidget.visualizationConfig
                                .roundingValue;
                        value =
                            Math.round(value / roundingValue) * roundingValue;
                    }

                    if (!values.has(value)) {
                        values.set(value, 0);
                    }
                    const currentVal = values.get(value);
                    values.set(value, currentVal + 1);
                });
            }
            values.forEach((value, key) => {
                finalLabels.push(key);
                finalValues.push(value);
            });

            let component;

            if (
                this.dataExplorerWidget.visualizationConfig.displayType ===
                'pie'
            ) {
                component = {
                    name: groupName,
                    values: finalValues,
                    labels: finalLabels,
                    type: 'pie',
                    domain: {
                        row: rowCount,
                        column: colCount,
                    },
                };

                if (colCount === this.colNo - 1) {
                    colCount = 0;
                    rowCount += 1;
                } else {
                    colCount += 1;
                }
            } else {
                component = {
                    x: histoValues,
                    type: 'histogram',
                };
            }
            this.data.push(component);
        });
    }

    existsLabel(labels: string[], value: string) {
        return labels.indexOf(value) > -1;
    }

    updateAppearance() {
        this.graph.layout.paper_bgcolor =
            this.dataExplorerWidget.baseAppearanceConfig.backgroundColor;
        this.graph.layout.plot_bgcolor =
            this.dataExplorerWidget.baseAppearanceConfig.backgroundColor;
        this.graph.layout.font.color =
            this.dataExplorerWidget.baseAppearanceConfig.textColor;
        this.graph.layout.grid = {
            rows: this.rowNo,
            columns: this.colNo,
        };
    }

    onResize(width: number, height: number) {
        this.currentWidth = width;
        this.currentHeight = height;
        this.graph.layout.autosize = false;
        (this.graph.layout as any).width = width;
        (this.graph.layout as any).height = height;
    }

    beforeDataFetched() {}

    onDataReceived(spQueryResult: SpQueryResult[]) {
        this.prepareData(spQueryResult);
        this.latestData = spQueryResult;
        this.setShownComponents(false, true, false, false);
    }

    handleUpdatedFields(
        addedFields: DataExplorerField[],
        removedFields: DataExplorerField[],
    ) {
        this.dataExplorerWidget.visualizationConfig.selectedProperty =
            this.triggerFieldUpdate(
                this.dataExplorerWidget.visualizationConfig.selectedProperty,
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
