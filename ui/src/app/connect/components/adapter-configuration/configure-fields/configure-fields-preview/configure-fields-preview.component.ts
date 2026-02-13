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

import { Component, EventEmitter, Input, Output } from '@angular/core';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { SpBasicInnerPanelComponent } from '@streampipes/shared-ui';
import { MatButton } from '@angular/material/button';
import { MatTooltip } from '@angular/material/tooltip';
import { MatIcon } from '@angular/material/icon';
import { JsonPrettyPrintPipe } from '../../../../../core-ui/pipes/json-pretty-print.pipe';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-configure-fields-preview',
    templateUrl: './configure-fields-preview.component.html',
    styleUrls: ['./configure-fields-preview.component.scss'],
    imports: [
        FlexDirective,
        LayoutDirective,
        LayoutGapDirective,
        SpBasicInnerPanelComponent,
        LayoutAlignDirective,
        MatButton,
        MatTooltip,
        MatIcon,
        JsonPrettyPrintPipe,
        TranslatePipe,
    ],
})
export class ConfigureFieldsPreviewComponent {
    @Input() originalPreview: Record<string, any>;
    @Input() resultPreview: Record<string, any>;

    @Output() refreshPreviewEmitter = new EventEmitter();

    public refreshEventPreview() {
        this.refreshPreviewEmitter.emit();
    }
}
