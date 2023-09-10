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
import { TimeSeriesChartWidgetModel } from './model/time-series-chart-widget.model';
import {
    DataExplorerField,
    SpQueryResult,
} from '@streampipes/platform-services';
import { ColorUtils } from '../utils/color-utils';

@Component({
    selector: 'sp-data-explorer-time-series-chart-widget',
    templateUrl: './time-series-chart-widget.component.html',
    styleUrls: ['./time-series-chart-widget.component.scss'],
})
export class TimeSeriesChartWidgetComponent
    extends BaseDataExplorerWidgetDirective<TimeSeriesChartWidgetModel>
    implements OnInit
{
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

    groupKeeper = {};

    data: any[] = undefined;

    // this can be set to scale the line chart according to the layout
    offsetRightLineChart = 10;

    orderedSelectedProperties = [];

    maxValue = -10000000;

    updatemenus = [];
    graph = {
        layout: {
            font: {
                color: '#FFF',
                family: 'Roboto',
            },
            autosize: true,
            plot_bgcolor: '#fff',
            paper_bgcolor: '#fff',
            yaxis: {
                fixedrange: true,
                automargin: true,
            },
            margin: {
                t: 35,
                b: 35,
            },
            updatemenus: this.updatemenus,

            hovermode: 'x',
            hoverlabel: {
                namelength: 200,
            },
            showlegend: true,
            shapes: [],
            selectdirection: 'h',
            dragmode: 'zoom',
        },
        config: {
            modeBarButtonsToRemove: [
                'lasso2d',
                'select2d',
                'toggleSpikelines',
                'toImage',
            ],
            displaylogo: false,
        },
    };

    revision = 1;

    ngOnInit(): void {
        this.updatemenus = [
            {
                buttons: [
                    {
                        args: ['mode', 'lines'],
                        label: 'Line',
                        method: 'restyle',
                    },
                    {
                        args: ['mode', 'markers'],
                        label: 'Dots',
                        method: 'restyle',
                    },

                    {
                        args: ['mode', 'lines+markers'],
                        label: 'Dots + Lines',
                        method: 'restyle',
                    },
                ],
                direction: 'left',
                pad: { r: 10, t: 10 },
                showactive: true,
                type: 'buttons',
                x: 0.0,
                xanchor: 'left',
                y: 1.3,
                yanchor: 'top',
                font: {
                    color: this.dataExplorerWidget.baseAppearanceConfig
                        .textColor,
                },
                plot_bgcolor:
                    this.dataExplorerWidget.baseAppearanceConfig
                        .backgroundColor,
                paper_bgcolor:
                    this.dataExplorerWidget.baseAppearanceConfig
                        .backgroundColor,
                bordercolor: '#000',
            },
        ];

        super.ngOnInit();
    }

    transformData(data: SpQueryResult, sourceIndex: number): any[] {
        const indexXkey = 0;

        const tmpLineChartTraces: any[] = [];

        let maxValue = -Infinity;

        const selectedFields =
            this.dataExplorerWidget.visualizationConfig
                .selectedTimeSeriesChartProperties;
        data.allDataSeries.forEach(group => {
            group.rows.forEach(row => {
                selectedFields.forEach(field => {
                    if (field.sourceIndex === data.sourceIndex) {
                        const columnIndex = this.getColumnIndex(field, data);
                        const value = row[columnIndex];
                        maxValue = value > maxValue ? value : maxValue;
                    }
                });
            });
        });

        data.allDataSeries.forEach((group, index) => {
            for (const row of group.rows) {
                for (const field of selectedFields) {
                    if (field.sourceIndex === data.sourceIndex) {
                        const columnIndex = this.getColumnIndex(field, data);
                        const name = field.fullDbName + sourceIndex.toString();
                        let value = row[columnIndex];
                        const fullDbNameAndIndex =
                            field.fullDbName + field.sourceIndex.toString();
                        if (
                            !this.orderedSelectedProperties.includes(
                                fullDbNameAndIndex,
                            )
                        ) {
                            this.orderedSelectedProperties.push(
                                fullDbNameAndIndex,
                            );
                        }

                        const { tags } = group;
                        if (tags != null) {
                            const groupValues =
                                this.groupKeeper[name] ?? new Set();
                            for (const [key, val] of Object.entries(tags)) {
                                groupValues.add(val);
                            }
                            this.groupKeeper[name] = groupValues;
                        }

                        if (
                            this.fieldProvider.booleanFields.find(
                                f =>
                                    field.fullDbName === f.fullDbName &&
                                    f.sourceIndex === data.sourceIndex,
                            ) !== undefined
                        ) {
                            value = value === true ? this.maxValue + 2 : 0;
                        }

                        const traceKey = name + index.toString();
                        const traceExists = traceKey in tmpLineChartTraces;

                        if (!traceExists) {
                            const headerName = data.headers[columnIndex];
                            tmpLineChartTraces[traceKey] = {
                                type: 'scatter',
                                mode: 'Line',
                                name: headerName,
                                connectgaps: false,
                                x: [],
                                y: [],
                            };
                        }

                        tmpLineChartTraces[traceKey].x.push(
                            new Date(row[indexXkey]),
                        );
                        tmpLineChartTraces[traceKey].y.push(value);
                    }
                }
            }
        });

        return Object.values(tmpLineChartTraces);
    }

    updateAppearance() {
        this.graph.layout.paper_bgcolor =
            this.dataExplorerWidget.baseAppearanceConfig.backgroundColor;
        this.graph.layout.plot_bgcolor =
            this.dataExplorerWidget.baseAppearanceConfig.backgroundColor;
        this.graph.layout.font.color =
            this.dataExplorerWidget.baseAppearanceConfig.textColor;

        if (this.dataExplorerWidget.visualizationConfig.showSpike) {
            this.graph.layout['xaxis'] = {
                type: 'date',
                showspikes: true,
                spikemode: 'across+toaxis',
                spikesnap: 'cursor',
                showline: true,
                showgrid: true,
                spikedash: 'dash',
                spikecolor: '#666666',
                spikethickness: 2,
                automargin: true,
            };
            this.graph.layout.hovermode = 'x';
        } else {
            this.graph.layout['xaxis'] = {
                type: 'date',
                automargin: true,
            };
            this.graph.layout.hovermode = '';
        }

        const colorKeeper = {};
        const dashTypeKeeper = {};
        const lineVisualizationOptions = ['solid', 'dash', 'dot', 'dashdot'];
        const barVisualizationOptions = ['', '+', '/', '.'];
        const symbolVisualizationOptions = [
            'diamond',
            'star-triangle-up',
            'pentagon',
            'star-diamond',
            'x',
        ];
        const scatterVisualizationOptions = [
            '',
            'diamond',
            'star-triangle-up',
            'pentagon',
            'star-diamond',
            'x',
        ];

        let pastGroups = 0;
        let index = 0;

        const collectNames = [];

        if (this.data) {
            this.orderedSelectedProperties.map((name, findex) => {
                collectNames.push(name);

                let localGroups = [];
                if (name in this.groupKeeper) {
                    localGroups = this.groupKeeper[name];
                }

                let repeat = 1;
                if (localGroups.length > 0) {
                    repeat = localGroups.length;
                }

                for (let it = 0; it < repeat; it++) {
                    index = pastGroups;

                    if (this.data[index] !== undefined) {
                        this.data[index]['marker'] = { color: '' };

                        if (
                            !(
                                name in
                                this.dataExplorerWidget.visualizationConfig
                                    .chosenColor
                            )
                        ) {
                            this.dataExplorerWidget.visualizationConfig.chosenColor[
                                name
                            ] = this.presetColors[index];
                        }

                        if (
                            !(
                                name in
                                this.dataExplorerWidget.visualizationConfig
                                    .displayName
                            )
                        ) {
                            this.dataExplorerWidget.visualizationConfig.displayName[
                                name
                            ] = name;
                        }

                        if (
                            !(
                                name in
                                this.dataExplorerWidget.visualizationConfig
                                    .displayType
                            )
                        ) {
                            this.dataExplorerWidget.visualizationConfig.displayType[
                                name
                            ] = 'lines';
                        }

                        if (
                            !(
                                name in
                                this.dataExplorerWidget.visualizationConfig
                                    .chosenAxis
                            )
                        ) {
                            this.dataExplorerWidget.visualizationConfig.chosenAxis[
                                name
                            ] = 'left';
                        } else {
                        }

                        let color =
                            this.dataExplorerWidget.visualizationConfig
                                .chosenColor[name];
                        const setType =
                            this.dataExplorerWidget.visualizationConfig
                                .displayType[name];

                        let visualizationOptions;
                        if (setType === 'bar') {
                            visualizationOptions = barVisualizationOptions;
                        }
                        if (
                            setType === 'lines' ||
                            setType === 'lines+markers'
                        ) {
                            visualizationOptions = lineVisualizationOptions;
                        }
                        if (setType === 'symbol_markers') {
                            visualizationOptions = symbolVisualizationOptions;
                        }
                        if (setType === 'normal_markers') {
                            visualizationOptions = scatterVisualizationOptions;
                        }

                        let dashType;

                        if (name in colorKeeper) {
                            dashType = dashTypeKeeper[name];
                            const visualizationTypePosition =
                                visualizationOptions.indexOf(dashType);
                            if (
                                visualizationTypePosition ===
                                visualizationOptions.length - 1
                            ) {
                                dashType = visualizationOptions[0];
                                dashTypeKeeper[name] = dashType;
                                color = ColorUtils.lightenColor(
                                    colorKeeper[name],
                                    11.0,
                                );
                                colorKeeper[name] = color;
                            } else {
                                dashType =
                                    visualizationOptions[
                                        visualizationTypePosition + 1
                                    ];
                                dashTypeKeeper[name] = dashType;
                                color = colorKeeper[name];
                            }
                        } else {
                            dashType = visualizationOptions[0];
                            dashTypeKeeper[name] = dashType;
                            colorKeeper[name] = color;
                        }

                        let displayName =
                            this.dataExplorerWidget.visualizationConfig
                                .displayName[name];
                        if (localGroups.length > 0) {
                            const tag = localGroups[it];
                            displayName = displayName + ' ' + tag;
                        }

                        this.data[index].marker.color = color;

                        if (setType === 'bar') {
                            this.data[index].marker['pattern'] = {
                                shape: dashType,
                                fillmode: 'overlay',
                                fgcolor: '#ffffff',
                                // 'size' : 3,
                            };
                        } else {
                            if (
                                setType === 'lines' ||
                                setType === 'lines+markers'
                            ) {
                                this.data[index]['line'] = {
                                    dash: dashType,
                                    width: 3,
                                };
                            }
                        }

                        this.data[index].name = displayName;

                        let displayType = 'scatter';
                        let displayMode = 'lines';

                        if (setType === 'bar') {
                            displayType = 'bar';
                        }
                        if (setType === 'lines') {
                            displayMode = 'lines';
                        }
                        if (setType === 'lines+markers') {
                            displayMode = 'lines+markers';
                        }
                        if (setType === 'normal_markers') {
                            displayMode = 'markers';
                            this.data[index].marker['symbol'] = dashType;
                            this.data[index].marker['size'] = 5;
                        }
                        if (setType === 'symbol_markers') {
                            displayMode = 'markers';
                            this.data[index].marker['symbol'] = dashType;
                            this.data[index].marker['size'] = 10;
                        }

                        this.data[index].type = displayType;
                        this.data[index].mode = displayMode;

                        const setAxis =
                            this.dataExplorerWidget.visualizationConfig
                                .chosenAxis[name] === 'left'
                                ? 'y1'
                                : 'y2';
                        this.data[index]['yaxis'] = setAxis;

                        if (setAxis === 'y2') {
                            this.graph.layout['yaxis2'] = {
                                title: '',
                                overlaying: 'y',
                                side: 'right',
                            };
                        }

                        pastGroups += 1;
                    }
                }
            });
        }
    }

    refreshView() {
        this.updateAppearance();
    }

    onResize(width: number, height: number) {
        setTimeout(() => {
            this.graph.layout.autosize = false;
            (this.graph.layout as any).width =
                width - this.offsetRightLineChart;
            (this.graph.layout as any).height = height;
            this.revision += 1;
        }, 10);
    }

    beforeDataFetched() {
        this.graph.layout.shapes = [];
        this.setShownComponents(false, false, true, false);
    }

    onDataReceived(spQueryResults: SpQueryResult[]) {
        this.data = [];
        this.groupKeeper = {};

        this.orderedSelectedProperties = [];

        this.data = spQueryResults.flatMap(spQueryResult =>
            this.transformData(spQueryResult, spQueryResult.sourceIndex),
        );

        this.setShownComponents(false, true, false, false);
        this.revision += 1;
    }

    handleUpdatedFields(
        addedFields: DataExplorerField[],
        removedFields: DataExplorerField[],
    ) {
        this.dataExplorerWidget.visualizationConfig.selectedTimeSeriesChartProperties =
            this.updateFieldSelection(
                this.dataExplorerWidget.visualizationConfig
                    .selectedTimeSeriesChartProperties,
                addedFields,
                removedFields,
                field => field.fieldCharacteristics.numeric,
            );
    }
}
