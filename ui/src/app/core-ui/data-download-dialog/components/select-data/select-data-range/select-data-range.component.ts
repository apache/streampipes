/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

import { Component, Input, OnInit } from '@angular/core';
import {
    DataExplorerDataConfig,
    DateRange,
} from '@streampipes/platform-services';
import { DataExportConfig } from '../../../model/data-export-config.model';

@Component({
    selector: 'sp-select-data-range',
    templateUrl: './select-data-range.component.html',
    styleUrls: [
        './select-data-range.component.scss',
        '../select-data.component.scss',
    ],
})
export class SelectDataRangeComponent implements OnInit {
    @Input() dataExplorerDataConfig: DataExplorerDataConfig;
    @Input() dataExportConfig: DataExportConfig;

    datePickerSelection: Date[] = [];

    ngOnInit(): void {
        if (!this.dataExportConfig.dateRange) {
            this.initDateSelection();
        }
    }

    initDateSelection() {
        const startDate = new Date();
        startDate.setDate(startDate.getDate() - 5);
        this.datePickerSelection[0] = startDate;
        this.datePickerSelection[1] = new Date();
        this.dataExportConfig.dateRange = new DateRange(
            this.datePickerSelection[0],
            this.datePickerSelection[1],
        );
        this.setDateRangeFromSelection();
    }

    private setDateRangeFromSelection() {
        this.dataExportConfig.dateRange = new DateRange(
            this.datePickerSelection[0],
            this.datePickerSelection[1],
        );
    }
}
