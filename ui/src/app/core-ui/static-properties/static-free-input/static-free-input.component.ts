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

import { Component, OnInit, ViewChild } from '@angular/core';
import { ValidatorFn, Validators } from '@angular/forms';
import { StaticPropertyUtilService } from '../static-property-util.service';
import { ConfigurationInfo } from '../../../connect/model/ConfigurationInfo';
import { FreeTextStaticProperty } from '@streampipes/platform-services';
import { XsService } from '../../../NS/xs.service';
import {
    ValidateNumber,
    ValidateString,
    ValidateUrl,
} from '../input.validator';
import { AbstractValidatedStaticPropertyRenderer } from '../base/abstract-validated-static-property';
import { QuillEditorComponent } from 'ngx-quill';

@Component({
    selector: 'sp-app-static-free-input',
    templateUrl: './static-free-input.component.html',
    styleUrls: ['./static-free-input.component.scss'],
})
export class StaticFreeInputComponent
    extends AbstractValidatedStaticPropertyRenderer<FreeTextStaticProperty>
    implements OnInit
{
    quillModules: any = {
        toolbar: [
            ['bold', 'italic', 'underline', 'strike'],
            [{ header: 1 }, { header: 2 }],
            [{ size: ['small', false, 'large', 'huge'] }],
            [{ header: [1, 2, 3, 4, 5, 6, false] }],
            [{ color: [] }, { background: [] }],
        ],
    };

    quillModulesFontFormat: any = {
        toolbar: [['bold', 'italic', 'underline', 'strike']],
    };

    @ViewChild('textEditor', { static: false })
    quillEditorComponent: QuillEditorComponent;

    constructor(
        public staticPropertyUtil: StaticPropertyUtilService,
        private xsService: XsService,
    ) {
        super();
    }

    ngOnInit() {
        this.addValidator(this.staticProperty.value, this.collectValidators());
        this.enableValidators();
        this.emitUpdate();
    }

    collectValidators() {
        const validators: ValidatorFn[] = [];
        validators.push(Validators.required);
        if (
            this.xsService.isNumber(this.staticProperty.requiredDatatype) ||
            this.xsService.isNumber(this.staticProperty.requiredDomainProperty)
        ) {
            validators.push(ValidateNumber);
            this.errorMessage = 'The value should be a number';
        } else if (
            this.staticProperty.requiredDomainProperty === this.xsService.SO_URL
        ) {
            validators.push(ValidateUrl);
            this.errorMessage = 'Please enter a valid URL';
        } else if (
            this.staticProperty.requiredDatatype === this.xsService.XS_STRING1
        ) {
            validators.push(ValidateString);
            this.errorMessage = 'Please enter a valid String';
        }

        return validators;
    }

    emitUpdate() {
        const valid =
            this.staticProperty.value !== undefined &&
            this.staticProperty.value !== '' &&
            this.staticProperty.value !== null;
        this.updateEmitter.emit(
            new ConfigurationInfo(this.staticProperty.internalName, valid),
        );
    }

    onStatusChange(status: any) {}

    onValueChange(value: any) {
        this.staticProperty.value = value;
        this.parentForm.updateValueAndValidity();
    }

    applyPlaceholder(runtimeName) {
        const valueToInsert = '#' + runtimeName + '#';
        if (this.quillEditorComponent) {
            const currentIndex =
                this.quillEditorComponent.quillEditor.selection.savedRange
                    .index;
            this.quillEditorComponent.quillEditor.insertText(
                currentIndex,
                valueToInsert,
                'user',
            );
        } else {
            this.parentForm.controls[this.fieldName].setValue(
                this.parentForm.controls[this.fieldName].value +
                    ' ' +
                    valueToInsert,
            );
        }
    }

    formatLabel(value: number) {
        if (!Number.isInteger(value)) {
            value = Number(value.toFixed(1));
        }
        return value;
    }
}
