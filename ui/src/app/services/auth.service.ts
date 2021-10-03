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

import { RestApi } from './rest-api.service';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, timer } from 'rxjs';
import { JwtHelperService } from '@auth0/angular-jwt';
import { JwtTokenStorageService } from './jwt-token-storage.service';
import { UserInfo } from '../core-model/gen/streampipes-model-client';
import { filter, map, switchMap } from 'rxjs/operators';
import { Router } from '@angular/router';
import { LoginService } from '../login/services/login.service';
import { PageName } from '../_enums/page-name.enum';
import { UserRole } from '../_enums/user-role.enum';

@Injectable()
export class AuthService {

    public authToken$: BehaviorSubject<string> = new BehaviorSubject<string>(undefined);
    public user$: BehaviorSubject<any> = new BehaviorSubject<any>(undefined);
    public isLoggedIn$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
    public darkMode$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

    constructor(private restApi: RestApi,
                private tokenStorage: JwtTokenStorageService,
                private router: Router,
                private loginService: LoginService) {
        if (this.authenticated()) {
            this.authToken$.next(tokenStorage.getToken());
            this.user$.next(tokenStorage.getUser());
            this.isLoggedIn$.next(true);

        } else {
            this.logout();
        }
        this.scheduleTokenRenew();
        this.watchTokenExpiration();
    }

    public login(data) {
        const jwtHelper: JwtHelperService = new JwtHelperService({});
        const decodedToken = jwtHelper.decodeToken(data.accessToken);
        console.log(decodedToken);
        this.tokenStorage.saveToken(data.accessToken);
        this.tokenStorage.saveUser(decodedToken.user);
        this.authToken$.next(data.accessToken);
        this.user$.next(decodedToken.user);
    }

    public logout() {
        this.tokenStorage.clearTokens();
        this.authToken$.next(undefined);
    }

    public getCurrentUser(): UserInfo {
        return this.user$.getValue() || this.tokenStorage.getUser();
    }

    public authenticated(): boolean {
        const token = this.authToken$.getValue() || this.tokenStorage.getToken();
        const jwtHelper: JwtHelperService = new JwtHelperService({});

        return token !== null && token !== undefined && !jwtHelper.isTokenExpired(token);
    }

    public decodeJwtToken(token: string): any {
        const jwtHelper: JwtHelperService = new JwtHelperService({});
        return jwtHelper.decodeToken(token);
    }

    checkConfiguration(): Observable<boolean> {
        return Observable.create((observer) => this.restApi.configured().subscribe(response => {
            if (response.configured) {
                observer.next(true);
            } else {
                observer.next(false);
            }
        }, error => {
            observer.error();
        }));
    }

    scheduleTokenRenew() {
        this.authToken$.pipe(
            filter((token: any) => !!token),
            map((token: any) => new JwtHelperService({}).getTokenExpirationDate(token)),
            switchMap((expiresIn: Date) => timer(expiresIn.getTime() - Date.now() - 60000)),
        ).subscribe(() => {
            if (this.authenticated()) {
                this.loginService.renewToken().subscribe(data => {
                    this.login(data);
                });
            }
        });
    }

    watchTokenExpiration() {
        this.authToken$.pipe(
            filter((token: any) => !!token),
            map((token: any) => new JwtHelperService({}).getTokenExpirationDate(token)),
            switchMap((expiresIn: Date) => timer(expiresIn.getTime() - Date.now() + 1))
        ).subscribe(() => {
            this.logout();
            this.router.navigate(['login']);
        });
    }

    getUserRoles(): string[] {
        return this.getCurrentUser().roles;
    }

    public hasRole(role: UserRole): boolean {
        return this.getUserRoles().includes(UserRole[role]);
    }

    public hasAnyRole(roles: UserRole[]): boolean {
        if (Array.isArray(roles)) {
            return roles.reduce((aggregator: false, role: UserRole) => aggregator || this.hasRole(role), false);
        }

        return false;
    }

    isAnyAccessGranted(pageNames: PageName[]): boolean {
        if (!pageNames || pageNames.length === 0) {
            return true;
        }

        const result = pageNames.some(pageName => this.isAccessGranted(pageName));
        if (!result) {
            this.router.navigate(['']);
        }
        console.log(pageNames);
        console.log(result);
        return result;
    }

    isAccessGranted(pageName: PageName) {
        console.log(pageName);
        console.log(this.hasRole(UserRole.ADMIN));
        if (this.hasRole(UserRole.ADMIN)) {
            return true;
        }
        switch (pageName) {
            case PageName.HOME:
                return true;
            case PageName.PIPELINE_EDITOR:
                return this.hasAnyRole([UserRole.PIPELINE_ADMIN]);
            case PageName.PIPELINE_OVERVIEW:
                return this.hasAnyRole([UserRole.PIPELINE_ADMIN]);
            case PageName.CONNECT:
                return this.hasAnyRole([UserRole.CONNECT_ADMIN]);
            case PageName.DASHBOARD:
                return this.hasAnyRole([UserRole.DASHBOARD_USER, UserRole.DASHBOARD_ADMIN]);
            case PageName.DATA_EXPLORER:
                return this.hasAnyRole([UserRole.DATA_EXPLORER_ADMIN, UserRole.DATA_EXPLORER_USER]);
            case PageName.APPS:
                return this.hasAnyRole([UserRole.APP_USER]);
            case PageName.FILE_UPLOAD:
                return this.hasAnyRole([UserRole.CONNECT_ADMIN, UserRole.PIPELINE_ADMIN]);
            case PageName.INSTALL_PIPELINE_ELEMENTS:
                return this.hasAnyRole([UserRole.ADMIN]);
            case PageName.SETTINGS:
                return this.hasAnyRole([UserRole.ADMIN]);
            default:
                return true;
        }
    }
}
