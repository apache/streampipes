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
import { RestorePasswordService } from '../../services/restore-password.service';
import {
    FormsModule,
    ReactiveFormsModule,
    UntypedFormBuilder,
    UntypedFormControl,
    UntypedFormGroup,
    Validators,
} from '@angular/forms';
import { checkPasswords } from '../../utils/check-password';
import { RegistrationModel } from '../register/registration.model';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
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
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-set-new-password',
    templateUrl: './set-new-password.component.html',
    styleUrls: ['../login/login.component.scss'],
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
        MatProgressSpinner,
        SpAlertBannerComponent,
        RouterLink,
        TranslatePipe,
    ],
})
export class SetNewPasswordComponent extends BaseLoginPageDirective {
    parentForm: UntypedFormGroup;
    registrationModel: RegistrationModel;
    recoveryCode: string;

    resetPerformed = false;
    resetInProgress = false;
    resetSuccess = false;

    private fb = inject(UntypedFormBuilder);
    private restorePasswordService = inject(RestorePasswordService);
    private route = inject(ActivatedRoute);
    private router = inject(Router);

    onSettingsAvailable(): void {
        this.route.queryParams.subscribe(params => {
            this.recoveryCode = params['recoveryCode'];
            if (this.recoveryCode) {
                this.restorePasswordService
                    .checkRecoveryCode(this.recoveryCode)
                    .subscribe(
                        _success => {},
                        _error => {
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
                _result => {
                    this.updateStatus(false, true, true);
                },
                _error => {
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
