import {UserInput} from '../model/UserInput';
import {StaticPropertyUtils} from './StaticPropertyUtils';
import {SpecificAdapterInput} from '../model/SpecificAdapterInput';
import {GenericAdapterInput} from '../model/GenericAdapterInput';
import {SpecificAdapterBuilder} from '../builder/SpecificAdapterBuilder';

export class AdapterUtils {

    public static testSpecificStreamAdapter(adapterConfiguration: SpecificAdapterInput) {

        AdapterUtils.goToConnect();

        AdapterUtils.selectAdapter(adapterConfiguration.adapterType);

        AdapterUtils.configureAdapter(adapterConfiguration.adapterConfiguration);

        AdapterUtils.finishEventSchemaConfiguration();

        AdapterUtils.startStreamAdapter(adapterConfiguration.adapterName);

        AdapterUtils.deleteAdapter();

    }

    public static testGenericStreamAdapter(adapterConfiguration: GenericAdapterInput) {

        AdapterUtils.addGenericStreamAdapter(adapterConfiguration);

        AdapterUtils.deleteAdapter();

    }


    public static addGenericStreamAdapter(adapterConfiguration: GenericAdapterInput) {
        AdapterUtils.addGenericAdapter(adapterConfiguration);

        AdapterUtils.startStreamAdapter(adapterConfiguration.adapterName);
    }

    public static addGenericSetAdapter(adapterConfiguration: GenericAdapterInput) {
        AdapterUtils.addGenericAdapter(adapterConfiguration);

        AdapterUtils.startSetAdapter(adapterConfiguration.adapterName);
    }

    private static addGenericAdapter(adapterConfiguration: GenericAdapterInput) {
        AdapterUtils.goToConnect();

        AdapterUtils.selectAdapter(adapterConfiguration.adapterType);

        AdapterUtils.configureAdapter(adapterConfiguration.protocolConfiguration);

        AdapterUtils.configureFormat(adapterConfiguration);

        AdapterUtils.markPropertyAsTimestamp(adapterConfiguration.timestampProperty);

        AdapterUtils.finishEventSchemaConfiguration();
    }

    public static addMachineDataSimulator(name: string) {

        const configuration = SpecificAdapterBuilder
            .create('Machine_Data_Simulator')
            .setName(name)
            .addInput('input', 'wait-time-ms','1000')
            .build();

        AdapterUtils.goToConnect();

        AdapterUtils.selectAdapter(configuration.adapterType);

        AdapterUtils.configureAdapter(configuration.adapterConfiguration);

        AdapterUtils.finishEventSchemaConfiguration();

        AdapterUtils.startStreamAdapter(configuration.adapterName);

    }

    private static goToConnect() {
        it('Login', function () {
            cy.login();
        });

        it('Go to StreamPipes connect', function () {
            cy.visit('#/connect');
        });
    }

    private static selectAdapter(name) {
        it('Select adapter', function () {
            cy.get('#' + name).click();
        });
    }

    private static configureAdapter(configs: UserInput[]) {
        it('Next Button should be disabled', function () {
            cy.get('button').contains('Next').parent().should('be.disabled');
        });

        StaticPropertyUtils.input(configs);

        it('Next Button should not be disabled', function () {
            cy.get('button').contains('Next').parent().should('not.be.disabled');
        });

        it('Click next', function () {
            cy.get('button').contains('Next').parent().click();
        });
    }

    private static configureFormat(adapterConfiguration: GenericAdapterInput) {
        it('Select format', function () {
            cy.dataCy(adapterConfiguration.format).click();
        });

        StaticPropertyUtils.input(adapterConfiguration.formatConfiguration);

        it('Click next', function () {
            // cy.dataCy('sp-format-selection-next-button').parent().should('not.be.disabled');
            cy.dataCy('sp-format-selection-next-button').contains('Next').parent().click();
        });
    }

    private static markPropertyAsTimestamp(propertyName: string) {
        it('Mark property as timestamp', function () {

            // Edit timestamp
            cy.dataCy('edit-' + propertyName).click();

            // Mark as timestamp
            cy.dataCy('sp-mark-as-timestamp').children().click();

            // Close
            cy.dataCy('sp-save-edit-property').click();
        });
    }

    private static finishEventSchemaConfiguration() {
        it('Click next', function () {
            cy.get('[data-cy=sp-connect-schema-editor]', { timeout: 10000 }).should('be.visible');
            cy.get('#event-schema-next-button').click();
        });
    }

    private static startStreamAdapter(name) {
        AdapterUtils.startAdapter(name, 'sp-connect-adapter-live-preview');
    }

    private static startSetAdapter(name) {
        AdapterUtils.startAdapter(name, 'sp-connect-adapter-set-success');
    }

    private static startAdapter(name ,successElement) {
        it('Set adapter name', function () {
            cy.get('[data-cy=sp-adapter-name]').type(name);
        });

        it('Start adapter', function () {
            cy.get('#button-startAdapter').click();
            cy.get('[data-cy=' + successElement + ']', { timeout: 10000 }).should('be.visible');
        });

        it('Close adapter preview', function () {
            cy.get('button').contains('Close').parent().click();
        });
    }

    public static deleteAdapter() {
        it('Delete adapter', function () {
            cy.visit('#/connect');

            cy.get('div').contains('My Adapters').parent().click();
            cy.dataCy('delete').should('have.length', 1)
            cy.dataCy('delete').click()
            cy.get('[data-cy=delete]', { timeout: 10000 }).should('have.length', 0);
        });
    }
}
