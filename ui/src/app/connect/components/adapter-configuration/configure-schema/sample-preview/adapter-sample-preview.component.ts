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

import { Component, input, output } from '@angular/core';
import { Mode } from '../../adapter-event-preview/adapter-event-preview.component';

@Component({
    selector: 'sp-adapter-sample-preview',
    standalone: false,
    templateUrl: './adapter-sample-preview.component.html',
})
export class AdapterSamplePreviewComponent {
    isSampleLoading = input(false);
    sampleErrorMessage = input<any>();
    fieldStatusInfos = input<any>();
    input = input<any>();
    sourceViewMode = input<Mode>('raw');

    sourceViewModeChange = output<Mode>();
    getSample = output<void>();
    uploadSample = output<void>();
}
