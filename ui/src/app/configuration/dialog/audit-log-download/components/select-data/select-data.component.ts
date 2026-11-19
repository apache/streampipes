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
    ChangeDetectionStrategy,
    Component,
    Input,
    OnInit,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import {
    DateInputComponent,
    FormFieldComponent,
    SearchSelectComponent,
    SplitSectionComponent,
} from '@streampipes/shared-ui';
import { TranslatePipe } from '@ngx-translate/core';
import {
    AuditActorOption,
    AuditDownloadDialogModel,
} from '../../model/audit-download-dialog.model';
import {
    AuditDateRangeSelection,
    AuditExportConfig,
} from '../../model/audit-export-config.model';

interface AuditOutcomeOption {
    id: string;
    label: string;
}

@Component({
    selector: 'sp-audit-select-data',
    templateUrl: './select-data.component.html',
    styleUrls: ['./select-data.component.scss'],
    changeDetection: ChangeDetectionStrategy.Eager,
    imports: [
        FormsModule,
        MatCheckboxModule,
        MatRadioGroup,
        MatRadioButton,
        SplitSectionComponent,
        FormFieldComponent,
        SearchSelectComponent,
        DateInputComponent,
        TranslatePipe,
    ],
})
export class AuditSelectDataComponent implements OnInit {
    @Input({ required: true }) dialogModel: AuditDownloadDialogModel;
    @Input({ required: true }) exportConfig: AuditExportConfig;

    private initialFromDate: Date;
    private initialToDate: Date;

    readonly eventTypeFromText = (text: string): string => text.trim();
    readonly actorSearchText = (actor: AuditActorOption): string =>
        `${actor.label} ${actor.id}`;
    readonly actorFromText = (text: string): AuditActorOption => {
        const value = text.trim();
        const byId = this.dialogModel.actors.find(actor => actor.id === value);
        const byUsername = this.dialogModel.actors.filter(
            actor => actor.username?.toLowerCase() === value.toLowerCase(),
        );
        return (
            byId ??
            (byUsername.length === 1
                ? byUsername[0]
                : { id: value, label: value })
        );
    };
    readonly outcomeOptions: AuditOutcomeOption[] = [
        { id: 'succeeded', label: 'Succeeded' },
        { id: 'failed', label: 'Failed' },
        { id: 'partial', label: 'Partial' },
        { id: 'requested', label: 'Requested' },
        { id: 'denied', label: 'Denied' },
    ];

    get selectedActor(): AuditActorOption | undefined {
        return this.dialogModel.query.actor
            ? this.actorFromText(this.dialogModel.query.actor)
            : undefined;
    }

    get selectedOutcome(): AuditOutcomeOption | undefined {
        return this.outcomeOptions.find(
            option => option.id === this.dialogModel.query.outcome,
        );
    }

    ngOnInit(): void {
        this.initialFromDate = new Date(this.exportConfig.fromDate);
        this.initialToDate = new Date(this.exportConfig.toDate);
    }

    onDateRangeSelectionChange(selection: AuditDateRangeSelection): void {
        this.exportConfig.dateRangeSelection = selection;
        if (selection === 'current') {
            this.exportConfig.fromDate = new Date(this.initialFromDate);
            this.exportConfig.toDate = new Date(this.initialToDate);
        }
    }

    onEventTypeChange(value: string | string[] | undefined): void {
        this.dialogModel.query.eventType =
            typeof value === 'string' ? value : '';
    }

    onActorChange(
        value: AuditActorOption | AuditActorOption[] | undefined,
    ): void {
        this.dialogModel.query.actor =
            value && !Array.isArray(value) ? value.id : '';
    }

    onOutcomeChange(
        value: AuditOutcomeOption | AuditOutcomeOption[] | undefined,
    ): void {
        this.dialogModel.query.outcome =
            value && !Array.isArray(value) ? value.id : '';
    }
}
