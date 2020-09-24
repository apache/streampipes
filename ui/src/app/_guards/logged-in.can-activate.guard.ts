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

import {Injectable} from "@angular/core";
import {
  ActivatedRouteSnapshot,
  CanActivate,
  Router,
  RouterStateSnapshot,
  UrlTree
} from "@angular/router";
import {Observable} from "rxjs";
import {AuthStatusService} from "../services/auth-status.service";
import {RestApi} from "../services/rest-api.service";

@Injectable()
export class LoggedInCanActivateGuard implements CanActivate {

  constructor(private AuthStatusService: AuthStatusService,
              private Router: Router,
              private RestApi: RestApi) {

  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    return new Promise((resolve) => this.RestApi.getAuthc().subscribe(response => {
      if (response.success) {
        this.AuthStatusService.username = response.info.authc.principal.username;
        this.AuthStatusService.email = response.info.authc.principal.email;
        this.AuthStatusService.authenticated = true;
        this.AuthStatusService.token = response.token;
        let url = this.Router.parseUrl('');
        resolve(url);
      } else {
        this.AuthStatusService.authenticated = false;
        resolve(true);
      }
    }, error => {
      let url = this.Router.parseUrl('startup');
      resolve(url);
    }));
  }
}