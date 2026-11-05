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

import {
    ChangeDetectionStrategy,
    Component,
    input,
    output,
} from '@angular/core';
import { MatTooltip } from '@angular/material/tooltip';

/**
 * Generic card shell for overview pages: identity header, content and footer.
 * Feature cards supply their own content through the projection slots.
 */
@Component({
    selector: 'sp-overview-card',
    templateUrl: './overview-card.component.html',
    styleUrls: ['./overview-card.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [MatTooltip],
})
export class SpOverviewCardComponent {
    readonly title = input.required<string>();
    readonly description = input<string>();
    /** Renders the title as a button that opens the resource. */
    readonly titleClickable = input(false);
    /** Colour of the leading icon, usually the colour of the module. */
    readonly accentColor = input<string>();

    readonly titleClick = output<void>();
}
