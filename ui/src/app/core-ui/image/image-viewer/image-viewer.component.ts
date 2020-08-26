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

import { Component, Input, OnInit } from '@angular/core';
import { DatalakeRestService } from '../../../core-services/datalake/datalake-rest.service';

@Component({
  selector: 'sp-image-viewer',
  templateUrl: './image-viewer.component.html',
  styleUrls: ['./image-viewer.component.css']
})
export class ImageViewerComponent implements OnInit {

  @Input()
  public imagesRoutes;

  public imagesIndex = 0;
  // public imagesIndex: number;

  // @Input()
  // measureName; // TODO: Remove hard coded Index, should be injected

  // eventSchema = undefined; // TODO: event schema should be also injected
  // imageField = undefined;
  // pageIndex = undefined;
  // pageSum = undefined;

  // Flags
  // private setImagesIndexToFirst = false;
  // private setImagesIndexToLast = false;

  constructor() {}

  ngOnInit(): void {
    // this.imagesSrcs = [];
    // this.imagesRoutes.forEach(route => {
    //   this.imagesSrcs.push(this.restService.getImageUrl(route));
    // });
  }

  // /* sp-image-bar */
  handleImageIndexChange(index) {
    this.imagesIndex = index;
  }
  // handleImagePageUp(e) {
  //   this.pageIndex += 1;
  //   this.setImagesIndexToLast = true;
  //   this.loadData();
  // }
  //
  // handleImagePageDown(e) {
  //   if (this.pageIndex - 1 >= 0) {
  //     this.pageIndex -= 1;
  //     this.setImagesIndexToFirst = true;
  //     this.loadData();
  //   }
  // }

}
