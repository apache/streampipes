describe('Add and Delete Label', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
    });

    it('Perform Test', () => {
        cy.visit('#/configuration/labels');

        // Add new label
        cy.dataCy('new-label-button').click();
        cy.dataCy('label-name').type('test');
        cy.dataCy('label-description').type('test test');
        cy.dataCy('save-label-button').click();

        // Check label
        cy.dataCy('available-labels-list').should('have.length', 1);
        cy.dataCy('label-text').should('have.text', ' test\n');

        // Delete label
        cy.dataCy('delete-label-button').click();
        cy.dataCy('available-labels-list').should('have.length', 0);
    });
});
