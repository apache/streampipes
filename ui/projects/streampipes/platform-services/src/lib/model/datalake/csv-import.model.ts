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

import { EventSchema } from '../gen/streampipes-model';

export type CsvImportTargetMode = 'NEW' | 'EXISTING';
export type CsvRuntimeType = 'STRING' | 'BOOLEAN' | 'LONG' | 'FLOAT';

export interface CsvImportConfiguration {
    delimiter: string;
    decimalSeparator: '.' | ',';
    hasHeader: boolean;
    timestampFormat?: string;
}

export interface CsvImportTarget {
    mode: CsvImportTargetMode;
    measurementName: string;
}

export interface CsvImportColumn {
    csvColumn: string;
    runtimeName: string;
    runtimeType: CsvRuntimeType;
    propertyScope?: string;
    semanticType?: string;
    label?: string;
    description?: string;
    inferredType?: CsvRuntimeType;
    timestampCandidate?: boolean;
}

export interface CsvImportValidationMessage {
    field: string;
    message: string;
}

export interface CsvImportPreviewRequest {
    fileName?: string;
    csvConfig: CsvImportConfiguration;
    headers: string[];
    rows: string[][];
    target?: CsvImportTarget;
}

export interface CsvImportPreviewResult {
    headers: string[];
    previewRows: string[][];
    columns: CsvImportColumn[];
    guessedEventSchema: EventSchema;
    timestampCandidates: string[];
    valid: boolean;
    validationMessages: CsvImportValidationMessage[];
}

export interface CsvImportSchemaValidationRequest {
    target: CsvImportTarget;
    timestampColumn: string;
    columns: CsvImportColumn[];
}

export type CsvImportSchemaIssueType =
    | 'TIMESTAMP_COLUMN_MISMATCH'
    | 'COLUMN_NAME_MISMATCH'
    | 'COLUMN_TYPE_MISMATCH'
    | 'COLUMN_SCOPE_MISMATCH';

export interface CsvImportSchemaIssue {
    type: CsvImportSchemaIssueType;
    columnName?: string;
    expected?: string | null;
    actual?: string | null;
}

export interface CsvImportSchemaValidationResult {
    valid: boolean;
    validationMessages: CsvImportValidationMessage[];
    issues: CsvImportSchemaIssue[];
}

export interface CsvImportRequest {
    csvConfig: CsvImportConfiguration;
    headers: string[];
    rows: string[][];
    target: CsvImportTarget;
    timestampColumn: string;
    columns: CsvImportColumn[];
}

export interface CsvImportResult {
    measurementName: string;
    measurementId: string;
    createdNewMeasurement: boolean;
    importedRowCount: number;
    validationMessages: CsvImportValidationMessage[];
}
