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

import {AfterViewInit, Component, OnInit, Renderer2} from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { PlotlyService } from 'angular-plotly.js';
import { DataResult } from '../../../../core-model/datalake/DataResult';
import { GroupedDataResult } from '../../../../core-model/datalake/GroupedDataResult';
import { DatalakeRestService } from '../../../../core-services/datalake/datalake-rest.service';
import { ChangeChartmodeDialog } from '../../../../core-ui/linechart/labeling-tool/dialogs/change-chartmode/change-chartmode.dialog';
import { LabelingDialog } from '../../../../core-ui/linechart/labeling-tool/dialogs/labeling/labeling.dialog';
import { ColorService } from '../../../../core-ui/linechart/labeling-tool/services/color.service';
import { BaseDataExplorerWidget } from '../base/base-data-explorer-widget';
import {EventPropertyUnion} from "../../../../core-model/gen/streampipes-model";
import {ResizeService} from "../../../services/resize.service";

@Component({
  selector: 'sp-data-explorer-line-chart-widget',
  templateUrl: './line-chart-widget.component.html',
  styleUrls: ['./line-chart-widget.component.css']
})
export class LineChartWidgetComponent extends BaseDataExplorerWidget implements OnInit {

  data: any[] = undefined;
  labels: any[] = undefined;
  availableColumns: EventPropertyUnion[] = [];
  availableNonNumericColumns: EventPropertyUnion[] = [];
  selectedColumns: EventPropertyUnion[] = [];
  selectedNonNumericColumn: EventPropertyUnion = undefined;
  dimensionProperties: EventPropertyUnion[] = [];

  yKeys: string[] = [];
  xKey: string;
  private nonNumericKey: string = undefined;

  advancedSettingsActive = false;
  showLabelColumnSelection  = true;

  selectedStartX = undefined;
  selectedEndX = undefined;
  n_selected_points = undefined;



  aggregationValue = 1;
  aggregationTimeUnit = 's';
  groupValue = 'None';
  showCountValue = false;


  constructor(public dialog: MatDialog,
              public plotlyService: PlotlyService,
              public colorService: ColorService,
              public renderer: Renderer2,
              protected dataLakeRestService: DatalakeRestService,
              private resizeService: ResizeService) {
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
      plot_bgcolor: '#fff',
      paper_bgcolor: '#fff',
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
    this.dimensionProperties = this.getDimensionProperties(this.dataExplorerWidget.dataLakeMeasure.eventSchema);
    this.availableNonNumericColumns = this.getNonNumericProperties(this.dataExplorerWidget.dataLakeMeasure.eventSchema);

    // Reduce selected columns when more then 6
    this.selectedColumns = this.availableColumns.length > 6 ? this.availableColumns.slice(0, 5) : this.availableColumns;
    if (this.availableNonNumericColumns.length > 0) {
      // this.selectedNonNumericColumn = this.availableNonNumericColumns[0];
    }
    this.xKey = this.getTimestampProperty(this.dataExplorerWidget.dataLakeMeasure.eventSchema).runtimeName;
    this.yKeys = this.getRuntimeNames(this.selectedColumns);
    //this.nonNumericKey = this.selectedNonNumericColumn.runtimeName;
    this.updateData();
    this.resizeService.resizeSubject.subscribe(info => {
      if (info.gridsterItem.id === this.gridsterItem.id) {
        setTimeout(() => {
          this.graph.layout.autosize = false;
          (this.graph.layout as any).width = (info.gridsterItemComponent.width - 10);
          (this.graph.layout as any).height = (info.gridsterItemComponent.height - 80);
        }, 100)
      }
    });
  }

