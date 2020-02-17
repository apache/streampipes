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

import {AfterViewInit, Component, ElementRef, OnDestroy, OnInit} from "@angular/core";
import {StaticPropertyExtractor} from "../../../sdk/extractor/static-property-extractor";
import {RxStompService} from "@stomp/ng2-stompjs";
import {LineConfig} from "./line-config";
import {ResizeService} from "../../../services/resize.service";
import {BaseNgxChartsStreamPipesWidget} from "../base/base-ngx-charts-widget";
import {BaseNgxLineChartsStreamPipesWidget} from "../base/base-ngx-line-charts-widget";

@Component({
    selector: 'line-widget',
    templateUrl: './line-widget.component.html',
    styleUrls: ['./line-widget.component.css']
})
export class LineWidgetComponent extends BaseNgxLineChartsStreamPipesWidget implements OnInit, OnDestroy {

    constructor(rxStompService: RxStompService, resizeService: ResizeService) {
        super(rxStompService, resizeService);
    }

    ngOnInit(): void {
        super.ngOnInit();
    }

    ngOnDestroy(): void {
        super.ngOnDestroy();
    }

}