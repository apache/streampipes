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

import { Component, EventEmitter, Input, Output } from '@angular/core';
import {
    DataExplorerWidgetModel,
    TimeSettings,
} from '@streampipes/platform-services';

@Component({
    selector: 'sp-data-explorer-data-view-toolbar',
    templateUrl: './data-explorer-data-view-toolbar.component.html',
    styleUrls: ['../data-explorer-data-view.component.scss'],
})
export class DataExplorerDataViewToolbarComponent {
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
    discardDataViewEmitter: EventEmitter<void> = new EventEmitter();

    @Output()
    updateDateRangeEmitter: EventEmitter<TimeSettings> = new EventEmitter();

    @Output()
    downloadFileEmitter: EventEmitter<void> = new EventEmitter();
}
