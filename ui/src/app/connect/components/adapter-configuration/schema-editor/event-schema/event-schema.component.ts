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
    Output,
    SimpleChanges,
    ViewChild,
} from '@angular/core';
import { RestService } from '../../../../services/rest.service';
import { ITreeOptions, TreeComponent } from '@circlon/angular-tree-component';
import { UUID } from 'angular2-uuid';
import { DataTypesService } from '../../../../services/data-type.service';
import {
    AdapterDescription,
    EventProperty,
    EventPropertyNested,
    EventPropertyPrimitive,
    EventSchema,
    FieldStatusInfo,
    GuessSchema,
    SpLogMessage,
} from '@streampipes/platform-services';
import { MatStepper } from '@angular/material/stepper';
import { UserErrorMessage } from '../../../../../core-model/base/UserErrorMessage';
import { TransformationRuleService } from '../../../../services/transformation-rule.service';

@Component({
    selector: 'sp-event-schema',
    templateUrl: './event-schema.component.html',
    styleUrls: ['./event-schema.component.scss'],
})
export class EventSchemaComponent implements OnChanges {
    constructor(
        private restService: RestService,
        private dataTypesService: DataTypesService,
        private transformationRuleService: TransformationRuleService,
    ) {}

    @Input()
    adapterDescription: AdapterDescription;

    isEditable = true;

    @Input()
    oldEventSchema: EventSchema;

    @Input()
    eventSchema: EventSchema = new EventSchema();

    @Input()
    isEditMode;
    refreshedEventSchema = false;

    @Output()
    isEditableChange = new EventEmitter<boolean>();
    @Output()
    adapterChange = new EventEmitter<AdapterDescription>();
    @Output()
    eventSchemaChange = new EventEmitter<EventSchema>();
    @Output()
    oldEventSchemaChange = new EventEmitter<EventSchema>();

    @Output()
    goBackEmitter: EventEmitter<MatStepper> = new EventEmitter();

    /**
     * Cancels the adapter configuration process
     */
    @Output()
    removeSelectionEmitter: EventEmitter<boolean> = new EventEmitter();

    /**
     * Go to next configuration step when this is complete
     */
    @Output()
    clickNextEmitter: EventEmitter<MatStepper> = new EventEmitter();

    @ViewChild(TreeComponent, { static: true })
    tree: TreeComponent;

    schemaGuess: GuessSchema = new GuessSchema();
    countSelected = 0;
    isLoading = false;
    isError = false;
    isPreviewEnabled = false;
    errorMessage: SpLogMessage;
    nodes: EventProperty[] = new Array<EventProperty>();
    validEventSchema = false;
    schemaErrorHints: UserErrorMessage[] = [];

    eventPreview: string[];
    desiredPreview: Record<string, any>;
    fieldStatusInfo: Record<string, FieldStatusInfo>;

    options: ITreeOptions = {
        childrenField: 'eventProperties',
        allowDrag: () => {
            return this.isEditable;
        },
        allowDrop: (node, { parent }) => {
            return (
                parent.data.eventProperties !== undefined &&
                parent.parent !== null
            );
        },
        displayField: 'runTimeName',
    };

