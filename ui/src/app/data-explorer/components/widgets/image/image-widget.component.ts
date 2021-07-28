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
import { DataResult } from '../../../../core-model/datalake/DataResult';
import { DatalakeRestService } from '../../../../core-services/datalake/datalake-rest.service';
import { BaseDataExplorerWidget } from '../base/base-data-explorer-widget';
import { MatDialog } from '@angular/material/dialog';
import { EventPropertyUnion, EventSchema } from '../../../../core-model/gen/streampipes-model';

@Component({
  selector: 'sp-data-explorer-image-widget',
  templateUrl: './image-widget.component.html',
  styleUrls: ['./image-widget.component.css']
})
export class ImageWidgetComponent extends BaseDataExplorerWidget implements OnInit, OnDestroy {

  @ViewChild(MatSort, {static: true}) sort: MatSort;

  availableColumns: EventPropertyUnion[];
  selectedColumn: EventPropertyUnion;

  canvasHeight;
  canvasWidth;
  imagePreviewHeight;

  public imagesRoutes = [];

  constructor(
    protected dataLakeRestService: DatalakeRestService,
    protected dialog: MatDialog) {
    super(dataLakeRestService, dialog);
  }

  ngOnInit(): void {
    this.canvasHeight = this.gridsterItemComponent.height - 240;
    this.canvasWidth = this.gridsterItemComponent.width - 20;
    this.imagePreviewHeight = this.gridsterItemComponent.width / 14;

    this.availableColumns = this.getImageProperties(this.dataLakeMeasure.eventSchema);
    this.selectedColumn = this.availableColumns[0];
    this.updateData();
  }

  getImageProperties(eventSchema: EventSchema): EventPropertyUnion[] {
    return eventSchema.eventProperties.filter(ep => ep.domainProperties.some(dp => dp === 'https://image.com'));
  }

  updateData() {
    this.setShownComponents(false, false, true);

    this.dataLakeRestService.getDataAutoAggregation(
        this.dataLakeMeasure.measureName, this.viewDateRange.startDate.getTime(), this.viewDateRange.endDate.getTime())
        .subscribe(
            (res: DataResult) => {
             // this.availableImageData = res;
             this.showIsLoadingData = false;
              this.imagesRoutes = [];
              if (res.rows !== null) {
                const imageField = res.headers.findIndex(name => name === this.selectedColumn.runtimeName);
                res.rows.forEach(row => {
                  this.imagesRoutes.push(row[imageField]);
                });
              }
            }
        );
  }

  ngOnDestroy(): void {

  }
}
