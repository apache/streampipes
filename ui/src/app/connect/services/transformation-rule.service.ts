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
    AddTimestampRuleDescription,
    AddValueTransformationRuleDescription,
    ChangeDatatypeTransformationRuleDescription,
    CorrectionValueTransformationRuleDescription,
    CreateNestedRuleDescription,
    DeleteRuleDescription,
    EventProperty,
    EventPropertyNested,
    EventPropertyPrimitive,
    EventPropertyUnion,
    EventSchema,
    MoveRuleDescription,
    PropertyScope,
    RenameRuleDescription,
    SemanticType,
    TimestampTranfsformationRuleDescription,
    TransformationRuleDescriptionUnion,
    UnitTransformRuleDescription,
} from '@streampipes/platform-services';
import { TimestampTransformationRuleMode } from '../model/TimestampTransformationRuleMode';
import { StaticValueTransformService } from './static-value-transform.service';

@Injectable({ providedIn: 'root' })
export class TransformationRuleService {
    constructor(
        private staticValueTransformService: StaticValueTransformService,
    ) {}

    private delimiter = '<-=>';

    public getTransformationRuleDescriptions(
        originalSchema: EventSchema,
        targetSchema: EventSchema,
    ): TransformationRuleDescriptionUnion[] {
        let transformationRuleDescriptions: TransformationRuleDescriptionUnion[] =
            [];

        if (originalSchema == null || targetSchema == null) {
            console.log('Old and new schema must be defined');
        } else {
            const addedTimestampProperty = this.getTimestampProperty(
                targetSchema.eventProperties,
            );
            if (addedTimestampProperty) {
                // add to old event schema for the case users moved the property to a nested property
                originalSchema.eventProperties.push(addedTimestampProperty);

                const timestampRuleDescription: AddTimestampRuleDescription =
                    new AddTimestampRuleDescription();
                timestampRuleDescription['@class'] =
                    'org.apache.streampipes.model.connect.rules.value.AddTimestampRuleDescription';
                timestampRuleDescription.runtimeKey =
                    addedTimestampProperty.runtimeName;
                timestampRuleDescription.propertyScope =
                    addedTimestampProperty.propertyScope as PropertyScope;
                transformationRuleDescriptions.push(timestampRuleDescription);
            }
            const staticValueProperties = this.getStaticValueProperties(
                targetSchema.eventProperties,
            );
            for (const ep of staticValueProperties) {
                originalSchema.eventProperties.push(ep);
                const rule: AddValueTransformationRuleDescription =
                    new AddValueTransformationRuleDescription();
                rule['@class'] =
                    'org.apache.streampipes.model.connect.rules.value.AddValueTransformationRuleDescription';
                rule.runtimeKey = ep.runtimeName;
                rule.datatype = ep.runtimeType;
                rule.label = ep.label;
                rule.description = ep.description;
                rule.propertyScope = ep.propertyScope as PropertyScope;
                if (ep.domainProperties.length > 0) {
                    rule.semanticType = ep.domainProperties[0];
                }
                rule.measurementUnit = ep.measurementUnit;
                rule.staticValue =
                    this.staticValueTransformService.getStaticValue(
                        ep.elementId,
                    );
                transformationRuleDescriptions.push(rule);
            }

            // Scale
            transformationRuleDescriptions = transformationRuleDescriptions
                .concat(
                    this.getCorrectionValueRules(
                        targetSchema.eventProperties,
                        originalSchema,
                        targetSchema,
                    ),
                )
                .concat(
                    this.getRenameRules(
                        targetSchema.eventProperties,
                        originalSchema,
                        targetSchema,
                    ),
                )
                .concat(
                    this.getCreateNestedRules(
                        targetSchema.eventProperties,
                        originalSchema,
                        targetSchema,
                    ),
                )
                .concat(
                    this.getMoveRules(
                        targetSchema.eventProperties,
                        originalSchema,
                        targetSchema,
                    ),
                )
                .concat(this.getDeleteRules(originalSchema, targetSchema))
                .concat(
                    this.getUnitTransformRules(
                        targetSchema.eventProperties,
                        originalSchema,
                        targetSchema,
                    ),
                )
                .concat(
                    this.getTimestampTransformRules(
                        targetSchema.eventProperties,
                        originalSchema,
                        targetSchema,
                    ),
                )
                .concat(
                    this.getDatatypeTransformRules(
                        targetSchema.eventProperties,
                        originalSchema,
                        targetSchema,
                    ),
                );
            return transformationRuleDescriptions;
        }
    }

