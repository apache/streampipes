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

import { ProfileService } from '../profile.service';
import { UserAccount } from '@streampipes/platform-services';
import { Directive } from '@angular/core';
import { AppConstants } from '../../services/app.constants';
import { AuthService } from '../../services/auth.service';
import { CurrentUserService } from '@streampipes/shared-ui';

@Directive()
export abstract class BasicProfileSettings {
    userData: UserAccount;
    profileLoaded = false;
    profileUpdating = false;
    errorMessage: string;

    constructor(
        protected profileService: ProfileService,
        public appConstants: AppConstants,
        protected currentUserService: CurrentUserService,
        protected authService: AuthService,
    ) {}

    receiveUserData() {
        this.profileService
            .getUserProfile(this.currentUserService.user$.getValue().username)
            .subscribe(userData => {
                this.userData = userData;
                this.onUserDataReceived();
                this.profileLoaded = true;
            });
    }

    saveProfileSettings() {
        this.profileUpdating = true;
        this.profileService
            .updateUserProfile(this.userData)
            .subscribe(response => {
                this.profileUpdating = false;
                if (response.success) {
                    this.receiveUserData();
                } else {
                    this.errorMessage = response.notifications[0].title;
                }
            });
    }

    abstract onUserDataReceived();
}
