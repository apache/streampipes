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

import { Component, EventEmitter, OnChanges, Output, SimpleChanges } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { PlotlyService } from 'angular-plotly.js';
import { DataResult } from '../../core-model/datalake/DataResult';
import { GroupedDataResult } from '../../core-model/datalake/GroupedDataResult';
import { BaseChartComponent } from '../chart/baseChart.component';
import { ChangeChartmodeDialog } from './labeling-tool/dialogs/change-chartmode/change-chartmode.dialog';
import { LabelingDialog } from './labeling-tool/dialogs/labeling/labeling.dialog';
import { ColorService } from './labeling-tool/services/color.service';


@Component({
    selector: 'sp-lineChart',
    templateUrl: './lineChart.component.html',
    styleUrls: ['./lineChart.component.css']
})
export class LineChartComponent extends BaseChartComponent implements OnChanges {

    @Output() zoomEvent =  new EventEmitter<[number, number]>();

    constructor(public dialog: MatDialog, public plotlyService: PlotlyService, public colorService: ColorService) {
        super();
    }

    dataToDisplay: any[] = undefined;

    selectedStartX = undefined;
    selectedEndX = undefined;
    n_selected_points = undefined;

    labels = {'test': ['testLabel'], 'categoryB': ['test123', 'bad', 'cdfgff', 'dssaadfgd', 'edggssd', 'ggfkfdjhssasddsdssadsasdsksdh', 'qab']};

    private dialogReference = undefined;

    updatemenus = [
        {
            buttons: [
                {
                    args: ['mode', 'lines'],
                    label: 'Line',
                    method: 'restyle'
                },
                {
                    args: ['mode', 'markers'],
                    label: 'Dots',
                    method: 'restyle'
                },

                {
                    args: ['mode', 'lines+markers'],
                    label: 'Dots + Lines',
                    method: 'restyle'
                }
            ],
            direction: 'left',
            pad: {'r': 10, 't': 10},
            showactive: true,
            type: 'buttons',
            x: 0.0,
            xanchor: 'left',
            y: 1.3,
            yanchor: 'top',
            font: {color: '#000'},
            bgcolor: '#fafafa',
            bordercolor: '#000'
        }
    ];

    graph = {
        layout: {
            autosize: true,
            plot_bgcolor: '#fafafa',
            paper_bgcolor: '#fafafa',
            xaxis: {
                type: 'date',
            },
            yaxis: {
                fixedrange: true
            },
            updatemenus: this.updatemenus,

            // setting hovermode to 'closest'
            hovermode: 'closest',
            // adding shapes for displaying labeled time intervals
            shapes: [],
            // box selection with fixed height
            selectdirection: 'h'
        },
        config: {
            // removing lasso-selection, box-selecting, toggling-spikelines and exporting-to-image buttons
            modeBarButtonsToRemove: ['lasso2d', 'select2d', 'toggleSpikelines', 'toImage'],
            // adding custom button: labeling
            modeBarButtonsToAdd: [{
                name: 'Labeling',
                icon: this.plotlyService.getPlotly().Icons.pencil,
                direction: 'up',
                click(gd) {
                    const plotlyService = new PlotlyService();
                    plotlyService.getPlotly().relayout(gd, 'dragmode', 'select'); }}],
            // removing plotly-icon from graph
            displaylogo: false
        }
    };

    ngOnChanges(changes: SimpleChanges) {
        // TODO: is needed because bindings are not working correct
        if (changes.endDateData !== undefined) {
            this.endDateData = changes.endDateData.currentValue;
        }
        if (changes.startDateData !== undefined) {
            this.startDateData = changes.startDateData.currentValue;
        }
        // TODO should be done in displaydata
        if (this.startDateData !== undefined && this.endDateData !== undefined) {
            this.graph.layout.xaxis['range'] = [this.startDateData.getTime(), this.endDateData.getTime()];
        }
    }


    displayData(transformedData: DataResult, yKeys: string[]) {
        if (this.yKeys.length > 0) {
            const tmp = [];
            this.yKeys.forEach(key => {
                transformedData.rows.forEach(serie => {
                    if (serie.name === key) {
                        tmp.push(serie);

                        // adding customdata property in order to store labels in graph
                        serie['customdata'] = Array(serie['x'].length).fill('');
                        // adding custom hovertemplate in order to display labels in graph
                        serie['hovertemplate'] = 'y: %{y}<br>' + 'x: %{x}<br>' + 'label: %{customdata}';
                    }
                });
            });
            this.dataToDisplay = tmp;

        } else {
            this.dataToDisplay = undefined;

        }
    }

    displayGroupedData(transformedData: GroupedDataResult, yKeys: string[]) {
        if (this.yKeys.length > 0) {

            const tmp = [];

            const groupNames = Object.keys(transformedData.dataResults);
            for (const groupName of groupNames)  {
                const value = transformedData.dataResults[groupName];
                this.yKeys.forEach(key => {
                    value.rows.forEach(serie => {
                        if (serie.name === key) {
                            serie.name = groupName + ' ' + serie.name;
                            tmp.push(serie);
                        }
                    });
                });
            }
            this.dataToDisplay = tmp;

        } else {
            this.dataToDisplay = undefined;
        }
    }

