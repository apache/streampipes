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
    inject,
} from '@angular/core';
import {
    TimeSelectionConstants,
    TimeSettings,
} from '@streampipes/platform-services';
import {
    DateRange,
    DefaultMatCalendarRangeStrategy,
    MatCalendar,
    MatRangeDateSelectionModel,
} from '@angular/material/datepicker';
import { differenceInDays, endOfDay, startOfDay } from 'date-fns';
import { TimeSelectorLabel } from '../../time-selector.model';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatCard } from '@angular/material/card';
import { MatInput } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-custom-time-range-selection',
    templateUrl: 'custom-time-range-selection.component.html',
    styleUrls: ['./custom-time-range-selection.component.scss'],
    imports: [
        LayoutDirective,
        FlexDirective,
        MatCard,
        MatCalendar,
        LayoutGapDirective,
        MatInput,
        FormsModule,
        LayoutAlignDirective,
        MatButton,
        TranslatePipe,
    ],
})
export class CustomTimeRangeSelectionComponent implements OnInit {
    private readonly selectionModel = inject<MatRangeDateSelectionModel<Date>>(
        MatRangeDateSelectionModel,
    );
    private readonly selectionStrategy = inject<
        DefaultMatCalendarRangeStrategy<Date>
    >(DefaultMatCalendarRangeStrategy);

    @Input() timeSettings: TimeSettings;

    @Input() labels: TimeSelectorLabel;

    @Input()
    enableTimePicker: boolean;

    @Input()
    maxDayRange: number;

    @Output() timeSettingsEmitter = new EventEmitter<TimeSettings>();

    currentStartDate: string;
    currentEndDate: string;
    currentStartTime: string;
    currentEndTime: string;
    currentDateRange: DateRange<Date>;
    dateSelectionComplete = false;
    dateRangeString: string;

    maxDateRangeError = false;

    ngOnInit(): void {
        this.initializeDateRange();
        this.triggerDisplayUpdate();
        this.dateSelectionComplete = true;
    }

    initializeDateRange(): void {
        this.currentDateRange = new DateRange(
            new Date(this.timeSettings.startTime),
            new Date(this.timeSettings.endTime),
        );
    }

    triggerDisplayUpdate() {
        this.updateDateStrings();
        this.updateTimeStrings();
    }

    updateTimeStrings(): void {
        this.currentStartTime = this.formatTime(this.currentDateRange.start);
        this.currentEndTime = this.formatTime(this.currentDateRange.end);
    }

    formatTime(date: Date): string {
        return date.toTimeString().slice(0, 8);
    }

    updateDateStrings(): void {
        this.currentStartDate = this.formatDate(this.currentDateRange.start);
        this.currentEndDate = this.formatDate(this.currentDateRange.end);
        this.dateRangeString = `${this.currentStartDate} - ${this.currentEndDate}`;
    }

    formatDate(date: Date): string {
        if (this.enableTimePicker === true) {
            return date?.toLocaleDateString() || '-';
        } else {
            return date?.toLocaleDateString() || ' ';
        }
    }

    onDateChange(selectedDate: Date): void {
        this.maxDateRangeError = false;
        const newSelection = this.selectionStrategy.selectionFinished(
            selectedDate,
            this.selectionModel.selection,
        );
        this.selectionModel.updateSelection(newSelection, this);
        this.currentDateRange = new DateRange<Date>(
            newSelection.start,
            newSelection.end,
        );
        this.updateDateStrings();
        const daysDiff = differenceInDays(newSelection.end, newSelection.start);
        if (this.selectionModel.isComplete()) {
            if (this.maxDayRange === 0 || daysDiff + 1 <= this.maxDayRange) {
                this.dateSelectionComplete = true;
                if (!this.enableTimePicker) {
                    this.saveSelection();
                }
            } else {
                this.maxDateRangeError = true;
                this.dateSelectionComplete = false;
            }
        }
    }

    saveSelection(): void {
        if (this.enableTimePicker === true) {
            this.updateDateTime(
                this.currentDateRange.start,
                this.currentStartTime,
            );
            this.updateDateTime(this.currentDateRange.end, this.currentEndTime);
            this.timeSettings.startTime = this.currentDateRange.start.getTime();
            this.timeSettings.endTime = this.currentDateRange.end.getTime();
        } else {
            this.timeSettings.startTime = startOfDay(
                this.currentDateRange.start,
            ).getTime();
            this.timeSettings.endTime = endOfDay(
                this.currentDateRange.end,
            ).getTime();
        }

        this.timeSettings.timeSelectionId = TimeSelectionConstants.CUSTOM;
        this.timeSettingsEmitter.emit(this.timeSettings);
    }

    updateDateTime(date: Date, time: string): void {
        const [hours, minutes, seconds] = time.split(':').map(Number);
        date.setHours(hours, minutes, seconds || 0);
    }
}
