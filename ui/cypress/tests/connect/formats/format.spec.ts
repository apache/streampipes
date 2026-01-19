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
import { AdapterBuilder } from '../../../support/builder/AdapterBuilder';
import { AdapterInput } from '../../../support/model/AdapterInput';

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
        const template = makeAdapterInputTemplate();

        template
            .setFormat('json')
            .addFormatInput('radio', 'json_options-object', '');

        createAdapterUntilEventSchemaConfiguration(template.build());

        // Validate result
        validateResult(expected);
    });

    it('Test array with json objects', () => {
        // Set up test
        FileManagementUtils.addFile('connect/format/jsonArray.json');
        const template = makeAdapterInputTemplate();

        template
            .setFormat('json')
            .addFormatInput('radio', 'json_options-array', '');

        createAdapterUntilEventSchemaConfiguration(template.build());

        // Validate result
        validateResult(expected);
    });

    it('Test json with a field of type array', () => {
        // Set up test
        FileManagementUtils.addFile(baseDir + 'jsonArrayField.json');
        const template = makeAdapterInputTemplate();

        template
            .setFormat('json')
            .addFormatInput('radio', 'json_options-object', '')
            .setScript(
                '  for (const item of event.field) {\n' +
                    '    out.collect(item);\n' +
                    '  }\n',
            );

        createAdapterUntilEventSchemaConfiguration(template.build());

        // Validate result
        validateResult(expected);
    });

    it('Test xml format', () => {
        // Set up test
        FileManagementUtils.addFile(baseDir + 'xmlObject.xml');
        const template = makeAdapterInputTemplate();
        template
            .setFormat('xml')
            .addFormatInput('input', ConnectBtns.xmlTag(), 'event');

        createAdapterUntilEventSchemaConfiguration(template.build());

        // Validate result
        validateResult(expected);
    });

    it('Test csv format with header', () => {
        // Set up test
        FileManagementUtils.addFile(baseDir + 'csvWithHeader.csv');
        const template = makeAdapterInputTemplate();
        template
            .setFormat('csv')
            .addFormatInput('input', ConnectBtns.csvDelimiter(), ';')
            .addFormatInput('checkbox', ConnectBtns.csvHeader(), 'check');

        createAdapterUntilEventSchemaConfiguration(template.build());

        // Validate result
        validateResult(expected);
    });

    it('Test csv format without header', () => {
        // Set up test
        FileManagementUtils.addFile(baseDir + 'csvWithoutHeader.csv');
        const template = makeAdapterInputTemplate();
        template
            .setFormat('csv')
            .addFormatInput('input', ConnectBtns.csvDelimiter(), ';');

        createAdapterUntilEventSchemaConfiguration(template.build());

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
        const template = makeAdapterInputTemplate();
        template
            .setFormat('csv')
            .addFormatInput('input', ConnectBtns.csvDelimiter(), ',')
            .addFormatInput('checkbox', ConnectBtns.csvHeader(), 'check');

        createAdapterUntilEventSchemaConfiguration(template.build());

        // Validate result
        validateResult(expected);
    });
});

const makeAdapterInputTemplate = (): AdapterBuilder => {
    return AdapterBuilder.create('File_Stream')
        .setName('File Stream Adapter Test')
        .setTimestampProperty('timestamp')
        .addProtocolInput('checkbox', 'replaceTimestamp', 'check');
};

const validateResult = expected => {
    ConnectBtns.configureFieldsEventPreviewResult().then(value => {
        const jsonResult = removeWhitespaceExceptInQuotes(value.text());
        expect(jsonResult).to.deep.equal(expected);
    });
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

const createAdapterUntilEventSchemaConfiguration = (
    adapterInput: AdapterInput,
) => {
    ConnectUtils.goToConnect();

    ConnectUtils.goToNewAdapterPage();

    ConnectUtils.selectAdapter(adapterInput.adapterType);

    ConnectUtils.configureAdapter(adapterInput);

    if (adapterInput.script) {
        ConnectBtns.scriptActiveToggle().click();
        ConnectUtils.replaceAdapterScript(adapterInput.script);
        ConnectBtns.configureSchemaRunScriptBtn().click();
        cy.wait(1000);
    }

    ConnectBtns.configureSchemaNextBtn().click();
};
