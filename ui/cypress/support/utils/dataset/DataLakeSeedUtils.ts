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

import * as CSV from 'csv-string';

type CsvRuntimeType = 'STRING' | 'BOOLEAN' | 'LONG' | 'FLOAT';
type CsvImportTargetMode = 'NEW' | 'EXISTING';

interface CsvImportConfiguration {
    delimiter: string;
    decimalSeparator: '.' | ',';
    hasHeader: boolean;
}

interface CsvImportColumn {
    csvColumn: string;
    runtimeName: string;
    runtimeType: CsvRuntimeType;
    propertyScope?: string;
    semanticType?: string;
    inferredType?: CsvRuntimeType;
    timestampCandidate?: boolean;
}

interface CsvImportTarget {
    mode: CsvImportTargetMode;
    measurementName: string;
}

interface CsvImportPreviewResult {
    headers: string[];
    previewRows: string[][];
    columns: CsvImportColumn[];
}

interface CsvImportResult {
    importedRowCount: number;
    validationMessages: Array<{ field: string; message: string }>;
}

interface ColumnOverride {
    runtimeName?: string;
    runtimeType?: CsvRuntimeType;
    propertyScope?: string;
    semanticType?: string;
}

interface CsvFixtureImportOptions {
    fixture: string;
    measurementName: string;
    delimiter?: string;
    decimalSeparator?: '.' | ',';
    timestampColumn?: string;
    columnOverrides?: Record<string, ColumnOverride>;
}

interface JsonArrayFixtureImportOptions {
    fixture: string;
    measurementName: string;
    timestampColumn?: string;
    columnOverrides?: Record<string, ColumnOverride>;
}

interface ImportRequest {
    csvConfig: CsvImportConfiguration;
    headers: string[];
    rows: string[][];
    target: CsvImportTarget;
    timestampColumn: string;
    columns: CsvImportColumn[];
}

export class DataLakeSeedUtils {
    private static readonly TIMESTAMP_SEMANTIC_TYPE =
        'http://schema.org/DateTime';

    public static importCsvFixture(
        options: CsvFixtureImportOptions,
    ): Cypress.Chainable<CsvImportResult> {
        const delimiter = options.delimiter ?? ';';
        const decimalSeparator = options.decimalSeparator ?? '.';

        return cy.fixture(options.fixture, 'utf8').then((content: string) => {
            const parseCsv = CSV.parse as any;
            const parsedCsv = parseCsv(content, delimiter);
            const headers = parsedCsv[0];
            const rows = parsedCsv.slice(1);
            const timestampColumn = options.timestampColumn ?? headers[0];

            return this.previewAndImport({
                headers,
                rows,
                csvConfig: {
                    delimiter,
                    decimalSeparator,
                    hasHeader: true,
                },
                measurementName: options.measurementName,
                timestampColumn,
                columnOverrides: options.columnOverrides,
            });
        });
    }

    public static importJsonArrayFixture(
        options: JsonArrayFixtureImportOptions,
    ): Cypress.Chainable<CsvImportResult> {
        return cy.fixture(options.fixture).then((records: Array<any>) => {
            const headers = this.extractHeaders(records);
            const rows = records.map(record =>
                headers.map(header =>
                    this.serializeCell(record ? record[header] : undefined),
                ),
            );
            const timestampColumn = options.timestampColumn ?? headers[0];

            return this.previewAndImport({
                headers,
                rows,
                csvConfig: {
                    delimiter: ';',
                    decimalSeparator: '.',
                    hasHeader: true,
                },
                measurementName: options.measurementName,
                timestampColumn,
                columnOverrides: options.columnOverrides,
            });
        });
    }

    private static previewAndImport(options: {
        headers: string[];
        rows: string[][];
        csvConfig: CsvImportConfiguration;
        measurementName: string;
        timestampColumn: string;
        columnOverrides?: Record<string, ColumnOverride>;
    }): Cypress.Chainable<CsvImportResult> {
        const token = window.localStorage.getItem('auth-token');
        const target = {
            mode: 'NEW' as CsvImportTargetMode,
            measurementName: options.measurementName,
        };

        return cy
            .request<CsvImportPreviewResult>({
                method: 'POST',
                url: '/streampipes-backend/api/v4/datalake/import/preview',
                body: {
                    csvConfig: options.csvConfig,
                    headers: options.headers,
                    rows: options.rows,
                    target,
                },
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            })
            .then(previewResponse => {
                const columns = this.buildColumns(
                    previewResponse.body.columns,
                    options.timestampColumn,
                    options.columnOverrides ?? {},
                );

                const request: ImportRequest = {
                    csvConfig: options.csvConfig,
                    headers: options.headers,
                    rows: options.rows,
                    target,
                    timestampColumn: options.timestampColumn,
                    columns,
                };

                return cy
                    .request<CsvImportResult>({
                        method: 'POST',
                        url: '/streampipes-backend/api/v4/datalake/import',
                        body: request,
                        headers: {
                            Authorization: `Bearer ${token}`,
                        },
                    })
                    .then(importResponse => {
                        expect(
                            importResponse.body.validationMessages,
                            'import validation messages',
                        ).to.have.length(0);
                        expect(
                            importResponse.body.importedRowCount,
                            'imported row count',
                        ).to.equal(options.rows.length);
                        return importResponse.body;
                    });
            });
    }

    private static buildColumns(
        previewColumns: CsvImportColumn[],
        timestampColumn: string,
        columnOverrides: Record<string, ColumnOverride>,
    ) {
        return previewColumns.map(previewColumn => {
            const override = columnOverrides[previewColumn.csvColumn] ?? {};
            if (previewColumn.csvColumn === timestampColumn) {
                return {
                    ...previewColumn,
                    runtimeName:
                        override.runtimeName ?? previewColumn.runtimeName,
                    runtimeType: override.runtimeType ?? 'LONG',
                    propertyScope: 'HEADER_PROPERTY',
                    semanticType:
                        override.semanticType ??
                        DataLakeSeedUtils.TIMESTAMP_SEMANTIC_TYPE,
                };
            }

            return {
                ...previewColumn,
                runtimeName: override.runtimeName ?? previewColumn.runtimeName,
                runtimeType:
                    override.runtimeType ??
                    this.defaultRuntimeType(previewColumn.inferredType),
                propertyScope: override.propertyScope ?? 'MEASUREMENT_PROPERTY',
                semanticType: override.semanticType ?? undefined,
            };
        });
    }

    private static defaultRuntimeType(
        inferredType: CsvRuntimeType = 'STRING',
    ): CsvRuntimeType {
        if (inferredType === 'LONG' || inferredType === 'FLOAT') {
            return 'FLOAT';
        }

        return inferredType;
    }

    private static extractHeaders(records: Array<any>) {
        const headers: string[] = [];
        records.forEach(record => {
            Object.keys(record || {}).forEach(key => {
                if (headers.indexOf(key) === -1) {
                    headers.push(key);
                }
            });
        });
        return headers;
    }

    private static serializeCell(value: any): string {
        if (value === undefined || value === null) {
            return '';
        }

        return String(value);
    }
}
