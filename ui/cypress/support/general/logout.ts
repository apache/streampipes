declare global {
    namespace Cypress {
        interface Chainable {
            /**
             * Logout of StreamPipes
             * @example cy.logout();
             */
            logout: typeof logout;
        }
    }
}

export const logout = () => {
    Cypress.Cookies.defaults({
        preserve: "JSESSIONID"
    });

    cy.dataCy('sp-user-preferences').click();
    cy.dataCy('sp-logout').click();
};