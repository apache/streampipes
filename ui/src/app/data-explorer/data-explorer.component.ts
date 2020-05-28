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
import { FormControl } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Observable } from 'rxjs/Observable';
import { map, startWith } from 'rxjs/operators';
import { DataLakeMeasure } from '../core-model/datalake/DataLakeMeasure';
import { DatalakeRestService } from '../core-services/datalake/datalake-rest.service';

@Component({
    selector: 'sp-data-explorer',
    templateUrl: './data-explorer.component.html',
    styleUrls: ['./data-explorer.css']
})
export class DataExplorerComponent implements OnInit {

    myControl = new FormControl();
    infoResult: DataLakeMeasure[];
    filteredIndexInfos: Observable<DataLakeMeasure[]>;

    page = 0;
    selectedInfoResult: DataLakeMeasure = undefined;

    downloadFormat = 'csv';
    isDownloading = false;

    constructor(private restService: DatalakeRestService, private snackBar: MatSnackBar) {

    }

    ngOnInit(): void {
        this.restService.getAllInfos().subscribe(res => {
                this.infoResult = res;
                this.filteredIndexInfos = this.myControl.valueChanges
                    .pipe(
                        startWith(''),
                        map(value => this._filter(value))
                    );
            }
        );
    }

    selectIndex(index: string) {
        this.selectedInfoResult = this._filter(index)[0];
    }

    _filter(value: string): DataLakeMeasure[] {
        const filterValue = value.toLowerCase();

        return this.infoResult.filter(option => option.measureName.toLowerCase().includes(filterValue));
    }

    openSnackBar(message: string) {
        this.snackBar.open(message, 'Close', {
            duration: 2000,
        });
    }
}
