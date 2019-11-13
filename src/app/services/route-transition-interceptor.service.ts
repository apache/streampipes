/*
 * Copyright 2019 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import {AuthService} from "./auth.service";
import {AuthStatusService} from "./auth-status.service";

export class RouteTransitionInterceptorService {

    AuthService: AuthService;
    AuthStatusService: AuthStatusService;
    $q: any;

    publicPages: string[] = ["login", "register", "setup"];

    constructor(AuthService, AuthStatusService, $q) {
        this.AuthService = AuthService;
        this.AuthStatusService = AuthStatusService;
        this.$q = $q;
    }

    onTransitionStarted(transitionInfo) {
        return new Promise(resolve => {
            if (transitionInfo.$to().name !== "startup") {
                this.AuthService.checkConfiguration().then(() => {
                    if (this.AuthStatusService.configured) {
                        this.AuthService.checkAuthentication().then(() => {
                            if (this.isProtectedPage(transitionInfo.$to().name) && !(this.AuthStatusService.authenticated)) {
                                resolve(transitionInfo.router.stateService.target('login'));
                            } else {
                                if (this.AuthStatusService.authenticated && (transitionInfo.$to().name === 'login'
                                    || transitionInfo.$to().name === 'setup')) {
                                    resolve(transitionInfo.router.stateService.target('streampipes'));
                                } else {
                                    if (transitionInfo.$to().name === 'setup') {
                                        resolve(transitionInfo.router.stateService.target('streampipes'));
                                    } else {
                                        resolve(true);
                                    }
                                }
                            }
                        })
                    } else {
                        if (transitionInfo.$to().name == 'setup') {
                            resolve(true);
                        } else {
                            resolve(transitionInfo.router.stateService.target('setup'));
                        }
                    }
                }, (error) => {
                    console.log(error);
                    if (error.status === 504 || error.status === 502) {
                        resolve(transitionInfo.router.stateService.target('startup'));
                    }
                });
            } else {
                resolve(true);
            }
        });
    }

    isProtectedPage(target) {
        return !(this.publicPages.some(p => p === target));
    }

}