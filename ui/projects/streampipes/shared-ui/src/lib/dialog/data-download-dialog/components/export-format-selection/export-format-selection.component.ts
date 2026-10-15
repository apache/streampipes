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

import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { TranslatePipe } from '@ngx-translate/core';
import { SplitSectionComponent } from '../../../../components/split-section/split-section.component';

export interface BasicFormatExportConfig {
    format: 'json' | 'csv' | 'xlsx';
    delimiter?: 'comma' | 'semicolon';
}

@Component({
    selector: 'sp-export-format-selection',
    templateUrl: './export-format-selection.component.html',
    styleUrls: ['./export-format-selection.component.scss'],
    changeDetection: ChangeDetectionStrategy.Eager,
    imports: [
        FormsModule,
        MatRadioGroup,
        MatRadioButton,
        SplitSectionComponent,
        TranslatePipe,
    ],
})
export class ExportFormatSelectionComponent {
    @Input({ required: true }) formatExportConfig: BasicFormatExportConfig;
    @Input() dataCyPrefix = 'download-configuration';
    @Input() delimiterDataCyPrefix = this.dataCyPrefix;
}
