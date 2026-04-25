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

import { Component, Input, OnChanges } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ClientDashboardItem } from '@streampipes/platform-services';
import { MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatOption, MatSelect } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';
import {
    FlexDirective,
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import {
    DashboardItemType,
    getDashboardItemType,
    getMergedDashboardLayoutSettings,
    writeDashboardLayoutSettings,
} from '../../../../../dashboard-shared/utils/dashboard-item.utils';

type LayoutAlignment = 'left' | 'center' | 'right';
type LayoutSize = 'sm' | 'md' | 'lg';
type DividerStyle = 'solid' | 'dashed';

@Component({
    selector: 'sp-dashboard-layout-properties-panel',
    templateUrl: './layout-properties-panel.component.html',
    styleUrls: ['./layout-properties-panel.component.scss'],
    imports: [
        FormsModule,
        MatFormField,
        MatInput,
        MatSelect,
        MatOption,
        TranslatePipe,
        FlexDirective,
        LayoutDirective,
        LayoutGapDirective,
    ],
})
export class LayoutPropertiesPanelComponent implements OnChanges {
    @Input()
    dashboardItem?: ClientDashboardItem;

    itemType: Exclude<DashboardItemType, 'chart'> = 'rich-text';
    content = '';
    label = '';
    size: LayoutSize = 'md';
    alignment: LayoutAlignment = 'left';
    dividerStyle: DividerStyle = 'solid';

    readonly alignmentOptions: LayoutAlignment[] = ['left', 'center', 'right'];
    readonly sizeOptions: LayoutSize[] = ['sm', 'md', 'lg'];
    readonly dividerStyleOptions: DividerStyle[] = ['solid', 'dashed'];

    ngOnChanges(): void {
        if (!this.dashboardItem) {
            return;
        }

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

    updateContent(value: string): void {
        this.content = value;
        this.persistSettings();
    }

    updateLabel(value: string): void {
        this.label = value;
        this.persistSettings();
    }

    updateSize(value: LayoutSize): void {
        this.size = value;
        this.persistSettings();
    }

    updateAlignment(value: LayoutAlignment): void {
        this.alignment = value;
        this.persistSettings();
    }

    updateDividerStyle(value: DividerStyle): void {
        this.dividerStyle = value;
        this.persistSettings();
    }

    private persistSettings(): void {
        if (!this.dashboardItem) {
            return;
        }

        writeDashboardLayoutSettings(this.dashboardItem, {
            content: this.content,
            label: this.label,
            size: this.size,
            alignment: this.alignment,
            dividerStyle: this.dividerStyle,
        });
    }
}
