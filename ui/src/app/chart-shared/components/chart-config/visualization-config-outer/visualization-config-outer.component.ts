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
    Component,
    Input,
    ChangeDetectionStrategy,
    inject,
    InjectionToken,
} from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';

import { MatButton } from '@angular/material/button';

export const CHART_FIELD_NAVIGATION = new InjectionToken<{
    chooseFields(): void;
}>('Chart field navigation');

@Component({
    selector: 'sp-visualization-config-outer',
    host: { class: 'flex-row' },
    templateUrl: './visualization-config-outer.component.html',
    changeDetection: ChangeDetectionStrategy.Eager,
    imports: [TranslatePipe, MatButton],
})
export class SpVisualizationConfigOuterComponent {
    readonly navigation = inject(CHART_FIELD_NAVIGATION, { optional: true });
    @Input() requirement: string;
    @Input()
    configurationValid: boolean;
}
