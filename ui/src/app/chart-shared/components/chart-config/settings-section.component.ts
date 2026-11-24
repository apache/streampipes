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

import { Component, input } from '@angular/core';
import { MatIcon } from '@angular/material/icon';

@Component({
    selector: 'sp-chart-settings-section',
    imports: [MatIcon],
    template: `
        <details [open]="initiallyOpen()">
            <summary>
                <span class="flex-col">
                    <strong>{{ heading() }}</strong>
                    @if (description()) {
                        <small [title]="description()">{{
                            description()
                        }}</small>
                    }
                </span>
                <mat-icon aria-hidden="true">chevron_right</mat-icon>
            </summary>
            <div class="section-body flex-col"><ng-content /></div>
        </details>
    `,
    styles: `
        :host {
            display: block;
            min-width: 0;
        }
        details {
            border-top: 1px solid var(--color-border-subtle);
        }
        summary {
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: var(--space-md);
            padding-block: var(--space-md);
            cursor: pointer;
            list-style: none;
        }
        summary::-webkit-details-marker {
            display: none;
        }
        summary:hover {
            background: var(--color-bg-1);
        }
        summary:focus-visible {
            outline: 2px solid var(--color-primary);
            outline-offset: -2px;
        }
        summary span {
            min-width: 0;
            gap: var(--space-2xs);
        }
        strong {
            font-size: var(--font-size-md);
            overflow-wrap: anywhere;
            font-weight: var(--font-weight-semibold, 600);
        }
        small {
            font-size: var(--font-size-xs);
            color: var(--color-text-2);
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
        }
        mat-icon {
            flex-shrink: 0;
            color: var(--color-text-2);
        }
        details[open] > summary mat-icon {
            transform: rotate(90deg);
            color: var(--color-primary);
        }
        .section-body {
            gap: var(--space-sm);
            padding-bottom: var(--space-md);
        }
    `,
})
export class ChartSettingsSectionComponent {
    heading = input.required<string>();
    description = input<string>();
    initiallyOpen = input(false);
}
