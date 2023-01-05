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

import { Component, Input } from '@angular/core';
import { DialogRef } from '@streampipes/shared-ui';
import { DatalakeRestService } from '@streampipes/platform-services';

@Component({
    selector: 'sp-delete-datalake-index-dialog',
    templateUrl: './delete-datalake-index-dialog.component.html',
    styleUrls: ['./delete-datalake-index-dialog.component.scss'],
})
export class DeleteDatalakeIndexComponent {
    @Input()
    measurementIndex: string;

    @Input()
    deleteDialog: boolean;

    isInProgress = false;
    currentStatus: any;

    constructor(
        private dialogRef: DialogRef<DeleteDatalakeIndexComponent>,
        private datalakeRestService: DatalakeRestService,
    ) {}

    close(refreshDataLakeIndex: boolean) {
        this.dialogRef.close(refreshDataLakeIndex);
    }

    truncateData() {
        this.isInProgress = true;
        this.currentStatus = 'Truncating data...';
        this.datalakeRestService
            .removeData(this.measurementIndex)
            .subscribe(data => {
                this.close(true);
            });
    }

    deleteData() {
        this.isInProgress = true;
        this.currentStatus = 'Deleting data...';

        // this.datalakeRestService.dropSingleMeasurementSeries(measurmentIndex);
        this.datalakeRestService
            .dropSingleMeasurementSeries(this.measurementIndex)
            .subscribe(data => {
                this.close(true);
            });
    }
}
