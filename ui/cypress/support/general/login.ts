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

import '@angular/compiler';
import { UserUtils } from '../utils/UserUtils';
import { JwtHelperService } from '@auth0/angular-jwt';
import { User } from '../model/User';

declare global {
    namespace Cypress {
        interface Chainable {
            /**
             * Login into streampipes with standard test user
             * @example cy.login();
             */
            login: typeof login;
        }
    }
}

export const login = (user?: User) => {
    let _user;
    _user = !user ? UserUtils.adminUser : user;

    cy.request('POST', '/streampipes-backend/api/v2/auth/login', {
        username: _user.email,
        password: _user.password,
    }).then(res => {
        const decodedToken = new JwtHelperService({}).decodeToken(
            res.body.accessToken,
        );
        window.localStorage.setItem(
            'auth-user',
            JSON.stringify(decodedToken.user),
        );
        window.localStorage.setItem('auth-token', res.body.accessToken);
        console.log(user);
    });
};
