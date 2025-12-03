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
import {
    DatalakeRestService,
    ExportProviderService,
} from '@streampipes/platform-services';
import { TranslateService } from '@ngx-translate/core';
import { finalize } from 'rxjs';

@Component({
    selector: 'sp-data-retention-now-dialog',
    templateUrl: './data-retention-now-dialog.component.html',
    standalone: false,
})
export class DataRetentionNowDialogComponent implements OnInit {
    @Input()
    measurementIndex: string;

    datalakeRestService = inject(DatalakeRestService);
    private dialogRef = inject(DialogRef<DataRetentionNowDialogComponent>);
    private translateService = inject(TranslateService);

    isInProgress = true;
    currentStatus: string = '';
    errorMessage = '';
    isError = false;
    message = '';
    filePath = '';

    ngOnInit(): void {
        this.isInProgress = true;
        this.isError = false;

        this.datalakeRestService
            .runCleanupNow(this.measurementIndex)
            .pipe(finalize(() => (this.isInProgress = false)))
            .subscribe(
                data => {
                    this.isError = false;
                    this.currentStatus = this.translateService.instant(
                        'Sync was successful.',
                    );
                },
                errorMessage => {
                    this.isError = true;
                    this.errorMessage = errorMessage.error;
                    this.currentStatus = this.translateService.instant(
                        'Sync was not successful',
                    );
                },
            );
    }

    close(refreshDataLakeIndex: boolean) {
        this.dialogRef.close(refreshDataLakeIndex);
    }
}
