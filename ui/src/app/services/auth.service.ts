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
import { Injectable, inject } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { fromEvent, Observable, of, throwError, timer } from 'rxjs';
import { JwtHelperService } from '@auth0/angular-jwt';
import {
    catchError,
    filter,
    finalize,
    map,
    retry,
    shareReplay,
    switchMap,
    tap,
} from 'rxjs/operators';
import { Router } from '@angular/router';
import { LoginService } from '../login/services/login.service';
import { UserInfo } from '@streampipes/platform-services';
import {
    CurrentUserService,
    JwtTokenStorageService,
} from '@streampipes/shared-ui';

@Injectable({ providedIn: 'root' })
export class AuthService {
    private restApi = inject(RestApi);
    private tokenStorage = inject(JwtTokenStorageService);
    private currentUserService = inject(CurrentUserService);
    private router = inject(Router);
    private loginService = inject(LoginService);

    private refreshInFlight$?: Observable<boolean>;

    /** Delay before retrying a refresh that lost the race against another tab. */
    private static readonly CONCURRENT_REFRESH_RETRY_DELAY_MS = 1000;
    /** Spreads the scheduled renewal of tabs sharing a token so they rarely refresh at the same moment. */
    private static readonly RENEW_JITTER_MS = 30000;

    constructor() {
        const tokenStorage = this.tokenStorage;

        if (this.authenticated() && tokenStorage.getUser()) {
            this.currentUserService.authToken$.next(tokenStorage.getToken());
            this.currentUserService.user$.next(tokenStorage.getUser());
            this.currentUserService.isLoggedIn$.next(true);
        } else {
            this.clearLocalAuthState();
        }
        this.scheduleTokenRenew();
        this.watchTokenExpiration();
        this.adoptTokensRefreshedInOtherTabs();
    }

    public login(data) {
        const jwtHelper: JwtHelperService = new JwtHelperService({});
        const decodedToken = jwtHelper.decodeToken(data.accessToken);
        this.tokenStorage.saveToken(data.accessToken);
        this.tokenStorage.saveUser(decodedToken.user);
        this.applyToken(data.accessToken, decodedToken.user);
    }

    public oauthLogin(token: string) {
        const jwtHelper: JwtHelperService = new JwtHelperService({});
        const decodedToken = jwtHelper.decodeToken(token);
        this.tokenStorage.saveToken(token);
        this.tokenStorage.saveUser(decodedToken.user);
        this.currentUserService.authToken$.next(token);
        this.currentUserService.user$.next(decodedToken.user);
        this.currentUserService.isLoggedIn$.next(true);
    }

    public logout() {
        this.loginService.logout().subscribe({
            next: () => this.clearLocalAuthState(),
            error: () => this.clearLocalAuthState(),
        });
    }

    public ensureAuthenticated(returnUrl: string): Observable<boolean> {
        if (this.authenticated()) {
            return of(true);
        }

        return this.refreshAccessToken().pipe(
            tap(authenticated => {
                if (!authenticated) {
                    this.router.navigate(['/login'], {
                        queryParams: { returnUrl },
                    });
                }
            }),
        );
    }

    public authenticated(): boolean {
        const token =
            this.currentUserService.authToken$.getValue() ||
            this.tokenStorage.getToken();
        const jwtHelper: JwtHelperService = new JwtHelperService({});

        return (
            token !== null &&
            token !== undefined &&
            !jwtHelper.isTokenExpired(token)
        );
    }

    checkConfiguration(): Observable<boolean> {
        return Observable.create(observer =>
            this.restApi.configured().subscribe(
                response => {
                    if (response.configured) {
                        observer.next(true);
                    } else {
                        observer.next(false);
                    }
                },
                _error => {
                    observer.error();
                },
            ),
        );
    }

