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
    inject,
    Input,
    OnInit,
    Output,
} from '@angular/core';
import {
    DataExplorerWidgetModel,
    TimeSettings,
    UserInfo,
} from '@streampipes/platform-services';
import { CurrentUserService } from '@streampipes/shared-ui';
import { UserRole } from 'src/app/_enums/user-role.enum';

@Component({
    selector: 'sp-data-explorer-data-view-toolbar',
    templateUrl: './data-explorer-chart-view-toolbar.component.html',
    styleUrls: ['../data-explorer-chart-view.component.scss'],
    standalone: false,
})
export class DataExplorerChartViewToolbarComponent implements OnInit {
    private readonly currentUserService = inject(CurrentUserService);

    @Input()
    editMode = true;

    @Input()
    timeSettings: TimeSettings;

    @Input()
    configuredWidget: DataExplorerWidgetModel;

    timeRangeVisible = true;

    @Output()
    saveDataViewEmitter: EventEmitter<void> = new EventEmitter();

    @Output()
    addToAssetEmitter: EventEmitter<void> = new EventEmitter();

    @Output()
    discardDataViewEmitter: EventEmitter<void> = new EventEmitter();

    @Output()
    updateDateRangeEmitter: EventEmitter<TimeSettings> = new EventEmitter();

    @Output()
    downloadFileEmitter: EventEmitter<void> = new EventEmitter();

    currentUser: UserInfo;
    isAssetAdmin = false;

    ngOnInit() {
        this.currentUser = this.currentUserService.getCurrentUser();
        this.isAssetAdmin = this.currentUserService.hasRole(
            UserRole.ROLE_ASSET_ADMIN,
        );
    }
}
