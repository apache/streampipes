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

import { Component } from '@angular/core';
import { LoginService } from '../../services/login.service';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import {
    UntypedFormBuilder,
    UntypedFormControl,
    UntypedFormGroup,
    Validators,
} from '@angular/forms';
import { BaseLoginPageDirective } from '../base-login-page.directive';

@Component({
    selector: 'sp-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.scss'],
})
export class LoginComponent extends BaseLoginPageDirective {
    parentForm: UntypedFormGroup;
    loading: boolean;
    authenticationFailed: boolean;
    credentials: any;

    returnUrl: string;

    constructor(
        loginService: LoginService,
        private router: Router,
        private route: ActivatedRoute,
        private authService: AuthService,
        private fb: UntypedFormBuilder,
    ) {
        super(loginService);
        this.loading = false;
        this.authenticationFailed = false;
        this.credentials = {};
    }

    doLogin() {
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

    onSettingsAvailable(): void {
        const token = this.route.snapshot.queryParamMap.get('token');
        if (token) {
            this.authService.oauthLogin(token);
            this.loading = false;
            this.router.navigate(['']);
        }
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
    }

    doOAuthLogin(provider: string): void {
        window.location.href = `/streampipes-backend/oauth2/authorization/${provider}?redirect_uri=${this.loginSettings.oAuthSettings.redirectUri}/%23/login`;
    }
}
