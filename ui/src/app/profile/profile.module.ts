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

import {NgModule} from "@angular/core";
import {FlexLayoutModule} from "@angular/flex-layout";
import {FormsModule} from "@angular/forms";
import {MatTabsModule} from "@angular/material/tabs";
import {MatButtonModule} from "@angular/material/button";
import {CustomMaterialModule} from "../CustomMaterial/custom-material.module";
import {CommonModule} from "@angular/common";
import {ProfileComponent} from "./profile.component";
import {TokenManagementSettingsComponent} from "./components/token/token-management-settings.component";
import {GeneralProfileSettingsComponent} from "./components/general/general-profile-settings.component";
import {ProfileService} from "./profile.service";
import {MatDividerModule} from "@angular/material/divider";

@NgModule({
  imports: [
    FlexLayoutModule,
    FormsModule,
    MatDividerModule,
    MatTabsModule,
    MatButtonModule,
    CustomMaterialModule,
    CommonModule,
  ],
  declarations: [
    GeneralProfileSettingsComponent,
    ProfileComponent,
    TokenManagementSettingsComponent
  ],
  providers: [
    ProfileService
  ],
  exports: [
    ProfileComponent
  ],
  entryComponents: [
    ProfileComponent
  ]
})
export class ProfileModule {

  constructor() {
  }

}
