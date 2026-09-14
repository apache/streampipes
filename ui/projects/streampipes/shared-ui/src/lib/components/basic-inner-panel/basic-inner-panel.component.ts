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

import { Component, Input } from '@angular/core';
import {
    animate,
    state,
    style,
    transition,
    trigger,
} from '@angular/animations';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { NgStyle } from '@angular/common';
import { SpBasicHeaderTitleComponent } from '../basic-header-title/header-title.component';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-basic-inner-panel',
    templateUrl: './basic-inner-panel.component.html',
    styleUrls: ['./basic-inner-panel.component.scss'],
    animations: [
        trigger('collapseExpand', [
            state(
                'expanded',
                style({
                    height: '*',
                    opacity: 1,
                    transform: 'translateY(0)',
                }),
            ),
            state(
                'collapsed',
                style({
                    height: '0px',
                    opacity: 0,
                    transform: 'translateY(-4px)',
                }),
            ),
            transition(
                'expanded <=> collapsed',
                animate('180ms cubic-bezier(0.2, 0, 0, 1)'),
            ),
        ]),
    ],
    imports: [
        LayoutDirective,
        FlexDirective,
        NgStyle,
        LayoutAlignDirective,
        SpBasicHeaderTitleComponent,
        MatIcon,
        TranslatePipe,
    ],
})
export class SpBasicInnerPanelComponent {
    private static nextId = 0;

    readonly contentId = `sp-basic-inner-panel-content-${SpBasicInnerPanelComponent.nextId++}`;

    @Input()
    panelTitle: string;

    @Input()
    showTitle = true;

    @Input()
    innerPadding = '15px';

    @Input()
    outerMargin = '0px';

    @Input()
    hideToolbar = false;

    @Input()
    headerBackground = true;

    @Input()
    headerHeight = '40px';

    @Input()
    collapsible = false;

    @Input()
    collapsed = false;

    @Input()
    collapseButtonAriaLabel = 'Toggle panel';

    toggleCollapse(): void {
        if (!this.collapsible) return;
        this.collapsed = !this.collapsed;
    }
}
