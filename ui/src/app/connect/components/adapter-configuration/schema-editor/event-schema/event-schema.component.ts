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
import { ITreeOptions, TreeComponent } from '@ali-hm/angular-tree-component';
import { UUID } from 'angular2-uuid';
import { DataTypesService } from '../../../../services/data-type.service';
import {
    AdapterDescription,
    CorrectionValueTransformationRuleDescription,
    EventPropertyNested,
    EventPropertyPrimitive,
    EventPropertyUnion,
    EventSchema,
    FieldStatusInfo,
    GuessSchema,
    SpLogMessage,
} from '@streampipes/platform-services';
import { MatStepper } from '@angular/material/stepper';
import { UserErrorMessage } from '../../../../../core-model/base/UserErrorMessage';
import { TransformationRuleService } from '../../../../services/transformation-rule.service';
import { StaticValueTransformService } from '../../../../services/static-value-transform.service';
import { EventPropertyUtilsService } from '../../../../services/event-property-utils.service';

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
        private staticValueTransformService: StaticValueTransformService,
        private epUtilsService: EventPropertyUtilsService,
    ) {}

    @Input()
    adapterDescription: AdapterDescription;

    isEditable = true;

    originalSchema: EventSchema;
    targetSchema: EventSchema = new EventSchema();
    timestampPresent = false;

    @Input()
    isEditMode;
    refreshedEventSchema = false;

    @Output()
    isEditableChange = new EventEmitter<boolean>();

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

    _tree: TreeComponent;

    schemaGuess: GuessSchema = new GuessSchema();
    countSelected = 0;
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

    options: ITreeOptions = {
        childrenField: 'eventProperties',
        allowDrag: () => {
            return this.isEditable;
        },
        allowDrop: (node, { parent }) => {
            return (
                parent.data instanceof EventPropertyNested ||
                parent.data.virtual
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
                this.targetSchema = guessSchema.targetSchema;
                this.targetSchema.eventProperties.sort((a, b) => {
                    return a.runtimeName < b.runtimeName ? -1 : 1;
                });
                this.schemaGuess = guessSchema;

                this.originalSchema = guessSchema.eventSchema;
                this.validEventSchema = this.checkIfValid(this.targetSchema);

                this.isEditable = true;
                this.isEditableChange.emit(true);
                this.isLoading = false;
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
                this.isLoading = false;
                this.targetSchema = new EventSchema();
            },
        );
    }

    public refreshTree(refreshPreview = true): void {
        if (this.targetSchema && this.targetSchema.eventProperties) {
            this.nodes = new Array<EventPropertyUnion>();
            this.nodes.push(...this.targetSchema.eventProperties);
            this.validEventSchema = this.checkIfValid(this.targetSchema);
            if (refreshPreview) {
                this.updatePreview();
            }
            setTimeout(() => {
                if (this._tree) {
                    this._tree.treeModel.expandAll();
                }
            });
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
        nested.runtimeName = 'nested';
        nested.additionalMetadata = {};
        if (!eventProperty) {
            this.targetSchema.eventProperties.push(nested);
        } else {
            eventProperty.eventProperties.push(nested);
        }
        this.refreshTree();
    }

    public removeSelectedProperties(eventProperties?: any): void {
        eventProperties = eventProperties || this.targetSchema.eventProperties;
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

    public addStaticValueProperty(runtimeName: string): void {
        const eventProperty = new EventPropertyPrimitive();
        eventProperty['@class'] =
            'org.apache.streampipes.model.schema.EventPropertyPrimitive';
        eventProperty.elementId =
            this.staticValueTransformService.makeDefaultElementId();

        eventProperty.runtimeName = runtimeName;
        eventProperty.runtimeType = this.dataTypesService.getStringTypeUrl();
        eventProperty.domainProperties = [];
        eventProperty.propertyScope = 'DIMENSION_PROPERTY';
        eventProperty.additionalMetadata = {};

        this.targetSchema.eventProperties.push(eventProperty);
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
        eventProperty.additionalMetadata = {};

        this.targetSchema.eventProperties.push(eventProperty);
        this.refreshTree();
    }

    public updatePreview(): void {
        this.isPreviewEnabled = false;
        const ruleDescriptions =
            this.transformationRuleService.getTransformationRuleDescriptions(
                this.originalSchema,
                this.targetSchema,
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
        this.timestampPresent = false;
        eventSchema.eventProperties.forEach(p => {
            if (p.domainProperties.indexOf('http://schema.org/DateTime') > -1) {
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
        this.targetSchema.eventProperties = this.nodes;
        return this.targetSchema;
    }

    onNodeMove(event: any) {
        this.targetSchema.eventProperties = this.nodes;
        this.updatePreview();
    }

    @ViewChild('tree')
    set tree(treeComponent: TreeComponent) {
        this._tree = treeComponent;
    }

    get tree(): TreeComponent {
        return this._tree;
    }
}
