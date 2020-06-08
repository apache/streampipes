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

import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { EventProperty } from '../../../../../connect/schema-editor/model/EventProperty';

@Component({
  selector: 'sp-aggregate-configuration',
  templateUrl: './aggregate-configuration.component.html',
  styleUrls: ['./aggregate-configuration.component.css']
})
export class AggregateConfigurationComponent implements OnInit {

  aggregationValue = 1;

  aggregationTimeUnit = 's';

  autoAggregationActive = false;

  @Output()
  update: EventEmitter<any> = new EventEmitter();

  constructor() { }

  ngOnInit(): void {
  }

  updateData() {
    this.update.emit({'aggregationValue': this.aggregationValue, 'aggregationTimeUnit': this.aggregationTimeUnit});
  }


}
