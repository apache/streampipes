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
import {
    AdapterEventPreviewComponent,
    Mode,
} from '../../adapter-event-preview/adapter-event-preview.component';
import {
    SpBasicInnerPanelComponent,
    SpExceptionMessageComponent,
} from '@streampipes/shared-ui';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import {
    MatButtonToggle,
    MatButtonToggleGroup,
} from '@angular/material/button-toggle';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-adapter-result-preview',
    templateUrl: './adapter-result-preview.component.html',
    imports: [
        SpBasicInnerPanelComponent,
        FlexDirective,
        LayoutAlignDirective,
        MatButtonToggleGroup,
        MatButtonToggle,
        LayoutDirective,
        MatProgressSpinner,
        SpExceptionMessageComponent,
        AdapterEventPreviewComponent,
        TranslatePipe,
    ],
})
export class AdapterResultPreviewComponent {
    isRunningScript = input(false);
    scriptError = input<any>();
    output = input<any>();
    resultViewMode = input<Mode>('raw');

    resultViewModeChange = output<Mode>();
}
