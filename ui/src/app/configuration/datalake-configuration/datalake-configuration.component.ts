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

import { Component, OnInit } from '@angular/core';
import { DatalakeRestService } from '../../core-services/datalake/datalake-rest.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'sp-datalake-configuration',
  templateUrl: './datalake-configuration.component.html',
  styleUrls: ['./datalake-configuration.component.css']
})
export class DatalakeConfigurationComponent implements OnInit {

  constructor(
    protected dataLakeRestService: DatalakeRestService,
    private snackBar: MatSnackBar) { }

  ngOnInit(): void {
  }

  removeDataFromDataLake(): void {
    console.log('Delete');
    this.dataLakeRestService.removeAllData().subscribe(
      res => {
        let message = '';

        if (res) {
          message = 'Data successfully deleted!';
        } else {
          message = 'There was a problem when deleting the data!';
        }

        this.snackBar.open(message, '', {
          duration: 2000,
          verticalPosition: 'top',
          horizontalPosition: 'right'
        });
      });
  }

}
