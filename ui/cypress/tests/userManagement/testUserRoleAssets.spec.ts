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

import { UserRole } from '../../../src/app/_enums/user-role.enum';
import { UserUtils } from '../../support/utils/UserUtils';
import { User } from '../../support/model/User';
import { AssetUtils } from '../../support/utils/asset/AssetUtils';
import { PermissionUtils } from '../../support/utils/user/PermissionUtils';
import { AssetBuilder } from '../../support/builder/AssetBuilder';

describe('Test User Roles for Dashboards', () => {
    const assetName = 'test-asset';
    let assetUser1: User;
    let assetAdmin1: User;
    let assetAdmin2: User;

    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();

        assetUser1 = UserUtils.createUser(
            'assetUser1',
            UserRole.ROLE_ASSET_USER,
        );

        assetAdmin1 = UserUtils.createUser(
            'assetAdmin1',
            UserRole.ROLE_ASSET_ADMIN,
        );

        assetAdmin2 = UserUtils.createUser(
            'assetAdmin2',
            UserRole.ROLE_ASSET_ADMIN,
        );
    });

    it('Asset is not shared with other users', () => {
        setup();

        // check admin
        assetIsVisibleAndEditableCanChangePermissions(UserUtils.adminUser);

        // check other users
        assetIsNotVisible(assetAdmin2);
    });

    it('Make asset public', () => {
        setup();

        PermissionUtils.markElementAsPublic(assetName);

        assetIsVisibleAndEditableCanChangePermissions(UserUtils.adminUser);

        assetIsVisibleButNotEditable(assetUser1);

        assetIsVisibleAndEditableCannotChangePermissions(assetAdmin2);
    });

    it('Share asset with other user and change ownership', () => {
        setup();

        PermissionUtils.authorizeUser(assetName, assetAdmin2.email);

        assetIsVisibleAndEditableCanChangePermissions(UserUtils.adminUser);

        assetIsVisibleAndEditableCannotChangePermissions(assetAdmin2);

        assetIsNotVisible(assetUser1);

        UserUtils.switchUser(assetAdmin1);
        AssetUtils.goToAssets();
        PermissionUtils.changeOwnership(assetName, assetAdmin2.email);

        assetIsNotVisible(assetAdmin1);

        assetIsVisibleAndEditableCanChangePermissions(UserUtils.adminUser);

        assetIsVisibleAndEditableCanChangePermissions(assetAdmin2);

        assetIsNotVisible(assetUser1);
    });

    it('Asset is shared with group for user 2', () => {
        const assetAdminGroup = 'asset_admin_group';
        UserUtils.createGroup(assetAdminGroup, UserRole.ROLE_ASSET_ADMIN);
        UserUtils.addGroupToUser(assetAdminGroup, assetAdmin2.name);

        setup();

        PermissionUtils.authorizeGroup(assetName, assetAdminGroup);

        assetIsVisibleAndEditableCanChangePermissions(UserUtils.adminUser);

        assetIsNotVisible(assetUser1);

        assetIsVisibleAndEditableCannotChangePermissions(assetAdmin2);
    });

    function setup() {
        UserUtils.switchUser(assetAdmin1);
        AssetUtils.goToAssets();
        const asset = AssetBuilder.create(assetName).build();
        AssetUtils.addAndSaveAsset(asset);
    }

    function assetIsVisibleAndEditableCanChangePermissions(user: User) {
        UserUtils.switchUser(user);
        AssetUtils.checkAmountOfAssets(1);
        AssetUtils.checkAssetCanBeEdited(assetName);

        PermissionUtils.validateUserCanChangePermissions(assetName);
    }

    function assetIsVisibleAndEditableCannotChangePermissions(user: User) {
        UserUtils.switchUser(user);
        AssetUtils.checkAmountOfAssets(1);
        AssetUtils.checkAssetCanBeEdited(assetName);

        PermissionUtils.validateUserCanNotChangePermissions(assetName);
    }

    function assetIsVisibleButNotEditable(user: User) {
        UserUtils.switchUser(user);
        AssetUtils.checkAmountOfAssets(1);
        AssetUtils.checkAssetCanNotBeEdited(assetName);

        PermissionUtils.validateUserCanNotChangePermissions(assetName);
    }

    function assetIsNotVisible(user: User) {
        UserUtils.switchUser(user);
        AssetUtils.checkAmountOfAssets(0);
    }
});
