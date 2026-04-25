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

import {
    Component,
    EventEmitter,
    Input,
    OnChanges,
    Output,
    SimpleChanges,
} from '@angular/core';
import { MatTab, MatTabGroup } from '@angular/material/tabs';
import {
    FlexDirective,
    FlexFillDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { ChartSelectionComponent } from './chart-selection/chart-selection.component';
import { TranslatePipe } from '@ngx-translate/core';
import { LayoutSelectionComponent } from './layout-selection/layout-selection.component';
import { LayoutPropertiesPanelComponent } from './layout-properties-panel/layout-properties-panel.component';
import {
    DashboardItemType,
    isChartDashboardItem,
} from '../../../../dashboard-shared/utils/dashboard-item.utils';
import { ClientDashboardItem } from '@streampipes/platform-services';

@Component({
    selector: 'sp-dashboard-chart-selection-panel',
    templateUrl: './chart-selection-panel.component.html',
    styleUrls: [
        './chart-selection-panel.component.scss',
        '../../../../chart/components/chart-view/designer-panel/chart-designer-panel.component.scss',
    ],
    imports: [
        FlexDirective,
        LayoutDirective,
        MatTabGroup,
        FlexFillDirective,
        MatTab,
        ChartSelectionComponent,
        LayoutSelectionComponent,
        LayoutPropertiesPanelComponent,
        TranslatePipe,
    ],
})
export class ChartSelectionPanelComponent implements OnChanges {
    @Input()
    selectedDashboardItem?: ClientDashboardItem;

    @Output()
    addChartEmitter: EventEmitter<string> = new EventEmitter<string>();

    @Output()
    addLayoutEmitter = new EventEmitter<Exclude<DashboardItemType, 'chart'>>();

    selectedTabIndex = 0;

    get selectedLayoutItem(): ClientDashboardItem | undefined {
        if (
            !this.selectedDashboardItem ||
            isChartDashboardItem(this.selectedDashboardItem)
        ) {
            return undefined;
        }

        return this.selectedDashboardItem;
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (!changes['selectedDashboardItem']) {
            return;
        }

        if (this.selectedLayoutItem) {
            this.selectedTabIndex = 2;
        } else if (this.selectedTabIndex === 2) {
            this.selectedTabIndex = 0;
        }
    }
}
