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
    SimpleChanges,
    ViewChild,
    ViewEncapsulation,
} from '@angular/core';
import {
    TimeSelectionId,
    TimeSettings,
    TimeString,
} from '@streampipes/platform-services';
import { MatMenuTrigger } from '@angular/material/menu';
import { TimeSelectionService } from '../../services/time-selection.service';
import { TimeRangeSelectorMenuComponent } from './time-selector-menu/time-selector-menu.component';

@Component({
    selector: 'sp-time-range-selector',
    templateUrl: 'time-range-selector.component.html',
    styleUrls: ['./time-range-selector.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class TimeRangeSelectorComponent implements OnInit, OnChanges {
    @ViewChild('menuTrigger') menu: MatMenuTrigger;
    @ViewChild('timeSelectorMenu')
    timeSelectorMenu: TimeRangeSelectorMenuComponent;

    @Output() dateRangeEmitter = new EventEmitter<TimeSettings>();

    @Input()
    timeSettings: TimeSettings;

    @Input()
    showTimeSelector = true;

    simpleTimeString: string = '';
    timeString: TimeString;
    timeStringMode: 'simple' | 'advanced' = 'simple';

    constructor(private timeSelectionService: TimeSelectionService) {}

    ngOnInit() {
        this.createDateString();
    }

    ngOnChanges(changes: SimpleChanges) {
        if (changes.timeSettings) {
            this.createDateString();
        }
    }

    applyPreviousInterval(): void {
        this.changeTimeByInterval((a, b) => a - b);
    }

    applyNextInterval(): void {
        this.changeTimeByInterval((a, b) => a + b);
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

    reloadData() {
        this.dateRangeEmitter.emit(this.timeSettings);
    }

    updateTimeSettingsAndReload() {
        this.timeSelectionService.updateTimeSettings(
            this.timeSettings,
            new Date(),
        );
        if (this.showTimeSelector) {
            this.timeSelectorMenu.triggerDisplayUpdate();
        }
        this.reloadData();
    }

    private changeTimeByInterval(func: (a: number, b: number) => number) {
        const difference =
            this.timeSettings.endTime - this.timeSettings.startTime;
        const newStartTime = func(this.timeSettings.startTime, difference);
        const newEndTime = func(this.timeSettings.endTime, difference);

        this.timeSettings.startTime = newStartTime;
        this.timeSettings.endTime = newEndTime;
        this.timeSettings.timeSelectionId = TimeSelectionId.CUSTOM;
        this.timeSelectorMenu.triggerDisplayUpdate();
        this.createDateString();
        this.reloadData();
    }

    applyCurrentDateRange(timeSettings: TimeSettings) {
        this.timeSettings = timeSettings;
        this.createDateString();
        this.menu.closeMenu();
        this.reloadData();
    }

    createDateString(): void {
        if (this.timeSettings.timeSelectionId !== TimeSelectionId.CUSTOM) {
            this.simpleTimeString = this.timeSelectionService.getTimeSelection(
                this.timeSettings.timeSelectionId,
            ).label;
            this.timeStringMode = 'simple';
        } else {
            const startDate = new Date(this.timeSettings.startTime);
            const endDate = new Date(this.timeSettings.endTime);
            this.timeString = {
                startDate: startDate.toLocaleDateString(),
                endDate: endDate.toLocaleDateString(),
                startTime: startDate.toLocaleTimeString(),
                endTime: endDate.toLocaleTimeString(),
            };
            this.timeStringMode = 'advanced';
        }
    }
}
