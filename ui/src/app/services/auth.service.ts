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
import { filter, map, switchMap, tap } from 'rxjs/operators';
import { Router } from '@angular/router';
import { LoginService } from '../login/services/login.service';

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
        this.tokenStorage.saveToken(data.accessToken);
        this.tokenStorage.saveUser(data.userInfo);
        this.authToken$.next(data.accessToken);
        this.user$.next(data.userInfo);
    }

    public logout() {
        this.tokenStorage.clearTokens();
        this.authToken$.next(undefined);
    }

    public getCurrentUser(): UserInfo {
        return this.tokenStorage.getUser();
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
}
