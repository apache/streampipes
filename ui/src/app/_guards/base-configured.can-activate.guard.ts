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

import {
    ActivatedRouteSnapshot,
    CanActivate,
    Router,
    RouterStateSnapshot,
    UrlTree,
} from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';

export abstract class BaseConfiguredCanActivateGuard implements CanActivate {
    constructor(
        protected router: Router,
        protected authService: AuthService,
    ) {}

    canActivate(
        route: ActivatedRouteSnapshot,
        state: RouterStateSnapshot,
    ):
        | Observable<boolean | UrlTree>
        | Promise<boolean | UrlTree>
        | boolean
        | UrlTree {
        return new Promise(resolve =>
            this.authService.checkConfiguration().subscribe(
                configured => {
                    if (!configured) {
                        resolve(this.onIsUnconfigured());
                    } else {
                        resolve(this.onIsConfigured());
                    }
                },
                error => {
                    const url = this.router.parseUrl('startup');
                    resolve(url);
                },
            ),
        );
    }

    abstract onIsConfigured(): boolean | UrlTree;

    abstract onIsUnconfigured(): boolean | UrlTree;
}
