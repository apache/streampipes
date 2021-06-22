import {FileManagementUtils} from '../../support/utils/FileManagementUtils';

describe('Test File Management', function () {
    it('Login', function () {
        cy.login();
    });

    FileManagementUtils.addFile('fileTest/test.csv')

    FileManagementUtils.deleteFile()

    it('Logout', function () {
        cy.logout();
    });

});