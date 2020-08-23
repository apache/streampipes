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

@Component({
    selector: 'app-image-labeling',
    templateUrl: './app-image-labeling.component.html',
    styleUrls: ['./app-image-labeling.component.css']
})
export class AppImageLabelingComponent implements  OnInit {


  dataLakeMeasures: DataLakeMeasure[] = [];
  selectedMeasure: DataLakeMeasure;

  @Output() appOpened = new EventEmitter<boolean>();

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
}
