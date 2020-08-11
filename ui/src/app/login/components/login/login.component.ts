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

import {ShepherdService} from "../../../services/tour/shepherd.service";
import {Component, Inject} from "@angular/core";
import {RestApi} from "../../../services/rest-api.service";
import {AuthStatusService} from "../../../services/auth-status.service";
import {FormGroup} from "@angular/forms";
import {LoginService} from "../../services/login.service";
import {Router} from "@angular/router";

@Component({
    selector: 'login',
    templateUrl: './login.component.html'
})
export class LoginComponent {

    loading: any;
    authenticationFailed: any;
    credentials: any;

    constructor(private loginService: LoginService,
                private Router: Router,
                private AuthStatusService: AuthStatusService,
                private ShepherdService: ShepherdService) {
        this.loading = false;
        this.authenticationFailed = false;
        this.credentials = {};
    }

    openDocumentation(){
       window.open('https://streampipes.apache.org/docs', '_blank');
    };

    logIn() {
        this.authenticationFailed = false;
        this.loading = true;
        this.loginService.login(this.credentials)
            .subscribe(response => { // success
                    this.loading = false;
                    if (response.success) {
                        this.AuthStatusService.username = response.info.authc.principal.username;
                        this.AuthStatusService.email = response.info.authc.principal.email;
                        this.AuthStatusService.token = response.token;
                        this.AuthStatusService.authenticated = true;
                        this.Router.navigateByUrl("");
                    }
                    else {
                        this.AuthStatusService.authenticated = false;
                        this.authenticationFailed = true;
                    }

                }, response => { // error
                    this.loading = false;
                    this.AuthStatusService.authenticated = false;
                    this.authenticationFailed = true;
                }
            )
    };

    setSheperdServiceDelay() {
        //this.ShepherdService.setTimeWaitMillies(100);
    }
};