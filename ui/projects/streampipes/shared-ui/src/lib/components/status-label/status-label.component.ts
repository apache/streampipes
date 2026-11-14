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
    computed,
    input,
} from '@angular/core';
import { MatTooltip } from '@angular/material/tooltip';

export type SpStatusTone = 'success' | 'warning' | 'error' | 'neutral';

const LIGHT_CLASSES: Record<SpStatusTone, string> = {
    success: 'light-green',
    warning: 'light-yellow',
    error: 'light-red',
    neutral: 'light-neutral',
};

/**
 * Status as a tinted label with a coloured dot, or as a status light without
 * text. Its dot and shape set it apart from the pill of `sp-label`, which is
 * used for tags and values. The label is required in both modes: without
 * visible text, it is the tooltip and accessible name of the light.
 */
@Component({
    selector: 'sp-status-label',
    templateUrl: './status-label.component.html',
    styleUrls: ['./status-label.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [MatTooltip],
})
export class SpStatusLabelComponent {
    readonly tone = input.required<SpStatusTone>();
    readonly label = input.required<string>();
    readonly showLabel = input(true);

    readonly lightClass = computed(() => `light ${LIGHT_CLASSES[this.tone()]}`);
}
