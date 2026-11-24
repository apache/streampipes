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
import { MatButton } from '@angular/material/button';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-field-selection-actions',
    host: { class: 'flex-row flex-wrap gap-sm' },
    imports: [MatButton, TranslatePipe],
    template: `
        <button
            type="button"
            mat-flat-button
            color="accent"
            class="small-button"
            [attr.data-cy]="selectDataCy()"
            [disabled]="disabled()"
            (click)="selectionChange.emit(true)"
        >
            {{ (filtered() ? 'Select matches' : 'Select all') | translate }}
        </button>
        <button
            type="button"
            mat-flat-button
            class="small-button mat-basic"
            [attr.data-cy]="deselectDataCy()"
            [disabled]="disabled()"
            (click)="selectionChange.emit(false)"
        >
            {{ (filtered() ? 'Deselect matches' : 'Deselect all') | translate }}
        </button>
    `,
})
export class FieldSelectionActionsComponent {
    readonly filtered = input(false);
    readonly disabled = input(false);
    readonly selectDataCy = input<string>();
    readonly deselectDataCy = input<string>();
    readonly selectionChange = output<boolean>();
}
