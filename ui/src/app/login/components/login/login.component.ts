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

import { Component, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import {
    FormsModule,
    ReactiveFormsModule,
    UntypedFormBuilder,
    UntypedFormControl,
    UntypedFormGroup,
    Validators,
} from '@angular/forms';
import { BaseLoginPageDirective } from '../base-login-page.directive';
import { AuthBoxComponent } from '../auth-box/auth-box.component';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import {
    FormFieldComponent,
    SpAlertBannerComponent,
} from '@streampipes/shared-ui';
import { MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatButton } from '@angular/material/button';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.scss'],
    imports: [
        AuthBoxComponent,
        FlexDirective,
        LayoutDirective,
        LayoutAlignDirective,
        FormsModule,
        ReactiveFormsModule,
        FormFieldComponent,
        MatFormField,
        MatInput,
        MatButton,
        MatCheckbox,
        MatProgressSpinner,
        SpAlertBannerComponent,
        RouterLink,
        TranslatePipe,
    ],
})
export class LoginComponent extends BaseLoginPageDirective {
    parentForm: UntypedFormGroup;
    loading = false;
    authenticationFailed = false;
    credentials: any = {};

    returnUrl: string;

    private router = inject(Router);
    private route = inject(ActivatedRoute);
    private authService = inject(AuthService);
    private fb = inject(UntypedFormBuilder);

    doLogin() {
        this.authenticationFailed = false;
        this.loading = true;
        this.loginService.login(this.credentials).subscribe(
            response => {
                // success
                this.authService.login(response);
                this.loading = false;
                this.router.navigate(['terms'], {
                    queryParams: { returnUrl: this.returnUrl },
                });
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
            this.router.navigate(['terms'], {
                queryParams: { returnUrl: this.returnUrl },
            });
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
        this.parentForm.addControl('rememberMe', new UntypedFormControl(false));

        this.parentForm.valueChanges.subscribe(v => {
            this.credentials.username = v.username;
            this.credentials.password = v.password;
            this.credentials.rememberMe = v.rememberMe;
        });
        this.credentials.rememberMe = false;
        this.returnUrl = this.route.snapshot.queryParams.returnUrl || '';
    }

    doOAuthLogin(provider: string): void {
        const rememberMe = !!this.parentForm?.get('rememberMe')?.value;
        window.location.href = `/streampipes-backend/oauth2/authorization/${provider}?redirect_uri=${this.loginSettings.oAuthSettings.redirectUri}/%23/login&remember_me=${rememberMe}`;
    }
}
