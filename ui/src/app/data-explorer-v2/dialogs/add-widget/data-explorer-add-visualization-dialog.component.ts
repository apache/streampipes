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

import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { EventSchema } from '../../../connect/schema-editor/model/EventSchema';
import { DataExplorerWidgetModel } from '../../../core-model/datalake/DataExplorerWidgetModel';
import { InfoResult } from '../../../core-model/datalake/InfoResult';
import { ElementIconText } from '../../../services/get-element-icon-text.service';
import { IDataViewDashboard } from '../../models/dataview-dashboard.model';
import { DataExplorerWidgetRegistry } from '../../registry/data-explorer-widget-registry';
import { DataViewDataExplorerService } from '../../services/data-view-data-explorer.service';

@Component({
  selector: 'sp-data-explorer-add-visualization-dialog-component',
  templateUrl: './data-explorer-add-visualization-dialog.component.html',
  styleUrls: ['./data-explorer-add-visualization-dialog.component.css']
})
export class DataExplorerAddVisualizationDialogComponent implements OnInit {


  constructor(
    public dialogRef: MatDialogRef<DataExplorerAddVisualizationDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private dataViewDataExplorerService: DataViewDataExplorerService,
    public elementIconText: ElementIconText) {
  }

  pages = [{
    type: 'select-pipeline',
    title: 'Select Pipeline',
    description: 'Select a pipeline you\'d like to visualize'
  }, {
    type: 'select-widget',
    title: 'Select Widget',
    description: 'Select widget'
  }];

  visualizableData: InfoResult[] = [];
  availableWidgets: string[];

  selectedDataSet: InfoResult;
  selectedWidget: string;

  dashboard: IDataViewDashboard;

  selectedType: any;
  page: any = 'select-pipeline';
  dialogTitle: string;

  static getSelectedCss(selected, current) {
    if (selected === current) {
      return 'wizard-preview wizard-preview-selected';
    } else {
      return 'wizard-preview';
    }
  }

  ngOnInit() {
    if (!this.data) {
      this.dialogTitle = 'Add widget';
      this.dataViewDataExplorerService.getVisualizableData().subscribe(visualizations => {
        this.visualizableData = visualizations;
      });
      this.availableWidgets = DataExplorerWidgetRegistry.getAvailableWidgetTemplates();
    } else {
      this.dialogTitle = 'Edit widget';
      // this.selectedDataSet = this.data.widget.dashboardWidgetDataConfig;
      // TODO
      //   this.selectedWidget = this.data.widget;
      this.page = 'configure-widget';
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  getSelectedPipelineCss(vis) {
    return DataExplorerAddVisualizationDialogComponent.getSelectedCss(this.selectedDataSet, vis);
  }

  getSelectedVisTypeCss(type) {
    return DataExplorerAddVisualizationDialogComponent.getSelectedCss(this.selectedDataSet, type);
  }

  getTabCss(page) {
    if (page === this.page) { return 'md-fab md-accent'; } else { return 'md-fab md-accent wizard-inactive'; }
  }

  selectPipeline(ds) {
    this.selectedDataSet = ds;
    this.next();
  }

  selectWidget(widget) {
    this.selectedWidget = widget;
    this.next();
  }

  findRequirement(requiredSchema: EventSchema, internalName: string) {
    return requiredSchema.eventProperties.find(ep => ep.runtimeName === internalName);
  }

  next() {
    if (this.page === 'select-pipeline') {
      this.page = 'select-widget';
    } else if (this.page === 'select-widget') {
      const configuredWidget: DataExplorerWidgetModel = new DataExplorerWidgetModel();

      // TODO find solution for event schema
      // configuredWidget.eventSchema = new EventSchema();
      // configuredWidget.eventSchema.eventProperties = this.selectedDataSet.eventSchema.eventProperties;
      // for (const ep of this.selectedDataSet.eventSchema.eventProperties) {
      //       configuredWidget.eventSchema.eventProperties.push(ep.copy());
      //  }

      configuredWidget.measureName = this.selectedDataSet.measureName;
      configuredWidget.widgetType = this.selectedWidget;
      this.dataViewDataExplorerService.saveWidget(configuredWidget).subscribe(response => {
        this.dialogRef.close(response);
      });

      // if (!this.data) {
      //     this.dashboardService.saveWidget(configuredWidget).subscribe(response => {
      //         this.dialogRef.close(response);
      //     });
      // } else {
      //     configuredWidget._id = this.data.widget._id;
      //     configuredWidget._ref = this.data.widget._ref;
      //     configuredWidget.widgetId = this.data.widget.widgetId;
      //     this.dialogRef.close(configuredWidget);
      // }
    }
  }

  back() {
    if (this.page === 'select-widget') {
      this.page = 'select-pipeline';
    }
  }

  iconText(s) {
    return this.elementIconText.getElementIconText(s);
  }

}
