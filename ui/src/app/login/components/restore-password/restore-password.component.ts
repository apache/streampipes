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
import { RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-restore-password',
    templateUrl: './restore-password.component.html',
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
        SpAlertBannerComponent,
        RouterLink,
        TranslatePipe,
    ],
})
export class RestorePasswordComponent extends BaseLoginPageDirective {
    parentForm: UntypedFormGroup;
    restoreSuccess = false;
    restoreCompleted = false;

    username: string;

    private fb = inject(UntypedFormBuilder);

    sendRestorePasswordLink() {
        this.restoreCompleted = false;
        this.loginService.sendRestorePasswordLink(this.username).subscribe(
            _response => {
                this.restoreSuccess = true;
                this.restoreCompleted = true;
            },
            _error => {
                this.restoreSuccess = false;
                this.restoreCompleted = true;
            },
        );
    }

    onSettingsAvailable(): void {
        this.parentForm = this.fb.group({});
        this.parentForm.addControl(
            'username',
            new UntypedFormControl('', Validators.required),
        );

        this.parentForm.valueChanges.subscribe(result => {
            this.username = result.username;
        });
    }
}
