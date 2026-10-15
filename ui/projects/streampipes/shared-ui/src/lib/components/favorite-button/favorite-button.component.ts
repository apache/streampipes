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
import { MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';

@Component({
    selector: 'sp-favorite-button',
    imports: [MatIconButton, MatIcon, MatTooltip],
    template: `
        <button
            type="button"
            mat-icon-button
            [disabled]="disabled()"
            [attr.aria-pressed]="selected()"
            [attr.aria-label]="label()"
            [matTooltip]="label()"
            data-cy="favorite-toggle"
            (click)="toggle($event)"
        >
            <mat-icon aria-hidden="true">{{
                selected() ? 'star' : 'star_border'
            }}</mat-icon>
        </button>
    `,
})
export class SpFavoriteButtonComponent {
    readonly selected = input(false);
    readonly disabled = input(false);
    readonly label = input.required<string>();
    readonly toggled = output<void>();

    toggle(event: Event): void {
        event.stopPropagation();
        this.toggled.emit();
    }
}
