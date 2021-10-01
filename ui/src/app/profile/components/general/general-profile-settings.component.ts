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

import { Component, OnDestroy, OnInit } from '@angular/core';
import { ProfileService } from '../../profile.service';
import { BasicProfileSettings } from '../basic-profile-settings';
import { AppConstants } from '../../../services/app.constants';
import { AuthService } from '../../../services/auth.service';
import { JwtTokenStorageService } from '../../../services/jwt-token-storage.service';

@Component({
  selector: 'general-profile-settings',
  templateUrl: './general-profile-settings.component.html',
  styleUrls: ['./general-profile-settings.component.scss']
})
export class GeneralProfileSettingsComponent extends BasicProfileSettings implements OnInit, OnDestroy {

  darkMode = false;
  originalDarkMode = false;
  darkModeChanged = false;

  constructor(private authService: AuthService,
              profileService: ProfileService,
              appConstants: AppConstants,
              tokenService: JwtTokenStorageService) {
    super(profileService, appConstants, tokenService);
  }

  ngOnInit(): void {
    this.authService.darkMode$.subscribe(darkMode => this.darkMode = darkMode);
    this.receiveUserData();
  }

  ngOnDestroy(): void {
    if (!this.darkModeChanged) {
      this.authService.darkMode$.next(this.originalDarkMode);
    }
  }

  changeModePreview(value: boolean) {
    this.authService.darkMode$.next(value);
  }

  onUserDataReceived() {
    this.originalDarkMode = this.userData.darkMode;
    this.authService.darkMode$.next(this.userData.darkMode);
  }

  updateAppearanceMode() {
    this.profileService.updateAppearanceMode(this.darkMode).subscribe(response => {
      this.darkModeChanged = true;
    });
  }

}
