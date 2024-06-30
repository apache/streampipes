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
import { AccountActivationService } from '../../services/account-activation.service';
import { ActivatedRoute, Router } from '@angular/router';
import { BaseLoginPageDirective } from '../base-login-page.directive';
import { LoginService } from '../../services/login.service';

@Component({
    selector: 'sp-activate-account',
    templateUrl: './activate-account.component.html',
    styleUrls: ['../login/login.component.scss'],
})
export class ActivateAccountComponent extends BaseLoginPageDirective {
    activationCode: string;
    activationSuccess: boolean;
    activationPerformed = false;

    constructor(
        private accountActivationService: AccountActivationService,
        private route: ActivatedRoute,
        private router: Router,
        protected loginService: LoginService,
    ) {
        super(loginService);
    }

    navigateToLoginPage() {
        this.router.navigate(['/login']);
    }

    onSettingsAvailable(): void {
        this.route.queryParams.subscribe(params => {
            this.activationCode = params['activationCode'];
            if (this.activationCode) {
                this.accountActivationService
                    .activateAccount(this.activationCode)
                    .subscribe(
                        success => {
                            this.activationPerformed = true;
                            this.activationSuccess = true;
                        },
                        error => {
                            this.activationPerformed = true;
                        },
                    );
            } else {
                this.activationPerformed = true;
            }
        });
    }
}
