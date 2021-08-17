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
import { DataResult } from '../../../../core-model/datalake/DataResult';
import { GroupedDataResult } from '../../../../core-model/datalake/GroupedDataResult';
import { DatalakeRestService } from '../../../../core-services/datalake/datalake-rest.service';
import { ColorService } from './services/color.service';
import { BaseDataExplorerWidget } from '../base/base-data-explorer-widget';
import { Label } from '../../../../core-model/gen/streampipes-model';
import { ResizeService } from '../../../services/resize.service';
import { LabelService } from '../../../../core-ui/labels/services/label.service';
import { LineChartWidgetModel } from './model/line-chart-widget.model';
import { WidgetConfigurationService } from '../../../services/widget-configuration.service';

@Component({
  selector: 'sp-data-explorer-line-chart-widget',
  templateUrl: './line-chart-widget.component.html',
  styleUrls: ['./line-chart-widget.component.css']
})
export class LineChartWidgetComponent extends BaseDataExplorerWidget<LineChartWidgetModel> implements OnInit {

  data: any[] = undefined;
  colorPropertyStringValues: any[] = undefined;
  private backgroundColorPropertyKey: string = undefined;

  advancedSettingsActive = false;
  showBackgroundColorProperty = true;

  selectedStartX = undefined;
  selectedEndX = undefined;
  n_selected_points = undefined;

  selectedLabel;

  // this can be set to scale the line chart according to the layout
  offsetRightLineChart = 10;


  constructor(public dialog: MatDialog,
              public colorService: ColorService,
              public renderer: Renderer2,
              protected dataLakeRestService: DatalakeRestService,
              public labelService: LabelService,
              widgetConfigurationService: WidgetConfigurationService,
              resizeService: ResizeService) {
    super(dataLakeRestService, dialog, widgetConfigurationService, resizeService);
  }

  // indicator variable if labeling mode is activated
  //labelingModeOn = false;

  updatemenus = [];

  graph = {

    layout: {
      font: {
        color: '#FFF'
      },
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
      modeBarButtonsToRemove: ['lasso2d', 'select2d', 'toggleSpikelines', 'toImage'],
      displaylogo: false
    }
  };


  ngOnInit(): void {
    this.updatemenus = [{
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
      font: {color: this.dataExplorerWidget.baseAppearanceConfig.textColor},
      bgcolor: this.dataExplorerWidget.baseAppearanceConfig.backgroundColor,
      bordercolor: '#000'
    }];

    this.updateAppearance();

    super.ngOnInit();
    this.updateData();
    this.resizeService.resizeSubject.subscribe(info => {
      if (info.gridsterItem.id === this.gridsterItem.id) {
        setTimeout(() => {
          this.graph.layout.autosize = false;
          (this.graph.layout as any).width = (info.gridsterItemComponent.width - this.offsetRightLineChart);
          (this.graph.layout as any).height = (info.gridsterItemComponent.height - 80);
        }, 100);
      }
    });
  }

  private processNonGroupedData(res: DataResult) {
    if (res.total === 0) {
      this.setShownComponents(true, false, false);
    } else {
      res.measureName = this.dataLakeMeasure.measureName;
      const tmp = this.transformData(res, this.dataExplorerWidget.dataConfig.xKey);
      this.data = this.displayData(tmp, this.dataExplorerWidget.dataConfig.yKeys);

      if (this.dataExplorerWidget.dataConfig.labelingModeOn) {
        this.backgroundColorPropertyKey = 'sp_internal_label';
      }
      this.colorPropertyStringValues = this.loadBackgroundColor(tmp, this.backgroundColorPropertyKey);
      this.addBackgroundColorToGraph(this.data, this.colorPropertyStringValues, this.backgroundColorPropertyKey);
      this.data['measureName'] = tmp.measureName;

      this.setShownComponents(false, true, false);
    }
  }

