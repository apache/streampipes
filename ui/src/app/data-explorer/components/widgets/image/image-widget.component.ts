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

import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatSort } from '@angular/material/sort';
import { BaseDataExplorerWidget } from '../base/base-data-explorer-widget';
import { EventPropertyUnion, EventSchema, SpQueryResult } from '../../../../core-model/gen/streampipes-model';
import { ImageWidgetModel } from './model/image-widget.model';
import { DatalakeQueryParameters } from '../../../../core-services/datalake/DatalakeQueryParameters';
import { DatalakeQueryParameterBuilder } from '../../../../core-services/datalake/DatalakeQueryParameterBuilder';
import { DataExplorerField } from '../../../models/dataview-dashboard.model';

@Component({
  selector: 'sp-data-explorer-image-widget',
  templateUrl: './image-widget.component.html',
  styleUrls: ['./image-widget.component.css']
})
export class ImageWidgetComponent extends BaseDataExplorerWidget<ImageWidgetModel> implements OnInit, OnDestroy {

  @ViewChild(MatSort, { static: true }) sort: MatSort;

  availableColumns: EventPropertyUnion[];
  selectedColumn: EventPropertyUnion;

  canvasHeight;
  canvasWidth;
  imagePreviewHeight;

  public imagesRoutes = [];

  ngOnInit(): void {
    this.canvasHeight = this.gridsterItemComponent.height - 240;
    this.canvasWidth = this.gridsterItemComponent.width - 20;
    this.imagePreviewHeight = this.gridsterItemComponent.width / 14;

    this.availableColumns = this.getImageProperties(this.dataExplorerWidget.dataConfig.sourceConfigs[0].measure.eventSchema);
    this.selectedColumn = this.availableColumns[0];
    this.updateData();
  }

  getImageProperties(eventSchema: EventSchema): EventPropertyUnion[] {
    return eventSchema.eventProperties.filter(ep => ep.domainProperties.some(dp => dp === 'https://image.com'));
  }

  ngOnDestroy(): void {

  }

  refreshData() {
    this.setShownComponents(false, false, true);

    this.dataLakeRestService.getData(
      this.dataExplorerWidget.dataConfig.sourceConfigs[0].measureName, this.buildQuery())
      .subscribe(
        (res: SpQueryResult) => {
          // this.availableImageData = res;
          this.showIsLoadingData = false;
          this.imagesRoutes = [];
          if (res.allDataSeries[0].rows !== null) {
            const imageField = res.headers.findIndex(name => name === this.selectedColumn.runtimeName);
            res.allDataSeries[0].rows.forEach(row => {
              this.imagesRoutes.push(row[imageField]);
            });
          }
        }
      );
  }

  refreshView() {
  }

  buildQuery(): DatalakeQueryParameters {
    return DatalakeQueryParameterBuilder.create(this.timeSettings.startTime, this.timeSettings.endTime).build();
  }

  onResize(width: number, height: number) {
  }

  beforeDataFetched() {
  }

  onDataReceived(spQueryResult: SpQueryResult[]) {
  }

  handleUpdatedFields(addedFields: DataExplorerField[], removedFields: DataExplorerField[]) {
  }
}
