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

import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { AdapterService, SpDataStream } from '@streampipes/platform-services';
import { RestService } from '../../../services/rest.service';

@Component({
    selector: 'sp-adapter-started-preview',
    templateUrl: './adapter-started-preview.component.html',
})
export class SpAdapterStartedPreviewComponent implements OnInit {
    @Input()
    streamDescription: SpDataStream;

    @Input()
    adapterElementId: string;

    constructor(
        private adapterService: AdapterService,
        private restService: RestService,
    ) {}

    ngOnInit() {
        this.getLiveViewPreview();
    }

    private getLiveViewPreview() {
        this.adapterService
            .getAdapter(this.adapterElementId)
            .subscribe(adapter => {
                this.restService
                    .getSourceDetails(adapter.correspondingDataStreamElementId)
                    .subscribe(st => {
                        this.streamDescription = st;
                    });
            });
    }
}
