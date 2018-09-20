describe('Adapter Tutorial 1', function () {

    it('Login', function () {
        cy.login();
    });

    it('Go to StreamPipes connect', function () {
        cy.get('#StreamPipes-Connect-link').click();
    });

    it('Start tutorial', function () {
        cy.get('#startAdapterTutorial').click();
    });

    it('Go to first step', function () {
        cy.get('.shepherd-button:not(.shepherd-button-secondary)').click();
    });

    it('Select OpenSenseMap adapter', function () {
        cy.get('#OpenSenseMap').click();
    });

    it('After configuring adapter click next', function () {
        cy.get('#specific-settings-next-button').click();
    });

    it('Open schema of id property', function () {
        cy.get('#id button:last-of-type').click();
    });

    it('Go to next step of tutorial', function () {
        cy.get('[data-id=step-5] > div > footer > ul > li:last-of-type > a ').click();
    });

    it('Leave schema editor tab', function () {
        cy.get('#event-schema-next-button').click();
    });

    it('Start the adapter', function () {
        cy.get('#button-startAdapter').click();
    });

    it('Start Adapter', function () {
        cy.get('#confirm_adapter_started_button').click();
        cy.wait(300);
    });

    it('Confirm Adapter sucessully started', function () {
        cy.get('[data-id=step-9] > div > footer > ul > li > a').click();
    });

    it('Logout', function () {
        cy.logout();
    });

});
