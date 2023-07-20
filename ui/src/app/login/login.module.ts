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
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { StartupComponent } from './components/startup/startup.component';
import { MatDividerModule } from '@angular/material/divider';
import { MatProgressBarModule } from '@angular/material/progress-bar';
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
