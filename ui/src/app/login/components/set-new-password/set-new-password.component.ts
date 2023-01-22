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

import { Component, OnInit } from '@angular/core';
import { RestorePasswordService } from '../../services/restore-password.service';
import {
    UntypedFormBuilder,
    UntypedFormControl,
    UntypedFormGroup,
    Validators,
} from '@angular/forms';
import { checkPasswords } from '../../utils/check-password';
import { RegistrationModel } from '../register/registration.model';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
    selector: 'sp-set-new-password',
    templateUrl: './set-new-password.component.html',
    styleUrls: ['../login/login.component.scss'],
})
export class SetNewPasswordComponent implements OnInit {
    parentForm: UntypedFormGroup;
    registrationModel: RegistrationModel;
    recoveryCode: string;

    resetPerformed = false;
    resetInProgress = false;
    resetSuccess = false;

    constructor(
        private fb: UntypedFormBuilder,
        private restorePasswordService: RestorePasswordService,
        private route: ActivatedRoute,
        private router: Router,
    ) {}

    ngOnInit(): void {
        this.route.queryParams.subscribe(params => {
            this.recoveryCode = params['recoveryCode'];
            if (this.recoveryCode) {
                this.restorePasswordService
                    .checkRecoveryCode(this.recoveryCode)
                    .subscribe(
                        success => {},
                        error => {
                            this.navigateToLoginPage();
                        },
                    );
            } else {
                this.navigateToLoginPage();
            }
        });
        this.parentForm = this.fb.group({});
        this.parentForm.addControl(
            'password',
            new UntypedFormControl('', Validators.required),
        );
        this.parentForm.addControl(
            'repeatPassword',
            new UntypedFormControl('', Validators.required),
        );
        this.parentForm.setValidators(checkPasswords);

        this.parentForm.valueChanges.subscribe(v => {
            this.registrationModel = { username: '', password: v.password };
        });
    }

    navigateToLoginPage() {
        this.router.navigate(['/login']);
    }

    setNewPassword() {
        this.updateStatus(true, false, false);
        this.restorePasswordService
            .restorePassword(this.recoveryCode, this.registrationModel)
            .subscribe(
                result => {
                    this.updateStatus(false, true, true);
                },
                error => {
                    this.updateStatus(false, false, true);
                },
            );
    }

    updateStatus(
        resetInProgress: boolean,
        resetSuccess: boolean,
        resetPerformed: boolean,
    ) {
        this.resetInProgress = resetInProgress;
        this.resetSuccess = resetSuccess;
        this.resetPerformed = resetPerformed;
    }
}