    public getMoveRules(
        newEventProperties: EventProperty[],
        oldEventSchema: EventSchema,
        newEventSchema: EventSchema,
    ): MoveRuleDescription[] {
        const result: MoveRuleDescription[] = [];

        const extractMoveRules = (
            eventProps: EventProperty[],
            keyPrefix: string,
        ) => {
            for (const eventProperty of eventProps) {
                if (eventProperty instanceof EventPropertyNested) {
                    extractMoveRules(
                        (eventProperty as EventPropertyNested).eventProperties,
                        keyPrefix,
                    );
                }
                const keyOld = this.getCompleteRuntimeNameKey(
                    oldEventSchema.eventProperties,
                    eventProperty.elementId,
                );
                const keyNew = this.getCompleteRuntimeNameKey(
                    newEventSchema.eventProperties,
                    eventProperty.elementId,
                );
                if (keyOld && keyNew) {
                    const keyOldPrefix = keyOld.substr(
                        0,
                        keyOld.lastIndexOf(this.delimiter),
                    );
                    const keyNewPrefix = keyNew.substr(
                        0,
                        keyNew.lastIndexOf(this.delimiter),
                    );

                    if (keyOldPrefix !== keyNewPrefix) {
                        let keyOfOldValue = '';
                        if (keyOldPrefix === '') {
                            keyOfOldValue = keyNew.substr(
                                keyNew.lastIndexOf(this.delimiter) + 1,
                            );
                        } else {
                            keyOfOldValue = `${keyOldPrefix}.${keyNew.substr(
                                keyNew.lastIndexOf(this.delimiter) + 1,
                            )}`;
                        }

                        const rule = new MoveRuleDescription();
                        rule['@class'] =
                            'org.apache.streampipes.model.connect.rules.schema.MoveRuleDescription';
                        rule.oldRuntimeKey = keyOfOldValue;
                        rule.newRuntimeKey = keyNewPrefix;
                        result.push(rule);
                    }
                }
            }
        };

        extractMoveRules(newEventProperties, '');
        return result;
    }

    public getCreateNestedRules(
        newEventProperties: EventProperty[],
        oldEventSchema: EventSchema,
        newEventSchema: EventSchema,
    ): CreateNestedRuleDescription[] {
        const allNewIds: string[] = this.getAllIds(
            newEventSchema.eventProperties,
        );
        const allOldIds: string[] = this.getAllIds(
            oldEventSchema.eventProperties,
        );

        const result: CreateNestedRuleDescription[] = [];
        for (const id of allNewIds) {
            if (allOldIds.indexOf(id) === -1) {
                const key = this.getCompleteRuntimeNameKey(
                    newEventSchema.eventProperties,
                    id,
                );
                const rule: CreateNestedRuleDescription =
                    new CreateNestedRuleDescription();
                rule['@class'] =
                    'org.apache.streampipes.model.connect.rules.schema.CreateNestedRuleDescription';
                rule.runtimeKey = key;
                result.push(rule);
            }
        }
        return result;
    }

