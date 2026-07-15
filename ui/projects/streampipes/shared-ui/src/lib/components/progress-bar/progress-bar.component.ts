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

import { DecimalPipe } from '@angular/common';
import {
    ChangeDetectionStrategy,
    Component,
    computed,
    input,
} from '@angular/core';
import { MatProgressBar } from '@angular/material/progress-bar';

@Component({
    selector: 'sp-progress-bar',
    templateUrl: './progress-bar.component.html',
    styleUrls: ['./progress-bar.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [DecimalPipe, MatProgressBar],
})
export class ProgressBarComponent {
    readonly value = input(0);
    readonly max = input(100);
    readonly title = input('');
    readonly ariaLabel = input('');
    readonly itemLabel = input('');
    readonly progressBarDataCy = input<string | undefined>(undefined);
    readonly progressLabelDataCy = input<string | undefined>(undefined);

    readonly maxValue = computed(() => Math.max(0, this.max()));
    readonly boundedValue = computed(() =>
        Math.min(this.maxValue(), Math.max(0, this.value())),
    );
    readonly progressValue = computed(() => {
        if (this.maxValue() === 0) {
            return 0;
        }
        return Math.round((this.boundedValue() / this.maxValue()) * 100);
    });
    readonly resolvedAriaLabel = computed(
        () => this.ariaLabel() || this.title(),
    );
}
