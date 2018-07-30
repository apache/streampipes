describe('Adapter Installation 1', function () {

    Cypress.Cookies.defaults({
        whitelist: "JSESSIONID"
    });

    it('Login', function () {
        cy.login();
    });

    it('Go to StreamPipes Editor', function () {
        cy.visit('#/streampipes/editor/');
    });


    select('Data Sinks');

    // TODO: Is not moving the pipeline-element, 'mousedown' is not trigger the element
    it('Move', function () {
        cy.get("#CouchDB")
            .trigger('mousedown', {which: 1})
            .trigger('mousemove', {pageX: 118, pageY: 400})
            .trigger('mouseup', {force: true})
    });

    it('Select', function () {
        cy.get("#CouchDB").click();

    });

    it('Logout', function () {
        cy.logout();

    });

});

function select(selectAt) {
    it('Select ' + selectAt, function () {
        cy.contains(selectAt).click();
    });
}
