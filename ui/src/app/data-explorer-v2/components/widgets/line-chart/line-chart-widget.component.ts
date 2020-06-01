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

import { Component, OnInit, Renderer2 } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { PlotlyService } from 'angular-plotly.js';
import { EventProperty } from '../../../../connect/schema-editor/model/EventProperty';
import { DataResult } from '../../../../core-model/datalake/DataResult';
import { DatalakeRestService } from '../../../../core-services/datalake/datalake-rest.service';
import { ChangeChartmodeDialog } from '../../../../core-ui/linechart/labeling-tool/dialogs/change-chartmode/change-chartmode.dialog';
import { LabelingDialog } from '../../../../core-ui/linechart/labeling-tool/dialogs/labeling/labeling.dialog';
import { ColorService } from '../../../../core-ui/linechart/labeling-tool/services/color.service';
import { BaseDataExplorerWidget } from '../base/base-data-explorer-widget';

@Component({
  selector: 'sp-data-explorer-line-chart-widget',
  templateUrl: './line-chart-widget.component.html',
  styleUrls: ['./line-chart-widget.component.css']
})
export class LineChartWidgetComponent extends BaseDataExplorerWidget implements OnInit {

  data: any[] = undefined;
  availableColumns: EventProperty[] = [];
  selectedColumns: EventProperty[] = [];
  yKeys: string[] = [];
  xKey: string;

  selectedStartX = undefined;
  selectedEndX = undefined;
  n_selected_points = undefined;

  constructor(public dialog: MatDialog, public plotlyService: PlotlyService, public colorService: ColorService,
              public renderer: Renderer2, protected dataLakeRestService: DatalakeRestService) {
    super(dataLakeRestService, dialog);
  }

  // indicator variable if labeling mode is activated
  private labelingModeOn = false;
  
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
      selectdirection: 'h',

