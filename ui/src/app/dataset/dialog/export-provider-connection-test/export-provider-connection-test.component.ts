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
import { ExportProviderService } from '@streampipes/platform-services';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'sp-export-provider-connection-test',
    templateUrl: './export-provider-connection-test.component.html',
    standalone: false,
})
export class ExportProviderConnectionTestComponent implements OnInit {
    ngOnInit(): void {
        this.testExportProvider();
    }
    @Input()
    providerId: string;

    private dialogRef = inject(
        DialogRef<ExportProviderConnectionTestComponent>,
    );
    private exportProviderRestService = inject(ExportProviderService);
    private translateService = inject(TranslateService);

    isInProgress = false;
    currentStatus: string;
    errorMessage = '';
    isError = false;
    message = '';
    filePath = '';

    close(refreshDataLakeIndex: boolean) {
        this.dialogRef.close(refreshDataLakeIndex);
    }

    testExportProvider() {
        this.isInProgress = true;
        this.currentStatus = this.translateService.instant(
            'Testing the connection.',
        );
        this.exportProviderRestService
            .testExportProviderById(this.providerId)
            .subscribe(
                data => {
                    this.isInProgress = false;
                    this.isError = false;
                    this.currentStatus = this.translateService.instant(
                        'Connection was established and test file was successfully saved:',
                    );
                    this.filePath = data.filePath;
                },
                errorMessage => {
                    this.currentStatus = this.translateService.instant(
                        'Connection could not be established.',
                    );
                    this.errorMessage = errorMessage.error;
                    this.isError = true;
                    this.isInProgress = false;
                },
            );
    }
}
