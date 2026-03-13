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

import { Component, inject, Input, ViewChild } from '@angular/core';
import {
    FormBuilder,
    FormsModule,
    ReactiveFormsModule,
    Validators,
} from '@angular/forms';
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
    DataType,
    DatalakeRestService,
    EventPropertyPrimitive,
    SemanticType,
} from '@streampipes/platform-services';
import {
    DialogRef,
    FormFieldComponent,
    SpAlertBannerComponent,
    SplitSectionComponent,
} from '@streampipes/shared-ui';
import { CommonModule } from '@angular/common';
import { MatButton } from '@angular/material/button';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatOption, MatSelect } from '@angular/material/select';
import { MatDivider } from '@angular/material/divider';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatIcon } from '@angular/material/icon';
import { MatStep, MatStepLabel, MatStepper } from '@angular/material/stepper';
import { TranslatePipe } from '@ngx-translate/core';

interface CsvImportColumnModel {
    column: CsvImportColumn;
    eventProperty: EventPropertyPrimitive;
}

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
        SpAlertBannerComponent,
        SplitSectionComponent,
        MatFormField,
        MatInput,
        MatSelect,
        MatOption,
        MatDivider,
        MatProgressSpinner,
        MatIcon,
        MatStepper,
        MatStep,
        MatStepLabel,
        TranslatePipe,
    ],
})
export class CsvImportDialogComponent {
    @Input()
    measurementNames: string[] = [];

    @ViewChild('csvImportStepper', { static: true })
    csvImportStepper: MatStepper;

    private fb = inject(FormBuilder);
    private dialogRef = inject(DialogRef<CsvImportDialogComponent>);
    private datalakeRestService = inject(DatalakeRestService);
    private previewReloadTimeout?: ReturnType<typeof setTimeout>;
    private schemaValidationTimeout?: ReturnType<typeof setTimeout>;

    selectedFile?: File;
    uploadId?: string;
    fileName = '';
    timestampFormat = '';
    previewResult?: CsvImportPreviewResult;
    schemaValidationResult?: CsvImportSchemaValidationResult;
    importResult?: CsvImportResult;
    columnModels: CsvImportColumnModel[] = [];
    previewLoading = false;
    importLoading = false;
    localMessages: CsvImportValidationMessage[] = [];

    parseForm = this.fb.group({
        delimiter: [',' as ',' | ';' | '|' | '\\t', Validators.required],
        decimalSeparator: ['.' as ',' | '.', Validators.required],
        hasHeader: [true, Validators.required],
    });

    targetForm = this.fb.group({
        mode: ['NEW', Validators.required],
        newMeasurementName: [''],
        existingMeasurementName: [''],
    });

    constructor() {
        this.parseForm.valueChanges.subscribe(() => {
            this.invalidatePreview();
            if (this.csvImportStepper?.selectedIndex === 1) {
                this.schedulePreviewReload();
            }
        });
        this.targetForm.controls.mode.valueChanges.subscribe(mode => {
            if (mode === 'NEW') {
                this.targetForm.controls.existingMeasurementName.setValue('');
            } else {
                this.targetForm.controls.newMeasurementName.setValue('');
            }
            this.invalidatePreview();
        });
        this.targetForm.controls.newMeasurementName.valueChanges.subscribe(
            () => {
                this.invalidatePreview();
            },
        );
        this.targetForm.controls.existingMeasurementName.valueChanges.subscribe(
            () => {
                this.invalidatePreview();
            },
        );
    }

    get topMessages(): CsvImportValidationMessage[] {
        return [
            ...(this.previewResult?.validationMessages?.filter(
                message =>
                    message.field !== 'columns' &&
                    message.field !== 'timestampColumn' &&
                    message.field !== 'schemaDetails',
            ) ?? []),
            ...this.localMessages,
            ...(this.importResult?.validationMessages?.filter(
                message =>
                    message.field !== 'columns' &&
                    message.field !== 'timestampColumn' &&
                    message.field !== 'schemaDetails',
            ) ?? []),
        ];
    }

    get hasPreview(): boolean {
        return !!this.previewResult;
    }

    get hasImportResult(): boolean {
        return !!this.importResult?.measurementName;
    }

    get uploadErrorMessages(): CsvImportValidationMessage[] {
        if (this.importLoading || this.hasImportResult) {
            return [];
        }

        return this.importResult?.validationMessages ?? [];
    }

    get hasUploadError(): boolean {
        return this.uploadErrorMessages.length > 0;
    }

    get previewRows(): string[][] {
        return this.previewResult?.previewRows ?? [];
    }

    get targetMode(): 'NEW' | 'EXISTING' {
        return this.targetForm.get('mode')?.value as 'NEW' | 'EXISTING';
    }

    get selectedTimestampColumn(): string | undefined {
        return this.columnModels.find(model =>
            SemanticType.isTimestamp(model.eventProperty),
        )?.column.runtimeName;
    }

    get canProceedToConfiguration(): boolean {
        return this.isTargetValid && !!this.selectedFile;
    }

