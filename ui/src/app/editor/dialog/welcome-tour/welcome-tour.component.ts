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

import { DialogRef } from '@streampipes/shared-ui';
import { ShepherdService } from '../../../services/tour/shepherd.service';
import { Component, Input, OnInit } from '@angular/core';
import { AppConstants } from '../../../services/app.constants';
import { AuthService } from '../../../services/auth.service';
import { UserAccount, UserInfo } from '@streampipes/platform-services';
import { ProfileService } from '../../../profile/profile.service';

@Component({
    selector: 'sp-welcome-tour',
    templateUrl: './welcome-tour.component.html',
    styleUrls: ['./welcome-tour.component.scss'],
})
export class WelcomeTourComponent implements OnInit {
    @Input()
    userInfo: UserInfo;

    currentUser: UserAccount;

    constructor(
        private authService: AuthService,
        private dialogRef: DialogRef<WelcomeTourComponent>,
        private profileService: ProfileService,
        private shepherdService: ShepherdService,
        public appConstants: AppConstants,
    ) {}

    ngOnInit(): void {
        this.profileService
            .getUserProfile(this.userInfo.username)
            .subscribe(data => {
                this.currentUser = data;
            });
    }

    startCreatePipelineTour() {
        this.shepherdService.startCreatePipelineTour();
        this.close();
    }

    hideTourForever() {
        this.currentUser.hideTutorial = true;
        this.profileService
            .updateUserProfile(this.currentUser)
            .subscribe(data => {
                this.authService.updateTokenAndUserInfo();
                this.close();
            });
    }

    close() {
        this.dialogRef.close();
    }
}
