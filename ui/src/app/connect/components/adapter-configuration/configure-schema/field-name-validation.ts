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

export interface FieldNameValidationResult {
    invalidFieldNames: string[];
    warningFieldNames: string[];
}

const INVALID_FIELD_NAME_CHARACTERS_REGEX = /[,";]/;
const RECOMMENDED_FIELD_NAME_REGEX = /^[A-Za-z0-9_-]+$/;

export function validateFieldNames(event: unknown): FieldNameValidationResult {
    const invalidFieldNames = new Set<string>();
    const warningFieldNames = new Set<string>();

    validateFieldNamesRecursively(
        event,
        '',
        invalidFieldNames,
        warningFieldNames,
    );

    return {
        invalidFieldNames: Array.from(invalidFieldNames),
        warningFieldNames: Array.from(warningFieldNames),
    };
}

function validateFieldNamesRecursively(
    value: unknown,
    currentPath: string,
    invalidFieldNames: Set<string>,
    warningFieldNames: Set<string>,
): void {
    if (Array.isArray(value)) {
        value.forEach(item =>
            validateFieldNamesRecursively(
                item,
                currentPath ? `${currentPath}[]` : '[]',
                invalidFieldNames,
                warningFieldNames,
            ),
        );
    } else if (isRecord(value)) {
        Object.entries(value).forEach(([fieldName, fieldValue]) => {
            const fieldPath = currentPath
                ? `${currentPath}.${fieldName}`
                : fieldName;

            if (INVALID_FIELD_NAME_CHARACTERS_REGEX.test(fieldName)) {
                invalidFieldNames.add(fieldPath);
            } else if (!RECOMMENDED_FIELD_NAME_REGEX.test(fieldName)) {
                warningFieldNames.add(fieldPath);
            }

            validateFieldNamesRecursively(
                fieldValue,
                fieldPath,
                invalidFieldNames,
                warningFieldNames,
            );
        });
    }
}

function isRecord(value: unknown): value is Record<string, unknown> {
    return (
        value !== null &&
        typeof value === 'object' &&
        Object.prototype.toString.call(value) === '[object Object]'
    );
}