    get canImport(): boolean {
        return (
            this.previewResult?.valid === true &&
            !!this.currentTarget &&
            !!this.selectedTimestampColumn &&
            (this.targetMode !== 'EXISTING' ||
                this.schemaValidationResult?.valid === true)
        );
    }

    get showTimestampSelectionWarning(): boolean {
        return this.hasPreview && !this.selectedTimestampColumn;
    }

    get selectedTimestampColumnModel(): CsvImportColumnModel | undefined {
        return this.columnModels.find(model => this.isTimestampColumn(model));
    }

    isTimestampSelectionDisabled(model: CsvImportColumnModel): boolean {
        return (
            !!this.selectedTimestampColumnModel &&
            !this.isTimestampColumn(model)
        );
    }

    getColumnRole(
        model: CsvImportColumnModel,
    ): 'TIMESTAMP' | 'DIMENSION_PROPERTY' | 'MEASUREMENT_PROPERTY' {
        if (this.isTimestampColumn(model)) {
            return 'TIMESTAMP';
        } else if (model.column.propertyScope === 'DIMENSION_PROPERTY') {
            return 'DIMENSION_PROPERTY';
        }
        return 'MEASUREMENT_PROPERTY';
    }

    get isTargetValid(): boolean {
        return this.validateLocalTarget().length === 0;
    }

    get hasSchemaMismatch(): boolean {
        return (
            this.targetMode === 'EXISTING' &&
            !!this.schemaValidationResult?.issues?.length
        );
    }

    get schemaMismatchSummary(): string {
        return 'Imported columns must exactly match the existing measurement schema.';
    }

    get schemaMismatchDetails(): string[] {
        return (this.schemaValidationResult?.issues ?? []).map(issue =>
            this.toSchemaIssueText(issue),
        );
    }

    onFileSelected(event: Event): void {
        const input = event.target as HTMLInputElement;
        const file = input.files?.[0];
        if (!file) {
            return;
        }

        this.fileName = file.name;
        this.selectedFile = file;
        this.uploadId = undefined;
        this.timestampFormat = '';
        this.invalidatePreview();
    }

    nextStep(): void {
        if (this.csvImportStepper.selectedIndex === 0) {
            if (!this.canProceedToConfiguration) {
                this.localMessages = this.validateLocalTarget();
                if (!this.selectedFile) {
                    this.localMessages.push({
                        field: 'file',
                        message: 'Please select a CSV file first.',
                    });
                }
                return;
            }
            this.csvImportStepper.next();
            this.loadPreview();
        }
    }

    previousStep(): void {
        this.csvImportStepper.previous();
    }

    startUpload(): void {
        this.localMessages = this.validateLocalTarget();
        if (!this.selectedTimestampColumn) {
            this.localMessages.push({
                field: 'timestampColumn',
                message: 'Please select exactly one timestamp column.',
            });
        }
        if (this.localMessages.length > 0 || !this.canImport) {
            return;
        }

        this.csvImportStepper.next();
        this.importData();
    }

    loadPreview(): void {
        this.localMessages = [];
        this.clearImportResult();

        if (!this.isTargetValid || !this.currentTarget) {
            this.localMessages = [
                {
                    field: 'target.measurementName',
                    message:
                        'Please complete the target dataset selection first.',
                },
            ];
            return;
        }

        if (!this.selectedFile && !this.uploadId) {
            this.localMessages = [
                { field: 'file', message: 'Please select a CSV file first.' },
            ];
            return;
        }

        this.previewLoading = true;
        const useMultipartUpload = !!this.selectedFile && !this.uploadId;
        this.datalakeRestService
            .previewImport(
                this.buildPreviewRequest(this.currentTarget),
                useMultipartUpload ? this.selectedFile : undefined,
            )
            .subscribe({
                next: preview => {
                    this.previewResult = preview;
                    this.uploadId = preview.uploadId ?? this.uploadId;
                    this.columnModels = preview.columns.map(column =>
                        this.toColumnModel(column),
                    );
                    this.schemaValidationResult = undefined;
                    this.previewLoading = false;
                    this.validateSchema();
                },
                error: error => {
                    this.previewLoading = false;
                    this.previewResult = undefined;
                    this.schemaValidationResult = undefined;
                    this.localMessages = [
                        {
                            field: 'preview',
                            message:
                                error?.error?.message ??
                                'Preview could not be generated.',
                        },
                    ];
                },
            });
    }

    setColumnType(
        model: CsvImportColumnModel,
        type: 'STRING' | 'BOOLEAN' | 'LONG' | 'FLOAT',
    ): void {
        model.eventProperty.runtimeType = this.toRuntimeType(type);
        model.column.runtimeType = type;
        if (this.isTimestampColumn(model)) {
            model.eventProperty.runtimeType = DataType.LONG;
            model.column.runtimeType = 'LONG';
        }
        this.syncColumn(model);
        this.scheduleSchemaValidation();
    }

