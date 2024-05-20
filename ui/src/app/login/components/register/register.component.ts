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
import {
    UntypedFormBuilder,
    UntypedFormControl,
    UntypedFormGroup,
    Validators,
} from '@angular/forms';
import { RegistrationModel } from './registration.model';
import { LoginService } from '../../services/login.service';
import { checkPasswords } from '../../utils/check-password';

@Component({
    selector: 'sp-register-user',
    templateUrl: './register.component.html',
    styleUrls: ['../login/login.component.scss'],
})
export class RegisterComponent implements OnInit {
    parentForm: UntypedFormGroup;

    registrationData: RegistrationModel;

    registrationInProcess = false;
    registrationSuccess = false;
    registrationError: string;

    constructor(
        private fb: UntypedFormBuilder,
        private loginService: LoginService,
    ) {}

    ngOnInit(): void {
        this.parentForm = this.fb.group({});
        this.parentForm.addControl(
            'username',
            new UntypedFormControl('', [Validators.required, Validators.email]),
        );
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
            this.registrationData = {
                username: v.username,
                password: v.password,
            };
        });
    }

    registerUser() {
        this.registrationError = undefined;
        this.registrationInProcess = true;
        this.loginService.registerUser(this.registrationData).subscribe(
            response => {
                this.registrationInProcess = false;
                this.registrationSuccess = true;
            },
            error => {
                this.registrationInProcess = false;
                this.registrationSuccess = false;
                this.registrationError = error.error.notifications[0].title;
            },
        );
    }
}
