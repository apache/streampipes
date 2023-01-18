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

import { ExportConfig } from '../model/export-config.model';
import { DateRange } from '@streampipes/platform-services';
import { FileNameService } from './file-name.service';

describe('FileNameService', () => {
    const service = new FileNameService();

    // Testing data
    const defaultExportDate = new Date(2022, 11, 11, 3);
    const defaultStartDate = new Date(2022, 11, 2, 3);
    const defaultEndDate = new Date(2022, 11, 6, 3);
    const defaultDateRange = new DateRange(defaultStartDate, defaultEndDate);
    let defaultExportConfig: ExportConfig;

    const expectedBaseName = '2022-12-11_measurement_';

    beforeEach(() => {
        defaultExportConfig = {
            dataExportConfig: {
                dataRangeConfiguration: 'all',
                missingValueBehaviour: 'ignore',
                measurement: 'measurement',
            },
            formatExportConfig: {
                exportFormat: 'csv',
                delimiter: 'comma',
            },
        };
    });

    it('Name for all data csv', () => {
        const result = service.generateName(
            defaultExportConfig,
            defaultExportDate,
        );
        expect(result).toBe(`${expectedBaseName}all.csv`);
    });

    it('Name for all data json', () => {
        defaultExportConfig.formatExportConfig.exportFormat = 'json';
        const result = service.generateName(
            defaultExportConfig,
            defaultExportDate,
        );
        expect(result).toBe(`${expectedBaseName}all.json`);
    });

    it('Name for custom interval csv', () => {
        defaultExportConfig.dataExportConfig.dataRangeConfiguration =
            'customInterval';
        defaultExportConfig.dataExportConfig.dateRange = defaultDateRange;

        const result = service.generateName(
            defaultExportConfig,
            defaultExportDate,
        );
        expect(result).toBe(
            `${expectedBaseName}customInterval_2022-12-02_2022-12-06.csv`,
        );
    });

    it('Name for custom visible json', () => {
        defaultExportConfig.formatExportConfig.exportFormat = 'json';
        defaultExportConfig.dataExportConfig.dataRangeConfiguration = 'visible';
        defaultExportConfig.dataExportConfig.dateRange = defaultDateRange;

        const result = service.generateName(
            defaultExportConfig,
            defaultExportDate,
        );
        expect(result).toBe(
            `${expectedBaseName}visible_2022-12-02_2022-12-06.json`,
        );
    });

    it('Name when missing start date range', () => {
        defaultExportConfig.dataExportConfig.dataRangeConfiguration = 'visible';
        defaultExportConfig.dataExportConfig.dateRange = new DateRange();
        defaultExportConfig.dataExportConfig.dateRange.endDate = defaultEndDate;

        const result = service.generateName(
            defaultExportConfig,
            defaultExportDate,
        );
        expect(result).toBe(`${expectedBaseName}visible_2022-12-06.csv`);
    });

    it('Name when missing end date range', () => {
        defaultExportConfig.dataExportConfig.dataRangeConfiguration = 'visible';
        defaultExportConfig.dataExportConfig.dateRange = new DateRange();
        defaultExportConfig.dataExportConfig.dateRange.startDate =
            defaultStartDate;

        const result = service.generateName(
            defaultExportConfig,
            defaultExportDate,
        );
        expect(result).toBe(`${expectedBaseName}visible_2022-12-02.csv`);
    });
});
