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
import {
    AdapterService,
    ChartService,
    DashboardService,
    DatalakeRestService,
    FileMetadata,
    FilesService,
    GenericStorageService,
    PipelineElementService,
    PipelineService,
    SpDataStream,
} from '@streampipes/platform-services';
import { zip } from 'rxjs';
import { map } from 'rxjs/operators';

interface AssetLinkNamedResource {
    elementId: string;
    name: string;
}

interface AssetLinkChartResource extends AssetLinkNamedResource {
    baseAppearanceConfig: {
        widgetTitle: string;
    };
}

interface AssetLinkMeasurementResource {
    elementId: string;
    measureName: string;
}

@Directive()
export abstract class BaseAssetLinksDirective {
    protected genericStorageService = inject(GenericStorageService);
    protected pipelineService = inject(PipelineService);
    protected chartService = inject(ChartService);
    protected dashboardService = inject(DashboardService);
    protected dataLakeService = inject(DatalakeRestService);
    protected pipelineElementService = inject(PipelineElementService);
    protected adapterService = inject(AdapterService);
    protected filesService = inject(FilesService);

    // Resources
    pipelines: AssetLinkNamedResource[];
    charts: AssetLinkChartResource[];
    dashboards: AssetLinkNamedResource[];
    dataLakeMeasures: AssetLinkMeasurementResource[];
    dataSources: SpDataStream[];
    adapters: AssetLinkNamedResource[];
    files: FileMetadata[];

    allResources: any[] = [];

    onInit() {
        this.getAllResources();
    }

    getAllResources() {
        zip(
            this.pipelineService
                .getPipelineSummary()
                .pipe(map(summary => summary.resources)),
            this.chartService.getChartSummary().pipe(
                map(summary =>
                    summary.resources.map(chart => ({
                        elementId: chart.elementId,
                        name: chart.name,
                        baseAppearanceConfig: {
                            widgetTitle: chart.name,
                        },
                    })),
                ),
            ),
            this.dashboardService
                .getDashboardSummary()
                .pipe(map(summary => summary.resources)),
            this.pipelineElementService.getDataStreams(),
            this.dataLakeService
                .getMeasurementSummary()
                .pipe(map(summary => summary.resources)),
            this.filesService.getFileMetadata(),
            this.adapterService
                .getAdapterSummary()
                .pipe(map(summary => summary.resources)),
        ).subscribe(
            ([
                pipelines,
                charts,
                dashboards,
                streams,
                measurements,
                files,
                adapters,
            ]) => {
                this.pipelines = pipelines.sort((a, b) =>
                    a.name.localeCompare(b.name),
                );
                this.charts = charts.sort((a, b) =>
                    a.baseAppearanceConfig.widgetTitle.localeCompare(
                        b.baseAppearanceConfig.widgetTitle,
                    ),
                );
                this.dashboards = dashboards.sort((a, b) =>
                    a.name.localeCompare(b.name),
                );
                this.dataSources = streams.sort((a, b) =>
                    a.name.localeCompare(b.name),
                );
                this.dataLakeMeasures = measurements.sort((a, b) =>
                    a.measureName.localeCompare(b.measureName),
                );
                this.files = files.sort((a, b) =>
                    a.filename.localeCompare(b.filename),
                );
                this.adapters = adapters.sort((a, b) =>
                    a.name.localeCompare(b.name),
                );

                this.allResources = [
                    ...this.pipelines,
                    ...this.charts,
                    ...this.dashboards,
                    ...this.dataSources,
                    ...this.dataLakeMeasures,
                    ...this.files,
                    ...this.adapters,
                ];
                this.afterResourcesLoaded();
            },
        );
    }

    abstract afterResourcesLoaded(): void;
}
