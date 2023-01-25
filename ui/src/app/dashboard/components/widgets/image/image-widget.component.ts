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

import { Component, OnDestroy, OnInit } from '@angular/core';
import { BaseNgxChartsStreamPipesWidget } from '../base/base-ngx-charts-widget';
import { ResizeService } from '../../../services/resize.service';
import { StaticPropertyExtractor } from '../../../sdk/extractor/static-property-extractor';
import { GaugeConfig } from '../gauge/gauge-config';
import { DatalakeRestService } from '@streampipes/platform-services';
import { SafeUrl } from '@angular/platform-browser';
import { SecurePipe } from '../../../../services/secure.pipe';
import { ImageConfig } from './image-config';

@Component({
    selector: 'sp-image-widget',
    templateUrl: './image-widget.component.html',
    styleUrls: ['./image-widget.component.css'],
})
export class ImageWidgetComponent
    extends BaseNgxChartsStreamPipesWidget
    implements OnInit, OnDestroy
{
    item: any;
    title: string;
    selectedProperty: string;
    imageBaseUrl: string;
    currentImageUrl: SafeUrl;
    imageReady = false;

    constructor(
        dataLakeService: DatalakeRestService,
        resizeService: ResizeService,
        private securePipe: SecurePipe,
    ) {
        super(dataLakeService, resizeService);
    }

    ngOnInit(): void {
        super.ngOnInit();
        this.imageBaseUrl = this.dataLakeService.dataLakeUrl + '/images/';
    }

    ngOnDestroy(): void {
        super.ngOnDestroy();
    }

    extractConfig(extractor: StaticPropertyExtractor) {
        this.title = extractor.singleValueParameter(GaugeConfig.TITLE_KEY);
        this.selectedProperty = extractor.mappingPropertyValue(
            ImageConfig.IMAGE_MAPPING_KEY,
        );
    }

    protected onEvent(events: any[]) {
        const url = this.imageBaseUrl + events[0][this.selectedProperty];
        this.securePipe.transform(url).subscribe(res => {
            this.currentImageUrl = res;
            if (!this.imageReady) {
                this.imageReady = true;
            }
        });
    }

    protected getQueryLimit(extractor: StaticPropertyExtractor): number {
        return 1;
    }

    getFieldsToQuery(): string[] {
        return [this.selectedProperty];
    }
}