    setColumnRole(
        model: CsvImportColumnModel,
        role: 'TIMESTAMP' | 'DIMENSION_PROPERTY' | 'MEASUREMENT_PROPERTY',
    ): void {
        if (role === 'TIMESTAMP') {
            this.columnModels.forEach(other => {
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
        this.scheduleSchemaValidation();
    }

    isTimestampColumn(model: CsvImportColumnModel): boolean {
        return SemanticType.isTimestamp(model.eventProperty);
    }

    importData(): void {
        this.localMessages = this.validateLocalTarget();
        if (!this.selectedTimestampColumn) {
            this.localMessages.push({
                field: 'timestampColumn',
                message: 'Please select exactly one timestamp column.',
            });
        }
        if (this.localMessages.length > 0) {
            return;
        }

        this.importLoading = true;
        this.datalakeRestService
            .importCsvData(this.buildImportRequest())
            .subscribe({
                next: result => {
                    this.importResult = result;
                    this.importLoading = false;
                },
                error: error => {
                    this.importLoading = false;
                    this.importResult = (error?.error as CsvImportResult) ?? {
                        measurementId: '',
                        measurementName: '',
                        createdNewMeasurement: false,
                        importedRowCount: 0,
                        validationMessages: [],
                    };

                    if (!this.importResult.validationMessages?.length) {
                        this.importResult.validationMessages = [
                            {
                                field: 'upload',
                                message:
                                    error?.error?.message ??
                                    'The CSV import failed. Please review the import configuration and try again.',
                            },
                        ];
                    }
                },
            });
    }

    close(refresh = false): void {
        this.dialogRef.close(refresh);
    }

    private buildPreviewRequest(
        target?: CsvImportTarget,
    ): CsvImportPreviewRequest {
        return {
            uploadId: this.uploadId,
            fileName: this.fileName,
            csvConfig: this.currentCsvConfig,
            target,
        };
    }

    private buildImportRequest(): CsvImportRequest {
        return {
            uploadId: this.uploadId,
            csvConfig: this.currentCsvConfig,
            target: this.currentTarget!,
            timestampColumn: this.selectedTimestampColumn!,
            columns: this.columnModels.map(model => model.column),
        };
    }

    private buildSchemaValidationRequest():
        | CsvImportSchemaValidationRequest
        | undefined {
        if (!this.currentTarget || !this.selectedTimestampColumn) {
            return undefined;
        }

        return {
            target: this.currentTarget,
            timestampColumn: this.selectedTimestampColumn,
            columns: this.columnModels.map(model => model.column),
        };
    }

    private validateLocalTarget(): CsvImportValidationMessage[] {
        const messages: CsvImportValidationMessage[] = [];
        if (this.targetMode === 'NEW') {
            const name = this.targetForm
                .get('newMeasurementName')
                ?.value?.trim();
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
        } else if (!this.targetForm.get('existingMeasurementName')?.value) {
            messages.push({
                field: 'target.measurementName',
                message: 'Please select an existing dataset.',
            });
        }
        return messages;
    }

    private get currentCsvConfig(): CsvImportConfiguration {
        return {
            delimiter: this.parseForm.get('delimiter')?.value ?? ',',
            decimalSeparator:
                (this.parseForm.get('decimalSeparator')?.value as ',' | '.') ??
                '.',
            hasHeader: this.parseForm.get('hasHeader')?.value ?? true,
            timestampFormat: this.timestampFormat.trim() || undefined,
        };
    }

    get currentTarget(): CsvImportTarget | undefined {
        if (this.targetMode === 'NEW') {
            const measurementName = this.targetForm
                .get('newMeasurementName')
                ?.value?.trim();
            return measurementName
                ? { mode: 'NEW', measurementName }
                : undefined;
        }

        const measurementName = this.targetForm.get('existingMeasurementName')
            ?.value as string;
        return measurementName
            ? { mode: 'EXISTING', measurementName }
            : undefined;
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
        this.previewResult = undefined;
        this.schemaValidationResult = undefined;
        this.columnModels = [];
        this.clearImportResult();
    }

    private clearImportResult(): void {
        this.importResult = undefined;
    }

    private schedulePreviewReload(): void {
        if (!this.canProceedToConfiguration || this.previewLoading) {
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
            this.schemaValidationResult = undefined;
            return;
        }

        this.datalakeRestService.validateImportSchema(request).subscribe({
            next: result => {
                this.schemaValidationResult = result;
            },
            error: error => {
                this.schemaValidationResult =
                    error?.error as CsvImportSchemaValidationResult;
                if (
                    !this.schemaValidationResult?.validationMessages?.length &&
                    !this.schemaValidationResult?.issues?.length
                ) {
                    this.schemaValidationResult = {
                        valid: false,
                        validationMessages: [
                            {
                                field: 'columns',
                                message:
                                    'Schema validation could not be completed.',
                            },
                        ],
                        issues: [],
                    };
                }
            },
        });
    }

    private scheduleSchemaValidation(): void {
        if (
            !this.hasPreview ||
            this.targetMode !== 'EXISTING' ||
            !this.selectedTimestampColumn
        ) {
            this.schemaValidationResult = undefined;
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
