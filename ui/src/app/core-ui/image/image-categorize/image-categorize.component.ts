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

import { AfterViewInit, Component, OnInit } from "@angular/core";
import { MatSnackBar } from '@angular/material/snack-bar';
import { DatalakeRestService } from '../../../core-services/datalake/datalake-rest.service';
import { ColorService } from "../services/color.service";


@Component({
  selector: 'sp-image-categorize',
  templateUrl: './image-categorize.component.html',
  styleUrls: ['./image-categorize.component.css']
})
export class ImageCategorizeComponent implements OnInit, AfterViewInit {

  // label
  public labels;
  public selectedLabels;

  // images
  public imagesSrcs;
  public imagesIndex: number;

  constructor(private restService: DatalakeRestService, public colorService: ColorService, private snackBar: MatSnackBar) { }

  ngOnInit(): void {
    this.selectedLabels = [];

    // 1. get labels
    this.labels = this.restService.getLabels();

    // 2. get Images
    this.imagesSrcs = this.restService.getImageSrcs();
  }


  ngAfterViewInit(): void {
    this.selectedLabels = [];
    this.imagesIndex = 0;
  }

  /* sp-image-view handler */


  /* sp-image-labels handler */
  handleLabelChange(label: {category, label}) {
    this.selectedLabels.push(label);
  }

  /* sp-image-bar */
  handleImageIndexChange(index) {
    this.save();
    this.selectedLabels = [];
    this.imagesIndex = index;
  }
  handleImagePageUp(e) {
    this.save();
    this.selectedLabels = [];
    alert('Page Up - Load new data');
  }

  handleImagePageDown(e) {
    this.save();
    this.selectedLabels = [];
    alert('Page Down - Load new data');
  }

  remove(label) {
    this.selectedLabels = this.selectedLabels.filter(l => l.label !== label.label);
  }

  private save() {
    // TODO
    this.openSnackBar('TODO: Save save class');
  }

  private openSnackBar(message: string) {
    this.snackBar.open(message, '', {
      duration: 2000,
      verticalPosition: 'top',
      horizontalPosition: 'right'
    });
  }


}
