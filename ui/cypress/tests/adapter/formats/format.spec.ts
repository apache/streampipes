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

import { FileManagementUtils } from '../../../support/utils/FileManagementUtils';
import { ConnectUtils } from '../../../support/utils/connect/ConnectUtils';
import { ConnectBtns } from '../../../support/utils/connect/ConnectBtns';
import { StaticPropertyUtils } from '../../../support/utils/StaticPropertyUtils';
import { UserInputBuilder } from '../../../support/builder/UserInputBuilder';

describe('Test adapter formats', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
    });

    const baseDir = 'connect/format/';

    const expected = {
        timestamp: 1667904471000,
        v1: 4.1,
        v2: 'abc',
        v3: true,
        v4: 1,
    };

    it('Test json object format', () => {
        // Set up test
        FileManagementUtils.addFile(baseDir + 'jsonObject.json');

        navigateToFormatSelection();

        // Set format configuration
        ConnectBtns.json().click();
        ConnectBtns.jsonObject().click();

        // Validate result
        validateResult(expected);
    });

    it('Test array with json objects', () => {
        // Set up test
        FileManagementUtils.addFile('connect/format/jsonArray.json');
        navigateToFormatSelection();

        // Set format configuration
        ConnectBtns.json().click();
        ConnectBtns.jsonArray().click();

        // Validate result
        validateResult(expected);
    });

    it('Test json with a field of type array', () => {
        // Set up test
        FileManagementUtils.addFile(baseDir + 'jsonArrayField.json');
        navigateToFormatSelection();

        // Set format configuration
        ConnectBtns.json().click();
        ConnectBtns.jsonArrayField().click();
        const arrayFieldInput = UserInputBuilder.create()
            .add('input', 'key', 'field')
            .build();
        StaticPropertyUtils.input(arrayFieldInput);

        // Validate result
        validateResult(expected);
    });

    it('Test geo json format', () => {
        // Set up test
        const geoJsonResultEvent = {
            latitude: 10.1,
            longitude: 125.6,
            timestamp: 1667904471000,
            v1: 4.1,
        };
        FileManagementUtils.addFile(baseDir + 'geoJson.json');
        navigateToFormatSelection();

        // Set format configuration
        ConnectBtns.json().click();
        ConnectBtns.geoJson().click();

        // Validate result
        validateResult(geoJsonResultEvent);
    });

    it('Test xml format', () => {
        // Set up test
        FileManagementUtils.addFile(baseDir + 'xmlObject.xml');
        navigateToFormatSelection();

        // Set format configuration
        ConnectBtns.xml().click();
        const tagInputField = UserInputBuilder.create()
            .add('input', 'tag', 'event')
            .build();
        StaticPropertyUtils.input(tagInputField);

        // Validate result
        validateResult(expected);
    });

    it('Test csv format with header', () => {
        // Set up test
        FileManagementUtils.addFile(baseDir + 'csvWithHeader.csv');
        navigateToFormatSelection();

        // Set format configuration
        ConnectBtns.csv().click();
        const delimiterInputField = UserInputBuilder.create()
            .add('input', 'delimiter', ';')
            .build();
        StaticPropertyUtils.input(delimiterInputField);
        const headerInputField = UserInputBuilder.create()
            .add('checkbox', 'header', 'check')
            .build();
        StaticPropertyUtils.input(headerInputField);

        // Validate result
        validateResult(expected);
    });

    it('Test csv format without header', () => {
        // Set up test
        FileManagementUtils.addFile(baseDir + 'csvWithoutHeader.csv');
        navigateToFormatSelection();

        // Set format configuration
        ConnectBtns.csv().click();
        const delimiterInputField = UserInputBuilder.create()
            .add('input', 'delimiter', ';')
            .build();
        StaticPropertyUtils.input(delimiterInputField);

        const expectedNoHeader = {
            key_0: 1667904471000,
            key_1: 4.1,
            key_2: 'abc',
            key_3: true,
            key_4: 1,
        };

        // Validate result
        validateResult(expectedNoHeader);
    });

    it('Test csv format with comma', () => {
        // Set up test
        FileManagementUtils.addFile(baseDir + 'csvWithComma.csv');
        navigateToFormatSelection();

        // Set format configuration
        ConnectBtns.csv().click();
        const delimiterInputField = UserInputBuilder.create()
            .add('input', 'delimiter', ',')
            .build();
        StaticPropertyUtils.input(delimiterInputField);
        const headerInputField = UserInputBuilder.create()
            .add('checkbox', 'header', 'check')
            .build();
        StaticPropertyUtils.input(headerInputField);

        // Validate result
        validateResult(expected);
    });
});

const navigateToFormatSelection = () => {
    ConnectUtils.goToConnect();

    ConnectUtils.goToNewAdapterPage();

    ConnectUtils.selectAdapter('File_Stream');

    ConnectUtils.configureAdapter([]);
};

const validateResult = expected => {
    ConnectBtns.formatSelectionNextBtn().click();
    cy.dataCy('schema-preview-original-event', { timeout: 10000 }).then(
        value => {
            const jsonResult = removeWhitespaceExceptInQuotes(value.text());
            expect(jsonResult).to.deep.equal(expected);
        },
    );
};

const removeWhitespaceExceptInQuotes = (input: string): string => {
    let inQuote = false;
    let result = '';

    for (const char of input) {
        if (char === '"') {
            inQuote = !inQuote;
        }

        if (/\s/.test(char) && !inQuote) {
            continue;
        }

        result += char;
    }

    return JSON.parse(result);
};
