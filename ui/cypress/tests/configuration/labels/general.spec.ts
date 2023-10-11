describe('Change basic settings', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
    });

    it('Perform Test', () => {
        cy.visit('#/configuration/general');

        // Rename app, change localhost and port
        cy.dataCy('general-config-app-name').clear();
        cy.dataCy('general-config-app-name').type('TEST APP');
        cy.dataCy('general-config-hostname').clear();
        cy.dataCy('general-config-hostname').type('testhost');
        cy.dataCy('general-config-port').clear();
        cy.dataCy('general-config-port').type('123');
        cy.dataCy('sp-element-general-config-save').click();

        // Leave, Re-visit configuration and check values
        cy.visit('#/dashboard');
        cy.visit('#/configuration/general');
        cy.dataCy('general-config-app-name').should('have.value', 'TEST APP');
        cy.dataCy('general-config-hostname').should('have.value', 'testhost');
        cy.dataCy('general-config-port').should('have.value', '123');
    });
});
