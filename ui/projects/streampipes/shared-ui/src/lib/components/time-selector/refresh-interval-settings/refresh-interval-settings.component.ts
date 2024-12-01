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
import { DashboardLiveSettings } from '@streampipes/platform-services';

@Component({
    selector: 'sp-data-explorer-refresh-interval-settings-component',
    templateUrl: './refresh-interval-settings.component.html',
})
export class DataExplorerRefreshIntervalSettingsComponent implements OnInit {
    @Input() liveSettings: DashboardLiveSettings;

    @Output()
    intervalSettingsChangedEmitter: EventEmitter<DashboardLiveSettings> =
        new EventEmitter<DashboardLiveSettings>();

    @Input()
    availableOptions: DashboardLiveSettings[];

    liveRefreshEnabled: boolean;

    ngOnInit() {
        if (!this.liveSettings?.label) {
            this.liveSettings = this.availableOptions[0];
        }
    }

    modifyRefreshInterval(option: DashboardLiveSettings): void {
        this.liveSettings = option;
        this.intervalSettingsChangedEmitter.emit(option);
    }

    handleEnableLiveRefresh(liveRefreshEnabled: boolean) {
        if (this.liveRefreshEnabled === true && liveRefreshEnabled === false) {
            this.intervalSettingsChangedEmitter.emit(this.availableOptions[0]);
        }
        this.liveRefreshEnabled = liveRefreshEnabled;
    }
}
