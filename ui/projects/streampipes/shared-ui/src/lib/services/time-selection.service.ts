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

import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import {
    DashboardLiveSettings,
    DateRange,
    QuickTimeSelection,
    TimeSelectionConstants,
    TimeSettings,
    WidgetTimeSettings,
} from '@streampipes/platform-services';
import {
    startOfDay,
    startOfHour,
    startOfMonth,
    startOfWeek,
    startOfYear,
    subDays,
    subHours,
    subMinutes,
    subMonths,
    subWeeks,
    subYears,
} from 'date-fns';
import { TimeSelectorLabel } from '../components/time-selector/time-selector.model';

@Injectable({ providedIn: 'root' })
export class TimeSelectionService {
    legacyMappings: Record<number, string> = {
        15: TimeSelectionConstants.LAST_15_MINUTES,
        60: TimeSelectionConstants.LAST_HOUR,
        1440: TimeSelectionConstants.LAST_DAY,
        10080: TimeSelectionConstants.LAST_WEEK,
        43200: TimeSelectionConstants.LAST_MONTH,
        525600: TimeSelectionConstants.LAST_YEAR,
    };

    defaultLabels: TimeSelectorLabel = {
        quickSelectionLabel: 'Quick Selection',
        customLabel: 'Custom',
        maxDayRangeErrorLabel:
            'Maximum of ${this.maxDayRange} days can be displayed. Please select a smaller range.',
        timeRangeSelectorTooltip: 'Modify time range',
    };

    defaultQuickTimeSelections: QuickTimeSelection[] = [
        {
            label: 'Last 15 min',
            timeSelectionId: TimeSelectionConstants.LAST_15_MINUTES,
            startTime: now => subMinutes(now, 15),
            endTime: now => now,
            supportsLiveRefresh: true,
        },
        {
            label: 'Last 1 hour',
            timeSelectionId: TimeSelectionConstants.LAST_HOUR,
            startTime: now => subHours(now, 1),
            endTime: now => now,
            supportsLiveRefresh: true,
        },
        {
            label: 'Last 1 day',
            timeSelectionId: TimeSelectionConstants.LAST_DAY,
            startTime: now => subDays(now, 1),
            endTime: now => now,
            supportsLiveRefresh: true,
        },
        {
            label: 'Last 1 week',
            timeSelectionId: TimeSelectionConstants.LAST_WEEK,
            startTime: now => subWeeks(now, 1),
            endTime: now => now,
            supportsLiveRefresh: true,
        },
        {
            label: 'Last 1 month',
            timeSelectionId: TimeSelectionConstants.LAST_MONTH,
            startTime: now => subMonths(now, 1),
            endTime: now => now,
            supportsLiveRefresh: true,
        },
        {
            label: 'Last 1 year',
            timeSelectionId: TimeSelectionConstants.LAST_YEAR,
            startTime: now => subYears(now, 1),
            endTime: now => now,
            supportsLiveRefresh: true,
        },
        {
            label: 'Current day',
            timeSelectionId: TimeSelectionConstants.CURRENT_DAY,
            startTime: now => startOfDay(now),
            endTime: now => now,
            supportsLiveRefresh: true,
        },
        {
            label: 'Current hour',
            timeSelectionId: TimeSelectionConstants.CURRENT_HOUR,
            startTime: now => startOfHour(now),
            endTime: now => now,
            supportsLiveRefresh: true,
        },
        {
            label: 'Current week',
            timeSelectionId: TimeSelectionConstants.CURRENT_WEEK,
            startTime: now => startOfWeek(now),
            endTime: now => now,
            supportsLiveRefresh: true,
        },
        {
            label: 'Current month',
            timeSelectionId: TimeSelectionConstants.CURRENT_MONTH,
            startTime: now => startOfMonth(now),
            endTime: now => now,
            supportsLiveRefresh: true,
        },
        {
            label: 'Current year',
            timeSelectionId: TimeSelectionConstants.CURRENT_YEAR,
            startTime: now => startOfYear(now),
            endTime: now => now,
            supportsLiveRefresh: true,
        },
    ];

    defaultAvailableLiveSettingsOptions: DashboardLiveSettings[] = [
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

    public getDateRange(quickSelection: QuickTimeSelection): DateRange {
        const now = new Date();
        return {
            startDate: quickSelection.startTime(now),
            endDate: quickSelection.endTime(now),
        };
    }

    public getDefaultTimeSettings(): TimeSettings {
        return this.getTimeSettings(
            this.defaultQuickTimeSelections,
            this.defaultQuickTimeSelections[0].timeSelectionId,
            new Date(),
        );
    }

    public getTimeSettings(
        quickTimeSelections: QuickTimeSelection[],
        timeSelectionId: string,
        now: Date,
    ): TimeSettings {
        const selection = this.getTimeSelection(
            quickTimeSelections,
            timeSelectionId,
        );
        return {
            startTime: selection.startTime(now).getTime(),
            endTime: selection.endTime(now).getTime(),
            dynamicSelection: -1,
            timeSelectionId: selection.timeSelectionId,
        };
    }

    public updateTimeSettings(
        quickTimeSelections: QuickTimeSelection[],
        timeSettings: TimeSettings,
        now: Date,
    ): void {
        // for backwards compatibility
        if (timeSettings.timeSelectionId === undefined) {
            timeSettings.timeSelectionId =
                this.findLegacyTimeSelectionId(timeSettings);
        } else if (typeof timeSettings.timeSelectionId === 'number') {
            timeSettings.timeSelectionId =
                TimeSelectionConstants.getLegacyTimeSelectionID(
                    timeSettings.timeSelectionId,
                );
        }
        if (timeSettings.timeSelectionId !== TimeSelectionConstants.CUSTOM) {
            if (
                quickTimeSelections.find(
                    s => s.timeSelectionId === timeSettings.timeSelectionId,
                ) === undefined
            ) {
                timeSettings.timeSelectionId =
                    quickTimeSelections[0].timeSelectionId;
            }
            const updatedTimeSettings = this.getTimeSettings(
                quickTimeSelections,
                timeSettings.timeSelectionId,
                now,
            );
            timeSettings.startTime = updatedTimeSettings.startTime;
            timeSettings.endTime = updatedTimeSettings.endTime;
        }
    }

    public getTimeSelection(
        quickTimeSelections: QuickTimeSelection[],
        timeSelectionId: string,
    ): QuickTimeSelection {
        return (
            quickTimeSelections.find(
                s => s.timeSelectionId === timeSelectionId,
            ) || quickTimeSelections[0]
        );
    }

    private findLegacyTimeSelectionId(timeSettings: TimeSettings): string {
        if (timeSettings.dynamicSelection in this.legacyMappings) {
            return this.legacyMappings[timeSettings.dynamicSelection];
        } else {
            return TimeSelectionConstants.CUSTOM;
        }
    }

    public timeSelectionChangeSubject: Subject<WidgetTimeSettings | undefined> =
        new Subject<WidgetTimeSettings | undefined>();

    public notify(timeSettings?: TimeSettings, widgetIndex?: number): void {
        const widgetTimeSettings = { timeSettings, widgetIndex };
        this.timeSelectionChangeSubject.next(widgetTimeSettings);
    }
}
