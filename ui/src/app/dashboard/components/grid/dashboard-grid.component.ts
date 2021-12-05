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
    OnInit,
    Output,
    QueryList,
    SimpleChanges,
    ViewChildren
} from "@angular/core";
import {Dashboard, DashboardConfig, DashboardItem} from "../../models/dashboard.model";
import {GridsterInfo} from "../../models/gridster-info.model";
import {ResizeService} from "../../services/resize.service";
import {GridsterItemComponent, GridType} from "angular-gridster2";
import {DashboardService} from "../../services/dashboard.service";
import {RefreshDashboardService} from "../../services/refresh-dashboard.service";
import {DashboardWidgetModel} from "../../../core-model/gen/streampipes-model";

@Component({
    selector: 'dashboard-grid',
    templateUrl: './dashboard-grid.component.html',
    styleUrls: ['./dashboard-grid.component.css']
})
export class DashboardGridComponent implements OnInit, OnChanges {

    @Input() editMode: boolean;
    @Input() headerVisible: boolean;
    @Input() dashboard: Dashboard;

    @Output() deleteCallback: EventEmitter<DashboardItem> = new EventEmitter<DashboardItem>();
    @Output() updateCallback: EventEmitter<DashboardWidgetModel> = new EventEmitter<DashboardWidgetModel>();

    options: DashboardConfig;
    loaded: boolean = false;

    @ViewChildren(GridsterItemComponent) gridsterItemComponents: QueryList<GridsterItemComponent>;

    constructor(private resizeService: ResizeService,
                private dashboardService: DashboardService,
                private refreshDashboardService: RefreshDashboardService) {

    }

    ngOnInit(): void {
        this.options = {
            disablePushOnDrag: true,
            draggable: { enabled: this.editMode },
            gridType: GridType.VerticalFixed,
            minCols: 12,
            maxCols: 12,
            minRows: 4,
            fixedRowHeight: 50,
            fixedColWidth: 50,
            margin: 5,
            resizable: { enabled: this.editMode },
            displayGrid: this.editMode ? 'always' : 'none',
            itemResizeCallback: ((item, itemComponent) => {
                this.resizeService.notify({id: item.id, width: itemComponent.width, height: itemComponent.height});
            }),
            itemInitCallback: ((item, itemComponent) => {
                this.resizeService.notify({id: item.id, width: itemComponent.width, height: itemComponent.height});
            })
        };
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (changes["editMode"] && this.options) {
            this.options.draggable.enabled = this.editMode;
            this.options.resizable.enabled = this.editMode;
            this.options.displayGrid = this.editMode ? 'always' : 'none';
            this.options.api.optionsChanged();
        }
    }

    propagateItemRemoval(widget: DashboardItem) {
        this.deleteCallback.emit(widget);
    }

    propagateItemUpdate(dashboardWidget: DashboardWidgetModel) {
        this.updateCallback.emit(dashboardWidget);
    }

}