  updateData() {
    this.graph.layout.shapes = [];
    if (!this.advancedSettingsActive) {
      this.setShownComponents(false, false, true);
      this.dataLakeRestService.getDataAutoAggregation(
        this.dataExplorerWidget.dataLakeMeasure.measureName, this.viewDateRange.startDate.getTime(), this.viewDateRange.endDate.getTime())
        .subscribe((res: DataResult) => {
            if (res.total === 0) {
              this.setShownComponents(true, false, false);
            } else {
              res.measureName = this.dataExplorerWidget.dataLakeMeasure.measureName;
              const tmp = this.transformData(res, this.xKey);
              this.data = this.displayData(tmp, this.yKeys);
              this.labels = this.loadLabels(tmp, this.nonNumericKey);
              this.addLabelsToGraph(this.data, this.labels);
              this.data['measureName'] = tmp.measureName;

              this.setShownComponents(false, true, false);
            }
          }
        );
    } else {
      if (this.groupValue === 'None') {
        this.setShownComponents(false, false, true);
        this.dataLakeRestService.getData(
          this.dataExplorerWidget.dataLakeMeasure.measureName, this.viewDateRange.startDate.getTime(), this.viewDateRange.endDate.getTime()
          , this.aggregationTimeUnit, this.aggregationValue)
          .subscribe((res: DataResult) => {
                if (res.total === 0) {
                  this.setShownComponents(true, false, false);
                } else {
                  res.measureName = this.dataExplorerWidget.dataLakeMeasure.measureName;
                  const tmp = this.transformData(res, this.xKey);
                  this.data = this.displayData(tmp, this.yKeys);
                  this.labels = this.loadLabels(tmp, this.nonNumericKey);
                  this.addLabelsToGraph(this.data, this.labels);
                  this.data['measureName'] = tmp.measureName;

                  this.setShownComponents(false, true, false);
                }
              }
          );
      } else {
        this.dataLakeRestService.getGroupedData(
          this.dataExplorerWidget.dataLakeMeasure.measureName, this.viewDateRange.startDate.getTime(), this.viewDateRange.endDate.getTime(),
          this.aggregationTimeUnit, this.aggregationValue, this.groupValue)
          .subscribe((res: GroupedDataResult) => {
              if (res.total === 0) {
                this.setShownComponents(true, false, false);
              } else {
                // res.measureName = this.dataExplorerWidget.dataLakeMeasure.measureName;
                const tmp = this.transformGroupedData(res, this.xKey);
                this.data = this.displayGroupedData(tmp, this.yKeys);
                // this.data['measureName'] = tmp.measureName;
                // this.data['labels'] = tmp.labels;

                if (this.data !== undefined && this.data['labels'] !== undefined && this.data['labels'].length > 0) {
                  this.addInitialColouredShapesToGraph();
                }

                this.setShownComponents(false, true, false);
              }

            }
          );
      }
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
      return tmp;

    } else {
      return undefined;

    }
  }

  loadLabels(transformedData: DataResult, labelKey: string) {
    let labels = undefined;
    if (labelKey !== undefined) {
      transformedData.rows.forEach(serie => {
        if (serie.name === labelKey) {
          labels = serie.y;
        }
      });
    }
    return labels;
  }

  addLabelsToGraph(data, labels) {
    data.forEach(serie => {
      // adding customdata property in order to store labels in graph
      if (labels !== undefined && labels.length !== 0) {
        serie['customdata'] = labels;
        this.addInitialColouredShapesToGraph();
      } else {
        serie['customdata'] = Array(serie['x'].length).fill('');
      }
      // adding custom hovertemplate in order to display labels in graph
      serie['hovertemplate'] = 'y: %{y}<br>' + 'x: %{x}<br>' + this.nonNumericKey + ': %{customdata}';
    });
    this.data = data;
  }

  displayGroupedData(transformedData: GroupedDataResult, yKeys: string[]) {
    // if (this.yKeys.length > 0) {

    console.log('count value ' + this.showCountValue);
      const tmp = [];

      const groupNames = Object.keys(transformedData.dataResults);
      for (const groupName of groupNames) {
        const value = transformedData.dataResults[groupName];
        this.yKeys.forEach(key => {
          value.rows.forEach(serie => {
            if (serie.name === key) {
              serie.name = groupName + ' ' + serie.name;
              tmp.push(serie);
            }
          });
        });

        if (this.showCountValue) {
          let containsCount = false;
          value.rows.forEach(serie => {
            if (serie.name.startsWith('count') && !containsCount) {
              serie.name = groupName + ' count';
              tmp.push(serie);
              containsCount = true;
            }
          });
        }
      }
      return tmp;

    // } else {
    //   return undefined;
    // }
  }

