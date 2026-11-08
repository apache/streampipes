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

import { ChangeDetectionStrategy, Component, input } from '@angular/core';

/** Row of `spOverviewCardFact` items with equal column widths. */
@Component({
    selector: 'sp-overview-card-facts',
    template: '<dl><ng-content /></dl>',
    styles: `
        :host {
            display: block;
        }

        dl {
            display: grid;
            grid-auto-columns: minmax(0, 1fr);
            grid-auto-flow: column;
            gap: var(--space-sm);
            margin: 0;
        }
    `,
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SpOverviewCardFactsComponent {}

/** Labelled value in `sp-overview-card-facts`. */
@Component({
    // A description list only allows dt and dd within a div.
    // eslint-disable-next-line @angular-eslint/component-selector
    selector: 'div[spOverviewCardFact]',
    template: `
        <dt>{{ label() }}</dt>
        <dd><ng-content /></dd>
    `,
    styles: `
        :host {
            min-width: 0;
        }

        dt {
            color: var(--mat-sys-on-surface-variant);
            font-size: var(--font-size-xs);
        }

        dd {
            margin: var(--space-2xs) 0 0;
            font-size: var(--font-size-sm);
            font-variant-numeric: tabular-nums;
            font-weight: var(--font-weight-medium);
            line-height: 1.6rem;
        }
    `,
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SpOverviewCardFactComponent {
    readonly label = input.required<string>();
}
