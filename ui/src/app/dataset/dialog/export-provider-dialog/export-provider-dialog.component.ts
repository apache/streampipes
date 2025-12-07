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
import { Component, inject, Input, OnInit } from '@angular/core';
import {
    AbstractControl,
    FormBuilder,
    FormGroup,
    ValidationErrors,
    Validators,
} from '@angular/forms';
import { DialogRef } from '@streampipes/shared-ui';
import {
    ExportProviderService,
    ExportProviderSettings,
} from '@streampipes/platform-services';

@Component({
    selector: 'sp-export-provider-dialog',
    templateUrl: './export-provider-dialog.component.html',
    standalone: false,
})
export class ExportProviderComponent implements OnInit {
    @Input()
    provider: ExportProviderSettings;

    private dialogRef = inject<DialogRef<ExportProviderComponent>>(DialogRef);
    private exportProviderRestService = inject(ExportProviderService);
    private fb = inject(FormBuilder);

    exportForm: FormGroup;

    ngOnInit() {
        this.initForm();

        if (this.provider) {
            this.exportForm.patchValue(this.provider);
        }

        this.exportForm.get('providerType')?.valueChanges.subscribe(type => {
            this.toggleS3Fields(type === 'S3');
        });
        this.toggleS3Fields(
            this.exportForm.get('providerType')?.value === 'S3',
        );
    }

    initForm() {
        this.exportForm = this.fb.group({
            providerType: ['FOLDER', Validators.required],
            accessKey: ['', Validators.required],
            secretKey: ['', Validators.required],
            endPoint: ['', [Validators.required, this.uriValidator]],
            bucketName: ['', Validators.required],
            awsRegion: ['us-east-1', Validators.required],
            providerId: [''],
            secretEncrypted: [false],
        });
    }

    toggleS3Fields(enabled: boolean) {
        const fields = [
            'accessKey',
            'secretKey',
            'endPoint',
            'bucketName',
            'awsRegion',
        ];
        fields.forEach(field => {
            const control = this.exportForm.get(field);
            if (enabled) {
                control?.setValidators(Validators.required);
                if (field === 'endPoint') {
                    control?.addValidators(this.uriValidator.bind(this));
                }
                control?.enable();
            } else {
                control?.clearValidators();
                control?.disable();
            }
            control?.updateValueAndValidity();
        });
    }
    uriValidator(control: AbstractControl): ValidationErrors | null {
        const value = control.value;
        if (!value) return null;

        try {
            new URL(value);
            return null;
        } catch (e) {
            return { invalidUri: true };
        }
    }

    addData() {
        if (this.exportForm.invalid) {
            this.exportForm.markAllAsTouched();
            return;
        }

        const formValue: ExportProviderSettings = this.exportForm.value;

        if (!formValue.providerId) {
            formValue.providerId = this.makeProviderId();
        }
        formValue.awsRegion = formValue.awsRegion;

        this.exportProviderRestService
            .updateExportProvider(formValue)
            .subscribe(() => this.dialogRef.close(true));
    }

    close(refresh: boolean) {
        this.dialogRef.close(refresh);
    }

    private makeProviderId(): string {
        return 'p' + Math.random().toString(36).substring(2, 9);
    }
}
