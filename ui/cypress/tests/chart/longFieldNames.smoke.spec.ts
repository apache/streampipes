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

import { ChartWidget } from '../../support/model/ChartWidget';
import { ChartBtns } from '../../support/utils/chart/ChartBtns';
import { ChartUtils } from '../../support/utils/chart/ChartUtils';
import { DatasetBtns } from '../../support/utils/dataset/DatasetBtns';
import { DatasetSeedUtils } from '../../support/utils/dataset/DatasetSeedUtils';
import { DatasetUtils } from '../../support/utils/dataset/DatasetUtils';
import { FieldNameUtils } from '../../support/utils/shared/FieldNameUtils';

describe('Long field names remain readable', () => {
    const dataset = 'Long field name display';
    const prefix = 'production_line_' + 'temperature_sensor_'.repeat(8);
    const fields = [
        prefix + 'primary',
        prefix + 'secondary',
        'unbroken' + 'a'.repeat(180),
    ];

    beforeEach(() => {
        cy.initStreamPipesTest();
        DatasetSeedUtils.importCsvData({
            headers: ['timestamp', ...fields],
            rows: [[Date.now().toString(), '21.5', '22.3', '23.1']],
            measurementName: dataset,
        });
    });

    it('wraps schema names and latest-event headers on desktop and narrow screens', () => {
        DatasetUtils.openDatasetDetails(dataset);
        [1440, 768, 390].forEach(width => {
            cy.viewport(width, 900);
            fields.forEach(name => {
                FieldNameUtils.expectWrapped(
                    DatasetBtns.datasetDetailsSchemaField(name),
                    name,
                );
            });
        });
        DatasetUtils.openLatestEventsTab();
        fields.forEach(name => {
            FieldNameUtils.expectWrapped(
                DatasetBtns.datasetDetailsEventHeader(name),
                name,
            );
        });
    });

    it('wraps chart field ordering, table headers and data-preview headers', () => {
        ChartUtils.createChart(dataset, ChartWidget.TABLE);
        [1440, 768].forEach(width => {
            cy.viewport(width, 900);
            fields.forEach(name => {
                FieldNameUtils.expectWrapped(
                    ChartBtns.fieldOrderLabel(name),
                    `${name} (${dataset})`,
                );
                FieldNameUtils.expectWrapped(
                    ChartBtns.tableHeader(name).parent(),
                    name,
                );
            });
        });
        ChartBtns.chartDataPreviewToggle().click();
        fields.forEach(name => {
            FieldNameUtils.expectWrapped(
                ChartBtns.chartDataPreviewColumn(name),
                name,
            );
        });
    });
});
