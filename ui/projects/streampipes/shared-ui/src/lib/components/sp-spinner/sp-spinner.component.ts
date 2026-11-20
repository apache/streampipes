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

import { Component, Input, ChangeDetectionStrategy } from '@angular/core';
import { MatProgressSpinner } from '@angular/material/progress-spinner';

@Component({
    selector: 'sp-spinner',
    templateUrl: './sp-spinner.component.html',
    styleUrls: ['./sp-spinner.component.scss'],
    changeDetection: ChangeDetectionStrategy.Eager,
    imports: [MatProgressSpinner],
})
export class SpSpinnerComponent {
    @Input() text = '';
    @Input() layout: 'column' | 'row' = 'column';
    @Input() align = 'center center';
    @Input() gap = '8px';
    @Input() diameter = 30;

    // `align` keeps the former fxLayoutAlign syntax: "<main axis> <cross axis>".
    get justifyContent(): string {
        return toFlexValue(this.align.trim().split(/\s+/)[0] || 'start');
    }

    get alignItems(): string {
        return toFlexValue(this.align.trim().split(/\s+/)[1] || 'stretch');
    }
}

function toFlexValue(value: string): string {
    return value === 'start' || value === 'end' ? `flex-${value}` : value;
}