      // default dragmode is zoom
      dragmode: 'zoom'
    },
    config: {
      // removing lasso-selection, box-selecting, toggling-spikelines and exporting-to-image buttons
      modeBarButtonsToRemove: ['lasso2d', 'select2d', 'toggleSpikelines', 'toImage'],
      // adding custom button: labeling
      modeBarButtonsToAdd: [this.createLabelingModeBarButton()],
      // removing plotly-icon from graph
      displaylogo: false
    }
  };


  ngOnInit(): void {

    this.availableColumns = this.getNumericProperty(this.dataExplorerWidget.dataLakeMeasure.eventSchema);
    // Reduce selected columns when more then 6
    this.selectedColumns = this.availableColumns.length > 6 ? this.availableColumns.slice(0, 5) : this.availableColumns;

    this.xKey = this.getTimestampProperty(this.dataExplorerWidget.dataLakeMeasure.eventSchema).runtimeName;
    this.yKeys = this.getRuntimeNames(this.selectedColumns);
    this.updateData();
  }

  changeResolution() {
    console.log('Change Resolution');

    this.setShownComponents(false, false, true);
    this.dataLakeRestService.getData(
      this.dataExplorerWidget.dataLakeMeasure.measureName, this.viewDateRange.startDate.getTime(), this.viewDateRange.endDate.getTime()
      , 's', 1)
      .subscribe((res: DataResult) => {

          if (res.total === 0) {
            this.setShownComponents(true, false, false);
          } else {
            res.measureName = this.dataExplorerWidget.dataLakeMeasure.measureName;
            const tmp = this.transformData(res, this.xKey);
            this.data = this.displayData(tmp, this.yKeys);
            this.data['measureName'] = tmp.measureName;
            this.data['labels'] = tmp.labels;

            if (this.data['labels'] !== undefined && this.data['labels'].length > 0) {
              this.addInitialColouredShapesToGraph();
            }

            this.setShownComponents(false, true, false);
          }

        }
      );
  }

  updateData() {

    this.setShownComponents(false, false, true);
    this.dataLakeRestService.getDataAutoAggergation(
      this.dataExplorerWidget.dataLakeMeasure.measureName, this.viewDateRange.startDate.getTime(), this.viewDateRange.endDate.getTime())
      .subscribe((res: DataResult) => {

        if (res.total === 0) {
          this.setShownComponents(true, false, false);
        } else {
          res.measureName = this.dataExplorerWidget.dataLakeMeasure.measureName;
          const tmp = this.transformData(res, this.xKey);
          this.data = this.displayData(tmp, this.yKeys);
          this.data['measureName'] = tmp.measureName;
          this.data['labels'] = tmp.labels;

          if (this.data['labels'] !== undefined && this.data['labels'].length > 0) {
            this.addInitialColouredShapesToGraph();
          }

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

            // adding customdata property in order to store labels in graph
            if (transformedData.labels !== undefined && transformedData.labels.length !== 0) {
              serie['customdata'] = transformedData.labels;
            } else {
              serie['customdata'] = Array(serie['x'].length).fill('');
            }
            // adding custom hovertemplate in order to display labels in graph
            serie['hovertemplate'] = 'y: %{y}<br>' + 'x: %{x}<br>' + 'label: %{customdata}';

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
    const label_column = [];

    data.rows.forEach(row => {
      data.headers.forEach((headerName, index) => {
        if (!dataKeys.includes(index) && typeof row[index] === 'number') {
          dataKeys.push(index);
        }
        else if (!label_column.includes(index) && typeof  row[index] == 'string' && data.headers[index] == "sp_internal_label") {
          label_column.push(index);
        }
      });
    });

    const indexXkey = data.headers.findIndex(headerName => headerName === this.xKey);
    const labels = [];

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
        } else if (label_column.length > 0 && label_column.includes(index)) {
          labels.push(row[index]);
        }
      });
    });
    data.rows = tmp;
    data.labels = labels;

    return data;
  }

  setSelectedColumn(selectedColumns: EventProperty[]) {
    this.selectedColumns = selectedColumns;
    this.yKeys = this.getRuntimeNames(selectedColumns);
    this.updateData();
  }

  handleDefaultModeBarButtonClicks($event) {
    if (!('xaxis.autorange' in $event) && !('hovermode' in $event)) {
      if ($event.dragmode !== 'select') {
        this.deactivateLabelingMode();
        this.labelingModeOn = false;
      }
    } else if (($event['xaxis.autorange'] === true || $event['hovermode'] === true) && this.labelingModeOn) {
      this.activateLabelingMode();
    }
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
    for (const series of this.data) {
      if (series['selectedpoints'] !== undefined) {
        selected_points = selected_points + series['selectedpoints'].length;
      }
    }

    // updating related global variable
    this.setNSelectedPoints(selected_points);

    // opening Labeling-Dialog
    this.openLabelingDialog();
    this.dialogReference.componentInstance.data = {labels: this.dataLakeRestService.get_timeseries_labels(), selected_label: '',
      startX: this.selectedStartX, endX: this.selectedEndX, n_selected_points: this.n_selected_points};
  }

  private openLabelingDialog() {
    if (this.dialog.openDialogs.length === 0) {

      // displaying Info-Dialog 'Change Chart-Mode' if current graph mode is 'lines'
      if (this.data[0]['mode'] === 'lines') {

        // deactivating labeling mode
        this.labelingModeOn = false;
        this.deactivateLabelingMode();

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
              data: {labels: this.dataLakeRestService.get_timeseries_labels(), selected_label: '', startX: this.selectedStartX, endX:
                this.selectedEndX, n_selected_points: this.n_selected_points}
            });

        this.dialogReference = dialogRef;

        // after closing Labeling-Dialog
        dialogRef.afterClosed().subscribe(result => {

          // adding selected label to displayed data points
          if (result !== undefined) {
            for (const series of this.data) {
              for (const point of series['selectedpoints']) {
                series['customdata'][point] = result;
              }
            }
            this.data['labels'] = this.data[0]['customdata'];
            // saving labels persistently
            this.saveLabelsInDatabase(result, this.selectedStartX, this.selectedEndX);

            // adding coloured shape (based on selected label) to graph (equals selected time interval)
            this.addShapeToGraph(this.selectedStartX, this.selectedEndX, this.colorService.getColor(result));

            // remain in selection dragmode if labeling mode is still activated
            if (this.labelingModeOn) {
              this.graph.layout.dragmode = 'select';
            } else {
              this.graph.layout.dragmode = 'zoom';
            }
          }
        });
      }
    }
  }

  private createLabelingModeBarButton() {
    const labelingModeBarButton = {
      name: 'Labeling',
      icon: this.plotlyService.getPlotly().Icons.pencil,
      direction: 'up',
      click: (gd) => {

        // only allowing to activate labeling mode if current graph mode does not equal 'lines'
        if (this.data[0]['mode'] !== 'lines') {
          this.labelingModeOn = !this.labelingModeOn;

          // activating labeling mode
          if (this.labelingModeOn) {
            this.activateLabelingMode();

            // deactivating labeling mode
          } else {
            this.deactivateLabelingMode();
          }

          // otherwise displaying 'Change Chart Mode Dialog' or deactivating labeling mode
        } else {
          if (this.labelingModeOn) {
            this.labelingModeOn = !this.labelingModeOn;
            this.deactivateLabelingMode();
          } else {
            this.openLabelingDialog();
          }
        }
      }
    };
    return labelingModeBarButton;
  }

  private activateLabelingMode() {
    const modeBarButtons = document.getElementsByClassName('modebar-btn');

    for (let i = 0; i < modeBarButtons.length; i++) {
      if (modeBarButtons[i].getAttribute('data-title') === 'Labeling') {

        // fetching path of labeling button icon
        const path = modeBarButtons[i].getElementsByClassName('icon').item(0)
            .getElementsByTagName('path').item(0);

        // adding 'clicked' to class list
        modeBarButtons[i].classList.add('clicked');

        // changing color of fetched path
        this.renderer.setStyle(path, 'fill', '#39B54A');
      }
    }

    // changing dragmode to 'select'
    this.graph.layout.dragmode = 'select';
  }

  private deactivateLabelingMode() {
    const modeBarButtons = document.getElementsByClassName('modebar-btn');

    for (let i = 0; i < modeBarButtons.length; i++) {
      if (modeBarButtons[i].getAttribute('data-title') === 'Labeling') {

        // fetching path of labeling button icon
        const path = modeBarButtons[i].getElementsByClassName('icon').item(0)
            .getElementsByTagName('path').item(0);

        // removing 'clicked' from class list
        modeBarButtons[i].classList.remove('clicked');

        // changing path color to default plotly modebar button color
        this.renderer.setStyle(path, 'fill', 'rgba(68, 68, 68, 0.3)');
      }
    }

    // changing dragmode to 'zoom'
    this.graph.layout.dragmode = 'zoom';
    
  }

  private saveLabelsInDatabase(label, start_X, end_X) {
    const startdate = new Date(start_X).getTime() - 1;
    const enddate = new Date(end_X).getTime() + 1;
    this.dataLakeRestService.saveLabelsInDatabase(this.data['measureName'], startdate, enddate, label).subscribe(
            res => {
              // console.log('Successfully wrote label ' + currentLabel + ' into database.');
            }
            );
  }

  private addInitialColouredShapesToGraph() {
    let selectedLabel = undefined;
    let indices = [];
    for (const label in this.data['labels']) {
      if (selectedLabel !== this.data['labels'][label] && indices.length > 0) {
        const startdate = new Date(this.data[0]['x'][indices[0]]).getTime();
        const enddate = new Date(this.data[0]['x'][indices[indices.length - 1]]).getTime();
        const color = this.colorService.getColor(selectedLabel);

        this.addShapeToGraph(startdate, enddate, color);

        selectedLabel = undefined;
        indices = [];
        indices.push(label);
      } else {
        indices.push(label);
      }
      selectedLabel = this.data['labels'][label];
    }
    const last_start = new Date(this.data[0]['x'][indices[0]]).getTime();
    const last_end = new Date(this.data[0]['x'][indices[indices.length - 1]]).getTime();
    const last_color = this.colorService.getColor(selectedLabel);

    this.addShapeToGraph(last_start, last_end, last_color);
  }

  private addShapeToGraph(start, end, color) {
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
      x0: start,
      // end x: right side of selected time interval
      x1: end,

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
  
  setStartX(startX: string) {
    this.selectedStartX = startX;
  }

  setEndX(endX: string) {
    this.selectedEndX = endX;
  }

  setNSelectedPoints(n_selected_points: number) {
    this.n_selected_points = n_selected_points;
  }
}
