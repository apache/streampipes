
describe('Adapter Installation HTTP', function () {
    Cypress.Cookies.defaults({
        whitelist: "JSESSIONID"
    });

    describe('Json', function () {

        var adapter = {
            name: 'HttpStream-JsonObject',
            protocol: {
                name: ' HTTP (Stream) ',
                id: 'HTTP_Stream',
                config: [
                    {key: 'url', value: 'http://localhost:3000/json'},
                    {key: 'interval', value: '5'}
                ]
            },
            format: {
                name: ' Json Object ',
                config: []
            },
            numberProperties: 5
        };

        adapterInstallation(adapter);
    });

    // describe('XML', function () {
    //
    //     var adapter = {
    //         name: 'HttpStream-XML',
    //         protocol: {
    //             name: ' HTTP (Stream) ',
    //             id: 'HTTP_Stream',
    //             config: [
    //                 {key: 'url', value: 'http://localhost:3000/xml'},
    //                 {key: 'interval', value: '5'}
    //             ]
    //         },
    //         format: {
    //             name: ' XML ',
    //             config: [
    //                 {key: 'tag', value: 'sensors'}
    //             ]
    //         },
    //         numberProperties: 5
    //     };
    //
    //     adapterInstallation(adapter);
    // });
    //
    describe('Json Array Key', function () {

        var adapter = {
            name: 'HttpStream-JsonArrayKey',
            protocol: {
                name: ' HTTP (Stream) ',
                id: 'HTTP_Stream',
                config: [
                    {key: 'url', value: 'http://localhost:3000/jsonarray-withkey'},
                    {key: 'interval', value: '5'}
                ]
            },
            format: {
                name: ' Json Array Key ',
                config: [
                    {key: 'key', value: 'user'}
                ]
            },
            numberProperties: 5
        };

        adapterInstallation(adapter);
    });

    describe('Json Array', function () {

        var adapter = {
            name: 'HttpStream-JsonArray',
            protocol: {
                name: ' HTTP (Stream) ',
                id: 'HTTP_Stream',
                config: [
                    {key: 'url', value: 'http://localhost:3000/jsonarray'},
                    {key: 'interval', value: '5'}
                ]
            },
            format: {
                name: ' Json Array No Key ',
                config: [
                ]
            },
            numberProperties: 5
        };

        adapterInstallation(adapter);
    });
    //
    // //
    // // // Not working
    // // describe('GeoJson', function () {
    // //
    // //     var adapter = {
    // //         name: 'HttpStream-GeoJson',
    // //         protocol: {
    // //             name: ' HTTP (Stream) ',
    // //             id: 'HTTP_Stream',
    // //             config: [
    // //                 {key: 'url', value: 'http://localhost:3000/geo-json'},
    // //                 {key: 'interval', value: '5'}
    // //             ]
    // //         },
    // //         format: {
    // //             name: ' GeoJSON ',
    // //             config: [
    // //             ]
    // //         },
    // //         numberProperties: 5
    // //     };
    // //
    // //     adapterInstallation(adapter);
    // // });
    //
    // describe('CSV with header', function () {
    //
    //     var adapter = {
    //         name: 'HttpStream-CSVWithHeader',
    //         protocol: {
    //             name: ' HTTP (Stream) ',
    //             id: 'HTTP_Stream',
    //             config: [
    //                 {key: 'url', value: 'http://localhost:3000/csv'},
    //                 {key: 'interval', value: '5'}
    //             ]
    //         },
    //         format: {
    //             name: ' Csv ',
    //             config: [
    //                 {key: 'delimiter', value: ','}
    //                 // {key: 'includes header', value: '1'}
    //             ]
    //         },
    //         numberProperties: 5
    //     };
    //
    //     adapterInstallation(adapter);
    // });
    //
    // describe('CSV no header', function () {
    //
    //     var adapter = {
    //         name: 'HttpStream-CSVNoHeader',
    //         protocol: {
    //             name: ' HTTP (Stream) ',
    //             id: 'HTTP_Stream',
    //             config: [
    //                 {key: 'url', value: 'http://localhost:3000/csv'},
    //                 {key: 'interval', value: '5'}
    //             ]
    //         },
    //         format: {
    //             name: ' Csv ',
    //             config: [
    //                 {key: 'delimiter', value: ','}
    //                 // {key: 'includes header', value: '0'}
    //             ]
    //
    //         },
    //         numberProperties: 5
    //     };
    //
    //     adapterInstallation(adapter);
    // });


});

function adapterInstallation(adapter) {
    describe('Adapter Installation', function () {
        it('Login', function () {
            cy.login();
        });

        it('Go to StreamPipes Connect', function () {
            cy.visit('#/streampipes/connect');
        });

        it('Select the Adpater', function () {
            cy.get('#' + adapter.protocol.id).click();
        });

        it('Configure Protocol', function () {

            for(var con of adapter.protocol.config) {
                cy.get('#input-' + con.key).type(con.value);
            }

            cy.get('#specific-settings-next-button').click();
        });

        it('Configure Format', function () {
            cy.contains(adapter.format.name).click();

            for(var config of adapter.format.config) {
                cy.get('#input-' + config.key).type(config.value);
            }

            cy.get('#format-selection-next-button').click();

        });

        it('Check if schema is guessed', function () {
            cy.get('#event-property-list-id').children().should('have.length', adapter.numberProperties);

            cy.get('#event-schema-next-button').click();
        });

        it('Configure Adapter Settings', function () {
            cy.get('#input-AdapterName').type(adapter.name);
            cy.get('#button-startAdapter').click();
        });

        it('Check if Data is coming', function () {
            cy.get('#preview-data-rows-id', {timeout: 10000}).children().should('have.length', adapter.numberProperties);
        });

        it('Finish adpater creation', function () {
            cy.get('#confirm_adapter_started_button').click();
            cy.wait(500);
        });

        // it('Go to StreamPipes Connect', function () {
        //     cy.visit('#/streampipes/connect');
        // });

        it('Go to All Adapters Tab', function () {
            cy.get('div').contains('All Adapters').parent().click({ force: true });
        });

        it('Delete created adapter', function () {
            cy.get('#delete-' + adapter.name).click();
        });

        it('Check that adapter is deleted', function () {
            cy.get('h3', {timeout: 10000});
            cy.get('h3', {timeout: 10000}).contains(adapter.name).should('have.length', 0);
        });

        it('Logout', function () {
            cy.logout();
        });
    });
};

