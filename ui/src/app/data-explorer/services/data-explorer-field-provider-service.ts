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
import { FieldProvider } from '../models/dataview-dashboard.model';
import {
    DataExplorerField,
    EventProperty,
    EventPropertyPrimitive,
    EventPropertyUnion,
    FieldConfig,
    SourceConfig,
} from '@streampipes/platform-services';

@Injectable({ providedIn: 'root' })
export class DataExplorerFieldProviderService {
    public generateFieldLists(sourceConfigs: SourceConfig[]): FieldProvider {
        const provider: FieldProvider = {
            allFields: [],
            numericFields: [],
            booleanFields: [],
            dimensionFields: [],
            nonNumericFields: [],
        };

        sourceConfigs.forEach((sourceConfig, sourceIndex) => {
            sourceConfig.queryConfig.fields
                .filter(field => field.selected)
                .forEach(field => {
                    this.addField(
                        sourceConfig.measureName,
                        sourceConfig.measure.eventSchema.eventProperties,
                        sourceIndex,
                        field,
                        provider,
                        sourceConfig.queryType === 'aggregated' ||
                            sourceConfig.queryType === 'single',
                    );
                });
        });

        return provider;
    }

    private addField(
        measure: string,
        eventProperties: EventPropertyUnion[],
        sourceIndex: number,
        fieldConfig: FieldConfig,
        provider: FieldProvider,
        useAggregations: boolean,
    ) {
        const property: EventPropertyUnion = eventProperties.find(
            p => p.runtimeName === fieldConfig.runtimeName,
        );

        if (!useAggregations) {
            this.addSingleField(
                measure,
                property,
                sourceIndex,
                fieldConfig,
                provider,
            );
        } else {
            fieldConfig.aggregations.forEach(agg =>
                this.addSingleField(
                    measure,
                    property,
                    sourceIndex,
                    fieldConfig,
                    provider,
                    agg,
                ),
            );
        }
    }

    private addSingleField(
        measure: string,
        property: EventPropertyUnion,
        sourceIndex: number,
        fieldConfig: FieldConfig,
        provider: FieldProvider,
        aggregation?: string,
    ) {
        const field: DataExplorerField = {
            runtimeName: fieldConfig.runtimeName,
            measure,
            aggregation,
            fullDbName: this.makeFullDbName(fieldConfig, aggregation),
            sourceIndex,
            fieldCharacteristics: {
                dimension: this.isDimensionProperty(property),
                numeric: this.isNumber(property) || aggregation === 'COUNT',
                binary: this.isBoolean(property),
                semanticTypes: property.domainProperties,
            },
        };
        provider.allFields.push(field);

        if (field.fieldCharacteristics.numeric) {
            provider.numericFields.push(field);
        } else {
            provider.nonNumericFields.push(field);
        }

        if (field.fieldCharacteristics.binary) {
            provider.booleanFields.push(field);
        }

        if (this.isTimestamp(property)) {
            provider.primaryTimestampField = field;
        }

        if (field.fieldCharacteristics.dimension) {
            provider.dimensionFields.push(field);
        }
    }

    public isDimensionProperty(p: EventProperty): boolean {
        return p.propertyScope === 'DIMENSION_PROPERTY';
    }

    public isBoolean(p: EventPropertyUnion): boolean {
        return (
            this.isPrimitive(p) &&
            (p as EventPropertyPrimitive).runtimeType ===
                'http://www.w3.org/2001/XMLSchema#boolean'
        );
    }

    public isString(p: EventPropertyUnion): boolean {
        return (
            this.isPrimitive(p) &&
            (p as EventPropertyPrimitive).runtimeType ===
                'http://www.w3.org/2001/XMLSchema#string'
        );
    }

    public isTimestamp(p: EventProperty) {
        return p.domainProperties.some(
            dp => dp === 'http://schema.org/DateTime',
        );
    }

    public getAddedFields(
        currentFields: DataExplorerField[],
        newFields: DataExplorerField[],
    ): DataExplorerField[] {
        return this.getMissingFields(newFields, currentFields);
    }

    public getRemovedFields(
        currentFields: DataExplorerField[],
        newFields: DataExplorerField[],
    ): DataExplorerField[] {
        return this.getMissingFields(currentFields, newFields);
    }

    public getMissingFields(
        origin: DataExplorerField[],
        target: DataExplorerField[],
    ): DataExplorerField[] {
        return origin.filter(
            field =>
                !target.find(
                    newField => newField.fullDbName === field.fullDbName,
                ),
        );
    }

    isNumber(p: EventPropertyUnion): boolean {
        if (this.isPrimitive(p)) {
            const runtimeType = (p as EventPropertyPrimitive).runtimeType;

            return (
                runtimeType === 'http://schema.org/Number' ||
                runtimeType === 'http://www.w3.org/2001/XMLSchema#float' ||
                runtimeType === 'http://www.w3.org/2001/XMLSchema#double' ||
                runtimeType === 'http://www.w3.org/2001/XMLSchema#integer' ||
                runtimeType === 'http://www.w3.org/2001/XMLSchema#long'
            );
        } else {
            return false;
        }
    }

    private isPrimitive(property: any): boolean {
        return (
            property instanceof EventPropertyPrimitive ||
            property['@class'] ===
                'org.apache.streampipes.model.schema.EventPropertyPrimitive'
        );
    }

    private makeFullDbName(
        fieldConfig: FieldConfig,
        aggregation?: string,
    ): string {
        const prefix = aggregation ? aggregation.toLowerCase() + '_' : '';
        return prefix + fieldConfig.runtimeName;
    }

    getSelectedField(
        currentlySelectedField: DataExplorerField,
        availableFields: DataExplorerField[],
        getDefaultField: () => DataExplorerField,
    ): DataExplorerField {
        if (
            currentlySelectedField !== undefined &&
            this.existsField(currentlySelectedField, availableFields)
        ) {
            return currentlySelectedField;
        } else {
            return getDefaultField();
        }
    }

    getSelectedFields(
        currentlySelectedFields: DataExplorerField[],
        availableFields: DataExplorerField[],
        getDefaultFields: () => DataExplorerField[],
    ): DataExplorerField[] {
        if (currentlySelectedFields !== undefined) {
            return currentlySelectedFields.filter(field =>
                availableFields.find(f => f.fullDbName === field.fullDbName),
            );
        } else {
            return getDefaultFields();
        }
    }

    existsField(
        currentlySelectedField: DataExplorerField,
        fieldsToSearch: DataExplorerField[],
    ): boolean {
        return (
            fieldsToSearch.find(field => {
                return this.isEqualField(currentlySelectedField, field);
            }) !== undefined
        );
    }

    isEqualField(field1: DataExplorerField, field2: DataExplorerField) {
        return field1.fullDbName === field2.fullDbName;
    }
}
