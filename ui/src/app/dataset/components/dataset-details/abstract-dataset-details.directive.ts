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

import { Directive, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import {
    DataLakeMeasure,
    DatalakeRestService,
} from '@streampipes/platform-services';
import { SpBreadcrumbService, SpNavigationItem } from '@streampipes/shared-ui';
import { catchError, of } from 'rxjs';
import { SpDatasetDetailsTabs } from './dataset-details-tabs';

@Directive()
export abstract class SpAbstractDatasetDetailsDirective {
    protected activatedRoute = inject(ActivatedRoute);
    protected datalakeRestService = inject(DatalakeRestService);
    protected breadcrumbService = inject(SpBreadcrumbService);

    currentDatasetId: string;
    tabs: SpNavigationItem[] = [];
    dataset: DataLakeMeasure;
    datasetNotFound = false;

    onInit(): void {
        const elementId = this.activatedRoute.snapshot.params.elementId;
        if (elementId) {
            this.currentDatasetId = elementId;
            this.tabs = new SpDatasetDetailsTabs().getTabs(elementId);
            this.loadDataset();
        }
    }

    loadDataset(): void {
        this.datalakeRestService
            .getMeasurement(this.currentDatasetId)
            .pipe(
                catchError(() => {
                    this.datasetNotFound = true;
                    return of(null);
                }),
            )
            .subscribe(dataset => {
                if (!dataset) {
                    return;
                }

                this.dataset = DataLakeMeasure.fromData(dataset);
                this.onDatasetLoaded();
            });
    }

    abstract onDatasetLoaded(): void;
}
