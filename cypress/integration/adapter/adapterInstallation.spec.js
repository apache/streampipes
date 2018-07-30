describe('Adapter Installation 1', function () {


    var connectionType= 'Set';

    var protocol= ' HTTP (Set) ';
    var protocolProperties = [
        {"fieldName": 'url', "value": 'https://www.stadt-koeln.de/externe-dienste/open-data/parking.php'}
    ];

    var format= ' Json Array Key';
    var formatProperties = [
        {"fieldName": 'key', "value": 'fields'}
    ];

    var adaterName = 'Parking';
    var gueesSchemaWait = 10000;

    it('Login', function () {
        cy.login();
    });

    it('Go to StreamPipes connect', function () {
        //      cy.get('button').contains('StreamPipes Connect').parent().click();
        cy.visit('#/streampipes/connect');
    });

    //Connection-Type Step
    it('Next Button should be disabled', function () {
        cy.get('button').contains('Next').parent().should('be.disabled');
    });
    it('Select Connection type', function () {
        if(connectionType === 'Set') {
            cy.get('#setBox').click();
            cy.get('#setBox').should('have.class', 'box selectedBox');
        } else {
            cy.get('#StreamBox').click();
            cy.get('#StreamBox').should('have.class', 'box selectedBox');
        }

    });
    pressNext();

    //Protocol Step
    select(protocol);
    for(var i in protocolProperties) {
        it('Put Propertie', function () {
            cy.get('#input-' + protocolProperties[i].fieldName).type(protocolProperties[i].value);
        });
    }
    pressNext();

    //Format Step
    select(format);
    for(var i in formatProperties) {
        it('Put Propertie', function () {
            cy.get('#input-' + formatProperties[i].fieldName).type(formatProperties[i].value);
        });
    }
    pressNext();

    //Define Event Schema Step
    it('Press Guess Event Schema', function () {
        cy.get('button').contains('Guess Schema').parent().click();
        cy.wait(gueesSchemaWait)
    });

    //Start Adapter Step
    pressNext();
    it('Write Adapter Name', function () {
        cy.get('#input-AdapterName').type(adaterName);
    });
    it('Start Adapter', function () {
        cy.get('#button-startAdapter').parent().click();
    });


});

function pressNext() {
    it('Press Next', function () {
        cy.get('button').contains('Next').parent().click({ force: true });
    });
}

function select(selectAt) {
    it('Select ' + selectAt, function () {
        cy.contains(selectAt).click();
    });
}
