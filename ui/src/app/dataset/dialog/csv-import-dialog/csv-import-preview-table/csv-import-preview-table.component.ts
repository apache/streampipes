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
import { Component, computed, input, output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatOption, MatSelect } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';
import { SemanticType } from '@streampipes/platform-services';
import { SpAlertBannerComponent } from '@streampipes/shared-ui';
import {
    CsvImportColumnModel,
    CsvImportColumnRole,
    CsvImportColumnRoleChange,
    CsvImportColumnTypeChange,
} from '../csv-import.model';

@Component({
    selector: 'sp-csv-import-preview-table',
    templateUrl: './csv-import-preview-table.component.html',
    styleUrls: ['./csv-import-preview-table.component.scss'],
    imports: [
        CommonModule,
        FormsModule,
        MatFormField,
        MatInput,
        MatSelect,
        MatOption,
        TranslatePipe,
        SpAlertBannerComponent,
    ],
})
export class CsvImportPreviewTableComponent {
    readonly hasPreview = input(false);
    readonly isExisting = input('NEW');
    readonly previewRows = input<string[][]>([]);
    readonly columnModels = input<CsvImportColumnModel[]>([]);
    readonly timestampFormat = input('');
    readonly hasSchemaMismatch = input(false);
    readonly schemaMismatchSummary = input('');
    readonly schemaMismatchDetails = input<string[]>([]);
    readonly showTimestampWarning = input(false);

    readonly columnTypeChange = output<CsvImportColumnTypeChange>();
    readonly columnRoleChange = output<CsvImportColumnRoleChange>();
    readonly timestampFormatChange = output<string>();

    private readonly selectedTimestampColumnModel = computed(() =>
        this.columnModels().find(model => this.isTimestampColumn(model)),
    );

    isTimestampColumn(model: CsvImportColumnModel): boolean {
        return SemanticType.isTimestamp(model.eventProperty);
    }

    isTimestampSelectionDisabled(model: CsvImportColumnModel): boolean {
        return (
            !!this.selectedTimestampColumnModel() &&
            !this.isTimestampColumn(model)
        );
    }

    getColumnRole(model: CsvImportColumnModel): CsvImportColumnRole {
        if (this.isTimestampColumn(model)) {
            return 'TIMESTAMP';
        } else if (model.column.propertyScope === 'DIMENSION_PROPERTY') {
            return 'DIMENSION_PROPERTY';
        }

        return 'MEASUREMENT_PROPERTY';
    }

    onColumnTypeChange(
        model: CsvImportColumnModel,
        type: CsvImportColumnTypeChange['type'],
    ): void {
        this.columnTypeChange.emit({ model, type });
    }

    onColumnRoleChange(
        model: CsvImportColumnModel,
        role: CsvImportColumnRole,
    ): void {
        this.columnRoleChange.emit({ model, role });
    }
}