  private processGroupedData(res: GroupedDataResult) {
    if (res.total === 0) {
      this.setShownComponents(true, false, false);
    } else {
      const tmp = this.transformGroupedData(res, this.dataExplorerWidget.dataConfig.xKey);
      this.data = this.displayGroupedData(tmp);

      if (this.data !== undefined &&
          this.data['colorPropertyStringValues'] !== undefined && this.data['colorPropertyStringValues'].length > 0) {
        this.addInitialColouredShapesToGraph(this.colorPropertyStringValues, this.colorService.getColor);
      }

      this.setShownComponents(false, true, false);
    }
  }

  displayData(transformedData: DataResult, yKeys: string[]) {
    if (this.dataExplorerWidget.dataConfig.yKeys && this.dataExplorerWidget.dataConfig.yKeys.length > 0) {
      const tmp = [];
      this.dataExplorerWidget.dataConfig.yKeys.forEach(key => {
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

  loadBackgroundColor(transformedData: DataResult, backgroundColorKey: string) {
    let labels;
    if (backgroundColorKey !== undefined) {
      transformedData.rows.forEach(serie => {
        if (serie.name === backgroundColorKey) {
          labels = serie.y;
        }
      });
    }
    return labels;
  }

  addBackgroundColorToGraph(data, colorPropertyStringValues, backgroundColorPropertyKey) {

    // the all labels function is required to get the correct color and internal name for labeled data
    this.labelService.getAllLabels().subscribe((res: Label[]) => {
      // holds all labels
      const bufferedLabels = {};

      for (const l of res) {
        bufferedLabels[l._id] = l;
      }

      const newColorPropertyStringValues = [];
      if (colorPropertyStringValues !== undefined && colorPropertyStringValues.length !== 0) {
        for (const c of colorPropertyStringValues) {
          if (this.dataExplorerWidget.dataConfig.labelingModeOn) {
            if (c === '') {
              newColorPropertyStringValues.push(c);
            } else {
              newColorPropertyStringValues.push(bufferedLabels[c].name);
            }
          } else {
            newColorPropertyStringValues.push(c);
          }
        }
      }

      // define the function that defines the colors
      let colorFunction;
      if (this.dataExplorerWidget.dataConfig.labelingModeOn) {
        colorFunction = ((id) => {
          if (id === '') {
            return '#FFFFFF';
          } else {
            return bufferedLabels[id].color;
          }
        });
      } else {
        colorFunction = this.colorService.getColor;
      }

      // Add labes and colors for each series of the line chart
      data.forEach(serie => {
        // add custom data property in order to store colorPropertyStringValues in graph
        if (newColorPropertyStringValues !== undefined && newColorPropertyStringValues.length !== 0) {
          serie['customdata'] = newColorPropertyStringValues;
          // TODO fix this and continue here

          serie['hovertemplate'] = 'y: %{y}<br>' + 'x: %{x}<br>' + backgroundColorPropertyKey + ': %{customdata}';
          this.addInitialColouredShapesToGraph(colorPropertyStringValues, colorFunction);
        } else {
          serie['customdata'] = Array(serie['x'].length).fill('');
          serie['hovertemplate'] = 'y: %{y}<br>' + 'x: %{x}';
        }
        // adding custom hovertemplate in order to display colorPropertyStringValues in graph
      });
      this.data = data;

    });
  }

  displayGroupedData(transformedData: GroupedDataResult) {
    const tmp = [];

    const groupNames = Object.keys(transformedData.dataResults);
    for (const groupName of groupNames) {
      const value = transformedData.dataResults[groupName];
      this.dataExplorerWidget.dataConfig.yKeys.forEach(key => {
        value.rows.forEach(serie => {
          if (serie.name === key) {
            serie.name = groupName + ' ' + serie.name;
            tmp.push(serie);
          }
        });
      });

      if (this.dataExplorerWidget.dataConfig.showCountValue) {
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
  }

  transformData(data: DataResult, xKey: string): DataResult {
    const columnsContainingNumbers = [];
    const columnsContainingStrings = [];

    // Check column type
    data.rows.forEach(row => {
      data.headers.forEach((headerName, index) => {
        if (!columnsContainingNumbers.includes(index) && typeof row[index] === 'number') {
          columnsContainingNumbers.push(index);
        } else if (!columnsContainingStrings.includes(index) && typeof row[index] === 'string') {
          columnsContainingStrings.push(index);
        }
      });
    });

    // Get key of timestamp column for x axis
    const indexXkey = data.headers.findIndex(headerName => headerName === this.dataExplorerWidget.dataConfig.xKey);


    const tmpLineChartTraces: any[] = [];

    // create line chart traces according to column type
    columnsContainingNumbers.forEach(key => {
      const headerName = data.headers[key];
      tmpLineChartTraces[key] = {
        type: 'scatter',
        mode: this.dataExplorerWidget.dataConfig.chartMode,
        name: headerName,
        connectgaps: false,
        x: [],
        y: []
      };
    });

    columnsContainingStrings.forEach(key => {
      const headerName = data.headers[key];
      tmpLineChartTraces[key] = {
        name: headerName, x: [], y: []
      };
    });

    // fill line chart traces with data
    data.rows.forEach(row => {
      data.headers.forEach((headerName, index) => {
        if (columnsContainingNumbers.includes(index) || columnsContainingStrings.includes(index)) {
          tmpLineChartTraces[index].x.push(new Date(row[indexXkey]));
          if ((row[index]) !== undefined) {
            tmpLineChartTraces[index].y.push(row[index]);
          } else {
            tmpLineChartTraces[index].y.push(null);
          }
        }
      });
    });
    data.rows = tmpLineChartTraces;
    return data;
  }

  transformGroupedData(data: GroupedDataResult, xKey: string): GroupedDataResult {
    for (const key in data.dataResults) {
      const dataResult = data.dataResults[key];
      dataResult.rows = this.transformData(dataResult, xKey).rows;
    }

    return data;
  }


  changeLabelOfArea($event) {
    const selected = $event.points[0];
    const allData = selected.fullData;

    const labelOfSelected = selected.customdata;
    const dateOfSelected = new Date(selected.x);
    const indexOfSelected = allData.x.map(Number).indexOf(+dateOfSelected);

    // got to left to get class change
    let searchIndex = indexOfSelected;

    while (labelOfSelected === allData.customdata[searchIndex]) {
      searchIndex = searchIndex - 1;
    }

    this.selectedStartX = allData.x[searchIndex + 1];

    searchIndex = indexOfSelected;

    while (labelOfSelected === allData.customdata[searchIndex]) {
      searchIndex = searchIndex + 1;
    }

    this.selectedEndX = allData.x[searchIndex - 1];

    this.saveLabelsInDatabase(this.selectedLabel._id, this.selectedStartX, this.selectedEndX);

    // adding coloured shape (based on selected label) to graph (equals selected time interval)
    this.addShapeToGraph(this.selectedStartX, this.selectedEndX, this.selectedLabel.color);


  }

  selectDataPoints($event) {

    if ($event !== undefined) {
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

      for (const series of this.data) {
        for (const point of series['selectedpoints']) {
          series['customdata'][point] = this.selectedLabel._id;
        }
      }
      this.colorPropertyStringValues = this.data[0]['customdata'];
      // saving colorPropertyStringValues persistently
      this.saveLabelsInDatabase(this.selectedLabel._id, this.selectedStartX, this.selectedEndX);

      // adding coloured shape (based on selected label) to graph (equals selected time interval)
      this.addShapeToGraph(this.selectedStartX, this.selectedEndX, this.selectedLabel.color);

    }
  }

  handleLabelChange(label: Label) {
    this.selectedLabel = label;
  }

  toggleLabelingMode() {
    if (this.dataExplorerWidget.dataConfig.labelingModeOn) {
      for (let i = 0; i < this.data.length; i++) {
        this.data[i]['mode'] = 'lines+markers';
      }
      this.activateLabelingMode();
      this.offsetRightLineChart = 150;
    } else {
      this.dataExplorerWidget.dataConfig.labelingModeOn = false;
      this.offsetRightLineChart = 10;
      this.deactivateLabelingMode();
    }
  }

  private activateLabelingMode() {
    const modeBarButtons = document.getElementsByClassName('modebar-btn');
    this.showBackgroundColorProperty = false;

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


    this.updateData();

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
    this.showBackgroundColorProperty = true;

  }

  private saveLabelsInDatabase(label, start_X, end_X) {
    const startdate = new Date(start_X).getTime() - 1;
    const enddate = new Date(end_X).getTime() + 1;

    this.dataLakeRestService.saveLabelsInDatabase(
        this.data['measureName'],
        'sp_internal_label',
        startdate,
        enddate,
        label,
        this.dataExplorerWidget.dataConfig.xKey
    ).subscribe(
        res => {
          // TODO add pop up similar to images
          // console.log('Successfully wrote label ' + currentLabel + ' into database.');
        }
    );
  }

  private addInitialColouredShapesToGraph(newColorPropertyStringValues, getColor) {
    let selectedLabel = '';
    let indices = [];
    if (newColorPropertyStringValues !== undefined) {

      for (let label = 0; label < newColorPropertyStringValues.length; label++) {
        if (selectedLabel !== newColorPropertyStringValues[label] && indices.length > 0) {
          const startdate = new Date(this.data[0]['x'][indices[0]]).getTime();
          const enddate = new Date(this.data[0]['x'][indices[indices.length - 1]]).getTime();

          // TODO get color of label and text
          this.addShapeToGraph(startdate, enddate, getColor(newColorPropertyStringValues[label - 1]), true);

          selectedLabel = undefined;
          indices = [];
          indices.push(label);
        } else {
          indices.push(label);
        }
        selectedLabel = this.colorPropertyStringValues[label];
      }
    }
  }

  private addShapeToGraph(start, end, color, initial = false) {
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

  setStartX(startX: string) {
    this.selectedStartX = startX;
  }

  setEndX(endX: string) {
    this.selectedEndX = endX;
  }

  setNSelectedPoints(n_selected_points: number) {
    this.n_selected_points = n_selected_points;
  }

  refreshData() {
    this.graph.layout.shapes = [];
    if (!this.advancedSettingsActive) {
      this.setShownComponents(false, false, true);
      this.dataLakeRestService.getDataAutoAggregation(
          this.dataLakeMeasure.measureName, this.timeSettings.startTime, this.timeSettings.endTime)
          .subscribe((res: DataResult) => {
            this.processNonGroupedData(res);
          });
    } else {
      if (this.dataExplorerWidget.dataConfig.groupValue === 'None') {
        this.setShownComponents(false, false, true);
        this.dataLakeRestService.getData(
            this.dataLakeMeasure.measureName, this.timeSettings.startTime, this.timeSettings.endTime,
            this.dataExplorerWidget.dataConfig.aggregationTimeUnit, this.dataExplorerWidget.dataConfig.aggregationValue)
            .subscribe((res: DataResult) => {
              this.processNonGroupedData(res);
            });
      } else {
        // this.dataLakeRestService.getGroupedData(
        //   this.dataExplorerWidget.dataLakeMeasure.measureName, this.viewDateRange.startDate.getTime(), this.viewDateRange.endDate.getTime(),
        //   this.aggregationTimeUnit, this.aggregationValue, this.groupValue)
        //   .subscribe((res: GroupedDataResult) => {
        //     this.processGroupedData(res);
        //   });
      }
    }
  }

  updateAppearance() {
    this.graph.layout.paper_bgcolor = this.dataExplorerWidget.baseAppearanceConfig.backgroundColor;
    this.graph.layout.plot_bgcolor = this.dataExplorerWidget.baseAppearanceConfig.backgroundColor;
    this.graph.layout.font.color = this.dataExplorerWidget.baseAppearanceConfig.textColor;
    if (this.data) {
      this.data.forEach(d => d.mode = this.dataExplorerWidget.dataConfig.chartMode);
    }
  }

  refreshView() {
    this.updateAppearance();
    if (this.data && !this.showNoDataInDateRange && !this.showIsLoadingData) {
      (window as any).Plotly.restyle(this.dataExplorerWidget._id, this.graph, 0);
    }
    //this.toggleLabelingMode();
  }
}
