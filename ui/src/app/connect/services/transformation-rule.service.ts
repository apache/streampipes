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
    CorrectionValueTransformationRuleDescription,
    CreateNestedRuleDescription,
    DeleteRuleDescription,
    EventProperty,
    EventPropertyNested,
    EventPropertyPrimitive,
    EventPropertyUnion,
    EventSchema,
    MoveRuleDescription,
    RenameRuleDescription,
    ChangeDatatypeTransformationRuleDescription,
    TimestampTranfsformationRuleDescription,
    TransformationRuleDescriptionUnion,
    UnitTransformRuleDescription,
} from '@streampipes/platform-services';
import { TimestampTransformationRuleMode } from '../model/TimestampTransformationRuleMode';

@Injectable()
export class TransformationRuleService {
    private oldEventSchema: EventSchema = null;
    private newEventSchema: EventSchema = null;

    constructor() {}

    public setOldEventSchema(oldEventSchema: EventSchema) {
        this.oldEventSchema = oldEventSchema;
    }

    public setNewEventSchema(newEventSchema: EventSchema) {
        this.newEventSchema = newEventSchema;
    }

    public getTransformationRuleDescriptions(): TransformationRuleDescriptionUnion[] {
        let transformationRuleDescription: TransformationRuleDescriptionUnion[] =
            [];

        if (this.oldEventSchema == null || this.newEventSchema == null) {
            console.log('Old and new schema must be defined');
        } else {
            const addedTimestampProperties = this.getTimestampProperty(
                this.newEventSchema.eventProperties,
            );
            if (addedTimestampProperties) {
                // add to old event schema for the case users moved the property to a nested property
                this.oldEventSchema.eventProperties.push(
                    addedTimestampProperties,
                );

                const timestampRuleDescription: AddTimestampRuleDescription =
                    new AddTimestampRuleDescription();
                timestampRuleDescription['@class'] =
                    'org.apache.streampipes.model.connect.rules.value.AddTimestampRuleDescription';
                timestampRuleDescription.runtimeKey =
                    addedTimestampProperties.runtimeName;
                transformationRuleDescription.push(timestampRuleDescription);
            }

            const staticValueProperties = this.getStaticValueProperties(
                this.newEventSchema.eventProperties,
            );
            for (const ep of staticValueProperties) {
                this.oldEventSchema.eventProperties.push(ep);
                const rule: AddValueTransformationRuleDescription =
                    new AddValueTransformationRuleDescription();
                rule['@class'] =
                    'org.apache.streampipes.model.connect.rules.value.AddValueTransformationRuleDescription';
                rule.runtimeKey = ep.runtimeName;
                rule.staticValue = (ep as any).staticValue;
                transformationRuleDescription.push(rule);
            }

            // Scale
            transformationRuleDescription = transformationRuleDescription
                .concat(
                    this.getCorrectionValueRules(
                        this.newEventSchema.eventProperties,
                        this.oldEventSchema,
                        this.newEventSchema,
                    ),
                )
                .concat(
                    this.getRenameRules(
                        this.newEventSchema.eventProperties,
                        this.oldEventSchema,
                        this.newEventSchema,
                    ),
                )
                .concat(
                    this.getCreateNestedRules(
                        this.newEventSchema.eventProperties,
                        this.oldEventSchema,
                        this.newEventSchema,
                    ),
                )
                .concat(
                    this.getMoveRules(
                        this.newEventSchema.eventProperties,
                        this.oldEventSchema,
                        this.newEventSchema,
                    ),
                )
                .concat(
                    this.getDeleteRules(
                        this.newEventSchema.eventProperties,
                        this.oldEventSchema,
                        this.newEventSchema,
                    ),
                )
                .concat(
                    this.getUnitTransformRules(
                        this.newEventSchema.eventProperties,
                        this.oldEventSchema,
                        this.newEventSchema,
                    ),
                )
                .concat(
                    this.getTimestampTransformRules(
                        this.newEventSchema.eventProperties,
                        this.oldEventSchema,
                        this.newEventSchema,
                    ),
                )
                .concat(
                    this.getDatatypeTransformRules(
                        this.newEventSchema.eventProperties,
                        this.oldEventSchema,
                        this.newEventSchema,
                    ),
                );

            return transformationRuleDescription;
        }
    }

