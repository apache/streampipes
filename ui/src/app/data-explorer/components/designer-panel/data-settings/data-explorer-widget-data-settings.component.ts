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

import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  DataExplorerWidgetModel,
  DataLakeMeasure
} from '../../../../core-model/gen/streampipes-model';
import { DataViewDataExplorerService } from '../../../services/data-view-data-explorer.service';
import { MatSelectChange } from '@angular/material/select';
import { Tuple2 } from '../../../../core-model/base/Tuple2';
import { DatalakeRestService } from '../../../../platform-services/apis/datalake-rest.service';

@Component({
  selector: 'sp-data-explorer-widget-data-settings',
  templateUrl: './data-explorer-widget-data-settings.component.html',
  styleUrls: ['./data-explorer-widget-data-settings.component.scss']
})
export class DataExplorerWidgetDataSettingsComponent implements OnInit {

  @Input() currentlyConfiguredWidget: DataExplorerWidgetModel;
  @Input() dataLakeMeasure: DataLakeMeasure;
  @Input() newWidgetMode: boolean;

  @Output() createWidgetEmitter: EventEmitter<Tuple2<DataLakeMeasure, DataExplorerWidgetModel>> =
    new EventEmitter<Tuple2<DataLakeMeasure, DataExplorerWidgetModel>>();
  @Output() dataLakeMeasureChange: EventEmitter<DataLakeMeasure> = new EventEmitter<DataLakeMeasure>();
  @Output() configureVisualizationEmitter: EventEmitter<void> = new EventEmitter<void>();

  availablePipelines: DataLakeMeasure[];
  availableMeasurements: DataLakeMeasure[];

  sourceSelection: 'pipeline' | 'measurement' = 'pipeline';

  constructor(private dataExplorerService: DataViewDataExplorerService,
              private datalakeRestService: DatalakeRestService) {

  }

  ngOnInit(): void {
    this.loadAvailablePipelines();
    this.loadAvailableMeasurements();
  }

  loadAvailablePipelines() {
    this.dataExplorerService.getAllPersistedDataStreams().subscribe(response => {
      this.availablePipelines = response;
    });
  }

  loadAvailableMeasurements() {
    this.datalakeRestService.getAllMeasurementSeries().subscribe(response => {
      this.availableMeasurements = response;
    });
  }

  updateMeasure(event: MatSelectChange) {
    if (event.value !== this.dataLakeMeasure.measureName) {
      this.dataLakeMeasure = this.findMeasure(event.value);
      this.dataLakeMeasureChange.emit(this.dataLakeMeasure);
    }
  }

  findMeasure(measureName) {
    return this.availablePipelines.find(pipeline => pipeline.measureName === measureName) ||
      this.availableMeasurements.find(m => m.measureName === measureName);
  }

  createWidget() {
    this.createWidgetEmitter.emit({ a: this.dataLakeMeasure, b: this.currentlyConfiguredWidget });
  }

}
