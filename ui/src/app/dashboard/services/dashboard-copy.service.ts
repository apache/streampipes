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

import { inject, Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import {
    ChartService,
    Dashboard,
    DashboardService,
} from '@streampipes/platform-services';
import { defer, firstValueFrom, Observable } from 'rxjs';
import { IdGeneratorService } from '../../core-services/id-generator/id-generator.service';

@Injectable()
export class DashboardCopyService {
    private chartService = inject(ChartService);
    private dashboardService = inject(DashboardService);
    private idGenerator = inject(IdGeneratorService);
    private translate = inject(TranslateService);

    // Retain successful copies if saving the dashboard fails and is retried.
    private copiedChartIds = new Map<string, string>();

    createDraft(source: Dashboard): Dashboard {
        const draft: Dashboard = JSON.parse(JSON.stringify(source));
        delete draft.id;
        delete draft.rev;
        draft.elementId = this.idGenerator.generateWithPrefix(
            'sp:dashboardmodel:',
            6,
        );
        draft.name = `${source.name} (${this.translate.instant('Copy')})`;
        draft.metadata = this.newMetadata();
        draft.widgets = (draft.widgets ?? []).map(widget => ({
            ...widget,
            id: this.idGenerator.generateWithPrefix('dashboard-widget-', 10),
        }));
        return draft;
    }

    save(draft: Dashboard, copyCharts: boolean): Observable<Dashboard> {
        return defer(async () => {
            const dashboard: Dashboard = JSON.parse(JSON.stringify(draft));
            dashboard.metadata = this.newMetadata();
            if (copyCharts) {
                for (const item of dashboard.widgets ?? []) {
                    const sourceId = item.dataViewElementId;
                    if (!this.copiedChartIds.has(sourceId)) {
                        const source = await firstValueFrom(
                            this.chartService.getChart(sourceId),
                        );
                        const chart = JSON.parse(JSON.stringify(source));
                        delete chart.rev;
                        delete chart.widgetId;
                        delete chart.affectedSchemaUpdateFields;
                        delete chart.healthStatus;
                        chart.elementId = this.idGenerator.generateWithPrefix(
                            'sp:dataexplorerwidgetmodel:',
                            6,
                        );
                        chart.metadata = this.newMetadata();
                        chart.baseAppearanceConfig.widgetTitle = `${chart.baseAppearanceConfig.widgetTitle} (${this.translate.instant('Copy')})`;
                        const saved = await firstValueFrom(
                            this.chartService.saveChart(chart),
                        );
                        this.copiedChartIds.set(sourceId, saved.elementId);
                    }
                    item.dataViewElementId = this.copiedChartIds.get(sourceId);
                }
            }
            return firstValueFrom(
                this.dashboardService.saveDashboard(dashboard),
            );
        });
    }

    private newMetadata() {
        const now = Date.now();
        return { createdAtEpochMs: now, lastModifiedEpochMs: now };
    }
}
