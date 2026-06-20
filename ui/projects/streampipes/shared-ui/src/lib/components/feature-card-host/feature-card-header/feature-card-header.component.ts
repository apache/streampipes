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

import { MatIcon } from '@angular/material/icon';
import { MatButton, MatIconButton } from '@angular/material/button';

@Component({
    selector: 'sp-feature-card-header',
    templateUrl: './feature-card-header.component.html',
    styleUrls: ['./feature-card-header.component.scss'],
    imports: [
        LayoutDirective,
        LayoutAlignDirective,
        LayoutGapDirective,
        FlexDirective,
        MatIcon,
        MatButton,
        MatIconButton,
    ],
})
export class FeatureCardHeaderComponent {
    @Input() title: string;
    @Input() description: string;

    @Input() icon: string;
    @Input() iconColor: string;
    @Input() detailsLink: string[];
    @Output() closeClick: EventEmitter<void> = new EventEmitter();
    @Output() detailsClick: EventEmitter<void> = new EventEmitter();
}
