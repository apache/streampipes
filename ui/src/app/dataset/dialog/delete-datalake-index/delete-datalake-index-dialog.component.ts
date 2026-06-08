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

import { Component, inject, Input, OnInit } from '@angular/core';
import { DialogRef } from '@streampipes/shared-ui';
import { DatalakeRestService } from '@streampipes/platform-services';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatButton } from '@angular/material/button';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatDivider } from '@angular/material/divider';

@Component({
    selector: 'sp-delete-datalake-index-dialog',
    templateUrl: './delete-datalake-index-dialog.component.html',
    imports: [
        LayoutDirective,
        FlexDirective,
        LayoutAlignDirective,
        MatButton,
        MatProgressSpinner,
        MatDivider,
        TranslatePipe,
    ],
})
export class DeleteDatalakeIndexComponent implements OnInit {
    @Input()
    measurementIndex: string;

    @Input()
    deleteDialog: boolean;

    isInProgress = false;
    currentStatus: any;

    private dialogRef = inject(DialogRef<DeleteDatalakeIndexComponent>);
    private datalakeRestService = inject(DatalakeRestService);
    private translateService = inject(TranslateService);

    confirmDeleteMessage = '';
    confirmTruncateMessage = '';

    ngOnInit() {
        this.confirmDeleteMessage = this.translateService.instant(
            'Do you really want to delete the dataset {{index}}?',
            { index: this.measurementIndex },
        );
        this.confirmTruncateMessage = this.translateService.instant(
            'Do you really want to truncate the data in {{index}}?',
            { index: this.measurementIndex },
        );
    }

    close(refreshDataLakeIndex: boolean) {
        this.dialogRef.close(refreshDataLakeIndex);
    }

    truncateData() {
        this.isInProgress = true;
        this.currentStatus =
            this.translateService.instant('Truncating data...');
        this.datalakeRestService
            .removeData(this.measurementIndex)
            .subscribe(_data => {
                this.close(true);
            });
    }

    deleteData() {
        this.isInProgress = true;
        this.currentStatus = this.translateService.instant('Deleting data...');

        // this.datalakeRestService.dropSingleMeasurementSeries(measurmentIndex);
        this.datalakeRestService
            .dropSingleMeasurementSeries(this.measurementIndex)
            .subscribe(_data => {
                this.close(true);
            });
    }
}
