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

import {BaseStreamPipesWidget} from "./base-widget";
import {RxStompService} from "@stomp/ng2-stompjs";
import {ResizeService} from "../../../services/resize.service";
import {GridsterInfo} from "../../../models/gridster-info.model";
import {GridsterItemComponent} from "angular-gridster2";
import {DashboardService} from "../../../services/dashboard.service";

export abstract class BaseNgxChartsStreamPipesWidget extends BaseStreamPipesWidget {

    view: any[] = [];
    displayChart: boolean = false;

    colorScheme: any;

    constructor(rxStompService: RxStompService, dashboardService: DashboardService, resizeService: ResizeService) {
        super(rxStompService, dashboardService, resizeService, true);
    }

    ngOnInit() {
        super.ngOnInit();
        this.colorScheme = {domain: [this.selectedSecondaryTextColor, this.selectedPrimaryTextColor]};
        this.view = [this.computeCurrentWidth(this.gridsterItemComponent),
            this.computeCurrentHeight(this.gridsterItemComponent)];
        this.displayChart = true;
    }

    protected onSizeChanged(width: number, height: number) {
        this.displayChart = false;
        this.view = [width, height];
        this.displayChart = true;
    }

}