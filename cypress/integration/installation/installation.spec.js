describe('Install Streampipes', function () {
    it('Open Streampipes', function () {
        cy.visit('http://localhost');
    });
    it('Should open Setup Page', function () {
        cy.url({timeout: 60000}).should('contain', '#/setup');
    });
    it('Install Button should be disabled', function () {
        cy.get('button').contains('Install').parent().should('be.disabled');
    });
    it('Fill in Username and Password', function () {
        cy.get('input[name="email"]').type('abt@fzi.de');
        cy.get('input[name="password"]').type('1234');
    });
    it('Install Button should not be disabled', function () {
        cy.get('button').contains('Install').parent().should('not.be.disabled');
    });
    it('Start Installation', function () {
        cy.get('button').contains('Install').parent().click();
    });
    it('Wait for Installation to finish', function () {
        cy.contains('a', 'Go to login page', {timeout: 1200000}).should('be.visible');
    });
    it('Scroll to Bottom', function () {
        cy.scrollTo('bottom');
    });
    it('Click Login Link', function () {
        cy.get('a').contains('Go to login page').click();
    });
});