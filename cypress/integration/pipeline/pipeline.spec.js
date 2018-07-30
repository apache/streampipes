describe('Adapter Installation 1', function () {


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
            .trigger('mousedown')
            .trigger('pointerup')
            .trigger('mousemove', { clientX: 0, clientY: 100 })

            .trigger('mouseup', {force: true})
    });

    it('Select', function () {
        cy.get("#CouchDB").click();

    });



});

function select(selectAt) {
    it('Select ' + selectAt, function () {
        cy.contains(selectAt).click();
    });
}