    transformData(data: DataResult, xKey: String): DataResult {
        const tmp: any[] = [];

        const dataKeys = [];

        data.rows.forEach(row => {
            data.headers.forEach((headerName, index) => {
                if (!dataKeys.includes(index) && typeof row[index] == 'number') {
                    dataKeys.push(index);
                }
            });
        });

        const indexXkey = data.headers.findIndex(headerName => headerName === this.xKey);

        dataKeys.forEach(key => {
            const headerName = data.headers[key];
            tmp[key] = {
                type: 'scatter', mode: 'lines', name: headerName, connectgaps: false, x: [], y: []};
            });
        data.rows.forEach(row => {
           data.headers.forEach((headerName, index) => {
               if (dataKeys.includes(index)) {
                   tmp[index].x.push(new Date(row[indexXkey]));
                   if ((row[index]) !== undefined) {
                       tmp[index].y.push(row[index]);
                   } else {
                       tmp[index].y.push(null);
                   }
               }
           });
        });
        data.rows = tmp;

        return data;
    }

    transformGroupedData(data: GroupedDataResult, xKey: string): GroupedDataResult {
        for (const key in data.dataResults) {
            const dataResult = data.dataResults[key];
            dataResult.rows = this.transformData(dataResult, xKey).rows;
        }

        return data;
    }

    stopDisplayData() {
    }

    zoomIn($event) {
        // custom zoom-in event handling is now deactivated (no graph reloading)
        // this.zoomEvent.emit([$event['xaxis.range[0]'], $event['xaxis.range[1]']]);
    }

    selectDataPoints($event) {
        // getting selected time interval
        const xStart = $event['range']['x'][0];
        const xEnd = $event['range']['x'][1];

        // updating related global time interval properties
        this.setStartX(xStart);
        this.setEndX(xEnd);

        // getting number of selected data points
        let selected_points = 0;
        for (const series of this.dataToDisplay) {
            if (series['selectedpoints'] !== undefined) {
                selected_points = selected_points + series['selectedpoints'].length;
            }
        }

        // updating related global variable
        this.setNSelectedPoints(selected_points);

        // opening Labeling-Dialog
        this.openLabelingDialog();
        this.dialogReference.componentInstance.data = {labels: this.labels, selected_label: '',
            startX: this.selectedStartX, endX: this.selectedEndX, n_selected_points: this.n_selected_points};
    }

    setStartX(startX: string) {
       this.selectedStartX = startX;
    }

    setEndX(endX: string) {
        this.selectedEndX = endX;
    }

    setNSelectedPoints(n_selected_points: number) {
        this.n_selected_points = n_selected_points;
    }

    private openLabelingDialog() {
        if (this.dialog.openDialogs.length === 0) {

            // displaying Info-Dialog 'Change Chart-Mode' if current graph mode is 'lines'
            if (this.dataToDisplay[0]['mode'] === 'lines') {
                const dialogRef = this.dialog.open(ChangeChartmodeDialog,
                    {
                        width: '400px',
                        position: {top: '150px'}
                });

                this.dialogReference = dialogRef;

            // displaying Labeling-Dialog, obtaining selected label and drawing coloured shape
            } else {
                const dialogRef = this.dialog.open(LabelingDialog,
                    {
                        width: '400px',
                        height: 'auto',
                        position: {top: '75px'},
                        data: {labels: this.labels, selected_label: '', startX: this.selectedStartX, endX:
                        this.selectedEndX, n_selected_points: this.n_selected_points}
                });

                this.dialogReference = dialogRef;

                // after closing Labeling-Dialog
                dialogRef.afterClosed().subscribe(result => {

                    // adding selected label to displayed data points
                    if (result !== undefined) {
                        for (const series of this.dataToDisplay) {
                            for (const point of series['selectedpoints']) {
                                series['customdata'][point] = result;
                            }
                        }

                        // adding coloured shape (based on selected label) to graph (equals selected time interval)
                        const color = this.colorService.getColor(result);
                        const shape = {
                            // shape: rectangle
                            type: 'rect',

                            // x-reference is assigned to the x-values
                            xref: 'x',

                            // y-reference is assigned to the plot paper [0,1]
                            yref: 'paper',
                            y0: 0,
                            y1: 1,

                            // start x: left side of selected time interval
                            x0: this.selectedStartX,
                            // end x: right side of selected time interval
                            x1: this.selectedEndX,

                            // adding color
                            fillcolor: color,

                            // opacity of 20%
                            opacity: 0.2,

                            line: {
                                width: 0
                            }
                        };

                        this.graph.layout.shapes.push(shape);
                    }
                });
            }
        }
    }
}