    public getRenameRules(
        newEventProperties: EventProperty[],
        oldEventSchema: EventSchema,
        newEventSchema: EventSchema,
    ): RenameRuleDescription[] {
        let result: RenameRuleDescription[] = [];

        for (const eventProperty of newEventProperties) {
            const keyOld = this.getCompleteRuntimeNameKey(
                oldEventSchema.eventProperties,
                eventProperty.elementId,
            );
            const keyNew = this.getCompleteRuntimeNameKey(
                newEventSchema.eventProperties,
                eventProperty.elementId,
            );

            const renameRule: RenameRuleDescription =
                new RenameRuleDescription();
            renameRule['@class'] =
                'org.apache.streampipes.model.connect.rules.schema.RenameRuleDescription';
            renameRule.oldRuntimeKey = keyOld;
            renameRule.newRuntimeKey = keyNew;
            result.push(renameRule);
            if (eventProperty instanceof EventPropertyNested) {
                const tmpResults: RenameRuleDescription[] = this.getRenameRules(
                    (eventProperty as EventPropertyNested).eventProperties,
                    oldEventSchema,
                    newEventSchema,
                );
                result = result.concat(tmpResults);
            }
        }

        const filteredResult: RenameRuleDescription[] = [];
        for (const res of result) {
            if (
                this.getRuntimeNameKey(res.newRuntimeKey) !==
                    this.getRuntimeNameKey(res.oldRuntimeKey) &&
                res.oldRuntimeKey
            ) {
                filteredResult.push(res);
            }
        }

        return filteredResult;
    }

    public getDeleteRules(
        oldEventSchema: EventSchema,
        newEventSchema: EventSchema,
    ): DeleteRuleDescription[] {
        const resultKeys: string[] = [];

        const allNewIds: string[] = this.getAllIds(
            newEventSchema.eventProperties,
        );
        const allOldIds: string[] = this.getAllIds(
            oldEventSchema.eventProperties,
        );

        for (const id of allOldIds) {
            // if not in new ids create delete rule
            if (allNewIds.indexOf(id) === -1) {
                const key = this.getCompleteRuntimeNameKey(
                    oldEventSchema.eventProperties,
                    id,
                );
                resultKeys.push(key);
            }
        }

        const resultRules: DeleteRuleDescription[] = [];
        for (const key of resultKeys) {
            const rule: DeleteRuleDescription = new DeleteRuleDescription();
            rule['@class'] =
                'org.apache.streampipes.model.connect.rules.schema.DeleteRuleDescription';
            rule.runtimeKey = key;
            resultRules.push(rule);
        }

        return resultRules;
    }

    public getUnitTransformRules(
        newEventProperties: EventProperty[],
        oldEventSchema: EventSchema,
        newEventSchema: EventSchema,
    ): UnitTransformRuleDescription[] {
        let result: UnitTransformRuleDescription[] = [];

        for (const eventProperty of newEventProperties) {
            if (eventProperty instanceof EventPropertyPrimitive) {
                const eventPropertyPrimitive =
                    eventProperty as EventPropertyPrimitive;
                const keyNew = this.getCompleteRuntimeNameKey(
                    newEventSchema.eventProperties,
                    eventPropertyPrimitive.elementId,
                );

                const rule: UnitTransformRuleDescription =
                    new UnitTransformRuleDescription();
                rule['@class'] =
                    'org.apache.streampipes.model.connect.rules.value.UnitTransformRuleDescription';
                rule.runtimeKey = keyNew;
                if (
                    eventProperty.additionalMetadata &&
                    eventProperty.additionalMetadata.toMeasurementUnit
                ) {
                    rule.fromUnitRessourceURL =
                        eventProperty.additionalMetadata.fromMeasurementUnit;
                    rule.toUnitRessourceURL =
                        eventPropertyPrimitive.additionalMetadata.toMeasurementUnit;

                    result.push(rule);
                }
            } else if (eventProperty instanceof EventPropertyNested) {
                const tmpResults: UnitTransformRuleDescription[] =
                    this.getUnitTransformRules(
                        (eventProperty as EventPropertyNested).eventProperties,
                        oldEventSchema,
                        newEventSchema,
                    );
                result = result.concat(tmpResults);
            }
        }

        return result;
    }

    public getCompleteRuntimeNameKey(
        eventProperties: EventProperty[],
        id: string,
    ): string {
        let result = '';

        for (const eventProperty of eventProperties) {
            if (eventProperty.elementId === id) {
                return eventProperty.runtimeName;
            } else {
                if (eventProperty instanceof EventPropertyNested) {
                    const methodResult = this.getCompleteRuntimeNameKey(
                        (eventProperty as EventPropertyNested).eventProperties,
                        id,
                    );
                    if (methodResult != null) {
                        result =
                            eventProperty.runtimeName +
                            this.delimiter +
                            methodResult;
                    }
                }
            }
        }

        if (result === '') {
            return null;
        } else {
            return result;
        }
    }

