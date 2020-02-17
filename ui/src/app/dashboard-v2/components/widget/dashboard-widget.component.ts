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

import {AfterViewInit, Component, ElementRef, EventEmitter, Input, OnInit, Output} from "@angular/core";
import {Dashboard, DashboardItem} from "../../models/dashboard.model";
import {DashboardService} from "../../services/dashboard.service";
import {DashboardImageComponent} from "../../../app-transport-monitoring/components/dashboard-image/dashboard-image.component";
import {DashboardWidget} from "../../../core-model/dashboard/DashboardWidget";
import {Subject} from "rxjs";
import {GridsterItem, GridsterItemComponent} from "angular-gridster2";
import {GridsterInfo} from "../../models/gridster-info.model";
import {ResizeService} from "../../services/resize.service";

@Component({
    selector: 'dashboard-widget',
    templateUrl: './dashboard-widget.component.html',
    styleUrls: ['./dashboard-widget.component.css']
})
export class DashboardWidgetComponent implements OnInit {

    @Input() widget: DashboardItem;
    @Input() editMode: boolean;
    @Input() item: GridsterItem;
    @Input() gridsterItemComponent: GridsterItemComponent;

    @Output() deleteCallback: EventEmitter<DashboardItem> = new EventEmitter<DashboardItem>();

    widgetLoaded: boolean = false;
    configuredWidget: DashboardWidget;

    constructor(private dashboardService: DashboardService) {
    }

    ngOnInit(): void {
        this.dashboardService.getWidget(this.widget.id).subscribe(response => {
            this.configuredWidget = response;
            this.widgetLoaded = true;
        });
    }

    removeWidget() {
        this.deleteCallback.emit(this.widget);
    }
}