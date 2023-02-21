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

import { NgModule } from '@angular/core';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatIconModule } from '@angular/material/icon';
import { FlexLayoutModule } from '@ngbracket/ngx-layout';
import { CommonModule } from '@angular/common';
import { LoginComponent } from './components/login/login.component';
import { SetupComponent } from './components/setup/setup.component';
import { MatLegacyCardModule as MatCardModule } from '@angular/material/legacy-card';
import { MatLegacyProgressSpinnerModule as MatProgressSpinnerModule } from '@angular/material/legacy-progress-spinner';
import { MatLegacyButtonModule as MatButtonModule } from '@angular/material/legacy-button';
import { MatLegacyCheckboxModule as MatCheckboxModule } from '@angular/material/legacy-checkbox';
import { MatLegacyFormFieldModule as MatFormFieldModule } from '@angular/material/legacy-form-field';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatLegacyInputModule as MatInputModule } from '@angular/material/legacy-input';
import { StartupComponent } from './components/startup/startup.component';
import { MatDividerModule } from '@angular/material/divider';
import { MatLegacyProgressBarModule as MatProgressBarModule } from '@angular/material/legacy-progress-bar';
import { LoginService } from './services/login.service';
import { AppRoutingModule } from '../app-routing.module';
import { AuthBoxComponent } from './components/auth-box/auth-box.component';
import { RestorePasswordComponent } from './components/restore-password/restore-password.component';
import { RegisterComponent } from './components/register/register.component';
import { RestorePasswordService } from './services/restore-password.service';
import { SetNewPasswordComponent } from './components/set-new-password/set-new-password.component';
import { ActivateAccountComponent } from './components/activate-account/activate-account.component';
import { AccountActivationService } from './services/account-activation.service';
import { PlatformServicesModule } from '@streampipes/platform-services';

@NgModule({
    imports: [
        AppRoutingModule,
        CommonModule,
        FlexLayoutModule,
        FormsModule,
        MatButtonModule,
        MatCardModule,
        MatDividerModule,
        MatGridListModule,
        MatIconModule,
        MatInputModule,
        MatProgressSpinnerModule,
        MatCheckboxModule,
        MatFormFieldModule,
        ReactiveFormsModule,
        MatProgressBarModule,
        PlatformServicesModule,
    ],
    declarations: [
        ActivateAccountComponent,
        AuthBoxComponent,
        LoginComponent,
        RegisterComponent,
        RestorePasswordComponent,
        SetNewPasswordComponent,
        SetupComponent,
        StartupComponent,
    ],
    providers: [AccountActivationService, LoginService, RestorePasswordService],
})
export class LoginModule {}
