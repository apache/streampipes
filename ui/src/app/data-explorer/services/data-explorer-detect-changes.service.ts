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
import {
    Dashboard,
    DataExplorerWidgetModel,
    TimeSettings,
} from '@streampipes/platform-services';

@Injectable({ providedIn: 'root' })
export class DataExplorerDetectChangesService {
    constructor() {}

    shouldShowConfirm<T extends Dashboard | DataExplorerWidgetModel>(
        originalItem: T,
        currentItem: DataExplorerWidgetModel | Dashboard,
        originalTimeSettings: TimeSettings,
        currentTimeSettings: TimeSettings,
        clearTimestampFn: (model: T) => void,
    ): boolean {
        return (
            this.hasWidgetChanged(
                originalItem,
                currentItem,
                clearTimestampFn,
            ) ||
            this.hasTimeSettingsChanged(
                originalTimeSettings,
                currentTimeSettings,
            )
        );
    }

    hasTimeSettingsChanged(
        originalTimeSettings: TimeSettings,
        currentTimeSettings: TimeSettings,
    ): boolean {
        if (originalTimeSettings.timeSelectionId == 0) {
            return this.hasCustomTimeSettingsChanged(
                originalTimeSettings,
                currentTimeSettings,
            );
        } else {
            return this.hasTimeSelectionIdChanged(
                originalTimeSettings,
                currentTimeSettings,
            );
        }
    }

    hasCustomTimeSettingsChanged(
        original: TimeSettings,
        current: TimeSettings,
    ): boolean {
        return (
            original.startTime !== current.startTime ||
            original.endTime !== current.endTime
        );
    }

    hasTimeSelectionIdChanged(
        original: TimeSettings,
        current: TimeSettings,
    ): boolean {
        return original.timeSelectionId !== current.timeSelectionId;
    }

    hasWidgetChanged(
        originalItem: DataExplorerWidgetModel | Dashboard,
        currentItem: DataExplorerWidgetModel | Dashboard,
        clearTimestampFn: (model: DataExplorerWidgetModel | Dashboard) => void,
    ): boolean {
        const clonedOriginal = JSON.parse(JSON.stringify(originalItem));
        const clonedCurrent = JSON.parse(JSON.stringify(currentItem));
        clearTimestampFn(clonedOriginal);
        clearTimestampFn(clonedCurrent);
        return JSON.stringify(clonedOriginal) !== JSON.stringify(clonedCurrent);
    }
}
