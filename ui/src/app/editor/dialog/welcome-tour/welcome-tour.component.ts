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

import {DialogRef} from "../../../core-ui/dialog/base-dialog/dialog-ref";
import {RestApi} from "../../../services/rest-api.service";
import {ShepherdService} from "../../../services/tour/shepherd.service";
import {Component} from "@angular/core";

@Component({
  selector: 'welcome-tour',
  templateUrl: './welcome-tour.component.html',
  styleUrls: ['./welcome-tour.component.scss']
})
export class WelcomeTourComponent {

  user: any;

  constructor(private DialogRef: DialogRef<WelcomeTourComponent>,
              private RestApi: RestApi,
              private ShepherdService: ShepherdService) {
  }

  startCreatePipelineTour() {
    this.ShepherdService.startCreatePipelineTour();
    this.close();
  }

  hideTourForever() {
    this.user.hideTutorial = true;
    this.RestApi.updateUserDetails(this.user).then(data => {
      this.close();
    });
  }

  close() {
    this.DialogRef.close();
  };
}
