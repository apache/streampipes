/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { Component, OnInit } from '@angular/core';
import { DatalakeRestService } from '../../../core-services/datalake/datalake-rest.service';
import { Observable } from 'rxjs';
import { DataLakeMeasure } from '../../../core-model/datalake/DataLakeMeasure';
import { DataViewDataExplorerService } from '../../../data-explorer/services/data-view-data-explorer.service';
import { TsonLdSerializerService } from '../../../platform-services/tsonld-serializer.service';

@Component({
  selector: 'sp-image-viewer',
  templateUrl: './image-viewer.component.html',
  styleUrls: ['./image-viewer.component.css']
})
export class ImageViewerComponent implements OnInit {

  // images
  public imagesSrcs;
  public imagesIndex: number;

  measureName = 'image'; // TODO: Remove hard coded Index, should be injected
  eventSchema = undefined; // TODO: event schema should be also injected
  imageField = undefined;
  pageIndex = undefined;
  pageSum = undefined;

  // Flags
  private setImagesIndexToFirst = false;
  private setImagesIndexToLast = false;

  constructor(private restService: DatalakeRestService, private tsonLdSerializerService: TsonLdSerializerService) { }

  ngOnInit(): void {

    // getVisualizableData(): Observable<DataLakeMeasure[]> {
    //   return this.dataLakeRestService.getAllInfos().map(data => {
    //     return this.tsonLdSerializerService.fromJsonLdContainer(data, 'sp:DataLakeMeasure');
    //   });
    // }

    this.restService.getAllInfos().map(data => {
      return this.tsonLdSerializerService.fromJsonLdContainer(data, 'sp:DataLakeMeasure');
    }).subscribe(
      res => {
        this.eventSchema = res.find(elem => elem.measureName === this.measureName).eventSchema;
        const properties = this.eventSchema.eventProperties;
        for (const prop of properties) {
          // if (prop.domainProperties.find(type => type === 'https://image.com')) {
          if (prop.domainProperty === 'https://image.com') {
            this.imageField = prop;
            break;
          }
        }
        this.loadData();
      }
    );

    this.imagesIndex = 0;
  }

  loadData() {
    if (this.pageIndex === undefined) {
      this.restService.getDataPageWithoutPage(this.measureName, 10).subscribe(
        res => this.processData(res)
      );
    } else {
      this.restService.getDataPage(this.measureName, 10, this.pageIndex).subscribe(
        res => this.processData(res)
      );
    }
  }

  processData(pageResult) {
    if (pageResult.rows === undefined) {
      this.pageIndex = pageResult.pageSum - 1;
    } else {
      pageResult.rows = pageResult.rows.reverse();
      this.pageIndex = pageResult.page;
      this.pageSum = pageResult.pageSum;

      if (this.setImagesIndexToFirst) {
        this.imagesIndex = 0;
      } else if (this.setImagesIndexToLast) {
        this.imagesIndex = pageResult.rows.length - 1;
      }
      this.setImagesIndexToLast = false;
      this.setImagesIndexToFirst = false;

      const imageIndex = pageResult.headers.findIndex(name => name === this.imageField.runtimeName);
      const tmp = [];
      pageResult.rows.forEach(row => {
        tmp.push(this.restService.getImageUrl(row[imageIndex]));
      });
      this.imagesSrcs = tmp;
    }
  }

  /* sp-image-bar */
  handleImageIndexChange(index) {
    this.imagesIndex = index;
  }
  handleImagePageUp(e) {
    this.pageIndex += 1;
    this.setImagesIndexToLast = true;
    this.loadData();
  }

  handleImagePageDown(e) {
    if (this.pageIndex - 1 >= 0) {
      this.pageIndex -= 1;
      this.setImagesIndexToFirst = true;
      this.loadData();
    }
  }

}