  transformData(data: DataResult, xKey: string): DataResult {
    const tmp: any[] = [];

    const dataKeys = [];
    const label_columns = [];

    data.rows.forEach(row => {
      data.headers.forEach((headerName, index) => {
        if (!dataKeys.includes(index) && typeof row[index] === 'number') {
          dataKeys.push(index);
        } else if (!label_columns.includes(index) && typeof row[index] === 'string') {
          label_columns.push(index);
        }
      });
    });

    const indexXkey = data.headers.findIndex(headerName => headerName === this.xKey);

    dataKeys.forEach(key => {
      const headerName = data.headers[key];
      tmp[key] = {
        type: 'scatter', mode: 'lines', name: headerName, connectgaps: false, x: [], y: []};
    });

    label_columns.forEach(key => {
      const headerName = data.headers[key];
      tmp[key] = {
        name: headerName, x: [], y: []};
    });

    data.rows.forEach(row => {
      data.headers.forEach((headerName, index) => {
        if (dataKeys.includes(index) || label_columns.includes(index)) {
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

  setSelectedColumn(selectedColumns: EventPropertyUnion[]) {
    this.selectedColumns = selectedColumns;
    this.yKeys = this.getRuntimeNames(selectedColumns);
    this.updateData();
  }

  setSelectedLabelColumn(selectedLabelColumn: EventPropertyUnion) {
    this.selectedNonNumericColumn = selectedLabelColumn;
    this.nonNumericKey = selectedLabelColumn.runtimeName;
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
            this.labels = this.data[0]['customdata'];
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
    if (this.nonNumericKey === undefined) {
      this.nonNumericKey = 'sp_internal_label';
    }

    this.dataLakeRestService.saveLabelsInDatabase(this.data['measureName'], this.nonNumericKey, startdate, enddate, label).subscribe(
            res => {
              // console.log('Successfully wrote label ' + currentLabel + ' into database.');
            }
            );
  }

  private addInitialColouredShapesToGraph() {
    let selectedLabel = '';
    let indices = [];
    for (let label = 0; label <= this.labels.length; label++) {
      if (selectedLabel !== this.labels[label] && indices.length > 0) {
        const startdate = new Date(this.data[0]['x'][indices[0]]).getTime();
        const enddate = new Date(this.data[0]['x'][indices[indices.length - 1]]).getTime();
        const color = this.colorService.getColor(selectedLabel);

        this.addShapeToGraph(startdate, enddate, color, true);

        selectedLabel = undefined;
        indices = [];
        indices.push(label);
      } else {
        indices.push(label);
      }
      selectedLabel = this.labels[label];
    }
  }

  private addShapeToGraph(start, end, color, initial= false) {
    start = new Date(start).getTime();
    end = new Date(end).getTime();

    const shape = this.createShape(start, end, color);

    if (!initial) {
      const updated_shapes = [];

      for (let i = 0; i < this.graph.layout.shapes.length; i++) {
        const selected_shape = this.graph.layout.shapes[i];
        const x0 = selected_shape['x0'];
        const x1 = selected_shape['x1'];

        if (x0 <= start && x1 > start && x1 <= end) {
          selected_shape.x1 = start;
          updated_shapes.push(selected_shape);

        } else if (x0 >= start && x0 <= end) {
          if (x1 > end) {
            selected_shape.x0 = end;
            updated_shapes.push(selected_shape);
          }

        } else if (x0 <= start && x1 > end) {
          const left_shape = this.createShape(x0, start, selected_shape.fillcolor);
          updated_shapes.push(left_shape);

          const right_shape = this.createShape(end, x1, selected_shape.fillcolor);
          updated_shapes.push(right_shape);

        } else {
          updated_shapes.push(selected_shape);
        }
      }
      this.graph.layout.shapes = updated_shapes;
    }
    this.graph.layout.shapes.push(shape);
  }

  private createShape(start, end, color) {
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
  return shape;
  }

  handlingAdvancedToggleChange() {
    this.showLabelColumnSelection = !this.showLabelColumnSelection;
    this.updateData();
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
