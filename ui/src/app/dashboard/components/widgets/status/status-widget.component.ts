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

import {Component, OnDestroy, OnInit} from "@angular/core";
import {RxStompService} from "@stomp/ng2-stompjs";
import {BaseStreamPipesWidget} from "../base/base-widget";
import {StaticPropertyExtractor} from "../../../sdk/extractor/static-property-extractor";
import {DashboardService} from "../../../services/dashboard.service";
import {ResizeService} from "../../../services/resize.service";
import {StatusWidgetConfig} from "./status-config";


@Component({
  selector: 'status-widget',
  templateUrl: './status-widget.component.html',
  styleUrls: ['./status-widget.component.scss']
})
export class StatusWidgetComponent extends BaseStreamPipesWidget implements OnInit, OnDestroy {

  interval: number;
  active: boolean = false;
  lastTimestamp: number = 0;

  statusLightWidth: string;
  statusLightHeight: string;

  constructor(rxStompService: RxStompService, dashboardService: DashboardService, resizeService: ResizeService) {
    super(rxStompService, dashboardService, resizeService, false);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.onSizeChanged(this.computeCurrentWidth(this.itemWidth), this.computeCurrentHeight(this.itemHeight));
  }

  protected extractConfig(extractor: StaticPropertyExtractor) {
    this.interval = extractor.integerParameter(StatusWidgetConfig.INTERVAL_KEY);
  }

  protected onEvent(event: any) {
    this.active = true;
    let timestamp = new Date().getTime();
    this.lastTimestamp = timestamp;
    setTimeout(() => {
      if (this.lastTimestamp <= timestamp) {
        this.active = false;
      }
    }, (this.interval * 1000));
  }

  protected onSizeChanged(width: number, height: number) {
    let size: string = Math.min(width, height) * 0.6 + "px";
    this.statusLightWidth = size;
    this.statusLightHeight = size;
  }

}
