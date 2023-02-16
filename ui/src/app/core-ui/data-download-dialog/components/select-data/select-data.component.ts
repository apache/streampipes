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

import { Component, Input } from '@angular/core';

import { DataExplorerDataConfig } from '@streampipes/platform-services';
import { DataExportConfig } from '../../model/data-export-config.model';

@Component({
    selector: 'sp-select-data',
    templateUrl: './select-data.component.html',
    styleUrls: ['./select-data.component.scss'],
})
export class SelectDataComponent {
    /**
     * Contains the measurement and date range for a selected data widget
     * This value is not required
     */
    @Input() dataExplorerDataConfig: DataExplorerDataConfig;

    /**
     * Represents the user configurations for the download
     */
    @Input() dataExportConfig: DataExportConfig;
}
