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

import { CommonModule } from '@angular/common';
import { Component, input } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import {
    CsvImportResult,
    CsvImportValidationMessage,
} from '@streampipes/platform-services';
import { SpAlertBannerComponent } from '@streampipes/shared-ui';

@Component({
    selector: 'sp-csv-import-upload-state',
    templateUrl: './csv-import-upload-state.component.html',
    styleUrls: ['./csv-import-upload-state.component.scss'],
    imports: [
        CommonModule,
        MatIcon,
        MatProgressSpinner,
        TranslatePipe,
        SpAlertBannerComponent,
    ],
})
export class CsvImportUploadStateComponent {
    readonly importLoading = input(false);
    readonly hasImportResult = input(false);
    readonly importResult = input<CsvImportResult | undefined>(undefined);
    readonly uploadErrors = input<CsvImportValidationMessage[]>([]);
}
