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

import { Component, Input, OnInit } from '@angular/core';
import { ConfigItem, DataType } from '@streampipes/platform-services';

@Component({
    selector: 'sp-service-configs-item',
    templateUrl: './service-configs-item.component.html',
})
export class ServiceConfigsItemComponent implements OnInit {
    @Input()
    configuration: ConfigItem;

    isPassword = false;
    isNumber = false;
    isString = false;
    isBoolean = false;

    ngOnInit() {
        this.isPassword = this.configuration.password;
        this.isString = DataType.STRING === this.configuration.valueType;
        this.isNumber = DataType.isNumberType(this.configuration.valueType);
        this.isBoolean = DataType.isBooleanType(this.configuration.valueType);
    }
}
