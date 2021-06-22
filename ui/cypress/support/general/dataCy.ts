declare global {
    namespace Cypress {
        interface Chainable {
            /**
             * Select cypress id's ([data-cy=...])
             */
            dataCy(value: string): Chainable<Element>
        }
    }
}

export const dataCy = (value) => {
    return cy.get(`[data-cy=${value}]`)
};
