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

import { CommonModule } from '@angular/common';
import {
    Component,
    computed,
    inject,
    Input,
    signal,
    viewChild,
} from '@angular/core';
import { toSignal, takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
    FormBuilder,
    FormsModule,
    ReactiveFormsModule,
    Validators,
} from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatDivider } from '@angular/material/divider';
import { MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatOption, MatSelect } from '@angular/material/select';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatStep, MatStepLabel, MatStepper } from '@angular/material/stepper';
import { TranslatePipe } from '@ngx-translate/core';
import {
    CsvImportColumn,
    CsvImportConfiguration,
    CsvImportPreviewRequest,
    CsvImportPreviewResult,
    CsvImportRequest,
    CsvImportResult,
    CsvImportSchemaIssue,
    CsvImportSchemaValidationRequest,
    CsvImportSchemaValidationResult,
    CsvImportTarget,
    CsvImportValidationMessage,
    CsvRuntimeType,
    DataType,
    DatalakeRestService,
    EventPropertyPrimitive,
    SemanticType,
} from '@streampipes/platform-services';
import {
    DialogRef,
    FormFieldComponent,
    SplitSectionComponent,
} from '@streampipes/shared-ui';
import { startWith } from 'rxjs';
import { CsvImportColumnModel, CsvImportColumnRole } from './csv-import.model';
import { CsvImportPreviewTableComponent } from './csv-import-preview-table/csv-import-preview-table.component';
import { CsvImportUploadStateComponent } from './csv-import-upload-state/csv-import-upload-state.component';

@Component({
    selector: 'sp-csv-import-dialog',
    templateUrl: './csv-import-dialog.component.html',
    styleUrls: ['./csv-import-dialog.component.scss'],
    imports: [
        CommonModule,
        FormsModule,
        ReactiveFormsModule,
        MatButton,
        MatCheckbox,
        FormFieldComponent,
        SplitSectionComponent,
        MatFormField,
        MatInput,
        MatSelect,
        MatOption,
        MatDivider,
        MatProgressSpinner,
        MatStepper,
        MatStep,
        MatStepLabel,
        TranslatePipe,
        CsvImportPreviewTableComponent,
        CsvImportUploadStateComponent,
    ],
})
export class CsvImportDialogComponent {
    @Input()
    measurementNames: string[] = [];

    readonly stepper = viewChild<MatStepper>('csvImportStepper');

    private readonly fb = inject(FormBuilder);
    private readonly dialogRef = inject(DialogRef<CsvImportDialogComponent>);
    private readonly datalakeRestService = inject(DatalakeRestService);

    private previewReloadTimeout?: ReturnType<typeof setTimeout>;
    private schemaValidationTimeout?: ReturnType<typeof setTimeout>;

    readonly fileName = signal('');
    readonly rawFileContent = signal('');
    readonly timestampFormat = signal('');
    readonly parsedHeaders = signal<string[]>([]);
    readonly parsedRows = signal<string[][]>([]);
    readonly previewResult = signal<CsvImportPreviewResult | undefined>(
        undefined,
    );
    readonly schemaValidationResult = signal<
        CsvImportSchemaValidationResult | undefined
    >(undefined);
    readonly importResult = signal<CsvImportResult | undefined>(undefined);
    readonly columnModels = signal<CsvImportColumnModel[]>([]);
    readonly previewLoading = signal(false);
    readonly importLoading = signal(false);
    readonly localMessages = signal<CsvImportValidationMessage[]>([]);
    readonly uploadMessages = signal<CsvImportValidationMessage[]>([]);

    readonly parseForm = this.fb.group({
        delimiter: [',' as string, Validators.required],
        decimalSeparator: ['.' as ',' | '.', Validators.required],
        hasHeader: [true, Validators.required],
    });

    readonly targetForm = this.fb.group({
        mode: ['NEW', Validators.required],
        newMeasurementName: [''],
        existingMeasurementName: [''],
    });

    private readonly parseFormValue = toSignal(
        this.parseForm.valueChanges.pipe(
            startWith(this.parseForm.getRawValue()),
        ),
        { initialValue: this.parseForm.getRawValue() },
    );

