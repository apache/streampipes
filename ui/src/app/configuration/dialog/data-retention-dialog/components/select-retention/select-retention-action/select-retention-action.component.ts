/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

import { Component, Input, OnInit } from '@angular/core';
import {
    DataExplorerDataConfig,
    DateRange,
    RetentionTimeConfig,
} from '@streampipes/platform-services';
import { DataRetentionConfig } from '../../../model/data-retention-config.model';

@Component({
    selector: 'sp-select-retention-action',
    templateUrl: './select-retention-action.component.html',
    styleUrls: ['./select-retention-action.component.scss'],
    standalone: false,
})
export class SelectRetentionActionComponent {
    @Input() dataExplorerDataConfig: DataExplorerDataConfig;
    @Input() dataRetentionConfig: RetentionTimeConfig;
}
