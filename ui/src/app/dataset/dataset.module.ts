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
import { NgModule } from '@angular/core';
import { PlatformServicesModule } from '@streampipes/platform-services';
import { CoreUiModule } from '../core-ui/core-ui.module';
import { RouterModule } from '@angular/router';
import { SharedUiModule } from '@streampipes/shared-ui';
import { TranslateModule } from '@ngx-translate/core';
import { DeleteDatalakeIndexComponent } from './dialog/delete-datalake-index/delete-datalake-index-dialog.component';
import { DatalakeConfigurationComponent } from './components/datalake-configuration/datalake-configuration.component';
import { DataRetentionDialogComponent } from './dialog/data-retention-dialog/data-retention-dialog.component';
import { SelectDataComponent } from './dialog/data-retention-dialog/components/select-retention/select-data.component';
import { SelectDataRetentionComponent } from './dialog/data-retention-dialog/components/select-retention/select-data-retention/select-data-retention.component';
import { ExportProviderComponent } from './dialog/export-provider-dialog/export-provider-dialog.component';
import { SelectRetentionActionComponent } from './dialog/data-retention-dialog/components/select-retention/select-retention-action/select-retention-action.component';
import { SelectDataExportComponent } from './dialog/data-retention-dialog/components/select-export/select-format.component';
import { DeleteExportProviderComponent } from './dialog/delete-export-provider/delete-export-provider-dialog.component';
import {
    DefaultLayoutDirective,
    FlexLayoutModule,
} from '@ngbracket/ngx-layout';
import { MatButtonModule, MatIconButton } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDividerModule } from '@angular/material/divider';
import { MatTableModule } from '@angular/material/table';
import { MatSortModule } from '@angular/material/sort';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import {
    MatPaginatorIntl,
    MatPaginatorModule,
} from '@angular/material/paginator';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { DataRetentionNowDialogComponent } from './dialog/data-retention-now-dialog/data-retention-now-dialog.component';
import { DataRetentionLogDialogComponent } from './dialog/data-retention-log-dialog/data-retention-log-dialog.component';
import { ExportProviderConnectionTestComponent } from './dialog/export-provider-connection-test/export-provider-connection-test.component';
import { PaginatorService } from 'projects/streampipes/shared-ui/src/lib/components/sp-table/sp-paginator/sp-paginator.component';

@NgModule({
    imports: [
        CommonModule,
        CoreUiModule,
        PlatformServicesModule,
        SharedUiModule,
        TranslateModule.forChild(),
        RouterModule.forChild([
            {
                path: '',
                children: [
                    {
                        path: '',
                        component: DatalakeConfigurationComponent,
                    },
                ],
            },
        ]),
        FlexLayoutModule,
        MatIconModule,
        MatTooltipModule,
        FormsModule,
        ReactiveFormsModule,
        MatButtonModule,
        MatDividerModule,
        MatTableModule,
        MatSortModule,
        MatProgressSpinnerModule,
        MatFormFieldModule,
        MatRadioModule,
        MatSelectModule,
        MatPaginatorModule,
        MatInputModule,
        MatMenuModule,
    ],
    declarations: [
        DeleteDatalakeIndexComponent,
        DataRetentionNowDialogComponent,
        DataRetentionLogDialogComponent,
        ExportProviderConnectionTestComponent,
        DatalakeConfigurationComponent,
        DataRetentionDialogComponent,
        ExportProviderComponent,
        SelectDataComponent,
        SelectDataRetentionComponent,
        SelectRetentionActionComponent,
        SelectDataExportComponent,
        DeleteExportProviderComponent,
    ],
    providers: [
        { provide: MatPaginatorIntl, useClass: PaginatorService }, // Use custom paginator service
    ],

    exports: [],
})
export class DatasetModule {
    constructor() {}
}
