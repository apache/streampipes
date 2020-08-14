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

import {Component, OnInit, ViewChild} from "@angular/core";
import {BaseNavigationComponent} from "../base-navigation.component";
import {Router} from "@angular/router";
import {RestApi} from "../../../services/rest-api.service";
import {AuthStatusService} from "../../../services/auth-status.service";
import {MatMenuTrigger} from "@angular/material/menu";

@Component({
  selector: 'toolbar',
  templateUrl: './toolbar.component.html',
  styleUrls: ['./toolbar.component.scss']
})
export class ToolbarComponent extends BaseNavigationComponent implements OnInit {

  @ViewChild('feedbackOpen') feedbackOpen: MatMenuTrigger;
  @ViewChild('accountMenuOpen') accountMenuOpen: MatMenuTrigger;

  constructor(Router: Router,
              private RestApi: RestApi,
              private AuthStatusService: AuthStatusService) {
    super(Router);
  }

  ngOnInit(): void {
    super.onInit();
  }

  closeFeedbackWindow() {
    //this.feedbackOpen = false;
    this.feedbackOpen.closeMenu();
  }

  openDocumentation() {
    window.open('https://streampipes.apache.org/docs', '_blank');
  };

  openInfo() {
    this.Router.navigate(["info"]);
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