    public getMoveRules(
        newEventProperties: EventProperty[],
        oldEventSchema: EventSchema,
        newEventSchema: EventSchema,
    ): MoveRuleDescription[] {
        let result: MoveRuleDescription[] = [];

        for (const eventProperty of newEventProperties) {
            if (eventProperty instanceof EventPropertyNested) {
                const tmpResults: MoveRuleDescription[] = this.getMoveRules(
                    (eventProperty as EventPropertyNested).eventProperties,
                    oldEventSchema,
                    newEventSchema,
                );
                result = result.concat(tmpResults);
            }
            const keyOld: string = this.getCompleteRuntimeNameKey(
                oldEventSchema.eventProperties,
                eventProperty.elementId,
            );
            const keyNew: string = this.getCompleteRuntimeNameKey(
                newEventSchema.eventProperties,
                eventProperty.elementId,
            );

            // get prefix
            if (keyOld && keyNew) {
                const keyOldPrefix: string = keyOld.substr(
                    0,
                    keyOld.lastIndexOf('.'),
                );
                const keyNewPrefix: string = keyNew.substr(
                    0,
                    keyNew.lastIndexOf('.'),
                );

                if (keyOldPrefix !== keyNewPrefix) {
                    // old key is equals old route and new name
                    let keyOfOldValue = '';
                    keyOfOldValue =
                        keyOldPrefix === ''
                            ? keyNew.substr(
                                  keyNew.lastIndexOf('.') + 1,
                                  keyNew.length,
                              )
                            : keyOldPrefix +
                              '.' +
                              keyNew.substr(
                                  keyNew.lastIndexOf('.') + 1,
                                  keyNew.length,
                              );
                    const rule: MoveRuleDescription = new MoveRuleDescription();
                    rule['@class'] =
                        'org.apache.streampipes.model.connect.rules.schema.MoveRuleDescription';
                    rule.oldRuntimeKey = keyOfOldValue;
                    rule.newRuntimeKey = keyNewPrefix;
                    result.push(rule);
                }
            }
        }

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
        newEventProperties: EventProperty[],
        oldEventSchema: EventSchema,
        newEventSchema: EventSchema,
    ): DeleteRuleDescription[] {
        let resultKeys: string[] = [];

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

        const uniqEs6 = arrArg => {
            return arrArg.filter((elem, pos, arr) => {
                let r = true;
                for (const a of arr) {
                    if (elem.startsWith(a) && a !== elem) {
                        r = false;
                    }
                }

                return r;
            });
        };

        resultKeys = uniqEs6(resultKeys);

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

                // let unitTransformation: UnitTransformRuleDescription = new UnitTransformRuleDescription();
                // unitTransformation.fromUnitRessourceURL = eventPropertyPrimitive.
                const rule: UnitTransformRuleDescription =
                    new UnitTransformRuleDescription();
                rule['@class'] =
                    'org.apache.streampipes.model.connect.rules.value.UnitTransformRuleDescription';
                rule.runtimeKey = keyNew;
                rule.fromUnitRessourceURL = (
                    eventPropertyPrimitive as any
                ).oldMeasurementUnit;
                rule.toUnitRessourceURL = (
                    eventPropertyPrimitive as any
                ).measurementUnitTmp;

                result.push(rule);
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

        const filteredResult: UnitTransformRuleDescription[] = [];
        for (const res of result) {
            if (res.fromUnitRessourceURL !== res.toUnitRessourceURL) {
                filteredResult.push(res);
            }
        }

        return filteredResult;
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
                        result = eventProperty.runtimeName + '.' + methodResult;
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
            const keyElements = completeKey.split('.');

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
    ): EventPropertyUnion[] {
        let result = [];

        for (const eventProperty of eventProperties) {
            if (
                eventProperty.elementId.startsWith(
                    'http://eventProperty.de/staticValue/',
                )
            ) {
                return [eventProperty];
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
                    rule.mode = (
                        eventProperty as any
                    ).timestampTransformationMode;
                    rule.formatString = (
                        eventProperty as any
                    ).timestampTransformationFormatString;
                    rule.multiplier = (
                        eventProperty as any
                    ).timestampTransformationMultiplier;
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
            dp => dp === 'http://schema.org/DateTime',
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
                rule.runtimeKey = keyNew;
                rule.correctionValue = (
                    eventPropertyPrimitive as any
                ).correctionValue;
                rule.operator = (eventPropertyPrimitive as any).operator;

                result.push(rule);
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