    private readonly targetFormValue = toSignal(
        this.targetForm.valueChanges.pipe(
            startWith(this.targetForm.getRawValue()),
        ),
        { initialValue: this.targetForm.getRawValue() },
    );

    readonly topMessages = computed(() => [
        ...(this.previewResult()?.validationMessages?.filter(
            message =>
                message.field !== 'columns' &&
                message.field !== 'timestampColumn' &&
                message.field !== 'schemaDetails',
        ) ?? []),
        ...this.localMessages(),
    ]);

    readonly hasPreview = computed(() => !!this.previewResult());

    readonly hasImportResult = computed(
        () => !!this.importResult()?.measurementName,
    );

    readonly previewRows = computed(
        () => this.previewResult()?.previewRows ?? [],
    );

    readonly targetMode = computed(
        () => this.targetFormValue().mode as 'NEW' | 'EXISTING',
    );

    readonly selectedTimestampColumn = computed(
        () =>
            this.columnModels().find(model =>
                SemanticType.isTimestamp(model.eventProperty),
            )?.column.runtimeName,
    );

    readonly canProceedToConfiguration = computed(
        () => this.isTargetValid() && !!this.rawFileContent(),
    );

    readonly currentTarget = computed<CsvImportTarget | undefined>(() => {
        const formValue = this.targetFormValue();

        if (this.targetMode() === 'NEW') {
            const measurementName = formValue.newMeasurementName?.trim();
            return measurementName
                ? { mode: 'NEW', measurementName }
                : undefined;
        }

        const measurementName = formValue.existingMeasurementName?.trim();
        return measurementName
            ? { mode: 'EXISTING', measurementName }
            : undefined;
    });

    readonly canImport = computed(
        () =>
            this.previewResult()?.valid === true &&
            !!this.currentTarget() &&
            !!this.selectedTimestampColumn() &&
            (this.targetMode() !== 'EXISTING' ||
                this.schemaValidationResult()?.valid === true),
    );

    readonly showTimestampWarning = computed(
        () => this.hasPreview() && !this.selectedTimestampColumn(),
    );

    readonly isTargetValid = computed(
        () => this.validateLocalTarget().length === 0,
    );

    readonly hasSchemaMismatch = computed(
        () =>
            this.targetMode() === 'EXISTING' &&
            !!this.schemaValidationResult()?.issues?.length,
    );

    readonly schemaMismatchSummary = computed(
        () =>
            'Imported columns must exactly match the existing measurement schema.',
    );

    readonly schemaMismatchDetails = computed(() =>
        (this.schemaValidationResult()?.issues ?? []).map(issue =>
            this.toSchemaIssueText(issue),
        ),
    );

    private readonly currentCsvConfig = computed<CsvImportConfiguration>(() => {
        const formValue = this.parseFormValue();

        return {
            delimiter: formValue.delimiter ?? ',',
            decimalSeparator: (formValue.decimalSeparator as ',' | '.') ?? '.',
            hasHeader: formValue.hasHeader ?? true,
            timestampFormat: this.timestampFormat().trim() || undefined,
        };
    });

    constructor() {
        this.parseForm.valueChanges.pipe(takeUntilDestroyed()).subscribe(() => {
            this.invalidatePreview();
            if (this.stepper()?.selectedIndex === 1) {
                this.schedulePreviewReload();
            }
        });

        this.targetForm.controls.mode.valueChanges
            .pipe(takeUntilDestroyed())
            .subscribe(mode => {
                if (mode === 'NEW') {
                    this.targetForm.controls.existingMeasurementName.setValue(
                        '',
                    );
                } else {
                    this.targetForm.controls.newMeasurementName.setValue('');
                }

                this.invalidatePreview();
            });

        this.targetForm.controls.newMeasurementName.valueChanges
            .pipe(takeUntilDestroyed())
            .subscribe(() => {
                this.invalidatePreview();
                if (this.stepper()?.selectedIndex === 1) {
                    this.schedulePreviewReload();
                }
            });

        this.targetForm.controls.existingMeasurementName.valueChanges
            .pipe(takeUntilDestroyed())
            .subscribe(() => {
                this.invalidatePreview();
                if (this.stepper()?.selectedIndex === 1) {
                    this.schedulePreviewReload();
                }
            });
    }

