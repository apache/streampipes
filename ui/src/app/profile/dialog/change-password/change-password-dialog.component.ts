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

import { Component, Input, OnInit, ViewEncapsulation } from '@angular/core';
import { DialogRef } from '@streampipes/shared-ui';
import {
    AbstractControl,
    UntypedFormBuilder,
    UntypedFormControl,
    UntypedFormGroup,
    ValidationErrors,
    ValidatorFn,
    Validators,
} from '@angular/forms';
import {
    UserAccount,
    ChangePasswordRequest,
    UserService,
} from '@streampipes/platform-services';

@Component({
    selector: 'sp-change-password-dialog',
    templateUrl: './change-password-dialog.component.html',
    styleUrls: ['./change-password-dialog.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class ChangePasswordDialogComponent implements OnInit {
    @Input()
    user: UserAccount;

    parentForm: UntypedFormGroup;

    existingPw = '';
    newPw = '';
    newPwConfirm = '';

    operationApplied = false;
    error = false;
    errorMessage = '';

    constructor(
        private dialogRef: DialogRef<ChangePasswordDialogComponent>,
        private fb: UntypedFormBuilder,
        private userService: UserService,
    ) {}

    ngOnInit(): void {
        this.parentForm = this.fb.group({});
        this.parentForm.addControl(
            'currentPassword',
            new UntypedFormControl(this.existingPw, [Validators.required]),
        );
        this.parentForm.addControl(
            'newPassword',
            new UntypedFormControl(this.newPw, [Validators.required]),
        );
        this.parentForm.addControl(
            'newPasswordConfirm',
            new UntypedFormControl(this.newPw, [Validators.required]),
        );
        this.parentForm.setValidators(this.checkPasswords);

        this.parentForm.valueChanges.subscribe(v => {
            this.existingPw = v.currentPassword;
            this.newPw = v.newPassword;
            this.newPwConfirm = v.newPasswordConfirm;
        });
    }

    close(refresh?: boolean) {
        this.dialogRef.close(refresh);
    }

    update() {
        const req: ChangePasswordRequest = {
            newPassword: this.newPw,
            existingPassword: this.existingPw,
        };
        this.userService.updatePassword(this.user, req).subscribe(
            response => {
                this.close(true);
            },
            error => {
                this.operationApplied = true;
                this.error = true;
                this.errorMessage = error.error.notifications[0].title;
            },
        );
    }

    checkPasswords: ValidatorFn = (
        group: AbstractControl,
    ): ValidationErrors | null => {
        const pass = group.get('newPassword');
        const confirmPass = group.get('newPasswordConfirm');

        if (!pass || !confirmPass) {
            return null;
        }
        return pass.value === confirmPass.value ? null : { notMatching: true };
    };
}