    public getRuntimeNameKey(completeKey: string): string {
        if (completeKey) {
            const keyElements = completeKey.split(this.delimiter);

            if (keyElements.length === 0) {
                return completeKey;
            } else {
                return keyElements[keyElements.length - 1];
            }
        }
    }

    public getAllIds(eventProperties: EventProperty[]): string[] {
        let result: string[] = [];

        for (const eventProperty of eventProperties) {
            result.push(eventProperty.elementId);

            if (eventProperty instanceof EventPropertyNested) {
                result = result.concat(
                    this.getAllIds(
                        (eventProperty as EventPropertyNested).eventProperties,
                    ),
                );
            }
        }
        return result;
    }

    public getEventProperty(
        eventProperties: EventProperty[],
        id: string,
    ): EventProperty {
        const result: EventProperty = null;

        for (const eventProperty of eventProperties) {
            if (eventProperty.elementId === id) {
                return eventProperty;
            } else {
                if (eventProperty instanceof EventPropertyNested) {
                    return this.getEventProperty(
                        (eventProperty as EventPropertyNested).eventProperties,
                        id,
                    );
                }
            }
        }
        return result;
    }

    private getTimestampProperty(
        eventProperties: EventPropertyUnion[],
    ): EventPropertyUnion {
        for (const eventProperty of eventProperties) {
            if (
                eventProperty.elementId.startsWith(
                    'http://eventProperty.de/timestamp/',
                )
            ) {
                return eventProperty;
            }

            if (eventProperty instanceof EventPropertyNested) {
                const result = this.getTimestampProperty(
                    eventProperty.eventProperties,
                );

                if (result) {
                    return result;
                }
            }
        }

        return null;
    }

    private getStaticValueProperties(
        eventProperties: EventPropertyUnion[],
    ): EventPropertyPrimitive[] {
        let result: EventPropertyPrimitive[] = [];
        for (const eventProperty of eventProperties) {
            if (
                this.staticValueTransformService.isStaticValueProperty(
                    eventProperty.elementId,
                )
            ) {
                result.push(eventProperty as EventPropertyPrimitive);
            }

            if (eventProperty instanceof EventPropertyNested) {
                const tmpResult = this.getStaticValueProperties(
                    eventProperty.eventProperties,
                );
                if (tmpResult.length > 0) {
                    result = result.concat(tmpResult);
                }
            }
        }

        return result;
    }

    public getTimestampTransformRules(
        newEventProperties: EventProperty[],
        oldEventSchema: EventSchema,
        newEventSchema: EventSchema,
    ): TimestampTranfsformationRuleDescription[] {
        let result: TimestampTranfsformationRuleDescription[] = [];

        for (const eventProperty of newEventProperties) {
            if (eventProperty instanceof EventPropertyPrimitive) {
                const eventPropertyPrimitive =
                    eventProperty as EventPropertyPrimitive;
                const keyNew = this.getCompleteRuntimeNameKey(
                    newEventSchema.eventProperties,
                    eventPropertyPrimitive.elementId,
                );

                if (this.isTimestampProperty(eventPropertyPrimitive)) {
                    const rule: TimestampTranfsformationRuleDescription =
                        new TimestampTranfsformationRuleDescription();
                    rule['@class'] =
                        'org.apache.streampipes.model.connect.rules.value.TimestampTranfsformationRuleDescription';
                    rule.runtimeKey = keyNew;
                    rule.mode = eventProperty.additionalMetadata.mode;
                    rule.formatString =
                        eventProperty.additionalMetadata.formatString;
                    rule.multiplier =
                        eventProperty.additionalMetadata.multiplier;
                    result.push(rule);
                }
            } else if (eventProperty instanceof EventPropertyNested) {
                const tmpResults: TimestampTranfsformationRuleDescription[] =
                    this.getTimestampTransformRules(
                        (eventProperty as EventPropertyNested).eventProperties,
                        oldEventSchema,
                        newEventSchema,
                    );
                result = result.concat(tmpResults);
            }
        }

        const filteredResult: TimestampTranfsformationRuleDescription[] = [];
        for (const res of result) {
            // TODO: better solution to check if the mode is valid
            if (
                res.mode === TimestampTransformationRuleMode.FORMAT_STRING ||
                (res.multiplier !== 0 &&
                    res.mode === TimestampTransformationRuleMode.TIME_UNIT)
            ) {
                filteredResult.push(res);
            }
        }

        return filteredResult;
    }

