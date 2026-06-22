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

import { UserBuilder } from '../../support/builder/UserBuilder';
import { UserRole } from '../../../src/app/core/auth/user-role.enum';
import { UserUtils } from '../../support/utils/UserUtils';

describe('Reset resource authorization', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
    });

    it('does not allow non-admin users to reset the system', () => {
        const user = UserBuilder.create('non-admin@streampipes.apache.org')
            .setName('non-admin')
            .setPassword('non-admin')
            .addRole(UserRole.ROLE_PIPELINE_USER)
            .build();

        UserUtils.addUser(user);
        UserUtils.switchUser(user);

        cy.window().then(win => {
            cy.request({
                method: 'POST',
                url: '/streampipes-backend/api/v2/reset',
                auth: {
                    bearer: win.localStorage.getItem('auth-token'),
                },
                failOnStatusCode: false,
            }).then(response => {
                expect(response.status).to.eq(403);
            });
        });
    });
});
