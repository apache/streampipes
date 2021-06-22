import {PipelineInput} from '../model/PipelineInput';
import {UserInput} from '../model/UserInput';
import {StaticPropertyUtils} from './StaticPropertyUtils';

export class PipelineUtils {

    public static testPipeline(pipelineInput: PipelineInput) {

        PipelineUtils.addPipeline(pipelineInput);

        PipelineUtils.deletePipeline();

    }

    public static addPipeline(pipelineInput: PipelineInput) {

        PipelineUtils.goToPipelineEditor();

        PipelineUtils.selectDataStream(pipelineInput);

        PipelineUtils.configurePipeline(pipelineInput);

        PipelineUtils.startPipeline(pipelineInput);

    }


    private static goToPipelineEditor() {
        it('Go to StreamPipes connect', function () {
            cy.visit('#/editor');
        });
    }

    private static selectDataStream(pipelineInput: PipelineInput) {
        it('Select a stream', function () {
            cy.dataCy('sp-editor-add-pipeline-element').click();
            cy.dataCy(pipelineInput.dataSource).click();
        });
    }


    private static configurePipeline(pipelineInput: PipelineInput) {

        it('Select processor', function () {
            cy.get('[data-cy=sp-possible-elements-' + pipelineInput.dataSource +']', { timeout: 10000 }).click();
            cy.dataCy('sp-compatible-elements-' + pipelineInput.processingElement.name).click();
        });

        StaticPropertyUtils.input(pipelineInput.processingElement.config);

        it('Save configuration', function () {
            cy.dataCy('sp-element-configuration-save').click();
        });

        it('Select sink', function () {
            cy.dataCy('sp-possible-elements-' + pipelineInput.processingElement.name).click();
            cy.dataCy('sp-compatible-elements-'+ pipelineInput.dataSink.name).click();
        });

        StaticPropertyUtils.input(pipelineInput.dataSink.config);

        it('Save sink configuration', function () {
            cy.dataCy('sp-element-configuration-save').click();
        });

    }

    private static startPipeline(pipelineInput: PipelineInput) {
        it('Save and start pipeline', function () {
            cy.dataCy('sp-editor-save-pipeline').click();
            cy.dataCy('sp-editor-pipeline-name').type(pipelineInput.pipelineName);
            cy.dataCy('sp-editor-checkbox-start-immediately').children().click();
            cy.dataCy('sp-editor-save').click();
            cy.get('[data-cy=sp-pipeline-started-dialog]', { timeout: 10000 }).should('be.visible');
            cy.get('[data-cy=sp-pipeline-dialog-close]', { timeout: 10000 }).click();
        });
    }

    public static deletePipeline() {
        it('Delete pipeline', function () {
            cy.visit('#/pipelines');
            cy.dataCy('delete').should('have.length', 1)
            cy.dataCy('delete').click()
            cy.dataCy('sp-pipeline-stop-and-delete').click()
            cy.get('[data-cy=delete]', { timeout: 10000 }).should('have.length', 0);
        });
    }
}
