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

import { Component, computed, input, output } from '@angular/core';
import {
    AdapterEventPreviewComponent,
    Mode,
} from '../../adapter-event-preview/adapter-event-preview.component';
import {
    SpExceptionMessageComponent,
    SpSpinnerComponent,
    SpLabelComponent,
} from '@streampipes/shared-ui';
import {
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import {
    MatButtonToggle,
    MatButtonToggleGroup,
} from '@angular/material/button-toggle';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-adapter-result-preview',
    templateUrl: './adapter-result-preview.component.html',
    styleUrl: '../schema-preview.scss',
    imports: [
        LayoutAlignDirective,
        MatButtonToggleGroup,
        MatButtonToggle,
        LayoutDirective,
        SpSpinnerComponent,
        SpLabelComponent,
        SpExceptionMessageComponent,
        AdapterEventPreviewComponent,
        TranslatePipe,
        SpLabelComponent,
        MatIcon,
    ],
})
export class AdapterResultPreviewComponent {
    isRunningScript = input(false);
    scriptError = input<any>();
    output = input<any>();
    original = input<Record<string, unknown>>({});
    previewOutdated = input(false);
    previewStatus = computed(() =>
        this.isRunningScript()
            ? 'info'
            : this.scriptError()
              ? 'error'
              : this.previewOutdated()
                ? 'warning'
                : 'success',
    );
    hasPreview = input(false);
    changedFields = computed(() => {
        const original = this.original() ?? {};
        const result = this.output() ?? {};
        return [...new Set([...Object.keys(original), ...Object.keys(result)])]
            .filter(
                key =>
                    JSON.stringify(original[key]) !==
                    JSON.stringify(result[key]),
            )
            .sort((a, b) => a.localeCompare(b, undefined, { numeric: true }));
    });
    resultViewMode = input<Mode>('tree');

    resultViewModeChange = output<Mode>();
}
