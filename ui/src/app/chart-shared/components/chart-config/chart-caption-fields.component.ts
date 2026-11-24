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

import { Component, input, output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { TranslatePipe } from '@ngx-translate/core';
import { FormFieldComponent } from '@streampipes/shared-ui';

@Component({
    selector: 'sp-chart-caption-fields',
    host: { class: 'flex-col gap-sm' },
    imports: [
        FormsModule,
        MatFormField,
        MatInput,
        TranslatePipe,
        FormFieldComponent,
    ],
    template: `
        <sp-form-field [level]="3" [label]="'Title' | translate">
            <mat-form-field class="flex-100">
                <input
                    [attr.data-cy]="
                        'data-explorer-' + chartKey() + '-title-input'
                    "
                    [attr.aria-label]="'Title' | translate"
                    matInput
                    type="text"
                    [(ngModel)]="caption().title"
                    (ngModelChange)="captionChange.emit()"
                />
            </mat-form-field>
        </sp-form-field>
        <sp-form-field [level]="3" [label]="'Description' | translate">
            <mat-form-field class="flex-100">
                <textarea
                    [attr.data-cy]="
                        'data-explorer-' + chartKey() + '-description-input'
                    "
                    [attr.aria-label]="'Description' | translate"
                    matInput
                    rows="3"
                    [(ngModel)]="caption().description"
                    (ngModelChange)="captionChange.emit()"
                ></textarea>
            </mat-form-field>
        </sp-form-field>
    `,
})
export class ChartCaptionFieldsComponent {
    readonly caption = input.required<{
        title?: string;
        description?: string;
    }>();
    readonly chartKey = input.required<string>();
    readonly captionChange = output<void>();
}
