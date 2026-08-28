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

import '@angular/compiler';
import { CsvRuntimeType, DataType } from '@streampipes/platform-services';
import { describe, expect, it } from 'vitest';
import { CsvImportDialogComponent } from './csv-import-dialog.component';

interface CsvImportTypeMappings {
    toSupportedColumnType(type: string): CsvRuntimeType;
    toRuntimeType(type: string): string;
    fromRuntimeType(type: string): CsvRuntimeType;
}

describe('CsvImportDialogComponent type mappings', () => {
    const component = Object.create(
        CsvImportDialogComponent.prototype,
    ) as CsvImportTypeMappings;
    const runtimeTypes: Array<[CsvRuntimeType, string]> = [
        ['STRING', DataType.STRING],
        ['BOOLEAN', DataType.BOOLEAN],
        ['INTEGER', DataType.INTEGER],
        ['LONG', DataType.LONG],
        ['FLOAT', DataType.FLOAT],
        ['DOUBLE', DataType.DOUBLE],
    ];

    it('preserves every supported CSV type for existing datasets', () => {
        runtimeTypes.forEach(([csvType]) => {
            expect(component.toSupportedColumnType(csvType)).toBe(csvType);
        });
    });

    it('maps every supported CSV type in both directions', () => {
        runtimeTypes.forEach(([csvType, xsdType]) => {
            expect(component.toRuntimeType(csvType)).toBe(xsdType);
            expect(component.fromRuntimeType(xsdType)).toBe(csvType);
        });
    });
});