    onFileSelected(event: Event): void {
        const input = event.target as HTMLInputElement;
        const file = input.files?.[0];
        if (!file) {
            return;
        }

        this.fileName.set(file.name);
        this.timestampFormat.set('');

        const reader = new FileReader();
        reader.onload = () => {
            this.rawFileContent.set(`${reader.result ?? ''}`);
            this.invalidatePreview();
        };
        reader.readAsText(file);
    }

    nextStep(): void {
        if (this.stepper()?.selectedIndex !== 0) {
            return;
        }

        if (!this.canProceedToConfiguration()) {
            const messages = this.validateLocalTarget();
            if (!this.rawFileContent()) {
                messages.push({
                    field: 'file',
                    message: 'Please select a CSV file first.',
                });
            }
            this.localMessages.set(messages);
            return;
        }

        this.stepper()?.next();
        this.loadPreview();
    }

    previousStep(): void {
        this.stepper()?.previous();
    }

    startUpload(): void {
        this.uploadMessages.set([]);

        const messages = this.validateLocalTarget();
        if (!this.selectedTimestampColumn()) {
            messages.push({
                field: 'timestampColumn',
                message: 'Please select exactly one timestamp column.',
            });
        }

        this.localMessages.set(messages);
        if (messages.length > 0 || !this.canImport()) {
            return;
        }

        this.stepper()?.next();
        this.importData();
    }

    loadPreview(): void {
        this.localMessages.set([]);
        this.clearImportResult();

        if (!this.isTargetValid() || !this.currentTarget()) {
            this.localMessages.set([
                {
                    field: 'target.measurementName',
                    message:
                        'Please complete the target dataset selection first.',
                },
            ]);
            return;
        }

        if (!this.rawFileContent()) {
            this.localMessages.set([
                {
                    field: 'file',
                    message: 'Please select a CSV file first.',
                },
            ]);
            return;
        }

        try {
            const { headers, rows } = this.parseCsv();
            this.parsedHeaders.set(headers);
            this.parsedRows.set(rows);
        } catch {
            this.localMessages.set([
                {
                    field: 'file',
                    message:
                        'The CSV file could not be parsed with the current settings.',
                },
            ]);
            return;
        }

        this.previewLoading.set(true);
        this.datalakeRestService
            .previewImport(this.buildPreviewRequest(this.currentTarget()))
            .subscribe({
                next: preview => {
                    this.previewResult.set(preview);
                    this.columnModels.set(
                        preview.columns.map(column =>
                            this.toColumnModel(column),
                        ),
                    );
                    this.schemaValidationResult.set(undefined);
                    this.previewLoading.set(false);
                    this.validateSchema();
                },
                error: error => {
                    this.previewLoading.set(false);
                    this.previewResult.set(undefined);
                    this.schemaValidationResult.set(undefined);
                    this.localMessages.set([
                        {
                            field: 'preview',
                            message:
                                error?.error?.message ??
                                'Preview could not be generated.',
                        },
                    ]);
                },
            });
    }

    setColumnType(model: CsvImportColumnModel, type: CsvRuntimeType): void {
        model.eventProperty.runtimeType = this.toRuntimeType(type);
        model.column.runtimeType = type;

        if (this.isTimestampColumn(model)) {
            model.eventProperty.runtimeType = DataType.LONG;
            model.column.runtimeType = 'LONG';
        }

        this.syncColumn(model);
        this.columnModels.update(models => [...models]);
        this.scheduleSchemaValidation();
    }