    public onUpdateData(treeComponent: TreeComponent): void {
        treeComponent.treeModel.expandAll();
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

    public guessSchema(): void {
        this.isLoading = true;
        this.isError = false;
        this.restService.getGuessSchema(this.adapterDescription).subscribe(
            guessSchema => {
                this.eventPreview = guessSchema.eventPreview;
                this.fieldStatusInfo = guessSchema.fieldStatusInfo;
                this.eventSchema = guessSchema.eventSchema;
                this.eventSchema.eventProperties.sort((a, b) => {
                    return a.runtimeName < b.runtimeName ? -1 : 1;
                });
                this.eventSchemaChange.emit(this.eventSchema);
                this.schemaGuess = guessSchema;

                this.validEventSchema = this.checkIfValid(this.eventSchema);

                this.oldEventSchema = EventSchema.fromData(
                    this.eventSchema,
                    new EventSchema(),
                );
                this.oldEventSchemaChange.emit(this.oldEventSchema);

                this.refreshTree();

                this.isEditable = true;
                this.isEditableChange.emit(true);
                this.isLoading = false;
                this.refreshedEventSchema = true;

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
                this.isLoading = false;
                this.eventSchema = new EventSchema();
            },
        );
    }

    public refreshTree(refreshPreview = true): void {
        this.nodes = new Array<EventProperty>();
        this.nodes.push(this.eventSchema as unknown as EventProperty);
        this.validEventSchema = this.checkIfValid(this.eventSchema);
        if (refreshPreview) {
            this.updatePreview();
        }
    }

    public addNestedProperty(eventProperty?: EventPropertyNested): void {
        const uuid: string = UUID.UUID();
        const nested: EventPropertyNested = new EventPropertyNested();
        nested['@class'] =
            'org.apache.streampipes.model.schema.EventPropertyNested';
        nested.elementId = uuid;
        nested.eventProperties = [];
        nested.domainProperties = [];
        if (!eventProperty) {
            this.eventSchema.eventProperties.push(nested);
        } else {
            eventProperty.eventProperties.push(nested);
        }
        this.refreshTree();
    }

    public removeSelectedProperties(eventProperties?: any): void {
        eventProperties = eventProperties || this.eventSchema.eventProperties;
        for (let i = eventProperties.length - 1; i >= 0; --i) {
            if (eventProperties[i].eventProperties) {
                this.removeSelectedProperties(
                    eventProperties[i].eventProperties,
                );
            }
            if (eventProperties[i].selected) {
                eventProperties.splice(i, 1);
            }
        }
        this.countSelected = 0;
        this.refreshTree();
    }

    public addStaticValueProperty(): void {
        const eventProperty = new EventPropertyPrimitive();
        eventProperty['@class'] =
            'org.apache.streampipes.model.schema.EventPropertyPrimitive';
        eventProperty.elementId =
            'http://eventProperty.de/staticValue/' + UUID.UUID();

        eventProperty.runtimeName = 'key_0';
        (eventProperty as any).staticValue = '';
        eventProperty.runtimeType = this.dataTypesService.getStringTypeUrl();
        eventProperty.domainProperties = [];

        this.eventSchema.eventProperties.push(eventProperty);
        this.refreshTree();
    }

    public addTimestampProperty(): void {
        const eventProperty = new EventPropertyPrimitive();
        eventProperty['@class'] =
            'org.apache.streampipes.model.schema.EventPropertyPrimitive';
        eventProperty.elementId =
            'http://eventProperty.de/timestamp/' + UUID.UUID();

        eventProperty.runtimeName = 'timestamp';
        eventProperty.label = 'Timestamp';
        eventProperty.description = 'The current timestamp value';
        eventProperty.domainProperties = ['http://schema.org/DateTime'];
        eventProperty.propertyScope = 'HEADER_PROPERTY';
        eventProperty.runtimeType = 'http://www.w3.org/2001/XMLSchema#long';

        this.eventSchema.eventProperties.push(eventProperty);
        this.refreshTree();
    }

    public updatePreview(): void {
        this.isPreviewEnabled = false;
        this.transformationRuleService.setOldEventSchema(this.oldEventSchema);
        this.transformationRuleService.setNewEventSchema(this.eventSchema);
        const ruleDescriptions =
            this.transformationRuleService.getTransformationRuleDescriptions();
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

    public removeSelection() {
        this.removeSelectionEmitter.emit();
    }

    public clickNext() {
        this.clickNextEmitter.emit();
    }

    public goBack() {
        this.goBackEmitter.emit();
    }

    private checkIfValid(eventSchema: EventSchema): boolean {
        let hasTimestamp = false;
        eventSchema.eventProperties.forEach(p => {
            if (p.domainProperties.indexOf('http://schema.org/DateTime') > -1) {
                hasTimestamp = true;
            }
        });

        this.schemaErrorHints = [];

        if (this.isEditMode && !this.refreshedEventSchema) {
            this.setEventSchemaEditWarning();
        }

        if (!hasTimestamp) {
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

        return hasTimestamp;
    }
}