    scheduleTokenRenew() {
        this.currentUserService.authToken$
            .pipe(
                filter((token: any) => !!token),
                map((token: any) =>
                    new JwtHelperService({}).getTokenExpirationDate(token),
                ),
                filter(
                    (expiresIn: Date | null): expiresIn is Date => !!expiresIn,
                ),
                switchMap((expiresIn: Date) =>
                    timer(
                        expiresIn.getTime() -
                            Date.now() -
                            60000 -
                            Math.random() * AuthService.RENEW_JITTER_MS,
                    ),
                ),
            )
            .subscribe(() => {
                if (this.currentUserService.authToken$.getValue()) {
                    this.updateTokenAndUserInfo();
                }
            });
    }

    updateTokenAndUserInfo() {
        this.refreshAccessToken().subscribe();
    }

    watchTokenExpiration() {
        this.currentUserService.authToken$
            .pipe(
                filter((token: any) => !!token),
                map((token: any) =>
                    new JwtHelperService({}).getTokenExpirationDate(token),
                ),
                filter(
                    (expiresIn: Date | null): expiresIn is Date => !!expiresIn,
                ),
                switchMap((expiresIn: Date) =>
                    timer(expiresIn.getTime() - Date.now() + 1),
                ),
            )
            .subscribe(() => {
                this.refreshAccessToken().subscribe(authenticated => {
                    if (!authenticated) {
                        this.router.navigate(['login']);
                    }
                });
            });
    }

    private refreshAccessToken(): Observable<boolean> {
        if (this.refreshInFlight$) {
            return this.refreshInFlight$;
        }

        this.refreshInFlight$ = this.requestRefreshedToken().pipe(
            map(() => true),
            catchError(() => {
                if (this.adoptTokenFromOtherTab()) {
                    return of(true);
                }
                this.clearLocalAuthState();
                return of(false);
            }),
            finalize(() => {
                this.refreshInFlight$ = undefined;
            }),
            shareReplay({ bufferSize: 1, refCount: true }),
        );

        return this.refreshInFlight$;
    }

    /**
     * Requests a new access token. A 409 means another tab sharing the refresh cookie rotated it a moment
     * earlier and received the new cookie, so retry once after a short delay. If that fails too, the caller
     * falls back to the token the other tab stored.
     */
    private requestRefreshedToken(): Observable<void> {
        return this.loginService.refreshToken().pipe(
            retry({
                count: 1,
                delay: (error: HttpErrorResponse) =>
                    error?.status === 409
                        ? timer(AuthService.CONCURRENT_REFRESH_RETRY_DELAY_MS)
                        : throwError(() => error),
            }),
            map(data => this.login(data)),
        );
    }

    private adoptTokensRefreshedInOtherTabs() {
        fromEvent<StorageEvent>(window, 'storage').subscribe(() =>
            this.adoptTokenFromOtherTab(),
        );
    }

    /** Uses a valid access token another tab stored if it differs from this tab's token. */
    private adoptTokenFromOtherTab(): boolean {
        const storedToken = this.tokenStorage.getToken();
        if (
            !storedToken ||
            storedToken === this.currentUserService.authToken$.getValue() ||
            new JwtHelperService({}).isTokenExpired(storedToken)
        ) {
            return false;
        }
        this.applyToken(
            storedToken,
            new JwtHelperService({}).decodeToken(storedToken).user,
        );
        return true;
    }

    private applyToken(token: string, user: UserInfo) {
        this.currentUserService.authToken$.next(token);
        this.currentUserService.user$.next(user);
        this.currentUserService.isLoggedIn$.next(true);
    }

    private clearLocalAuthState() {
        this.tokenStorage.clearTokens();
        this.currentUserService.authToken$.next(undefined);
        this.currentUserService.user$.next(undefined);
        this.currentUserService.isLoggedIn$.next(false);
    }

    public hasRole(role: string): boolean {
        return this.currentUserService.hasRole(role);
    }

    public hasAnyRole(roles: string[]): boolean {
        return this.currentUserService.hasAnyRole(roles);
    }

    isAnyAccessGranted(privileges: string[], redirect?: boolean): boolean {
        if (!privileges || privileges.length === 0) {
            return true;
        }

        const result = this.hasAnyRole(privileges);
        if (!result && redirect) {
            this.router.navigate(['']);
        }
        return result;
    }
}
