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

import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { DataResult } from '../../../../core-model/datalake/DataResult';
import { GroupedDataResult } from '../../../../core-model/datalake/GroupedDataResult';
import { BaseVisualisationComponent } from '../base/baseVisualisation.component';


@Component({
    selector: 'sp-lineChart',
    templateUrl: './lineChart.component.html',
    styleUrls: ['./lineChart.component.css']
})
export class LineChartComponent extends BaseVisualisationComponent implements OnChanges {

    @Output() zoomEvent =  new EventEmitter<[number, number]>();

    constructor() {
        super();
    }

    dataToDisplay: any[] = undefined;

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
        this.zoomEvent.emit([$event['xaxis.range[0]'], $event['xaxis.range[1]']]);
  }

}
