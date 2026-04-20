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
    EventProperty,
    EventPropertyList,
    EventPropertyNested,
    EventPropertyPrimitive,
    EventPropertyUnion,
} from '@streampipes/platform-services';

@Injectable({ providedIn: 'root' })
export class EventSchemaDiffService {
    // Copies user-configurable fields from old to new for matching properties.
    public applyUserConfiguration(
        oldEventProperties: EventPropertyUnion[] | undefined,
        newEventProperties: EventPropertyUnion[] | undefined,
    ): void {
        const pairs = this.getMatchingRuntimeTypePairs(
            oldEventProperties,
            newEventProperties,
        );

        for (const { oldProperty, newProperty } of pairs) {
            newProperty.semanticType = oldProperty.semanticType;
            newProperty.additionalMetadata = oldProperty.additionalMetadata;
            newProperty.description = oldProperty.description;
            newProperty.label = oldProperty.label;
            newProperty.propertyScope = oldProperty.propertyScope;

            if (
                this.isPrimitiveProperty(newProperty) &&
                this.isPrimitiveProperty(oldProperty)
            ) {
                const newPrimitive = newProperty as EventPropertyPrimitive;
                const oldPrimitive = oldProperty as EventPropertyPrimitive;
                newPrimitive.measurementUnit = oldPrimitive.measurementUnit;
                newPrimitive.runtimeType = oldPrimitive.runtimeType;
            }
        }
    }

    // Returns only matching old/new pairs where runtimeType is unchanged.
    private getMatchingRuntimeTypePairs(
        oldEventProperties: EventPropertyUnion[] | undefined,
        newEventProperties: EventPropertyUnion[] | undefined,
    ): Array<{
        oldProperty: EventProperty;
        newProperty: EventProperty;
    }> {
        const diff = this.compareEventSchemas(
            oldEventProperties,
            newEventProperties,
        );

        return diff
            .filter(entry => {
                if (!entry.oldProperty || !entry.newProperty) {
                    return false;
                }
                const oldDataType = this.getDataType(entry.oldProperty);
                const newDataType = this.getDataType(entry.newProperty);
                if (oldDataType !== undefined && newDataType !== undefined) {
                    return oldDataType === newDataType;
                }

                const oldIsPrimitive = this.isPrimitiveProperty(
                    entry.oldProperty,
                );
                const newIsPrimitive = this.isPrimitiveProperty(
                    entry.newProperty,
                );
                return !oldIsPrimitive && !newIsPrimitive;
            })
            .map(entry => ({
                oldProperty: entry.oldProperty as EventProperty,
                newProperty: entry.newProperty as EventProperty,
            }));
    }

    // Returns a path-based comparison of old vs. new event properties for diffing or merge logic.
    private compareEventSchemas(
        oldEventProperties: EventPropertyUnion[] | undefined,
        newEventProperties: EventPropertyUnion[] | undefined,
    ): Array<{
        path: string;
        oldProperty?: EventPropertyUnion;
        newProperty?: EventPropertyUnion;
    }> {
        const oldMap = this.buildEventPropertyPathMap(oldEventProperties);
        const newMap = this.buildEventPropertyPathMap(newEventProperties);

        const paths = new Set<string>();
        oldMap.forEach((_value, key) => paths.add(key));
        newMap.forEach((_value, key) => paths.add(key));

        return Array.from(paths)
            .sort()
            .map(path => ({
                path,
                oldProperty: oldMap.get(path),
                newProperty: newMap.get(path),
            }));
    }

    private buildEventPropertyPathMap(
        eventProperties: EventPropertyUnion[] | undefined,
        basePath: string = '',
    ): Map<string, EventPropertyUnion> {
        const map = new Map<string, EventPropertyUnion>();

        if (!eventProperties) {
            return map;
        }

        for (const eventProperty of eventProperties) {
            const path = this.buildEventPropertyPath(
                basePath,
                eventProperty?.runtimeName,
            );
            if (path) {
                map.set(path, eventProperty);
            }

            if (this.isNestedProperty(eventProperty)) {
                const childMap = this.buildEventPropertyPathMap(
                    eventProperty.eventProperties,
                    path,
                );
                childMap.forEach((value, key) => map.set(key, value));
            } else if (this.isListProperty(eventProperty)) {
                const listPath = path ? `${path}[]` : '[]';
                const childMap = this.buildEventPropertyPathMap(
                    [eventProperty.eventProperty],
                    listPath,
                );
                childMap.forEach((value, key) => map.set(key, value));
            }
        }

        return map;
    }

    private buildEventPropertyPath(
        basePath: string,
        runtimeName?: string,
    ): string {
        if (!runtimeName) {
            return basePath;
        }
        return basePath ? `${basePath}.${runtimeName}` : runtimeName;
    }

    private isNestedProperty(
        eventProperty: EventPropertyUnion,
    ): eventProperty is EventPropertyNested {
        return (
            eventProperty?.['@class'] ===
            'org.apache.streampipes.model.schema.EventPropertyNested'
        );
    }

    private isListProperty(
        eventProperty: EventPropertyUnion,
    ): eventProperty is EventPropertyList {
        return (
            eventProperty?.['@class'] ===
            'org.apache.streampipes.model.schema.EventPropertyList'
        );
    }

    private isPrimitiveProperty(
        eventProperty: EventProperty,
    ): eventProperty is EventPropertyPrimitive {
        return (
            eventProperty?.['@class'] ===
            'org.apache.streampipes.model.schema.EventPropertyPrimitive'
        );
    }

    private getDataType(eventProperty: EventProperty): string | undefined {
        const originType = eventProperty?.additionalMetadata?.originType;
        if (originType !== undefined) {
            return originType;
        }
        if (this.isPrimitiveProperty(eventProperty)) {
            return eventProperty.runtimeType;
        }
        return undefined;
    }
}
