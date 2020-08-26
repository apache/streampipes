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

import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {DatalakeRestService} from "../core-services/datalake/datalake-rest.service";
import {DataLakeMeasure} from "../core-model/gen/streampipes-model";
import { CocoFormat } from '../core-model/coco/Coco.format';

@Component({
    selector: 'app-image-labeling',
    templateUrl: './app-image-labeling.component.html',
    styleUrls: ['./app-image-labeling.component.css']
})
export class AppImageLabelingComponent implements  OnInit {

  public imagesSrcs = [];

  dataLakeMeasures: DataLakeMeasure[] = [];
  selectedMeasure: DataLakeMeasure;

  @Output() appOpened = new EventEmitter<boolean>();

  public pageIndex;
  public measureName;

  constructor(private restService: DatalakeRestService) {

  }

  ngOnInit() {
      this.appOpened.emit(true);
        this.restService.getAllInfos().subscribe(res => {
              this.dataLakeMeasures = res;
              this.selectedMeasure = res[0];
          }
        );
  }


  // loadData() {
  //   if (this.pageIndex === undefined) {
  //     this.restService.getDataPageWithoutPage(this.measureName, 10).subscribe(
  //       res => this.processData(res)
  //     );
  //   } else {
  //     this.restService.getDataPage(this.measureName, 10, this.pageIndex).subscribe(
  //       res => this.processData(res)
  //     );
  //   }
  // }

  // processData(pageResult) {
  //   if (pageResult.rows === undefined) {
  //     this.pageIndex = pageResult.pageSum - 1;
  //     this.openSnackBar('No new data found');
  //   } else {
  //     pageResult.rows = pageResult.rows.reverse();
  //     this.pageIndex = pageResult.page;
  //     this.pageSum = pageResult.pageSum;
  //
  //     if (this.setImagesIndexToFirst) {
  //       this.imagesIndex = 0;
  //     } else if (this.setImagesIndexToLast) {
  //       this.imagesIndex = pageResult.rows.length - 1;
  //     }
  //     this.setImagesIndexToLast = false;
  //     this.setImagesIndexToFirst = false;
  //
  //     const imageIndex = pageResult.headers.findIndex(name => name === this.imageField.runtimeName);
  //     const tmp = [];
  //     this.cocoFiles = [];
  //     pageResult.rows.forEach(row => {
  //       tmp.push(this.restService.getImageUrl(row[imageIndex]));
  //
  //       // This is relevant for coco
  //       this.restService.getCocoFileForImage(row[imageIndex]).subscribe(
  //         coco => {
  //           if (coco === null) {
  //             const cocoFile = new CocoFormat();
  //             this.cocoFormatService.addImage(cocoFile, (row[imageIndex]));
  //             this.cocoFiles.push(cocoFile);
  //           } else {
  //             this.cocoFiles.push(coco as CocoFormat);
  //           }
  //         }
  //       );
  //
  //     });
  //     this.imagesSrcs = tmp;
  //   }
  // }

}
