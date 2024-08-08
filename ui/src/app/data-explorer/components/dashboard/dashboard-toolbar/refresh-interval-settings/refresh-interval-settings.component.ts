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

import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
    Dashboard,
    DashboardLiveSettings,
} from '@streampipes/platform-services';

@Component({
    selector: 'sp-data-explorer-refresh-interval-settings-component',
    templateUrl: './refresh-interval-settings.component.html',
})
export class DataExplorerRefreshIntervalSettingsComponent implements OnInit {
    @Input()
    dashboard: Dashboard;

    @Output()
    intervalSettingsChangedEmitter: EventEmitter<void> =
        new EventEmitter<void>();

    availableOptions: DashboardLiveSettings[] = [
        {
            label: 'Off',
            refreshModeActive: false,
        },
        {
            label: '1 sec',
            refreshModeActive: true,
            refreshIntervalInSeconds: 1,
        },
        {
            label: '2 sec',
            refreshModeActive: true,
            refreshIntervalInSeconds: 2,
        },
        {
            label: '5 sec',
            refreshModeActive: true,
            refreshIntervalInSeconds: 5,
        },
        {
            label: '10 sec',
            refreshModeActive: true,
            refreshIntervalInSeconds: 10,
        },
        {
            label: '30 sec',
            refreshModeActive: true,
            refreshIntervalInSeconds: 30,
        },
        {
            label: '1 min',
            refreshModeActive: true,
            refreshIntervalInSeconds: 60,
        },
        {
            label: '5 min',
            refreshModeActive: true,
            refreshIntervalInSeconds: 300,
        },
        {
            label: '30 min',
            refreshModeActive: true,
            refreshIntervalInSeconds: 60 * 30,
        },
    ];

    ngOnInit() {
        if (!this.dashboard.dashboardLiveSettings?.label) {
            this.dashboard.dashboardLiveSettings = this.availableOptions[0];
        }
    }

    modifyRefreshInterval(option: DashboardLiveSettings): void {
        this.dashboard.dashboardLiveSettings = option;
        this.intervalSettingsChangedEmitter.emit();
    }
}
