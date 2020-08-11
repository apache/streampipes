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

import {Component, OnInit} from "@angular/core";
import {BaseNavigationComponent} from "../base-navigation.component";
import {Router} from "@angular/router";
import {RestApi} from "../../../services/rest-api.service";
import {AuthStatusService} from "../../../services/auth-status.service";

@Component({
  selector: 'toolbar',
  templateUrl: './toolbar.component.html',
})
export class ToolbarComponent extends BaseNavigationComponent implements OnInit {

  feedbackOpen: boolean = false;
  accountMenuOpen: boolean = false;

  whiteColor: string = "#FFFFFF";
  greenColor: string = "#39b54a";

  accountMenuBackground: any = this.makeColor('background-color', this.greenColor);
  accountMenuIconColor: any = this.makeColor('color', this.whiteColor);

  feedbackMenuBackground: any = this.makeColor('background-color', this.greenColor);
  feedbackMenuIconColor: any = this.makeColor('color', this.whiteColor);

  constructor(Router: Router,
              private RestApi: RestApi,
              private AuthStatusService: AuthStatusService) {
    super(Router);
  }

  ngOnInit(): void {
  }
  
  openMenu() {
    this.accountMenuOpen = true;
  }
  
  closeMenu() {
    this.accountMenuOpen = false;
  }

  makeColor(type: string, color: string) {
    return {[type]: color};
  }

  toggleFeedback() {
    this.feedbackOpen = !this.feedbackOpen;
    this.updateFeedbackColors();
  }

  closeFeedbackWindow() {
    this.updateFeedbackColors();
    this.feedbackOpen = false;
  }

  triggerAccountMenu($mdMenu, $event) {
    this.updateAccountColors();
    this.accountMenuOpen = true;
    $mdMenu.open($event)
  }

  updateAccountColors() {
    this.accountMenuBackground = this.getNewColor('background-color', this.accountMenuBackground);
    this.accountMenuIconColor = this.getNewColor('color', this.accountMenuIconColor);
  }

  updateFeedbackColors() {
    this.feedbackMenuBackground = this.getNewColor('background-color', this.feedbackMenuBackground);
    this.feedbackMenuIconColor = this.getNewColor('color', this.feedbackMenuIconColor);
  }

  getNewColor(type: string, currentColor: any) {
    return currentColor[type] == this.greenColor ? this.makeColor(type, this.whiteColor) : this.makeColor(type, this.greenColor);
  }

  openDocumentation() {
    window.open('https://streampipes.apache.org/docs', '_blank');
  };

  openInfo() {
    this.Router.navigateByUrl("info");
    this.activePage = "Info";
  }

  logout() {
    this.RestApi.logout().subscribe(() => {
      this.AuthStatusService.user = undefined;
      this.AuthStatusService.authenticated = false;
      this.Router.navigateByUrl('login');
    });
  };

}