    isTimestampProperty(property: EventPropertyPrimitive) {
        return property.domainProperties.some(
            dp => dp === SemanticType.TIMESTAMP,
        );
    }

    private getDatatypeTransformRules(
        eventProperties: EventPropertyUnion[],
        oldEventSchema: EventSchema,
        newEventSchema: EventSchema,
    ): ChangeDatatypeTransformationRuleDescription[] {
        let result: ChangeDatatypeTransformationRuleDescription[] = [];

        eventProperties.forEach(ep => {
            if (ep instanceof EventPropertyPrimitive) {
                const eventPropertyPrimitive = ep as EventPropertyPrimitive;
                const newRuntimeType = ep.runtimeType;
                const keyNew = this.getCompleteRuntimeNameKey(
                    newEventSchema.eventProperties,
                    eventPropertyPrimitive.elementId,
                );
                const oldProperty = this.getEventProperty(
                    oldEventSchema.eventProperties,
                    ep.elementId,
                );
                if (oldProperty) {
                    const oldRuntimeType = (
                        oldProperty as EventPropertyPrimitive
                    ).runtimeType;
                    if (newRuntimeType !== oldRuntimeType) {
                        const rule: ChangeDatatypeTransformationRuleDescription =
                            new ChangeDatatypeTransformationRuleDescription();
                        rule['@class'] =
                            'org.apache.streampipes.model.connect.rules.value.ChangeDatatypeTransformationRuleDescription';
                        rule.runtimeKey = keyNew;
                        rule.originalDatatypeXsd = oldRuntimeType;
                        rule.targetDatatypeXsd = newRuntimeType;

                        result.push(rule);
                    }
                }
            } else if (ep instanceof EventPropertyNested) {
                const nestedResults = this.getDatatypeTransformRules(
                    (ep as EventPropertyNested).eventProperties,
                    oldEventSchema,
                    newEventSchema,
                );
                result = result.concat(nestedResults);
            }
        });

        return result;
    }

    private getCorrectionValueRules(
        eventProperties: EventPropertyUnion[],
        oldEventSchema: EventSchema,
        newEventSchema: EventSchema,
    ) {
        let result: CorrectionValueTransformationRuleDescription[] = [];

        for (const eventProperty of eventProperties) {
            if (eventProperty instanceof EventPropertyPrimitive) {
                const eventPropertyPrimitive =
                    eventProperty as EventPropertyPrimitive;
                const keyNew = this.getCompleteRuntimeNameKey(
                    newEventSchema.eventProperties,
                    eventPropertyPrimitive.elementId,
                );

                const rule: CorrectionValueTransformationRuleDescription =
                    new CorrectionValueTransformationRuleDescription();
                rule['@class'] =
                    'org.apache.streampipes.model.connect.rules.value.CorrectionValueTransformationRuleDescription';
                if (
                    eventPropertyPrimitive.additionalMetadata &&
                    eventPropertyPrimitive.additionalMetadata.correctionValue
                ) {
                    rule.runtimeKey = keyNew;
                    rule.correctionValue =
                        eventPropertyPrimitive.additionalMetadata.correctionValue;
                    rule.operator =
                        eventPropertyPrimitive.additionalMetadata.operator;

                    result.push(rule);
                }
            } else if (eventProperty instanceof EventPropertyNested) {
                const tmpResults: CorrectionValueTransformationRuleDescription[] =
                    this.getCorrectionValueRules(
                        (eventProperty as EventPropertyNested).eventProperties,
                        oldEventSchema,
                        newEventSchema,
                    );
                result = result.concat(tmpResults);
            }
        }

        const filteredResult: CorrectionValueTransformationRuleDescription[] =
            [];
        for (const res of result) {
            if (res.correctionValue) {
                filteredResult.push(res);
            }
        }

        return filteredResult;
    }
}
