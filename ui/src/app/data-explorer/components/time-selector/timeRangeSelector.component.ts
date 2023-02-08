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
    ViewEncapsulation,
} from '@angular/core';
import { TimeSettings } from '@streampipes/platform-services';

@Component({
    selector: 'sp-time-range-selector',
    templateUrl: 'timeRangeSelector.component.html',
    styleUrls: ['./timeRangeSelector.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class TimeRangeSelectorComponent implements OnInit {
    @Output() dateRangeEmitter = new EventEmitter<TimeSettings>();

    _dateRange: TimeSettings;

    startDate: Date;
    endDate: Date;

    public possibleTimeButtons = [
        { value: '15 min', offset: 15 },
        { value: '1 hour', offset: 60 },
        { value: '1 day', offset: 1440 },
        { value: '1 week', offset: 10080 },
        { value: '1 month', offset: 43800 },
        { value: '1 year', offset: 525600 },
        { value: 'custom', offset: -1 },
    ];

    public selectedTimeButton;

    constructor() {}

    ngOnInit() {
        if (!this.dateRange.startTime) {
            this.setCurrentDateRange(this.possibleTimeButtons[0]);
        } else if (this.dateRange.dynamicSelection !== -1) {
            this.setCurrentDateRange(
                this.possibleTimeButtons.find(
                    tb => tb.offset === this.dateRange.dynamicSelection,
                ),
            );
        } else {
            this.startDate = new Date(this._dateRange.startTime);
            this.endDate = new Date(this._dateRange.endTime);
        }
    }

    @Input()
    set dateRange(dateRange: TimeSettings) {
        if (!this.compare(dateRange, this._dateRange)) {
            this._dateRange = dateRange;
            this.updateTimeSettings();
        }
    }

    get dateRange(): TimeSettings {
        return this._dateRange;
    }

    compare(newDateRange: TimeSettings, oldDateRange: TimeSettings): boolean {
        return (
            newDateRange &&
            oldDateRange &&
            newDateRange.startTime === oldDateRange.startTime &&
            newDateRange.endTime === oldDateRange.endTime &&
            newDateRange.dynamicSelection === oldDateRange.dynamicSelection
        );
    }

    updateTimeSettings() {
        this.startDate = new Date(this.dateRange.startTime);
        this.endDate = new Date(this.dateRange.endTime);
        this.selectedTimeButton = this.findOffset(
            this.dateRange.dynamicSelection,
        );
        this.reloadData();
    }

    findOffset(dynamicSelection: number) {
        return (
            this.possibleTimeButtons.find(
                el => el.offset === dynamicSelection,
            ) || this.possibleTimeButtons[0]
        );
    }

    reloadData() {
        this.dateRangeEmitter.emit(this.dateRange);
    }

    increaseTime() {
        this.changeTimeByInterval((a, b) => a + b);
    }

    decreaseTime() {
        this.changeTimeByInterval((a, b) => a - b);
    }

    refreshData() {
        const difference = this.endDate.getTime() - this.startDate.getTime();

        const current = new Date().getTime();
        this.dateRange = {
            startTime: current - difference,
            endTime: current,
            dynamicSelection: this.dateRange.dynamicSelection,
        };

        this.reloadData();
    }

    private changeTimeByInterval(func) {
        const difference = this.endDate.getTime() - this.startDate.getTime();
        const newStartTime = func(this.startDate.getTime(), difference);
        const newEndTime = func(this.endDate.getTime(), difference);

        this.startDate = new Date(newStartTime);
        this.endDate = new Date(newEndTime);
        this.selectedTimeButton =
            this.possibleTimeButtons[this.possibleTimeButtons.length - 1];
        this.dateRange = {
            startTime: newStartTime,
            endTime: newEndTime,
            dynamicSelection: -1,
        };
    }

    changeCustomDateRange() {
        this.selectedTimeButton =
            this.possibleTimeButtons[this.possibleTimeButtons.length - 1];
        const newStartTime = this.startDate.getTime();
        const newEndTime = this.endDate.getTime();

        this.dateRange = {
            startTime: newStartTime,
            endTime: newEndTime,
            dynamicSelection: -1,
        };
    }

    /**
     * Sets the current date range from now to the value of offset in the past
     * @param offset in minutes
     */
    setCurrentDateRange(item) {
        this.selectedTimeButton = item;
        const current = new Date().getTime();
        this.startDate = new Date(current - item.offset * 60000);
        this.endDate = new Date(current);
        this.dateRange = {
            startTime: this.startDate.getTime(),
            endTime: this.endDate.getTime(),
            dynamicSelection: item.offset,
        };
    }
}
