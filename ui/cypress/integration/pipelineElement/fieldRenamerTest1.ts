import {ProcessingElementTestUtils} from '../../support/utils/ProcessingElementTestUtils';
import {PipelineElementBuilder} from '../../support/builder/PipelineElementBuilder';

describe('Test Field Renamer 1', function () {

    it('Login', function () {
        cy.login();
    });

    // Config
    const testName = 'fieldRenamer1'
    const inputFile = 'pipelineElement/fieldRenamer1/input.csv';
    const expectedResultFile = 'pipelineElement/fieldRenamer1/expected.csv';

    const processor = PipelineElementBuilder.create('field_renamer')
        .addInput('drop-down', 'convert-property','count')
        .addInput('input', 'field-name','newname')
        .build();

    ProcessingElementTestUtils.testElement(testName, inputFile, expectedResultFile, processor);
});