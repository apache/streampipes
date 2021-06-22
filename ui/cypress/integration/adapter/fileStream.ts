import {AdapterUtils} from '../../support/utils/AdapterUtils';
import {FileManagementUtils} from '../../support/utils/FileManagementUtils';
import {GenericAdapterBuilder} from '../../support/builder/GenericAdapterBuilder';

describe('Test File Stream Adapter', function () {

    it('Login', function () {
        cy.login();
    });

    FileManagementUtils.addFile('fileTest/random.csv');

    const adapterInput = GenericAdapterBuilder
        .create('File_Stream')
        .setName('File Stream Adapter Test')
        .setTimestampProperty('timestamp')
        .addProtocolInput('input', 'speed','1')
        .addProtocolInput('checkbox', 'replaceTimestamp','check')
        .setFormat('csv')
        .addFormatInput('input', 'delimiter',';')
        .addFormatInput('checkbox', 'header','check')
        .build();

    AdapterUtils.testGenericStreamAdapter(adapterInput);

    FileManagementUtils.deleteFile();
});