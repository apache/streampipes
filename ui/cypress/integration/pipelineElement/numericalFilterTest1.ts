import {UserInput} from '../../support/model/UserInput';
import {PipelineElementInput} from '../../support/model/PipelineElementInput';
import {ProcessingElementTestUtils} from '../../support/utils/ProcessingElementTestUtils';
import {PipelineElementBuilder} from '../../support/builder/PipelineElementBuilder';

describe('Test Numerical Filter 1', function () {

    it('Login', function () {
        cy.login();
    });

    // Config
    const testName = 'numericalFilter1'
    const inputFile = 'pipelineElement/numericalFilter1/input.csv';
    const expectedResultFile = 'pipelineElement/numericalFilter1/expected.csv';

    const processor = PipelineElementBuilder.create('numerical_filter')
        .addInput('drop-down', 'number-mapping','randomnumber')
        .addInput('radio', 'operation','\\>')
        .addInput('input', 'value','50')
        .build();

    ProcessingElementTestUtils.testElement(testName, inputFile, expectedResultFile, processor);
});