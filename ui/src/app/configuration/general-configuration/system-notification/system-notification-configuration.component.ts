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
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import {
    DateInputComponent,
    FormFieldComponent,
    SpAlertBannerComponent,
    SplitSectionComponent,
} from '@streampipes/shared-ui';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatOption, MatSelect } from '@angular/material/select';
import { MatButton } from '@angular/material/button';
import { FlexDirective } from '@ngbracket/ngx-layout/flex';
import { TranslatePipe } from '@ngx-translate/core';
import { isValid } from 'date-fns';

@Component({
    selector: 'sp-system-notification-configuration',
    templateUrl: './system-notification-configuration.component.html',
    imports: [
        SplitSectionComponent,
        ReactiveFormsModule,
        FormFieldComponent,
        SpAlertBannerComponent,
        DateInputComponent,
        MatCheckbox,
        MatFormField,
        MatInput,
        MatSelect,
        MatOption,
        MatButton,
        FlexDirective,
        TranslatePipe,
    ],
})
export class SpSystemNotificationConfigurationComponent {
    @Input()
    parentForm: FormGroup;

    get expiryDate(): Date {
        return this.expiresAtMillis
            ? new Date(this.expiresAtMillis)
            : undefined;
    }

    set expiryDate(date: Date) {
        // sp-date-input reports an invalid date when its field is cleared
        this.parentForm
            .get('notificationExpiresAtMillis')
            .setValue(isValid(date) ? date.getTime() : undefined);
    }

    get expired(): boolean {
        return !!this.expiresAtMillis && this.expiresAtMillis <= Date.now();
    }

    clearExpiry(): void {
        this.expiryDate = undefined;
    }

    private get expiresAtMillis(): number {
        return this.parentForm.get('notificationExpiresAtMillis').value;
    }
}
