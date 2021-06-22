declare global {
    namespace Cypress {
        interface Chainable {
            /**
             * Login into streampipes with standard test user
             * @example cy.login();
             */
            login: typeof login;
        }
    }
}

export const login = () => {
    Cypress.Cookies.defaults({
        preserve: "JSESSIONID"
    });

    cy.request('POST', '/streampipes-backend/api/v2/admin/login', {
        username: 'test@streampipes.de',
        password: 'test1234'
    });
};
