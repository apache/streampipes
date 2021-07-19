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
import {Client} from "@stomp/stompjs";
import {NotificationItem} from "../../../notifications/model/notifications.model";
import {Router} from "@angular/router";
import {AuthStatusService} from "../../../services/auth-status.service";
import {NotificationCountService} from "../../../services/notification-count-service";

@Component({
  selector: 'iconbar',
  templateUrl: './iconbar.component.html',
  styleUrls: ['./iconbar.component.scss']
})
export class IconbarComponent extends BaseNavigationComponent implements OnInit {

  unreadNotifications: number = 0;

  constructor(Router: Router,
              private AuthStatusService: AuthStatusService,
              public NotificationCountService: NotificationCountService) {
    super(Router);
  }

  ngOnInit(): void {
    super.onInit();
    this.connectToBroker();
    this.NotificationCountService.loadUnreadNotifications();
  }

  connectToBroker() {
    var login = 'admin';
    var passcode = 'admin';
    var websocketProtocol = window.location.protocol === "http:" ? "ws" : "wss";
    var brokerUrl = websocketProtocol + '://' + window.location.hostname + ':' + window.location.port + '/streampipes/ws';
    var inputTopic = '/topic/org.apache.streampipes.notifications.' + this.AuthStatusService.email;

    let stompClient = new Client({
      brokerURL: brokerUrl,
      connectHeaders: {
        login: login,
        passcode: passcode
      },
      reconnectDelay: 5000
    });

    stompClient.onConnect = (frame) => {

      stompClient.subscribe(inputTopic, message => {
        this.NotificationCountService.increaseNotificationCount(JSON.parse(message.body) as NotificationItem);
      });
    };

    stompClient.activate();
  }
}
