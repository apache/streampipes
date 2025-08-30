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

import { inject, Injectable } from '@angular/core';
import {
    ActivatedRouteSnapshot,
    CanActivateChild,
    GuardResult,
    MaybeAsync,
    Router,
    RouterStateSnapshot,
} from '@angular/router';
import { CurrentUserService } from '@streampipes/shared-ui';
import { LoginService } from '../login/services/login.service';
import { of, take } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class TermsCanActivateChildrenGuard implements CanActivateChild {
    canActivateChild(
        childRoute: ActivatedRouteSnapshot,
        state: RouterStateSnapshot,
    ): MaybeAsync<GuardResult> {
        const currentUser = this.currentUserService.getCurrentUser();
        return this.loginService.fetchLoginSettings().pipe(
            take(1),
            map(settings => {
                const needsAck =
                    settings.termsAcknowledgmentRequired &&
                    !currentUser?.hasAcknowledged;

                if (needsAck) {
                    return this.router.createUrlTree(['/terms'], {
                        queryParams: { returnUrl: state.url },
                    });
                }
                return true;
            }),
            catchError(() => of(true)),
        );
    }

    private currentUserService = inject(CurrentUserService);
    private loginService = inject(LoginService);

    private router = inject(Router);
}
