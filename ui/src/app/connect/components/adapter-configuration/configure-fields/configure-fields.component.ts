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
    inject,
    Input,
    OnInit,
    Output,
} from '@angular/core';
import {
    AdapterDescription,
    EventSchema,
    SpLogMessage,
} from '@streampipes/platform-services';
import { MatStepper } from '@angular/material/stepper';
import { SemanticType } from '@streampipes/platform-services';
import { RestService } from '../../../services/rest.service';
import { UserErrorMessage } from '../../../../core-model/base/UserErrorMessage';

@Component({
    selector: 'sp-configure-fields',
    templateUrl: './configure-fields.component.html',
    styleUrls: ['./configure-fields.component.scss'],
    standalone: false,
})
export class ConfigureFieldsComponent implements OnInit {
    private restService = inject(RestService);

    @Input()
    adapterDescription: AdapterDescription;

    @Input()
    isEditMode: boolean;

    @Output()
    goBackEmitter: EventEmitter<MatStepper> = new EventEmitter();

    /**
     * Cancels the adapter configuration process
     */
    @Output()
    cancelEmitter: EventEmitter<boolean> = new EventEmitter();

    /**
     * Go to next configuration step when this is complete
     */
    @Output()
    nextEmitter: EventEmitter<MatStepper> = new EventEmitter();

    eventSchema: EventSchema = new EventSchema();
    timestampPresent = false;

    isLoading = false;
    isError = false;
    errorMessage: SpLogMessage;
    validEventSchema = false;
    schemaErrorHints: UserErrorMessage[] = [];

    eventPreview: Record<string, any>;
    resultPreview: Record<string, any>;

    ngOnInit() {
        this.resetEventSchema();
    }

    public setEventSchemaEditWarning() {
        this.schemaErrorHints.push(
            new UserErrorMessage(
                'Edit mode',
                'Changes in the adapter might require you to refresh the event schema.',
                'info',
            ),
        );
    }

    public resetEventSchema(): void {
        this.isLoading = true;
        this.isError = false;

        this.eventPreview =
            this.adapterDescription.schemaTransformationConfig.outputs[0];

        this.restService.guessEventSchema(this.adapterDescription).subscribe(
            eventSchema => {
                this.eventSchema = eventSchema;

                this.adapterDescription.dataStream.eventSchema =
                    this.eventSchema;
                this.eventSchema.eventProperties.sort((a, b) => {
                    return a.runtimeName < b.runtimeName ? -1 : 1;
                });

                this.validEventSchema = this.checkSchemaContainsTimestampField(
                    this.eventSchema,
                );

                this.isLoading = false;

                this.updatePreview();
            },
            errorMessage => {
                this.errorMessage = errorMessage.error;
                this.isError = true;
                this.isLoading = false;
                this.eventSchema = new EventSchema();
            },
        );
    }

    public eventPropertyChange(): void {
        this.validEventSchema = this.checkSchemaContainsTimestampField(
            this.eventSchema,
        );

        this.updatePreview();
    }

    public updatePreview(): void {
        if (this.eventPreview) {
            this.restService
                .getAdapterEventPreview(this.adapterDescription)
                .subscribe(preview => {
                    this.resultPreview = preview;
                });
        }
    }

    public cancel() {
        this.cancelEmitter.emit();
    }

    public next() {
        this.nextEmitter.emit();
    }

    public goBack() {
        this.goBackEmitter.emit();
    }

    private checkSchemaContainsTimestampField(
        eventSchema: EventSchema,
    ): boolean {
        this.timestampPresent = false;
        eventSchema.eventProperties.forEach(p => {
            if (SemanticType.isTimestamp(p)) {
                this.timestampPresent = true;
            }
        });

        this.schemaErrorHints = [];

        if (this.isEditMode) {
            this.setEventSchemaEditWarning();
        }

        if (!this.timestampPresent) {
            this.schemaErrorHints.push(
                new UserErrorMessage(
                    'Missing Timestamp',
                    'The timestamp must be a UNIX timestamp in milliseconds. Edit the timestamp field or add an ingestion timestamp.',
                ),
            );
        }
        return this.timestampPresent;
    }
}