    setColumnRole(
        model: CsvImportColumnModel,
        role: CsvImportColumnRole,
    ): void {
        if (role === 'TIMESTAMP') {
            this.columnModels().forEach(other => {
                if (SemanticType.isTimestamp(other.eventProperty)) {
                    other.eventProperty.semanticType = undefined;
                    if (
                        other.eventProperty.propertyScope === 'HEADER_PROPERTY'
                    ) {
                        other.eventProperty.propertyScope =
                            'MEASUREMENT_PROPERTY';
                    }
                    if (other.column.inferredType) {
                        other.eventProperty.runtimeType = this.toRuntimeType(
                            other.column.inferredType,
                        );
                    }
                    this.syncColumn(other);
                }
            });

            model.eventProperty.semanticType = SemanticType.TIMESTAMP;
            model.eventProperty.propertyScope = 'HEADER_PROPERTY';
            model.eventProperty.runtimeType = DataType.LONG;
            model.column.runtimeType = 'LONG';
        } else {
            model.eventProperty.semanticType = undefined;
            model.eventProperty.propertyScope = role;
            model.eventProperty.runtimeType = this.toRuntimeType(
                model.column.runtimeType ||
                    model.column.inferredType ||
                    'STRING',
            );
        }

        this.syncColumn(model);
        this.columnModels.update(models => [...models]);
        this.scheduleSchemaValidation();
    }

    updateTimestampFormat(timestampFormat: string): void {
        this.timestampFormat.set(timestampFormat);
        this.scheduleSchemaValidation();
    }

    importData(): void {
        const messages = this.validateLocalTarget();
        this.uploadMessages.set([]);

        if (!this.selectedTimestampColumn()) {
            messages.push({
                field: 'timestampColumn',
                message: 'Please select exactly one timestamp column.',
            });
        }

        this.localMessages.set(messages);
        if (messages.length > 0) {
            return;
        }

        this.importLoading.set(true);
        this.datalakeRestService
            .importCsvData(this.buildImportRequest())
            .subscribe({
                next: result => {
                    this.importResult.set(result);
                    this.importLoading.set(false);
                },
                error: error => {
                    this.importLoading.set(false);
                    const result = error?.error as CsvImportResult | undefined;
                    this.importResult.set(result);
                    this.uploadMessages.set(
                        result?.validationMessages?.length
                            ? result.validationMessages
                            : [
                                  {
                                      field: 'import',
                                      message:
                                          error?.error?.message ??
                                          'CSV import failed.',
                                  },
                              ],
                    );
                },
            });
    }

    close(refresh = false): void {
        this.dialogRef.close(refresh);
    }

    private parseCsv(): { headers: string[]; rows: string[][] } {
        const delimiter = this.normalizeDelimiter(
            this.parseFormValue().delimiter ?? ',',
        );
        const parsed = this.parseCsvContent(this.rawFileContent(), delimiter);
        const rows = parsed.filter(row =>
            row.some(cell => `${cell}`.trim() !== ''),
        );

        if (rows.length === 0) {
            throw new Error('CSV contains no rows');
        }

        const hasHeader = this.parseFormValue().hasHeader ?? true;
        let headers: string[];
        let contentRows: string[][];

        if (hasHeader) {
            headers = rows[0].map((header, index) =>
                this.normalizeHeader(
                    index === 0 ? this.stripBom(header) : header,
                    index,
                ),
            );
            contentRows = rows.slice(1);
        } else {
            headers = rows[0].map((_, index) => `column_${index + 1}`);
            contentRows = rows;
        }

        return { headers, rows: contentRows };
    }

    private normalizeDelimiter(value: string): string {
        return value === '\\t' ? '\t' : value;
    }

    private stripBom(value: string): string {
        return value.replace(/^\uFEFF/, '');
    }

    private normalizeHeader(value: string, index: number): string {
        const trimmed = value?.trim();
        return trimmed ? trimmed : `column_${index + 1}`;
    }

