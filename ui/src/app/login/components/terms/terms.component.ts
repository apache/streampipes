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
import { BaseLoginPageDirective } from '../base-login-page.directive';
import { ActivatedRoute, Router } from '@angular/router';
import { CurrentUserService } from '@streampipes/shared-ui';
import { ProfileService } from '../../../profile/profile.service';
import { AuthService } from '../../../services/auth.service';
import { UserAccount } from '@streampipes/platform-services';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

@Component({
    selector: 'sp-terms',
    templateUrl: './terms.component.html',
    styleUrls: ['./terms.component.scss'],
    standalone: false,
})
export class TermsComponent extends BaseLoginPageDirective {
    returnUrl = '';
    showAcknowledgment = false;
    profile: UserAccount;
    sanitizedText: SafeHtml | undefined;

    private router = inject(Router);
    private route = inject(ActivatedRoute);
    private profileService = inject(ProfileService);
    private authService = inject(AuthService);
    private currentUserService = inject(CurrentUserService);
    private sanitizer = inject(DomSanitizer);

    onSettingsAvailable(): void {
        this.returnUrl = this.route.snapshot.queryParams.returnUrl || '';
        if (!this.authService.authenticated()) {
            this.router.navigate(['login']);
        } else {
            if (this.loginSettings.termsAcknowledgmentRequired) {
                this.profileService
                    .getUserProfile(
                        this.currentUserService.getCurrentUser().username,
                    )
                    .subscribe(profile => {
                        if (!profile.hasAcknowledged) {
                            const normalizedText = this.normalizeNbsp(
                                this.loginSettings.termsAcknowledgmentText,
                            );
                            this.sanitizedText =
                                this.sanitizer.bypassSecurityTrustHtml(
                                    normalizedText,
                                );
                            this.profile = profile;
                            this.showAcknowledgment = true;
                        } else {
                            this.proceedWithLogin();
                        }
                    });
            } else {
                this.proceedWithLogin();
            }
        }
    }

    onTermsAcknowledged(): void {
        const userInfo = this.currentUserService.getCurrentUser();
        userInfo.hasAcknowledged = true;
        this.currentUserService.user$.next(userInfo);
        this.profileService
            .updateUserProfile({
                ...this.profile,
                hasAcknowledged: true,
            })
            .subscribe(() => this.proceedWithLogin());
    }

    onTermsRejected(): void {
        this.authService.logout();
        this.router.navigate(['login']);
    }

    proceedWithLogin(): void {
        this.router.navigateByUrl(this.returnUrl);
    }

    private normalizeNbsp(html: string): string {
        return html.replace(/&nbsp;|\u00A0/g, ' ');
    }
}
