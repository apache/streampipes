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

import { Component, Input } from '@angular/core';
import {
    DataExplorerDataConfig,
    RetentionTimeConfig,
} from '@streampipes/platform-services';
import {
    FormFieldComponent,
    SplitSectionComponent,
} from '@streampipes/shared-ui';
import { MatFormField, MatSuffix } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-select-data-retention',
    templateUrl: './select-data-retention.component.html',
    styleUrls: [
        './select-data-retention.component.scss',
        // '../select-data.component.scss',
    ],
    imports: [
        MatFormField,
        MatInput,
        FormsModule,
        MatSuffix,
        TranslatePipe,
        SplitSectionComponent,
        FormFieldComponent,
    ],
})
export class SelectDataRetentionComponent {
    @Input() dataExplorerDataConfig: DataExplorerDataConfig;
    @Input() dataRetentionConfig: RetentionTimeConfig;
}
