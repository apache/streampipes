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

import { Component, OnDestroy, OnInit } from '@angular/core';
import { DataResult } from '../../../../core-model/datalake/DataResult';
import { DatalakeRestService } from '../../../../core-services/datalake/datalake-rest.service';
import { BaseDataExplorerWidget } from '../base/base-data-explorer-widget';

@Component({
  selector: 'sp-data-explorer-line-chart-widget',
  templateUrl: './line-chart-widget.component.html',
  styleUrls: ['./line-chart-widget.component.css']
})
export class LineChartWidgetComponent extends BaseDataExplorerWidget implements OnInit {

  data: any[] = undefined;
  availableColumns: string[] = ['time', 'count', 'randomText', 'randomNumber', 'timestamp'];
  yKeys: string[] = ['time', 'count', 'randomText', 'randomNumber', 'timestamp'];
  xKey = 'time';

  constructor(private dataLakeRestService: DatalakeRestService) {
    super();
  }


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


  ngOnInit(): void {
    this.updateData();
  }

  updateData() {

    this.setShownComponents(false, false, true);
    this.dataLakeRestService.getDataAutoAggergation(
      this.dataExplorerWidget.dataLakeMeasure.measureName, this.viewDateRange.startDate.getTime(), this.viewDateRange.endDate.getTime())
      .subscribe((res: DataResult) => {
        if (res.total === 0) {
          this.setShownComponents(true, false, false);
        } else {
          const tmp = this.transformData(res, this.xKey);
          this.data = this.displayData(tmp, this.yKeys);
          this.setShownComponents(false, true, false);
        }

      }
    );
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
      return tmp;

    } else {
      return undefined;

    }
  }

  transformData(data: DataResult, xKey: string): DataResult {
    const tmp: any[] = [];

    const dataKeys = [];

    data.rows.forEach(row => {
      data.headers.forEach((headerName, index) => {
        if (!dataKeys.includes(index) && typeof row[index] === 'number') {
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

  setSelectedColumn(selectedColumns: string[]) {
    this.yKeys = selectedColumns;
  }
}
