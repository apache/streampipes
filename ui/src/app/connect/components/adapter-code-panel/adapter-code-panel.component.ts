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

import { Component, Input, OnInit, inject } from '@angular/core';
import {
    AdapterDescription,
    AdapterService,
    CompactAdapter,
} from '@streampipes/platform-services';
import { FlexDirective } from '@ngbracket/ngx-layout/flex';
import { ConfigurationCodePanelComponent } from '../../../core-ui/configuration-code-panel/configuration-code-panel.component';

@Component({
    selector: 'sp-adapter-code-panel',
    templateUrl: './adapter-code-panel.component.html',
    styleUrls: ['./adapter-code-panel.component.scss'],
    imports: [FlexDirective, ConfigurationCodePanelComponent],
})
export class AdapterCodePanelComponent implements OnInit {
    private adapterService = inject(AdapterService);

    @Input()
    adapterDescription: AdapterDescription;

    @Input()
    maxHeight = '300px';

    compactAdapter: CompactAdapter;

    ngOnInit(): void {
        this.adapterService
            .convertToCompactAdapter(this.adapterDescription)
            .subscribe(res => {
                this.compactAdapter = res;
            });
    }
}
