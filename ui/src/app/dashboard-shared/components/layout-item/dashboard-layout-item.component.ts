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

import { Component, DoCheck, EventEmitter, Input, Output } from '@angular/core';
import { ClientDashboardItem } from '@streampipes/platform-services';
import { MatIcon } from '@angular/material/icon';
import { MatIconButton } from '@angular/material/button';
import { MatTooltip } from '@angular/material/tooltip';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { MarkdownComponent } from 'ngx-markdown';
import { TranslatePipe } from '@ngx-translate/core';
import {
    DashboardItemType,
    getDashboardItemType,
    getMergedDashboardLayoutSettings,
} from '../../utils/dashboard-item.utils';

@Component({
    selector: 'sp-dashboard-layout-item',
    templateUrl: './dashboard-layout-item.component.html',
    styleUrls: ['./dashboard-layout-item.component.scss'],
    imports: [
        MatIcon,
        MatIconButton,
        MatTooltip,
        LayoutDirective,
        LayoutAlignDirective,
        FlexDirective,
        MarkdownComponent,
        TranslatePipe,
    ],
})
export class DashboardLayoutItemComponent implements DoCheck {
    @Input()
    dashboardItem: ClientDashboardItem;

    @Input()
    editMode = false;

    @Input()
    kioskMode = false;

    @Input()
    widgetIndex: number;

    @Input()
    selected = false;

    @Output()
    deleteCallback = new EventEmitter<number>();

    @Output()
    selectItemEmitter = new EventEmitter<string>();

    itemType: Exclude<DashboardItemType, 'chart'> = 'rich-text';
    content = '';
    label = '';
    size: 'sm' | 'md' | 'lg' = 'md';
    alignment: 'left' | 'center' | 'right' = 'left';
    dividerStyle: 'solid' | 'dashed' = 'solid';

    ngDoCheck(): void {
        this.itemType = getDashboardItemType(this.dashboardItem) as Exclude<
            DashboardItemType,
            'chart'
        >;
        const settings = getMergedDashboardLayoutSettings(this.dashboardItem);
        this.content = settings.content ?? '';
        this.label = settings.label ?? '';
        this.size = settings.size ?? 'md';
        this.alignment = settings.alignment ?? 'left';
        this.dividerStyle = settings.dividerStyle ?? 'solid';
    }

    get richTextPreview(): string {
        return this.content?.trim();
    }

    get headerText(): string {
        return this.content?.trim();
    }

    get showActionOverlay(): boolean {
        return this.editMode && this.selected && !this.kioskMode;
    }

    selectItem(): void {
        this.selectItemEmitter.emit(this.dashboardItem.id);
    }

    removeItem(): void {
        this.deleteCallback.emit(this.widgetIndex);
    }
}
