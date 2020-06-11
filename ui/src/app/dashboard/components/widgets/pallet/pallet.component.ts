/*
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

import { Component, OnDestroy, OnInit } from '@angular/core';

import { RxStompService } from '@stomp/ng2-stompjs';
import { StaticPropertyExtractor } from '../../../sdk/extractor/static-property-extractor';
import { DashboardService } from '../../../services/dashboard.service';
import { ResizeService } from '../../../services/resize.service';
import { BaseStreamPipesWidget } from '../base/base-widget';
import { NumberConfig } from '../number/number-config';
import { add_items, intialize_default } from './pallet_functions';

declare function convert(packing_plan): any;
declare function render_on_canvas(canvas_id, viewpoint, options_pref, boxes, pallet): any;
declare function scale_pallet(x, y, z): any;



@Component({
  selector: 'sp-pallet',
  templateUrl: './pallet.component.html',
  styleUrls: ['./pallet.component.css']
})

export class PalletComponent extends BaseStreamPipesWidget implements OnInit, OnDestroy {

  selectedProperty: string;

  constructor(rxStompService: RxStompService, dashboardService: DashboardService, resizeService: ResizeService) {
    super(rxStompService, dashboardService, resizeService, false);
  }

  pallet;

  ngOnInit(): void {
    super.ngOnInit();
    const data = intialize_default();
    const boxes = convert(data);
    this.pallet = scale_pallet(data.bin.size.width, data.bin.size.width / 8, data.bin.size.depth);
    render_on_canvas('canvas', 'rotate', {width: 640, height: 480}, boxes, this.pallet);
  }

  ngOnDestroy(): void {
    super.ngOnDestroy();
  }

  extractConfig(extractor: StaticPropertyExtractor) {
    this.selectedProperty = extractor.mappingPropertyValue(NumberConfig.NUMBER_MAPPING_KEY);
  }

  protected onEvent(event: any) {

    let data = intialize_default();

    data = add_items(data, event['pallet']);
    const boxes = convert(data);
    render_on_canvas('canvas', 'rotate', {width: 640, height: 480}, boxes, this.pallet);
  }

  protected onSizeChanged(width: number, height: number) {
  }

}
