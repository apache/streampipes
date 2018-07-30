// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add("login", (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add("drag", { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add("dismiss", { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This is will overwrite an existing command --
// Cypress.Commands.overwrite("visit", (originalFn, url, options) => { ... })



Cypress.Commands.add('login', (options = {}) => {
    Cypress.Cookies.defaults({
        whitelist: "JSESSIONID"
    });

    cy.visit('');

    cy.url({timeout: 60000}).should('contain', '#/login');

    //TODO change again
    cy.get('input[type="email"]').type('riemer@fzi.de');
    cy.get('input[type="password"]').type('1234');

    cy.get('button').contains('Login').parent().click();

});

Cypress.Commands.add('logout', (options = {}) => {
    Cypress.Cookies.defaults({
        whitelist: "JSESSIONID"
    });

    cy.get('#sp_logout').click();
});
