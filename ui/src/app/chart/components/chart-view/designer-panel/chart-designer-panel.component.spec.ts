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

import { provideHttpClient } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { DatalakeRestService } from '@streampipes/platform-services';
import { of } from 'rxjs';
import {
    SearchSelectComponent,
    SpAssetBrowserService,
} from '@streampipes/shared-ui';
import { By } from '@angular/platform-browser';
import { provideTranslateService, TranslateService } from '@ngx-translate/core';
import { describe, expect, it, vi } from 'vitest';
import { ChartRegistry } from '../../../../chart-shared/registry/chart-registry.service';
import { ChartAppearanceSettingsComponent } from './appearance-settings/chart-appearance-settings.component';
import { ChartVisualisationSettingsComponent } from './visualisation-settings/chart-visualisation-settings.component';
import { ChartDesignerPanelComponent } from './chart-designer-panel.component';

import { FieldSelectionPanelComponent } from './data-settings/field-selection-panel/field-selection-panel.component';

describe('Chart designer', () => {
    it('lists all fields and applies bulk selection only to search matches', async () => {
        TestBed.configureTestingModule({
            providers: [provideTranslateService()],
        });
        const translator = TestBed.inject(TranslateService);
        translator.setTranslation('en', {
            '{{count}} selected': '{{count}} selected',
        });
        translator.use('en');
        const fixture = TestBed.createComponent(FieldSelectionPanelComponent);
        const eventProperties = Array.from({ length: 8 }, (_, i) => ({
            runtimeName: 'sensor-' + i,
        }));
        fixture.componentRef.setInput('sourceConfig', {
            queryType: 'raw',
            measure: { eventSchema: { eventProperties } },
            queryConfig: { fields: [] },
        });
        fixture.detectChanges();
        const panel: HTMLElement = fixture.nativeElement;
        const rows = () => panel.querySelectorAll('sp-field-selection');
        expect(rows()).toHaveLength(8);

        const search = panel.querySelector<HTMLInputElement>(
            '[data-cy=data-explorer-field-search]',
        );
        search.value = 'sensor-7';
        search.dispatchEvent(new Event('input'));
        fixture.detectChanges();
        expect(rows()).toHaveLength(1);
        expect(rows()[0].textContent).toContain('sensor-7');
        panel
            .querySelector<HTMLButtonElement>(
                '[data-cy=data-explorer-data-set-field-select-all]',
            )
            .click();
        fixture.detectChanges();
        expect(panel.querySelector('summary small').textContent).toContain(
            '4 selected · sensor-0',
        );
    });

    it('edits data and chart appearance from the two designer tabs', async () => {
        const property = (runtimeName: string) => ({
            runtimeName,
            '@class':
                'org.apache.streampipes.model.schema.EventPropertyPrimitive',
            'runtimeType': 'http://www.w3.org/2001/XMLSchema#string',
            'propertyScope': 'DIMENSION_PROPERTY',
        });
        const dataset = {
            elementId: 'compressor-id',
            measureName: 'Compressor',
            eventSchema: { eventProperties: ['Area', 'Legacy'].map(property) },
        };
        const linkedDataset = {
            ...dataset,
            elementId: 'press-id',
            measureName: 'Press temperature',
            eventSchema: { eventProperties: ['Area', 'New'].map(property) },
        };
        const loadMeasure = vi.fn(() => of(linkedDataset));
        TestBed.configureTestingModule({
            providers: [
                provideTranslateService(),
                provideRouter([]),
                provideHttpClient(),
                {
                    provide: SpAssetBrowserService,
                    useValue: {
                        assetData$: of({
                            assets: [
                                {
                                    assetId: 'line',
                                    assetName: 'Line 1',
                                    assets: [
                                        {
                                            assetId: 'press',
                                            assetName: 'Press',
                                            assetLinks: [
                                                {
                                                    linkType: 'dataset',
                                                    resourceId: 'press-id',
                                                },
                                            ],
                                        },
                                        {
                                            assetId: 'private',
                                            assetName: 'Private',
                                            assetLinks: [
                                                {
                                                    linkType: 'dataset',
                                                    resourceId: 'unavailable',
                                                },
                                            ],
                                        },
                                    ],
                                },
                            ],
                        }),
                    },
                },
                {
                    provide: DatalakeRestService,
                    useValue: {
                        getTagValues: () => of({}),
                        getMeasurementSummary: () =>
                            of({ resources: [dataset, linkedDataset] }),
                        getMeasurementByName: loadMeasure,
                    },
                },
            ],
        });
        const fixture = TestBed.createComponent(ChartDesignerPanelComponent);
        const widget = {
            widgetType: 'time-series-chart',
            visualizationConfig: {},
            dataConfig: {
                sourceConfigs: [
                    {
                        measureName: dataset.measureName,
                        measure: dataset,
                        queryType: 'raw',
                        queryConfig: {
                            fields: ['Area', 'Legacy'].map(runtimeName => ({
                                runtimeName,
                                selected: true,
                                numeric: false,
                            })),
                            selectedFilters: [
                                {
                                    field: { runtimeName: 'Legacy' },
                                    operator: '=',
                                    value: 'line',
                                },
                            ],
                            groupBy: [
                                {
                                    runtimeName: 'Area',
                                    selected: true,
                                    numeric: false,
                                },
                            ],
                        },
                    },
                ],
                ignoreMissingValues: false,
            },
            baseAppearanceConfig: {
                chartAppearance: {
                    showLegend: true,
                    showTooltip: true,
                    showToolbox: true,
                },
            },
        };
        fixture.componentRef.setInput('dataLakeMeasure', dataset);
        fixture.componentRef.setInput('currentlyConfiguredWidget', widget);
        fixture.detectChanges();
        await fixture.whenStable();
        const panel: HTMLElement = fixture.nativeElement;
        const tabs = Array.from(
            panel.querySelectorAll<HTMLElement>('[role=tab]'),
        );
        expect(tabs.map(tab => tab.textContent.trim())).toEqual([
            'Data',
            'Visualization',
        ]);
        const browse = Array.from(panel.querySelectorAll('button')).find(
            button => button.textContent.includes('Browse by asset'),
        );
        const directPicker = fixture.debugElement.query(
            By.directive(SearchSelectComponent),
        ).componentInstance;
        directPicker.value.set({ ...dataset });
        fixture.detectChanges();
        expect(loadMeasure).not.toHaveBeenCalled();
        browse.click();
        fixture.detectChanges();
        const assetPicker = fixture.debugElement.query(
            By.directive(SearchSelectComponent),
        ).componentInstance;
        expect(assetPicker.items().map(asset => asset.label)).toEqual([
            'Line 1',
            'Line 1 / Press',
        ]);
        assetPicker.value.set(assetPicker.items()[0]);
        fixture.detectChanges();
        const result = panel.querySelector<HTMLButtonElement>('.asset-result');
        expect(result.textContent).toContain('Press temperature');
        expect(result.textContent).toContain('Line 1 / Press');
        expect(widget.dataConfig.sourceConfigs[0].measureName).toBe(
            'Compressor',
        );
        result.click();
        fixture.detectChanges();
        expect(widget.dataConfig.sourceConfigs[0].measureName).toBe(
            'Press temperature',
        );
        expect(browse.getAttribute('aria-expanded')).toBe('false');
        const query = widget.dataConfig.sourceConfigs[0].queryConfig;
        expect(
            query.fields
                .filter(field => field.selected)
                .map(field => field.runtimeName),
        ).toEqual(['Area']);
        expect(
            query.groupBy
                .filter(field => field.selected)
                .map(field => field.runtimeName),
        ).toEqual(['Area']);
        expect(query.selectedFilters).toHaveLength(0);
        expect(panel.querySelector('sp-alert-banner').textContent).toContain(
            'Legacy',
        );
        const advanced = panel.querySelector<HTMLElement>(
            '[data-cy=chart-section-advanced] summary',
        );
        advanced.click();
        panel
            .querySelector<HTMLInputElement>(
                '[data-cy=data-explorer-ignore-missing-values-checkbox] input',
            )
            .click();
        expect(widget.dataConfig.ignoreMissingValues).toBe(true);
        tabs[1].click();
        fixture.detectChanges();
        await fixture.whenStable();
        fixture.detectChanges();
        await expect
            .poll(() =>
                panel.querySelector('sp-echarts-widget-appearance-config'),
            )
            .toBeTruthy();
        const legend = Array.from(panel.querySelectorAll('summary')).find(
            summary => summary.textContent.includes('Legend & tooltip'),
        );
        legend.click();
        legend.closest('details').querySelector('input').click();
        await fixture.whenStable();
        fixture.detectChanges();
        expect(widget.baseAppearanceConfig.chartAppearance.showLegend).toBe(
            false,
        );
        expect(legend.textContent).toContain('Legend off');
        const repair = Array.from(panel.querySelectorAll('button')).find(
            button => button.textContent.includes('Choose fields'),
        );
        expect(repair.parentElement.textContent).toContain(
            'Select at least one numeric field',
        );
        repair.click();
        fixture.detectChanges();
        await fixture.whenStable();
        await expect
            .poll(() =>
                panel
                    .querySelector('sp-field-selection-panel details')
                    ?.hasAttribute('open'),
            )
            .toBe(true);
    });

    it('keeps shared colors and the registered editors available when changing chart types', async () => {
        TestBed.configureTestingModule({
            providers: [provideTranslateService(), provideHttpClient()],
        });
        const registry = TestBed.inject(ChartRegistry);
        const fixture = TestBed.createComponent(
            ChartVisualisationSettingsComponent,
        );
        const properties = [
            { runtimeName: 'temperature', type: 'double' },
            { runtimeName: 'pressure', type: 'double' },
            { runtimeName: 'running', type: 'boolean' },
            {
                runtimeName: 'frame',
                type: 'string',
                semanticType: 'https://image.com',
            },
        ];
        const source = {
            measureName: 'Compressor',
            measure: {
                eventSchema: {
                    eventProperties: properties.map(property => ({
                        '@class':
                            'org.apache.streampipes.model.schema.EventPropertyPrimitive',
                        'runtimeName': property.runtimeName,
                        'runtimeType':
                            'http://www.w3.org/2001/XMLSchema#' + property.type,
                        'semanticType': property.semanticType,
                    })),
                },
            },
            queryType: 'raw',
            queryConfig: {
                fields: properties.map(({ runtimeName }) => ({
                    runtimeName,
                    selected: true,
                })),
                groupBy: [],
            },
        };
        const appearanceConfig = { textColor: '#123456' };
        for (const chart of registry.getAvailableChartTemplates()) {
            fixture.componentRef.setInput('currentlyConfiguredWidget', {
                widgetType: chart.id,
                dataConfig: { sourceConfigs: [source] },
                visualizationConfig: {},
                baseAppearanceConfig: appearanceConfig,
            });
            fixture.detectChanges();
            await fixture.whenStable();
            expect(
                fixture.debugElement.query(
                    By.directive(ChartAppearanceSettingsComponent),
                ).componentInstance.appearanceConfig,
            ).toBe(appearanceConfig);
            const panel = fixture.nativeElement as HTMLElement;
            const summaries = Array.from(
                panel.querySelectorAll('details > summary'),
            );
            expect(summaries[0]?.closest('details').open, chart.id).toBe(true);
            for (const summary of summaries) {
                expect(
                    summary.querySelector('strong').textContent.trim(),
                    chart.id,
                ).not.toBe('Settings');
                const description = summary
                    .querySelector('small')
                    ?.textContent.trim();
                expect(description, chart.id).toBeTruthy();
                expect(description, chart.id).not.toMatch(/undefined|null/);
            }
            if (chart.widgetConfigurationComponent) {
                expect(
                    fixture.debugElement.query(
                        By.directive(chart.widgetConfigurationComponent),
                    ),
                    chart.id,
                ).toBeTruthy();
            }
            if (chart.widgetAppearanceConfigurationComponent) {
                expect(
                    fixture.debugElement.query(
                        By.directive(
                            chart.widgetAppearanceConfigurationComponent,
                        ),
                    ).componentInstance.appearanceConfig,
                    chart.id,
                ).toBe(appearanceConfig);
            }
        }
        expect(appearanceConfig.textColor).toBe('#123456');
    });
});
