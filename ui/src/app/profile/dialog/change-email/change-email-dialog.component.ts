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
import { UserAccount, UserService } from '@streampipes/platform-services';

@Component({
    selector: 'sp-change-email-dialog',
    templateUrl: './change-email-dialog.component.html',
    styleUrls: ['./change-email-dialog.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class ChangeEmailDialogComponent implements OnInit {
    parentForm: UntypedFormGroup;

    @Input()
    user: UserAccount;

    clonedUser: UserAccount;

    email = '';
    confirmMail = '';
    confirmPw = '';

    operationApplied = false;
    error = false;
    errorMessage = '';

    constructor(
        private dialogRef: DialogRef<ChangeEmailDialogComponent>,
        private fb: UntypedFormBuilder,
        private userService: UserService,
    ) {}

    ngOnInit(): void {
        this.clonedUser = UserAccount.fromData(this.user);
        this.email = this.clonedUser.username;
        this.parentForm = this.fb.group({});
        this.parentForm.addControl(
            'email',
            new UntypedFormControl(this.email, [
                Validators.required,
                Validators.email,
            ]),
        );
        this.parentForm.addControl(
            'emailConfirm',
            new UntypedFormControl(this.confirmMail, [
                Validators.required,
                Validators.email,
            ]),
        );
        this.parentForm.addControl(
            'passwordConfirm',
            new UntypedFormControl(this.confirmPw, [Validators.required]),
        );
        this.parentForm.setValidators(this.checkEmail);

        this.parentForm.valueChanges.subscribe(v => {
            this.email = v.email;
            this.confirmMail = v.emailConfirm;
            this.confirmPw = v.passwordConfirm;
        });
    }

    close(refresh?: boolean) {
        this.dialogRef.close(refresh);
    }

    update() {
        this.clonedUser.username = this.email;
        this.clonedUser.password = this.confirmPw;
        this.userService.updateUsername(this.clonedUser).subscribe(
            result => {
                this.close(true);
            },
            error => {
                this.operationApplied = true;
                this.error = true;
                this.errorMessage = error.error.notifications[0].title;
            },
        );
    }

    checkEmail: ValidatorFn = (
        group: AbstractControl,
    ): ValidationErrors | null => {
        const email = group.get('email');
        const emailConfirm = group.get('emailConfirm');

        if (!email || !emailConfirm) {
            return null;
        }
        return email.value === emailConfirm.value
            ? null
            : { notMatching: true };
    };
}
