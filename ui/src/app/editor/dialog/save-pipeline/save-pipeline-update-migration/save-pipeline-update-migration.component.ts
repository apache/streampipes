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
    CriticalMeasurementFieldChange,
    MeasurementUpdateInfo,
} from '@streampipes/platform-services';
import {
    LayoutAlignDirective,
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-save-pipeline-update-migration',
    templateUrl: './save-pipeline-update-migration.component.html',
    styleUrls: ['./save-pipeline-update-migration.component.scss'],
    imports: [
        LayoutDirective,
        LayoutAlignDirective,
        LayoutGapDirective,
        MatButton,
        MatIcon,
        TranslatePipe,
    ],
})
export class SavePipelineUpdateMigrationComponent {
    @Input()
    measurementUpdateInfos: MeasurementUpdateInfo[] = [];

    @Output()
    startUpdateEmitter: EventEmitter<void> = new EventEmitter<void>();

    getAffectedFieldsText(affectedFields: string[]): string {
        return affectedFields.join(', ');
    }

    hasCriticalFieldChanges(): boolean {
        return this.measurementUpdateInfos.some(
            updateInfo =>
                updateInfo.criticalMeasurementFieldChanges?.length > 0,
        );
    }

    getCriticalFieldChangesText(
        criticalFieldChanges: CriticalMeasurementFieldChange[],
    ): string {
        return criticalFieldChanges
            .map(
                change =>
                    `${change.runtimeName} (${change.existingType} -> ${change.updatedType})`,
            )
            .join(', ');
    }
}
