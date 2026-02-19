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
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import {
    MatButtonToggle,
    MatButtonToggleGroup,
} from '@angular/material/button-toggle';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { ShowFieldStatusInfosComponent } from '../show-field-status-infos/show-field-status-infos.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-adapter-sample-preview',
    templateUrl: './adapter-sample-preview.component.html',
    imports: [
        SpBasicInnerPanelComponent,
        LayoutAlignDirective,
        FlexDirective,
        MatButton,
        MatIcon,
        MatButtonToggleGroup,
        MatButtonToggle,
        LayoutDirective,
        MatProgressSpinner,
        SpExceptionMessageComponent,
        ShowFieldStatusInfosComponent,
        AdapterEventPreviewComponent,
        TranslatePipe,
    ],
})
export class AdapterSamplePreviewComponent {
    isSampleLoading = input(false);
    sampleErrorMessage = input<any>();
    fieldStatusInfos = input<any>();
    input = input<any>();
    sourceViewMode = input<Mode>('raw');

    sourceViewModeChange = output<Mode>();
    getSample = output<void>();
    uploadSample = output<void>();
}
