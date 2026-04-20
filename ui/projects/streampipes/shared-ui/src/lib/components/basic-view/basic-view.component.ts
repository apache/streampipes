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

import { Component, Input, inject } from '@angular/core';
import { Router } from '@angular/router';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatIconButton } from '@angular/material/button';
import { MatTooltip } from '@angular/material/tooltip';
import { MatIcon } from '@angular/material/icon';
import { NgClass } from '@angular/common';
import { ClassDirective } from '@ngbracket/ngx-layout/extended';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-basic-view',
    templateUrl: './basic-view.component.html',
    styleUrls: ['./basic-view.component.scss'],
    imports: [
        LayoutDirective,
        FlexDirective,
        LayoutAlignDirective,
        MatIconButton,
        MatTooltip,
        MatIcon,
        NgClass,
        ClassDirective,
        TranslatePipe,
    ],
})
export class SpBasicViewComponent {
    private router = inject(Router);

    @Input()
    padding = false;

    @Input()
    showBackLink = false;

    @Input()
    backLinkTarget: string[];

    @Input()
    hideNavbar = false;

    @Input()
    margin = '10px';

    navigateBack() {
        this.router.navigate(this.backLinkTarget);
    }
}
