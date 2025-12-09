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
    OnChanges,
    OnDestroy,
    Output,
    SimpleChanges,
} from '@angular/core';
import {
    AdapterDescription,
    DataType,
    EventPropertyPrimitive,
    EventPropertyUnion,
    EventSchema,
    FieldStatusInfo,
    GuessSchema,
    SpLogMessage,
} from '@streampipes/platform-services';
import { MatStepper } from '@angular/material/stepper';
import { SemanticType } from '@streampipes/platform-services';
import { interval, Subscription } from 'rxjs';
import { RestService } from '../../../services/rest.service';
import { TransformationRuleService } from '../../../services/transformation-rule.service';
import { IdGeneratorService } from '../../../../core-services/id-generator/id-generator.service';
import { UserErrorMessage } from '../../../../core-model/base/UserErrorMessage';

@Component({
    selector: 'sp-event-schema',
    templateUrl: './event-schema.component.html',
    styleUrls: ['./event-schema.component.scss'],
    standalone: false,
})
export class EventSchemaComponent implements OnChanges, OnDestroy {
    private restService = inject(RestService);
    private transformationRuleService = inject(TransformationRuleService);
    private idGeneratorService = inject(IdGeneratorService);

    @Input()
    adapterDescription: AdapterDescription;

    @Input()
    isEditMode: boolean;

    originalSchema: EventSchema;
    eventSchema: EventSchema = new EventSchema();
    timestampPresent = false;

    refreshedEventSchema = false;

    @Output()
    isEditableChange = new EventEmitter<boolean>();

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

    schemaGuess: GuessSchema = new GuessSchema();
    isLoading = false;
    isError = false;
    isPreviewEnabled = false;
    errorMessage: SpLogMessage;
    nodes: EventPropertyUnion[] = new Array<EventPropertyUnion>();
    validEventSchema = false;
    schemaErrorHints: UserErrorMessage[] = [];

    eventPreview: string[];
    desiredPreview: Record<string, any>;
    fieldStatusInfo: Record<string, FieldStatusInfo>;

    progress = 0;
    progressSub: Subscription;

    public setEventSchemaEditWarning() {
        this.schemaErrorHints.push(
            new UserErrorMessage(
                'Edit mode',
                'Changes in the adapter might require you to refresh the event schema.',
                'info',
            ),
        );
    }

    public guessSchema(): void {
        this.isLoading = true;
        this.isError = false;

        this.progress = 0;

        const duration = 18000;
        const tickRate = 150;
        const totalTicks = duration / tickRate;
        let tick = 0;

        this.progressSub = interval(tickRate).subscribe(() => {
            tick++;
            this.progress = (tick / totalTicks) * 100;

            if (tick >= totalTicks) {
                this.stopProgress();
            }
        });

        this.restService.getGuessSchema(this.adapterDescription).subscribe(
            guessSchema => {
                this.progress = 100;
                this.eventPreview = guessSchema.eventPreview;
                this.fieldStatusInfo = guessSchema.fieldStatusInfo;
                this.eventSchema = guessSchema.eventSchema;
                this.eventSchema.eventProperties.sort((a, b) => {
                    return a.runtimeName < b.runtimeName ? -1 : 1;
                });
                this.schemaGuess = guessSchema;

                this.originalSchema = guessSchema.eventSchema;
                this.validEventSchema = this.checkIfValid(this.eventSchema);

                this.isEditableChange.emit(true);
                this.stopProgress();
                this.refreshedEventSchema = true;
                this.refreshTree();
                if (
                    guessSchema.eventPreview &&
                    guessSchema.eventPreview.length > 0
                ) {
                    this.updatePreview();
                }
            },
            errorMessage => {
                this.errorMessage = errorMessage.error;
                this.isError = true;
                this.stopProgress();
                this.eventSchema = new EventSchema();
            },
        );
    }

    private stopProgress() {
        this.progress = 100;
        this.isLoading = false;
        this.progressSub?.unsubscribe();
        this.progress = 0;
    }

    public refreshTree(refreshPreview = true): void {
        if (this.eventSchema && this.eventSchema.eventProperties) {
            this.nodes = new Array<EventPropertyUnion>();
            this.nodes.push(...this.eventSchema.eventProperties);
            this.validEventSchema = this.checkIfValid(this.eventSchema);
            if (refreshPreview) {
                this.updatePreview();
            }
        }
    }

    public addTimestampProperty(): void {
        const eventProperty = new EventPropertyPrimitive();
        eventProperty['@class'] =
            'org.apache.streampipes.model.schema.EventPropertyPrimitive';
        eventProperty.elementId =
            'http://eventProperty.de/timestamp/' +
            this.idGeneratorService.generate(25);

        eventProperty.runtimeName = 'timestamp';
        eventProperty.label = 'Timestamp';
        eventProperty.description = 'The current timestamp value';
        eventProperty.semanticType = SemanticType.TIMESTAMP;
        eventProperty.propertyScope = 'HEADER_PROPERTY';
        eventProperty.runtimeType = DataType.LONG;
        eventProperty.additionalMetadata = {};

        this.eventSchema.eventProperties.push(eventProperty);
        this.refreshTree();
    }

    public updatePreview(): void {
        this.isPreviewEnabled = false;
        const ruleDescriptions =
            this.transformationRuleService.makeTransformationRuleDescriptions(
                this.originalSchema,
                this.eventSchema,
            );
        if (this.eventPreview && this.eventPreview.length > 0) {
            this.restService
                .getAdapterEventPreview({
                    rules: ruleDescriptions,
                    inputData: this.eventPreview[0],
                })
                .subscribe(preview => {
                    this.desiredPreview = preview;
                    this.isPreviewEnabled = true;
                });
        }
    }

    ngOnChanges(changes: SimpleChanges) {
        setTimeout(() => {
            this.refreshTree();
        }, 200);
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

    private checkIfValid(eventSchema: EventSchema): boolean {
        this.timestampPresent = false;
        eventSchema.eventProperties.forEach(p => {
            if (SemanticType.isTimestamp(p)) {
                this.timestampPresent = true;
            }
        });

        this.schemaErrorHints = [];

        if (this.isEditMode && !this.refreshedEventSchema) {
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

        if (this.fieldStatusInfo) {
            const badFields = eventSchema.eventProperties
                .filter(
                    ep => this.fieldStatusInfo[ep.runtimeName] !== undefined,
                )
                .map(ep => this.fieldStatusInfo[ep.runtimeName])
                .find(field => field.fieldStatus !== 'GOOD');
            if (badFields !== undefined) {
                this.schemaErrorHints.push(
                    new UserErrorMessage(
                        'Bad reading',
                        'At least one field could not be properly read. If this is a permanent problem, consider removing it - keeping this field might cause the adapter to fail or to omit sending events.',
                        'warning',
                    ),
                );
            }
        }

        return this.timestampPresent;
    }

    getOriginalSchema(): EventSchema {
        return this.originalSchema;
    }

    getTargetSchema(): EventSchema {
        this.eventSchema.eventProperties = this.nodes;
        return this.eventSchema;
    }

    ngOnDestroy() {
        this.progressSub?.unsubscribe();
    }
}
