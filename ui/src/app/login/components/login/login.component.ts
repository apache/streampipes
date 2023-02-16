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

import { ShepherdService } from '../../../services/tour/shepherd.service';
import { Component, OnInit } from '@angular/core';
import { LoginService } from '../../services/login.service';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { LoginModel } from './login.model';
import {
    UntypedFormBuilder,
    UntypedFormControl,
    UntypedFormGroup,
    Validators,
} from '@angular/forms';

@Component({
    selector: 'sp-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.scss'],
})
export class LoginComponent implements OnInit {
    parentForm: UntypedFormGroup;
    configReady = false;
    loading: boolean;
    authenticationFailed: boolean;
    credentials: any;

    loginSettings: LoginModel;
    returnUrl: string;

    constructor(
        private loginService: LoginService,
        private router: Router,
        private route: ActivatedRoute,
        private shepherdService: ShepherdService,
        private authService: AuthService,
        private fb: UntypedFormBuilder,
    ) {
        this.loading = false;
        this.authenticationFailed = false;
        this.credentials = {};
    }

    ngOnInit() {
        this.loginService.fetchLoginSettings().subscribe(result => {
            this.loginSettings = result;
            this.configReady = true;
            this.parentForm = this.fb.group({});
            this.parentForm.addControl(
                'username',
                new UntypedFormControl('', Validators.required),
            );
            this.parentForm.addControl(
                'password',
                new UntypedFormControl('', Validators.required),
            );

            this.parentForm.valueChanges.subscribe(v => {
                this.credentials.username = v.username;
                this.credentials.password = v.password;
            });
            this.returnUrl = this.route.snapshot.queryParams.returnUrl || '';
        });
    }

    logIn() {
        this.authenticationFailed = false;
        this.loading = true;
        this.loginService.login(this.credentials).subscribe(
            response => {
                // success
                this.authService.login(response);
                this.loading = false;
                this.router.navigateByUrl(this.returnUrl);
            },
            response => {
                // error
                this.loading = false;
                this.authenticationFailed = true;
            },
        );
    }
}
