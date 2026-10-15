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
import { SplitSectionComponent } from '@streampipes/shared-ui';
import { TranslatePipe } from '@ngx-translate/core';
import { AuditFormatExportConfig } from '../../model/format-export-config.model';

@Component({
    selector: 'sp-audit-select-format',
    templateUrl: './select-format.component.html',
    styleUrls: ['./select-format.component.scss'],
    changeDetection: ChangeDetectionStrategy.Eager,
    imports: [
        FormsModule,
        MatRadioGroup,
        MatRadioButton,
        SplitSectionComponent,
        TranslatePipe,
    ],
})
export class AuditSelectFormatComponent {
    @Input({ required: true }) formatExportConfig: AuditFormatExportConfig;
}
