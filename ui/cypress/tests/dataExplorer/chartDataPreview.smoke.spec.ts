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
 */

import { DataExplorerWidget } from '../../support/model/DataExplorerWidget';
import { PrepareTestDataUtils } from '../../support/utils/PrepareTestDataUtils';
import { DataExplorerBtns } from '../../support/utils/dataExplorer/DataExplorerBtns';
import { DataExplorerUtils } from '../../support/utils/dataExplorer/DataExplorerUtils';

describe('Test Chart Data Preview in Data Explorer', () => {
    beforeEach('Setup Test', () => {
        DataExplorerUtils.initDataLakeTests();
    });

    it('Shows and toggles the chart data preview', () => {
        DataExplorerUtils.addDataViewAndWidget(
            'preview-view',
            PrepareTestDataUtils.dataName,
            DataExplorerWidget.TIME_SERIES,
        );

        DataExplorerBtns.chartDataPreviewHeader().should('be.visible');
        DataExplorerBtns.chartDataPreviewTable().should('not.exist');

        DataExplorerBtns.chartDataPreviewToggle().click();

        DataExplorerBtns.chartDataPreviewTable().should('be.visible');
        DataExplorerBtns.chartDataPreviewCell('time')
            .should('exist')
            .and('have.length.at.least', 1);

        DataExplorerBtns.chartDataPreviewToggle().click();
        DataExplorerBtns.chartDataPreviewTable().should('not.exist');
    });
});
