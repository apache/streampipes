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
    DashboardLiveSettings,
    QuickTimeSelection,
    TimeSelectionConstants,
    TimeSettings,
    TimeString,
} from '@streampipes/platform-services';
import { MatMenuTrigger } from '@angular/material/menu';
import { TimeSelectionService } from '../../services/time-selection.service';
import { TimeRangeSelectorMenuComponent } from './time-selector-menu/time-selector-menu.component';
import { TimeSelectorLabel } from './time-selector.model';
import { differenceInMilliseconds, isSameDay } from 'date-fns';

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

    @Output()
    intervalSettingsChangedEmitter = new EventEmitter<DashboardLiveSettings>();

    @Input()
    timeSettings: TimeSettings;

    @Input()
    liveSettings: DashboardLiveSettings;

    @Input()
    showTimeSelector = true;

    @Input()
    enableTimeChange = true;

    @Input()
    maxDayRange = 0;

    @Input()
    quickSelections: QuickTimeSelection[];

    @Input()
    availableOptions: DashboardLiveSettings[];

    @Input()
    labels: TimeSelectorLabel;

    simpleTimeString: string = '';
    timeString: TimeString;
    timeStringMode: 'simple' | 'advanced' = 'simple';
    dateFormat: Intl.DateTimeFormatOptions = {
        weekday: 'short',
        year: 'numeric',
        month: 'numeric',
        day: 'numeric',
    };

    constructor(private timeSelectionService: TimeSelectionService) {}

    ngOnInit() {
        this.quickSelections ??=
            this.timeSelectionService.defaultQuickTimeSelections;
        this.labels ??= this.timeSelectionService.defaultLabels;
        this.availableOptions ??=
            this.timeSelectionService.defaultAvailableLiveSettingsOptions;
        this.createDateString();
    }

    ngOnChanges(changes: SimpleChanges) {
        if (changes.timeSettings && this.quickSelections !== undefined) {
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
            this.quickSelections,
            this.timeSettings,
            new Date(),
        );
        if (this.showTimeSelector) {
            this.timeSelectorMenu.triggerDisplayUpdate();
        }
        this.reloadData();
    }

    private changeTimeByInterval(func: (a: number, b: number) => number) {
        const timeDiff =
            (differenceInMilliseconds(
                this.timeSettings.startTime,
                this.timeSettings.endTime,
            ) -
                1) *
            -1;
        const newStartTime = func(this.timeSettings.startTime, timeDiff);
        const newEndTime = func(this.timeSettings.endTime, timeDiff);

        this.timeSettings.startTime = newStartTime;
        this.timeSettings.endTime = newEndTime;
        this.timeSettings.timeSelectionId = TimeSelectionConstants.CUSTOM;
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
        if (
            this.timeSettings.timeSelectionId !== TimeSelectionConstants.CUSTOM
        ) {
            this.simpleTimeString = this.timeSelectionService.getTimeSelection(
                this.quickSelections,
                this.timeSettings.timeSelectionId,
            ).label;
            this.timeStringMode = 'simple';
        } else {
            const startDate = new Date(this.timeSettings.startTime);
            const endDate = new Date(this.timeSettings.endTime);
            this.timeString = {
                startDate: this.formatDate(startDate),
                endDate: this.formatDate(endDate),
                startTime: startDate.toLocaleTimeString(),
                endTime: endDate.toLocaleTimeString(),
                sameDay: isSameDay(startDate, endDate),
            };

            this.timeStringMode = 'advanced';
        }
    }

    private formatDate(date: Date): string {
        return this.enableTimeChange
            ? date.toLocaleDateString()
            : date.toLocaleDateString(navigator.language, this.dateFormat);
    }
}