    private parseCsvContent(content: string, delimiter: string): string[][] {
        const rows: string[][] = [];
        let currentRow: string[] = [];
        let currentValue = '';
        let inQuotes = false;

        for (let i = 0; i < content.length; i++) {
            const char = content[i];
            const nextChar = content[i + 1];

            if (char === '"') {
                if (inQuotes && nextChar === '"') {
                    currentValue += '"';
                    i += 1;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (!inQuotes && char === delimiter) {
                currentRow.push(currentValue);
                currentValue = '';
            } else if (!inQuotes && (char === '\n' || char === '\r')) {
                if (char === '\r' && nextChar === '\n') {
                    i += 1;
                }
                currentRow.push(currentValue);
                rows.push(currentRow);
                currentRow = [];
                currentValue = '';
            } else {
                currentValue += char;
            }
        }

        currentRow.push(currentValue);
        rows.push(currentRow);
        return rows;
    }

    private buildPreviewRequest(
        target?: CsvImportTarget,
    ): CsvImportPreviewRequest {
        return {
            fileName: this.fileName(),
            csvConfig: this.currentCsvConfig(),
            headers: this.parsedHeaders(),
            rows: this.parsedRows(),
            target,
        };
    }

    private buildImportRequest(): CsvImportRequest {
        return {
            csvConfig: this.currentCsvConfig(),
            headers: this.parsedHeaders(),
            rows: this.parsedRows(),
            target: this.currentTarget()!,
            timestampColumn: this.selectedTimestampColumn()!,
            columns: this.columnModels().map(model => model.column),
        };
    }

    private buildSchemaValidationRequest():
        | CsvImportSchemaValidationRequest
        | undefined {
        if (!this.currentTarget() || !this.selectedTimestampColumn()) {
            return undefined;
        }

        return {
            target: this.currentTarget()!,
            timestampColumn: this.selectedTimestampColumn()!,
            columns: this.columnModels().map(model => model.column),
        };
    }

    private validateLocalTarget(): CsvImportValidationMessage[] {
        const messages: CsvImportValidationMessage[] = [];
        const targetFormValue = this.targetFormValue();

        if (this.targetMode() === 'NEW') {
            const name = targetFormValue.newMeasurementName?.trim();

            if (!name) {
                messages.push({
                    field: 'target.measurementName',
                    message: 'Please provide a new dataset name.',
                });
            } else if (this.measurementNames.includes(name)) {
                messages.push({
                    field: 'target.measurementName',
                    message: 'A dataset with this name already exists.',
                });
            }
        } else if (!targetFormValue.existingMeasurementName) {
            messages.push({
                field: 'target.measurementName',
                message: 'Please select an existing dataset.',
            });
        }

        return messages;
    }

    private toColumnModel(column: CsvImportColumn): CsvImportColumnModel {
        const initialType = this.toDefaultColumnType(
            column.runtimeType || column.inferredType || 'STRING',
        );
        const property = new EventPropertyPrimitive();
        property['@class'] =
            'org.apache.streampipes.model.schema.EventPropertyPrimitive';
        property.runtimeName = column.runtimeName;
        property.runtimeType = this.toRuntimeType(initialType);
        property.propertyScope = 'MEASUREMENT_PROPERTY';
        property.semanticType = undefined;
        property.label = column.label || '';
        property.description = column.description || '';
        property.additionalMetadata = {};

        return {
            column: {
                ...column,
                runtimeType: initialType,
                propertyScope: 'MEASUREMENT_PROPERTY',
                semanticType: undefined,
            },
            eventProperty: property,
        };
    }

    private syncColumn(model: CsvImportColumnModel): void {
        model.column.runtimeName = model.eventProperty.runtimeName;
        model.column.runtimeType = this.fromRuntimeType(
            model.eventProperty.runtimeType,
        );
        model.column.propertyScope = model.eventProperty.propertyScope;
        model.column.semanticType = model.eventProperty.semanticType;
        model.column.label = model.eventProperty.label;
        model.column.description = model.eventProperty.description;
    }

    private isTimestampColumn(model: CsvImportColumnModel): boolean {
        return SemanticType.isTimestamp(model.eventProperty);
    }

    private toRuntimeType(type: string): string {
        switch (type) {
            case 'BOOLEAN':
                return DataType.BOOLEAN;
            case 'LONG':
                return DataType.LONG;
            case 'FLOAT':
                return DataType.FLOAT;
            default:
                return DataType.STRING;
        }
    }

    private fromRuntimeType(
        type: string,
    ): 'STRING' | 'BOOLEAN' | 'LONG' | 'FLOAT' {
        if (type === DataType.BOOLEAN) {
            return 'BOOLEAN';
        } else if (type === DataType.LONG || type === DataType.INTEGER) {
            return 'LONG';
        } else if (type === DataType.FLOAT || type === DataType.DOUBLE) {
            return 'FLOAT';
        }
        return 'STRING';
    }

    private toDefaultColumnType(
        type: string,
    ): 'STRING' | 'BOOLEAN' | 'LONG' | 'FLOAT' {
        if (type === 'BOOLEAN') {
            return 'BOOLEAN';
        } else if (type === 'STRING') {
            return 'STRING';
        }
        return 'FLOAT';
    }

    private invalidatePreview(): void {
        if (this.previewReloadTimeout) {
            clearTimeout(this.previewReloadTimeout);
        }

        if (this.schemaValidationTimeout) {
            clearTimeout(this.schemaValidationTimeout);
        }

        this.previewResult.set(undefined);
        this.schemaValidationResult.set(undefined);
        this.columnModels.set([]);
        this.clearImportResult();
    }

    private clearImportResult(): void {
        this.importResult.set(undefined);
        this.uploadMessages.set([]);
    }

    private schedulePreviewReload(): void {
        if (!this.canProceedToConfiguration() || this.previewLoading()) {
            return;
        }

        if (this.previewReloadTimeout) {
            clearTimeout(this.previewReloadTimeout);
        }

        this.previewReloadTimeout = setTimeout(() => {
            this.loadPreview();
        }, 250);
    }

    private validateSchema(): void {
        const request = this.buildSchemaValidationRequest();
        if (!request) {
            this.schemaValidationResult.set(undefined);
            return;
        }

        this.datalakeRestService.validateImportSchema(request).subscribe({
            next: result => {
                this.schemaValidationResult.set(result);
            },
            error: error => {
                const result = error?.error as
                    | CsvImportSchemaValidationResult
                    | undefined;

                if (
                    result?.validationMessages?.length ||
                    result?.issues?.length
                ) {
                    this.schemaValidationResult.set(result);
                } else {
                    this.schemaValidationResult.set({
                        valid: false,
                        validationMessages: [
                            {
                                field: 'columns',
                                message:
                                    'Schema validation could not be completed.',
                            },
                        ],
                        issues: [],
                    });
                }
            },
        });
    }

    private scheduleSchemaValidation(): void {
        if (
            !this.hasPreview() ||
            this.targetMode() !== 'EXISTING' ||
            !this.selectedTimestampColumn()
        ) {
            this.schemaValidationResult.set(undefined);
            return;
        }

        if (this.schemaValidationTimeout) {
            clearTimeout(this.schemaValidationTimeout);
        }

        this.schemaValidationTimeout = setTimeout(() => {
            this.validateSchema();
        }, 250);
    }

    private toSchemaIssueText(issue: CsvImportSchemaIssue): string {
        switch (issue.type) {
            case 'TIMESTAMP_COLUMN_MISMATCH':
                return `Timestamp column must be "${issue.expected}" but is "${issue.actual}".`;
            case 'COLUMN_NAME_MISMATCH':
                return `Column "${issue.actual || issue.columnName}" is not part of the existing measurement schema.`;
            case 'COLUMN_TYPE_MISMATCH':
                return `Column "${issue.columnName}" must use type "${this.formatSchemaValue(issue.expected)}" instead of "${this.formatSchemaValue(issue.actual)}".`;
            case 'COLUMN_SCOPE_MISMATCH':
                return `Column "${issue.columnName}" must use role "${this.formatScope(issue.expected)}" instead of "${this.formatScope(issue.actual)}".`;
            default:
                return 'Imported columns must exactly match the existing measurement schema.';
        }
    }

    private formatSchemaValue(value?: string | null): string {
        if (!value) {
            return '-';
        }

        if (value.includes('#')) {
            return value.substring(value.lastIndexOf('#') + 1).toUpperCase();
        }

        return value;
    }

    private formatScope(value?: string | null): string {
        switch (value) {
            case 'HEADER_PROPERTY':
                return 'Header';
            case 'DIMENSION_PROPERTY':
                return 'Dimension';
            case 'MEASUREMENT_PROPERTY':
                return 'Measurement';
            default:
                return value || '-';
        }
    }
}
