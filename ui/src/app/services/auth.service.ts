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

import {RestApi} from "./rest-api.service";
import {AuthStatusService} from "./auth-status.service";
import {Inject, Injectable} from "@angular/core";
import {Observable} from "rxjs";

@Injectable()
export class AuthService {

    constructor(@Inject("RestApi") private RestApi: RestApi,
                @Inject("AuthStatusService") private AuthStatusService: AuthStatusService) {
    }

    checkConfiguration() {
        return Observable.create((observer) => this.RestApi.configured().subscribe(response => {
            if (response.configured) {
                this.AuthStatusService.configured = true;
                // TODO
                //this.$rootScope.appConfig = response.data.appConfig;
            } else {
                this.AuthStatusService.configured = false;
            }
            observer.complete();
        }, error => {
            observer.error();
        }));
    }

    checkAuthentication() {
        return this.RestApi.getAuthc().subscribe(response => {
            if (response.success) {
                this.AuthStatusService.username = response.info.authc.principal.username;
                this.AuthStatusService.email = response.info.authc.principal.email;
                this.AuthStatusService.authenticated = true;
                this.AuthStatusService.token = response.token;
            } else {
                this.AuthStatusService.authenticated = false;
            }
        })
    }
}