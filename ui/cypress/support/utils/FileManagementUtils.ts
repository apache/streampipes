
export class FileManagementUtils {

    public static addFile(filePath: string) {
        it('Go to StreamPipes file management', function () {
            cy.visit('#/files');
        });

        it('Open file upload dialog', function () {
            cy.dataCy('sp-open-file-upload-dialog').click();
        });

        it('Upload file', function () {
            // const filepath = 'fileTest/test.csv'
            cy.dataCy('sp-file-management-file-input').attachFile(filePath);
            cy.dataCy('sp-file-management-store-file').click()
        });
    }

    public static deleteFile() {
        it('Go to StreamPipes file management', function () {
            cy.visit('#/files');
        });
        it('Check if file was uploaded and delete it', function () {
            cy.dataCy('delete').should('have.length', 1)
            cy.dataCy('delete').click()
            cy.dataCy('confirm-delete').click()
            cy.dataCy('delete').should('have.length', 0)
        });
    }


}