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

import { ChartUtils } from '../../../support/utils/chart/ChartUtils';
import { PrepareTestDataUtils } from '../../../support/utils/PrepareTestDataUtils';
import { ChartBtns } from '../../../support/utils/chart/ChartBtns';

describe('Test Progress Bar View in Charts', () => {
    beforeEach('Setup Test', () => {
        ChartUtils.initDataLakeTests();
    });

    it('Perform Test', () => {
        ChartUtils.addDataViewAndWidget(
            PrepareTestDataUtils.dataName,
            'progress-bar',
        );

        ChartUtils.openVisualizationConfig();
        ChartBtns.progressBarTitleInput().type('Job Progress');
        ChartBtns.progressBarDescriptionInput().type(
            'Progress against the configured target value.',
        );
        ChartBtns.progressBarTargetValueInput().clear().type('50');
        ChartBtns.progressBarInvertCheckbox()
            .check({ force: true })
            .should('be.checked');

        ChartBtns.progressBarWidget().should('be.visible');
        ChartBtns.progressBarTitle().should('contain.text', 'Job Progress');
        ChartBtns.progressBarDescription().should(
            'contain.text',
            'Progress against the configured target value.',
        );
        ChartBtns.progressBarStatus().should('contain.text', 'Remaining');
        ChartBtns.progressBarPercent().should('contain.text', '%');
        ChartBtns.progressBarPrimaryLabel().should('contain.text', '%');
        ChartBtns.progressBarSecondaryLabel().should('contain.text', '/');
        ChartBtns.progressBarFill()
            .invoke('attr', 'style')
            .should('match', /width:\s*[0-9.]+%/);
    });
});
