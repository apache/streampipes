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
    EventEmitter,
    Input,
    Output,
} from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-download-status',
    templateUrl: './download-status.component.html',
    styleUrls: ['./download-status.component.scss'],
    changeDetection: ChangeDetectionStrategy.Eager,
    imports: [
        FlexDirective,
        LayoutDirective,
        LayoutAlignDirective,
        MatProgressSpinner,
        MatButton,
        MatIcon,
        DecimalPipe,
        TranslatePipe,
    ],
})
export class DownloadStatusComponent {
    @Input({ required: true }) downloading: boolean;
    @Input({ required: true }) finished: boolean;
    @Input() failed = false;
    @Input() downloadedMBs?: number;
    @Input() cancelable = false;
    @Input() errorMessage = '';
    @Output() downloadCancelled = new EventEmitter<void>();
}
