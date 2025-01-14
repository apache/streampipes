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
    OnInit,
    Output,
    ViewChild,
} from '@angular/core';
import {
    QuickTimeSelection,
    TimeSettings,
    ExtendedTimeSettings,
} from '@streampipes/platform-services';
import { TimeSelectionService } from '../../../services/time-selection.service';
import { CustomTimeRangeSelectionComponent } from './custom-time-range-selection/custom-time-range-selection.component';
import { TimeSelectorLabel } from '../time-selector.model';

@Component({
    selector: 'sp-time-selector-menu',
    templateUrl: 'time-selector-menu.component.html',
    styleUrls: ['./time-selector-menu.component.scss'],
})
export class TimeRangeSelectorMenuComponent implements OnInit {
    @Input()
    timeSettings: TimeSettings;

    @Input()
    labels: TimeSelectorLabel;

    @Input()
    enableTimePicker: boolean;

    @Input()
    maxDayRange: number;

    @Output()
    timeSettingsEmitter: EventEmitter<ExtendedTimeSettings> =
        new EventEmitter<ExtendedTimeSettings>();

    @Input()
    quickSelections: QuickTimeSelection[];

    @ViewChild('timeRangeSelection')
    timeRangeSelection: CustomTimeRangeSelectionComponent;

    constructor(private timeSelectionService: TimeSelectionService) {}

    ngOnInit(): void {}

    applyQuickSelection(quickSelection: QuickTimeSelection): void {
        const selectedDateRange =
            this.timeSelectionService.getDateRange(quickSelection);
        this.timeSettings.timeSelectionId = quickSelection.timeSelectionId;
        this.timeSettings.startTime = selectedDateRange.startDate.getTime();
        this.timeSettings.endTime = selectedDateRange.endDate.getTime();
        this.timeRangeSelection.initializeDateRange();
        this.triggerDisplayUpdate();
        this.timeSettingsEmitter.emit({
            supportsLiveRefresh: quickSelection.supportsLiveRefresh,
            timeSettings: this.timeSettings,
        });
    }

    triggerDisplayUpdate(): void {
        this.timeRangeSelection.initializeDateRange();
        this.timeRangeSelection.triggerDisplayUpdate();
    }
}
